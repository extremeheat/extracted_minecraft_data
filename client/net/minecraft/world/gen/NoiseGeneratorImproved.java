package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator {
   private final int[] field_76312_d = new int[512];
   public double field_76315_a;
   public double field_76313_b;
   public double field_76314_c;
   private static final double[] field_152381_e = new double[]{1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D};
   private static final double[] field_152382_f = new double[]{1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D};
   private static final double[] field_152383_g = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D, 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D};
   private static final double[] field_152384_h = new double[]{1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 1.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, -1.0D, 0.0D};
   private static final double[] field_152385_i = new double[]{0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 1.0D, -1.0D, -1.0D, 1.0D, 1.0D, -1.0D, -1.0D, 0.0D, 1.0D, 0.0D, -1.0D};

   public NoiseGeneratorImproved(Random var1) {
      super();
      this.field_76315_a = var1.nextDouble() * 256.0D;
      this.field_76313_b = var1.nextDouble() * 256.0D;
      this.field_76314_c = var1.nextDouble() * 256.0D;

      int var2;
      for(var2 = 0; var2 < 256; this.field_76312_d[var2] = var2++) {
      }

      for(var2 = 0; var2 < 256; ++var2) {
         int var3 = var1.nextInt(256 - var2) + var2;
         int var4 = this.field_76312_d[var2];
         this.field_76312_d[var2] = this.field_76312_d[var3];
         this.field_76312_d[var3] = var4;
         this.field_76312_d[var2 + 256] = this.field_76312_d[var2];
      }

   }

   public double func_205561_a(double var1, double var3, double var5) {
      double var7 = var1 + this.field_76315_a;
      double var9 = var3 + this.field_76313_b;
      double var11 = var5 + this.field_76314_c;
      int var13 = (int)var7;
      int var14 = (int)var9;
      int var15 = (int)var11;
      if (var7 < (double)var13) {
         --var13;
      }

      if (var9 < (double)var14) {
         --var14;
      }

      if (var11 < (double)var15) {
         --var15;
      }

      int var16 = var13 & 255;
      int var17 = var14 & 255;
      int var18 = var15 & 255;
      var7 -= (double)var13;
      var9 -= (double)var14;
      var11 -= (double)var15;
      double var19 = var7 * var7 * var7 * (var7 * (var7 * 6.0D - 15.0D) + 10.0D);
      double var21 = var9 * var9 * var9 * (var9 * (var9 * 6.0D - 15.0D) + 10.0D);
      double var23 = var11 * var11 * var11 * (var11 * (var11 * 6.0D - 15.0D) + 10.0D);
      int var25 = this.field_76312_d[var16] + var17;
      int var26 = this.field_76312_d[var25] + var18;
      int var27 = this.field_76312_d[var25 + 1] + var18;
      int var28 = this.field_76312_d[var16 + 1] + var17;
      int var29 = this.field_76312_d[var28] + var18;
      int var30 = this.field_76312_d[var28 + 1] + var18;
      return this.func_76311_b(var23, this.func_76311_b(var21, this.func_76311_b(var19, this.func_76310_a(this.field_76312_d[var26], var7, var9, var11), this.func_76310_a(this.field_76312_d[var29], var7 - 1.0D, var9, var11)), this.func_76311_b(var19, this.func_76310_a(this.field_76312_d[var27], var7, var9 - 1.0D, var11), this.func_76310_a(this.field_76312_d[var30], var7 - 1.0D, var9 - 1.0D, var11))), this.func_76311_b(var21, this.func_76311_b(var19, this.func_76310_a(this.field_76312_d[var26 + 1], var7, var9, var11 - 1.0D), this.func_76310_a(this.field_76312_d[var29 + 1], var7 - 1.0D, var9, var11 - 1.0D)), this.func_76311_b(var19, this.func_76310_a(this.field_76312_d[var27 + 1], var7, var9 - 1.0D, var11 - 1.0D), this.func_76310_a(this.field_76312_d[var30 + 1], var7 - 1.0D, var9 - 1.0D, var11 - 1.0D))));
   }

   public final double func_76311_b(double var1, double var3, double var5) {
      return var3 + var1 * (var5 - var3);
   }

   public final double func_76309_a(int var1, double var2, double var4) {
      int var6 = var1 & 15;
      return field_152384_h[var6] * var2 + field_152385_i[var6] * var4;
   }

   public final double func_76310_a(int var1, double var2, double var4, double var6) {
      int var8 = var1 & 15;
      return field_152381_e[var8] * var2 + field_152382_f[var8] * var4 + field_152383_g[var8] * var6;
   }

   public double func_205562_a(double var1, double var3) {
      return this.func_205561_a(var1, var3, 0.0D);
   }

   public double func_205560_c(double var1, double var3, double var5) {
      return this.func_205561_a(var1, var3, var5);
   }

   public void func_76308_a(double[] var1, double var2, double var4, double var6, int var8, int var9, int var10, double var11, double var13, double var15, double var17) {
      int var10001;
      int var19;
      int var22;
      double var31;
      double var35;
      int var37;
      double var38;
      int var40;
      int var41;
      double var42;
      int var75;
      if (var9 == 1) {
         boolean var64 = false;
         boolean var65 = false;
         boolean var21 = false;
         boolean var68 = false;
         double var70 = 0.0D;
         double var73 = 0.0D;
         var75 = 0;
         double var77 = 1.0D / var17;

         for(int var30 = 0; var30 < var8; ++var30) {
            var31 = var2 + (double)var30 * var11 + this.field_76315_a;
            int var78 = (int)var31;
            if (var31 < (double)var78) {
               --var78;
            }

            int var34 = var78 & 255;
            var31 -= (double)var78;
            var35 = var31 * var31 * var31 * (var31 * (var31 * 6.0D - 15.0D) + 10.0D);

            for(var37 = 0; var37 < var10; ++var37) {
               var38 = var6 + (double)var37 * var15 + this.field_76314_c;
               var40 = (int)var38;
               if (var38 < (double)var40) {
                  --var40;
               }

               var41 = var40 & 255;
               var38 -= (double)var40;
               var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0D - 15.0D) + 10.0D);
               var19 = this.field_76312_d[var34] + 0;
               int var66 = this.field_76312_d[var19] + var41;
               int var67 = this.field_76312_d[var34 + 1] + 0;
               var22 = this.field_76312_d[var67] + var41;
               var70 = this.func_76311_b(var35, this.func_76309_a(this.field_76312_d[var66], var31, var38), this.func_76310_a(this.field_76312_d[var22], var31 - 1.0D, 0.0D, var38));
               var73 = this.func_76311_b(var35, this.func_76310_a(this.field_76312_d[var66 + 1], var31, 0.0D, var38 - 1.0D), this.func_76310_a(this.field_76312_d[var22 + 1], var31 - 1.0D, 0.0D, var38 - 1.0D));
               double var79 = this.func_76311_b(var42, var70, var73);
               var10001 = var75++;
               var1[var10001] += var79 * var77;
            }
         }

      } else {
         var19 = 0;
         double var20 = 1.0D / var17;
         var22 = -1;
         boolean var23 = false;
         boolean var24 = false;
         boolean var25 = false;
         boolean var26 = false;
         boolean var27 = false;
         boolean var28 = false;
         double var29 = 0.0D;
         var31 = 0.0D;
         double var33 = 0.0D;
         var35 = 0.0D;

         for(var37 = 0; var37 < var8; ++var37) {
            var38 = var2 + (double)var37 * var11 + this.field_76315_a;
            var40 = (int)var38;
            if (var38 < (double)var40) {
               --var40;
            }

            var41 = var40 & 255;
            var38 -= (double)var40;
            var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0D - 15.0D) + 10.0D);

            for(int var44 = 0; var44 < var10; ++var44) {
               double var45 = var6 + (double)var44 * var15 + this.field_76314_c;
               int var47 = (int)var45;
               if (var45 < (double)var47) {
                  --var47;
               }

               int var48 = var47 & 255;
               var45 -= (double)var47;
               double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0D - 15.0D) + 10.0D);

               for(int var51 = 0; var51 < var9; ++var51) {
                  double var52 = var4 + (double)var51 * var13 + this.field_76313_b;
                  int var54 = (int)var52;
                  if (var52 < (double)var54) {
                     --var54;
                  }

                  int var55 = var54 & 255;
                  var52 -= (double)var54;
                  double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0D - 15.0D) + 10.0D);
                  if (var51 == 0 || var55 != var22) {
                     var22 = var55;
                     int var69 = this.field_76312_d[var41] + var55;
                     int var71 = this.field_76312_d[var69] + var48;
                     int var72 = this.field_76312_d[var69 + 1] + var48;
                     int var74 = this.field_76312_d[var41 + 1] + var55;
                     var75 = this.field_76312_d[var74] + var48;
                     int var76 = this.field_76312_d[var74 + 1] + var48;
                     var29 = this.func_76311_b(var42, this.func_76310_a(this.field_76312_d[var71], var38, var52, var45), this.func_76310_a(this.field_76312_d[var75], var38 - 1.0D, var52, var45));
                     var31 = this.func_76311_b(var42, this.func_76310_a(this.field_76312_d[var72], var38, var52 - 1.0D, var45), this.func_76310_a(this.field_76312_d[var76], var38 - 1.0D, var52 - 1.0D, var45));
                     var33 = this.func_76311_b(var42, this.func_76310_a(this.field_76312_d[var71 + 1], var38, var52, var45 - 1.0D), this.func_76310_a(this.field_76312_d[var75 + 1], var38 - 1.0D, var52, var45 - 1.0D));
                     var35 = this.func_76311_b(var42, this.func_76310_a(this.field_76312_d[var72 + 1], var38, var52 - 1.0D, var45 - 1.0D), this.func_76310_a(this.field_76312_d[var76 + 1], var38 - 1.0D, var52 - 1.0D, var45 - 1.0D));
                  }

                  double var58 = this.func_76311_b(var56, var29, var31);
                  double var60 = this.func_76311_b(var56, var33, var35);
                  double var62 = this.func_76311_b(var49, var58, var60);
                  var10001 = var19++;
                  var1[var10001] += var62 * var20;
               }
            }
         }

      }
   }
}
