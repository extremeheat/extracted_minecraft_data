package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ProbabilityFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<ProbabilityFeatureConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((var0x) -> {
         return var0x.probability;
      })).apply(var0, ProbabilityFeatureConfiguration::new);
   });
   public final float probability;

   public ProbabilityFeatureConfiguration(float var1) {
      super();
      this.probability = var1;
   }
}
