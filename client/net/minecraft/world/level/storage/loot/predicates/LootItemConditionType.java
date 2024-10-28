package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;

public record LootItemConditionType(MapCodec<? extends LootItemCondition> codec) {
   public LootItemConditionType(MapCodec<? extends LootItemCondition> codec) {
      super();
      this.codec = codec;
   }

   public MapCodec<? extends LootItemCondition> codec() {
      return this.codec;
   }
}
