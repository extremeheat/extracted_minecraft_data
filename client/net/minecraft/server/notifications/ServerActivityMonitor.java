package net.minecraft.server.notifications;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Util;

public class ServerActivityMonitor {
   private final long minimumMillisBetweenNotifications;
   private final AtomicLong lastNotificationTime = new AtomicLong();
   private final AtomicBoolean serverActivity = new AtomicBoolean(false);
   private final NotificationManager notificationManager;

   public ServerActivityMonitor(NotificationManager var1, int var2) {
      super();
      this.notificationManager = var1;
      this.minimumMillisBetweenNotifications = TimeUnit.SECONDS.toMillis((long)var2);
   }

   public void tick() {
      this.processWithRateLimit();
   }

   public void reportLoginActivity() {
      this.serverActivity.set(true);
      this.processWithRateLimit();
   }

   private void processWithRateLimit() {
      long var1 = Util.getMillis();
      if (this.serverActivity.get() && var1 - this.lastNotificationTime.get() >= this.minimumMillisBetweenNotifications) {
         this.notificationManager.serverActivityOccured();
         this.lastNotificationTime.set(Util.getMillis());
      }

      this.serverActivity.set(false);
   }
}
