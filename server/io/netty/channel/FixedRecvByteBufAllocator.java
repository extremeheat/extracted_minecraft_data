package io.netty.channel;

public class FixedRecvByteBufAllocator extends DefaultMaxMessagesRecvByteBufAllocator {
   private final int bufferSize;

   public FixedRecvByteBufAllocator(int var1) {
      super();
      if (var1 <= 0) {
         throw new IllegalArgumentException("bufferSize must greater than 0: " + var1);
      } else {
         this.bufferSize = var1;
      }
   }

   public RecvByteBufAllocator.Handle newHandle() {
      return new FixedRecvByteBufAllocator.HandleImpl(this.bufferSize);
   }

   public FixedRecvByteBufAllocator respectMaybeMoreData(boolean var1) {
      super.respectMaybeMoreData(var1);
      return this;
   }

   private final class HandleImpl extends DefaultMaxMessagesRecvByteBufAllocator.MaxMessageHandle {
      private final int bufferSize;

      public HandleImpl(int var2) {
         super();
         this.bufferSize = var2;
      }

      public int guess() {
         return this.bufferSize;
      }
   }
}
