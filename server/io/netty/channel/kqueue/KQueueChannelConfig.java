package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.Limits;
import java.util.Map;

public class KQueueChannelConfig extends DefaultChannelConfig {
   final AbstractKQueueChannel channel;
   private volatile boolean transportProvidesGuess;
   private volatile long maxBytesPerGatheringWrite;

   KQueueChannelConfig(AbstractKQueueChannel var1) {
      super(var1);
      this.maxBytesPerGatheringWrite = Limits.SSIZE_MAX;
      this.channel = var1;
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      return var1 == KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS ? this.getRcvAllocTransportProvidesGuess() : super.getOption(var1);
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == KQueueChannelOption.RCV_ALLOC_TRANSPORT_PROVIDES_GUESS) {
         this.setRcvAllocTransportProvidesGuess((Boolean)var2);
         return true;
      } else {
         return super.setOption(var1, var2);
      }
   }

   public KQueueChannelConfig setRcvAllocTransportProvidesGuess(boolean var1) {
      this.transportProvidesGuess = var1;
      return this;
   }

   public boolean getRcvAllocTransportProvidesGuess() {
      return this.transportProvidesGuess;
   }

   public KQueueChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public KQueueChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public KQueueChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public KQueueChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      if (!(var1.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
         throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
      } else {
         super.setRecvByteBufAllocator(var1);
         return this;
      }
   }

   public KQueueChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public KQueueChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public KQueueChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   protected final void autoReadCleared() {
      this.channel.clearReadFilter();
   }

   final void setMaxBytesPerGatheringWrite(long var1) {
      this.maxBytesPerGatheringWrite = Math.min(Limits.SSIZE_MAX, var1);
   }

   final long getMaxBytesPerGatheringWrite() {
      return this.maxBytesPerGatheringWrite;
   }
}
