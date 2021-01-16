package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.ServerDomainSocketChannel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.net.SocketAddress;

public final class KQueueServerDomainSocketChannel extends AbstractKQueueServerChannel implements ServerDomainSocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(KQueueServerDomainSocketChannel.class);
   private final KQueueServerChannelConfig config;
   private volatile DomainSocketAddress local;

   public KQueueServerDomainSocketChannel() {
      super(BsdSocket.newSocketDomain(), false);
      this.config = new KQueueServerChannelConfig(this);
   }

   public KQueueServerDomainSocketChannel(int var1) {
      this(new BsdSocket(var1), false);
   }

   KQueueServerDomainSocketChannel(BsdSocket var1, boolean var2) {
      super(var1, var2);
      this.config = new KQueueServerChannelConfig(this);
   }

   protected Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception {
      return new KQueueDomainSocketChannel(this, new BsdSocket(var1));
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

   public KQueueServerChannelConfig config() {
      return this.config;
   }

   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }
}
