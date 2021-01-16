package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BidirectionalIterator;

public interface ObjectBidirectionalIterator<K> extends ObjectIterator<K>, BidirectionalIterator<K> {
   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previous();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return ObjectIterator.super.skip(var1);
   }
}
