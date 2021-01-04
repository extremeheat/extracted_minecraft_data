package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class PillagerOutpostConfiguration implements FeatureConfiguration {
   public final double probability;

   public PillagerOutpostConfiguration(double var1) {
      super();
      this.probability = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("probability"), var1.createDouble(this.probability))));
   }

   public static <T> PillagerOutpostConfiguration deserialize(Dynamic<T> var0) {
      float var1 = var0.get("probability").asFloat(0.0F);
      return new PillagerOutpostConfiguration((double)var1);
   }
}
