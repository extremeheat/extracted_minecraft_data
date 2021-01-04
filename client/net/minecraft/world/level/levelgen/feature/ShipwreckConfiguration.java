package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ShipwreckConfiguration implements FeatureConfiguration {
   public final boolean isBeached;

   public ShipwreckConfiguration(boolean var1) {
      super();
      this.isBeached = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("is_beached"), var1.createBoolean(this.isBeached))));
   }

   public static <T> ShipwreckConfiguration deserialize(Dynamic<T> var0) {
      boolean var1 = var0.get("is_beached").asBoolean(false);
      return new ShipwreckConfiguration(var1);
   }
}
