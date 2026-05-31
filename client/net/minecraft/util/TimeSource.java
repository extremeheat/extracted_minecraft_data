package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface TimeSource {
   long get(TimeUnit timeUnit);

   static NanoTimeSource constant(final long value) {
      return () -> value;
   }

   public interface NanoTimeSource extends LongSupplier, TimeSource {
      default long get(final TimeUnit timeUnit) {
         return timeUnit.convert(this.getAsLong(), TimeUnit.NANOSECONDS);
      }
   }
}
