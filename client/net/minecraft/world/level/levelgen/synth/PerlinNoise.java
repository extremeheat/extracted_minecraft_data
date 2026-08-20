package net.minecraft.world.level.levelgen.synth;

import com.google.common.annotations.VisibleForTesting;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;

public class PerlinNoise extends GradientNoise {
   public static final Interval RANGE = Interval.ofSymmetric(2.0F);
   public static final double STANDARD_DEVIATION = 0.2702247831245211;

   public PerlinNoise(final RandomSource random) {
      super(random);
   }

   public Interval range() {
      return RANGE;
   }

   public float get(final double x, final double y) {
      return this.get(wrap(x), 0.0, wrap(y));
   }

   public float get(final double _x, final double _y, final double _z) {
      double x = wrap(_x) + this.offsetX;
      double y = wrap(_y) + this.offsetY;
      double z = wrap(_z) + this.offsetZ;
      int floorX = Mth.floor(x);
      int floorY = Mth.floor(y);
      int floorZ = Mth.floor(z);
      float relativeX = (float)(x - (double)floorX);
      float relativeY = (float)(y - (double)floorY);
      float relativeZ = (float)(z - (double)floorZ);
      return this.sampleAndLerp(floorX, floorY, floorZ, relativeX, relativeY, relativeZ, relativeY);
   }

   public float noiseWithDerivative(final double _x, final double _y, final double _z, final float[] derivativeOut) {
      double x = wrap(_x) + this.offsetX;
      double y = wrap(_y) + this.offsetY;
      double z = wrap(_z) + this.offsetZ;
      int floorX = Mth.floor(x);
      int floorY = Mth.floor(y);
      int floorZ = Mth.floor(z);
      float relativeX = (float)(x - (double)floorX);
      float relativeY = (float)(y - (double)floorY);
      float relativeZ = (float)(z - (double)floorZ);
      return this.sampleWithDerivative(floorX, floorY, floorZ, relativeX, relativeY, relativeZ, derivativeOut);
   }

   protected float sampleAndLerp(final int x, final int y, final int z, final float relativeX, final float relativeY, final float relativeZ, final float originalRelativeY) {
      int x0 = this.permute(x);
      int x1 = this.permute(x + 1);
      int xy00 = this.permute(x0 + y);
      int xy01 = this.permute(x0 + y + 1);
      int xy10 = this.permute(x1 + y);
      int xy11 = this.permute(x1 + y + 1);
      float d000 = gradDot(this.permute(xy00 + z), relativeX, relativeY, relativeZ);
      float d100 = gradDot(this.permute(xy10 + z), relativeX - 1.0F, relativeY, relativeZ);
      float d010 = gradDot(this.permute(xy01 + z), relativeX, relativeY - 1.0F, relativeZ);
      float d110 = gradDot(this.permute(xy11 + z), relativeX - 1.0F, relativeY - 1.0F, relativeZ);
      float d001 = gradDot(this.permute(xy00 + z + 1), relativeX, relativeY, relativeZ - 1.0F);
      float d101 = gradDot(this.permute(xy10 + z + 1), relativeX - 1.0F, relativeY, relativeZ - 1.0F);
      float d011 = gradDot(this.permute(xy01 + z + 1), relativeX, relativeY - 1.0F, relativeZ - 1.0F);
      float d111 = gradDot(this.permute(xy11 + z + 1), relativeX - 1.0F, relativeY - 1.0F, relativeZ - 1.0F);
      float xAlpha = Mth.smoothstep(relativeX);
      float yAlpha = Mth.smoothstep(originalRelativeY);
      float zAlpha = Mth.smoothstep(relativeZ);
      return Mth.lerp3(xAlpha, yAlpha, zAlpha, d000, d100, d010, d110, d001, d101, d011, d111);
   }

   public void addToVolume(final float[] buffer, final DensityVolume volume, final double xzScale, final double yScale, final float amplitude) {
      float d000xz = 0.0F;
      float d100xz = 0.0F;
      float d010xz = 0.0F;
      float d110xz = 0.0F;
      float d001xz = 0.0F;
      float d101xz = 0.0F;
      float d011xz = 0.0F;
      float d111xz = 0.0F;
      float g000y = 0.0F;
      float g100y = 0.0F;
      float g010y = 0.0F;
      float g110y = 0.0F;
      float g001y = 0.0F;
      float g101y = 0.0F;
      float g011y = 0.0F;
      float g111y = 0.0F;
      int index = 0;

      for(int indexZ = 0; indexZ < volume.sizeZ(); ++indexZ) {
         double z = wrap((double)volume.blockZ(indexZ) * xzScale) + this.offsetZ;
         int floorZ = Mth.floor(z);
         float relativeZ = (float)(z - (double)floorZ);
         float alphaZ = Mth.smoothstep(relativeZ);

         for(int indexX = 0; indexX < volume.sizeX(); ++indexX) {
            double x = wrap((double)volume.blockX(indexX) * xzScale) + this.offsetX;
            int floorX = Mth.floor(x);
            float relativeX = (float)(x - (double)floorX);
            int x0 = this.permute(floorX);
            int x1 = this.permute(floorX + 1);
            float alphaX = Mth.smoothstep(relativeX);
            int lastFloorY = -2147483648;

            for(int indexY = 0; indexY < volume.sizeY(); ++indexY) {
               double y = wrap((double)volume.blockY(indexY) * yScale) + this.offsetY;
               int floorY = Mth.floor(y);
               float relativeY = (float)(y - (double)floorY);
               float alphaY = Mth.smoothstep(relativeY);
               if (lastFloorY != floorY) {
                  int xy00 = this.permute(x0 + floorY);
                  int xy01 = this.permute(x0 + floorY + 1);
                  int xy10 = this.permute(x1 + floorY);
                  int xy11 = this.permute(x1 + floorY + 1);
                  GradientNoise.Gradient g000 = this.permuteToGrad(xy00 + floorZ);
                  d000xz = g000.dotXz(relativeX, relativeZ);
                  g000y = (float)g000.y();
                  GradientNoise.Gradient g100 = this.permuteToGrad(xy10 + floorZ);
                  d100xz = g100.dotXz(relativeX - 1.0F, relativeZ);
                  g100y = (float)g100.y();
                  GradientNoise.Gradient g010 = this.permuteToGrad(xy01 + floorZ);
                  d010xz = g010.dotXz(relativeX, relativeZ);
                  g010y = (float)g010.y();
                  GradientNoise.Gradient g110 = this.permuteToGrad(xy11 + floorZ);
                  d110xz = g110.dotXz(relativeX - 1.0F, relativeZ);
                  g110y = (float)g110.y();
                  GradientNoise.Gradient g001 = this.permuteToGrad(xy00 + floorZ + 1);
                  d001xz = g001.dotXz(relativeX, relativeZ - 1.0F);
                  g001y = (float)g001.y();
                  GradientNoise.Gradient g101 = this.permuteToGrad(xy10 + floorZ + 1);
                  d101xz = g101.dotXz(relativeX - 1.0F, relativeZ - 1.0F);
                  g101y = (float)g101.y();
                  GradientNoise.Gradient g011 = this.permuteToGrad(xy01 + floorZ + 1);
                  d011xz = g011.dotXz(relativeX, relativeZ - 1.0F);
                  g011y = (float)g011.y();
                  GradientNoise.Gradient g111 = this.permuteToGrad(xy11 + floorZ + 1);
                  d111xz = g111.dotXz(relativeX - 1.0F, relativeZ - 1.0F);
                  g111y = (float)g111.y();
                  lastFloorY = floorY;
               }

               buffer[index] += amplitude * Mth.lerp3(alphaX, alphaY, alphaZ, d000xz + g000y * relativeY, d100xz + g100y * relativeY, d010xz + g010y * (relativeY - 1.0F), d110xz + g110y * (relativeY - 1.0F), d001xz + g001y * relativeY, d101xz + g101y * relativeY, d011xz + g011y * (relativeY - 1.0F), d111xz + g111y * (relativeY - 1.0F));
               ++index;
            }
         }
      }

   }

   private float sampleWithDerivative(final int x, final int y, final int z, final float xr, final float yr, final float zr, final float[] derivativeOut) {
      int x0 = this.permute(x);
      int x1 = this.permute(x + 1);
      int xy00 = this.permute(x0 + y);
      int xy01 = this.permute(x0 + y + 1);
      int xy10 = this.permute(x1 + y);
      int xy11 = this.permute(x1 + y + 1);
      GradientNoise.Gradient g000 = this.permuteToGrad(xy00 + z);
      GradientNoise.Gradient g100 = this.permuteToGrad(xy10 + z);
      GradientNoise.Gradient g010 = this.permuteToGrad(xy01 + z);
      GradientNoise.Gradient g110 = this.permuteToGrad(xy11 + z);
      GradientNoise.Gradient g001 = this.permuteToGrad(xy00 + z + 1);
      GradientNoise.Gradient g101 = this.permuteToGrad(xy10 + z + 1);
      GradientNoise.Gradient g011 = this.permuteToGrad(xy01 + z + 1);
      GradientNoise.Gradient g111 = this.permuteToGrad(xy11 + z + 1);
      float d000 = g000.dot(xr, yr, zr);
      float d100 = g100.dot(xr - 1.0F, yr, zr);
      float d010 = g010.dot(xr, yr - 1.0F, zr);
      float d110 = g110.dot(xr - 1.0F, yr - 1.0F, zr);
      float d001 = g001.dot(xr, yr, zr - 1.0F);
      float d101 = g101.dot(xr - 1.0F, yr, zr - 1.0F);
      float d011 = g011.dot(xr, yr - 1.0F, zr - 1.0F);
      float d111 = g111.dot(xr - 1.0F, yr - 1.0F, zr - 1.0F);
      float xAlpha = Mth.smoothstep(xr);
      float yAlpha = Mth.smoothstep(yr);
      float zAlpha = Mth.smoothstep(zr);
      float d1x = Mth.lerp3(xAlpha, yAlpha, zAlpha, (float)g000.x(), (float)g100.x(), (float)g010.x(), (float)g110.x(), (float)g001.x(), (float)g101.x(), (float)g011.x(), (float)g111.x());
      float d1y = Mth.lerp3(xAlpha, yAlpha, zAlpha, (float)g000.y(), (float)g100.y(), (float)g010.y(), (float)g110.y(), (float)g001.y(), (float)g101.y(), (float)g011.y(), (float)g111.y());
      float d1z = Mth.lerp3(xAlpha, yAlpha, zAlpha, (float)g000.z(), (float)g100.z(), (float)g010.z(), (float)g110.z(), (float)g001.z(), (float)g101.z(), (float)g011.z(), (float)g111.z());
      float d2x = Mth.lerp2(yAlpha, zAlpha, d100 - d000, d110 - d010, d101 - d001, d111 - d011);
      float d2y = Mth.lerp2(zAlpha, xAlpha, d010 - d000, d011 - d001, d110 - d100, d111 - d101);
      float d2z = Mth.lerp2(xAlpha, yAlpha, d001 - d000, d101 - d100, d011 - d010, d111 - d110);
      float xSD = Mth.smoothstepDerivative(xr);
      float ySD = Mth.smoothstepDerivative(yr);
      float zSD = Mth.smoothstepDerivative(zr);
      float dX = d1x + xSD * d2x;
      float dY = d1y + ySD * d2y;
      float dZ = d1z + zSD * d2z;
      derivativeOut[0] += dX;
      derivativeOut[1] += dY;
      derivativeOut[2] += dZ;
      return Mth.lerp3(xAlpha, yAlpha, zAlpha, d000, d100, d010, d110, d001, d101, d011, d111);
   }

   @VisibleForTesting
   public void parityConfigString(final StringBuilder sb) {
      NoiseUtils.parityNoiseOctaveConfigString(sb, this.offsetX, this.offsetY, this.offsetZ, this.perms);
   }
}
