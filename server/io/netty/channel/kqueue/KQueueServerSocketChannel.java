package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.EventLoop;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.unix.NativeInetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class KQueueServerSocketChannel extends AbstractKQueueServerChannel implements ServerSocketChannel {
   private final KQueueServerSocketChannelConfig config;

   public KQueueServerSocketChannel() {
      super(BsdSocket.newSocketStream(), false);
      this.config = new KQueueServerSocketChannelConfig(this);
   }

   public KQueueServerSocketChannel(int var1) {
      this(new BsdSocket(var1));
   }

   KQueueServerSocketChannel(BsdSocket var1) {
      super(var1);
      this.config = new KQueueServerSocketChannelConfig(this);
   }

   KQueueServerSocketChannel(BsdSocket var1, boolean var2) {
      super(var1, var2);
      this.config = new KQueueServerSocketChannelConfig(this);
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof KQueueEventLoop;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      super.doBind(var1);
      this.socket.listen(this.config.getBacklog());
      this.active = true;
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public KQueueServerSocketChannelConfig config() {
      return this.config;
   }

   protected Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception {
      return new KQueueSocketChannel(this, new BsdSocket(var1), NativeInetAddress.address(var2, var3, var4));
   }
}
