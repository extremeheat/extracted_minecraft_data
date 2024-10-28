package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface TimeSource {
   long get(TimeUnit var1);

   public interface NanoTimeSource extends TimeSource, LongSupplier {
      default long get(TimeUnit var1) {
         return var1.convert(this.getAsLong(), TimeUnit.NANOSECONDS);
      }
   }
}
