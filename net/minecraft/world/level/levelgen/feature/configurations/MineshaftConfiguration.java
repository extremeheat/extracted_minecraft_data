package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;

public class MineshaftConfiguration implements FeatureConfiguration {
   public final double probability;
   public final MineshaftFeature.Type type;

   public MineshaftConfiguration(double var1, MineshaftFeature.Type var3) {
      this.probability = var1;
      this.type = var3;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("probability"), var1.createDouble(this.probability), var1.createString("type"), var1.createString(this.type.getName()))));
   }

   public static MineshaftConfiguration deserialize(Dynamic var0) {
      float var1 = var0.get("probability").asFloat(0.0F);
      MineshaftFeature.Type var2 = MineshaftFeature.Type.byName(var0.get("type").asString(""));
      return new MineshaftConfiguration((double)var1, var2);
   }
}
