package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.unix.Limits;
import java.io.IOException;
import java.util.Map;

public class EpollChannelConfig extends DefaultChannelConfig {
   final AbstractEpollChannel channel;
   private volatile long maxBytesPerGatheringWrite;

   EpollChannelConfig(AbstractEpollChannel var1) {
      super(var1);
      this.maxBytesPerGatheringWrite = Limits.SSIZE_MAX;
      this.channel = var1;
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{EpollChannelOption.EPOLL_MODE});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      return var1 == EpollChannelOption.EPOLL_MODE ? this.getEpollMode() : super.getOption(var1);
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == EpollChannelOption.EPOLL_MODE) {
         this.setEpollMode((EpollMode)var2);
         return true;
      } else {
         return super.setOption(var1, var2);
      }
   }

   public EpollChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      if (!(var1.newHandle() instanceof RecvByteBufAllocator.ExtendedHandle)) {
         throw new IllegalArgumentException("allocator.newHandle() must return an object of type: " + RecvByteBufAllocator.ExtendedHandle.class);
      } else {
         super.setRecvByteBufAllocator(var1);
         return this;
      }
   }

   public EpollChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public EpollChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   public EpollMode getEpollMode() {
      return this.channel.isFlagSet(Native.EPOLLET) ? EpollMode.EDGE_TRIGGERED : EpollMode.LEVEL_TRIGGERED;
   }

   public EpollChannelConfig setEpollMode(EpollMode var1) {
      if (var1 == null) {
         throw new NullPointerException("mode");
      } else {
         try {
            switch(var1) {
            case EDGE_TRIGGERED:
               this.checkChannelNotRegistered();
               this.channel.setFlag(Native.EPOLLET);
               break;
            case LEVEL_TRIGGERED:
               this.checkChannelNotRegistered();
               this.channel.clearFlag(Native.EPOLLET);
               break;
            default:
               throw new Error();
            }

            return this;
         } catch (IOException var3) {
            throw new ChannelException(var3);
         }
      }
   }

   private void checkChannelNotRegistered() {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("EpollMode can only be changed before channel is registered");
      }
   }

   protected final void autoReadCleared() {
      this.channel.clearEpollIn();
   }

   final void setMaxBytesPerGatheringWrite(long var1) {
      this.maxBytesPerGatheringWrite = var1;
   }

   final long getMaxBytesPerGatheringWrite() {
      return this.maxBytesPerGatheringWrite;
   }
}
