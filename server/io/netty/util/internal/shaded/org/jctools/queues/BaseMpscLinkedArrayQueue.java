package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.util.Iterator;

public abstract class BaseMpscLinkedArrayQueue<E> extends BaseMpscLinkedArrayQueueColdProducerFields<E> implements MessagePassingQueue<E>, QueueProgressIndicators {
   private static final Object JUMP = new Object();
   private static final int CONTINUE_TO_P_INDEX_CAS = 0;
   private static final int RETRY = 1;
   private static final int QUEUE_FULL = 2;
   private static final int QUEUE_RESIZE = 3;

   public BaseMpscLinkedArrayQueue(int var1) {
      super();
      RangeUtil.checkGreaterThanOrEqual(var1, 2, "initialCapacity");
      int var2 = Pow2.roundToPowerOfTwo(var1);
      long var3 = (long)(var2 - 1 << 1);
      Object[] var5 = CircularArrayOffsetCalculator.allocate(var2 + 1);
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
                  Object[] var4 = this.producerBuffer;
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
                     var7 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var5, var2);
                     UnsafeRefArrayAccess.soElement(var4, var7, var1);
                     return true;
                  }
               }
            }
         }
      }
   }

   public E poll() {
      Object[] var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvElement(var1, var6);
      if (var8 == null) {
         if (var2 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var8 = UnsafeRefArrayAccess.lvElement(var1, var6);
         } while(var8 == null);
      }

      if (var8 == JUMP) {
         Object[] var9 = this.getNextBuffer(var1, var4);
         return this.newBufferPoll(var9, var2);
      } else {
         UnsafeRefArrayAccess.soElement(var1, var6, (Object)null);
         this.soConsumerIndex(var2 + 2L);
         return var8;
      }
   }

   public E peek() {
      Object[] var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvElement(var1, var6);
      if (var8 == null && var2 != this.lvProducerIndex()) {
         do {
            var8 = UnsafeRefArrayAccess.lvElement(var1, var6);
         } while(var8 == null);
      }

      return var8 == JUMP ? this.newBufferPeek(this.getNextBuffer(var1, var4), var2) : var8;
   }

   private int offerSlowPath(long var1, long var3, long var5) {
      long var7 = this.lvConsumerIndex();
      long var9 = this.getCurrentBufferCapacity(var1);
      if (var7 + var9 > var3) {
         return !this.casProducerLimit(var5, var7 + var9) ? 1 : 0;
      } else if (this.availableInQueue(var3, var7) <= 0L) {
         return 2;
      } else {
         return this.casProducerIndex(var3, var3 + 1L) ? 3 : 1;
      }
   }

   protected abstract long availableInQueue(long var1, long var3);

   private E[] getNextBuffer(E[] var1, long var2) {
      long var4 = this.nextArrayOffset(var2);
      Object[] var6 = (Object[])UnsafeRefArrayAccess.lvElement(var1, var4);
      UnsafeRefArrayAccess.soElement(var1, var4, (Object)null);
      return var6;
   }

   private long nextArrayOffset(long var1) {
      return LinkedArrayQueueUtil.modifiedCalcElementOffset(var1 + 2L, 9223372036854775807L);
   }

   private E newBufferPoll(E[] var1, long var2) {
      long var4 = this.newBufferAndOffset(var1, var2);
      Object var6 = UnsafeRefArrayAccess.lvElement(var1, var4);
      if (var6 == null) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         UnsafeRefArrayAccess.soElement(var1, var4, (Object)null);
         this.soConsumerIndex(var2 + 2L);
         return var6;
      }
   }

   private E newBufferPeek(E[] var1, long var2) {
      long var4 = this.newBufferAndOffset(var1, var2);
      Object var6 = UnsafeRefArrayAccess.lvElement(var1, var4);
      if (null == var6) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         return var6;
      }
   }

   private long newBufferAndOffset(E[] var1, long var2) {
      this.consumerBuffer = var1;
      this.consumerMask = (long)(LinkedArrayQueueUtil.length(var1) - 2 << 1);
      return LinkedArrayQueueUtil.modifiedCalcElementOffset(var2, this.consumerMask);
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
      Object[] var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvElement(var1, var6);
      if (var8 == null) {
         return null;
      } else if (var8 == JUMP) {
         Object[] var9 = this.getNextBuffer(var1, var4);
         return this.newBufferPoll(var9, var2);
      } else {
         UnsafeRefArrayAccess.soElement(var1, var6, (Object)null);
         this.soConsumerIndex(var2 + 2L);
         return var8;
      }
   }

   public E relaxedPeek() {
      Object[] var1 = this.consumerBuffer;
      long var2 = this.consumerIndex;
      long var4 = this.consumerMask;
      long var6 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var2, var4);
      Object var8 = UnsafeRefArrayAccess.lvElement(var1, var6);
      return var8 == JUMP ? this.newBufferPeek(this.getNextBuffer(var1, var4), var2) : var8;
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
            Object[] var5 = this.producerBuffer;
            long var11 = Math.min(var9, var6 + (long)(2 * var2));
            if (var6 >= var9 || var9 < var11) {
               int var13 = this.offerSlowPath(var3, var6, var9);
               switch(var13) {
               case 0:
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

               for(int var14 = 0; var14 < var8; ++var14) {
                  long var10 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var6 + (long)(2 * var14), var3);
                  UnsafeRefArrayAccess.soElement(var5, var10, var1.get());
               }

               return var8;
            }
         }
      }
   }

   public void fill(MessagePassingQueue.Supplier<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      label22:
      while(true) {
         if (var3.keepRunning()) {
            if (this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0) {
               continue;
            }

            int var4 = 0;

            while(true) {
               if (!var3.keepRunning() || this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH) != 0) {
                  continue label22;
               }

               var4 = var2.idle(var4);
            }
         }

         return;
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

   private void resize(long var1, E[] var3, long var4, E var6) {
      int var7 = this.getNextBufferSize(var3);
      Object[] var8 = CircularArrayOffsetCalculator.allocate(var7);
      this.producerBuffer = var8;
      int var9 = var7 - 2 << 1;
      this.producerMask = (long)var9;
      long var10 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var4, var1);
      long var12 = LinkedArrayQueueUtil.modifiedCalcElementOffset(var4, (long)var9);
      UnsafeRefArrayAccess.soElement(var8, var12, var6);
      UnsafeRefArrayAccess.soElement(var3, this.nextArrayOffset(var1), var8);
      long var14 = this.lvConsumerIndex();
      long var16 = this.availableInQueue(var4, var14);
      RangeUtil.checkPositive(var16, "availableInQueue");
      this.soProducerLimit(var4 + Math.min((long)var9, var16));
      this.soProducerIndex(var4 + 2L);
      UnsafeRefArrayAccess.soElement(var3, var10, JUMP);
   }

   protected abstract int getNextBufferSize(E[] var1);

   protected abstract long getCurrentBufferCapacity(long var1);
}
