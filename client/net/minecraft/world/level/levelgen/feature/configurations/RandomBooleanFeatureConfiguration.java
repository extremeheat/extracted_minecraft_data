package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public class RandomBooleanFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<RandomBooleanFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ConfiguredFeature.CODEC.fieldOf("feature_true").forGetter((var0x) -> {
         return var0x.featureTrue;
      }), ConfiguredFeature.CODEC.fieldOf("feature_false").forGetter((var0x) -> {
         return var0x.featureFalse;
      })).apply(var0, RandomBooleanFeatureConfiguration::new);
   });
   public final Supplier<ConfiguredFeature<?, ?>> featureTrue;
   public final Supplier<ConfiguredFeature<?, ?>> featureFalse;

   public RandomBooleanFeatureConfiguration(Supplier<ConfiguredFeature<?, ?>> var1, Supplier<ConfiguredFeature<?, ?>> var2) {
      super();
      this.featureTrue = var1;
      this.featureFalse = var2;
   }

   public Stream<ConfiguredFeature<?, ?>> getFeatures() {
      return Stream.concat(((ConfiguredFeature)this.featureTrue.get()).getFeatures(), ((ConfiguredFeature)this.featureFalse.get()).getFeatures());
   }
}
