package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class SimpleRandomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<SimpleRandomFeatureConfiguration> CODEC;
   public final List<Supplier<ConfiguredFeature<?, ?>>> features;

   public SimpleRandomFeatureConfiguration(List<Supplier<ConfiguredFeature<?, ?>>> var1) {
      super();
      this.features = var1;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return this.features.stream().flatMap((var0) -> {
         return ((ConfiguredFeature)var0.get()).getFeatures();
      });
   }

   static {
      CODEC = ConfiguredFeature.LIST_CODEC.fieldOf("features").xmap(SimpleRandomFeatureConfiguration::new, (var0) -> {
         return var0.features;
      }).codec();
   }
}
