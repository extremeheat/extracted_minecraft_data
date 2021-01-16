package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface ShortBidirectionalIterator extends ShortIterator, ObjectBidirectionalIterator<Short> {
   short previousShort();

   /** @deprecated */
   @Deprecated
   default Short previous() {
      return this.previousShort();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousShort();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return ShortIterator.super.skip(var1);
   }
}
