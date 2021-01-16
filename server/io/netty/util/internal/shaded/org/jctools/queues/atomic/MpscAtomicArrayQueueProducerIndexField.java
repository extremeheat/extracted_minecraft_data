package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueProducerIndexField<E> extends MpscAtomicArrayQueueL1Pad<E> {
   private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueProducerIndexField> P_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(MpscAtomicArrayQueueProducerIndexField.class, "producerIndex");
   private volatile long producerIndex;

   public MpscAtomicArrayQueueProducerIndexField(int var1) {
      super(var1);
   }

   public final long lvProducerIndex() {
      return this.producerIndex;
   }

   protected final boolean casProducerIndex(long var1, long var3) {
      return P_INDEX_UPDATER.compareAndSet(this, var1, var3);
   }
}
