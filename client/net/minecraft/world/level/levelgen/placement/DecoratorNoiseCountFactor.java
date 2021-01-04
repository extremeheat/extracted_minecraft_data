package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorNoiseCountFactor implements DecoratorConfiguration {
   public final int noiseToCountRatio;
   public final double noiseFactor;
   public final double noiseOffset;
   public final Heightmap.Types heightmap;

   public DecoratorNoiseCountFactor(int var1, double var2, double var4, Heightmap.Types var6) {
      super();
      this.noiseToCountRatio = var1;
      this.noiseFactor = var2;
      this.noiseOffset = var4;
      this.heightmap = var6;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("noise_to_count_ratio"), var1.createInt(this.noiseToCountRatio), var1.createString("noise_factor"), var1.createDouble(this.noiseFactor), var1.createString("noise_offset"), var1.createDouble(this.noiseOffset), var1.createString("heightmap"), var1.createString(this.heightmap.getSerializationKey()))));
   }

   public static DecoratorNoiseCountFactor deserialize(Dynamic<?> var0) {
      int var1 = var0.get("noise_to_count_ratio").asInt(10);
      double var2 = var0.get("noise_factor").asDouble(80.0D);
      double var4 = var0.get("noise_offset").asDouble(0.0D);
      Heightmap.Types var6 = Heightmap.Types.getFromKey(var0.get("heightmap").asString("OCEAN_FLOOR_WG"));
      return new DecoratorNoiseCountFactor(var1, var2, var4, var6);
   }
}
