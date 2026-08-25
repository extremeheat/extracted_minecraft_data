package net.minecraft.world.level.levelgen.densityfunction;

import net.minecraft.util.context.ContextKey;

public record ContextBoundSampler(ContextKey<? extends DensitySampler> key, DensitySampler fallbackSampler) implements DensitySampler {
   public ContextBoundSampler {
      super();
   }

   public void sampleVolume(final SamplerContext context, final DensityBuffer outputBuffer, final DensityVolume volume) {
      DensitySampler sampler = (DensitySampler)context.getFieldOrDefault(this.key, this.fallbackSampler);
      sampler.sampleVolume(context, outputBuffer, volume);
   }

   public float sampleValue(final SamplerContext context, final int blockX, final int blockY, final int blockZ) {
      DensitySampler sampler = (DensitySampler)context.getFieldOrDefault(this.key, this.fallbackSampler);
      return sampler.sampleValue(context, blockX, blockY, blockZ);
   }
}
