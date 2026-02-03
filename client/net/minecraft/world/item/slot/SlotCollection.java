package net.minecraft.world.item.slot;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;

public interface SlotCollection {
   SlotCollection EMPTY = Stream::empty;

   Stream<ItemStack> itemCopies();

   default SlotCollection filter(final Predicate<? super ItemStack> predicate) {
      return new Filtered(this, predicate);
   }

   default SlotCollection flatMap(final Function<ItemStack, ? extends SlotCollection> mapper) {
      return new FlatMapped(this, mapper);
   }

   default SlotCollection limit(final int limit) {
      return new Limited(this, limit);
   }

   static SlotCollection of(final SlotAccess slotAccess) {
      return () -> Stream.of(slotAccess.get().copy());
   }

   static SlotCollection of(final Collection<? extends SlotAccess> slots) {
      SlotCollection var10000;
      switch (slots.size()) {
         case 0 -> var10000 = EMPTY;
         case 1 -> var10000 = of((SlotAccess)slots.iterator().next());
         default -> var10000 = () -> slots.stream().map(SlotAccess::get).map(ItemStack::copy);
      }

      return var10000;
   }

   static SlotCollection concat(final SlotCollection first, final SlotCollection second) {
      return () -> Stream.concat(first.itemCopies(), second.itemCopies());
   }

   static SlotCollection concat(final List<? extends SlotCollection> terms) {
      SlotCollection var10000;
      switch (terms.size()) {
         case 0 -> var10000 = EMPTY;
         case 1 -> var10000 = (SlotCollection)terms.getFirst();
         case 2 -> var10000 = concat((SlotCollection)terms.get(0), (SlotCollection)terms.get(1));
         default -> var10000 = () -> terms.stream().flatMap(SlotCollection::itemCopies);
      }

      return var10000;
   }

   public static record Filtered(SlotCollection slots, Predicate<? super ItemStack> filter) implements SlotCollection {
      public Filtered {
         super();
      }

      public Stream<ItemStack> itemCopies() {
         return this.slots.itemCopies().filter(this.filter);
      }

      public SlotCollection filter(final Predicate<? super ItemStack> predicate) {
         Objects.requireNonNull(predicate);
         return new Filtered(this.slots, (t) -> this.filter.test(t) && predicate.test(t));
      }
   }

   public static record FlatMapped(SlotCollection slots, Function<ItemStack, ? extends SlotCollection> mapper) implements SlotCollection {
      public FlatMapped {
         super();
      }

      public Stream<ItemStack> itemCopies() {
         return this.slots.itemCopies().map(this.mapper).flatMap(SlotCollection::itemCopies);
      }
   }

   public static record Limited(SlotCollection slots, int limit) implements SlotCollection {
      public Limited {
         super();
      }

      public Stream<ItemStack> itemCopies() {
         return this.slots.itemCopies().limit((long)this.limit);
      }

      public SlotCollection limit(final int limit) {
         return new Limited(this.slots, Math.min(this.limit, limit));
      }
   }
}
