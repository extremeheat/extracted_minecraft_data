package net.minecraft.world.level.levelgen.densityfunction.op;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Interval;
import net.minecraft.world.level.levelgen.densityfunction.DensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunctions;
import net.minecraft.world.level.levelgen.densityfunction.DensitySampler;
import net.minecraft.world.level.levelgen.densityfunction.DensityVolume;
import net.minecraft.world.level.levelgen.densityfunction.DfRewriteRule;
import net.minecraft.world.level.levelgen.densityfunction.SamplerContext;
import net.minecraft.world.level.levelgen.densityfunction.ScopedDensityBuffer;
import net.minecraft.world.level.levelgen.densityfunction.generator.ConstantFunction;

public record RangeChoiceFunction(DensityFunction input, float minInclusive, float maxExclusive, DensityFunction whenInRange, DensityFunction whenOutOfRange) implements DensityFunction {
   public static final MapCodec<RangeChoiceFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunction.CODEC.fieldOf("input").forGetter(RangeChoiceFunction::input), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("min_inclusive").forGetter(RangeChoiceFunction::minInclusive), DensityFunctions.NOISE_VALUE_CODEC.fieldOf("max_exclusive").forGetter(RangeChoiceFunction::maxExclusive), DensityFunction.CODEC.fieldOf("when_in_range").forGetter(RangeChoiceFunction::whenInRange), DensityFunction.CODEC.fieldOf("when_out_of_range").forGetter(RangeChoiceFunction::whenOutOfRange)).apply(i, RangeChoiceFunction::new));

   public RangeChoiceFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      DensitySampler input = this.input.compileSampler(context);
      DensityFunction var7 = this.whenInRange;
      if (var7 instanceof ConstantFunction var3) {
         ConstantFunction var10000 = var3;

         try {
            var14 = var10000.value();
         } catch (Throwable var11) {
            throw new MatchException(var11.toString(), var11);
         }

         float whenOutOfRangeValue = var14;
         if (true) {
            float whenInRangeValue = whenOutOfRangeValue;
            var7 = this.whenOutOfRange;
            if (var7 instanceof ConstantFunction) {
               ConstantFunction var5 = (ConstantFunction)var7;
               var10000 = var5;

               try {
                  var16 = var10000.value();
               } catch (Throwable var10) {
                  throw new MatchException(var10.toString(), var10);
               }

               whenOutOfRangeValue = var16;
               if (true) {
                  return new ConstSampler(input, this.minInclusive, this.maxExclusive, whenInRangeValue, whenOutOfRangeValue);
               }
            }
         }
      }

      return new Sampler(input, this.minInclusive, this.maxExclusive, this.whenInRange.compileSampler(context), this.whenOutOfRange.compileSampler(context));
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      DensityFunction input = rule.rewrite(this.input);
      DensityFunction whenInRange = rule.rewrite(this.whenInRange);
      DensityFunction whenOutOfRange = rule.rewrite(this.whenOutOfRange);
      return input == this.input && whenInRange == this.whenInRange && whenOutOfRange == this.whenOutOfRange ? this : new RangeChoiceFunction(input, this.minInclusive, this.maxExclusive, whenInRange, whenOutOfRange);
   }

   public Interval range() {
      return Interval.encapsulating(this.whenInRange.range(), this.whenOutOfRange.range());
   }

   public @DensityFunction.Axes int domainAxes() {
      return this.input.domainAxes() | this.whenInRange.domainAxes() | this.whenOutOfRange.domainAxes();
   }

   public MapCodec<RangeChoiceFunction> codec() {
      return CODEC;
   }

   private static record Sampler(DensitySampler input, float minInclusive, float maxExclusive, DensitySampler whenInRange, DensitySampler whenOutOfRange) implements DensitySampler {
      private Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.whenInRange.sampleVolume(context, outputBuffer, volume);

         try (ScopedDensityBuffer inputBuffer = context.acquireBuffer(volume)) {
            this.input.sampleVolume(context, inputBuffer, volume);

            try (ScopedDensityBuffer whenOutOfRangeBuffer = context.acquireBuffer(volume)) {
               this.whenOutOfRange.sampleVolume(context, whenOutOfRangeBuffer, volume);

               for(int i = 0; i < outputBuffer.size(); ++i) {
                  float input = inputBuffer.get(i);
                  if (!(input >= this.minInclusive) || !(input < this.maxExclusive)) {
                     outputBuffer.set(i, whenOutOfRangeBuffer.get(i));
                  }
               }
            }
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         float inputValue = this.input.sampleValue(context, blockX, blockY, blockZ);
         return inputValue >= this.minInclusive && inputValue < this.maxExclusive ? this.whenInRange.sampleValue(context, blockX, blockY, blockZ) : this.whenOutOfRange.sampleValue(context, blockX, blockY, blockZ);
      }
   }

   private static record ConstSampler(DensitySampler input, float minInclusive, float maxExclusive, float whenInRange, float whenOutOfRange) implements DensitySampler {
      private ConstSampler {
         super();
      }

      private float choose(final float input) {
         return input >= this.minInclusive && input < this.maxExclusive ? this.whenInRange : this.whenOutOfRange;
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         this.input.sampleVolume(context, outputBuffer, volume);

         for(int i = 0; i < outputBuffer.size(); ++i) {
            outputBuffer.set(i, this.choose(outputBuffer.get(i)));
         }

      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.choose(this.input.sampleValue(context, blockX, blockY, blockZ));
      }
   }
}
