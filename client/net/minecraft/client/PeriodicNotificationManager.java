package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class PeriodicNotificationManager extends SimplePreparableReloadListener<Map<String, List<Notification>>> implements AutoCloseable {
   private static final Codec<Map<String, List<Notification>>> CODEC;
   private static final Logger LOGGER;
   private final ResourceLocation notifications;
   private final Object2BooleanFunction<String> selector;
   @Nullable
   private Timer timer;
   @Nullable
   private NotificationTask notificationTask;

   public PeriodicNotificationManager(ResourceLocation var1, Object2BooleanFunction<String> var2) {
      super();
      this.notifications = var1;
      this.selector = var2;
   }

   protected Map<String, List<Notification>> prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         BufferedReader var3 = var1.openAsReader(this.notifications);

         Map var4;
         try {
            var4 = (Map)CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(var3)).result().orElseThrow();
         } catch (Throwable var7) {
            if (var3 != null) {
               try {
                  ((Reader)var3).close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (var3 != null) {
            ((Reader)var3).close();
         }

         return var4;
      } catch (Exception var8) {
         LOGGER.warn("Failed to load {}", this.notifications, var8);
         return ImmutableMap.of();
      }
   }

   protected void apply(Map<String, List<Notification>> var1, ResourceManager var2, ProfilerFiller var3) {
      List var4 = (List)var1.entrySet().stream().filter((var1x) -> (Boolean)this.selector.apply((String)var1x.getKey())).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
      if (var4.isEmpty()) {
         this.stopTimer();
      } else if (var4.stream().anyMatch((var0) -> var0.period == 0L)) {
         Util.logAndPauseIfInIde("A periodic notification in " + String.valueOf(this.notifications) + " has a period of zero minutes");
         this.stopTimer();
      } else {
         long var5 = this.calculateInitialDelay(var4);
         long var7 = this.calculateOptimalPeriod(var4, var5);
         if (this.timer == null) {
            this.timer = new Timer();
         }

         if (this.notificationTask == null) {
            this.notificationTask = new NotificationTask(var4, var5, var7);
         } else {
            this.notificationTask = this.notificationTask.reset(var4, var7);
         }

         this.timer.scheduleAtFixedRate(this.notificationTask, TimeUnit.MINUTES.toMillis(var5), TimeUnit.MINUTES.toMillis(var7));
      }
   }

   public void close() {
      this.stopTimer();
   }

   private void stopTimer() {
      if (this.timer != null) {
         this.timer.cancel();
      }

   }

   private long calculateOptimalPeriod(List<Notification> var1, long var2) {
      return var1.stream().mapToLong((var2x) -> {
         long var3 = var2x.delay - var2;
         return LongMath.gcd(var3, var2x.period);
      }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + String.valueOf(this.notifications)));
   }

   private long calculateInitialDelay(List<Notification> var1) {
      return var1.stream().mapToLong((var0) -> var0.delay).min().orElse(0L);
   }

   // $FF: synthetic method
   protected Object prepare(final ResourceManager var1, final ProfilerFiller var2) {
      return this.prepare(var1, var2);
   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, RecordCodecBuilder.create((var0) -> var0.group(Codec.LONG.optionalFieldOf("delay", 0L).forGetter(Notification::delay), Codec.LONG.fieldOf("period").forGetter(Notification::period), Codec.STRING.fieldOf("title").forGetter(Notification::title), Codec.STRING.fieldOf("message").forGetter(Notification::message)).apply(var0, Notification::new)).listOf());
      LOGGER = LogUtils.getLogger();
   }

   public static record Notification(long delay, long period, String title, String message) {
      final long delay;
      final long period;
      final String title;
      final String message;

      public Notification(final long var1, final long var3, final String var5, final String var6) {
         super();
         this.delay = var1 != 0L ? var1 : var3;
         this.period = var3;
         this.title = var5;
         this.message = var6;
      }
   }

   static class NotificationTask extends TimerTask {
      private final Minecraft minecraft = Minecraft.getInstance();
      private final List<Notification> notifications;
      private final long period;
      private final AtomicLong elapsed;

      public NotificationTask(List<Notification> var1, long var2, long var4) {
         super();
         this.notifications = var1;
         this.period = var4;
         this.elapsed = new AtomicLong(var2);
      }

      public NotificationTask reset(List<Notification> var1, long var2) {
         this.cancel();
         return new NotificationTask(var1, this.elapsed.get(), var2);
      }

      public void run() {
         long var1 = this.elapsed.getAndAdd(this.period);
         long var3 = this.elapsed.get();

         for(Notification var6 : this.notifications) {
            if (var1 >= var6.delay) {
               long var7 = var1 / var6.period;
               long var9 = var3 / var6.period;
               if (var7 != var9) {
                  this.minecraft.execute(() -> SystemToast.add(Minecraft.getInstance().getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable(var6.title, var7), Component.translatable(var6.message, var7)));
                  return;
               }
            }
         }

      }
   }
}
