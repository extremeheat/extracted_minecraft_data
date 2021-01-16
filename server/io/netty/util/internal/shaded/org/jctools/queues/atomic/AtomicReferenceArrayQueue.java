package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.IndexedQueueSizeUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

abstract class AtomicReferenceArrayQueue<E> extends AbstractQueue<E> implements IndexedQueueSizeUtil.IndexedQueue, QueueProgressIndicators, MessagePassingQueue<E> {
   protected final AtomicReferenceArray<E> buffer;
   protected final int mask;

   public AtomicReferenceArrayQueue(int var1) {
      super();
      int var2 = Pow2.roundToPowerOfTwo(var1);
      this.mask = var2 - 1;
      this.buffer = new AtomicReferenceArray(var2);
   }

   public Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   public String toString() {
      return this.getClass().getName();
   }

   public void clear() {
      while(this.poll() != null) {
      }

   }

   protected final int calcElementOffset(long var1, int var3) {
      return (int)var1 & var3;
   }

   protected final int calcElementOffset(long var1) {
      return (int)var1 & this.mask;
   }

   public static <E> E lvElement(AtomicReferenceArray<E> var0, int var1) {
      return var0.get(var1);
   }

   public static <E> E lpElement(AtomicReferenceArray<E> var0, int var1) {
      return var0.get(var1);
   }

   protected final E lpElement(int var1) {
      return this.buffer.get(var1);
   }

   public static <E> void spElement(AtomicReferenceArray<E> var0, int var1, E var2) {
      var0.lazySet(var1, var2);
   }

   protected final void spElement(int var1, E var2) {
      this.buffer.lazySet(var1, var2);
   }

   public static <E> void soElement(AtomicReferenceArray<E> var0, int var1, E var2) {
      var0.lazySet(var1, var2);
   }

   protected final void soElement(int var1, E var2) {
      this.buffer.lazySet(var1, var2);
   }

   public static <E> void svElement(AtomicReferenceArray<E> var0, int var1, E var2) {
      var0.set(var1, var2);
   }

   protected final E lvElement(int var1) {
      return lvElement(this.buffer, var1);
   }

   public final int capacity() {
      return this.mask + 1;
   }

   public final int size() {
      return IndexedQueueSizeUtil.size(this);
   }

   public final boolean isEmpty() {
      return IndexedQueueSizeUtil.isEmpty(this);
   }

   public final long currentProducerIndex() {
      return this.lvProducerIndex();
   }

   public final long currentConsumerIndex() {
      return this.lvConsumerIndex();
   }
}
