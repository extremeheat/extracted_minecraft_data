package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;
import io.netty.util.internal.ObjectUtil;

public interface RecvByteBufAllocator {
   RecvByteBufAllocator.Handle newHandle();

   public static class DelegatingHandle implements RecvByteBufAllocator.Handle {
      private final RecvByteBufAllocator.Handle delegate;

      public DelegatingHandle(RecvByteBufAllocator.Handle var1) {
         super();
         this.delegate = (RecvByteBufAllocator.Handle)ObjectUtil.checkNotNull(var1, "delegate");
      }

      protected final RecvByteBufAllocator.Handle delegate() {
         return this.delegate;
      }

      public ByteBuf allocate(ByteBufAllocator var1) {
         return this.delegate.allocate(var1);
      }

      public int guess() {
         return this.delegate.guess();
      }

      public void reset(ChannelConfig var1) {
         this.delegate.reset(var1);
      }

      public void incMessagesRead(int var1) {
         this.delegate.incMessagesRead(var1);
      }

      public void lastBytesRead(int var1) {
         this.delegate.lastBytesRead(var1);
      }

      public int lastBytesRead() {
         return this.delegate.lastBytesRead();
      }

      public boolean continueReading() {
         return this.delegate.continueReading();
      }

      public int attemptedBytesRead() {
         return this.delegate.attemptedBytesRead();
      }

      public void attemptedBytesRead(int var1) {
         this.delegate.attemptedBytesRead(var1);
      }

      public void readComplete() {
         this.delegate.readComplete();
      }
   }

   public interface ExtendedHandle extends RecvByteBufAllocator.Handle {
      boolean continueReading(UncheckedBooleanSupplier var1);
   }

   /** @deprecated */
   @Deprecated
   public interface Handle {
      ByteBuf allocate(ByteBufAllocator var1);

      int guess();

      void reset(ChannelConfig var1);

      void incMessagesRead(int var1);

      void lastBytesRead(int var1);

      int lastBytesRead();

      void attemptedBytesRead(int var1);

      int attemptedBytesRead();

      boolean continueReading();

      void readComplete();
   }
}
