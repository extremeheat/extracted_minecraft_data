package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class BaseMpscLinkedAtomicArrayQueue<E> extends BaseMpscLinkedAtomicArrayQueueColdProducerFields<E> implements MessagePassingQueue<E>, QueueProgressIndicators {
   private static final Object JUMP = new Object();

   public BaseMpscLinkedAtomicArrayQueue(int var1) {
      super();
      RangeUtil.checkGreaterThanOrEqual(var1, 2, "initialCapacity");
      int var2 = Pow2.roundToPowerOfTwo(var1);
      long var3 = (long)(var2 - 1 << 1);
      AtomicReferenceArray var5 = LinkedAtomicArrayQueueUtil.allocate(var2 + 1);
      this.producerBuffer = var5;
      this.producerMask = var3;
      this.consumerBuffer = var5;
      this.consumerMask = var3;
      this.soProducerLimit(var3);
   }

   public final Iterator<E> iterator() {
      throw new UnsupportedOperationException();
   }

   public final int size() {
      long var1 = this.lvConsumerIndex();

      long var5;
      long var7;
      do {
         var5 = var1;
         var7 = this.lvProducerIndex();
         var1 = this.lvConsumerIndex();
      } while(var5 != var1);

      long var3 = var7 - var1 >> 1;
      return var3 > 2147483647L ? 2147483647 : (int)var3;
   }

   public final boolean isEmpty() {
      return this.lvConsumerIndex() == this.lvProducerIndex();
   }

   public String toString() {
      return this.getClass().getName();
   }

   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         while(true) {
            while(true) {
               long var7 = this.lvProducerLimit();
               long var5 = this.lvProducerIndex();
               if ((var5 & 1L) != 1L) {
                  long var2 = this.producerMask;
                  AtomicReferenceArray var4 = this.producerBuffer;
                  if (var7 <= var5) {
                     int var9 = this.offerSlowPath(var2, var5, var7);
                     switch(var9) {
                     case 0:
                     default:
                        break;
                     case 1:
                        continue;
                     case 2:
                        return false;
                     case 3:
                        this.resize(var2, var4, var5, var1);
                        return true;
                     }
                  }

                  if (this.casProducerIndex(var5, var5 + 2L)) {
                     int var10 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var5, var2);
                     LinkedAtomicArrayQueueUtil.soElement(var4, var10, var1);
                     return true;
                  }
               }
            }
         }
      }
   }

   public E poll() {
      AtomicReferenceArray var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      int var6 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var7 = LinkedAtomicArrayQueueUtil.lvElement(var1, var6);
      if (var7 == null) {
         if (var2 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var7 = LinkedAtomicArrayQueueUtil.lvElement(var1, var6);
         } while(var7 == null);
      }

      if (var7 == JUMP) {
         AtomicReferenceArray var8 = this.getNextBuffer(var1, var4);
         return this.newBufferPoll(var8, var2);
      } else {
         LinkedAtomicArrayQueueUtil.soElement(var1, var6, (Object)null);
         this.soConsumerIndex(var2 + 2L);
         return var7;
      }
   }

   public E peek() {
      AtomicReferenceArray var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      int var6 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var7 = LinkedAtomicArrayQueueUtil.lvElement(var1, var6);
      if (var7 == null && var2 != this.lvProducerIndex()) {
         while(true) {
            if ((var7 = LinkedAtomicArrayQueueUtil.lvElement(var1, var6)) == null) {
               continue;
            }
         }
      }

      return var7 == JUMP ? this.newBufferPeek(this.getNextBuffer(var1, var4), var2) : var7;
   }

   private int offerSlowPath(long var1, long var3, long var5) {
      long var8 = this.lvConsumerIndex();
      long var10 = this.getCurrentBufferCapacity(var1);
      byte var7 = 0;
      if (var8 + var10 > var3) {
         if (!this.casProducerLimit(var5, var8 + var10)) {
            var7 = 1;
         }
      } else if (this.availableInQueue(var3, var8) <= 0L) {
         var7 = 2;
      } else if (this.casProducerIndex(var3, var3 + 1L)) {
         var7 = 3;
      } else {
         var7 = 1;
      }

      return var7;
   }

   protected abstract long availableInQueue(long var1, long var3);

   private AtomicReferenceArray<E> getNextBuffer(AtomicReferenceArray<E> var1, long var2) {
      int var4 = this.nextArrayOffset(var2);
      AtomicReferenceArray var5 = (AtomicReferenceArray)LinkedAtomicArrayQueueUtil.lvElement(var1, var4);
      LinkedAtomicArrayQueueUtil.soElement(var1, var4, (Object)null);
      return var5;
   }

   private int nextArrayOffset(long var1) {
      return LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var1 + 2L, 9223372036854775807L);
   }

   private E newBufferPoll(AtomicReferenceArray<E> var1, long var2) {
      int var4 = this.newBufferAndOffset(var1, var2);
      Object var5 = LinkedAtomicArrayQueueUtil.lvElement(var1, var4);
      if (var5 == null) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         LinkedAtomicArrayQueueUtil.soElement(var1, var4, (Object)null);
         this.soConsumerIndex(var2 + 2L);
         return var5;
      }
   }

   private E newBufferPeek(AtomicReferenceArray<E> var1, long var2) {
      int var4 = this.newBufferAndOffset(var1, var2);
      Object var5 = LinkedAtomicArrayQueueUtil.lvElement(var1, var4);
      if (null == var5) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         return var5;
      }
   }

   private int newBufferAndOffset(AtomicReferenceArray<E> var1, long var2) {
      this.consumerBuffer = var1;
      this.consumerMask = (long)(LinkedAtomicArrayQueueUtil.length(var1) - 2 << 1);
      int var4 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var2, this.consumerMask);
      return var4;
   }

   public long currentProducerIndex() {
      return this.lvProducerIndex() / 2L;
   }

   public long currentConsumerIndex() {
      return this.lvConsumerIndex() / 2L;
   }

   public abstract int capacity();

   public boolean relaxedOffer(E var1) {
      return this.offer(var1);
   }

   public E relaxedPoll() {
      AtomicReferenceArray var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      int var6 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var7 = LinkedAtomicArrayQueueUtil.lvElement(var1, var6);
      if (var7 == null) {
         return null;
      } else if (var7 == JUMP) {
         AtomicReferenceArray var8 = this.getNextBuffer(var1, var4);
         return this.newBufferPoll(var8, var2);
      } else {
         LinkedAtomicArrayQueueUtil.soElement(var1, var6, (Object)null);
         this.soConsumerIndex(var2 + 2L);
         return var7;
      }
   }

   public E relaxedPeek() {
      AtomicReferenceArray var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      int var6 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var7 = LinkedAtomicArrayQueueUtil.lvElement(var1, var6);
      return var7 == JUMP ? this.newBufferPeek(this.getNextBuffer(var1, var4), var2) : var7;
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

   public int fill(MessagePassingQueue.Supplier<E> var1, int var2) {
      while(true) {
         long var9 = this.lvProducerLimit();
         long var6 = this.lvProducerIndex();
         if ((var6 & 1L) != 1L) {
            long var3 = this.producerMask;
            AtomicReferenceArray var5 = this.producerBuffer;
            long var11 = Math.min(var9, var6 + (long)(2 * var2));
            if (var6 == var9 || var9 < var11) {
               int var13 = this.offerSlowPath(var3, var6, var9);
               switch(var13) {
               case 1:
                  continue;
               case 2:
                  return 0;
               case 3:
                  this.resize(var3, var5, var6, var1.get());
                  return 1;
               }
            }

            if (this.casProducerIndex(var6, var11)) {
               int var8 = (int)((var11 - var6) / 2L);
               boolean var14 = false;

               for(int var15 = 0; var15 < var8; ++var15) {
                  int var10 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var6 + (long)(2 * var15), var3);
                  LinkedAtomicArrayQueueUtil.soElement(var5, var10, var1.get());
               }

               return var8;
            }
         }
      }
   }

   public void fill(MessagePassingQueue.Supplier<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      while(var3.keepRunning()) {
         while(this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0 && var3.keepRunning()) {
         }

         for(int var4 = 0; var3.keepRunning() && this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH) == 0; var4 = var2.idle(var4)) {
         }
      }

   }

   public int drain(MessagePassingQueue.Consumer<E> var1) {
      return this.drain(var1, this.capacity());
   }

   public int drain(MessagePassingQueue.Consumer<E> var1, int var2) {
      int var3;
      Object var4;
      for(var3 = 0; var3 < var2 && (var4 = this.relaxedPoll()) != null; ++var3) {
         var1.accept(var4);
      }

      return var3;
   }

   public void drain(MessagePassingQueue.Consumer<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      int var4 = 0;

      while(var3.keepRunning()) {
         Object var5 = this.relaxedPoll();
         if (var5 == null) {
            var4 = var2.idle(var4);
         } else {
            var4 = 0;
            var1.accept(var5);
         }
      }

   }

   private void resize(long var1, AtomicReferenceArray<E> var3, long var4, E var6) {
      int var7 = this.getNextBufferSize(var3);
      AtomicReferenceArray var8 = LinkedAtomicArrayQueueUtil.allocate(var7);
      this.producerBuffer = var8;
      int var9 = var7 - 2 << 1;
      this.producerMask = (long)var9;
      int var10 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var4, var1);
      int var11 = LinkedAtomicArrayQueueUtil.modifiedCalcElementOffset(var4, (long)var9);
      LinkedAtomicArrayQueueUtil.soElement(var8, var11, var6);
      LinkedAtomicArrayQueueUtil.soElement(var3, this.nextArrayOffset(var1), var8);
      long var12 = this.lvConsumerIndex();
      long var14 = this.availableInQueue(var4, var12);
      RangeUtil.checkPositive(var14, "availableInQueue");
      this.soProducerLimit(var4 + Math.min((long)var9, var14));
      this.soProducerIndex(var4 + 2L);
      LinkedAtomicArrayQueueUtil.soElement(var3, var10, JUMP);
   }

   protected abstract int getNextBufferSize(AtomicReferenceArray<E> var1);

   protected abstract long getCurrentBufferCapacity(long var1);
}
