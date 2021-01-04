package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class DecoratorRange implements DecoratorConfiguration {
   public final int min;
   public final int max;

   public DecoratorRange(int var1, int var2) {
      super();
      this.min = var1;
      this.max = var2;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("min"), var1.createInt(this.min), var1.createString("max"), var1.createInt(this.max))));
   }

   public static DecoratorRange deserialize(Dynamic<?> var0) {
      int var1 = var0.get("min").asInt(0);
      int var2 = var0.get("max").asInt(0);
      return new DecoratorRange(var1, var2);
   }
}
