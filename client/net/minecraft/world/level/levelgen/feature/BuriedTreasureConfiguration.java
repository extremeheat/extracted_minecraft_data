package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class BuriedTreasureConfiguration implements FeatureConfiguration {
   public final float probability;

   public BuriedTreasureConfiguration(float var1) {
      super();
      this.probability = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("probability"), var1.createFloat(this.probability))));
   }

   public static <T> BuriedTreasureConfiguration deserialize(Dynamic<T> var0) {
      float var1 = var0.get("probability").asFloat(0.0F);
      return new BuriedTreasureConfiguration(var1);
   }
}
