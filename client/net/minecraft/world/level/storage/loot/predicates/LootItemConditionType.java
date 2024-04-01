package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.Codec;

public record LootItemConditionType(Codec<? extends LootItemCondition> a) {
   private final Codec<? extends LootItemCondition> codec;

   public LootItemConditionType(Codec<? extends LootItemCondition> var1) {
      super();
      this.codec = var1;
   }
}
