package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.BufferedReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.Map.Entry;
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

public class PeriodicNotificationManager
   extends SimplePreparableReloadListener<Map<String, List<PeriodicNotificationManager.Notification>>>
   implements AutoCloseable {
   private static final Codec<Map<String, List<PeriodicNotificationManager.Notification>>> CODEC = Codec.unboundedMap(
      Codec.STRING,
      RecordCodecBuilder.create(
            var0 -> var0.group(
                     Codec.LONG.optionalFieldOf("delay", 0L).forGetter(PeriodicNotificationManager.Notification::delay),
                     Codec.LONG.fieldOf("period").forGetter(PeriodicNotificationManager.Notification::period),
                     Codec.STRING.fieldOf("title").forGetter(PeriodicNotificationManager.Notification::title),
                     Codec.STRING.fieldOf("message").forGetter(PeriodicNotificationManager.Notification::message)
                  )
                  .apply(var0, PeriodicNotificationManager.Notification::new)
         )
         .listOf()
   );
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation notifications;
   private final Object2BooleanFunction<String> selector;
   @Nullable
   private java.util.Timer timer;
   @Nullable
   private PeriodicNotificationManager.NotificationTask notificationTask;

   public PeriodicNotificationManager(ResourceLocation var1, Object2BooleanFunction<String> var2) {
      super();
      this.notifications = var1;
      this.selector = var2;
   }

   protected Map<String, List<PeriodicNotificationManager.Notification>> prepare(ResourceManager var1, ProfilerFiller var2) {
      try {
         Map var4;
         try (BufferedReader var3 = var1.openAsReader(this.notifications)) {
            var4 = (Map)CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(var3)).result().orElseThrow();
         }

         return var4;
      } catch (Exception var8) {
         LOGGER.warn("Failed to load {}", this.notifications, var8);
         return ImmutableMap.of();
      }
   }

   protected void apply(Map<String, List<PeriodicNotificationManager.Notification>> var1, ResourceManager var2, ProfilerFiller var3) {
      List var4 = var1.entrySet()
         .stream()
         .filter(var1x -> this.selector.apply((String)var1x.getKey()))
         .map(Entry::getValue)
         .flatMap(Collection::stream)
         .collect(Collectors.toList());
      if (var4.isEmpty()) {
         this.stopTimer();
      } else if (var4.stream().anyMatch(var0 -> var0.period == 0L)) {
         Util.logAndPauseIfInIde("A periodic notification in " + this.notifications + " has a period of zero minutes");
         this.stopTimer();
      } else {
         long var5 = this.calculateInitialDelay(var4);
         long var7 = this.calculateOptimalPeriod(var4, var5);
         if (this.timer == null) {
            this.timer = new java.util.Timer();
         }

         if (this.notificationTask == null) {
            this.notificationTask = new PeriodicNotificationManager.NotificationTask(var4, var5, var7);
         } else {
            this.notificationTask = this.notificationTask.reset(var4, var7);
         }

         this.timer.scheduleAtFixedRate(this.notificationTask, TimeUnit.MINUTES.toMillis(var5), TimeUnit.MINUTES.toMillis(var7));
      }
   }

   @Override
   public void close() {
      this.stopTimer();
   }

   private void stopTimer() {
      if (this.timer != null) {
         this.timer.cancel();
      }
   }

   private long calculateOptimalPeriod(List<PeriodicNotificationManager.Notification> var1, long var2) {
      return var1.stream().mapToLong(var2x -> {
         long var3 = var2x.delay - var2;
         return LongMath.gcd(var3, var2x.period);
      }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + this.notifications));
   }

   private long calculateInitialDelay(List<PeriodicNotificationManager.Notification> var1) {
      return var1.stream().mapToLong(var0 -> var0.delay).min().orElse(0L);
   }

   public static record Notification(long a, long b, String c, String d) {
      final long delay;
      final long period;
      final String title;
      final String message;

      public Notification(long var1, long var3, String var5, String var6) {
         super();
         this.delay = var1 != 0L ? var1 : var3;
         this.period = var3;
         this.title = var5;
         this.message = var6;
      }
   }

   static class NotificationTask extends TimerTask {
      private final Minecraft minecraft = Minecraft.getInstance();
      private final List<PeriodicNotificationManager.Notification> notifications;
      private final long period;
      private final AtomicLong elapsed;

      public NotificationTask(List<PeriodicNotificationManager.Notification> var1, long var2, long var4) {
         super();
         this.notifications = var1;
         this.period = var4;
         this.elapsed = new AtomicLong(var2);
      }

      public PeriodicNotificationManager.NotificationTask reset(List<PeriodicNotificationManager.Notification> var1, long var2) {
         this.cancel();
         return new PeriodicNotificationManager.NotificationTask(var1, this.elapsed.get(), var2);
      }

      @Override
      public void run() {
         long var1 = this.elapsed.getAndAdd(this.period);
         long var3 = this.elapsed.get();

         for(PeriodicNotificationManager.Notification var6 : this.notifications) {
            if (var1 >= var6.delay) {
               long var7 = var1 / var6.period;
               long var9 = var3 / var6.period;
               if (var7 != var9) {
                  this.minecraft
                     .execute(
                        () -> SystemToast.add(
                              Minecraft.getInstance().getToasts(),
                              SystemToast.SystemToastId.PERIODIC_NOTIFICATION,
                              Component.translatable(var6.title, var7),
                              Component.translatable(var6.message, var7)
                           )
                     );
                  return;
               }
            }
         }
      }
   }
}
