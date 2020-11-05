package net.minecraft.util;

import java.util.Random;

public class IntRange {
   private final int minInclusive;
   private final int maxInclusive;

   public IntRange(int var1, int var2) {
      super();
      if (var2 < var1) {
         throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + var1 + ", Given max: " + var2);
      } else {
         this.minInclusive = var1;
         this.maxInclusive = var2;
      }
   }

   public static IntRange of(int var0, int var1) {
      return new IntRange(var0, var1);
   }

   public int randomValue(Random var1) {
      return Mth.nextInt(var1, this.minInclusive, this.maxInclusive);
   }

   public int getMinInclusive() {
      return this.minInclusive;
   }

   public int getMaxInclusive() {
      return this.maxInclusive;
   }

   public String toString() {
      return "IntRange[" + this.minInclusive + "-" + this.maxInclusive + "]";
   }
}
