package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface FloatBidirectionalIterator extends FloatIterator, ObjectBidirectionalIterator<Float> {
   float previousFloat();

   /** @deprecated */
   @Deprecated
   default Float previous() {
      return this.previousFloat();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousFloat();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return FloatIterator.super.skip(var1);
   }
}
