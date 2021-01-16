package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface ShortPriorityQueue extends PriorityQueue<Short> {
   void enqueue(short var1);

   short dequeueShort();

   short firstShort();

   default short lastShort() {
      throw new UnsupportedOperationException();
   }

   ShortComparator comparator();

   /** @deprecated */
   @Deprecated
   default void enqueue(Short var1) {
      this.enqueue(var1);
   }

   /** @deprecated */
   @Deprecated
   default Short dequeue() {
      return this.dequeueShort();
   }

   /** @deprecated */
   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   /** @deprecated */
   @Deprecated
   default Short last() {
      return this.lastShort();
   }
}
