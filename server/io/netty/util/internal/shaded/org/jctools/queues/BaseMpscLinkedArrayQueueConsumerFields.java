package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;

abstract class BaseMpscLinkedArrayQueueConsumerFields<E> extends BaseMpscLinkedArrayQueuePad2<E> {
   private static final long C_INDEX_OFFSET;
   protected long consumerMask;
   protected E[] consumerBuffer;
   protected long consumerIndex;

   BaseMpscLinkedArrayQueueConsumerFields() {
      super();
   }

   public final long lvConsumerIndex() {
      return UnsafeAccess.UNSAFE.getLongVolatile(this, C_INDEX_OFFSET);
   }

   final void soConsumerIndex(long var1) {
      UnsafeAccess.UNSAFE.putOrderedLong(this, C_INDEX_OFFSET, var1);
   }

   static {
      try {
         Field var0 = BaseMpscLinkedArrayQueueConsumerFields.class.getDeclaredField("consumerIndex");
         C_INDEX_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(var0);
      } catch (NoSuchFieldException var1) {
         throw new RuntimeException(var1);
      }
   }
}
