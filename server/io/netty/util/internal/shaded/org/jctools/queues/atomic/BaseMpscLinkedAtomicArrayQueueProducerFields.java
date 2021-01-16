package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class BaseMpscLinkedAtomicArrayQueueProducerFields<E> extends BaseMpscLinkedAtomicArrayQueuePad1<E> {
   private static final AtomicLongFieldUpdater<BaseMpscLinkedAtomicArrayQueueProducerFields> P_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(BaseMpscLinkedAtomicArrayQueueProducerFields.class, "producerIndex");
   protected volatile long producerIndex;

   BaseMpscLinkedAtomicArrayQueueProducerFields() {
      super();
   }

   public final long lvProducerIndex() {
      return this.producerIndex;
   }

   final void soProducerIndex(long var1) {
      P_INDEX_UPDATER.lazySet(this, var1);
   }

   final boolean casProducerIndex(long var1, long var3) {
      return P_INDEX_UPDATER.compareAndSet(this, var1, var3);
   }
}
