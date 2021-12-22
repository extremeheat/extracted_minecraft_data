package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.RandomSource;

public final class ImprovedNoise {
   private static final float SHIFT_UP_EPSILON = 1.0E-7F;
   // $FF: renamed from: p byte[]
   private final byte[] field_430;
   // $FF: renamed from: xo double
   public final double field_431;
   // $FF: renamed from: yo double
   public final double field_432;
   // $FF: renamed from: zo double
   public final double field_433;

   public ImprovedNoise(RandomSource var1) {
      super();
      this.field_431 = var1.nextDouble() * 256.0D;
      this.field_432 = var1.nextDouble() * 256.0D;
      this.field_433 = var1.nextDouble() * 256.0D;
      this.field_430 = new byte[256];

      int var2;
      for(var2 = 0; var2 < 256; ++var2) {
         this.field_430[var2] = (byte)var2;
      }

      for(var2 = 0; var2 < 256; ++var2) {
         int var3 = var1.nextInt(256 - var2);
         byte var4 = this.field_430[var2];
         this.field_430[var2] = this.field_430[var2 + var3];
         this.field_430[var2 + var3] = var4;
      }

   }

   public double noise(double var1, double var3, double var5) {
      return this.noise(var1, var3, var5, 0.0D, 0.0D);
   }

   /** @deprecated */
   @Deprecated
   public double noise(double var1, double var3, double var5, double var7, double var9) {
      double var11 = var1 + this.field_431;
      double var13 = var3 + this.field_432;
      double var15 = var5 + this.field_433;
      int var17 = Mth.floor(var11);
      int var18 = Mth.floor(var13);
      int var19 = Mth.floor(var15);
      double var20 = var11 - (double)var17;
      double var22 = var13 - (double)var18;
      double var24 = var15 - (double)var19;
      double var26;
      if (var7 != 0.0D) {
         double var28;
         if (var9 >= 0.0D && var9 < var22) {
            var28 = var9;
         } else {
            var28 = var22;
         }

         var26 = (double)Mth.floor(var28 / var7 + 1.0000000116860974E-7D) * var7;
      } else {
         var26 = 0.0D;
      }

      return this.sampleAndLerp(var17, var18, var19, var20, var22 - var26, var24, var22);
   }

   public double noiseWithDerivative(double var1, double var3, double var5, double[] var7) {
      double var8 = var1 + this.field_431;
      double var10 = var3 + this.field_432;
      double var12 = var5 + this.field_433;
      int var14 = Mth.floor(var8);
      int var15 = Mth.floor(var10);
      int var16 = Mth.floor(var12);
      double var17 = var8 - (double)var14;
      double var19 = var10 - (double)var15;
      double var21 = var12 - (double)var16;
      return this.sampleWithDerivative(var14, var15, var16, var17, var19, var21, var7);
   }

   private static double gradDot(int var0, double var1, double var3, double var5) {
      return SimplexNoise.dot(SimplexNoise.GRADIENT[var0 & 15], var1, var3, var5);
   }

   // $FF: renamed from: p (int) int
   private int method_115(int var1) {
      return this.field_430[var1 & 255] & 255;
   }

   private double sampleAndLerp(int var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      int var12 = this.method_115(var1);
      int var13 = this.method_115(var1 + 1);
      int var14 = this.method_115(var12 + var2);
      int var15 = this.method_115(var12 + var2 + 1);
      int var16 = this.method_115(var13 + var2);
      int var17 = this.method_115(var13 + var2 + 1);
      double var18 = gradDot(this.method_115(var14 + var3), var4, var6, var8);
      double var20 = gradDot(this.method_115(var16 + var3), var4 - 1.0D, var6, var8);
      double var22 = gradDot(this.method_115(var15 + var3), var4, var6 - 1.0D, var8);
      double var24 = gradDot(this.method_115(var17 + var3), var4 - 1.0D, var6 - 1.0D, var8);
      double var26 = gradDot(this.method_115(var14 + var3 + 1), var4, var6, var8 - 1.0D);
      double var28 = gradDot(this.method_115(var16 + var3 + 1), var4 - 1.0D, var6, var8 - 1.0D);
      double var30 = gradDot(this.method_115(var15 + var3 + 1), var4, var6 - 1.0D, var8 - 1.0D);
      double var32 = gradDot(this.method_115(var17 + var3 + 1), var4 - 1.0D, var6 - 1.0D, var8 - 1.0D);
      double var34 = Mth.smoothstep(var4);
      double var36 = Mth.smoothstep(var10);
      double var38 = Mth.smoothstep(var8);
      return Mth.lerp3(var34, var36, var38, var18, var20, var22, var24, var26, var28, var30, var32);
   }

   private double sampleWithDerivative(int var1, int var2, int var3, double var4, double var6, double var8, double[] var10) {
      int var11 = this.method_115(var1);
      int var12 = this.method_115(var1 + 1);
      int var13 = this.method_115(var11 + var2);
      int var14 = this.method_115(var11 + var2 + 1);
      int var15 = this.method_115(var12 + var2);
      int var16 = this.method_115(var12 + var2 + 1);
      int var17 = this.method_115(var13 + var3);
      int var18 = this.method_115(var15 + var3);
      int var19 = this.method_115(var14 + var3);
      int var20 = this.method_115(var16 + var3);
      int var21 = this.method_115(var13 + var3 + 1);
      int var22 = this.method_115(var15 + var3 + 1);
      int var23 = this.method_115(var14 + var3 + 1);
      int var24 = this.method_115(var16 + var3 + 1);
      int[] var25 = SimplexNoise.GRADIENT[var17 & 15];
      int[] var26 = SimplexNoise.GRADIENT[var18 & 15];
      int[] var27 = SimplexNoise.GRADIENT[var19 & 15];
      int[] var28 = SimplexNoise.GRADIENT[var20 & 15];
      int[] var29 = SimplexNoise.GRADIENT[var21 & 15];
      int[] var30 = SimplexNoise.GRADIENT[var22 & 15];
      int[] var31 = SimplexNoise.GRADIENT[var23 & 15];
      int[] var32 = SimplexNoise.GRADIENT[var24 & 15];
      double var33 = SimplexNoise.dot(var25, var4, var6, var8);
      double var35 = SimplexNoise.dot(var26, var4 - 1.0D, var6, var8);
      double var37 = SimplexNoise.dot(var27, var4, var6 - 1.0D, var8);
      double var39 = SimplexNoise.dot(var28, var4 - 1.0D, var6 - 1.0D, var8);
      double var41 = SimplexNoise.dot(var29, var4, var6, var8 - 1.0D);
      double var43 = SimplexNoise.dot(var30, var4 - 1.0D, var6, var8 - 1.0D);
      double var45 = SimplexNoise.dot(var31, var4, var6 - 1.0D, var8 - 1.0D);
      double var47 = SimplexNoise.dot(var32, var4 - 1.0D, var6 - 1.0D, var8 - 1.0D);
      double var49 = Mth.smoothstep(var4);
      double var51 = Mth.smoothstep(var6);
      double var53 = Mth.smoothstep(var8);
      double var55 = Mth.lerp3(var49, var51, var53, (double)var25[0], (double)var26[0], (double)var27[0], (double)var28[0], (double)var29[0], (double)var30[0], (double)var31[0], (double)var32[0]);
      double var57 = Mth.lerp3(var49, var51, var53, (double)var25[1], (double)var26[1], (double)var27[1], (double)var28[1], (double)var29[1], (double)var30[1], (double)var31[1], (double)var32[1]);
      double var59 = Mth.lerp3(var49, var51, var53, (double)var25[2], (double)var26[2], (double)var27[2], (double)var28[2], (double)var29[2], (double)var30[2], (double)var31[2], (double)var32[2]);
      double var61 = Mth.lerp2(var51, var53, var35 - var33, var39 - var37, var43 - var41, var47 - var45);
      double var63 = Mth.lerp2(var53, var49, var37 - var33, var45 - var41, var39 - var35, var47 - var43);
      double var65 = Mth.lerp2(var49, var51, var41 - var33, var43 - var35, var45 - var37, var47 - var39);
      double var67 = Mth.smoothstepDerivative(var4);
      double var69 = Mth.smoothstepDerivative(var6);
      double var71 = Mth.smoothstepDerivative(var8);
      double var73 = var55 + var67 * var61;
      double var75 = var57 + var69 * var63;
      double var77 = var59 + var71 * var65;
      var10[0] += var73;
      var10[1] += var75;
      var10[2] += var77;
      return Mth.lerp3(var49, var51, var53, var33, var35, var37, var39, var41, var43, var45, var47);
   }

   @VisibleForTesting
   public void parityConfigString(StringBuilder var1) {
      NoiseUtils.parityNoiseOctaveConfigString(var1, this.field_431, this.field_432, this.field_433, this.field_430);
   }
}
