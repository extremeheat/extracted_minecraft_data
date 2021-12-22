package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RandomBooleanFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<RandomBooleanFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(PlacedFeature.CODEC.fieldOf("feature_true").forGetter((var0x) -> {
         return var0x.featureTrue;
      }), PlacedFeature.CODEC.fieldOf("feature_false").forGetter((var0x) -> {
         return var0x.featureFalse;
      })).apply(var0, RandomBooleanFeatureConfiguration::new);
   });
   public final Supplier<PlacedFeature> featureTrue;
   public final Supplier<PlacedFeature> featureFalse;

   public RandomBooleanFeatureConfiguration(Supplier<PlacedFeature> var1, Supplier<PlacedFeature> var2) {
      super();
      this.featureTrue = var1;
      this.featureFalse = var2;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(((PlacedFeature)this.featureTrue.get()).getFeatures(), ((PlacedFeature)this.featureFalse.get()).getFeatures());
   }
}
