package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface BytePriorityQueue extends PriorityQueue<Byte> {
   void enqueue(byte var1);

   byte dequeueByte();

   byte firstByte();

   default byte lastByte() {
      throw new UnsupportedOperationException();
   }

   ByteComparator comparator();

   /** @deprecated */
   @Deprecated
   default void enqueue(Byte var1) {
      this.enqueue(var1);
   }

   /** @deprecated */
   @Deprecated
   default Byte dequeue() {
      return this.dequeueByte();
   }

   /** @deprecated */
   @Deprecated
   default Byte first() {
      return this.firstByte();
   }

   /** @deprecated */
   @Deprecated
   default Byte last() {
      return this.lastByte();
   }
}
