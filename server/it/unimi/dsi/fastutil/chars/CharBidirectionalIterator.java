package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface CharBidirectionalIterator extends CharIterator, ObjectBidirectionalIterator<Character> {
   char previousChar();

   /** @deprecated */
   @Deprecated
   default Character previous() {
      return this.previousChar();
   }

   default int back(int var1) {
      int var2 = var1;

      while(var2-- != 0 && this.hasPrevious()) {
         this.previousChar();
      }

      return var1 - var2 - 1;
   }

   default int skip(int var1) {
      return CharIterator.super.skip(var1);
   }
}
