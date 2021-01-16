package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;

abstract class MpscChunkedAtomicArrayQueueColdProducerFields<E> extends BaseMpscLinkedAtomicArrayQueue<E> {
   protected final long maxQueueCapacity;

   public MpscChunkedAtomicArrayQueueColdProducerFields(int var1, int var2) {
      super(var1);
      RangeUtil.checkGreaterThanOrEqual(var2, 4, "maxCapacity");
      RangeUtil.checkLessThan(Pow2.roundToPowerOfTwo(var1), Pow2.roundToPowerOfTwo(var2), "initialCapacity");
      this.maxQueueCapacity = (long)Pow2.roundToPowerOfTwo(var2) << 1;
   }
}
