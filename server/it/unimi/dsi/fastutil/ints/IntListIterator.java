package it.unimi.dsi.fastutil.ints;

import java.util.ListIterator;

public interface IntListIterator extends IntBidirectionalIterator, ListIterator<Integer> {
   default void set(int var1) {
      throw new UnsupportedOperationException();
   }

   default void add(int var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Integer var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Integer var1) {
      this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer next() {
      return IntBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Integer previous() {
      return IntBidirectionalIterator.super.previous();
   }
}
