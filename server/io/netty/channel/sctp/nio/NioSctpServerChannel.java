package io.netty.channel.sctp.nio;

import com.sun.nio.sctp.SctpChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.sctp.DefaultSctpServerChannelConfig;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NioSctpServerChannel extends AbstractNioMessageChannel implements SctpServerChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private final SctpServerChannelConfig config = new NioSctpServerChannel.NioSctpServerChannelConfig(this, this.javaChannel());

   private static com.sun.nio.sctp.SctpServerChannel newSocket() {
      try {
         return com.sun.nio.sctp.SctpServerChannel.open();
      } catch (IOException var1) {
         throw new ChannelException("Failed to open a server socket.", var1);
      }
   }

   public NioSctpServerChannel() {
      super((Channel)null, newSocket(), 16);
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public Set<InetSocketAddress> allLocalAddresses() {
      try {
         Set var1 = this.javaChannel().getAllLocalAddresses();
         LinkedHashSet var2 = new LinkedHashSet(var1.size());
         Iterator var3 = var1.iterator();

         while(var3.hasNext()) {
            SocketAddress var4 = (SocketAddress)var3.next();
            var2.add((InetSocketAddress)var4);
         }

         return var2;
      } catch (Throwable var5) {
         return Collections.emptySet();
      }
   }

   public SctpServerChannelConfig config() {
      return this.config;
   }

   public boolean isActive() {
      return this.isOpen() && !this.allLocalAddresses().isEmpty();
   }

   public InetSocketAddress remoteAddress() {
      return null;
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   protected com.sun.nio.sctp.SctpServerChannel javaChannel() {
      return (com.sun.nio.sctp.SctpServerChannel)super.javaChannel();
   }

   protected SocketAddress localAddress0() {
      try {
         Iterator var1 = this.javaChannel().getAllLocalAddresses().iterator();
         if (var1.hasNext()) {
            return (SocketAddress)var1.next();
         }
      } catch (IOException var2) {
      }

      return null;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.javaChannel().bind(var1, this.config.getBacklog());
   }

   protected void doClose() throws Exception {
      this.javaChannel().close();
   }

   protected int doReadMessages(List<Object> var1) throws Exception {
      SctpChannel var2 = this.javaChannel().accept();
      if (var2 == null) {
         return 0;
      } else {
         var1.add(new NioSctpChannel(this, var2));
         return 1;
      }
   }

   public ChannelFuture bindAddress(InetAddress var1) {
      return this.bindAddress(var1, this.newPromise());
   }

   public ChannelFuture bindAddress(final InetAddress var1, final ChannelPromise var2) {
      if (this.eventLoop().inEventLoop()) {
         try {
            this.javaChannel().bindAddress(var1);
            var2.setSuccess();
         } catch (Throwable var4) {
            var2.setFailure(var4);
         }
      } else {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               NioSctpServerChannel.this.bindAddress(var1, var2);
            }
         });
      }

      return var2;
   }

   public ChannelFuture unbindAddress(InetAddress var1) {
      return this.unbindAddress(var1, this.newPromise());
   }

   public ChannelFuture unbindAddress(final InetAddress var1, final ChannelPromise var2) {
      if (this.eventLoop().inEventLoop()) {
         try {
            this.javaChannel().unbindAddress(var1);
            var2.setSuccess();
         } catch (Throwable var4) {
            var2.setFailure(var4);
         }
      } else {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               NioSctpServerChannel.this.unbindAddress(var1, var2);
            }
         });
      }

      return var2;
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

   protected Object filterOutboundMessage(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   private final class NioSctpServerChannelConfig extends DefaultSctpServerChannelConfig {
      private NioSctpServerChannelConfig(NioSctpServerChannel var2, com.sun.nio.sctp.SctpServerChannel var3) {
         super(var2, var3);
      }

      protected void autoReadCleared() {
         NioSctpServerChannel.this.clearReadPending();
      }

      // $FF: synthetic method
      NioSctpServerChannelConfig(NioSctpServerChannel var2, com.sun.nio.sctp.SctpServerChannel var3, Object var4) {
         this(var2, var3);
      }
   }
}
