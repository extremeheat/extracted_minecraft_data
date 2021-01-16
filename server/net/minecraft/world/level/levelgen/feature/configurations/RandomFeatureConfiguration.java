package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.WeightedConfiguredFeature;

public class RandomFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<RandomFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.apply2(RandomFeatureConfiguration::new, WeightedConfiguredFeature.CODEC.listOf().fieldOf("features").forGetter((var0x) -> {
         return var0x.features;
      }), ConfiguredFeature.CODEC.fieldOf("default").forGetter((var0x) -> {
         return var0x.defaultFeature;
      }));
   });
   public final List<WeightedConfiguredFeature> features;
   public final Supplier<ConfiguredFeature<?, ?>> defaultFeature;

   public RandomFeatureConfiguration(List<WeightedConfiguredFeature> var1, ConfiguredFeature<?, ?> var2) {
      this(var1, () -> {
         return var2;
      });
   }

   private RandomFeatureConfiguration(List<WeightedConfiguredFeature> var1, Supplier<ConfiguredFeature<?, ?>> var2) {
      super();
      this.features = var1;
      this.defaultFeature = var2;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(this.features.stream().flatMap((var0) -> {
         return ((ConfiguredFeature)var0.feature.get()).getFeatures();
      }), ((ConfiguredFeature)this.defaultFeature.get()).getFeatures());
   }
}
