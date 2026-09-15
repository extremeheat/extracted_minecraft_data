package net.minecraft.world.item;

import java.util.NoSuchElementException;
import java.util.function.Predicate;
import org.jspecify.annotations.Nullable;

public class ArrayItemProvider implements ItemProvider {
   protected final ItemStack[] items;
   protected int position;
   private @Nullable ItemStack peekedItem;

   public ArrayItemProvider(final ItemStack[] items) {
      super();
      this.items = items;
   }

   public boolean hasNext() {
      return this.position < this.items.length;
   }

   public void restart() {
      this.setPosition(0);
   }

   public final ItemStack next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         ItemStack next = this.peekedItem != null ? this.peekedItem : this.computeNextItem();
         this.peekedItem = null;
         ++this.position;
         return next;
      }
   }

   public final ItemStack peek() {
      if (!this.hasNext()) {
         throw new NoSuchElementException();
      } else {
         if (this.peekedItem == null) {
            this.peekedItem = this.computeNextItem();
         }

         return this.peekedItem;
      }
   }

   protected ItemStack computeNextItem() {
      return this.items[this.position].copy();
   }

   public boolean findNext(final Predicate<ItemStack> predicate) {
      for(int i = this.position; i < this.items.length; ++i) {
         if (predicate.test(this.items[i])) {
            this.setPosition(i);
            return true;
         }
      }

      return false;
   }

   protected void setPosition(final int newPosition) {
      if (this.position != newPosition) {
         this.position = newPosition;
         this.peekedItem = null;
      }

   }

   public static class Cycled extends ArrayItemProvider {
      public Cycled(final ItemStack[] items) {
         super(items);
      }

      public boolean hasNext() {
         return this.items.length > 0;
      }

      protected ItemStack computeNextItem() {
         return this.items[this.position % this.items.length].copy();
      }

      public boolean findNext(final Predicate<ItemStack> predicate) {
         for(int offset = 0; offset < this.items.length; ++offset) {
            int index = (this.position + offset) % this.items.length;
            if (predicate.test(this.items[index])) {
               this.setPosition(this.position + offset);
               return true;
            }
         }

         return false;
      }
   }
}
