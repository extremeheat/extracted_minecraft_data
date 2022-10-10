package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorPerlin extends NoiseGenerator {
   private final NoiseGeneratorSimplex[] field_151603_a;
   private final int field_151602_b;

   public NoiseGeneratorPerlin(Random var1, int var2) {
      super();
      this.field_151602_b = var2;
      this.field_151603_a = new NoiseGeneratorSimplex[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.field_151603_a[var3] = new NoiseGeneratorSimplex(var1);
      }

   }

   public double func_151601_a(double var1, double var3) {
      double var5 = 0.0D;
      double var7 = 1.0D;

      for(int var9 = 0; var9 < this.field_151602_b; ++var9) {
         var5 += this.field_151603_a[var9].func_151605_a(var1 * var7, var3 * var7) / var7;
         var7 /= 2.0D;
      }

      return var5;
   }

   public double[] func_202644_a(double var1, double var3, int var5, int var6, double var7, double var9, double var11) {
      return this.func_202645_a(var1, var3, var5, var6, var7, var9, var11, 0.5D);
   }

   public double[] func_202645_a(double var1, double var3, int var5, int var6, double var7, double var9, double var11, double var13) {
      double[] var15 = new double[var5 * var6];
      double var16 = 1.0D;
      double var18 = 1.0D;

      for(int var20 = 0; var20 < this.field_151602_b; ++var20) {
         this.field_151603_a[var20].func_151606_a(var15, var1, var3, var5, var6, var7 * var18 * var16, var9 * var18 * var16, 0.55D / var16);
         var18 *= var11;
         var16 *= var13;
      }

      return var15;
   }
}
