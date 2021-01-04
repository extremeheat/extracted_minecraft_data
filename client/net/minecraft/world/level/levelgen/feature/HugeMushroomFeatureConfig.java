package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HugeMushroomFeatureConfig implements FeatureConfiguration {
   public final boolean planted;

   public HugeMushroomFeatureConfig(boolean var1) {
      super();
      this.planted = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("planted"), var1.createBoolean(this.planted))));
   }

   public static <T> HugeMushroomFeatureConfig deserialize(Dynamic<T> var0) {
      boolean var1 = var0.get("planted").asBoolean(false);
      return new HugeMushroomFeatureConfig(var1);
   }
}
