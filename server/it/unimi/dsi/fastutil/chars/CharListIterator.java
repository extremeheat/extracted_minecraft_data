package it.unimi.dsi.fastutil.chars;

import java.util.ListIterator;

public interface CharListIterator extends CharBidirectionalIterator, ListIterator<Character> {
   default void set(char var1) {
      throw new UnsupportedOperationException();
   }

   default void add(char var1) {
      throw new UnsupportedOperationException();
   }

   default void remove() {
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

   /** @deprecated */
   @Deprecated
   default Character next() {
      return CharBidirectionalIterator.super.next();
   }

   /** @deprecated */
   @Deprecated
   default Character previous() {
      return CharBidirectionalIterator.super.previous();
   }
}
