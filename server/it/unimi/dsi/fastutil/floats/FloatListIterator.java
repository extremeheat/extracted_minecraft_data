package it.unimi.dsi.fastutil.floats;

import java.util.ListIterator;

public interface FloatListIterator extends FloatBidirectionalIterator, ListIterator<Float> {
   default void set(float var1) {
      throw new UnsupportedOperationException();
   }

   default void add(float var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Float var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Float var1) {
      this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default Float next() {
      return FloatBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Float previous() {
      return FloatBidirectionalIterator.super.previous();
   }
}
