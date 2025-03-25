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

      public boolean matches(ItemStack var1, HashedPatchMap.HashGenerator var2) {
         return var1.isEmpty();
      }
   };
   StreamCodec<RegistryFriendlyByteBuf, HashedStack> STREAM_CODEC = ByteBufCodecs.optional(HashedStack.ActualItem.STREAM_CODEC).map((var0) -> (HashedStack)DataFixUtils.orElse(var0, EMPTY), (var0) -> {
      Optional var10000;
      if (var0 instanceof ActualItem var1) {
         var10000 = Optional.of(var1);
      } else {
         var10000 = Optional.empty();
      }

      return var10000;
   });

   boolean matches(ItemStack var1, HashedPatchMap.HashGenerator var2);

   static HashedStack create(ItemStack var0, HashedPatchMap.HashGenerator var1) {
      return (HashedStack)(var0.isEmpty() ? EMPTY : new ActualItem(var0.getItemHolder(), var0.getCount(), HashedPatchMap.create(var0.getComponentsPatch(), var1)));
   }

   public static record ActualItem(Holder<Item> item, int count, HashedPatchMap components) implements HashedStack {
      public static final StreamCodec<RegistryFriendlyByteBuf, ActualItem> STREAM_CODEC;

      public ActualItem(Holder<Item> var1, int var2, HashedPatchMap var3) {
         super();
         this.item = var1;
         this.count = var2;
         this.components = var3;
      }

      public boolean matches(ItemStack var1, HashedPatchMap.HashGenerator var2) {
         if (this.count != var1.getCount()) {
            return false;
         } else {
            return !this.item.equals(var1.getItemHolder()) ? false : this.components.matches(var1.getComponentsPatch(), var2);
         }
      }

      static {
         STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.ITEM), ActualItem::item, ByteBufCodecs.VAR_INT, ActualItem::count, HashedPatchMap.STREAM_CODEC, ActualItem::components, ActualItem::new);
      }
   }
}
