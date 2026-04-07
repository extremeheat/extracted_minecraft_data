package net.minecraft.client;

import com.google.common.collect.ImmutableMap;
import com.google.common.math.LongMath;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PeriodicNotificationManager extends SimplePreparableReloadListener<Map<String, List<Notification>>> implements AutoCloseable {
   private static final Codec<Map<String, List<Notification>>> CODEC;
   private static final Logger LOGGER;
   private final Identifier notifications;
   private final Object2BooleanFunction<String> selector;
   private @Nullable Timer timer;
   private @Nullable NotificationTask notificationTask;

   public PeriodicNotificationManager(final Identifier notifications, final Object2BooleanFunction<String> selector) {
      super();
      this.notifications = notifications;
      this.selector = selector;
   }

   protected Map<String, List<Notification>> prepare(final ResourceManager manager, final ProfilerFiller profiler) {
      try {
         Reader reader = manager.openAsReader(this.notifications);

         Map var4;
         try {
            var4 = (Map)CODEC.parse(JsonOps.INSTANCE, StrictJsonParser.parse(reader)).result().orElseThrow();
         } catch (Throwable var7) {
            if (reader != null) {
               try {
                  reader.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }
            }

            throw var7;
         }

         if (reader != null) {
            reader.close();
         }

         return var4;
      } catch (Exception e) {
         LOGGER.warn("Failed to load {}", this.notifications, e);
         return ImmutableMap.of();
      }
   }

   protected void apply(final Map<String, List<Notification>> preparations, final ResourceManager manager, final ProfilerFiller profiler) {
      List<Notification> notifications = (List)preparations.entrySet().stream().filter((e) -> (Boolean)this.selector.apply((String)e.getKey())).map(Map.Entry::getValue).flatMap(Collection::stream).collect(Collectors.toList());
      if (notifications.isEmpty()) {
         this.stopTimer();
      } else if (notifications.stream().anyMatch((n) -> n.period == 0L)) {
         Util.logAndPauseIfInIde("A periodic notification in " + String.valueOf(this.notifications) + " has a period of zero minutes");
         this.stopTimer();
      } else {
         long delay = this.calculateInitialDelay(notifications);
         long period = this.calculateOptimalPeriod(notifications, delay);
         if (this.timer == null) {
            this.timer = new Timer();
         }

         if (this.notificationTask == null) {
            this.notificationTask = new NotificationTask(notifications, delay, period);
         } else {
            this.notificationTask = this.notificationTask.reset(notifications, period);
         }

         this.timer.scheduleAtFixedRate(this.notificationTask, TimeUnit.MINUTES.toMillis(delay), TimeUnit.MINUTES.toMillis(period));
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

   private long calculateOptimalPeriod(final List<Notification> notifications, final long initialDelay) {
      return notifications.stream().mapToLong((c) -> {
         long delayPeriods = c.delay - initialDelay;
         return LongMath.gcd(delayPeriods, c.period);
      }).reduce(LongMath::gcd).orElseThrow(() -> new IllegalStateException("Empty notifications from: " + String.valueOf(this.notifications)));
   }

   private long calculateInitialDelay(final List<Notification> notifications) {
      return notifications.stream().mapToLong((c) -> c.delay).min().orElse(0L);
   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, RecordCodecBuilder.create((i) -> i.group(Codec.LONG.optionalFieldOf("delay", 0L).forGetter(Notification::delay), Codec.LONG.fieldOf("period").forGetter(Notification::period), Codec.STRING.fieldOf("title").forGetter(Notification::title), Codec.STRING.fieldOf("message").forGetter(Notification::message)).apply(i, Notification::new)).listOf());
      LOGGER = LogUtils.getLogger();
   }

   public static record Notification(long delay, long period, String title, String message) {
      public Notification(final long delay, final long period, final String title, final String message) {
         super();
         this.delay = delay != 0L ? delay : period;
         this.period = period;
         this.title = title;
         this.message = message;
      }
   }

   private static class NotificationTask extends TimerTask {
      private final Minecraft minecraft = Minecraft.getInstance();
      private final List<Notification> notifications;
      private final long period;
      private final AtomicLong elapsed;

      public NotificationTask(final List<Notification> notifications, final long elapsed, final long period) {
         super();
         this.notifications = notifications;
         this.period = period;
         this.elapsed = new AtomicLong(elapsed);
      }

      public NotificationTask reset(final List<Notification> notifications, final long period) {
         this.cancel();
         return new NotificationTask(notifications, this.elapsed.get(), period);
      }

      public void run() {
         long currentMinute = this.elapsed.getAndAdd(this.period);
         long nextMinute = this.elapsed.get();

         for(Notification notification : this.notifications) {
            if (currentMinute >= notification.delay) {
               long elapsedPeriods = currentMinute / notification.period;
               long currentPeriods = nextMinute / notification.period;
               if (elapsedPeriods != currentPeriods) {
                  this.minecraft.execute(() -> SystemToast.add(Minecraft.getInstance().gui.toastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, Component.translatable(notification.title, elapsedPeriods), Component.translatable(notification.message, elapsedPeriods)));
                  return;
               }
            }
         }

      }
   }
}
