package it.unimi.dsi.fastutil.doubles;

import java.util.ListIterator;

public interface DoubleListIterator extends DoubleBidirectionalIterator, ListIterator<Double> {
   default void set(double var1) {
      throw new UnsupportedOperationException();
   }

   default void add(double var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Double var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Double var1) {
      this.add(var1);
   }

   /** @deprecated */
   @Deprecated
   default Double next() {
      return DoubleBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Double previous() {
      return DoubleBidirectionalIterator.super.previous();
   }
}
