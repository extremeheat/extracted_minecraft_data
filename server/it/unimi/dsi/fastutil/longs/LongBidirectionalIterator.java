package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface LongBidirectionalIterator extends LongIterator, ObjectBidirectionalIterator<Long> {
   long previousLong();

   /** @deprecated */
   @Deprecated
   default Long previous() {
      return this.previousLong();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousLong();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return LongIterator.super.skip(var1);
   }
}
