package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.unix.DatagramSocketAddress;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.internal.StringUtil;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

public final class KQueueDatagramChannel extends AbstractKQueueChannel implements DatagramChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
   private volatile boolean connected;
   private final KQueueDatagramChannelConfig config;

   public KQueueDatagramChannel() {
      super((Channel)null, BsdSocket.newSocketDgram(), false);
      this.config = new KQueueDatagramChannelConfig(this);
   }

   public KQueueDatagramChannel(int var1) {
      this(new BsdSocket(var1), true);
   }

   KQueueDatagramChannel(BsdSocket var1, boolean var2) {
      super((Channel)null, var1, var2);
      this.config = new KQueueDatagramChannelConfig(this);
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public boolean isActive() {
      return this.socket.isOpen() && (this.config.getActiveOnOpen() && this.isRegistered() || this.active);
   }

   public boolean isConnected() {
      return this.connected;
   }

   public ChannelFuture joinGroup(InetAddress var1) {
      return this.joinGroup(var1, this.newPromise());
   }

   public ChannelFuture joinGroup(InetAddress var1, ChannelPromise var2) {
      try {
         return this.joinGroup(var1, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), (InetAddress)null, var2);
      } catch (SocketException var4) {
         var2.setFailure(var4);
         return var2;
      }
   }

   public ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2) {
      return this.joinGroup(var1, var2, this.newPromise());
   }

   public ChannelFuture joinGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3) {
      return this.joinGroup(var1.getAddress(), var2, (InetAddress)null, var3);
   }

   public ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.joinGroup(var1, var2, var3, this.newPromise());
   }

   public ChannelFuture joinGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      if (var1 == null) {
         throw new NullPointerException("multicastAddress");
      } else if (var2 == null) {
         throw new NullPointerException("networkInterface");
      } else {
         var4.setFailure(new UnsupportedOperationException("Multicast not supported"));
         return var4;
      }
   }

   public ChannelFuture leaveGroup(InetAddress var1) {
      return this.leaveGroup(var1, this.newPromise());
   }

   public ChannelFuture leaveGroup(InetAddress var1, ChannelPromise var2) {
      try {
         return this.leaveGroup(var1, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), (InetAddress)null, var2);
      } catch (SocketException var4) {
         var2.setFailure(var4);
         return var2;
      }
   }

   public ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2) {
      return this.leaveGroup(var1, var2, this.newPromise());
   }

   public ChannelFuture leaveGroup(InetSocketAddress var1, NetworkInterface var2, ChannelPromise var3) {
      return this.leaveGroup(var1.getAddress(), var2, (InetAddress)null, var3);
   }

   public ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.leaveGroup(var1, var2, var3, this.newPromise());
   }

   public ChannelFuture leaveGroup(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      if (var1 == null) {
         throw new NullPointerException("multicastAddress");
      } else if (var2 == null) {
         throw new NullPointerException("networkInterface");
      } else {
         var4.setFailure(new UnsupportedOperationException("Multicast not supported"));
         return var4;
      }
   }

   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3) {
      return this.block(var1, var2, var3, this.newPromise());
   }

   public ChannelFuture block(InetAddress var1, NetworkInterface var2, InetAddress var3, ChannelPromise var4) {
      if (var1 == null) {
         throw new NullPointerException("multicastAddress");
      } else if (var3 == null) {
         throw new NullPointerException("sourceToBlock");
      } else if (var2 == null) {
         throw new NullPointerException("networkInterface");
      } else {
         var4.setFailure(new UnsupportedOperationException("Multicast not supported"));
         return var4;
      }
   }

   public ChannelFuture block(InetAddress var1, InetAddress var2) {
      return this.block(var1, var2, this.newPromise());
   }

   public ChannelFuture block(InetAddress var1, InetAddress var2, ChannelPromise var3) {
      try {
         return this.block(var1, NetworkInterface.getByInetAddress(this.localAddress().getAddress()), var2, var3);
      } catch (Throwable var5) {
         var3.setFailure(var5);
         return var3;
      }
   }

   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new KQueueDatagramChannel.KQueueDatagramChannelUnsafe();
   }

   protected void doBind(SocketAddress var1) throws Exception {
      super.doBind(var1);
      this.active = true;
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      while(true) {
         Object var2 = var1.current();
         if (var2 == null) {
            this.writeFilter(false);
         } else {
            try {
               boolean var3 = false;

               for(int var4 = this.config().getWriteSpinCount(); var4 > 0; --var4) {
                  if (this.doWriteMessage(var2)) {
                     var3 = true;
                     break;
                  }
               }

               if (var3) {
                  var1.remove();
                  continue;
               }

               this.writeFilter(true);
            } catch (IOException var5) {
               var1.remove(var5);
               continue;
            }
         }

         return;
      }
   }

   private boolean doWriteMessage(Object var1) throws Exception {
      ByteBuf var2;
      InetSocketAddress var3;
      if (var1 instanceof AddressedEnvelope) {
         AddressedEnvelope var4 = (AddressedEnvelope)var1;
         var2 = (ByteBuf)var4.content();
         var3 = (InetSocketAddress)var4.recipient();
      } else {
         var2 = (ByteBuf)var1;
         var3 = null;
      }

      int var9 = var2.readableBytes();
      if (var9 == 0) {
         return true;
      } else {
         long var5;
         if (var2.hasMemoryAddress()) {
            long var7 = var2.memoryAddress();
            if (var3 == null) {
               var5 = (long)this.socket.writeAddress(var7, var2.readerIndex(), var2.writerIndex());
            } else {
               var5 = (long)this.socket.sendToAddress(var7, var2.readerIndex(), var2.writerIndex(), var3.getAddress(), var3.getPort());
            }
         } else if (var2.nioBufferCount() > 1) {
            IovArray var10 = ((KQueueEventLoop)this.eventLoop()).cleanArray();
            var10.add(var2);
            int var8 = var10.count();

            assert var8 != 0;

            if (var3 == null) {
               var5 = this.socket.writevAddresses(var10.memoryAddress(0), var8);
            } else {
               var5 = (long)this.socket.sendToAddresses(var10.memoryAddress(0), var8, var3.getAddress(), var3.getPort());
            }
         } else {
            ByteBuffer var11 = var2.internalNioBuffer(var2.readerIndex(), var2.readableBytes());
            if (var3 == null) {
               var5 = (long)this.socket.write(var11, var11.position(), var11.limit());
            } else {
               var5 = (long)this.socket.sendTo(var11, var11.position(), var11.limit(), var3.getAddress(), var3.getPort());
            }
         }

         return var5 > 0L;
      }
   }

   protected Object filterOutboundMessage(Object var1) {
      ByteBuf var3;
      if (var1 instanceof DatagramPacket) {
         DatagramPacket var5 = (DatagramPacket)var1;
         var3 = (ByteBuf)var5.content();
         return UnixChannelUtil.isBufferCopyNeededForWrite(var3) ? new DatagramPacket(this.newDirectBuffer(var5, var3), (InetSocketAddress)var5.recipient()) : var1;
      } else if (var1 instanceof ByteBuf) {
         ByteBuf var4 = (ByteBuf)var1;
         return UnixChannelUtil.isBufferCopyNeededForWrite(var4) ? this.newDirectBuffer(var4) : var4;
      } else {
         if (var1 instanceof AddressedEnvelope) {
            AddressedEnvelope var2 = (AddressedEnvelope)var1;
            if (var2.content() instanceof ByteBuf && (var2.recipient() == null || var2.recipient() instanceof InetSocketAddress)) {
               var3 = (ByteBuf)var2.content();
               return UnixChannelUtil.isBufferCopyNeededForWrite(var3) ? new DefaultAddressedEnvelope(this.newDirectBuffer(var2, var3), (InetSocketAddress)var2.recipient()) : var2;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      }
   }

   public KQueueDatagramChannelConfig config() {
      return this.config;
   }

   protected void doDisconnect() throws Exception {
      this.socket.disconnect();
      this.connected = this.active = false;
   }

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (super.doConnect(var1, var2)) {
         this.connected = true;
         return true;
      } else {
         return false;
      }
   }

   protected void doClose() throws Exception {
      super.doClose();
      this.connected = false;
   }

   final class KQueueDatagramChannelUnsafe extends AbstractKQueueChannel.AbstractKQueueUnsafe {
      KQueueDatagramChannelUnsafe() {
         super();
      }

      void readReady(KQueueRecvByteAllocatorHandle var1) {
         assert KQueueDatagramChannel.this.eventLoop().inEventLoop();

         KQueueDatagramChannelConfig var2 = KQueueDatagramChannel.this.config();
         if (KQueueDatagramChannel.this.shouldBreakReadReady(var2)) {
            this.clearReadFilter0();
         } else {
            ChannelPipeline var3 = KQueueDatagramChannel.this.pipeline();
            ByteBufAllocator var4 = var2.getAllocator();
            var1.reset(var2);
            this.readReadyBefore();
            Throwable var5 = null;

            try {
               ByteBuf var6 = null;

               try {
                  do {
                     var6 = var1.allocate(var4);
                     var1.attemptedBytesRead(var6.writableBytes());
                     DatagramSocketAddress var7;
                     if (var6.hasMemoryAddress()) {
                        var7 = KQueueDatagramChannel.this.socket.recvFromAddress(var6.memoryAddress(), var6.writerIndex(), var6.capacity());
                     } else {
                        ByteBuffer var8 = var6.internalNioBuffer(var6.writerIndex(), var6.writableBytes());
                        var7 = KQueueDatagramChannel.this.socket.recvFrom(var8, var8.position(), var8.limit());
                     }

                     if (var7 == null) {
                        var1.lastBytesRead(-1);
                        var6.release();
                        var6 = null;
                        break;
                     }

                     var1.incMessagesRead(1);
                     var1.lastBytesRead(var7.receivedAmount());
                     var6.writerIndex(var6.writerIndex() + var1.lastBytesRead());
                     this.readPending = false;
                     var3.fireChannelRead(new DatagramPacket(var6, (InetSocketAddress)this.localAddress(), var7));
                     var6 = null;
                  } while(var1.continueReading());
               } catch (Throwable var12) {
                  if (var6 != null) {
                     var6.release();
                  }

                  var5 = var12;
               }

               var1.readComplete();
               var3.fireChannelReadComplete();
               if (var5 != null) {
                  var3.fireExceptionCaught(var5);
               }
            } finally {
               this.readReadyFinally(var2);
            }

         }
      }
   }
}
