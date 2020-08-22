package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ShipwreckConfiguration implements FeatureConfiguration {
   public final boolean isBeached;

   public ShipwreckConfiguration(boolean var1) {
      this.isBeached = var1;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("is_beached"), var1.createBoolean(this.isBeached))));
   }

   public static ShipwreckConfiguration deserialize(Dynamic var0) {
      boolean var1 = var0.get("is_beached").asBoolean(false);
      return new ShipwreckConfiguration(var1);
   }
}
