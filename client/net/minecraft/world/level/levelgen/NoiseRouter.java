package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;

public record NoiseRouter(DensityFunction temperature, DensityFunction vegetation, DensityFunction continents, DensityFunction erosion, DensityFunction depth, DensityFunction ridges, DensityFunction chunkSurfaceLevel, DensityFunction finalDensity) {
   public static final Codec<NoiseRouter> CODEC = RecordCodecBuilder.create((i) -> i.group(DensityFunction.CODEC.fieldOf("temperature").forGetter(NoiseRouter::temperature), DensityFunction.CODEC.fieldOf("vegetation").forGetter(NoiseRouter::vegetation), DensityFunction.CODEC.fieldOf("continents").forGetter(NoiseRouter::continents), DensityFunction.CODEC.fieldOf("erosion").forGetter(NoiseRouter::erosion), DensityFunction.CODEC.fieldOf("depth").forGetter(NoiseRouter::depth), DensityFunction.CODEC.fieldOf("ridges").forGetter(NoiseRouter::ridges), DensityFunction.CODEC.fieldOf("chunk_surface_level").forGetter(NoiseRouter::chunkSurfaceLevel), DensityFunction.CODEC.fieldOf("final_density").forGetter(NoiseRouter::finalDensity)).apply(i, NoiseRouter::new));

   public NoiseRouter {
      super();
   }

   public Climate.Sampler createClimateSampler(final DensitySamplerSet densitySamplers) {
      return new Climate.Sampler(densitySamplers.get(this.temperature), densitySamplers.get(this.vegetation), densitySamplers.get(this.continents), densitySamplers.get(this.erosion), densitySamplers.get(this.depth), densitySamplers.get(this.ridges));
   }
}
