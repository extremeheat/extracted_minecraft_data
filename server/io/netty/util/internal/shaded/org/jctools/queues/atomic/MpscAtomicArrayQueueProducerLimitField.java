package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueProducerLimitField<E> extends MpscAtomicArrayQueueMidPad<E> {
   private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueProducerLimitField> P_LIMIT_UPDATER = AtomicLongFieldUpdater.newUpdater(MpscAtomicArrayQueueProducerLimitField.class, "producerLimit");
   private volatile long producerLimit;

   public MpscAtomicArrayQueueProducerLimitField(int var1) {
      super(var1);
      this.producerLimit = (long)var1;
   }

   protected final long lvProducerLimit() {
      return this.producerLimit;
   }

   protected final void soProducerLimit(long var1) {
      P_LIMIT_UPDATER.lazySet(this, var1);
   }
}
