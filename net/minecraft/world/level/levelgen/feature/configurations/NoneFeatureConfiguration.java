package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoneFeatureConfiguration implements FeatureConfiguration {
   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.emptyMap());
   }

   public static NoneFeatureConfiguration deserialize(Dynamic var0) {
      return NONE;
   }
}
