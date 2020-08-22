package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RandomRandomFeatureConfiguration implements FeatureConfiguration {
   public final List features;
   public final int count;

   public RandomRandomFeatureConfiguration(List var1, int var2) {
      this.features = var1;
      this.count = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("features"), var1.createList(this.features.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      })), var1.createString("count"), var1.createInt(this.count))));
   }

   public static RandomRandomFeatureConfiguration deserialize(Dynamic var0) {
      List var1 = var0.get("features").asList(ConfiguredFeature::deserialize);
      int var2 = var0.get("count").asInt(0);
      return new RandomRandomFeatureConfiguration(var1, var2);
   }
}
