package io.netty.channel.sctp.oio;

import com.sun.nio.sctp.SctpChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.sctp.DefaultSctpServerChannelConfig;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class OioSctpServerChannel extends AbstractOioMessageChannel implements SctpServerChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSctpServerChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 1);
   private final com.sun.nio.sctp.SctpServerChannel sch;
   private final SctpServerChannelConfig config;
   private final Selector selector;

   private static com.sun.nio.sctp.SctpServerChannel newServerSocket() {
      try {
         return com.sun.nio.sctp.SctpServerChannel.open();
      } catch (IOException var1) {
         throw new ChannelException("failed to create a sctp server channel", var1);
      }
   }

   public OioSctpServerChannel() {
      this(newServerSocket());
   }

   public OioSctpServerChannel(com.sun.nio.sctp.SctpServerChannel var1) {
      super((Channel)null);
      if (var1 == null) {
         throw new NullPointerException("sctp server channel");
      } else {
         this.sch = var1;
         boolean var2 = false;

         try {
            var1.configureBlocking(false);
            this.selector = Selector.open();
            var1.register(this.selector, 16);
            this.config = new OioSctpServerChannel.OioSctpServerChannelConfig(this, var1);
            var2 = true;
         } catch (Exception var11) {
            throw new ChannelException("failed to initialize a sctp server channel", var11);
         } finally {
            if (!var2) {
               try {
                  var1.close();
               } catch (IOException var10) {
                  logger.warn("Failed to close a sctp server channel.", (Throwable)var10);
               }
            }

         }

      }
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public SctpServerChannelConfig config() {
      return this.config;
   }

   public InetSocketAddress remoteAddress() {
      return null;
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public boolean isOpen() {
      return this.sch.isOpen();
   }

   protected SocketAddress localAddress0() {
      try {
         Iterator var1 = this.sch.getAllLocalAddresses().iterator();
         if (var1.hasNext()) {
            return (SocketAddress)var1.next();
         }
      } catch (IOException var2) {
      }

      return null;
   }

   public Set<InetSocketAddress> allLocalAddresses() {
      try {
         Set var1 = this.sch.getAllLocalAddresses();
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

   public boolean isActive() {
      return this.isOpen() && this.localAddress0() != null;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.sch.bind(var1, this.config.getBacklog());
   }

   protected void doClose() throws Exception {
      try {
         this.selector.close();
      } catch (IOException var2) {
         logger.warn("Failed to close a selector.", (Throwable)var2);
      }

      this.sch.close();
   }

   protected int doReadMessages(List<Object> var1) throws Exception {
      if (!this.isActive()) {
         return -1;
      } else {
         SctpChannel var2 = null;
         int var3 = 0;

         try {
            int var4 = this.selector.select(1000L);
            if (var4 > 0) {
               Iterator var5 = this.selector.selectedKeys().iterator();

               do {
                  SelectionKey var6 = (SelectionKey)var5.next();
                  var5.remove();
                  if (var6.isAcceptable()) {
                     var2 = this.sch.accept();
                     if (var2 != null) {
                        var1.add(new OioSctpChannel(this, var2));
                        ++var3;
                     }
                  }
               } while(var5.hasNext());

               return var3;
            }
         } catch (Throwable var8) {
            logger.warn("Failed to create a new channel from an accepted sctp channel.", var8);
            if (var2 != null) {
               try {
                  var2.close();
               } catch (Throwable var7) {
                  logger.warn("Failed to close a sctp channel.", var7);
               }
            }
         }

         return var3;
      }
   }

   public ChannelFuture bindAddress(InetAddress var1) {
      return this.bindAddress(var1, this.newPromise());
   }

   public ChannelFuture bindAddress(final InetAddress var1, final ChannelPromise var2) {
      if (this.eventLoop().inEventLoop()) {
         try {
            this.sch.bindAddress(var1);
            var2.setSuccess();
         } catch (Throwable var4) {
            var2.setFailure(var4);
         }
      } else {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               OioSctpServerChannel.this.bindAddress(var1, var2);
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
            this.sch.unbindAddress(var1);
            var2.setSuccess();
         } catch (Throwable var4) {
            var2.setFailure(var4);
         }
      } else {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               OioSctpServerChannel.this.unbindAddress(var1, var2);
            }
         });
      }

      return var2;
   }

   protected void doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected SocketAddress remoteAddress0() {
      return null;
   }

   protected void doDisconnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected Object filterOutboundMessage(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   private final class OioSctpServerChannelConfig extends DefaultSctpServerChannelConfig {
      private OioSctpServerChannelConfig(OioSctpServerChannel var2, com.sun.nio.sctp.SctpServerChannel var3) {
         super(var2, var3);
      }

      protected void autoReadCleared() {
         OioSctpServerChannel.this.clearReadPending();
      }

      // $FF: synthetic method
      OioSctpServerChannelConfig(OioSctpServerChannel var2, com.sun.nio.sctp.SctpServerChannel var3, Object var4) {
         this(var2, var3);
      }
   }
}
