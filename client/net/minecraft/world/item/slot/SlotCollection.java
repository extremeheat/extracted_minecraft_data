package net.minecraft.world.item.slot;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public interface SlotCollection {
   SlotCollection EMPTY = Stream::empty;

   Stream<ItemStack> itemCopies();

   default SlotCollection filter(Predicate<ItemStack> var1) {
      return new Filtered(this, var1);
   }

   default SlotCollection flatMap(Function<ItemStack, ? extends SlotCollection> var1) {
      return new FlatMapped(this, var1);
   }

   default SlotCollection limit(int var1) {
      return new Limited(this, var1);
   }

   static SlotCollection of(SlotAccess var0) {
      return () -> Stream.of(var0.get().copy());
   }

   static SlotCollection of(Collection<? extends SlotAccess> var0) {
      SlotCollection var10000;
      switch (var0.size()) {
         case 0 -> var10000 = EMPTY;
         case 1 -> var10000 = of((SlotAccess)var0.iterator().next());
         default -> var10000 = () -> var0.stream().map(SlotAccess::get).map(ItemStack::copy);
      }

      return var10000;
   }

   static SlotCollection concat(SlotCollection var0, SlotCollection var1) {
      return () -> Stream.concat(var0.itemCopies(), var1.itemCopies());
   }

   static SlotCollection concat(List<? extends SlotCollection> var0) {
      SlotCollection var10000;
      switch (var0.size()) {
         case 0 -> var10000 = EMPTY;
         case 1 -> var10000 = (SlotCollection)var0.getFirst();
         case 2 -> var10000 = concat((SlotCollection)var0.get(0), (SlotCollection)var0.get(1));
         default -> var10000 = () -> var0.stream().flatMap(SlotCollection::itemCopies);
      }

      return var10000;
   }

   public static record Filtered(SlotCollection slots, Predicate<ItemStack> filter) implements SlotCollection {
      public Filtered(SlotCollection var1, Predicate<ItemStack> var2) {
         super();
         this.slots = var1;
         this.filter = var2;
      }

      public Stream<ItemStack> itemCopies() {
         return this.slots.itemCopies().filter(this.filter);
      }

      public SlotCollection filter(Predicate<ItemStack> var1) {
         return new Filtered(this.slots, this.filter.and(var1));
      }
   }

   public static record FlatMapped(SlotCollection slots, Function<ItemStack, ? extends SlotCollection> mapper) implements SlotCollection {
      public FlatMapped(SlotCollection var1, Function<ItemStack, ? extends SlotCollection> var2) {
         super();
         this.slots = var1;
         this.mapper = var2;
      }

      public Stream<ItemStack> itemCopies() {
         return this.slots.itemCopies().map(this.mapper).flatMap(SlotCollection::itemCopies);
      }
   }

   public static record Limited(SlotCollection slots, int limit) implements SlotCollection {
      public Limited(SlotCollection var1, int var2) {
         super();
         this.slots = var1;
         this.limit = var2;
      }

      public Stream<ItemStack> itemCopies() {
         return this.slots.itemCopies().limit((long)this.limit);
      }

      public SlotCollection limit(int var1) {
         return new Limited(this.slots, Math.min(this.limit, var1));
      }
   }
}
