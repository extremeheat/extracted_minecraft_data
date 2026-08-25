package net.minecraft.world.level.levelgen.densityfunction.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.TilingMode;

public record GradientFunction(Direction.Axis axis, TilingMode tiling, int fromCoordinate, int toCoordinate, float fromValue, float toValue) implements DensityFunction {
   public static final MapCodec<GradientFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Direction.Axis.CODEC.fieldOf("axis").forGetter(GradientFunction::axis), TilingMode.CODEC.optionalFieldOf("tiling", TilingMode.CLAMP_TO_EDGE).forGetter(GradientFunction::tiling), Codec.INT.fieldOf("from_coordinate").forGetter(GradientFunction::fromCoordinate), Codec.INT.fieldOf("to_coordinate").forGetter(GradientFunction::toCoordinate), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("from_value").forGetter(GradientFunction::fromValue), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("to_value").forGetter(GradientFunction::toValue)).apply(i, GradientFunction::new)).validate(GradientFunction::validate);

   public GradientFunction {
      super();
   }

   private static DataResult<GradientFunction> validate(final GradientFunction gradient) {
      return gradient.fromCoordinate == gradient.toCoordinate ? DataResult.error(() -> "from_coordinate cannot be equal to to_coordinate") : DataResult.success(gradient);
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      int coordinateRange = this.toCoordinate - this.fromCoordinate;
      float coordinateFactor = (this.toValue - this.fromValue) / (float)coordinateRange;
      Object var10000;
      switch (this.tiling) {
         case CLAMP_TO_EDGE:
            int minCoordinate = Math.min(this.fromCoordinate, this.toCoordinate);
            int maxCoordinate = Math.max(this.fromCoordinate, this.toCoordinate);
            var10000 = new ClampedSampler(this.axis, this.fromCoordinate, minCoordinate, maxCoordinate, this.fromValue, coordinateFactor);
            break;
         case REPEAT:
            var10000 = new RepeatSampler(this.axis, this.fromCoordinate, coordinateRange, this.fromValue, coordinateFactor);
            break;
         case MIRRORED_REPEAT:
            var10000 = new MirroredRepeatSampler(this.axis, this.fromCoordinate, coordinateRange, this.fromValue, coordinateFactor);
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return (DensitySampler)var10000;
   }

   public Interval range() {
      return Interval.encapsulating(this.fromValue, this.toValue);
   }

   public @DensityFunction.Axes int domainAxes() {
      return DensityFunction.axesFrom(this.axis);
   }

   public MapCodec<GradientFunction> codec() {
      return CODEC;
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      return this;
   }

   private interface GradientSampler extends DensitySampler {
      static void sampleVolume(final GradientSampler sampler, final DensityBuffer output, final DensityVolume volume) {
         switch (sampler.axis()) {
            case X:
               for(int x = 0; x < volume.sizeX(); ++x) {
                  float value = sampler.compute(volume.blockX(x));

                  for(int z = 0; z < volume.sizeZ(); ++z) {
                     output.setRange(volume.indexUnchecked(x, 0, z), volume.sizeY(), value);
                  }
               }
               break;
            case Y:
               for(int y = 0; y < volume.sizeY(); ++y) {
                  float value = sampler.compute(volume.blockY(y));

                  for(int z = 0; z < volume.sizeZ(); ++z) {
                     for(int x = 0; x < volume.sizeX(); ++x) {
                        output.set(volume.indexUnchecked(x, y, z), value);
                     }
                  }
               }
               break;
            case Z:
               for(int z = 0; z < volume.sizeZ(); ++z) {
                  float value = sampler.compute(volume.blockZ(z));
                  output.setRange(volume.indexUnchecked(0, 0, z), volume.sizeX() * volume.sizeY(), value);
               }
         }

      }

      Direction.Axis axis();

      float compute(int coordinate);
   }

   private static record ClampedSampler(Direction.Axis axis, int fromCoordinate, int minCoordinate, int maxCoordinate, float fromValue, float coordinateFactor) implements GradientSampler {
      private ClampedSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         GradientFunction.GradientSampler.sampleVolume(this, outputBuffer, volume);
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.compute(this.axis.choose(blockX, blockY, blockZ));
      }

      public float compute(final int coordinate) {
         int relativeCoordinate = Mth.clamp(coordinate, this.minCoordinate, this.maxCoordinate) - this.fromCoordinate;
         return this.fromValue + (float)relativeCoordinate * this.coordinateFactor;
      }
   }

   private static record RepeatSampler(Direction.Axis axis, int fromCoordinate, int coordinateRange, float fromValue, float coordinateFactor) implements GradientSampler {
      private RepeatSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         GradientFunction.GradientSampler.sampleVolume(this, outputBuffer, volume);
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.compute(this.axis.choose(blockX, blockY, blockZ));
      }

      public float compute(final int coordinate) {
         int relativeCoordinate = coordinate - this.fromCoordinate;
         return this.fromValue + (float)Math.floorMod(relativeCoordinate, this.coordinateRange) * this.coordinateFactor;
      }
   }

   private static record MirroredRepeatSampler(Direction.Axis axis, int fromCoordinate, int coordinateRange, float fromValue, float coordinateFactor) implements GradientSampler {
      private MirroredRepeatSampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         GradientFunction.GradientSampler.sampleVolume(this, outputBuffer, volume);
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.compute(this.axis.choose(blockX, blockY, blockZ));
      }

      public float compute(final int coordinate) {
         int relativeCoordinate = coordinate - this.fromCoordinate;
         int tileIndex = Math.floorDiv(relativeCoordinate, this.coordinateRange);
         int localCoordinate = relativeCoordinate - tileIndex * this.coordinateRange;
         return (tileIndex & 1) == 0 ? this.fromValue + (float)localCoordinate * this.coordinateFactor : this.fromValue + (float)(this.coordinateRange - localCoordinate) * this.coordinateFactor;
      }
   }
}
