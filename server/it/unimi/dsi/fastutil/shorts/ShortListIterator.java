package it.unimi.dsi.fastutil.shorts;

import java.util.ListIterator;

public interface ShortListIterator extends ShortBidirectionalIterator, ListIterator<Short> {
   default void set(short var1) {
      throw new UnsupportedOperationException();
   }

   default void add(short var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Short var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Short var1) {
      this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short next() {
      return ShortBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Short previous() {
      return ShortBidirectionalIterator.super.previous();
   }
}
