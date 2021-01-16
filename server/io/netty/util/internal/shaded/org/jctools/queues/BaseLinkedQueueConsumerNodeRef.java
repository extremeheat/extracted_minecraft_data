package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;

abstract class BaseLinkedQueueConsumerNodeRef<E> extends BaseLinkedQueuePad1<E> {
   protected static final long C_NODE_OFFSET;
   protected LinkedQueueNode<E> consumerNode;

   BaseLinkedQueueConsumerNodeRef() {
      super();
   }

   protected final void spConsumerNode(LinkedQueueNode<E> var1) {
      this.consumerNode = var1;
   }

   protected final LinkedQueueNode<E> lvConsumerNode() {
      return (LinkedQueueNode)UnsafeAccess.UNSAFE.getObjectVolatile(this, C_NODE_OFFSET);
   }

   protected final LinkedQueueNode<E> lpConsumerNode() {
      return this.consumerNode;
   }

   static {
      try {
         Field var0 = BaseLinkedQueueConsumerNodeRef.class.getDeclaredField("consumerNode");
         C_NODE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(var0);
      } catch (NoSuchFieldException var1) {
         throw new RuntimeException(var1);
      }
   }
}
