package io.netty.channel.epoll;

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

public final class EpollDatagramChannel extends AbstractEpollChannel implements DatagramChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(InetSocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
   private final EpollDatagramChannelConfig config;
   private volatile boolean connected;

   public EpollDatagramChannel() {
      super(LinuxSocket.newSocketDgram(), Native.EPOLLIN);
      this.config = new EpollDatagramChannelConfig(this);
   }

   public EpollDatagramChannel(int var1) {
      this(new LinuxSocket(var1));
   }

   EpollDatagramChannel(LinuxSocket var1) {
      super((Channel)null, var1, Native.EPOLLIN, true);
      this.config = new EpollDatagramChannelConfig(this);
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

   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollDatagramChannel.EpollDatagramChannelUnsafe();
   }

   protected void doBind(SocketAddress var1) throws Exception {
      super.doBind(var1);
      this.active = true;
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      label65:
      while(true) {
         Object var2 = var1.current();
         if (var2 == null) {
            this.clearFlag(Native.EPOLLOUT);
         } else {
            try {
               int var4;
               if (Native.IS_SUPPORTING_SENDMMSG && var1.size() > 1) {
                  NativeDatagramPacketArray var3 = NativeDatagramPacketArray.getInstance(var1);
                  var4 = var3.count();
                  if (var4 >= 1) {
                     int var5 = 0;
                     NativeDatagramPacketArray.NativeDatagramPacket[] var6 = var3.packets();

                     while(true) {
                        if (var4 <= 0) {
                           continue label65;
                        }

                        int var7 = Native.sendmmsg(this.socket.intValue(), var6, var5, var4);
                        if (var7 == 0) {
                           this.setFlag(Native.EPOLLOUT);
                           return;
                        }

                        for(int var8 = 0; var8 < var7; ++var8) {
                           var1.remove();
                        }

                        var4 -= var7;
                        var5 += var7;
                     }
                  }
               }

               boolean var10 = false;

               for(var4 = this.config().getWriteSpinCount(); var4 > 0; --var4) {
                  if (this.doWriteMessage(var2)) {
                     var10 = true;
                     break;
                  }
               }

               if (var10) {
                  var1.remove();
                  continue;
               }

               this.setFlag(Native.EPOLLOUT);
            } catch (IOException var9) {
               var1.remove(var9);
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
            IovArray var10 = ((EpollEventLoop)this.eventLoop()).cleanArray();
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

   public EpollDatagramChannelConfig config() {
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

   final class EpollDatagramChannelUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      EpollDatagramChannelUnsafe() {
         super();
      }

      void epollInReady() {
         assert EpollDatagramChannel.this.eventLoop().inEventLoop();

         EpollDatagramChannelConfig var1 = EpollDatagramChannel.this.config();
         if (EpollDatagramChannel.this.shouldBreakEpollInReady(var1)) {
            this.clearEpollIn0();
         } else {
            EpollRecvByteAllocatorHandle var2 = this.recvBufAllocHandle();
            var2.edgeTriggered(EpollDatagramChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline var3 = EpollDatagramChannel.this.pipeline();
            ByteBufAllocator var4 = var1.getAllocator();
            var2.reset(var1);
            this.epollInBefore();
            Throwable var5 = null;

            try {
               ByteBuf var6 = null;

               while(true) {
                  try {
                     var6 = var2.allocate(var4);
                     var2.attemptedBytesRead(var6.writableBytes());
                     DatagramSocketAddress var7;
                     if (var6.hasMemoryAddress()) {
                        var7 = EpollDatagramChannel.this.socket.recvFromAddress(var6.memoryAddress(), var6.writerIndex(), var6.capacity());
                     } else {
                        ByteBuffer var8 = var6.internalNioBuffer(var6.writerIndex(), var6.writableBytes());
                        var7 = EpollDatagramChannel.this.socket.recvFrom(var8, var8.position(), var8.limit());
                     }

                     if (var7 == null) {
                        var2.lastBytesRead(-1);
                        var6.release();
                        var6 = null;
                     } else {
                        Object var14 = var7.localAddress();
                        if (var14 == null) {
                           var14 = (InetSocketAddress)this.localAddress();
                        }

                        var2.incMessagesRead(1);
                        var2.lastBytesRead(var7.receivedAmount());
                        var6.writerIndex(var6.writerIndex() + var2.lastBytesRead());
                        this.readPending = false;
                        var3.fireChannelRead(new DatagramPacket(var6, (InetSocketAddress)var14, var7));
                        var6 = null;
                        if (var2.continueReading()) {
                           continue;
                        }
                     }
                  } catch (Throwable var12) {
                     if (var6 != null) {
                        var6.release();
                     }

                     var5 = var12;
                  }

                  var2.readComplete();
                  var3.fireChannelReadComplete();
                  if (var5 != null) {
                     var3.fireExceptionCaught(var5);
                  }
                  break;
               }
            } finally {
               this.epollInFinally(var1);
            }

         }
      }
   }
}
