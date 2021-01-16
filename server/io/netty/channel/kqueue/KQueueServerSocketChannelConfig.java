package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.channel.unix.UnixChannelOption;
import java.io.IOException;
import java.util.Map;

public class KQueueServerSocketChannelConfig extends KQueueServerChannelConfig implements ServerSocketChannelConfig {
   KQueueServerSocketChannelConfig(KQueueServerSocketChannel var1) {
      super(var1);
      this.setReuseAddress(true);
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{UnixChannelOption.SO_REUSEPORT, KQueueChannelOption.SO_ACCEPTFILTER});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == UnixChannelOption.SO_REUSEPORT) {
         return this.isReusePort();
      } else {
         return var1 == KQueueChannelOption.SO_ACCEPTFILTER ? this.getAcceptFilter() : super.getOption(var1);
      }
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == UnixChannelOption.SO_REUSEPORT) {
         this.setReusePort((Boolean)var2);
      } else {
         if (var1 != KQueueChannelOption.SO_ACCEPTFILTER) {
            return super.setOption(var1, var2);
         }

         this.setAcceptFilter((AcceptFilter)var2);
      }

      return true;
   }

   public KQueueServerSocketChannelConfig setReusePort(boolean var1) {
      try {
         this.channel.socket.setReusePort(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isReusePort() {
      try {
         return this.channel.socket.isReusePort();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public KQueueServerSocketChannelConfig setAcceptFilter(AcceptFilter var1) {
      try {
         this.channel.socket.setAcceptFilter(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public AcceptFilter getAcceptFilter() {
      try {
         return this.channel.socket.getAcceptFilter();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public KQueueServerSocketChannelConfig setRcvAllocTransportProvidesGuess(boolean var1) {
      super.setRcvAllocTransportProvidesGuess(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setReuseAddress(boolean var1) {
      super.setReuseAddress(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setReceiveBufferSize(int var1) {
      super.setReceiveBufferSize(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3) {
      return this;
   }

   public KQueueServerSocketChannelConfig setBacklog(int var1) {
      super.setBacklog(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueServerSocketChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueServerSocketChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueServerSocketChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public KQueueServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }
}
