package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountFeatureConfiguration implements FeatureConfiguration {
   public final int count;

   public CountFeatureConfiguration(int var1) {
      super();
      this.count = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("count"), var1.createInt(this.count))));
   }

   public static <T> CountFeatureConfiguration deserialize(Dynamic<T> var0) {
      int var1 = var0.get("count").asInt(0);
      return new CountFeatureConfiguration(var1);
   }
}
