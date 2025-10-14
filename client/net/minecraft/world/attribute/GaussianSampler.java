package net.minecraft.world.attribute;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class GaussianSampler {
   private static final int GAUSSIAN_SAMPLE_RADIUS = 2;
   private static final int GAUSSIAN_SAMPLE_BREADTH = 6;
   private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0, 1.0, 4.0, 6.0, 4.0, 1.0, 0.0};

   public GaussianSampler() {
      super();
   }

   public static <V> void sample(Vec3 var0, Sampler<V> var1, Accumulator<V> var2) {
      var0 = var0.subtract(0.5, 0.5, 0.5);
      int var3 = Mth.floor(var0.x());
      int var4 = Mth.floor(var0.y());
      int var5 = Mth.floor(var0.z());
      double var6 = var0.x() - (double)var3;
      double var8 = var0.y() - (double)var4;
      double var10 = var0.z() - (double)var5;

      for(int var12 = 0; var12 < 6; ++var12) {
         double var13 = Mth.lerp(var10, GAUSSIAN_SAMPLE_KERNEL[var12 + 1], GAUSSIAN_SAMPLE_KERNEL[var12]);
         int var15 = var5 - 2 + var12;

         for(int var16 = 0; var16 < 6; ++var16) {
            double var17 = Mth.lerp(var6, GAUSSIAN_SAMPLE_KERNEL[var16 + 1], GAUSSIAN_SAMPLE_KERNEL[var16]);
            int var19 = var3 - 2 + var16;

            for(int var20 = 0; var20 < 6; ++var20) {
               double var21 = Mth.lerp(var8, GAUSSIAN_SAMPLE_KERNEL[var20 + 1], GAUSSIAN_SAMPLE_KERNEL[var20]);
               int var23 = var4 - 2 + var20;
               double var24 = var17 * var21 * var13;
               Object var26 = var1.get(var19, var23, var15);
               var2.accumulate(var24, var26);
            }
         }
      }

   }

   @FunctionalInterface
   public interface Accumulator<V> {
      void accumulate(double var1, V var3);
   }

   @FunctionalInterface
   public interface Sampler<V> {
      V get(int var1, int var2, int var3);
   }
}
