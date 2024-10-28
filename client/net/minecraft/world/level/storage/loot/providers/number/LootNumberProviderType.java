package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;

public record LootNumberProviderType(MapCodec<? extends NumberProvider> codec) {
   public LootNumberProviderType(MapCodec<? extends NumberProvider> codec) {
      super();
      this.codec = codec;
   }

   public MapCodec<? extends NumberProvider> codec() {
      return this.codec;
   }
}
