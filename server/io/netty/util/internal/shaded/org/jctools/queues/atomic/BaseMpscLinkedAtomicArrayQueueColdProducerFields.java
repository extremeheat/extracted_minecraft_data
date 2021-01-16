package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceArray;

abstract class BaseMpscLinkedAtomicArrayQueueColdProducerFields<E> extends BaseMpscLinkedAtomicArrayQueuePad3<E> {
   private static final AtomicLongFieldUpdater<BaseMpscLinkedAtomicArrayQueueColdProducerFields> P_LIMIT_UPDATER = AtomicLongFieldUpdater.newUpdater(BaseMpscLinkedAtomicArrayQueueColdProducerFields.class, "producerLimit");
   protected volatile long producerLimit;
   protected long producerMask;
   protected AtomicReferenceArray<E> producerBuffer;

   BaseMpscLinkedAtomicArrayQueueColdProducerFields() {
      super();
   }

   final long lvProducerLimit() {
      return this.producerLimit;
   }

   final boolean casProducerLimit(long var1, long var3) {
      return P_LIMIT_UPDATER.compareAndSet(this, var1, var3);
   }

   final void soProducerLimit(long var1) {
      P_LIMIT_UPDATER.lazySet(this, var1);
   }
}
