package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class SeagrassFeatureConfiguration implements FeatureConfiguration {
   public final int count;
   public final double tallSeagrassProbability;

   public SeagrassFeatureConfiguration(int var1, double var2) {
      super();
      this.count = var1;
      this.tallSeagrassProbability = var2;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("count"), var1.createInt(this.count), var1.createString("tall_seagrass_probability"), var1.createDouble(this.tallSeagrassProbability))));
   }

   public static <T> SeagrassFeatureConfiguration deserialize(Dynamic<T> var0) {
      int var1 = var0.get("count").asInt(0);
      double var2 = var0.get("tall_seagrass_probability").asDouble(0.0D);
      return new SeagrassFeatureConfiguration(var1, var2);
   }
}
