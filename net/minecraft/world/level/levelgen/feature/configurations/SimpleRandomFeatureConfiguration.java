package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class SimpleRandomFeatureConfiguration implements FeatureConfiguration {
   public final List features;

   public SimpleRandomFeatureConfiguration(List var1) {
      this.features = var1;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("features"), var1.createList(this.features.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      })))));
   }

   public static SimpleRandomFeatureConfiguration deserialize(Dynamic var0) {
      List var1 = var0.get("features").asList(ConfiguredFeature::deserialize);
      return new SimpleRandomFeatureConfiguration(var1);
   }
}
