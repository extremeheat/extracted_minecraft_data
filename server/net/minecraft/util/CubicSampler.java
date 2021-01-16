package net.minecraft.util;

import net.minecraft.world.phys.Vec3;

public class CubicSampler {
   private static final double[] GAUSSIAN_SAMPLE_KERNEL = new double[]{0.0D, 1.0D, 4.0D, 6.0D, 4.0D, 1.0D, 0.0D};

   public interface Vec3Fetcher {
      Vec3 fetch(int var1, int var2, int var3);
   }
}
