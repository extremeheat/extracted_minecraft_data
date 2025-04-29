package net.minecraft.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public record ItemStackWithSlot(int slot, ItemStack stack) {
   public static final Codec<ItemStackWithSlot> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.UNSIGNED_BYTE.fieldOf("Slot").orElse(0).forGetter(ItemStackWithSlot::slot), ItemStack.MAP_CODEC.forGetter(ItemStackWithSlot::stack)).apply(var0, ItemStackWithSlot::new));

   public ItemStackWithSlot(int var1, ItemStack var2) {
      super();
      this.slot = var1;
      this.stack = var2;
   }

   public boolean isValidInContainer(int var1) {
      return this.slot >= 0 && this.slot < var1;
   }
}
