package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.Iterator;

public abstract class ConcurrentCircularArrayQueue<E> extends ConcurrentCircularArrayQueueL0Pad<E> {
   protected final long mask;
   protected final E[] buffer;

   public ConcurrentCircularArrayQueue(int var1) {
      super();
      int var2 = Pow2.roundToPowerOfTwo(var1);
      this.mask = (long)(var2 - 1);
      this.buffer = CircularArrayOffsetCalculator.allocate(var2);
   }

   protected static long calcElementOffset(long var0, long var2) {
      return CircularArrayOffsetCalculator.calcElementOffset(var0, var2);
   }

   protected final long calcElementOffset(long var1) {
      return calcElementOffset(var1, this.mask);
   }

   public Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   public final int size() {
      return IndexedQueueSizeUtil.size(this);
   }

   public final boolean isEmpty() {
      return IndexedQueueSizeUtil.isEmpty(this);
   }

   public String toString() {
      return this.getClass().getName();
   }

   public void clear() {
      while(this.poll() != null) {
      }

   }

   public int capacity() {
      return (int)(this.mask + 1L);
   }

   public final long currentProducerIndex() {
      return this.lvProducerIndex();
   }

   public final long currentConsumerIndex() {
      return this.lvConsumerIndex();
   }
}
