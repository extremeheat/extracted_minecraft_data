package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;

abstract class BaseMpscLinkedArrayQueueProducerFields<E> extends BaseMpscLinkedArrayQueuePad1<E> {
   private static final long P_INDEX_OFFSET;
   protected long producerIndex;

   BaseMpscLinkedArrayQueueProducerFields() {
      super();
   }

   public final long lvProducerIndex() {
      return UnsafeAccess.UNSAFE.getLongVolatile(this, P_INDEX_OFFSET);
   }

   final void soProducerIndex(long var1) {
      UnsafeAccess.UNSAFE.putOrderedLong(this, P_INDEX_OFFSET, var1);
   }

   final boolean casProducerIndex(long var1, long var3) {
      return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, var1, var3);
   }

   static {
      try {
         Field var0 = BaseMpscLinkedArrayQueueProducerFields.class.getDeclaredField("producerIndex");
         P_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(var0);
      } catch (NoSuchFieldException var1) {
         throw new RuntimeException(var1);
      }
   }
}
