package org.apache.logging.log4j.core.util;

public final class SystemClock implements Clock {
   public SystemClock() {
      super();
   }

   public long currentTimeMillis() {
      return System.currentTimeMillis();
   }
}
