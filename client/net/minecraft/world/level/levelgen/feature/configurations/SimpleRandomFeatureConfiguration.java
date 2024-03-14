package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class SimpleRandomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<SimpleRandomFeatureConfiguration> CODEC = ExtraCodecs.nonEmptyHolderSet(PlacedFeature.LIST_CODEC)
      .fieldOf("features")
      .xmap(SimpleRandomFeatureConfiguration::new, var0 -> var0.features)
      .codec();
   public final HolderSet<PlacedFeature> features;

   public SimpleRandomFeatureConfiguration(HolderSet<PlacedFeature> var1) {
      super();
      this.features = var1;
   }

   @Override
   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return this.features.stream().flatMap(var0 -> ((PlacedFeature)var0.value()).getFeatures());
   }
}
