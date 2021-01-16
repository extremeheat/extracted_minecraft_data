package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

final class LinkedQueueNode<E> {
   private static final long NEXT_OFFSET;
   private E value;
   private volatile LinkedQueueNode<E> next;

   LinkedQueueNode() {
      this((Object)null);
   }

   LinkedQueueNode(E var1) {
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

   public void soNext(LinkedQueueNode<E> var1) {
      UnsafeAccess.UNSAFE.putOrderedObject(this, NEXT_OFFSET, var1);
   }

   public LinkedQueueNode<E> lvNext() {
      return this.next;
   }

   static {
      try {
         NEXT_OFFSET = UnsafeAccess.UNSAFE.objectFieldOffset(LinkedQueueNode.class.getDeclaredField("next"));
      } catch (NoSuchFieldException var1) {
         throw new RuntimeException(var1);
      }
   }
}
