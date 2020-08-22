package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;

public class RangeDecoratorConfiguration implements DecoratorConfiguration {
   public final int min;
   public final int max;

   public RangeDecoratorConfiguration(int var1, int var2) {
      this.min = var1;
      this.max = var2;
   }

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("min"), var1.createInt(this.min), var1.createString("max"), var1.createInt(this.max))));
   }

   public static RangeDecoratorConfiguration deserialize(Dynamic var0) {
      int var1 = var0.get("min").asInt(0);
      int var2 = var0.get("max").asInt(0);
      return new RangeDecoratorConfiguration(var1, var2);
   }
}
