package net.minecraft.world.level.levelgen.densityfunction;

@FunctionalInterface
public interface DensitySamplerSet {
   DensitySampler.Bound get(DensityFunction function);

   default float sampleValue(final DensityFunction function, final int blockX, final int blockY, final int blockZ) {
      return this.get(function).sampleValue(blockX, blockY, blockZ);
   }
}
