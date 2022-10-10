package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.util.math.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
   private final NoiseGeneratorImproved[] field_76307_a;
   private final int field_76306_b;

   public NoiseGeneratorOctaves(Random var1, int var2) {
      super();
      this.field_76306_b = var2;
      this.field_76307_a = new NoiseGeneratorImproved[var2];

      for(int var3 = 0; var3 < var2; ++var3) {
         this.field_76307_a[var3] = new NoiseGeneratorImproved(var1);
      }

   }

   public double func_205563_a(double var1, double var3, double var5) {
      double var7 = 0.0D;
      double var9 = 1.0D;

      for(int var11 = 0; var11 < this.field_76306_b; ++var11) {
         var7 += this.field_76307_a[var11].func_205560_c(var1 * var9, var3 * var9, var5 * var9) / var9;
         var9 /= 2.0D;
      }

      return var7;
   }

   public double[] func_202647_a(int var1, int var2, int var3, int var4, int var5, int var6, double var7, double var9, double var11) {
      double[] var13 = new double[var4 * var5 * var6];
      double var14 = 1.0D;

      for(int var16 = 0; var16 < this.field_76306_b; ++var16) {
         double var17 = (double)var1 * var14 * var7;
         double var19 = (double)var2 * var14 * var9;
         double var21 = (double)var3 * var14 * var11;
         long var23 = MathHelper.func_76124_d(var17);
         long var25 = MathHelper.func_76124_d(var21);
         var17 -= (double)var23;
         var21 -= (double)var25;
         var23 %= 16777216L;
         var25 %= 16777216L;
         var17 += (double)var23;
         var21 += (double)var25;
         this.field_76307_a[var16].func_76308_a(var13, var17, var19, var21, var4, var5, var6, var7 * var14, var9 * var14, var11 * var14, var14);
         var14 /= 2.0D;
      }

      return var13;
   }

   public double[] func_202646_a(int var1, int var2, int var3, int var4, double var5, double var7, double var9) {
      return this.func_202647_a(var1, 10, var2, var3, 1, var4, var5, 1.0D, var7);
   }
}
