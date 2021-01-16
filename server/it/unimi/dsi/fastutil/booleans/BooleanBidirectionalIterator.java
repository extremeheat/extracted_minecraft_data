package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface BooleanBidirectionalIterator extends BooleanIterator, ObjectBidirectionalIterator<Boolean> {
   boolean previousBoolean();

   /** @deprecated */
   @Deprecated
   default Boolean previous() {
      return this.previousBoolean();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousBoolean();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return BooleanIterator.super.skip(var1);
   }
}
