package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public final class KQueueSocketChannel extends AbstractKQueueStreamChannel implements SocketChannel {
   private final KQueueSocketChannelConfig config = new KQueueSocketChannelConfig(this);

   public KQueueSocketChannel() {
      super((Channel)null, BsdSocket.newSocketStream(), false);
   }

   public KQueueSocketChannel(int var1) {
      super(new BsdSocket(var1));
   }

   KQueueSocketChannel(Channel var1, BsdSocket var2, InetSocketAddress var3) {
      super(var1, var2, var3);
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public KQueueSocketChannelConfig config() {
      return this.config;
   }

   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new KQueueSocketChannel.KQueueSocketChannelUnsafe();
   }

   private final class KQueueSocketChannelUnsafe extends AbstractKQueueStreamChannel.KQueueStreamUnsafe {
      private KQueueSocketChannelUnsafe() {
         super();
      }

      protected Executor prepareToClose() {
         try {
            if (KQueueSocketChannel.this.isOpen() && KQueueSocketChannel.this.config().getSoLinger() > 0) {
               ((KQueueEventLoop)KQueueSocketChannel.this.eventLoop()).remove(KQueueSocketChannel.this);
               return GlobalEventExecutor.INSTANCE;
            }
         } catch (Throwable var2) {
         }

         return null;
      }

      // $FF: synthetic method
      KQueueSocketChannelUnsafe(Object var2) {
         this();
      }
   }
}
