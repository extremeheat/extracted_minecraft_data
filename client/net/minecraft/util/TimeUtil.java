package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.valueproviders.UniformInt;

public class TimeUtil {
   public static final long NANOSECONDS_PER_SECOND;
   public static final long NANOSECONDS_PER_MILLISECOND;

   public TimeUtil() {
      super();
   }

   public static UniformInt rangeOfSeconds(int var0, int var1) {
      return UniformInt.of(var0 * 20, var1 * 20);
   }

   static {
      NANOSECONDS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L);
      NANOSECONDS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1L);
   }
}
