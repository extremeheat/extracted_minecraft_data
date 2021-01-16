package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public class MpscArrayQueue<E> extends MpscArrayQueueL3Pad<E> {
   public MpscArrayQueue(int var1) {
      super(var1);
   }

   public boolean offerIfBelowThreshold(E var1, int var2) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         long var3 = this.mask;
         long var5 = var3 + 1L;
         long var7 = this.lvProducerLimit();

         long var9;
         long var11;
         do {
            var9 = this.lvProducerIndex();
            var11 = var7 - var9;
            long var13 = var5 - var11;
            if (var13 >= (long)var2) {
               long var15 = this.lvConsumerIndex();
               var13 = var9 - var15;
               if (var13 >= (long)var2) {
                  return false;
               }

               var7 = var15 + var5;
               this.soProducerLimit(var7);
            }
         } while(!this.casProducerIndex(var9, var9 + 1L));

         var11 = calcElementOffset(var9, var3);
         UnsafeRefArrayAccess.soElement(this.buffer, var11, var1);
         return true;
      }
   }

   public boolean offer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         long var2 = this.mask;
         long var4 = this.lvProducerLimit();

         long var6;
         long var8;
         do {
            var6 = this.lvProducerIndex();
            if (var6 >= var4) {
               var8 = this.lvConsumerIndex();
               var4 = var8 + var2 + 1L;
               if (var6 >= var4) {
                  return false;
               }

               this.soProducerLimit(var4);
            }
         } while(!this.casProducerIndex(var6, var6 + 1L));

         var8 = calcElementOffset(var6, var2);
         UnsafeRefArrayAccess.soElement(this.buffer, var8, var1);
         return true;
      }
   }

   public final int failFastOffer(E var1) {
      if (null == var1) {
         throw new NullPointerException();
      } else {
         long var2 = this.mask;
         long var4 = var2 + 1L;
         long var6 = this.lvProducerIndex();
         long var8 = this.lvProducerLimit();
         long var10;
         if (var6 >= var8) {
            var10 = this.lvConsumerIndex();
            var8 = var10 + var4;
            if (var6 >= var8) {
               return 1;
            }

            this.soProducerLimit(var8);
         }

         if (!this.casProducerIndex(var6, var6 + 1L)) {
            return -1;
         } else {
            var10 = calcElementOffset(var6, var2);
            UnsafeRefArrayAccess.soElement(this.buffer, var10, var1);
            return 0;
         }
      }
   }

   public E poll() {
      long var1 = this.lpConsumerIndex();
      long var3 = this.calcElementOffset(var1);
      Object[] var5 = this.buffer;
      Object var6 = UnsafeRefArrayAccess.lvElement(var5, var3);
      if (null == var6) {
         if (var1 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var6 = UnsafeRefArrayAccess.lvElement(var5, var3);
         } while(var6 == null);
      }

      UnsafeRefArrayAccess.spElement(var5, var3, (Object)null);
      this.soConsumerIndex(var1 + 1L);
      return var6;
   }

   public E peek() {
      Object[] var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      long var4 = this.calcElementOffset(var2);
      Object var6 = UnsafeRefArrayAccess.lvElement(var1, var4);
      if (null == var6) {
         if (var2 == this.lvProducerIndex()) {
            return null;
         }

         do {
            var6 = UnsafeRefArrayAccess.lvElement(var1, var4);
         } while(var6 == null);
      }

      return var6;
   }

   public boolean relaxedOffer(E var1) {
      return this.offer(var1);
   }

   public E relaxedPoll() {
      Object[] var1 = this.buffer;
      long var2 = this.lpConsumerIndex();
      long var4 = this.calcElementOffset(var2);
      Object var6 = UnsafeRefArrayAccess.lvElement(var1, var4);
      if (null == var6) {
         return null;
      } else {
         UnsafeRefArrayAccess.spElement(var1, var4, (Object)null);
         this.soConsumerIndex(var2 + 1L);
         return var6;
      }
   }

   public E relaxedPeek() {
      Object[] var1 = this.buffer;
      long var2 = this.mask;
      long var4 = this.lpConsumerIndex();
      return UnsafeRefArrayAccess.lvElement(var1, calcElementOffset(var4, var2));
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
      Object[] var3 = this.buffer;
      long var4 = this.mask;
      long var6 = this.lpConsumerIndex();

      for(int var8 = 0; var8 < var2; ++var8) {
         long var9 = var6 + (long)var8;
         long var11 = calcElementOffset(var9, var4);
         Object var13 = UnsafeRefArrayAccess.lvElement(var3, var11);
         if (null == var13) {
            return var8;
         }

         UnsafeRefArrayAccess.spElement(var3, var11, (Object)null);
         this.soConsumerIndex(var9 + 1L);
         var1.accept(var13);
      }

      return var2;
   }

   public int fill(MessagePassingQueue.Supplier<E> var1, int var2) {
      long var3 = this.mask;
      long var5 = var3 + 1L;
      long var7 = this.lvProducerLimit();
      boolean var11 = false;

      long var9;
      long var14;
      int var16;
      do {
         var9 = this.lvProducerIndex();
         long var12 = var7 - var9;
         if (var12 <= 0L) {
            var14 = this.lvConsumerIndex();
            var7 = var14 + var5;
            var12 = var7 - var9;
            if (var12 <= 0L) {
               return 0;
            }

            this.soProducerLimit(var7);
         }

         var16 = Math.min((int)var12, var2);
      } while(!this.casProducerIndex(var9, var9 + (long)var16));

      Object[] var17 = this.buffer;

      for(int var13 = 0; var13 < var16; ++var13) {
         var14 = calcElementOffset(var9 + (long)var13, var3);
         UnsafeRefArrayAccess.soElement(var17, var14, var1.get());
      }

      return var16;
   }

   public void drain(MessagePassingQueue.Consumer<E> var1, MessagePassingQueue.WaitStrategy var2, MessagePassingQueue.ExitCondition var3) {
      Object[] var4 = this.buffer;
      long var5 = this.mask;
      long var7 = this.lpConsumerIndex();
      int var9 = 0;

      while(var3.keepRunning()) {
         for(int var10 = 0; var10 < 4096; ++var10) {
            long var11 = calcElementOffset(var7, var5);
            Object var13 = UnsafeRefArrayAccess.lvElement(var4, var11);
            if (null == var13) {
               var9 = var2.idle(var9);
            } else {
               ++var7;
               var9 = 0;
               UnsafeRefArrayAccess.spElement(var4, var11, (Object)null);
               this.soConsumerIndex(var7);
               var1.accept(var13);
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
}
