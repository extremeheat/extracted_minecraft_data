package net.minecraft.world.level.levelgen.synth;

import java.util.Random;
import net.minecraft.util.Mth;

public final class ImprovedNoise {
   private final byte[] p;
   public final double xo;
   public final double yo;
   public final double zo;

   public ImprovedNoise(Random var1) {
      this.xo = var1.nextDouble() * 256.0D;
      this.yo = var1.nextDouble() * 256.0D;
      this.zo = var1.nextDouble() * 256.0D;
      this.p = new byte[256];

      int var2;
      for(var2 = 0; var2 < 256; ++var2) {
         this.p[var2] = (byte)var2;
      }

      for(var2 = 0; var2 < 256; ++var2) {
         int var3 = var1.nextInt(256 - var2);
         byte var4 = this.p[var2];
         this.p[var2] = this.p[var2 + var3];
         this.p[var2 + var3] = var4;
      }

   }

   public double noise(double var1, double var3, double var5, double var7, double var9) {
      double var11 = var1 + this.xo;
      double var13 = var3 + this.yo;
      double var15 = var5 + this.zo;
      int var17 = Mth.floor(var11);
      int var18 = Mth.floor(var13);
      int var19 = Mth.floor(var15);
      double var20 = var11 - (double)var17;
      double var22 = var13 - (double)var18;
      double var24 = var15 - (double)var19;
      double var26 = Mth.smoothstep(var20);
      double var28 = Mth.smoothstep(var22);
      double var30 = Mth.smoothstep(var24);
      double var32;
      if (var7 != 0.0D) {
         double var34 = Math.min(var9, var22);
         var32 = (double)Mth.floor(var34 / var7) * var7;
      } else {
         var32 = 0.0D;
      }

      return this.sampleAndLerp(var17, var18, var19, var20, var22 - var32, var24, var26, var28, var30);
   }

   private static double gradDot(int var0, double var1, double var3, double var5) {
      int var7 = var0 & 15;
      return SimplexNoise.dot(SimplexNoise.GRADIENT[var7], var1, var3, var5);
   }

   private int p(int var1) {
      return this.p[var1 & 255] & 255;
   }

   public double sampleAndLerp(int var1, int var2, int var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      int var16 = this.p(var1) + var2;
      int var17 = this.p(var16) + var3;
      int var18 = this.p(var16 + 1) + var3;
      int var19 = this.p(var1 + 1) + var2;
      int var20 = this.p(var19) + var3;
      int var21 = this.p(var19 + 1) + var3;
      double var22 = gradDot(this.p(var17), var4, var6, var8);
      double var24 = gradDot(this.p(var20), var4 - 1.0D, var6, var8);
      double var26 = gradDot(this.p(var18), var4, var6 - 1.0D, var8);
      double var28 = gradDot(this.p(var21), var4 - 1.0D, var6 - 1.0D, var8);
      double var30 = gradDot(this.p(var17 + 1), var4, var6, var8 - 1.0D);
      double var32 = gradDot(this.p(var20 + 1), var4 - 1.0D, var6, var8 - 1.0D);
      double var34 = gradDot(this.p(var18 + 1), var4, var6 - 1.0D, var8 - 1.0D);
      double var36 = gradDot(this.p(var21 + 1), var4 - 1.0D, var6 - 1.0D, var8 - 1.0D);
      return Mth.lerp3(var10, var12, var14, var22, var24, var26, var28, var30, var32, var34, var36);
   }
}
