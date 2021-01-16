package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public enum ThreadNameCachingStrategy {
   CACHED {
      public String getThreadName() {
         String var1 = (String)ThreadNameCachingStrategy.THREADLOCAL_NAME.get();
         if (var1 == null) {
            var1 = Thread.currentThread().getName();
            ThreadNameCachingStrategy.THREADLOCAL_NAME.set(var1);
         }

         return var1;
      }
   },
   UNCACHED {
      public String getThreadName() {
         return Thread.currentThread().getName();
      }
   };

   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final ThreadLocal<String> THREADLOCAL_NAME = new ThreadLocal();

   private ThreadNameCachingStrategy() {
   }

   abstract String getThreadName();

   public static ThreadNameCachingStrategy create() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("AsyncLogger.ThreadNameStrategy", CACHED.name());

      try {
         ThreadNameCachingStrategy var1 = valueOf(var0);
         LOGGER.debug("AsyncLogger.ThreadNameStrategy={}", var1);
         return var1;
      } catch (Exception var2) {
         LOGGER.debug("Using AsyncLogger.ThreadNameStrategy.CACHED: '{}' not valid: {}", var0, var2.toString());
         return CACHED;
      }
   }

   // $FF: synthetic method
   ThreadNameCachingStrategy(Object var3) {
      this();
   }
}
