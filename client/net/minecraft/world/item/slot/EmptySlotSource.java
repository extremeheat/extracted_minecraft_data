package net.minecraft.world.item.slot;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;

public record EmptySlotSource() implements SlotSource {
   public static final MapCodec<EmptySlotSource> MAP_CODEC = MapCodec.unit(new EmptySlotSource());

   public EmptySlotSource() {
      super();
   }

   public MapCodec<EmptySlotSource> codec() {
      return MAP_CODEC;
   }

   public SlotCollection provide(LootContext var1) {
      return SlotCollection.EMPTY;
   }
}
