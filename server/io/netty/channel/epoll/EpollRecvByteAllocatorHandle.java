package io.netty.channel.epoll;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;

class EpollRecvByteAllocatorHandle implements RecvByteBufAllocator.ExtendedHandle {
   private final RecvByteBufAllocator.ExtendedHandle delegate;
   private final UncheckedBooleanSupplier defaultMaybeMoreDataSupplier = new UncheckedBooleanSupplier() {
      public boolean get() {
         return EpollRecvByteAllocatorHandle.this.maybeMoreDataToRead();
      }
   };
   private boolean isEdgeTriggered;
   private boolean receivedRdHup;

   EpollRecvByteAllocatorHandle(RecvByteBufAllocator.ExtendedHandle var1) {
      super();
      this.delegate = (RecvByteBufAllocator.ExtendedHandle)ObjectUtil.checkNotNull(var1, "handle");
   }

   final void receivedRdHup() {
      this.receivedRdHup = true;
   }

   final boolean isReceivedRdHup() {
      return this.receivedRdHup;
   }

   boolean maybeMoreDataToRead() {
      return this.isEdgeTriggered && this.lastBytesRead() > 0 || !this.isEdgeTriggered && this.lastBytesRead() == this.attemptedBytesRead() || this.receivedRdHup;
   }

   final void edgeTriggered(boolean var1) {
      this.isEdgeTriggered = var1;
   }

   final boolean isEdgeTriggered() {
      return this.isEdgeTriggered;
   }

   public final ByteBuf allocate(ByteBufAllocator var1) {
      return this.delegate.allocate(var1);
   }

   public final int guess() {
      return this.delegate.guess();
   }

   public final void reset(ChannelConfig var1) {
      this.delegate.reset(var1);
   }

   public final void incMessagesRead(int var1) {
      this.delegate.incMessagesRead(var1);
   }

   public final void lastBytesRead(int var1) {
      this.delegate.lastBytesRead(var1);
   }

   public final int lastBytesRead() {
      return this.delegate.lastBytesRead();
   }

   public final int attemptedBytesRead() {
      return this.delegate.attemptedBytesRead();
   }

   public final void attemptedBytesRead(int var1) {
      this.delegate.attemptedBytesRead(var1);
   }

   public final void readComplete() {
      this.delegate.readComplete();
   }

   public final boolean continueReading(UncheckedBooleanSupplier var1) {
      return this.delegate.continueReading(var1);
   }

   public final boolean continueReading() {
      return this.delegate.continueReading(this.defaultMaybeMoreDataSupplier);
   }
}
