package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedConfiguredFeature;

public class RandomFeatureConfiguration implements FeatureConfiguration {
   public final List features;
   public final ConfiguredFeature defaultFeature;

   public RandomFeatureConfiguration(List var1, ConfiguredFeature var2) {
      this.features = var1;
      this.defaultFeature = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      Object var2 = var1.createList(this.features.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      }));
      Object var3 = this.defaultFeature.serialize(var1).getValue();
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("features"), var2, var1.createString("default"), var3)));
   }

   public static RandomFeatureConfiguration deserialize(Dynamic var0) {
      List var1 = var0.get("features").asList(WeightedConfiguredFeature::deserialize);
      ConfiguredFeature var2 = ConfiguredFeature.deserialize(var0.get("default").orElseEmptyMap());
      return new RandomFeatureConfiguration(var1, var2);
   }
}
