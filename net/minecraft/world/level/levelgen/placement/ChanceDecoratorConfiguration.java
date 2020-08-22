package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class ChanceDecoratorConfiguration implements DecoratorConfiguration {
   public final int chance;

   public ChanceDecoratorConfiguration(int var1) {
      this.chance = var1;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("chance"), var1.createInt(this.chance))));
   }

   public static ChanceDecoratorConfiguration deserialize(Dynamic var0) {
      int var1 = var0.get("chance").asInt(0);
      return new ChanceDecoratorConfiguration(var1);
   }
}
