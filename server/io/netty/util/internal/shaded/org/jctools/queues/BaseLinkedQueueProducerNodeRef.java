package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.lang.reflect.Field;

abstract class BaseLinkedQueueProducerNodeRef<E> extends BaseLinkedQueuePad0<E> {
   protected static final long P_NODE_OFFSET;
   protected LinkedQueueNode<E> producerNode;

   BaseLinkedQueueProducerNodeRef() {
      super();
   }

   protected final void spProducerNode(LinkedQueueNode<E> var1) {
      this.producerNode = var1;
   }

   protected final LinkedQueueNode<E> lvProducerNode() {
      return (LinkedQueueNode)UnsafeAccess.UNSAFE.getObjectVolatile(this, P_NODE_OFFSET);
   }

   protected final boolean casProducerNode(LinkedQueueNode<E> var1, LinkedQueueNode<E> var2) {
      return UnsafeAccess.UNSAFE.compareAndSwapObject(this, P_NODE_OFFSET, var1, var2);
   }

   protected final LinkedQueueNode<E> lpProducerNode() {
      return this.producerNode;
   }

   static {
      try {
         Field var0 = BaseLinkedQueueProducerNodeRef.class.getDeclaredField("producerNode");
         P_NODE_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(var0);
      } catch (NoSuchFieldException var1) {
         throw new RuntimeException(var1);
      }
   }
}
