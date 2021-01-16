package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.DatagramChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

public final class EpollDatagramChannelConfig extends EpollChannelConfig implements DatagramChannelConfig {
   private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator(2048);
   private final EpollDatagramChannel datagramChannel;
   private boolean activeOnOpen;

   EpollDatagramChannelConfig(EpollDatagramChannel var1) {
      super(var1);
      this.datagramChannel = var1;
      this.setRecvByteBufAllocator(DEFAULT_RCVBUF_ALLOCATOR);
   }

   public Map<ChannelOption<?>, Object> getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, EpollChannelOption.SO_REUSEPORT, EpollChannelOption.IP_TRANSPARENT, EpollChannelOption.IP_RECVORIGDSTADDR});
   }

   public <T> T getOption(ChannelOption<T> var1) {
      if (var1 == ChannelOption.SO_BROADCAST) {
         return this.isBroadcast();
      } else if (var1 == ChannelOption.SO_RCVBUF) {
         return this.getReceiveBufferSize();
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         return this.getSendBufferSize();
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         return this.isReuseAddress();
      } else if (var1 == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
         return this.isLoopbackModeDisabled();
      } else if (var1 == ChannelOption.IP_MULTICAST_ADDR) {
         return this.getInterface();
      } else if (var1 == ChannelOption.IP_MULTICAST_IF) {
         return this.getNetworkInterface();
      } else if (var1 == ChannelOption.IP_MULTICAST_TTL) {
         return this.getTimeToLive();
      } else if (var1 == ChannelOption.IP_TOS) {
         return this.getTrafficClass();
      } else if (var1 == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         return this.activeOnOpen;
      } else if (var1 == EpollChannelOption.SO_REUSEPORT) {
         return this.isReusePort();
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         return this.isIpTransparent();
      } else {
         return var1 == EpollChannelOption.IP_RECVORIGDSTADDR ? this.isIpRecvOrigDestAddr() : super.getOption(var1);
      }
   }

   public <T> boolean setOption(ChannelOption<T> var1, T var2) {
      this.validate(var1, var2);
      if (var1 == ChannelOption.SO_BROADCAST) {
         this.setBroadcast((Boolean)var2);
      } else if (var1 == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize((Integer)var2);
      } else if (var1 == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress((Boolean)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
         this.setLoopbackModeDisabled((Boolean)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_ADDR) {
         this.setInterface((InetAddress)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_IF) {
         this.setNetworkInterface((NetworkInterface)var2);
      } else if (var1 == ChannelOption.IP_MULTICAST_TTL) {
         this.setTimeToLive((Integer)var2);
      } else if (var1 == ChannelOption.IP_TOS) {
         this.setTrafficClass((Integer)var2);
      } else if (var1 == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         this.setActiveOnOpen((Boolean)var2);
      } else if (var1 == EpollChannelOption.SO_REUSEPORT) {
         this.setReusePort((Boolean)var2);
      } else if (var1 == EpollChannelOption.IP_TRANSPARENT) {
         this.setIpTransparent((Boolean)var2);
      } else {
         if (var1 != EpollChannelOption.IP_RECVORIGDSTADDR) {
            return super.setOption(var1, var2);
         }

         this.setIpRecvOrigDestAddr((Boolean)var2);
      }

      return true;
   }

   private void setActiveOnOpen(boolean var1) {
      if (this.channel.isRegistered()) {
         throw new IllegalStateException("Can only changed before channel was registered");
      } else {
         this.activeOnOpen = var1;
      }
   }

   boolean getActiveOnOpen() {
      return this.activeOnOpen;
   }

   public EpollDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1) {
      super.setMessageSizeEstimator(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollDatagramChannelConfig setWriteBufferLowWaterMark(int var1) {
      super.setWriteBufferLowWaterMark(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollDatagramChannelConfig setWriteBufferHighWaterMark(int var1) {
      super.setWriteBufferHighWaterMark(var1);
      return this;
   }

   public EpollDatagramChannelConfig setWriteBufferWaterMark(WriteBufferWaterMark var1) {
      super.setWriteBufferWaterMark(var1);
      return this;
   }

   public EpollDatagramChannelConfig setAutoClose(boolean var1) {
      super.setAutoClose(var1);
      return this;
   }

   public EpollDatagramChannelConfig setAutoRead(boolean var1) {
      super.setAutoRead(var1);
      return this;
   }

   public EpollDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1) {
      super.setRecvByteBufAllocator(var1);
      return this;
   }

   public EpollDatagramChannelConfig setWriteSpinCount(int var1) {
      super.setWriteSpinCount(var1);
      return this;
   }

   public EpollDatagramChannelConfig setAllocator(ByteBufAllocator var1) {
      super.setAllocator(var1);
      return this;
   }

   public EpollDatagramChannelConfig setConnectTimeoutMillis(int var1) {
      super.setConnectTimeoutMillis(var1);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public EpollDatagramChannelConfig setMaxMessagesPerRead(int var1) {
      super.setMaxMessagesPerRead(var1);
      return this;
   }

   public int getSendBufferSize() {
      try {
         return this.datagramChannel.socket.getSendBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setSendBufferSize(int var1) {
      try {
         this.datagramChannel.socket.setSendBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getReceiveBufferSize() {
      try {
         return this.datagramChannel.socket.getReceiveBufferSize();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReceiveBufferSize(int var1) {
      try {
         this.datagramChannel.socket.setReceiveBufferSize(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getTrafficClass() {
      try {
         return this.datagramChannel.socket.getTrafficClass();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setTrafficClass(int var1) {
      try {
         this.datagramChannel.socket.setTrafficClass(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isReuseAddress() {
      try {
         return this.datagramChannel.socket.isReuseAddress();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReuseAddress(boolean var1) {
      try {
         this.datagramChannel.socket.setReuseAddress(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isBroadcast() {
      try {
         return this.datagramChannel.socket.isBroadcast();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setBroadcast(boolean var1) {
      try {
         this.datagramChannel.socket.setBroadcast(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isLoopbackModeDisabled() {
      return false;
   }

   public DatagramChannelConfig setLoopbackModeDisabled(boolean var1) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public int getTimeToLive() {
      return -1;
   }

   public EpollDatagramChannelConfig setTimeToLive(int var1) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public InetAddress getInterface() {
      return null;
   }

   public EpollDatagramChannelConfig setInterface(InetAddress var1) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public NetworkInterface getNetworkInterface() {
      return null;
   }

   public EpollDatagramChannelConfig setNetworkInterface(NetworkInterface var1) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public EpollDatagramChannelConfig setEpollMode(EpollMode var1) {
      super.setEpollMode(var1);
      return this;
   }

   public boolean isReusePort() {
      try {
         return this.datagramChannel.socket.isReusePort();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setReusePort(boolean var1) {
      try {
         this.datagramChannel.socket.setReusePort(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpTransparent() {
      try {
         return this.datagramChannel.socket.isIpTransparent();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setIpTransparent(boolean var1) {
      try {
         this.datagramChannel.socket.setIpTransparent(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isIpRecvOrigDestAddr() {
      try {
         return this.datagramChannel.socket.isIpRecvOrigDestAddr();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public EpollDatagramChannelConfig setIpRecvOrigDestAddr(boolean var1) {
      try {
         this.datagramChannel.socket.setIpRecvOrigDestAddr(var1);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }
}
