package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.Codec;

public record LootPoolEntryType(Codec<? extends LootPoolEntryContainer> a) {
   private final Codec<? extends LootPoolEntryContainer> codec;

   public LootPoolEntryType(Codec<? extends LootPoolEntryContainer> var1) {
      super();
      this.codec = var1;
   }
}
