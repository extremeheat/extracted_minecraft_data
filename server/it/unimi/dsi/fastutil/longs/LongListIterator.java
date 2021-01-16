package it.unimi.dsi.fastutil.longs;

import java.util.ListIterator;

public interface LongListIterator extends LongBidirectionalIterator, ListIterator<Long> {
   default void set(long var1) {
      throw new UnsupportedOperationException();
   }

   default void add(long var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Long var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Long var1) {
      this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long next() {
      return LongBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Long previous() {
      return LongBidirectionalIterator.super.previous();
   }
}
