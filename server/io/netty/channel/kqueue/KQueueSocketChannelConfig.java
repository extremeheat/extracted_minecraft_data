package io.netty.channel.kqueue;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.util.Map;

public final class KQueueSocketChannelConfig extends KQueueChannelConfig implements SocketChannelConfig {
   private final KQueueSocketChannel channel;
   private volatile boolean allowHalfClosure;

   KQueueSocketChannelConfig(KQueueSocketChannel var1) {
      super(var1);
      this.channel = var1;
      if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
         this.setTcpNoDelay(true);
      }

      this.calculateMaxBytesPerGatheringWrite();
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE, KQueueChannelOption.SO_SNDLOWAT, KQueueChannelOption.TCP_NOPUSH});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == ChannelOption.SO_RCVBUF) {
         return this.getReceiveBufferSize();
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         return this.getSendBufferSize();
      } else if (var1 == ChannelOption.TCP_NODELAY) {
         return this.isTcpNoDelay();
      } else if (var1 == ChannelOption.SO_KEEPALIVE) {
         return this.isKeepAlive();
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         return this.isReuseAddress();
      } else if (var1 == ChannelOption.SO_LINGER) {
         return this.getSoLinger();
      } else if (var1 == ChannelOption.IP_TOS) {
         return this.getTrafficClass();
      } else if (var1 == ChannelOption.ALLOW_HALF_CLOSURE) {
         return this.isAllowHalfClosure();
      } else if (var1 == KQueueChannelOption.SO_SNDLOWAT) {
         return this.getSndLowAt();
      } else {
         return var1 == KQueueChannelOption.TCP_NOPUSH ? this.isTcpNoPush() : super.getOption(var1);
      }
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.TCP_NODELAY) {
         this.setTcpNoDelay((Boolean)var2);
      } else if (var1 == ChannelOption.SO_KEEPALIVE) {
         this.setKeepAlive((Boolean)var2);
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress((Boolean)var2);
      } else if (var1 == ChannelOption.SO_LINGER) {
         this.setSoLinger((Integer)var2);
      } else if (var1 == ChannelOption.IP_TOS) {
         this.setTrafficClass((Integer)var2);
      } else if (var1 == ChannelOption.ALLOW_HALF_CLOSURE) {
         this.setAllowHalfClosure((Boolean)var2);
      } else if (var1 == KQueueChannelOption.SO_SNDLOWAT) {
         this.setSndLowAt((Integer)var2);
      } else {
         if (var1 != KQueueChannelOption.TCP_NOPUSH) {
            return super.setOption(var1, var2);
         }

         this.setTcpNoPush((Boolean)var2);
      }

      return true;
   }

   public int getReceiveBufferSize() {
      try {
         return this.channel.socket.getReceiveBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getSendBufferSize() {
      try {
         return this.channel.socket.getSendBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getSoLinger() {
      try {
         return this.channel.socket.getSoLinger();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getTrafficClass() {
      try {
         return this.channel.socket.getTrafficClass();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public boolean isKeepAlive() {
      try {
         return this.channel.socket.isKeepAlive();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public boolean isReuseAddress() {
      try {
         return this.channel.socket.isReuseAddress();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public boolean isTcpNoDelay() {
      try {
         return this.channel.socket.isTcpNoDelay();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getSndLowAt() {
      try {
         return this.channel.socket.getSndLowAt();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public void setSndLowAt(int var1) {
      try {
         this.channel.socket.setSndLowAt(var1);
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isTcpNoPush() {
      try {
         return this.channel.socket.isTcpNoPush();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public void setTcpNoPush(boolean var1) {
      try {
         this.channel.socket.setTcpNoPush(var1);
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public KQueueSocketChannelConfig setKeepAlive(boolean var1) {
      try {
         this.channel.socket.setKeepAlive(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public KQueueSocketChannelConfig setReceiveBufferSize(int var1) {
      try {
         this.channel.socket.setReceiveBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public KQueueSocketChannelConfig setReuseAddress(boolean var1) {
      try {
         this.channel.socket.setReuseAddress(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public KQueueSocketChannelConfig setSendBufferSize(int var1) {
      try {
         this.channel.socket.setSendBufferSize(var1);
         this.calculateMaxBytesPerGatheringWrite();
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public KQueueSocketChannelConfig setSoLinger(int var1) {
      try {
         this.channel.socket.setSoLinger(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public KQueueSocketChannelConfig setTcpNoDelay(boolean var1) {
      try {
         this.channel.socket.setTcpNoDelay(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public KQueueSocketChannelConfig setTrafficClass(int var1) {
      try {
         this.channel.socket.setTrafficClass(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isAllowHalfClosure() {
      return this.allowHalfClosure;
   }

   public KQueueSocketChannelConfig setRcvAllocTransportProvidesGuess(boolean var1) {
      super.setRcvAllocTransportProvidesGuess(var1);
      return this;
   }

   public KQueueSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3) {
      return this;
   }

   public KQueueSocketChannelConfig setAllowHalfClosure(boolean var1) {
      this.allowHalfClosure = var1;
      return this;
   }

   public KQueueSocketChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueSocketChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public KQueueSocketChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public KQueueSocketChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public KQueueSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public KQueueSocketChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   public KQueueSocketChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueSocketChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public KQueueSocketChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public KQueueSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public KQueueSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   private void calculateMaxBytesPerGatheringWrite() {
      int var1 = this.getSendBufferSize() << 1;
      if (var1 > 0) {
         this.setMaxBytesPerGatheringWrite((long)(this.getSendBufferSize() << 1));
      }

   }
}
