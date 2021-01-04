package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class FeatureRadius implements FeatureConfiguration {
   public final int radius;

   public FeatureRadius(int var1) {
      super();
      this.radius = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("radius"), var1.createInt(this.radius))));
   }

   public static <T> FeatureRadius deserialize(Dynamic<T> var0) {
      int var1 = var0.get("radius").asInt(0);
      return new FeatureRadius(var1);
   }
}
