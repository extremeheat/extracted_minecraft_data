package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.ServerDomainSocketChannel;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.SocketAddress;

public final class EpollServerDomainSocketChannel extends AbstractEpollServerChannel implements ServerDomainSocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollServerDomainSocketChannel.class);
   private final EpollServerChannelConfig config = new EpollServerChannelConfig(this);
   private volatile DomainSocketAddress local;

   public EpollServerDomainSocketChannel() {
      super(LinuxSocket.newSocketDomain(), false);
   }

   public EpollServerDomainSocketChannel(int var1) {
      super(var1);
   }

   EpollServerDomainSocketChannel(LinuxSocket var1) {
      super(var1);
   }

   EpollServerDomainSocketChannel(LinuxSocket var1, boolean var2) {
      super(var1, var2);
   }

   protected Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception {
      return new EpollDomainSocketChannel(this, new Socket(var1));
   }

   protected DomainSocketAddress localAddress0() {
      return this.local;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.socket.bind(var1);
      this.socket.listen(this.config.getBacklog());
      this.local = (DomainSocketAddress)var1;
      this.active = true;
   }

   protected void doClose() throws Exception {
      boolean var9 = false;

      try {
         var9 = true;
         super.doClose();
         var9 = false;
      } finally {
         if (var9) {
            DomainSocketAddress var5 = this.local;
            if (var5 != null) {
               File var6 = new File(var5.path());
               boolean var7 = var6.delete();
               if (!var7 && logger.isDebugEnabled()) {
                  logger.debug("Failed to delete a domain socket file: {}", (Object)var5.path());
               }
            }

         }
      }

      DomainSocketAddress var1 = this.local;
      if (var1 != null) {
         File var2 = new File(var1.path());
         boolean var3 = var2.delete();
         if (!var3 && logger.isDebugEnabled()) {
            logger.debug("Failed to delete a domain socket file: {}", (Object)var1.path());
         }
      }

   }

   public EpollServerChannelConfig config() {
      return this.config;
   }

   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }
}
