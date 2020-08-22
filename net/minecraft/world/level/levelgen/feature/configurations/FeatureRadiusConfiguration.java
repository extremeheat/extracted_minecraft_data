package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class FeatureRadiusConfiguration implements FeatureConfiguration {
   public final int radius;

   public FeatureRadiusConfiguration(int var1) {
      this.radius = var1;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("radius"), var1.createInt(this.radius))));
   }

   public static FeatureRadiusConfiguration deserialize(Dynamic var0) {
      int var1 = var0.get("radius").asInt(0);
      return new FeatureRadiusConfiguration(var1);
   }
}
