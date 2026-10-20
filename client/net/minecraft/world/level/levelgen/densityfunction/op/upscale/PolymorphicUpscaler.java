package net.minecraft.world.level.levelgen.densityfunction.op.upscale;

import java.lang.invoke.MethodHandles;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;

public record PolymorphicUpscaler(UpscaleFunction.Mode modeX, UpscaleFunction.Mode modeY, UpscaleFunction.Mode modeZ, int sampleCountX, int sampleCountY, int sampleCountZ, int sampleStrideY, int sampleStrideX, int sampleStrideZ, int cellSizeX, int cellSizeY, int cellSizeZ, float cellSizeXInv, float cellSizeYInv, Upscaler.Coefficients[] coefficientsX, Upscaler.Coefficients[] coefficientsY, Upscaler.Coefficients[] coefficientsZ) implements Upscaler {
   public PolymorphicUpscaler(final Upscaler.Parameters parameters) {
      int sampleCountX = parameters.modeX().sampleCount();
      int sampleCountY = parameters.modeY().sampleCount();
      int sampleCountZ = parameters.modeZ().sampleCount();
      this(parameters.modeX(), parameters.modeY(), parameters.modeZ(), sampleCountX, sampleCountY, sampleCountZ, 1, sampleCountY, sampleCountX * sampleCountY, parameters.cellSizeX(), parameters.cellSizeY(), parameters.cellSizeZ(), 1.0F / (float)parameters.cellSizeX(), 1.0F / (float)parameters.cellSizeY(), Upscaler.Coefficients.computeTable(parameters.cellSizeX(), parameters.modeX()), Upscaler.Coefficients.computeTable(parameters.cellSizeY(), parameters.modeY()), Upscaler.Coefficients.computeTable(parameters.cellSizeZ(), parameters.modeZ()));
   }

   public PolymorphicUpscaler {
      super();
   }

   public Upscaler makeMonomorphicDelegate() {
      return (Upscaler)(new Upscaler.Delegate(this)).makeMonomorphic(MethodHandles.lookup());
   }

   public void doUpscale(final Upscaler.AxisPlan x, final Upscaler.AxisPlan y, final Upscaler.AxisPlan z, final DensityBuffer outputBuffer, final DensityVolume outputVolume, final DensityBuffer cellBuffer, final DensityVolume cellVolume) {
      if (x.mode() == this.modeX && y.mode() == this.modeY && z.mode() == this.modeZ && x.cellSize() == this.cellSizeX && y.cellSize() == this.cellSizeY && z.cellSize() == this.cellSizeZ) {
         float[] samples = new float[this.sampleCountX * this.sampleCountY * this.sampleCountZ];
         float[] scratchSamplesX = new float[this.sampleStrideX];
         float[] scratchSamplesZ = new float[this.sampleStrideZ];

         for(int cellZ = 0; cellZ < z.cellOutputCount(); ++cellZ) {
            int cellOutputZ = z.minCellOutput() + cellZ * this.cellSizeZ;

            for(int cellX = 0; cellX < x.cellOutputCount(); ++cellX) {
               int cellOutputX = x.minCellOutput() + cellX * this.cellSizeX;

               for(int sampleOffsetZ = 0; sampleOffsetZ < this.sampleCountZ; ++sampleOffsetZ) {
                  int sampleZ = Math.min(cellZ + sampleOffsetZ, cellVolume.sizeZ() - 1);

                  for(int sampleOffsetX = 0; sampleOffsetX < this.sampleCountX; ++sampleOffsetX) {
                     int sampleX = Math.min(cellX + sampleOffsetX, cellVolume.sizeX() - 1);

                     for(int sampleOffsetY = 0; sampleOffsetY < this.sampleCountY; ++sampleOffsetY) {
                        int sampleY = Math.min(sampleOffsetY, cellVolume.sizeY() - 1);
                        int sampleIndex = sampleOffsetX * this.sampleStrideX + sampleOffsetY * this.sampleStrideY + sampleOffsetZ * this.sampleStrideZ;
                        samples[sampleIndex] = cellBuffer.get(cellVolume.indexUnchecked(sampleX, sampleY, sampleZ));
                     }
                  }
               }

               this.fillCell(outputBuffer, outputVolume, cellOutputX, y.minCellOutput(), cellOutputZ, samples, scratchSamplesX, scratchSamplesZ);

               for(int cellY = 1; cellY < y.cellOutputCount(); ++cellY) {
                  int cellOutputY = y.minCellOutput() + cellY * this.cellSizeY;
                  int newSampleOffsetY = this.sampleCountY - 1;
                  int newSampleY = Math.min(cellY + newSampleOffsetY, cellVolume.sizeY() - 1);

                  for(int sampleOffsetZ = 0; sampleOffsetZ < this.sampleCountZ; ++sampleOffsetZ) {
                     int sampleZ = Math.min(cellZ + sampleOffsetZ, cellVolume.sizeZ() - 1);

                     for(int sampleOffsetX = 0; sampleOffsetX < this.sampleCountX; ++sampleOffsetX) {
                        int sampleX = Math.min(cellX + sampleOffsetX, cellVolume.sizeX() - 1);
                        int sampleColumnIndex = sampleOffsetX * this.sampleStrideX + sampleOffsetZ * this.sampleStrideZ;

                        for(int sampleOffsetY = 1; sampleOffsetY < this.sampleCountY; ++sampleOffsetY) {
                           int sampleIndex = sampleColumnIndex + sampleOffsetY * this.sampleStrideY;
                           samples[sampleIndex - this.sampleStrideY] = samples[sampleIndex];
                        }

                        samples[sampleColumnIndex + newSampleOffsetY * this.sampleStrideY] = cellBuffer.get(cellVolume.indexUnchecked(sampleX, newSampleY, sampleZ));
                     }
                  }

                  this.fillCell(outputBuffer, outputVolume, cellOutputX, cellOutputY, cellOutputZ, samples, scratchSamplesX, scratchSamplesZ);
               }
            }
         }

      } else {
         String var10002 = String.valueOf(x);
         throw new IllegalArgumentException("Axis plans " + var10002 + ", " + String.valueOf(y) + ", " + String.valueOf(z) + " are not supported for " + String.valueOf(this));
      }
   }

   private void fillCell(final DensityBuffer outputBuffer, final DensityVolume outputVolume, final int cellOutputX, final int cellOutputY, final int cellOutputZ, final float[] samples, final float[] scratchSamplesX, final float[] scratchSamplesZ) {
      int x0 = this.cellSizeX == 1 ? 0 : Math.max(0, -cellOutputX);
      int y0 = this.cellSizeY == 1 ? 0 : Math.max(0, -cellOutputY);
      int z0 = this.cellSizeZ == 1 ? 0 : Math.max(0, -cellOutputZ);
      int x1 = this.cellSizeX == 1 ? 0 : Math.min(this.cellSizeX, outputVolume.sizeX() - cellOutputX) - 1;
      int y1 = this.cellSizeY == 1 ? 0 : Math.min(this.cellSizeY, outputVolume.sizeY() - cellOutputY) - 1;
      int z1 = this.cellSizeZ == 1 ? 0 : Math.min(this.cellSizeZ, outputVolume.sizeZ() - cellOutputZ) - 1;
      int strideX = outputVolume.strideX();
      int strideZ = outputVolume.strideZ();
      int outputIndex = outputVolume.indexUnchecked(cellOutputX + x0, cellOutputY + y0, cellOutputZ + z0);

      for(int z = z0; z <= z1; ++z) {
         Upscaler.Coefficients coefficientsZ = this.coefficientsZ[z];

         for(int i = 0; i < this.sampleStrideZ; ++i) {
            scratchSamplesZ[i] = coefficientsZ.multiply(samples, i, this.sampleStrideZ);
         }

         this.fillCellXy(outputBuffer, scratchSamplesZ, scratchSamplesX, x0, x1, y0, y1, outputIndex, strideX);
         outputIndex += strideZ;
      }

   }

   private void fillCellXy(final DensityBuffer outputBuffer, final float[] samples, final float[] scratchSamplesX, final int x0, final int x1, final int y0, final int y1, int outputIndex, final int strideX) {
      if (this.modeX == UpscaleFunction.Mode.INTERPOLATE_LINEAR && this.modeY == UpscaleFunction.Mode.NEAREST_NEIGHBOR) {
         float v0 = samples[0];
         float v1 = samples[1];
         float valueStep = (v1 - v0) * this.cellSizeXInv;

         for(int x = x0; x <= x1; ++x) {
            scratchSamplesX[0] = v0 + valueStep * (float)x;
            this.fillCellY(outputBuffer, scratchSamplesX, y0, y1, outputIndex);
            outputIndex += strideX;
         }

      } else {
         for(int x = x0; x <= x1; ++x) {
            Upscaler.Coefficients coefficientsX = this.coefficientsX[x];

            for(int i = 0; i < this.sampleStrideX; ++i) {
               scratchSamplesX[i] = coefficientsX.multiply(samples, i, this.sampleStrideX);
            }

            this.fillCellY(outputBuffer, scratchSamplesX, y0, y1, outputIndex);
            outputIndex += strideX;
         }

      }
   }

   private void fillCellY(final DensityBuffer outputBuffer, final float[] samples, final int y0, final int y1, int outputIndex) {
      if (this.modeY == UpscaleFunction.Mode.NEAREST_NEIGHBOR) {
         float value = samples[0];

         for(int y = y0; y <= y1; ++y) {
            outputBuffer.set(outputIndex++, value);
         }

      } else if (this.modeY == UpscaleFunction.Mode.INTERPOLATE_LINEAR) {
         float v0 = samples[0];
         float v1 = samples[this.sampleStrideY];
         float valueStep = (v1 - v0) * this.cellSizeYInv;

         for(int y = y0; y <= y1; ++y) {
            outputBuffer.set(outputIndex++, v0 + valueStep * (float)y);
         }

      } else {
         for(int y = y0; y <= y1; ++y) {
            outputBuffer.set(outputIndex++, this.coefficientsY[y].multiply(samples, 0, this.sampleStrideY));
         }

      }
   }

   public float upscaleValue(final int xInCell, final int yInCell, final int zInCell, final DensityBuffer cellBuffer) {
      if (cellBuffer.size() != this.sampleCountX * this.sampleCountY * this.sampleCountZ) {
         int var10002 = cellBuffer.size();
         throw new IllegalArgumentException("Buffer had wrong size: " + var10002 + " for " + this.sampleCountX + "x" + this.sampleCountY + "x" + this.sampleCountZ + " samples");
      } else {
         Upscaler.Coefficients coefficientsZ = this.coefficientsZ[zInCell];
         float[] samplesZ = new float[this.sampleStrideZ];

         for(int i = 0; i < this.sampleStrideZ; ++i) {
            samplesZ[i] = coefficientsZ.multiply(cellBuffer.asArrayUnsafe(), i, this.sampleStrideZ);
         }

         Upscaler.Coefficients coefficientsX = this.coefficientsX[xInCell];
         float[] samplesX = new float[this.sampleStrideX];

         for(int i = 0; i < this.sampleStrideX; ++i) {
            samplesX[i] = coefficientsX.multiply(samplesZ, i, this.sampleStrideX);
         }

         return this.coefficientsY[yInCell].multiply(samplesX, 0, this.sampleStrideY);
      }
   }

   public boolean equals(final Object obj) {
      throw new UnsupportedOperationException();
   }
}
