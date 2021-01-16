package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.util.Map;

public class EpollServerChannelConfig extends EpollChannelConfig implements ServerSocketChannelConfig {
   protected final AbstractEpollChannel channel;
   private volatile int backlog;
   private volatile int pendingFastOpenRequestsThreshold;

   EpollServerChannelConfig(AbstractEpollChannel var1) {
      super(var1);
      this.backlog = NetUtil.SOMAXCONN;
      this.channel = var1;
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG, EpollChannelOption.TCP_FASTOPEN});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == ChannelOption.SO_RCVBUF) {
         return this.getReceiveBufferSize();
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         return this.isReuseAddress();
      } else if (var1 == ChannelOption.SO_BACKLOG) {
         return this.getBacklog();
      } else {
         return var1 == EpollChannelOption.TCP_FASTOPEN ? this.getTcpFastopen() : super.getOption(var1);
      }
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress((Boolean)var2);
      } else if (var1 == ChannelOption.SO_BACKLOG) {
         this.setBacklog((Integer)var2);
      } else {
         if (var1 != EpollChannelOption.TCP_FASTOPEN) {
            return super.setOption(var1, var2);
         }

         this.setTcpFastopen((Integer)var2);
      }

      return true;
   }

   public boolean isReuseAddress() {
      try {
         return this.channel.socket.isReuseAddress();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerChannelConfig setReuseAddress(boolean var1) {
      try {
         this.channel.socket.setReuseAddress(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getReceiveBufferSize() {
      try {
         return this.channel.socket.getReceiveBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerChannelConfig setReceiveBufferSize(int var1) {
      try {
         this.channel.socket.setReceiveBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getBacklog() {
      return this.backlog;
   }

   public EpollServerChannelConfig setBacklog(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("backlog: " + var1);
      } else {
         this.backlog = var1;
         return this;
      }
   }

   public int getTcpFastopen() {
      return this.pendingFastOpenRequestsThreshold;
   }

   public EpollServerChannelConfig setTcpFastopen(int var1) {
      if (this.pendingFastOpenRequestsThreshold < 0) {
         throw new IllegalArgumentException("pendingFastOpenRequestsThreshold: " + var1);
      } else {
         this.pendingFastOpenRequestsThreshold = var1;
         return this;
      }
   }

   public EpollServerChannelConfig setPerformancePreferences(int var1, int var2, int var3) {
      return this;
   }

   public EpollServerChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollServerChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollServerChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollServerChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollServerChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollServerChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollServerChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public EpollServerChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   public EpollServerChannelConfig setEpollMode(EpollMode var1) {
      super.setEpollMode(var1);
      return this;
   }
}
