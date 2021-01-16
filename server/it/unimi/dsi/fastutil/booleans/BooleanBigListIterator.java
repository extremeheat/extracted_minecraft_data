package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface BooleanBigListIterator extends BooleanBidirectionalIterator, BigListIterator<Boolean> {
   default void set(boolean var1) {
      throw new UnsupportedOperationException();
   }

   default void add(boolean var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Boolean var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Boolean var1) {
      this.add(var1);
   }

   default long skip(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasNext()) {
         this.nextBoolean();
      }

      return var1 - var3 - 1L;
   }

   default long back(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasPrevious()) {
         this.previousBoolean();
      }

      return var1 - var3 - 1L;
   }

   default int skip(int var1) {
      return SafeMath.safeLongToInt(this.skip((long)var1));
   }
}
