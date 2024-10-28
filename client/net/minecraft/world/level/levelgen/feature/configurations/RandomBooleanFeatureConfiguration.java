package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
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
   public final Holder<PlacedFeature> featureTrue;
   public final Holder<PlacedFeature> featureFalse;

   public RandomBooleanFeatureConfiguration(Holder<PlacedFeature> var1, Holder<PlacedFeature> var2) {
      super();
      this.featureTrue = var1;
      this.featureFalse = var2;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(((PlacedFeature)this.featureTrue.value()).getFeatures(), ((PlacedFeature)this.featureFalse.value()).getFeatures());
   }
}
