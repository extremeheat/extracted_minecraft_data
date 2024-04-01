package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;

public record LootItemFunctionType(Codec<? extends LootItemFunction> a) {
   private final Codec<? extends LootItemFunction> codec;

   public LootItemFunctionType(Codec<? extends LootItemFunction> var1) {
      super();
      this.codec = var1;
   }
}
