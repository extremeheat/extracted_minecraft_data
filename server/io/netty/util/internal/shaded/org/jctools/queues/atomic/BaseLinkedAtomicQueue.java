package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import java.util.Iterator;

abstract class BaseLinkedAtomicQueue<E> extends BaseLinkedAtomicQueuePad2<E> {
   BaseLinkedAtomicQueue() {
      super();
   }

   public final Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   public String toString() {
      return this.getClass().getName();
   }

   protected final LinkedQueueAtomicNode<E> newNode() {
      return new LinkedQueueAtomicNode();
   }

   protected final LinkedQueueAtomicNode<E> newNode(E var1) {
      return new LinkedQueueAtomicNode(var1);
   }

   public final int size() {
      LinkedQueueAtomicNode var1 = this.lvConsumerNode();
      LinkedQueueAtomicNode var2 = this.lvProducerNode();

      int var3;
      for(var3 = 0; var1 != var2 && var1 != null && var3 < 2147483647; ++var3) {
         LinkedQueueAtomicNode var4 = var1.lvNext();
         if (var4 == var1) {
            return var3;
         }

         var1 = var4;
      }

      return var3;
   }

   public final boolean isEmpty() {
      return this.lvConsumerNode() == this.lvProducerNode();
   }

   protected E getSingleConsumerNodeValue(LinkedQueueAtomicNode<E> var1, LinkedQueueAtomicNode<E> var2) {
      Object var3 = var2.getAndNullValue();
      var1.soNext(var1);
      this.spConsumerNode(var2);
      return var3;
   }

   public E relaxedPoll() {
      LinkedQueueAtomicNode var1 = this.lpConsumerNode();
      LinkedQueueAtomicNode var2 = var1.lvNext();
      return var2 != null ? this.getSingleConsumerNodeValue(var1, var2) : null;
   }

   public E relaxedPeek() {
      LinkedQueueAtomicNode var1 = this.lpConsumerNode().lvNext();
      return var1 != null ? var1.lpValue() : null;
   }

   public boolean relaxedOffer(E var1) {
      return this.offer(var1);
   }

   public int drain(MessagePassingQueue.Consumer<E> var1) {
      long var2 = 0L;

      int var4;
      do {
         var4 = this.drain(var1, 4096);
         var2 += (long)var4;
      } while(var4 == 4096 && var2 <= 2147479551L);

      return (int)var2;
   }

   public int drain(MessagePassingQueue.Consumer<E> var1, int var2) {
      LinkedQueueAtomicNode var3 = this.consumerNode;

      for(int var4 = 0; var4 < var2; ++var4) {
         LinkedQueueAtomicNode var5 = var3.lvNext();
         if (var5 == null) {
            return var4;
         }

         Object var6 = this.getSingleConsumerNodeValue(var3, var5);
         var3 = var5;
         var1.accept(var6);
      }

      return var2;
   }

   public void drain(MessagePassingQueue.Consumer<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      LinkedQueueAtomicNode var4 = this.consumerNode;
      int var5 = 0;

      while(var3.keepRunning()) {
         for(int var6 = 0; var6 < 4096; ++var6) {
            LinkedQueueAtomicNode var7 = var4.lvNext();
            if (var7 == null) {
               var5 = var2.idle(var5);
            } else {
               var5 = 0;
               Object var8 = this.getSingleConsumerNodeValue(var4, var7);
               var4 = var7;
               var1.accept(var8);
            }
         }
      }

   }

   public int capacity() {
      return -1;
   }
}
