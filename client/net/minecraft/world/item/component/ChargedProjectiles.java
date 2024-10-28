package net.minecraft.world.item.component;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ChargedProjectiles {
   public static final ChargedProjectiles EMPTY = new ChargedProjectiles(List.of());
   public static final Codec<ChargedProjectiles> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ChargedProjectiles> STREAM_CODEC;
   private final List<ItemStack> items;

   private ChargedProjectiles(List<ItemStack> var1) {
      super();
      this.items = var1;
   }

   public static ChargedProjectiles of(ItemStack var0) {
      return new ChargedProjectiles(List.of(var0.copy()));
   }

   public static ChargedProjectiles of(List<ItemStack> var0) {
      return new ChargedProjectiles(List.copyOf(Lists.transform(var0, ItemStack::copy)));
   }

   public boolean contains(Item var1) {
      Iterator var2 = this.items.iterator();

      ItemStack var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (ItemStack)var2.next();
      } while(!var3.is(var1));

      return true;
   }

   public List<ItemStack> getItems() {
      return Lists.transform(this.items, ItemStack::copy);
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof ChargedProjectiles) {
            ChargedProjectiles var2 = (ChargedProjectiles)var1;
            if (ItemStack.listMatches(this.items, var2.items)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return ItemStack.hashStackList(this.items);
   }

   public String toString() {
      return "ChargedProjectiles[items=" + String.valueOf(this.items) + "]";
   }

   static {
      CODEC = ItemStack.CODEC.listOf().xmap(ChargedProjectiles::new, (var0) -> {
         return var0.items;
      });
      STREAM_CODEC = ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()).map(ChargedProjectiles::new, (var0) -> {
         return var0.items;
      });
   }
}
