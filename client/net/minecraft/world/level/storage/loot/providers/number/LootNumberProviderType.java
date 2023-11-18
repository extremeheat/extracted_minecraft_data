package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;

public record LootNumberProviderType(Codec<? extends NumberProvider> a) {
   private final Codec<? extends NumberProvider> codec;

   public LootNumberProviderType(Codec<? extends NumberProvider> var1) {
      super();
      this.codec = var1;
   }
}
