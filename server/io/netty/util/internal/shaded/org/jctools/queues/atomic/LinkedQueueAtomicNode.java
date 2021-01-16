package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicReference;

public final class LinkedQueueAtomicNode<E> extends AtomicReference<LinkedQueueAtomicNode<E>> {
   private static final long serialVersionUID = 2404266111789071508L;
   private E value;

   LinkedQueueAtomicNode() {
      super();
   }

   LinkedQueueAtomicNode(E var1) {
      super();
      this.spValue(var1);
   }

   public E getAndNullValue() {
      Object var1 = this.lpValue();
      this.spValue((Object)null);
      return var1;
   }

   public E lpValue() {
      return this.value;
   }

   public void spValue(E var1) {
      this.value = var1;
   }

   public void soNext(LinkedQueueAtomicNode<E> var1) {
      this.lazySet(var1);
   }

   public LinkedQueueAtomicNode<E> lvNext() {
      return (LinkedQueueAtomicNode)this.get();
   }
}
