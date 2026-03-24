package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record UseRemainder(ItemStackTemplate convertInto) {
   public static final Codec<UseRemainder> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, UseRemainder> STREAM_CODEC;

   public UseRemainder {
      super();
   }

   public ItemStack convertIntoRemainder(final ItemStack usedStack, final int stackCountBeforeUsing, final boolean hasInfiniteMaterials, final OnExtraCreatedRemainder onExtraCreatedRemainder) {
      if (hasInfiniteMaterials) {
         return usedStack;
      } else if (usedStack.getCount() >= stackCountBeforeUsing) {
         return usedStack;
      } else {
         ItemStack remainderStack = this.convertInto.create();
         if (usedStack.isEmpty()) {
            return remainderStack;
         } else {
            onExtraCreatedRemainder.apply(remainderStack);
            return usedStack;
         }
      }
   }

   static {
      CODEC = ItemStackTemplate.CODEC.xmap(UseRemainder::new, UseRemainder::convertInto);
      STREAM_CODEC = StreamCodec.composite(ItemStackTemplate.STREAM_CODEC, UseRemainder::convertInto, UseRemainder::new);
   }

   @FunctionalInterface
   public interface OnExtraCreatedRemainder {
      void apply(final ItemStack extraCreatedRemainder);
   }
}
