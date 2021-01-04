package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RandomBooleanFeatureConfig implements FeatureConfiguration {
   public final ConfiguredFeature<?> featureTrue;
   public final ConfiguredFeature<?> featureFalse;

   public RandomBooleanFeatureConfig(ConfiguredFeature<?> var1, ConfiguredFeature<?> var2) {
      super();
      this.featureTrue = var1;
      this.featureFalse = var2;
   }

   public RandomBooleanFeatureConfig(Feature<?> var1, FeatureConfiguration var2, Feature<?> var3, FeatureConfiguration var4) {
      this(getFeature(var1, var2), getFeature(var3, var4));
   }

   private static <FC extends FeatureConfiguration> ConfiguredFeature<FC> getFeature(Feature<FC> var0, FeatureConfiguration var1) {
      return new ConfiguredFeature(var0, var1);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("feature_true"), this.featureTrue.serialize(var1).getValue(), var1.createString("feature_false"), this.featureFalse.serialize(var1).getValue())));
   }

   public static <T> RandomBooleanFeatureConfig deserialize(Dynamic<T> var0) {
      ConfiguredFeature var1 = ConfiguredFeature.deserialize(var0.get("feature_true").orElseEmptyMap());
      ConfiguredFeature var2 = ConfiguredFeature.deserialize(var0.get("feature_false").orElseEmptyMap());
      return new RandomBooleanFeatureConfig(var1, var2);
   }
}
