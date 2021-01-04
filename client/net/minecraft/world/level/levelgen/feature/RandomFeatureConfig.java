package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomFeatureConfig implements FeatureConfiguration {
   public final List<WeightedConfiguredFeature<?>> features;
   public final ConfiguredFeature<?> defaultFeature;

   public RandomFeatureConfig(List<WeightedConfiguredFeature<?>> var1, ConfiguredFeature<?> var2) {
      super();
      this.features = var1;
      this.defaultFeature = var2;
   }

   public RandomFeatureConfig(Feature<?>[] var1, FeatureConfiguration[] var2, float[] var3, Feature<?> var4, FeatureConfiguration var5) {
      this((List)IntStream.range(0, var1.length).mapToObj((var3x) -> {
         return getWeightedConfiguredFeature(var1[var3x], var2[var3x], var3[var3x]);
      }).collect(Collectors.toList()), getDefaultFeature(var4, var5));
   }

   private static <FC extends FeatureConfiguration> WeightedConfiguredFeature<FC> getWeightedConfiguredFeature(Feature<FC> var0, FeatureConfiguration var1, float var2) {
      return new WeightedConfiguredFeature(var0, var1, var2);
   }

   private static <FC extends FeatureConfiguration> ConfiguredFeature<FC> getDefaultFeature(Feature<FC> var0, FeatureConfiguration var1) {
      return new ConfiguredFeature(var0, var1);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      Object var2 = var1.createList(this.features.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      }));
      Object var3 = this.defaultFeature.serialize(var1).getValue();
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("features"), var2, var1.createString("default"), var3)));
   }

   public static <T> RandomFeatureConfig deserialize(Dynamic<T> var0) {
      List var1 = var0.get("features").asList(WeightedConfiguredFeature::deserialize);
      ConfiguredFeature var2 = ConfiguredFeature.deserialize(var0.get("default").orElseEmptyMap());
      return new RandomFeatureConfig(var1, var2);
   }
}
