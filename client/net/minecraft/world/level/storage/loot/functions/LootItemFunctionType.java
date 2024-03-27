package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;

public record LootItemFunctionType(MapCodec<? extends LootItemFunction> a) {
   private final MapCodec<? extends LootItemFunction> codec;

   public LootItemFunctionType(MapCodec<? extends LootItemFunction> var1) {
      super();
      this.codec = var1;
   }
}
