package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;

public class SpscLinkedAtomicQueue<E> extends BaseLinkedAtomicQueue<E> {
   public SpscLinkedAtomicQueue() {
      super();
      LinkedQueueAtomicNode var1 = this.newNode();
      this.spProducerNode(var1);
      this.spConsumerNode(var1);
      var1.soNext((LinkedQueueAtomicNode)null);
   }

   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         LinkedQueueAtomicNode var2 = this.newNode(var1);
         this.lpProducerNode().soNext(var2);
         this.spProducerNode(var2);
         return true;
      }
   }

   public E poll() {
      return this.relaxedPoll();
   }

   public E peek() {
      return this.relaxedPeek();
   }

   public int fill(MessagePassingQueue.Supplier<E> var1) {
      long var2 = 0L;

      do {
         this.fill(var1, 4096);
         var2 += 4096L;
      } while(var2 <= 2147479551L);

      return (int)var2;
   }

   public int fill(MessagePassingQueue.Supplier<E> var1, int var2) {
      if (var2 == 0) {
         return 0;
      } else {
         LinkedQueueAtomicNode var3 = this.newNode(var1.get());

         for(int var5 = 1; var5 < var2; ++var5) {
            LinkedQueueAtomicNode var6 = this.newNode(var1.get());
            var3.soNext(var6);
            var3 = var6;
         }

         LinkedQueueAtomicNode var7 = this.lpProducerNode();
         var7.soNext(var3);
         this.spProducerNode(var3);
         return var2;
      }
   }

   public void fill(MessagePassingQueue.Supplier<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      LinkedQueueAtomicNode var4 = this.producerNode;

      while(var3.keepRunning()) {
         for(int var5 = 0; var5 < 4096; ++var5) {
            LinkedQueueAtomicNode var6 = this.newNode(var1.get());
            var4.soNext(var6);
            var4 = var6;
            this.producerNode = var6;
         }
      }

   }
}
