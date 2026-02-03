package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<RandomFeatureConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.apply2(RandomFeatureConfiguration::new, WeightedPlacedFeature.CODEC.listOf().fieldOf("features").forGetter((c) -> c.features), PlacedFeature.CODEC.fieldOf("default").forGetter((c) -> c.defaultFeature)));
   public final List<WeightedPlacedFeature> features;
   public final Holder<PlacedFeature> defaultFeature;

   public RandomFeatureConfiguration(final List<WeightedPlacedFeature> features, final Holder<PlacedFeature> defaultFeature) {
      super();
      this.features = features;
      this.defaultFeature = defaultFeature;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(this.features.stream().flatMap((weighted) -> (weighted.feature.value()).getFeatures()), (this.defaultFeature.value()).getFeatures());
   }
}
