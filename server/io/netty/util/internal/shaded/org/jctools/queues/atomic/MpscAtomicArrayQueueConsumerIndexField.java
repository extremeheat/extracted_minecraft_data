package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueConsumerIndexField<E> extends MpscAtomicArrayQueueL2Pad<E> {
   private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueConsumerIndexField> C_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(MpscAtomicArrayQueueConsumerIndexField.class, "consumerIndex");
   protected volatile long consumerIndex;

   public MpscAtomicArrayQueueConsumerIndexField(int var1) {
      super(var1);
   }

   protected final long lpConsumerIndex() {
      return this.consumerIndex;
   }

   public final long lvConsumerIndex() {
      return this.consumerIndex;
   }

   protected void soConsumerIndex(long var1) {
      C_INDEX_UPDATER.lazySet(this, var1);
   }
}
