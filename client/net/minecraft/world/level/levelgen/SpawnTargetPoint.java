package net.minecraft.world.level.levelgen;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.densityfunction.DensityFunction;

public record SpawnTargetPoint(Map<Holder<DensityFunction>, Climate.Parameter> parameters) {
   public static final Codec<SpawnTargetPoint> CODEC;

   public SpawnTargetPoint {
      super();
   }

   public Wired wire(final DensityFunction.Visitor noiseWirer, final DensityFunction.Visitor flattener) {
      return new Wired(this.parameters.entrySet().stream().map((entry) -> {
         DensityFunction wiredFunction = ((DensityFunction)((Holder)entry.getKey()).value()).mapAll(noiseWirer);
         DensityFunction flattenedFunction = wiredFunction.mapAll(flattener);
         return Pair.of(flattenedFunction, (Climate.Parameter)entry.getValue());
      }).toList());
   }

   static {
      CODEC = Codec.unboundedMap(DensityFunction.REFERENCE_CODEC, Climate.Parameter.CODEC).xmap(SpawnTargetPoint::new, SpawnTargetPoint::parameters);
   }

   public static record Wired(List<Pair<DensityFunction, Climate.Parameter>> parameters) {
      public Wired {
         super();
      }

      public long sampleFitness(final DensityFunction.SinglePointContext context) {
         long fitness = 0L;

         for(Pair<DensityFunction, Climate.Parameter> parameter : this.parameters) {
            DensityFunction function = (DensityFunction)parameter.getFirst();
            long value = Climate.quantizeCoord(function.compute(context));
            fitness += Mth.square(((Climate.Parameter)parameter.getSecond()).distance(value));
         }

         return fitness;
      }
   }
}
