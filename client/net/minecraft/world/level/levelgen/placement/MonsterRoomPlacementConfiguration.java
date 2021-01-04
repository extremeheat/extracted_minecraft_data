package net.minecraft.world.level.levelgen.placement;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.world.level.levelgen.feature.DecoratorConfiguration;

public class MonsterRoomPlacementConfiguration implements DecoratorConfiguration {
   public final int chance;

   public MonsterRoomPlacementConfiguration(int var1) {
      super();
      this.chance = var1;
   }

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("chance"), var1.createInt(this.chance))));
   }

   public static MonsterRoomPlacementConfiguration deserialize(Dynamic<?> var0) {
      int var1 = var0.get("chance").asInt(0);
      return new MonsterRoomPlacementConfiguration(var1);
   }
}
