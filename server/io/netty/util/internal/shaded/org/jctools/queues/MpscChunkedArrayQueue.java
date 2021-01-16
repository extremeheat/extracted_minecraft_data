package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.Pow2;

public class MpscChunkedArrayQueue<E> extends MpscChunkedArrayQueueColdProducerFields<E> {
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

   public MpscChunkedArrayQueue(int var1) {
      super(Math.max(2, Math.min(1024, Pow2.roundToPowerOfTwo(var1 / 8))), var1);
   }

   public MpscChunkedArrayQueue(int var1, int var2) {
      super(var1, var2);
   }

   protected long availableInQueue(long var1, long var3) {
      return this.maxQueueCapacity - (var1 - var3);
   }

   public int capacity() {
      return (int)(this.maxQueueCapacity / 2L);
   }

   protected int getNextBufferSize(E[] var1) {
      return LinkedArrayQueueUtil.length(var1);
   }

   protected long getCurrentBufferCapacity(long var1) {
      return var1;
   }
}
