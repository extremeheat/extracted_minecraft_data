package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ChargedProjectiles {
   public static final ChargedProjectiles EMPTY = new ChargedProjectiles(List.of());
   public static final Codec<ChargedProjectiles> CODEC = ItemStack.CODEC.listOf().xmap(ChargedProjectiles::new, var0 -> var0.items);
   public static final StreamCodec<RegistryFriendlyByteBuf, ChargedProjectiles> STREAM_CODEC = ItemStack.STREAM_CODEC
      .apply(ByteBufCodecs.list())
      .map(ChargedProjectiles::new, var0 -> var0.items);
   private final List<ItemStack> items;

   private ChargedProjectiles(List<ItemStack> var1) {
      super();
      this.items = (List<ItemStack>)var1;
   }

   public static ChargedProjectiles of(ItemStack var0) {
      return new ChargedProjectiles(List.of(var0.copy()));
   }

   public static ChargedProjectiles of(List<ItemStack> var0) {
      return new ChargedProjectiles(Lists.transform(var0, ItemStack::copy));
   }

   public boolean contains(Item var1) {
      for (ItemStack var3 : this.items) {
         if (var3.is(var1)) {
            return true;
         }
      }

      return false;
   }

   public List<ItemStack> getItems() {
      return Lists.transform(this.items, ItemStack::copy);
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof ChargedProjectiles var2 && ItemStack.listMatches(this.items, var2.items)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return ItemStack.hashStackList(this.items);
   }

   @Override
   public String toString() {
      return "ChargedProjectiles[items=" + this.items + "]";
   }
}
