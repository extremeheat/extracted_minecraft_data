package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NoneFeatureConfiguration implements FeatureConfiguration {
   public NoneFeatureConfiguration() {
      super();
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.emptyMap());
   }

   public static <T> NoneFeatureConfiguration deserialize(Dynamic<T> var0) {
      return FeatureConfiguration.NONE;
   }
}
