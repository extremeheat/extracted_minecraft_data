package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SimpleRandomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<SimpleRandomFeatureConfiguration> CODEC;
   public final List<Supplier<PlacedFeature>> features;

   public SimpleRandomFeatureConfiguration(List<Supplier<PlacedFeature>> var1) {
      super();
      this.features = var1;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return this.features.stream().flatMap((var0) -> {
         return ((PlacedFeature)var0.get()).getFeatures();
      });
   }

   static {
      CODEC = ExtraCodecs.nonEmptyList(PlacedFeature.LIST_CODEC).fieldOf("features").xmap(SimpleRandomFeatureConfiguration::new, (var0) -> {
         return var0.features;
      }).codec();
   }
}
