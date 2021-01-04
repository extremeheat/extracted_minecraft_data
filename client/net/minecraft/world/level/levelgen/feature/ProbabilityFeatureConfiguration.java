package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

public class ProbabilityFeatureConfiguration implements CarverConfiguration, FeatureConfiguration {
   public final float probability;

   public ProbabilityFeatureConfiguration(float var1) {
      super();
      this.probability = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("probability"), var1.createFloat(this.probability))));
   }

   public static <T> ProbabilityFeatureConfiguration deserialize(Dynamic<T> var0) {
      float var1 = var0.get("probability").asFloat(0.0F);
      return new ProbabilityFeatureConfiguration(var1);
   }
}
