package net.minecraft.world.level.levelgen;

import com.mojang.serialization.Codec;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;
import net.minecraft.world.level.levelgen.densityfunction.DensitySamplerSet;

public record SpawnTargetPoint(Map<Holder<DensityFunction>, Climate.Parameter> parameters) {
   public static final Codec<SpawnTargetPoint> CODEC;

   public SpawnTargetPoint {
      super();
   }

   public long sampleFitness(final DensitySamplerSet samplers, final int blockX, final int blockY, final int blockZ) {
      long fitness = 0L;

      for(Map.Entry<Holder<DensityFunction>, Climate.Parameter> parameter : this.parameters.entrySet()) {
         DensityFunction function = (DensityFunction)((Holder)parameter.getKey()).value();
         long value = Climate.quantizeCoord(samplers.sampleValue(function, blockX, blockY, blockZ));
         fitness += Mth.square(((Climate.Parameter)parameter.getValue()).distance(value));
      }

      return fitness;
   }

   static {
      CODEC = Codec.unboundedMap(DensityFunction.REFERENCE_CODEC, Climate.Parameter.CODEC).xmap(SpawnTargetPoint::new, SpawnTargetPoint::parameters);
   }
}
