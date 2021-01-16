package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface ObjectBigListIterator<K> extends ObjectBidirectionalIterator<K>, BigListIterator<K> {
   default void set(K var1) {
      throw new UnsupportedOperationException();
   }

   default void add(K var1) {
      throw new UnsupportedOperationException();
   }

   default long skip(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasNext()) {
         this.next();
      }

      return var1 - var3 - 1L;
   }

   default long back(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasPrevious()) {
         this.previous();
      }

      return var1 - var3 - 1L;
   }

   default int skip(int var1) {
      return SafeMath.safeLongToInt(this.skip((long)var1));
   }
}
