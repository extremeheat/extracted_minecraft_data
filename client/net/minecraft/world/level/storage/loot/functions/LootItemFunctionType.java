package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;

public record LootItemFunctionType<T extends LootItemFunction>(MapCodec<T> codec) {
   public LootItemFunctionType(MapCodec<T> var1) {
      super();
      this.codec = var1;
   }

   public MapCodec<T> codec() {
      return this.codec;
   }
}
