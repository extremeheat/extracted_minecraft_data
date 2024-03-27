package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;

public record LootNumberProviderType(MapCodec<? extends NumberProvider> a) {
   private final MapCodec<? extends NumberProvider> codec;

   public LootNumberProviderType(MapCodec<? extends NumberProvider> var1) {
      super();
      this.codec = var1;
   }
}
