package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<RandomFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.apply2(RandomFeatureConfiguration::new, WeightedPlacedFeature.CODEC.listOf().fieldOf("features").forGetter((var0x) -> {
         return var0x.features;
      }), PlacedFeature.CODEC.fieldOf("default").forGetter((var0x) -> {
         return var0x.defaultFeature;
      }));
   });
   public final List<WeightedPlacedFeature> features;
   public final Supplier<PlacedFeature> defaultFeature;

   public RandomFeatureConfiguration(List<WeightedPlacedFeature> var1, PlacedFeature var2) {
      this(var1, () -> {
         return var2;
      });
   }

   private RandomFeatureConfiguration(List<WeightedPlacedFeature> var1, Supplier<PlacedFeature> var2) {
      super();
      this.features = var1;
      this.defaultFeature = var2;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(this.features.stream().flatMap((var0) -> {
         return ((PlacedFeature)var0.feature.get()).getFeatures();
      }), ((PlacedFeature)this.defaultFeature.get()).getFeatures());
   }
}
