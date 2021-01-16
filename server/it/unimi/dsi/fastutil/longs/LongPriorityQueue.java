package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface LongPriorityQueue extends PriorityQueue<Long> {
   void enqueue(long var1);

   long dequeueLong();

   long firstLong();

   default long lastLong() {
      throw new UnsupportedOperationException();
   }

   LongComparator comparator();

   /** @deprecated */
   @Deprecated
   default void enqueue(Long var1) {
      this.enqueue(var1);
   }

   /** @deprecated */
   @Deprecated
   default Long dequeue() {
      return this.dequeueLong();
   }

   /** @deprecated */
   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   /** @deprecated */
   @Deprecated
   default Long last() {
      return this.lastLong();
   }
}
