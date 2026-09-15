package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public record FindTopSurfaceFunction(DensityFunction density, DensityFunction upperBound, int lowerBound, int cellHeight) implements DensityFunction {
   public static final MapCodec<FindTopSurfaceFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("density").forGetter(FindTopSurfaceFunction::density), DensityFunction.CODEC.fieldOf("upper_bound").forGetter(FindTopSurfaceFunction::upperBound), Codec.intRange(DimensionType.MIN_Y * 2, DimensionType.MAX_Y * 2).fieldOf("lower_bound").forGetter(FindTopSurfaceFunction::lowerBound), ExtraCodecs.POSITIVE_INT.fieldOf("cell_height").forGetter(FindTopSurfaceFunction::cellHeight)).apply(i, FindTopSurfaceFunction::new));

   public FindTopSurfaceFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      Sampler sampler = new Sampler(this.density.compileSampler(context), this.upperBound.compileSampler(context), this.lowerBound, this.cellHeight);
      return new SliceFunction.YSampler(sampler, 0);
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction density = rule.rewrite(this.density);
      DensityFunction upperBound = rule.rewrite(this.upperBound);
      return density == this.density && upperBound == this.upperBound ? this : new FindTopSurfaceFunction(density, upperBound, this.lowerBound, this.cellHeight);
   }

   public Interval range() {
      return Interval.of((float)this.lowerBound, Math.max((float)this.lowerBound, this.upperBound.range().max()));
   }

   public @DensityFunction.Axes int domainAxes() {
      return (this.density.domainAxes() | this.upperBound.domainAxes()) & -3;
   }

   public MapCodec<FindTopSurfaceFunction> codec() {
      return CODEC;
   }

   private static record Sampler(DensitySampler density, DensitySampler upperBound, int lowerBound, int cellHeight) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         if (volume.sizeY() != 1) {
            throw new IllegalArgumentException("Cannot sample with sizeY=" + volume.sizeY());
         } else {
            this.upperBound.sampleVolume(context, outputBuffer, volume);
            int index = 0;

            for(int z = 0; z < volume.sizeZ(); ++z) {
               int blockZ = volume.blockZ(z);

               for(int x = 0; x < volume.sizeX(); ++x) {
                  int blockX = volume.blockX(x);
                  float upperBound = outputBuffer.get(index);
                  outputBuffer.set(index, (float)this.findSurfaceFrom(context, blockX, blockZ, upperBound));
                  ++index;
               }
            }

         }
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float upperBound = this.upperBound.sampleValue(context, blockX, blockY, blockZ);
         return (float)this.findSurfaceFrom(context, blockX, blockZ, upperBound);
      }

      private int findSurfaceFrom(final SamplerContext context, final int x, final int z, final float upperBound) {
         int topY = Mth.floor(upperBound / (float)this.cellHeight) * this.cellHeight;
         if (topY <= this.lowerBound) {
            return this.lowerBound;
         } else {
            for(int probeY = topY; probeY >= this.lowerBound; probeY -= this.cellHeight) {
               if (this.density.sampleValue(context, x, probeY, z) > 0.0F) {
                  return probeY;
               }
            }

            return this.lowerBound;
         }
      }
   }
}
