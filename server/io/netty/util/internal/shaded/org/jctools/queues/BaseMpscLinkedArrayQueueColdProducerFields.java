package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;

abstract class BaseMpscLinkedArrayQueueColdProducerFields<E> extends BaseMpscLinkedArrayQueuePad3<E> {
   private static final long P_LIMIT_OFFSET;
   private volatile long producerLimit;
   protected long producerMask;
   protected E[] producerBuffer;

   BaseMpscLinkedArrayQueueColdProducerFields() {
      super();
   }

   final long lvProducerLimit() {
      return this.producerLimit;
   }

   final boolean casProducerLimit(long var1, long var3) {
      return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_LIMIT_OFFSET, var1, var3);
   }

   final void soProducerLimit(long var1) {
      UnsafeAccess.UNSAFE.putOrderedLong(this, P_LIMIT_OFFSET, var1);
   }

   static {
      try {
         Field var0 = BaseMpscLinkedArrayQueueColdProducerFields.class.getDeclaredField("producerLimit");
         P_LIMIT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(var0);
      } catch (NoSuchFieldException var1) {
         throw new RuntimeException(var1);
      }
   }
}
