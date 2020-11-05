package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class NoiseCountFactorDecoratorConfiguration implements DecoratorConfiguration {
   public static final Codec<NoiseCountFactorDecoratorConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.INT.fieldOf("noise_to_count_ratio").forGetter((var0x) -> {
         return var0x.noiseToCountRatio;
      }), Codec.DOUBLE.fieldOf("noise_factor").forGetter((var0x) -> {
         return var0x.noiseFactor;
      }), Codec.DOUBLE.fieldOf("noise_offset").orElse(0.0D).forGetter((var0x) -> {
         return var0x.noiseOffset;
      })).apply(var0, NoiseCountFactorDecoratorConfiguration::new);
   });
   public final int noiseToCountRatio;
   public final double noiseFactor;
   public final double noiseOffset;

   public NoiseCountFactorDecoratorConfiguration(int var1, double var2, double var4) {
      super();
      this.noiseToCountRatio = var1;
      this.noiseFactor = var2;
      this.noiseOffset = var4;
   }
}
