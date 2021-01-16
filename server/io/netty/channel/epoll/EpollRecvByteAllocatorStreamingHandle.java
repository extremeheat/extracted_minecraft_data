package io.netty.channel.epoll;

import io.netty.channel.RecvByteBufAllocator;

final class EpollRecvByteAllocatorStreamingHandle extends EpollRecvByteAllocatorHandle {
   public EpollRecvByteAllocatorStreamingHandle(RecvByteBufAllocator.ExtendedHandle var1) {
      super(var1);
   }

   boolean maybeMoreDataToRead() {
      return this.lastBytesRead() == this.attemptedBytesRead() || this.isReceivedRdHup();
   }
}
