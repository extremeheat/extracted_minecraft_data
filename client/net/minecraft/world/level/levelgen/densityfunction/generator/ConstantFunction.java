package net.minecraft.world.level.levelgen.densityfunction.generator;

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

public record ConstantFunction(float value) implements DensityFunction {
   public static final MapCodec<ConstantFunction> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(DensityFunctions.NOISE_VALUE_CODEC.fieldOf("value").forGetter(ConstantFunction::value)).apply(i, ConstantFunction::new));

   public ConstantFunction {
      super();
   }

   public DensitySampler compileSampler(final DensityFunction.CompileContext context) {
      return new Sampler(this.value);
   }

   public Interval range() {
      return Interval.ofExact(this.value);
   }

   public @DensityFunction.Axes int domainAxes() {
      return 0;
   }

   public MapCodec<ConstantFunction> codec() {
      return CODEC;
   }

   public DensityFunction rewriteChildren(final DfRewriteRule rule) {
      return this;
   }

   public static record Sampler(float value) implements DensitySampler {
      public Sampler {
         super();
      }

      public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
         outputBuffer.fill(this.value);
      }

      public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
         return this.value;
      }
   }
}
