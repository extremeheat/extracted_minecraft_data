package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.channels.NotYetConnectedException;
import java.util.List;
import java.util.Locale;

public class OioDatagramChannel extends AbstractOioMessageChannel implements DatagramChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioDatagramChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(SocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
   private final MulticastSocket socket;
   private final OioDatagramChannelConfig config;
   private final java.net.DatagramPacket tmpPacket;

   private static MulticastSocket newSocket() {
      try {
         return new MulticastSocket((SocketAddress)null);
      } catch (Exception var1) {
         throw new ChannelException("failed to create a new socket", var1);
      }
   }

   public OioDatagramChannel() {
      this(newSocket());
   }

   public OioDatagramChannel(MulticastSocket var1) {
      super((Channel)null);
      this.tmpPacket = new java.net.DatagramPacket(EmptyArrays.EMPTY_BYTES, 0);
      boolean var2 = false;

      try {
         var1.setSoTimeout(1000);
         var1.setBroadcast(false);
         var2 = true;
      } catch (SocketException var7) {
         throw new ChannelException("Failed to configure the datagram socket timeout.", var7);
      } finally {
         if (!var2) {
            var1.close();
         }

      }

      this.socket = var1;
      this.config = new DefaultOioDatagramChannelConfig(this, var1);
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public DatagramChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return !this.socket.isClosed();
   }

   public boolean isActive() {
      return this.isOpen() && ((Boolean)this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) && this.isRegistered() || this.socket.isBound());
   }

   public boolean isConnected() {
      return this.socket.isConnected();
   }

   protected SocketAddress localAddress0() {
      return this.socket.getLocalSocketAddress();
   }

   protected SocketAddress remoteAddress0() {
      return this.socket.getRemoteSocketAddress();
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.socket.bind(var1);
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   protected void doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (var2 != null) {
         this.socket.bind(var2);
      }

      boolean var3 = false;

      try {
         this.socket.connect(var1);
         var3 = true;
      } finally {
         if (!var3) {
            try {
               this.socket.close();
            } catch (Throwable var10) {
               logger.warn("Failed to close a socket.", var10);
            }
         }

      }

   }

   protected void doDisconnect() throws Exception {
      this.socket.disconnect();
   }

   protected void doClose() throws Exception {
      this.socket.close();
   }

   protected int doReadMessages(List<Object> var1) throws Exception {
      DatagramChannelConfig var2 = this.config();
      RecvByteBufAllocator.Handle var3 = this.unsafe().recvBufAllocHandle();
      ByteBuf var4 = var2.getAllocator().heapBuffer(var3.guess());
      boolean var5 = true;

      byte var7;
      try {
         byte var17;
         try {
            this.tmpPacket.setAddress((InetAddress)null);
            this.tmpPacket.setData(var4.array(), var4.arrayOffset(), var4.capacity());
            this.socket.receive(this.tmpPacket);
            InetSocketAddress var6 = (InetSocketAddress)this.tmpPacket.getSocketAddress();
            var3.lastBytesRead(this.tmpPacket.getLength());
            var1.add(new DatagramPacket(var4.writerIndex(var3.lastBytesRead()), this.localAddress(), var6));
            var5 = false;
            var17 = 1;
            return var17;
         } catch (SocketTimeoutException var13) {
            var17 = 0;
            return var17;
         } catch (SocketException var14) {
            if (!var14.getMessage().toLowerCase(Locale.US).contains("socket closed")) {
               throw var14;
            }

            var7 = -1;
            return var7;
         } catch (Throwable var15) {
            PlatformDependent.throwException(var15);
            var7 = -1;
         }
      } finally {
         if (var5) {
            var4.release();
         }

      }

      return var7;
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      while(true) {
         Object var2 = var1.current();
         if (var2 == null) {
            return;
         }

         ByteBuf var3;
         SocketAddress var4;
         if (var2 instanceof AddressedEnvelope) {
            AddressedEnvelope var5 = (AddressedEnvelope)var2;
            var4 = var5.recipient();
            var3 = (ByteBuf)var5.content();
         } else {
            var3 = (ByteBuf)var2;
            var4 = null;
         }

         int var8 = var3.readableBytes();

         try {
            if (var4 != null) {
               this.tmpPacket.setSocketAddress(var4);
            } else {
               if (!this.isConnected()) {
                  throw new NotYetConnectedException();
               }

               this.tmpPacket.setAddress((InetAddress)null);
            }

            if (var3.hasArray()) {
               this.tmpPacket.setData(var3.array(), var3.arrayOffset() + var3.readerIndex(), var8);
            } else {
               byte[] var6 = new byte[var8];
               var3.getBytes(var3.readerIndex(), var6);
               this.tmpPacket.setData(var6);
            }

            this.socket.send(this.tmpPacket);
            var1.remove();
         } catch (Exception var7) {
            var1.remove(var7);
         }
      }
   }

   protected Object filterOutboundMessage(Object var1) {
      if (!(var1 instanceof DatagramPacket) && !(var1 instanceof ByteBuf)) {
         if (var1 instanceof AddressedEnvelope) {
            AddressedEnvelope var2 = (AddressedEnvelope)var1;
            if (var2.content() instanceof ByteBuf) {
               return var1;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      } else {
         return var1;
      }
   }

   public ChannelFuture joinGroup(InetAddress var1) {
      return this.joinGroup(var1, this.newPromise());
   }

   public ChannelFuture joinGroup(InetAddress var1, ChannelPromise var2) {
      this.ensureBound();

      try {
         this.socket.joinGroup(var1);
         var2.setSuccess();
      } catch (IOException var4) {
         var2.setFailure(var4);
      }

      return var2;
   }

   public ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2) {
      return this.joinGroup(var1, var2, this.newPromise());
   }

   public ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3) {
      this.ensureBound();

      try {
         this.socket.joinGroup(var1, var2);
         var3.setSuccess();
      } catch (IOException var5) {
         var3.setFailure(var5);
      }

      return var3;
   }

   public ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      var4.setFailure(new UnsupportedOperationException());
      return var4;
   }

   private void ensureBound() {
      if (!this.isActive()) {
         throw new IllegalStateException(DatagramChannel.class.getName() + " must be bound to join a group.");
      }
   }

   public ChannelFuture leaveGroup(InetAddress var1) {
      return this.leaveGroup(var1, this.newPromise());
   }

   public ChannelFuture leaveGroup(InetAddress var1, ChannelPromise var2) {
      try {
         this.socket.leaveGroup(var1);
         var2.setSuccess();
      } catch (IOException var4) {
         var2.setFailure(var4);
      }

      return var2;
   }

   public ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2) {
      return this.leaveGroup(var1, var2, this.newPromise());
   }

   public ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3) {
      try {
         this.socket.leaveGroup(var1, var2);
         var3.setSuccess();
      } catch (IOException var5) {
         var3.setFailure(var5);
      }

      return var3;
   }

   public ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      var4.setFailure(new UnsupportedOperationException());
      return var4;
   }

   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      var4.setFailure(new UnsupportedOperationException());
      return var4;
   }

   public ChannelFuture block(InetAddress var1, InetAddress var2) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture block(InetAddress var1, InetAddress var2, ChannelPromise var3) {
      var3.setFailure(new UnsupportedOperationException());
      return var3;
   }
}
