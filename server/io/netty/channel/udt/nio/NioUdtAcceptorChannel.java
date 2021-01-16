package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.ServerSocketChannelUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.udt.DefaultUdtServerChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtServerChannel;
import io.netty.channel.udt.UdtServerChannelConfig;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;

/** @deprecated */
@Deprecated
public abstract class NioUdtAcceptorChannel extends AbstractNioMessageChannel implements UdtServerChannel {
   protected static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtAcceptorChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private final UdtServerChannelConfig config;

   protected NioUdtAcceptorChannel(ServerSocketChannelUDT var1) {
      super((Channel)null, var1, 16);

      try {
         var1.configureBlocking(false);
         this.config = new DefaultUdtServerChannelConfig(this, var1, true);
      } catch (Exception var5) {
         try {
            var1.close();
         } catch (Exception var4) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to close channel.", (Throwable)var4);
            }
         }

         throw new ChannelException("Failed to configure channel.", var5);
      }
   }

   protected NioUdtAcceptorChannel(TypeUDT var1) {
      this(NioUdtProvider.newAcceptorChannelUDT(var1));
   }

   public UdtServerChannelConfig config() {
      return this.config;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.javaChannel().socket().bind(var1, this.config.getBacklog());
   }

   protected void doClose() throws Exception {
      this.javaChannel().close();
   }

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected void doDisconnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   protected void doFinishConnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   protected boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected final Object filterOutboundMessage(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   public boolean isActive() {
      return this.javaChannel().socket().isBound();
   }

   protected ServerSocketChannelUDT javaChannel() {
      return (ServerSocketChannelUDT)super.javaChannel();
   }

   protected SocketAddress localAddress0() {
      return SocketUtils.localSocketAddress(this.javaChannel().socket());
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return null;
   }

   protected SocketAddress remoteAddress0() {
      return null;
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected int doReadMessages(List<Object> var1) throws Exception {
      SocketChannelUDT var2 = (SocketChannelUDT)SocketUtils.accept(this.javaChannel());
      if (var2 == null) {
         return 0;
      } else {
         var1.add(this.newConnectorChannel(var2));
         return 1;
      }
   }

   protected abstract UdtChannel newConnectorChannel(SocketChannelUDT var1);
}
