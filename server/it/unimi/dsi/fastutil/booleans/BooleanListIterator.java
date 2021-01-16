package it.unimi.dsi.fastutil.booleans;

import java.util.ListIterator;

public interface BooleanListIterator extends BooleanBidirectionalIterator, ListIterator<Boolean> {
   default void set(boolean var1) {
      throw new UnsupportedOperationException();
   }

   default void add(boolean var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Boolean var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Boolean var1) {
      this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default Boolean next() {
      return BooleanBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Boolean previous() {
      return BooleanBidirectionalIterator.super.previous();
   }
}
