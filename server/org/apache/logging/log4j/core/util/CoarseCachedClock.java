package org.apache.logging.log4j.core.util;

import java.util.concurrent.locks.LockSupport;

public final class CoarseCachedClock implements Clock {
   private static volatile CoarseCachedClock instance;
   private static final Object INSTANCE_LOCK = new Object();
   private volatile long millis = System.currentTimeMillis();
   private final Thread updater = new Log4jThread("CoarseCachedClock Updater Thread") {
      public void run() {
         while(true) {
            CoarseCachedClock.this.millis = System.currentTimeMillis();
            LockSupport.parkNanos(1000000L);
         }
      }
   };

   private CoarseCachedClock() {
      super();
      this.updater.setDaemon(true);
      this.updater.start();
   }

   public static CoarseCachedClock instance() {
      CoarseCachedClock var0 = instance;
      if (var0 == null) {
         synchronized(INSTANCE_LOCK) {
            var0 = instance;
            if (var0 == null) {
               instance = var0 = new CoarseCachedClock();
            }
         }
      }

      return var0;
   }

   public long currentTimeMillis() {
      return this.millis;
   }
}
