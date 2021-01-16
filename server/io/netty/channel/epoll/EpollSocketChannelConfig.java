package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollSocketChannelConfig extends EpollChannelConfig implements SocketChannelConfig {
   private final EpollSocketChannel channel;
   private volatile boolean allowHalfClosure;

   EpollSocketChannelConfig(EpollSocketChannel var1) {
      super(var1);
      this.channel = var1;
      if (PlatformDependent.canEnableTcpNoDelayByDefault()) {
         this.setTcpNoDelay(true);
      }

      this.calculateMaxBytesPerGatheringWrite();
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE, EpollChannelOption.TCP_CORK, EpollChannelOption.TCP_NOTSENT_LOWAT, EpollChannelOption.TCP_KEEPCNT, EpollChannelOption.TCP_KEEPIDLE, EpollChannelOption.TCP_KEEPINTVL, EpollChannelOption.TCP_MD5SIG, EpollChannelOption.TCP_QUICKACK, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.TCP_FASTOPEN_CONNECT});
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
      } else if (var1 == EpollChannelOption.TCP_CORK) {
         return this.isTcpCork();
      } else if (var1 == EpollChannelOption.TCP_NOTSENT_LOWAT) {
         return this.getTcpNotSentLowAt();
      } else if (var1 == EpollChannelOption.TCP_KEEPIDLE) {
         return this.getTcpKeepIdle();
      } else if (var1 == EpollChannelOption.TCP_KEEPINTVL) {
         return this.getTcpKeepIntvl();
      } else if (var1 == EpollChannelOption.TCP_KEEPCNT) {
         return this.getTcpKeepCnt();
      } else if (var1 == EpollChannelOption.TCP_USER_TIMEOUT) {
         return this.getTcpUserTimeout();
      } else if (var1 == EpollChannelOption.TCP_QUICKACK) {
         return this.isTcpQuickAck();
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         return this.isIpTransparent();
      } else {
         return var1 == EpollChannelOption.TCP_FASTOPEN_CONNECT ? this.isTcpFastOpenConnect() : super.getOption(var1);
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
      } else if (var1 == EpollChannelOption.TCP_CORK) {
         this.setTcpCork((Boolean)var2);
      } else if (var1 == EpollChannelOption.TCP_NOTSENT_LOWAT) {
         this.setTcpNotSentLowAt((Long)var2);
      } else if (var1 == EpollChannelOption.TCP_KEEPIDLE) {
         this.setTcpKeepIdle((Integer)var2);
      } else if (var1 == EpollChannelOption.TCP_KEEPCNT) {
         this.setTcpKeepCnt((Integer)var2);
      } else if (var1 == EpollChannelOption.TCP_KEEPINTVL) {
         this.setTcpKeepIntvl((Integer)var2);
      } else if (var1 == EpollChannelOption.TCP_USER_TIMEOUT) {
         this.setTcpUserTimeout((Integer)var2);
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         this.setIpTransparent((Boolean)var2);
      } else if (var1 == EpollChannelOption.TCP_MD5SIG) {
         Map var3 = (Map)var2;
         this.setTcpMd5Sig(var3);
      } else if (var1 == EpollChannelOption.TCP_QUICKACK) {
         this.setTcpQuickAck((Boolean)var2);
      } else {
         if (var1 != EpollChannelOption.TCP_FASTOPEN_CONNECT) {
            return super.setOption(var1, var2);
         }

         this.setTcpFastOpenConnect((Boolean)var2);
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

   public boolean isTcpCork() {
      try {
         return this.channel.socket.isTcpCork();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public long getTcpNotSentLowAt() {
      try {
         return this.channel.socket.getTcpNotSentLowAt();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getTcpKeepIdle() {
      try {
         return this.channel.socket.getTcpKeepIdle();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getTcpKeepIntvl() {
      try {
         return this.channel.socket.getTcpKeepIntvl();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getTcpKeepCnt() {
      try {
         return this.channel.socket.getTcpKeepCnt();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getTcpUserTimeout() {
      try {
         return this.channel.socket.getTcpUserTimeout();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollSocketChannelConfig setKeepAlive(boolean var1) {
      try {
         this.channel.socket.setKeepAlive(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3) {
      return this;
   }

   public EpollSocketChannelConfig setReceiveBufferSize(int var1) {
      try {
         this.channel.socket.setReceiveBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setReuseAddress(boolean var1) {
      try {
         this.channel.socket.setReuseAddress(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setSendBufferSize(int var1) {
      try {
         this.channel.socket.setSendBufferSize(var1);
         this.calculateMaxBytesPerGatheringWrite();
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setSoLinger(int var1) {
      try {
         this.channel.socket.setSoLinger(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpNoDelay(boolean var1) {
      try {
         this.channel.socket.setTcpNoDelay(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpCork(boolean var1) {
      try {
         this.channel.socket.setTcpCork(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpNotSentLowAt(long var1) {
      try {
         this.channel.socket.setTcpNotSentLowAt(var1);
         return this;
      } catch (IOException var4) {
         throw new ChannelException(var4);
      }
   }

   public EpollSocketChannelConfig setTrafficClass(int var1) {
      try {
         this.channel.socket.setTrafficClass(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpKeepIdle(int var1) {
      try {
         this.channel.socket.setTcpKeepIdle(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpKeepIntvl(int var1) {
      try {
         this.channel.socket.setTcpKeepIntvl(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   /** @deprecated */
   @Deprecated
   public EpollSocketChannelConfig setTcpKeepCntl(int var1) {
      return this.setTcpKeepCnt(var1);
   }

   public EpollSocketChannelConfig setTcpKeepCnt(int var1) {
      try {
         this.channel.socket.setTcpKeepCnt(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpUserTimeout(int var1) {
      try {
         this.channel.socket.setTcpUserTimeout(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpTransparent() {
      try {
         return this.channel.socket.isIpTransparent();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollSocketChannelConfig setIpTransparent(boolean var1) {
      try {
         this.channel.socket.setIpTransparent(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> var1) {
      try {
         this.channel.setTcpMd5Sig(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollSocketChannelConfig setTcpQuickAck(boolean var1) {
      try {
         this.channel.socket.setTcpQuickAck(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isTcpQuickAck() {
      try {
         return this.channel.socket.isTcpQuickAck();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollSocketChannelConfig setTcpFastOpenConnect(boolean var1) {
      try {
         this.channel.socket.setTcpFastOpenConnect(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isTcpFastOpenConnect() {
      try {
         return this.channel.socket.isTcpFastOpenConnect();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public boolean isAllowHalfClosure() {
      return this.allowHalfClosure;
   }

   public EpollSocketChannelConfig setAllowHalfClosure(boolean var1) {
      this.allowHalfClosure = var1;
      return this;
   }

   public EpollSocketChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollSocketChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollSocketChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollSocketChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollSocketChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   public EpollSocketChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollSocketChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollSocketChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public EpollSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   public EpollSocketChannelConfig setEpollMode(EpollMode var1) {
      super.setEpollMode(var1);
      return this;
   }

   private void calculateMaxBytesPerGatheringWrite() {
      int var1 = this.getSendBufferSize() << 1;
      if (var1 > 0) {
         this.setMaxBytesPerGatheringWrite((long)(this.getSendBufferSize() << 1));
      }

   }
}
