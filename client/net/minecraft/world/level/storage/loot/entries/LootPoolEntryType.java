package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;

public record LootPoolEntryType(MapCodec<? extends LootPoolEntryContainer> a) {
   private final MapCodec<? extends LootPoolEntryContainer> codec;

   public LootPoolEntryType(MapCodec<? extends LootPoolEntryContainer> var1) {
      super();
      this.codec = var1;
   }
}
