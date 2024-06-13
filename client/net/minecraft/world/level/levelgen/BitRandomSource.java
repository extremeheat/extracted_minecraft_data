package net.minecraft.world.level.levelgen;

import net.minecraft.util.RandomSource;

public interface BitRandomSource extends RandomSource {
   float FLOAT_MULTIPLIER = 5.9604645E-8F;
   double DOUBLE_MULTIPLIER = 1.1102230246251565E-16;

   int next(int var1);

   @Override
   default int nextInt() {
      return this.next(32);
   }

   @Override
   default int nextInt(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else if ((var1 & var1 - 1) == 0) {
         return (int)((long)var1 * (long)this.next(31) >> 31);
      } else {
         int var2;
         int var3;
         do {
            var2 = this.next(31);
            var3 = var2 % var1;
         } while (var2 - var3 + (var1 - 1) < 0);

         return var3;
      }
   }

   @Override
   default long nextLong() {
      int var1 = this.next(32);
      int var2 = this.next(32);
      long var3 = (long)var1 << 32;
      return var3 + (long)var2;
   }

   @Override
   default boolean nextBoolean() {
      return this.next(1) != 0;
   }

   @Override
   default float nextFloat() {
      return (float)this.next(24) * 5.9604645E-8F;
   }

   @Override
   default double nextDouble() {
      int var1 = this.next(26);
      int var2 = this.next(27);
      long var3 = ((long)var1 << 27) + (long)var2;
      return (double)var3 * 1.1102230246251565E-16;
   }
}
