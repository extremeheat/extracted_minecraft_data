package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;

public record InterpolatedFunction(DensityFunction input, int cellSizeXz, int cellSizeY) implements DensityFunction {
   public static final MapCodec<InterpolatedFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(InterpolatedFunction::input), ExtraCodecs.POSITIVE_INT.fieldOf("cell_size_xz").forGetter(InterpolatedFunction::cellSizeXz), ExtraCodecs.POSITIVE_INT.fieldOf("cell_size_y").forGetter(InterpolatedFunction::cellSizeY)).apply(i, InterpolatedFunction::new));

   public InterpolatedFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      return new Sampler(this.input.compileSampler(context), this.cellSizeXz, this.cellSizeY, 1.0F / (float)this.cellSizeXz, 1.0F / (float)this.cellSizeY);
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      return input == this.input ? this : new InterpolatedFunction(input, this.cellSizeXz, this.cellSizeY);
   }

   public Interval range() {
      return this.input.range();
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes();
   }

   public MapCodec<InterpolatedFunction> codec() {
      return CODEC;
   }

   private static record Sampler(DensitySampler input, int cellSizeXz, int cellSizeY, float cellSizeXzInv, float cellSizeYInv) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         if ((volume.stepBlockX() == this.cellSizeXz || volume.sizeX() == 1) && (volume.stepBlockY() == this.cellSizeY || volume.sizeY() == 1) && (volume.stepBlockZ() == this.cellSizeXz || volume.sizeZ() == 1) && Math.floorMod(volume.minBlockX(), this.cellSizeXz) == 0 && Math.floorMod(volume.minBlockY(), this.cellSizeY) == 0 && Math.floorMod(volume.minBlockZ(), this.cellSizeXz) == 0) {
            this.input.sampleVolume(context, outputBuffer, volume);
         } else if (volume.stepBlockX() == 1 && volume.stepBlockY() == 1 && volume.stepBlockZ() == 1) {
            this.sampleWithBlockStep(context, outputBuffer, volume);
         } else {
            this.sampleWithNonBlockStep(context, outputBuffer, volume);
         }
      }

      private void sampleWithNonBlockStep(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         DensityVolume blockVolume = new DensityVolume(volume.sizeX() * volume.stepBlockX(), volume.sizeY() * volume.stepBlockY(), volume.sizeZ() * volume.stepBlockZ(), volume.minBlockX(), volume.minBlockY(), volume.minBlockZ(), 1, 1, 1);

         try (ScopedDensityBuffer blockBuffer = context.acquireBuffer(blockVolume)) {
            this.sampleWithBlockStep(context, blockBuffer, blockVolume);

            for(int z = 0; z < volume.sizeZ(); ++z) {
               for(int x = 0; x < volume.sizeX(); ++x) {
                  for(int y = 0; y < volume.sizeY(); ++y) {
                     float value = blockBuffer.get(blockVolume.indexUnchecked(x * volume.stepBlockX(), y * volume.stepBlockY(), z * volume.stepBlockZ()));
                     outputBuffer.set(volume.indexUnchecked(x, y, z), value);
                  }
               }
            }
         }

      }

      private void sampleWithBlockStep(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         int minCellX = Mth.floorDiv(volume.minBlockX(), this.cellSizeXz);
         int minCellY = Mth.floorDiv(volume.minBlockY(), this.cellSizeY);
         int minCellZ = Mth.floorDiv(volume.minBlockZ(), this.cellSizeXz);
         int maxCellX = Mth.floorDiv(volume.maxBlockX(), this.cellSizeXz);
         int maxCellY = Mth.floorDiv(volume.maxBlockY(), this.cellSizeY);
         int maxCellZ = Mth.floorDiv(volume.maxBlockZ(), this.cellSizeXz);
         int cellCountX = maxCellX - minCellX + 1;
         int cellCountY = maxCellY - minCellY + 1;
         int cellCountZ = maxCellZ - minCellZ + 1;
         DensityVolume cellVolume = new DensityVolume(Math.floorMod(volume.maxBlockX(), this.cellSizeXz) == 0 ? cellCountX : cellCountX + 1, Math.floorMod(volume.maxBlockY(), this.cellSizeY) == 0 ? cellCountY : cellCountY + 1, Math.floorMod(volume.maxBlockZ(), this.cellSizeXz) == 0 ? cellCountZ : cellCountZ + 1, minCellX * this.cellSizeXz, minCellY * this.cellSizeY, minCellZ * this.cellSizeXz, this.cellSizeXz, this.cellSizeY, this.cellSizeXz);

         try (ScopedDensityBuffer cellBuffer = context.acquireBuffer(cellVolume)) {
            this.input.sampleVolume(context, cellBuffer, cellVolume);

            for(int cellZ = 0; cellZ < cellCountZ; ++cellZ) {
               int nextCellZ = Math.min(cellZ + 1, cellVolume.sizeZ() - 1);

               for(int cellX = 0; cellX < cellCountX; ++cellX) {
                  int nextCellX = Math.min(cellX + 1, cellVolume.sizeX() - 1);
                  float v000 = cellBuffer.get(cellVolume.indexUnchecked(cellX, 0, cellZ));
                  float v100 = cellBuffer.get(cellVolume.indexUnchecked(nextCellX, 0, cellZ));
                  float v001 = cellBuffer.get(cellVolume.indexUnchecked(cellX, 0, nextCellZ));
                  float v101 = cellBuffer.get(cellVolume.indexUnchecked(nextCellX, 0, nextCellZ));

                  for(int cellY = 0; cellY < cellCountY; ++cellY) {
                     int nextCellY = Math.min(cellY + 1, cellVolume.sizeY() - 1);
                     float v010 = cellBuffer.get(cellVolume.indexUnchecked(cellX, nextCellY, cellZ));
                     float v110 = cellBuffer.get(cellVolume.indexUnchecked(nextCellX, nextCellY, cellZ));
                     float v011 = cellBuffer.get(cellVolume.indexUnchecked(cellX, nextCellY, nextCellZ));
                     float v111 = cellBuffer.get(cellVolume.indexUnchecked(nextCellX, nextCellY, nextCellZ));
                     this.fillCell(outputBuffer, volume, cellVolume, cellX, cellY, cellZ, v000, v100, v010, v110, v001, v101, v011, v111);
                     v000 = v010;
                     v100 = v110;
                     v001 = v011;
                     v101 = v111;
                  }
               }
            }
         }

      }

      private void fillCell(final DensityBuffer outputBuffer, final DensityVolume outputVolume, final DensityVolume cellVolume, final int cellX, final int cellY, final int cellZ, final float v000, final float v100, final float v010, final float v110, final float v001, final float v101, final float v011, final float v111) {
         int cellOutputX = cellVolume.blockX(cellX) - outputVolume.minBlockX();
         int cellOutputY = cellVolume.blockY(cellY) - outputVolume.minBlockY();
         int cellOutputZ = cellVolume.blockZ(cellZ) - outputVolume.minBlockZ();
         int x0 = Math.max(0, -cellOutputX);
         int y0 = Math.max(0, -cellOutputY);
         int z0 = Math.max(0, -cellOutputZ);
         int x1 = Math.min(this.cellSizeXz, outputVolume.sizeX() - cellOutputX) - 1;
         int y1 = Math.min(this.cellSizeY, outputVolume.sizeY() - cellOutputY) - 1;
         int z1 = Math.min(this.cellSizeXz, outputVolume.sizeZ() - cellOutputZ) - 1;

         for(int z = z0; z <= z1; ++z) {
            int outputZ = cellOutputZ + z;
            float alphaZ = (float)z * this.cellSizeXzInv;
            float v00_ = Mth.lerp(alphaZ, v000, v001);
            float v01_ = Mth.lerp(alphaZ, v010, v011);
            float v10_ = Mth.lerp(alphaZ, v100, v101);
            float v11_ = Mth.lerp(alphaZ, v110, v111);

            for(int x = x0; x <= x1; ++x) {
               int outputX = cellOutputX + x;
               float alphaX = (float)x * this.cellSizeXzInv;
               float v_0_ = Mth.lerp(alphaX, v00_, v10_);
               float v_1_ = Mth.lerp(alphaX, v01_, v11_);
               float valueStep = (v_1_ - v_0_) * this.cellSizeYInv;
               float value = v_0_ + valueStep * (float)y0;
               int outputIndex = outputVolume.indexUnchecked(outputX, cellOutputY + y0, outputZ);

               for(int y = y0; y <= y1; ++y) {
                  outputBuffer.set(outputIndex++, value);
                  value += valueStep;
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         int xInCell = Math.floorMod(blockX, this.cellSizeXz);
         int yInCell = Math.floorMod(blockY, this.cellSizeY);
         int zInCell = Math.floorMod(blockZ, this.cellSizeXz);
         if (xInCell == 0 && yInCell == 0 && zInCell == 0) {
            return this.input.sampleValue(context, blockX, blockY, blockZ);
         } else {
            DensityVolume volume = new DensityVolume(2, 2, 2, blockX - xInCell, blockY - yInCell, blockZ - zInCell, this.cellSizeXz, this.cellSizeY, this.cellSizeXz);

            try (ScopedDensityBuffer inputBuffer = context.acquireBuffer(volume)) {
               this.input.sampleVolume(context, inputBuffer, volume);
               return Mth.lerp3((float)xInCell / (float)this.cellSizeXz, (float)yInCell / (float)this.cellSizeY, (float)zInCell / (float)this.cellSizeXz, inputBuffer.get(volume.indexUnchecked(0, 0, 0)), inputBuffer.get(volume.indexUnchecked(1, 0, 0)), inputBuffer.get(volume.indexUnchecked(0, 1, 0)), inputBuffer.get(volume.indexUnchecked(1, 1, 0)), inputBuffer.get(volume.indexUnchecked(0, 0, 1)), inputBuffer.get(volume.indexUnchecked(1, 0, 1)), inputBuffer.get(volume.indexUnchecked(0, 1, 1)), inputBuffer.get(volume.indexUnchecked(1, 1, 1)));
            }
         }
      }
   }
}
