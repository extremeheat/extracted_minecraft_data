package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class NoiseDependantDecoratorConfiguration implements DecoratorConfiguration {
   public static final Codec<NoiseDependantDecoratorConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.DOUBLE.fieldOf("noise_level").forGetter((var0x) -> {
         return var0x.noiseLevel;
      }), Codec.INT.fieldOf("below_noise").forGetter((var0x) -> {
         return var0x.belowNoise;
      }), Codec.INT.fieldOf("above_noise").forGetter((var0x) -> {
         return var0x.aboveNoise;
      })).apply(var0, NoiseDependantDecoratorConfiguration::new);
   });
   public final double noiseLevel;
   public final int belowNoise;
   public final int aboveNoise;

   public NoiseDependantDecoratorConfiguration(double var1, int var3, int var4) {
      super();
      this.noiseLevel = var1;
      this.belowNoise = var3;
      this.aboveNoise = var4;
   }
}
