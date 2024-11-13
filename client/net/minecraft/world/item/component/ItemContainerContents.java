package net.minecraft.world.item.component;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public final class ItemContainerContents {
   private static final int NO_SLOT = -1;
   private static final int MAX_SIZE = 256;
   public static final ItemContainerContents EMPTY = new ItemContainerContents(NonNullList.create());
   public static final Codec<ItemContainerContents> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainerContents> STREAM_CODEC;
   private final NonNullList<ItemStack> items;
   private final int hashCode;

   private ItemContainerContents(NonNullList<ItemStack> var1) {
      super();
      if (var1.size() > 256) {
         throw new IllegalArgumentException("Got " + var1.size() + " items, but maximum is 256");
      } else {
         this.items = var1;
         this.hashCode = ItemStack.hashStackList(var1);
      }
   }

   private ItemContainerContents(int var1) {
      this(NonNullList.withSize(var1, ItemStack.EMPTY));
   }

   private ItemContainerContents(List<ItemStack> var1) {
      this(var1.size());

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.items.set(var2, (ItemStack)var1.get(var2));
      }

   }

   private static ItemContainerContents fromSlots(List<Slot> var0) {
      OptionalInt var1 = var0.stream().mapToInt(Slot::index).max();
      if (var1.isEmpty()) {
         return EMPTY;
      } else {
         ItemContainerContents var2 = new ItemContainerContents(var1.getAsInt() + 1);

         for(Slot var4 : var0) {
            var2.items.set(var4.index(), var4.item());
         }

         return var2;
      }
   }

   public static ItemContainerContents fromItems(List<ItemStack> var0) {
      int var1 = findLastNonEmptySlot(var0);
      if (var1 == -1) {
         return EMPTY;
      } else {
         ItemContainerContents var2 = new ItemContainerContents(var1 + 1);

         for(int var3 = 0; var3 <= var1; ++var3) {
            var2.items.set(var3, ((ItemStack)var0.get(var3)).copy());
         }

         return var2;
      }
   }

   private static int findLastNonEmptySlot(List<ItemStack> var0) {
      for(int var1 = var0.size() - 1; var1 >= 0; --var1) {
         if (!((ItemStack)var0.get(var1)).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   private List<Slot> asSlots() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         ItemStack var3 = this.items.get(var2);
         if (!var3.isEmpty()) {
            var1.add(new Slot(var2, var3));
         }
      }

      return var1;
   }

   public void copyInto(NonNullList<ItemStack> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         ItemStack var3 = var2 < this.items.size() ? (ItemStack)this.items.get(var2) : ItemStack.EMPTY;
         var1.set(var2, var3.copy());
      }

   }

   public ItemStack copyOne() {
      return this.items.isEmpty() ? ItemStack.EMPTY : ((ItemStack)this.items.get(0)).copy();
   }

   public Stream<ItemStack> stream() {
      return this.items.stream().map(ItemStack::copy);
   }

   public Stream<ItemStack> nonEmptyStream() {
      return this.items.stream().filter((var0) -> !var0.isEmpty()).map(ItemStack::copy);
   }

   public Iterable<ItemStack> nonEmptyItems() {
      return Iterables.filter(this.items, (var0) -> !var0.isEmpty());
   }

   public Iterable<ItemStack> nonEmptyItemsCopy() {
      return Iterables.transform(this.nonEmptyItems(), ItemStack::copy);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof ItemContainerContents) {
            ItemContainerContents var2 = (ItemContainerContents)var1;
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
      return this.hashCode;
   }

   static {
      CODEC = ItemContainerContents.Slot.CODEC.sizeLimitedListOf(256).xmap(ItemContainerContents::fromSlots, ItemContainerContents::asSlots);
      STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)).map(ItemContainerContents::new, (var0) -> var0.items);
   }

   static record Slot(int index, ItemStack item) {
      public static final Codec<Slot> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index), ItemStack.CODEC.fieldOf("item").forGetter(Slot::item)).apply(var0, Slot::new));

      Slot(int var1, ItemStack var2) {
         super();
         this.index = var1;
         this.item = var2;
      }
   }
}
