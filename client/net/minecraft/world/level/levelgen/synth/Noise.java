package net.minecraft.world.level.levelgen.synth;

import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;

public interface Noise {
   Interval range();

   float get(double x, double y);

   float get(double x, double y, double z);

   default void addToVolume(final float[] buffer, final DensityVolume volume, final double xzScale, final double yScale, final float amplitude) {
      int index = 0;

      for(int indexZ = 0; indexZ < volume.sizeZ(); ++indexZ) {
         double z = (double)volume.blockZ(indexZ) * xzScale;

         for(int indexX = 0; indexX < volume.sizeX(); ++indexX) {
            double x = (double)volume.blockX(indexX) * xzScale;

            for(int indexY = 0; indexY < volume.sizeY(); ++indexY) {
               double y = (double)volume.blockY(indexY) * yScale;
               buffer[index] += amplitude * this.get(x, y, z);
               ++index;
            }
         }
      }

   }
}
