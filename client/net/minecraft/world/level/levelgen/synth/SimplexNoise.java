package net.minecraft.world.level.levelgen.synth;

import java.util.Random;
import net.minecraft.util.Mth;

public class SimplexNoise {
   protected static final int[][] GRADIENT = new int[][]{{1, 1, 0}, {-1, 1, 0}, {1, -1, 0}, {-1, -1, 0}, {1, 0, 1}, {-1, 0, 1}, {1, 0, -1}, {-1, 0, -1}, {0, 1, 1}, {0, -1, 1}, {0, 1, -1}, {0, -1, -1}, {1, 1, 0}, {0, -1, 1}, {-1, 1, 0}, {0, -1, -1}};
   private static final double SQRT_3 = Math.sqrt(3.0D);
   private static final double F2;
   private static final double G2;
   private final int[] p = new int[512];
   public final double xo;
   public final double yo;
   public final double zo;

   public SimplexNoise(Random var1) {
      super();
      this.xo = var1.nextDouble() * 256.0D;
      this.yo = var1.nextDouble() * 256.0D;
      this.zo = var1.nextDouble() * 256.0D;

      int var2;
      for(var2 = 0; var2 < 256; this.p[var2] = var2++) {
      }

      for(var2 = 0; var2 < 256; ++var2) {
         int var3 = var1.nextInt(256 - var2);
         int var4 = this.p[var2];
         this.p[var2] = this.p[var3 + var2];
         this.p[var3 + var2] = var4;
      }

   }

   private int p(int var1) {
      return this.p[var1 & 255];
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
      double var5 = (var1 + var3) * F2;
      int var7 = Mth.floor(var1 + var5);
      int var8 = Mth.floor(var3 + var5);
      double var9 = (double)(var7 + var8) * G2;
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

      double var21 = var15 - (double)var19 + G2;
      double var23 = var17 - (double)var20 + G2;
      double var25 = var15 - 1.0D + 2.0D * G2;
      double var27 = var17 - 1.0D + 2.0D * G2;
      int var29 = var7 & 255;
      int var30 = var8 & 255;
      int var31 = this.p(var29 + this.p(var30)) % 12;
      int var32 = this.p(var29 + var19 + this.p(var30 + var20)) % 12;
      int var33 = this.p(var29 + 1 + this.p(var30 + 1)) % 12;
      double var34 = this.getCornerNoise3D(var31, var15, var17, 0.0D, 0.5D);
      double var36 = this.getCornerNoise3D(var32, var21, var23, 0.0D, 0.5D);
      double var38 = this.getCornerNoise3D(var33, var25, var27, 0.0D, 0.5D);
      return 70.0D * (var34 + var36 + var38);
   }

   static {
      F2 = 0.5D * (SQRT_3 - 1.0D);
      G2 = (3.0D - SQRT_3) / 6.0D;
   }
}
