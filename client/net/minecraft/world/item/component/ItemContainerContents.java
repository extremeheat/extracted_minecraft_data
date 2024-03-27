package net.minecraft.world.item.component;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class ItemContainerContents implements Iterable<ItemStack> {
   private static final int MAX_SIZE = 256;
   public static final ItemContainerContents EMPTY = new ItemContainerContents(NonNullList.create());
   public static final Codec<ItemContainerContents> CODEC = ItemContainerContents.Slot.CODEC
      .sizeLimitedListOf(256)
      .xmap(ItemContainerContents::fromSlots, ItemContainerContents::asSlots);
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainerContents> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
      .<NonNullList<ItemStack>>apply(ByteBufCodecs.list(256))
      .map(ItemContainerContents::new, var0 -> var0.items);
   private final NonNullList<ItemStack> items;

   private ItemContainerContents(NonNullList<ItemStack> var1) {
      super();
      this.items = var1;
   }

   private ItemContainerContents(int var1) {
      this(NonNullList.withSize(var1, ItemStack.EMPTY));
   }

   private ItemContainerContents(List<ItemStack> var1) {
      this(var1.size());

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.items.set(var2, var1.get(var2));
      }
   }

   private static ItemContainerContents fromSlots(List<ItemContainerContents.Slot> var0) {
      int var1 = var0.stream().mapToInt(ItemContainerContents.Slot::index).max().orElse(-1);
      ItemContainerContents var2 = new ItemContainerContents(var1 + 1);

      for(ItemContainerContents.Slot var4 : var0) {
         var2.items.set(var4.index(), var4.item());
      }

      return var2;
   }

   public static ItemContainerContents copyOf(List<ItemStack> var0) {
      int var1 = getFilledSize(var0);
      if (var1 == 0) {
         return EMPTY;
      } else {
         ItemContainerContents var2 = new ItemContainerContents(var1);

         for(int var3 = 0; var3 < var1; ++var3) {
            var2.items.set(var3, ((ItemStack)var0.get(var3)).copy());
         }

         return var2;
      }
   }

   private static int getFilledSize(List<ItemStack> var0) {
      for(int var1 = var0.size() - 1; var1 >= 0; --var1) {
         if (!((ItemStack)var0.get(var1)).isEmpty()) {
            return var1 + 1;
         }
      }

      return 0;
   }

   private List<ItemContainerContents.Slot> asSlots() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         ItemStack var3 = this.items.get(var2);
         if (!var3.isEmpty()) {
            var1.add(new ItemContainerContents.Slot(var2, var3));
         }
      }

      return var1;
   }

   public void copyInto(NonNullList<ItemStack> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         ItemStack var3 = var2 < this.items.size() ? this.items.get(var2) : ItemStack.EMPTY;
         var1.set(var2, var3.copy());
      }
   }

   public ItemStack copyOne() {
      return this.items.isEmpty() ? ItemStack.EMPTY : this.items.get(0).copy();
   }

   public Stream<ItemStack> stream() {
      return this.items.stream().filter(var0 -> !var0.isEmpty()).map(ItemStack::copy);
   }

   @Override
   public Iterator<ItemStack> iterator() {
      return Iterators.transform(Iterators.filter(this.items.iterator(), var0 -> !var0.isEmpty()), ItemStack::copy);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof ItemContainerContents var2 && ItemStack.listMatches(this.items, var2.items)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return ItemStack.hashStackList(this.items);
   }

   static record Slot(int b, ItemStack c) {
      private final int index;
      private final ItemStack item;
      public static final Codec<ItemContainerContents.Slot> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  Codec.intRange(0, 255).fieldOf("slot").forGetter(ItemContainerContents.Slot::index),
                  ItemStack.CODEC.fieldOf("item").forGetter(ItemContainerContents.Slot::item)
               )
               .apply(var0, ItemContainerContents.Slot::new)
      );

      Slot(int var1, ItemStack var2) {
         super();
         this.index = var1;
         this.item = var2;
      }
   }
}
