package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface IntBidirectionalIterator extends IntIterator, ObjectBidirectionalIterator<Integer> {
   int previousInt();

   /** @deprecated */
   @Deprecated
   default Integer previous() {
      return this.previousInt();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousInt();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return IntIterator.super.skip(var1);
   }
}
