package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomRandomFeatureConfig implements FeatureConfiguration {
   public final List<ConfiguredFeature<?>> features;
   public final int count;

   public RandomRandomFeatureConfig(List<ConfiguredFeature<?>> var1, int var2) {
      super();
      this.features = var1;
      this.count = var2;
   }

   public RandomRandomFeatureConfig(Feature<?>[] var1, FeatureConfiguration[] var2, int var3) {
      this((List)IntStream.range(0, var1.length).mapToObj((var2x) -> {
         return getConfiguredFeature(var1[var2x], var2[var2x]);
      }).collect(Collectors.toList()), var3);
   }

   private static <FC extends FeatureConfiguration> ConfiguredFeature<?> getConfiguredFeature(Feature<FC> var0, FeatureConfiguration var1) {
      return new ConfiguredFeature(var0, var1);
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("features"), var1.createList(this.features.stream().map((var1x) -> {
         return var1x.serialize(var1).getValue();
      })), var1.createString("count"), var1.createInt(this.count))));
   }

   public static <T> RandomRandomFeatureConfig deserialize(Dynamic<T> var0) {
      List var1 = var0.get("features").asList(ConfiguredFeature::deserialize);
      int var2 = var0.get("count").asInt(0);
      return new RandomRandomFeatureConfig(var1, var2);
   }
}
