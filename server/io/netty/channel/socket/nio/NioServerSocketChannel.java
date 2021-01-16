package io.netty.channel.socket.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.List;

public class NioServerSocketChannel extends AbstractNioMessageChannel implements ServerSocketChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioServerSocketChannel.class);
   private final ServerSocketChannelConfig config;

   private static java.nio.channels.ServerSocketChannel newSocket(SelectorProvider var0) {
      try {
         return var0.openServerSocketChannel();
      } catch (IOException var2) {
         throw new ChannelException("Failed to open a server socket.", var2);
      }
   }

   public NioServerSocketChannel() {
      this(newSocket(DEFAULT_SELECTOR_PROVIDER));
   }

   public NioServerSocketChannel(SelectorProvider var1) {
      this(newSocket(var1));
   }

   public NioServerSocketChannel(java.nio.channels.ServerSocketChannel var1) {
      super((Channel)null, var1, 16);
      this.config = new NioServerSocketChannel.NioServerSocketChannelConfig(this, this.javaChannel().socket());
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public ServerSocketChannelConfig config() {
      return this.config;
   }

   public boolean isActive() {
      return this.javaChannel().socket().isBound();
   }

   public InetSocketAddress remoteAddress() {
      return null;
   }

   protected java.nio.channels.ServerSocketChannel javaChannel() {
      return (java.nio.channels.ServerSocketChannel)super.javaChannel();
   }

   protected SocketAddress localAddress0() {
      return SocketUtils.localSocketAddress(this.javaChannel().socket());
   }

   protected void doBind(SocketAddress var1) throws Exception {
      if (PlatformDependent.javaVersion() >= 7) {
         this.javaChannel().bind(var1, this.config.getBacklog());
      } else {
         this.javaChannel().socket().bind(var1, this.config.getBacklog());
      }

   }

   protected void doClose() throws Exception {
      this.javaChannel().close();
   }

   protected int doReadMessages(List<Object> var1) throws Exception {
      SocketChannel var2 = SocketUtils.accept(this.javaChannel());

      try {
         if (var2 != null) {
            var1.add(new NioSocketChannel(this, var2));
            return 1;
         }
      } catch (Throwable var6) {
         logger.warn("Failed to create a new channel from an accepted socket.", var6);

         try {
            var2.close();
         } catch (Throwable var5) {
            logger.warn("Failed to close a socket.", var5);
         }
      }

      return 0;
   }

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected void doFinishConnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   protected SocketAddress remoteAddress0() {
      return null;
   }

   protected void doDisconnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   protected boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected final Object filterOutboundMessage(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected boolean closeOnReadError(Throwable var1) {
      return super.closeOnReadError(var1);
   }

   private final class NioServerSocketChannelConfig extends DefaultServerSocketChannelConfig {
      private NioServerSocketChannelConfig(NioServerSocketChannel var2, ServerSocket var3) {
         super(var2, var3);
      }

      protected void autoReadCleared() {
         NioServerSocketChannel.this.clearReadPending();
      }

      // $FF: synthetic method
      NioServerSocketChannelConfig(NioServerSocketChannel var2, ServerSocket var3, Object var4) {
         this(var2, var3);
      }
   }
}
