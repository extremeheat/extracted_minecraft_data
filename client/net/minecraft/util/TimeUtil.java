package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.valueproviders.UniformInt;

public class TimeUtil {
   public static final long NANOSECONDS_PER_SECOND;
   public static final long NANOSECONDS_PER_MILLISECOND;
   public static final long MILLISECONDS_PER_SECOND;
   public static final long SECONDS_PER_HOUR;
   public static final int SECONDS_PER_MINUTE;

   public TimeUtil() {
      super();
   }

   public static UniformInt rangeOfSeconds(int var0, int var1) {
      return UniformInt.of(var0 * 20, var1 * 20);
   }

   static {
      NANOSECONDS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L);
      NANOSECONDS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1L);
      MILLISECONDS_PER_SECOND = TimeUnit.SECONDS.toMillis(1L);
      SECONDS_PER_HOUR = TimeUnit.HOURS.toSeconds(1L);
      SECONDS_PER_MINUTE = (int)TimeUnit.MINUTES.toSeconds(1L);
   }
}
