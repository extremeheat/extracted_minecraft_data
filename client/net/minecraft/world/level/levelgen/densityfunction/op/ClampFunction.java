package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;

public record ClampFunction(DensityFunction input, float min, float max) implements DensityFunction {
   public static final MapCodec<ClampFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(ClampFunction::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min").forGetter(ClampFunction::min), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max").forGetter(ClampFunction::max)).apply(i, ClampFunction::new)).validate(ClampFunction::validate);

   public ClampFunction {
      super();
   }

   private static DataResult<ClampFunction> validate(final ClampFunction clamp) {
      return clamp.max < clamp.min ? DataResult.error(() -> "min (" + clamp.min + ") must be less than or equal to max (" + clamp.max + ")") : DataResult.success(clamp);
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      return new Sampler(this.input.compileSampler(context), this.min, this.max);
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      return input == this.input ? this : new ClampFunction(input, this.min, this.max);
   }

   public MapCodec<ClampFunction> codec() {
      return CODEC;
   }

   public Interval range() {
      return Interval.clamp(this.input.range(), this.min, this.max);
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes();
   }

   public static record Sampler(DensitySampler input, float min, float max) implements DensitySampler {
      public Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, Mth.clamp(outputBuffer.get(i), this.min, this.max));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return Mth.clamp(this.input.sampleValue(context, blockX, blockY, blockZ), this.min, this.max);
      }
   }
}
