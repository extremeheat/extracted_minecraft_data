package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;

public final class EpollSocketChannel extends AbstractEpollStreamChannel implements SocketChannel {
   private final EpollSocketChannelConfig config = new EpollSocketChannelConfig(this);
   private volatile Collection<InetAddress> tcpMd5SigAddresses = Collections.emptyList();

   public EpollSocketChannel() {
      super(LinuxSocket.newSocketStream(), false);
   }

   public EpollSocketChannel(int var1) {
      super(var1);
   }

   EpollSocketChannel(LinuxSocket var1, boolean var2) {
      super(var1, var2);
   }

   EpollSocketChannel(Channel var1, LinuxSocket var2, InetSocketAddress var3) {
      super(var1, var2, var3);
      if (var1 instanceof EpollServerSocketChannel) {
         this.tcpMd5SigAddresses = ((EpollServerSocketChannel)var1).tcpMd5SigAddresses();
      }

   }

   public EpollTcpInfo tcpInfo() {
      return this.tcpInfo(new EpollTcpInfo());
   }

   public EpollTcpInfo tcpInfo(EpollTcpInfo var1) {
      try {
         this.socket.getTcpInfo(var1);
         return var1;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public EpollSocketChannelConfig config() {
      return this.config;
   }

   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollSocketChannel.EpollSocketChannelUnsafe();
   }

   void setTcpMd5Sig(Map<InetAddress, byte[]> var1) throws IOException {
      this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, var1);
   }

   private final class EpollSocketChannelUnsafe extends AbstractEpollStreamChannel.EpollStreamUnsafe {
      private EpollSocketChannelUnsafe() {
         super();
      }

      protected Executor prepareToClose() {
         try {
            if (EpollSocketChannel.this.isOpen() && EpollSocketChannel.this.config().getSoLinger() > 0) {
               ((EpollEventLoop)EpollSocketChannel.this.eventLoop()).remove(EpollSocketChannel.this);
               return GlobalEventExecutor.INSTANCE;
            }
         } catch (Throwable var2) {
         }

         return null;
      }

      // $FF: synthetic method
      EpollSocketChannelUnsafe(Object var2) {
         this();
      }
   }
}
