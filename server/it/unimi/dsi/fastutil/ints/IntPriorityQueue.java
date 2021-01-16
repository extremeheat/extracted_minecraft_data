package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface IntPriorityQueue extends PriorityQueue<Integer> {
   void enqueue(int var1);

   int dequeueInt();

   int firstInt();

   default int lastInt() {
      throw new UnsupportedOperationException();
   }

   IntComparator comparator();

   /** @deprecated */
   @Deprecated
   default void enqueue(Integer var1) {
      this.enqueue(var1);
   }

   /** @deprecated */
   @Deprecated
   default Integer dequeue() {
      return this.dequeueInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   /** @deprecated */
   @Deprecated
   default Integer last() {
      return this.lastInt();
   }
}
