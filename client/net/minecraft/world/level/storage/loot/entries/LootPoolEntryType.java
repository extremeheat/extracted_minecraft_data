package net.minecraft.world.level.storage.loot.entries;

import com.mojang.serialization.MapCodec;

public record LootPoolEntryType(MapCodec<? extends LootPoolEntryContainer> codec) {
   public LootPoolEntryType(MapCodec<? extends LootPoolEntryContainer> codec) {
      super();
      this.codec = codec;
   }
}
