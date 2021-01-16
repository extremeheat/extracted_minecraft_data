package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ServerChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class AbstractEpollServerChannel extends AbstractEpollChannel implements ServerChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);

   protected AbstractEpollServerChannel(int var1) {
      this(new LinuxSocket(var1), false);
   }

   AbstractEpollServerChannel(LinuxSocket var1) {
      this(var1, isSoErrorZero(var1));
   }

   AbstractEpollServerChannel(LinuxSocket var1, boolean var2) {
      super((Channel)null, var1, Native.EPOLLIN, var2);
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof EpollEventLoop;
   }

   protected InetSocketAddress remoteAddress0() {
      return null;
   }

   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new AbstractEpollServerChannel.EpollServerSocketUnsafe();
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected Object filterOutboundMessage(Object var1) throws Exception {
      throw new UnsupportedOperationException();
   }

   abstract Channel newChildChannel(int var1, byte[] var2, int var3, int var4) throws Exception;

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      throw new UnsupportedOperationException();
   }

   final class EpollServerSocketUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      private final byte[] acceptedAddress = new byte[26];

      EpollServerSocketUnsafe() {
         super();
      }

      public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         var3.setFailure(new UnsupportedOperationException());
      }

      void epollInReady() {
         assert AbstractEpollServerChannel.this.eventLoop().inEventLoop();

         EpollChannelConfig var1 = AbstractEpollServerChannel.this.config();
         if (AbstractEpollServerChannel.this.shouldBreakEpollInReady(var1)) {
            this.clearEpollIn0();
         } else {
            EpollRecvByteAllocatorHandle var2 = this.recvBufAllocHandle();
            var2.edgeTriggered(AbstractEpollServerChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline var3 = AbstractEpollServerChannel.this.pipeline();
            var2.reset(var1);
            var2.attemptedBytesRead(1);
            this.epollInBefore();
            Throwable var4 = null;

            try {
               try {
                  do {
                     var2.lastBytesRead(AbstractEpollServerChannel.this.socket.accept(this.acceptedAddress));
                     if (var2.lastBytesRead() == -1) {
                        break;
                     }

                     var2.incMessagesRead(1);
                     this.readPending = false;
                     var3.fireChannelRead(AbstractEpollServerChannel.this.newChildChannel(var2.lastBytesRead(), this.acceptedAddress, 1, this.acceptedAddress[0]));
                  } while(var2.continueReading());
               } catch (Throwable var9) {
                  var4 = var9;
               }

               var2.readComplete();
               var3.fireChannelReadComplete();
               if (var4 != null) {
                  var3.fireExceptionCaught(var4);
               }
            } finally {
               this.epollInFinally(var1);
            }

         }
      }
   }
}
