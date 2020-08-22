package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoiseDependantDecoratorConfiguration implements DecoratorConfiguration {
   public final double noiseLevel;
   public final int belowNoise;
   public final int aboveNoise;

   public NoiseDependantDecoratorConfiguration(double var1, int var3, int var4) {
      this.noiseLevel = var1;
      this.belowNoise = var3;
      this.aboveNoise = var4;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("noise_level"), var1.createDouble(this.noiseLevel), var1.createString("below_noise"), var1.createInt(this.belowNoise), var1.createString("above_noise"), var1.createInt(this.aboveNoise))));
   }

   public static NoiseDependantDecoratorConfiguration deserialize(Dynamic var0) {
      double var1 = var0.get("noise_level").asDouble(0.0D);
      int var3 = var0.get("below_noise").asInt(0);
      int var4 = var0.get("above_noise").asInt(0);
      return new NoiseDependantDecoratorConfiguration(var1, var3, var4);
   }
}
