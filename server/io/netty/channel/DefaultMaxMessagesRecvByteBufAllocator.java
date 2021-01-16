package io.netty.channel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.UncheckedBooleanSupplier;

public abstract class DefaultMaxMessagesRecvByteBufAllocator implements MaxMessagesRecvByteBufAllocator {
   private volatile int maxMessagesPerRead;
   private volatile boolean respectMaybeMoreData;

   public DefaultMaxMessagesRecvByteBufAllocator() {
      this(1);
   }

   public DefaultMaxMessagesRecvByteBufAllocator(int var1) {
      super();
      this.respectMaybeMoreData = true;
      this.maxMessagesPerRead(var1);
   }

   public int maxMessagesPerRead() {
      return this.maxMessagesPerRead;
   }

   public MaxMessagesRecvByteBufAllocator maxMessagesPerRead(int var1) {
      if (var1 <= 0) {
         throw new IllegalArgumentException("maxMessagesPerRead: " + var1 + " (expected: > 0)");
      } else {
         this.maxMessagesPerRead = var1;
         return this;
      }
   }

   public DefaultMaxMessagesRecvByteBufAllocator respectMaybeMoreData(boolean var1) {
      this.respectMaybeMoreData = var1;
      return this;
   }

   public final boolean respectMaybeMoreData() {
      return this.respectMaybeMoreData;
   }

   public abstract class MaxMessageHandle implements RecvByteBufAllocator.ExtendedHandle {
      private ChannelConfig config;
      private int maxMessagePerRead;
      private int totalMessages;
      private int totalBytesRead;
      private int attemptedBytesRead;
      private int lastBytesRead;
      private final boolean respectMaybeMoreData;
      private final UncheckedBooleanSupplier defaultMaybeMoreSupplier;

      public MaxMessageHandle() {
         super();
         this.respectMaybeMoreData = DefaultMaxMessagesRecvByteBufAllocator.this.respectMaybeMoreData;
         this.defaultMaybeMoreSupplier = new UncheckedBooleanSupplier() {
            public boolean get() {
               return MaxMessageHandle.this.attemptedBytesRead == MaxMessageHandle.this.lastBytesRead;
            }
         };
      }

      public void reset(ChannelConfig var1) {
         this.config = var1;
         this.maxMessagePerRead = DefaultMaxMessagesRecvByteBufAllocator.this.maxMessagesPerRead();
         this.totalMessages = this.totalBytesRead = 0;
      }

      public ByteBuf allocate(ByteBufAllocator var1) {
         return var1.ioBuffer(this.guess());
      }

      public final void incMessagesRead(int var1) {
         this.totalMessages += var1;
      }

      public void lastBytesRead(int var1) {
         this.lastBytesRead = var1;
         if (var1 > 0) {
            this.totalBytesRead += var1;
         }

      }

      public final int lastBytesRead() {
         return this.lastBytesRead;
      }

      public boolean continueReading() {
         return this.continueReading(this.defaultMaybeMoreSupplier);
      }

      public boolean continueReading(UncheckedBooleanSupplier var1) {
         return this.config.isAutoRead() && (!this.respectMaybeMoreData || var1.get()) && this.totalMessages < this.maxMessagePerRead && this.totalBytesRead > 0;
      }

      public void readComplete() {
      }

      public int attemptedBytesRead() {
         return this.attemptedBytesRead;
      }

      public void attemptedBytesRead(int var1) {
         this.attemptedBytesRead = var1;
      }

      protected final int totalBytesRead() {
         return this.totalBytesRead < 0 ? 2147483647 : this.totalBytesRead;
      }
   }
}
