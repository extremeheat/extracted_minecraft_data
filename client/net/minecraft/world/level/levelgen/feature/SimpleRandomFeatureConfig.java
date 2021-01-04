package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SimpleRandomFeatureConfig implements FeatureConfiguration {
   public final List<ConfiguredFeature<?>> features;

   public SimpleRandomFeatureConfig(List<ConfiguredFeature<?>> var1) {
      super();
      this.features = var1;
   }

   public SimpleRandomFeatureConfig(Feature<?>[] var1, FeatureConfiguration[] var2) {
      this((List)IntStream.range(0, var1.length).mapToObj((var2x) -> {
         return getConfiguredFeature(var1[var2x], var2[var2x]);
      }).collect(Collectors.toList()));
   }

   private static <FC extends FeatureConfiguration> ConfiguredFeature<FC> getConfiguredFeature(Feature<FC> var0, FeatureConfiguration var1) {
      return new ConfiguredFeature(var0, var1);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("features"), var1.createList(this.features.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      })))));
   }

   public static <T> SimpleRandomFeatureConfig deserialize(Dynamic<T> var0) {
      List var1 = var0.get("features").asList(ConfiguredFeature::deserialize);
      return new SimpleRandomFeatureConfig(var1);
   }
}
