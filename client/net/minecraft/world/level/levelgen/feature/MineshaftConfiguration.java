package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class MineshaftConfiguration implements FeatureConfiguration {
   public final double probability;
   public final MineshaftFeature.Type type;

   public MineshaftConfiguration(double var1, MineshaftFeature.Type var3) {
      super();
      this.probability = var1;
      this.type = var3;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("probability"), var1.createDouble(this.probability), var1.createString("type"), var1.createString(this.type.getName()))));
   }

   public static <T> MineshaftConfiguration deserialize(Dynamic<T> var0) {
      float var1 = var0.get("probability").asFloat(0.0F);
      MineshaftFeature.Type var2 = MineshaftFeature.Type.byName(var0.get("type").asString(""));
      return new MineshaftConfiguration((double)var1, var2);
   }
}
