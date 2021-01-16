package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.unix.NativeInetAddress;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public final class EpollServerSocketChannel extends AbstractEpollServerChannel implements ServerSocketChannel {
   private final EpollServerSocketChannelConfig config;
   private volatile Collection<InetAddress> tcpMd5SigAddresses;

   public EpollServerSocketChannel() {
      super(LinuxSocket.newSocketStream(), false);
      this.tcpMd5SigAddresses = Collections.emptyList();
      this.config = new EpollServerSocketChannelConfig(this);
   }

   public EpollServerSocketChannel(int var1) {
      this(new LinuxSocket(var1));
   }

   EpollServerSocketChannel(LinuxSocket var1) {
      super(var1);
      this.tcpMd5SigAddresses = Collections.emptyList();
      this.config = new EpollServerSocketChannelConfig(this);
   }

   EpollServerSocketChannel(LinuxSocket var1, boolean var2) {
      super(var1, var2);
      this.tcpMd5SigAddresses = Collections.emptyList();
      this.config = new EpollServerSocketChannelConfig(this);
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof EpollEventLoop;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      super.doBind(var1);
      if (Native.IS_SUPPORTING_TCP_FASTOPEN && this.config.getTcpFastopen() > 0) {
         this.socket.setTcpFastOpen(this.config.getTcpFastopen());
      }

      this.socket.listen(this.config.getBacklog());
      this.active = true;
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public EpollServerSocketChannelConfig config() {
      return this.config;
   }

   protected Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception {
      return new EpollSocketChannel(this, new LinuxSocket(var1), NativeInetAddress.address(var2, var3, var4));
   }

   Collection<InetAddress> tcpMd5SigAddresses() {
      return this.tcpMd5SigAddresses;
   }

   void setTcpMd5Sig(Map<InetAddress, byte[]> var1) throws IOException {
      this.tcpMd5SigAddresses = TcpMd5Util.newTcpMd5Sigs(this, this.tcpMd5SigAddresses, var1);
   }
}
