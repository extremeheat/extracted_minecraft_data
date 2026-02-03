package net.minecraft.world.level.levelgen;

import net.minecraft.util.RandomSource;

public interface BitRandomSource extends RandomSource {
   float FLOAT_MULTIPLIER = 5.9604645E-8F;
   double DOUBLE_MULTIPLIER = 1.1102230246251565E-16;

   int next(final int bits);

   default int nextInt() {
      return this.next(32);
   }

   default int nextInt(final int bound) {
      if (bound <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else if ((bound & bound - 1) == 0) {
         return (int)((long)bound * (long)this.next(31) >> 31);
      } else {
         int sample;
         int modulo;
         do {
            sample = this.next(31);
            modulo = sample % bound;
         } while(sample - modulo + (bound - 1) < 0);

         return modulo;
      }
   }

   default long nextLong() {
      int upper = this.next(32);
      int lower = this.next(32);
      long shifted = (long)upper << 32;
      return shifted + (long)lower;
   }

   default boolean nextBoolean() {
      return this.next(1) != 0;
   }

   default float nextFloat() {
      return (float)this.next(24) * 5.9604645E-8F;
   }

   default double nextDouble() {
      int upper = this.next(26);
      int lower = this.next(27);
      long combined = ((long)upper << 27) + (long)lower;
      return (double)combined * 1.1102230246251565E-16;
   }
}
