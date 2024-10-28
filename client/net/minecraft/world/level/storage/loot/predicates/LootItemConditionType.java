package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;

public record LootItemConditionType(MapCodec<? extends LootItemCondition> codec) {
   public LootItemConditionType(MapCodec<? extends LootItemCondition> var1) {
      super();
      this.codec = var1;
   }

   public MapCodec<? extends LootItemCondition> codec() {
      return this.codec;
   }
}
