package net.minecraft.util;

import net.minecraft.world.phys.Vec3;

public class CubicSampler {
   private static final int GAUSSIAN_SAMPLE_RADIUS = 2;
   private static final int GAUSSIAN_SAMPLE_BREADTH = 6;
   private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};

   private CubicSampler() {
      super();
   }

   public static Vec3 gaussianSampleVec3(Vec3 var0, CubicSampler.Vec3Fetcher var1) {
      int var2 = Mth.floor(var0.x());
      int var3 = Mth.floor(var0.y());
      int var4 = Mth.floor(var0.z());
      double var5 = var0.x() - (double)var2;
      double var7 = var0.y() - (double)var3;
      double var9 = var0.z() - (double)var4;
      double var11 = 0.0;
      Vec3 var13 = Vec3.ZERO;

      for (int var14 = 0; var14 < 6; var14++) {
         double var15 = Mth.lerp(var5, GAUSSIAN_SAMPLE_KERNEL[var14 + 1], GAUSSIAN_SAMPLE_KERNEL[var14]);
         int var17 = var2 - 2 + var14;

         for (int var18 = 0; var18 < 6; var18++) {
            double var19 = Mth.lerp(var7, GAUSSIAN_SAMPLE_KERNEL[var18 + 1], GAUSSIAN_SAMPLE_KERNEL[var18]);
            int var21 = var3 - 2 + var18;

            for (int var22 = 0; var22 < 6; var22++) {
               double var23 = Mth.lerp(var9, GAUSSIAN_SAMPLE_KERNEL[var22 + 1], GAUSSIAN_SAMPLE_KERNEL[var22]);
               int var25 = var4 - 2 + var22;
               double var26 = var15 * var19 * var23;
               var11 += var26;
               var13 = var13.add(var1.fetch(var17, var21, var25).scale(var26));
            }
         }
      }

      return var13.scale(1.0 / var11);
   }

   @FunctionalInterface
   public interface Vec3Fetcher {
      Vec3 fetch(int var1, int var2, int var3);
   }
}
