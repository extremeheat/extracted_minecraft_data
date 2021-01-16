package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface DoubleBidirectionalIterator extends DoubleIterator, ObjectBidirectionalIterator<Double> {
   double previousDouble();

   /** @deprecated */
   @Deprecated
   default Double previous() {
      return this.previousDouble();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousDouble();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return DoubleIterator.super.skip(var1);
   }
}
