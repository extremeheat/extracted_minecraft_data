package net.minecraft.network;

import com.mojang.datafixers.DataFixUtils;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface HashedStack {
   HashedStack EMPTY = new HashedStack() {
      public String toString() {
         return "<empty>";
      }

      public boolean matches(final ItemStack stack, final HashedPatchMap.HashGenerator hasher) {
         return stack.isEmpty();
      }
   };
   StreamCodec<RegistryFriendlyByteBuf, HashedStack> STREAM_CODEC = ByteBufCodecs.optional(HashedStack.ActualItem.STREAM_CODEC).map((actualItem) -> (HashedStack)DataFixUtils.orElse(actualItem, EMPTY), (hashedStack) -> {
      Optional var10000;
      if (hashedStack instanceof ActualItem actualItem) {
         var10000 = Optional.of(actualItem);
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   });

   boolean matches(ItemStack stack, HashedPatchMap.HashGenerator hasher);

   static HashedStack create(final ItemStack itemStack, final HashedPatchMap.HashGenerator hasher) {
      return (HashedStack)(itemStack.isEmpty() ? EMPTY : new ActualItem(itemStack.typeHolder(), itemStack.getCount(), HashedPatchMap.create(itemStack.getComponentsPatch(), hasher)));
   }

   public static record ActualItem(Holder<Item> item, int count, HashedPatchMap components) implements HashedStack {
      public static final StreamCodec<RegistryFriendlyByteBuf, ActualItem> STREAM_CODEC;

      public ActualItem {
         super();
      }

      public boolean matches(final ItemStack itemStack, final HashedPatchMap.HashGenerator hasher) {
         if (this.count != itemStack.getCount()) {
            return false;
         } else {
            return !this.item.equals(itemStack.typeHolder()) ? false : this.components.matches(itemStack.getComponentsPatch(), hasher);
         }
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.ITEM), ActualItem::item, ByteBufCodecs.VAR_INT, ActualItem::count, HashedPatchMap.STREAM_CODEC, ActualItem::components, ActualItem::new);
      }
   }
}
