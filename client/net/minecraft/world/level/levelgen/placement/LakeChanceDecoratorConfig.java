package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class LakeChanceDecoratorConfig implements DecoratorConfiguration {
   public final int chance;

   public LakeChanceDecoratorConfig(int var1) {
      super();
      this.chance = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("chance"), var1.createInt(this.chance))));
   }

   public static LakeChanceDecoratorConfig deserialize(Dynamic<?> var0) {
      int var1 = var0.get("chance").asInt(0);
      return new LakeChanceDecoratorConfig(var1);
   }
}
