package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.ServerSocketChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

public final class EpollServerSocketChannelConfig extends EpollServerChannelConfig implements ServerSocketChannelConfig {
   EpollServerSocketChannelConfig(EpollServerSocketChannel var1) {
      super(var1);
      this.setReuseAddress(true);
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_FREEBIND, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.TCP_DEFER_ACCEPT});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == EpollChannelOption.SO_REUSEPORT) {
         return this.isReusePort();
      } else if (var1 == EpollChannelOption.IP_FREEBIND) {
         return this.isFreeBind();
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         return this.isIpTransparent();
      } else {
         return var1 == EpollChannelOption.TCP_DEFER_ACCEPT ? this.getTcpDeferAccept() : super.getOption(var1);
      }
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == EpollChannelOption.SO_REUSEPORT) {
         this.setReusePort((Boolean)var2);
      } else if (var1 == EpollChannelOption.IP_FREEBIND) {
         this.setFreeBind((Boolean)var2);
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         this.setIpTransparent((Boolean)var2);
      } else if (var1 == EpollChannelOption.TCP_MD5SIG) {
         Map var3 = (Map)var2;
         this.setTcpMd5Sig(var3);
      } else {
         if (var1 != EpollChannelOption.TCP_DEFER_ACCEPT) {
            return super.setOption(var1, var2);
         }

         this.setTcpDeferAccept((Integer)var2);
      }

      return true;
   }

   public EpollServerSocketChannelConfig setReuseAddress(boolean var1) {
      super.setReuseAddress(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setReceiveBufferSize(int var1) {
      super.setReceiveBufferSize(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3) {
      return this;
   }

   public EpollServerSocketChannelConfig setBacklog(int var1) {
      super.setBacklog(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollServerSocketChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollServerSocketChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollServerSocketChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   public EpollServerSocketChannelConfig setTcpMd5Sig(Map<InetAddress, byte[]> var1) {
      try {
         ((EpollServerSocketChannel)this.channel).setTcpMd5Sig(var1);
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

   public EpollServerSocketChannelConfig setReusePort(boolean var1) {
      try {
         this.channel.socket.setReusePort(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isFreeBind() {
      try {
         return this.channel.socket.isIpFreeBind();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollServerSocketChannelConfig setFreeBind(boolean var1) {
      try {
         this.channel.socket.setIpFreeBind(var1);
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

   public EpollServerSocketChannelConfig setIpTransparent(boolean var1) {
      try {
         this.channel.socket.setIpTransparent(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public EpollServerSocketChannelConfig setTcpDeferAccept(int var1) {
      try {
         this.channel.socket.setTcpDeferAccept(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getTcpDeferAccept() {
      try {
         return this.channel.socket.getTcpDeferAccept();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }
}
