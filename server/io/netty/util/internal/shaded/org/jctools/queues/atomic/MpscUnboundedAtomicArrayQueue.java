package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscUnboundedAtomicArrayQueue<E> extends BaseMpscLinkedAtomicArrayQueue<E> {
   long p0;
   long p1;
   long p2;
   long p3;
   long p4;
   long p5;
   long p6;
   long p7;
   long p10;
   long p11;
   long p12;
   long p13;
   long p14;
   long p15;
   long p16;
   long p17;

   public MpscUnboundedAtomicArrayQueue(int var1) {
      super(var1);
   }

   protected long availableInQueue(long var1, long var3) {
      return 2147483647L;
   }

   public int capacity() {
      return -1;
   }

   public int drain(MessagePassingQueue.Consumer<E> var1) {
      return this.drain(var1, 4096);
   }

   public int fill(MessagePassingQueue.Supplier<E> var1) {
      long var2 = 0L;
      boolean var4 = true;

      do {
         int var5 = this.fill(var1, PortableJvmInfo.RECOMENDED_OFFER_BATCH);
         if (var5 == 0) {
            return (int)var2;
         }

         var2 += (long)var5;
      } while(var2 <= 4096L);

      return (int)var2;
   }

   protected int getNextBufferSize(AtomicReferenceArray<E> var1) {
      return LinkedAtomicArrayQueueUtil.length(var1);
   }

   protected long getCurrentBufferCapacity(long var1) {
      return var1;
   }
}
