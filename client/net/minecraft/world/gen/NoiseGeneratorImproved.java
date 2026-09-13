package net.minecraft.world.gen;

import java.util.Random;

public class NoiseGeneratorImproved extends NoiseGenerator {
   private int[] field_76312_d = new int[512];
   public double field_76315_a;
   public double field_76313_b;
   public double field_76314_c;
   private static final double[] field_152381_e = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] field_152382_f = new double[]{1.0, 1.0, -1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0};
   private static final double[] field_152383_g = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};
   private static final double[] field_152384_h = new double[]{1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, -1.0, 0.0};
   private static final double[] field_152385_i = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, -1.0, -1.0, 1.0, 1.0, -1.0, -1.0, 0.0, 1.0, 0.0, -1.0};

   public NoiseGeneratorImproved() {
      this(new Random());
   }

   public NoiseGeneratorImproved(Random var1) {
      super();
      this.field_76315_a = var1.nextDouble() * 256.0;
      this.field_76313_b = var1.nextDouble() * 256.0;
      this.field_76314_c = var1.nextDouble() * 256.0;
      int var2 = 0;

      while(var2 < 256) {
         this.field_76312_d[var2] = var2++;
      }

      for(int var5 = 0; var5 < 256; ++var5) {
         int var3 = var1.nextInt(256 - var5) + var5;
         int var4 = this.field_76312_d[var5];
         this.field_76312_d[var5] = this.field_76312_d[var3];
         this.field_76312_d[var3] = var4;
         this.field_76312_d[var5 + 256] = this.field_76312_d[var5];
      }
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

   public void func_76308_a(
      double[] var1, double var2, double var4, double var6, int var8, int var9, int var10, double var11, double var13, double var15, double var17
   ) {
      if (var9 == 1) {
         int var64 = 0;
         int var66 = 0;
         int var21 = 0;
         int var69 = 0;
         double var72 = 0.0;
         double var76 = 0.0;
         int var80 = 0;
         double var82 = 1.0 / var17;

         for(int var30 = 0; var30 < var8; ++var30) {
            double var83 = var2 + (double)var30 * var11 + this.field_76315_a;
            int var85 = (int)var83;
            if (var83 < (double)var85) {
               --var85;
            }

            int var34 = var85 & 0xFF;
            var83 -= (double)var85;
            double var86 = var83 * var83 * var83 * (var83 * (var83 * 6.0 - 15.0) + 10.0);

            for(int var87 = 0; var87 < var10; ++var87) {
               double var89 = var6 + (double)var87 * var15 + this.field_76314_c;
               int var91 = (int)var89;
               if (var89 < (double)var91) {
                  --var91;
               }

               int var92 = var91 & 0xFF;
               var89 -= (double)var91;
               double var93 = var89 * var89 * var89 * (var89 * (var89 * 6.0 - 15.0) + 10.0);
               var64 = this.field_76312_d[var34] + 0;
               var66 = this.field_76312_d[var64] + var92;
               var21 = this.field_76312_d[var34 + 1] + 0;
               var69 = this.field_76312_d[var21] + var92;
               var72 = this.func_76311_b(
                  var86, this.func_76309_a(this.field_76312_d[var66], var83, var89), this.func_76310_a(this.field_76312_d[var69], var83 - 1.0, 0.0, var89)
               );
               var76 = this.func_76311_b(
                  var86,
                  this.func_76310_a(this.field_76312_d[var66 + 1], var83, 0.0, var89 - 1.0),
                  this.func_76310_a(this.field_76312_d[var69 + 1], var83 - 1.0, 0.0, var89 - 1.0)
               );
               double var94 = this.func_76311_b(var93, var72, var76);
               int var97 = var80++;
               var1[var97] += var94 * var82;
            }
         }
      } else {
         int var19 = 0;
         double var20 = 1.0 / var17;
         int var22 = -1;
         int var23 = 0;
         int var24 = 0;
         int var25 = 0;
         int var26 = 0;
         int var27 = 0;
         int var28 = 0;
         double var29 = 0.0;
         double var31 = 0.0;
         double var33 = 0.0;
         double var35 = 0.0;

         for(int var37 = 0; var37 < var8; ++var37) {
            double var38 = var2 + (double)var37 * var11 + this.field_76315_a;
            int var40 = (int)var38;
            if (var38 < (double)var40) {
               --var40;
            }

            int var41 = var40 & 0xFF;
            var38 -= (double)var40;
            double var42 = var38 * var38 * var38 * (var38 * (var38 * 6.0 - 15.0) + 10.0);

            for(int var44 = 0; var44 < var10; ++var44) {
               double var45 = var6 + (double)var44 * var15 + this.field_76314_c;
               int var47 = (int)var45;
               if (var45 < (double)var47) {
                  --var47;
               }

               int var48 = var47 & 0xFF;
               var45 -= (double)var47;
               double var49 = var45 * var45 * var45 * (var45 * (var45 * 6.0 - 15.0) + 10.0);

               for(int var51 = 0; var51 < var9; ++var51) {
                  double var52 = var4 + (double)var51 * var13 + this.field_76313_b;
                  int var54 = (int)var52;
                  if (var52 < (double)var54) {
                     --var54;
                  }

                  int var55 = var54 & 0xFF;
                  var52 -= (double)var54;
                  double var56 = var52 * var52 * var52 * (var52 * (var52 * 6.0 - 15.0) + 10.0);
                  if (var51 == 0 || var55 != var22) {
                     var22 = var55;
                     var23 = this.field_76312_d[var41] + var55;
                     var24 = this.field_76312_d[var23] + var48;
                     var25 = this.field_76312_d[var23 + 1] + var48;
                     var26 = this.field_76312_d[var41 + 1] + var55;
                     var27 = this.field_76312_d[var26] + var48;
                     var28 = this.field_76312_d[var26 + 1] + var48;
                     var29 = this.func_76311_b(
                        var42,
                        this.func_76310_a(this.field_76312_d[var24], var38, var52, var45),
                        this.func_76310_a(this.field_76312_d[var27], var38 - 1.0, var52, var45)
                     );
                     var31 = this.func_76311_b(
                        var42,
                        this.func_76310_a(this.field_76312_d[var25], var38, var52 - 1.0, var45),
                        this.func_76310_a(this.field_76312_d[var28], var38 - 1.0, var52 - 1.0, var45)
                     );
                     var33 = this.func_76311_b(
                        var42,
                        this.func_76310_a(this.field_76312_d[var24 + 1], var38, var52, var45 - 1.0),
                        this.func_76310_a(this.field_76312_d[var27 + 1], var38 - 1.0, var52, var45 - 1.0)
                     );
                     var35 = this.func_76311_b(
                        var42,
                        this.func_76310_a(this.field_76312_d[var25 + 1], var38, var52 - 1.0, var45 - 1.0),
                        this.func_76310_a(this.field_76312_d[var28 + 1], var38 - 1.0, var52 - 1.0, var45 - 1.0)
                     );
                  }

                  double var58 = this.func_76311_b(var56, var29, var31);
                  double var60 = this.func_76311_b(var56, var33, var35);
                  double var62 = this.func_76311_b(var49, var58, var60);
                  int var10001 = var19++;
                  var1[var10001] += var62 * var20;
               }
            }
         }
      }
   }
}
