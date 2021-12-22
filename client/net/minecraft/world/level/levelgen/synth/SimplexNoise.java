package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;

public class SimplexNoise {
   protected static final int[][] GRADIENT = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
   private static final double SQRT_3 = Math.sqrt(3.0D);
   // $FF: renamed from: F2 double
   private static final double field_387;
   // $FF: renamed from: G2 double
   private static final double field_388;
   // $FF: renamed from: p int[]
   private final int[] field_389 = new int[512];
   // $FF: renamed from: xo double
   public final double field_390;
   // $FF: renamed from: yo double
   public final double field_391;
   // $FF: renamed from: zo double
   public final double field_392;

   public SimplexNoise(RandomSource var1) {
      super();
      this.field_390 = var1.nextDouble() * 256.0D;
      this.field_391 = var1.nextDouble() * 256.0D;
      this.field_392 = var1.nextDouble() * 256.0D;

      int var2;
      for(var2 = 0; var2 < 256; this.field_389[var2] = var2++) {
      }

      for(var2 = 0; var2 < 256; ++var2) {
         int var3 = var1.nextInt(256 - var2);
         int var4 = this.field_389[var2];
         this.field_389[var2] = this.field_389[var3 + var2];
         this.field_389[var3 + var2] = var4;
      }

   }

   // $FF: renamed from: p (int) int
   private int method_100(int var1) {
      return this.field_389[var1 & 255];
   }

   protected static double dot(int[] var0, double var1, double var3, double var5) {
      return (double)var0[0] * var1 + (double)var0[1] * var3 + (double)var0[2] * var5;
   }

   private double getCornerNoise3D(int var1, double var2, double var4, double var6, double var8) {
      double var12 = var8 - var2 * var2 - var4 * var4 - var6 * var6;
      double var10;
      if (var12 < 0.0D) {
         var10 = 0.0D;
      } else {
         var12 *= var12;
         var10 = var12 * var12 * dot(GRADIENT[var1], var2, var4, var6);
      }

      return var10;
   }

   public double getValue(double var1, double var3) {
      double var5 = (var1 + var3) * field_387;
      int var7 = Mth.floor(var1 + var5);
      int var8 = Mth.floor(var3 + var5);
      double var9 = (double)(var7 + var8) * field_388;
      double var11 = (double)var7 - var9;
      double var13 = (double)var8 - var9;
      double var15 = var1 - var11;
      double var17 = var3 - var13;
      byte var19;
      byte var20;
      if (var15 > var17) {
         var19 = 1;
         var20 = 0;
      } else {
         var19 = 0;
         var20 = 1;
      }

      double var21 = var15 - (double)var19 + field_388;
      double var23 = var17 - (double)var20 + field_388;
      double var25 = var15 - 1.0D + 2.0D * field_388;
      double var27 = var17 - 1.0D + 2.0D * field_388;
      int var29 = var7 & 255;
      int var30 = var8 & 255;
      int var31 = this.method_100(var29 + this.method_100(var30)) % 12;
      int var32 = this.method_100(var29 + var19 + this.method_100(var30 + var20)) % 12;
      int var33 = this.method_100(var29 + 1 + this.method_100(var30 + 1)) % 12;
      double var34 = this.getCornerNoise3D(var31, var15, var17, 0.0D, 0.5D);
      double var36 = this.getCornerNoise3D(var32, var21, var23, 0.0D, 0.5D);
      double var38 = this.getCornerNoise3D(var33, var25, var27, 0.0D, 0.5D);
      return 70.0D * (var34 + var36 + var38);
   }

   public double getValue(double var1, double var3, double var5) {
      double var7 = 0.3333333333333333D;
      double var9 = (var1 + var3 + var5) * 0.3333333333333333D;
      int var11 = Mth.floor(var1 + var9);
      int var12 = Mth.floor(var3 + var9);
      int var13 = Mth.floor(var5 + var9);
      double var14 = 0.16666666666666666D;
      double var16 = (double)(var11 + var12 + var13) * 0.16666666666666666D;
      double var18 = (double)var11 - var16;
      double var20 = (double)var12 - var16;
      double var22 = (double)var13 - var16;
      double var24 = var1 - var18;
      double var26 = var3 - var20;
      double var28 = var5 - var22;
      byte var30;
      byte var31;
      byte var32;
      byte var33;
      byte var34;
      byte var35;
      if (var24 >= var26) {
         if (var26 >= var28) {
            var30 = 1;
            var31 = 0;
            var32 = 0;
            var33 = 1;
            var34 = 1;
            var35 = 0;
         } else if (var24 >= var28) {
            var30 = 1;
            var31 = 0;
            var32 = 0;
            var33 = 1;
            var34 = 0;
            var35 = 1;
         } else {
            var30 = 0;
            var31 = 0;
            var32 = 1;
            var33 = 1;
            var34 = 0;
            var35 = 1;
         }
      } else if (var26 < var28) {
         var30 = 0;
         var31 = 0;
         var32 = 1;
         var33 = 0;
         var34 = 1;
         var35 = 1;
      } else if (var24 < var28) {
         var30 = 0;
         var31 = 1;
         var32 = 0;
         var33 = 0;
         var34 = 1;
         var35 = 1;
      } else {
         var30 = 0;
         var31 = 1;
         var32 = 0;
         var33 = 1;
         var34 = 1;
         var35 = 0;
      }

      double var36 = var24 - (double)var30 + 0.16666666666666666D;
      double var38 = var26 - (double)var31 + 0.16666666666666666D;
      double var40 = var28 - (double)var32 + 0.16666666666666666D;
      double var42 = var24 - (double)var33 + 0.3333333333333333D;
      double var44 = var26 - (double)var34 + 0.3333333333333333D;
      double var46 = var28 - (double)var35 + 0.3333333333333333D;
      double var48 = var24 - 1.0D + 0.5D;
      double var50 = var26 - 1.0D + 0.5D;
      double var52 = var28 - 1.0D + 0.5D;
      int var54 = var11 & 255;
      int var55 = var12 & 255;
      int var56 = var13 & 255;
      int var57 = this.method_100(var54 + this.method_100(var55 + this.method_100(var56))) % 12;
      int var58 = this.method_100(var54 + var30 + this.method_100(var55 + var31 + this.method_100(var56 + var32))) % 12;
      int var59 = this.method_100(var54 + var33 + this.method_100(var55 + var34 + this.method_100(var56 + var35))) % 12;
      int var60 = this.method_100(var54 + 1 + this.method_100(var55 + 1 + this.method_100(var56 + 1))) % 12;
      double var61 = this.getCornerNoise3D(var57, var24, var26, var28, 0.6D);
      double var63 = this.getCornerNoise3D(var58, var36, var38, var40, 0.6D);
      double var65 = this.getCornerNoise3D(var59, var42, var44, var46, 0.6D);
      double var67 = this.getCornerNoise3D(var60, var48, var50, var52, 0.6D);
      return 32.0D * (var61 + var63 + var65 + var67);
   }

   static {
      field_387 = 0.5D * (SQRT_3 - 1.0D);
      field_388 = (3.0D - SQRT_3) / 6.0D;
   }
}
