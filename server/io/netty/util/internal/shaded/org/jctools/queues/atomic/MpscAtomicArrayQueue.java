package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscAtomicArrayQueue<E> extends MpscAtomicArrayQueueL3Pad<E> {
   public MpscAtomicArrayQueue(int var1) {
      super(var1);
   }

   public boolean offerIfBelowThreshold(E var1, int var2) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         int var3 = this.mask;
         long var4 = (long)(var3 + 1);
         long var6 = this.lvProducerLimit();

         long var8;
         do {
            var8 = this.lvProducerIndex();
            long var10 = var6 - var8;
            long var12 = var4 - var10;
            if (var12 >= (long)var2) {
               long var14 = this.lvConsumerIndex();
               var12 = var8 - var14;
               if (var12 >= (long)var2) {
                  return false;
               }

               var6 = var14 + var4;
               this.soProducerLimit(var6);
            }
         } while(!this.casProducerIndex(var8, var8 + 1L));

         int var16 = this.calcElementOffset(var8, var3);
         soElement(this.buffer, var16, var1);
         return true;
      }
   }

   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         int var2 = this.mask;
         long var3 = this.lvProducerLimit();

         long var5;
         do {
            var5 = this.lvProducerIndex();
            if (var5 >= var3) {
               long var7 = this.lvConsumerIndex();
               var3 = var7 + (long)var2 + 1L;
               if (var5 >= var3) {
                  return false;
               }

               this.soProducerLimit(var3);
            }
         } while(!this.casProducerIndex(var5, var5 + 1L));

         int var9 = this.calcElementOffset(var5, var2);
         soElement(this.buffer, var9, var1);
         return true;
      }
   }

   public final int failFastOffer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         int var2 = this.mask;
         long var3 = (long)(var2 + 1);
         long var5 = this.lvProducerIndex();
         long var7 = this.lvProducerLimit();
         if (var5 >= var7) {
            long var9 = this.lvConsumerIndex();
            var7 = var9 + var3;
            if (var5 >= var7) {
               return 1;
            }

            this.soProducerLimit(var7);
         }

         if (!this.casProducerIndex(var5, var5 + 1L)) {
            return -1;
         } else {
            int var11 = this.calcElementOffset(var5, var2);
            soElement(this.buffer, var11, var1);
            return 0;
         }
      }
   }

   public E poll() {
      long var1 = this.lpConsumerIndex();
      int var3 = this.calcElementOffset(var1);
      AtomicReferenceArray var4 = this.buffer;
      Object var5 = lvElement(var4, var3);
      if (null == var5) {
         if (var1 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var5 = lvElement(var4, var3);
         } while(var5 == null);
      }

      spElement(var4, var3, (Object)null);
      this.soConsumerIndex(var1 + 1L);
      return var5;
   }

   public E peek() {
      AtomicReferenceArray var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      int var4 = this.calcElementOffset(var2);
      Object var5 = lvElement(var1, var4);
      if (null == var5) {
         if (var2 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var5 = lvElement(var1, var4);
         } while(var5 == null);
      }

      return var5;
   }

   public boolean relaxedOffer(E var1) {
      return this.offer(var1);
   }

   public E relaxedPoll() {
      AtomicReferenceArray var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      int var4 = this.calcElementOffset(var2);
      Object var5 = lvElement(var1, var4);
      if (null == var5) {
         return null;
      } else {
         spElement(var1, var4, (Object)null);
         this.soConsumerIndex(var2 + 1L);
         return var5;
      }
   }

   public E relaxedPeek() {
      AtomicReferenceArray var1 = this.buffer;
      int var2 = this.mask;
      long var3 = this.lpConsumerIndex();
      return lvElement(var1, this.calcElementOffset(var3, var2));
   }

   public int drain(MessagePassingQueue.Consumer<E> var1) {
      return this.drain(var1, this.capacity());
   }

   public int fill(MessagePassingQueue.Supplier<E> var1) {
      long var2 = 0L;
      int var4 = this.capacity();

      do {
         int var5 = this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH);
         if (var5 == 0) {
            return (int)var2;
         }

         var2 += (long)var5;
      } while(var2 <= (long)var4);

      return (int)var2;
   }

   public int drain(MessagePassingQueue.Consumer<E> var1, int var2) {
      AtomicReferenceArray var3 = this.buffer;
      int var4 = this.mask;
      long var5 = this.lpConsumerIndex();

      for(int var7 = 0; var7 < var2; ++var7) {
         long var8 = var5 + (long)var7;
         int var10 = this.calcElementOffset(var8, var4);
         Object var11 = lvElement(var3, var10);
         if (null == var11) {
            return var7;
         }

         spElement(var3, var10, (Object)null);
         this.soConsumerIndex(var8 + 1L);
         var1.accept(var11);
      }

      return var2;
   }

   public int fill(MessagePassingQueue.Supplier<E> var1, int var2) {
      int var3 = this.mask;
      long var4 = (long)(var3 + 1);
      long var6 = this.lvProducerLimit();
      boolean var10 = false;

      long var8;
      int var15;
      do {
         var8 = this.lvProducerIndex();
         long var11 = var6 - var8;
         if (var11 <= 0L) {
            long var13 = this.lvConsumerIndex();
            var6 = var13 + var4;
            var11 = var6 - var8;
            if (var11 <= 0L) {
               return 0;
            }

            this.soProducerLimit(var6);
         }

         var15 = Math.min((int)var11, var2);
      } while(!this.casProducerIndex(var8, var8 + (long)var15));

      AtomicReferenceArray var16 = this.buffer;

      for(int var12 = 0; var12 < var15; ++var12) {
         int var17 = this.calcElementOffset(var8 + (long)var12, var3);
         soElement(var16, var17, var1.get());
      }

      return var15;
   }

   public void drain(MessagePassingQueue.Consumer<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      AtomicReferenceArray var4 = this.buffer;
      int var5 = this.mask;
      long var6 = this.lpConsumerIndex();
      int var8 = 0;

      while(var3.keepRunning()) {
         for(int var9 = 0; var9 < 4096; ++var9) {
            int var10 = this.calcElementOffset(var6, var5);
            Object var11 = lvElement(var4, var10);
            if (null == var11) {
               var8 = var2.idle(var8);
            } else {
               ++var6;
               var8 = 0;
               spElement(var4, var10, (Object)null);
               this.soConsumerIndex(var6);
               var1.accept(var11);
            }
         }
      }

   }

   public void fill(MessagePassingQueue.Supplier<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      int var4 = 0;

      while(var3.keepRunning()) {
         if (this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0) {
            var4 = var2.idle(var4);
         } else {
            var4 = 0;
         }
      }

   }

   /** @deprecated */
   @Deprecated
   public int weakOffer(E var1) {
      return this.failFastOffer(var1);
   }
}
