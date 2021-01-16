package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class BaseLinkedAtomicQueueProducerNodeRef<E> extends BaseLinkedAtomicQueuePad0<E> {
   private static final AtomicReferenceFieldUpdater<BaseLinkedAtomicQueueProducerNodeRef, LinkedQueueAtomicNode> P_NODE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(BaseLinkedAtomicQueueProducerNodeRef.class, LinkedQueueAtomicNode.class, "producerNode");
   protected volatile LinkedQueueAtomicNode<E> producerNode;

   BaseLinkedAtomicQueueProducerNodeRef() {
      super();
   }

   protected final void spProducerNode(LinkedQueueAtomicNode<E> var1) {
      P_NODE_UPDATER.lazySet(this, var1);
   }

   protected final LinkedQueueAtomicNode<E> lvProducerNode() {
      return this.producerNode;
   }

   protected final boolean casProducerNode(LinkedQueueAtomicNode<E> var1, LinkedQueueAtomicNode<E> var2) {
      return P_NODE_UPDATER.compareAndSet(this, var1, var2);
   }

   protected final LinkedQueueAtomicNode<E> lpProducerNode() {
      return this.producerNode;
   }

   protected final LinkedQueueAtomicNode<E> xchgProducerNode(LinkedQueueAtomicNode<E> var1) {
      return (LinkedQueueAtomicNode)P_NODE_UPDATER.getAndSet(this, var1);
   }
}
