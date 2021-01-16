package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.SafeMath;

public interface CharBigListIterator extends CharBidirectionalIterator, BigListIterator<Character> {
   default void set(char var1) {
      throw new UnsupportedOperationException();
   }

   default void add(char var1) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   @Deprecated
   default void set(Character var1) {
      this.set(var1);
   }

   /** @deprecated */
   @Deprecated
   default void add(Character var1) {
      this.add(var1);
   }

   default long skip(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasNext()) {
         this.nextChar();
      }

      return var1 - var3 - 1L;
   }

   default long back(long var1) {
      long var3 = var1;

      while(var3-- != 0L && this.hasPrevious()) {
         this.previousChar();
      }

      return var1 - var3 - 1L;
   }

   default int skip(int var1) {
      return SafeMath.safeLongToInt(this.skip((long)var1));
   }
}
