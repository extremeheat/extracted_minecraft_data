package org.apache.logging.log4j.core.util;

import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class ClockFactory {
   public static final String PROPERTY_NAME = "log4j.Clock";
   private static final StatusLogger LOGGER = StatusLogger.getLogger();

   private ClockFactory() {
      super();
   }

   public static Clock getClock() {
      return createClock();
   }

   private static Clock createClock() {
      String var0 = PropertiesUtil.getProperties().getStringProperty("log4j.Clock");
      if (var0 != null && !"SystemClock".equals(var0)) {
         if (!CachedClock.class.getName().equals(var0) && !"CachedClock".equals(var0)) {
            if (!CoarseCachedClock.class.getName().equals(var0) && !"CoarseCachedClock".equals(var0)) {
               try {
                  Clock var1 = (Clock)Loader.newCheckedInstanceOf(var0, Clock.class);
                  LOGGER.trace("Using {} for timestamps.", var1.getClass().getName());
                  return var1;
               } catch (Exception var3) {
                  String var2 = "Could not create {}: {}, using default SystemClock for timestamps.";
                  LOGGER.error("Could not create {}: {}, using default SystemClock for timestamps.", var0, var3);
                  return new SystemClock();
               }
            } else {
               LOGGER.trace("Using specified CoarseCachedClock for timestamps.");
               return CoarseCachedClock.instance();
            }
         } else {
            LOGGER.trace("Using specified CachedClock for timestamps.");
            return CachedClock.instance();
         }
      } else {
         LOGGER.trace("Using default SystemClock for timestamps.");
         return new SystemClock();
      }
   }
}
