package net.minecraft.world.level.storage.loot.providers.nbt;

import com.mojang.serialization.Codec;

public record LootNbtProviderType(Codec<? extends NbtProvider> a) {
   private final Codec<? extends NbtProvider> codec;

   public LootNbtProviderType(Codec<? extends NbtProvider> var1) {
      super();
      this.codec = var1;
   }
}
