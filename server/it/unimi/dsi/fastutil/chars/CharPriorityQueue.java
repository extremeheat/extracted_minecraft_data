package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface CharPriorityQueue extends PriorityQueue<Character> {
   void enqueue(char var1);

   char dequeueChar();

   char firstChar();

   default char lastChar() {
      throw new UnsupportedOperationException();
   }

   CharComparator comparator();

   /** @deprecated */
   @Deprecated
   default void enqueue(Character var1) {
      this.enqueue(var1);
   }

   /** @deprecated */
   @Deprecated
   default Character dequeue() {
      return this.dequeueChar();
   }

   /** @deprecated */
   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   /** @deprecated */
   @Deprecated
   default Character last() {
      return this.lastChar();
   }
}
