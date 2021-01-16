package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;

final class KQueueRecvByteAllocatorHandle implements RecvByteBufAllocator.ExtendedHandle {
   private final RecvByteBufAllocator.ExtendedHandle delegate;
   private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
      public boolean get() {
         return KQueueRecvByteAllocatorHandle.this.maybeMoreDataToRead();
      }
   };
   private boolean overrideGuess;
   private boolean readEOF;
   private long numberBytesPending;

   KQueueRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle var1) {
      super();
      this.delegate = (RecvByteBufAllocator.ExtendedHandle)ObjectUtil.checkNotNull(var1, "handle");
   }

   public int guess() {
      return this.overrideGuess ? this.guess0() : this.delegate.guess();
   }

   public void reset(ChannelConfig var1) {
      this.overrideGuess = ((KQueueChannelConfig)var1).getRcvAllocTransportProvidesGuess();
      this.delegate.reset(var1);
   }

   public void incMessagesRead(int var1) {
      this.delegate.incMessagesRead(var1);
   }

   public ByteBuf allocate(ByteBufAllocator var1) {
      return this.overrideGuess ? var1.ioBuffer(this.guess0()) : this.delegate.allocate(var1);
   }

   public void lastBytesRead(int var1) {
      this.numberBytesPending = var1 < 0 ? 0L : Math.max(0L, this.numberBytesPending - (long)var1);
      this.delegate.lastBytesRead(var1);
   }

   public int lastBytesRead() {
      return this.delegate.lastBytesRead();
   }

   public void attemptedBytesRead(int var1) {
      this.delegate.attemptedBytesRead(var1);
   }

   public int attemptedBytesRead() {
      return this.delegate.attemptedBytesRead();
   }

   public void readComplete() {
      this.delegate.readComplete();
   }

   public boolean continueReading(UncheckedBooleanSupplier var1) {
      return this.delegate.continueReading(var1);
   }

   public boolean continueReading() {
      return this.delegate.continueReading(this.defaultMaybeMoreDataSupplier);
   }

   void readEOF() {
      this.readEOF = true;
   }

   void numberBytesPending(long var1) {
      this.numberBytesPending = var1;
   }

   boolean maybeMoreDataToRead() {
      return this.numberBytesPending != 0L || this.readEOF;
   }

   private int guess0() {
      return (int)Math.min(this.numberBytesPending, 2147483647L);
   }
}
