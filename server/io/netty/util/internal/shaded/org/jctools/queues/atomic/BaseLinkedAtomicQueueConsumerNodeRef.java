package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class BaseLinkedAtomicQueueConsumerNodeRef<E> extends BaseLinkedAtomicQueuePad1<E> {
   private static final AtomicReferenceFieldUpdater<BaseLinkedAtomicQueueConsumerNodeRef, LinkedQueueAtomicNode> C_NODE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(BaseLinkedAtomicQueueConsumerNodeRef.class, LinkedQueueAtomicNode.class, "consumerNode");
   protected volatile LinkedQueueAtomicNode<E> consumerNode;

   BaseLinkedAtomicQueueConsumerNodeRef() {
      super();
   }

   protected final void spConsumerNode(LinkedQueueAtomicNode<E> var1) {
      C_NODE_UPDATER.lazySet(this, var1);
   }

   protected final LinkedQueueAtomicNode<E> lvConsumerNode() {
      return this.consumerNode;
   }

   protected final LinkedQueueAtomicNode<E> lpConsumerNode() {
      return this.consumerNode;
   }
}
