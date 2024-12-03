package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record UseRemainder(ItemStack convertInto) {
   public static final Codec<UseRemainder> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, UseRemainder> STREAM_CODEC;

   public UseRemainder(ItemStack var1) {
      super();
      this.convertInto = var1;
   }

   public ItemStack convertIntoRemainder(ItemStack var1, int var2, boolean var3, OnExtraCreatedRemainder var4) {
      if (var3) {
         return var1;
      } else if (var1.getCount() >= var2) {
         return var1;
      } else {
         ItemStack var5 = this.convertInto.copy();
         if (var1.isEmpty()) {
            return var5;
         } else {
            var4.apply(var5);
            return var1;
         }
      }
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         UseRemainder var2 = (UseRemainder)var1;
         return ItemStack.matches(this.convertInto, var2.convertInto);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return ItemStack.hashItemAndComponents(this.convertInto);
   }

   static {
      CODEC = ItemStack.CODEC.xmap(UseRemainder::new, UseRemainder::convertInto);
      STREAM_CODEC = StreamCodec.composite(ItemStack.STREAM_CODEC, UseRemainder::convertInto, UseRemainder::new);
   }

   @FunctionalInterface
   public interface OnExtraCreatedRemainder {
      void apply(ItemStack var1);
   }
}
