package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.ServerChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public abstract class AbstractKQueueServerChannel extends AbstractKQueueChannel implements ServerChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);

   AbstractKQueueServerChannel(BsdSocket var1) {
      this(var1, isSoErrorZero(var1));
   }

   AbstractKQueueServerChannel(BsdSocket var1, boolean var2) {
      super((Channel)null, var1, var2);
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof KQueueEventLoop;
   }

   protected InetSocketAddress remoteAddress0() {
      return null;
   }

   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new AbstractKQueueServerChannel.KQueueServerSocketUnsafe();
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

   final class KQueueServerSocketUnsafe extends AbstractKQueueChannel.AbstractKQueueUnsafe {
      private final byte[] acceptedAddress = new byte[26];

      KQueueServerSocketUnsafe() {
         super();
      }

      void readReady(KQueueRecvByteAllocatorHandle var1) {
         assert AbstractKQueueServerChannel.this.eventLoop().inEventLoop();

         KQueueChannelConfig var2 = AbstractKQueueServerChannel.this.config();
         if (AbstractKQueueServerChannel.this.shouldBreakReadReady(var2)) {
            this.clearReadFilter0();
         } else {
            ChannelPipeline var3 = AbstractKQueueServerChannel.this.pipeline();
            var1.reset(var2);
            var1.attemptedBytesRead(1);
            this.readReadyBefore();
            Throwable var4 = null;

            try {
               try {
                  do {
                     int var5 = AbstractKQueueServerChannel.this.socket.accept(this.acceptedAddress);
                     if (var5 == -1) {
                        var1.lastBytesRead(-1);
                        break;
                     }

                     var1.lastBytesRead(1);
                     var1.incMessagesRead(1);
                     this.readPending = false;
                     var3.fireChannelRead(AbstractKQueueServerChannel.this.newChildChannel(var5, this.acceptedAddress, 1, this.acceptedAddress[0]));
                  } while(var1.continueReading());
               } catch (Throwable var9) {
                  var4 = var9;
               }

               var1.readComplete();
               var3.fireChannelReadComplete();
               if (var4 != null) {
                  var3.fireExceptionCaught(var4);
               }
            } finally {
               this.readReadyFinally(var2);
            }

         }
      }
   }
}
