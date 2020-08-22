package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RandomBooleanFeatureConfiguration implements FeatureConfiguration {
   public final ConfiguredFeature featureTrue;
   public final ConfiguredFeature featureFalse;

   public RandomBooleanFeatureConfiguration(ConfiguredFeature var1, ConfiguredFeature var2) {
      this.featureTrue = var1;
      this.featureFalse = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("feature_true"), this.featureTrue.serialize(var1).getValue(), var1.createString("feature_false"), this.featureFalse.serialize(var1).getValue())));
   }

   public static RandomBooleanFeatureConfiguration deserialize(Dynamic var0) {
      ConfiguredFeature var1 = ConfiguredFeature.deserialize(var0.get("feature_true").orElseEmptyMap());
      ConfiguredFeature var2 = ConfiguredFeature.deserialize(var0.get("feature_false").orElseEmptyMap());
      return new RandomBooleanFeatureConfiguration(var1, var2);
   }
}
