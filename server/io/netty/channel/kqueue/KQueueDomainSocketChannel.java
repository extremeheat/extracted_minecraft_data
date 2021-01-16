package io.netty.channel.kqueue;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.PeerCredentials;
import java.io.IOException;
import java.net.SocketAddress;

public final class KQueueDomainSocketChannel extends AbstractKQueueStreamChannel implements DomainSocketChannel {
   private final KQueueDomainSocketChannelConfig config;
   private volatile DomainSocketAddress local;
   private volatile DomainSocketAddress remote;

   public KQueueDomainSocketChannel() {
      super((Channel)null, BsdSocket.newSocketDomain(), false);
      this.config = new KQueueDomainSocketChannelConfig(this);
   }

   public KQueueDomainSocketChannel(int var1) {
      this((Channel)null, new BsdSocket(var1));
   }

   KQueueDomainSocketChannel(Channel var1, BsdSocket var2) {
      super(var1, var2, true);
      this.config = new KQueueDomainSocketChannelConfig(this);
   }

   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new KQueueDomainSocketChannel.KQueueDomainUnsafe();
   }

   protected DomainSocketAddress localAddress0() {
      return this.local;
   }

   protected DomainSocketAddress remoteAddress0() {
      return this.remote;
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.socket.bind(var1);
      this.local = (DomainSocketAddress)var1;
   }

   public KQueueDomainSocketChannelConfig config() {
      return this.config;
   }

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (super.doConnect(var1, var2)) {
         this.local = (DomainSocketAddress)var2;
         this.remote = (DomainSocketAddress)var1;
         return true;
      } else {
         return false;
      }
   }

   public DomainSocketAddress remoteAddress() {
      return (DomainSocketAddress)super.remoteAddress();
   }

   public DomainSocketAddress localAddress() {
      return (DomainSocketAddress)super.localAddress();
   }

   protected int doWriteSingle(ChannelOutboundBuffer var1) throws Exception {
      Object var2 = var1.current();
      if (var2 instanceof FileDescriptor && this.socket.sendFd(((FileDescriptor)var2).intValue()) > 0) {
         var1.remove();
         return 1;
      } else {
         return super.doWriteSingle(var1);
      }
   }

   protected Object filterOutboundMessage(Object var1) {
      return var1 instanceof FileDescriptor ? var1 : super.filterOutboundMessage(var1);
   }

   public PeerCredentials peerCredentials() throws IOException {
      return this.socket.getPeerCredentials();
   }

   private final class KQueueDomainUnsafe extends AbstractKQueueStreamChannel.KQueueStreamUnsafe {
      private KQueueDomainUnsafe() {
         super();
      }

      void readReady(KQueueRecvByteAllocatorHandle var1) {
         switch(KQueueDomainSocketChannel.this.config().getReadMode()) {
         case BYTES:
            super.readReady(var1);
            break;
         case FILE_DESCRIPTORS:
            this.readReadyFd();
            break;
         default:
            throw new Error();
         }

      }

      private void readReadyFd() {
         if (KQueueDomainSocketChannel.this.socket.isInputShutdown()) {
            super.clearReadFilter0();
         } else {
            KQueueDomainSocketChannelConfig var1 = KQueueDomainSocketChannel.this.config();
            KQueueRecvByteAllocatorHandle var2 = this.recvBufAllocHandle();
            ChannelPipeline var3 = KQueueDomainSocketChannel.this.pipeline();
            var2.reset(var1);
            this.readReadyBefore();

            while(true) {
               try {
                  int var4 = KQueueDomainSocketChannel.this.socket.recvFd();
                  switch(var4) {
                  case -1:
                     var2.lastBytesRead(-1);
                     this.close(this.voidPromise());
                     return;
                  case 0:
                     var2.lastBytesRead(0);
                     break;
                  default:
                     var2.lastBytesRead(1);
                     var2.incMessagesRead(1);
                     this.readPending = false;
                     var3.fireChannelRead(new FileDescriptor(var4));
                     if (var2.continueReading()) {
                        continue;
                     }
                  }

                  var2.readComplete();
                  var3.fireChannelReadComplete();
               } catch (Throwable var8) {
                  var2.readComplete();
                  var3.fireChannelReadComplete();
                  var3.fireExceptionCaught(var8);
               } finally {
                  this.readReadyFinally(var1);
               }

               return;
            }
         }
      }

      // $FF: synthetic method
      KQueueDomainUnsafe(Object var2) {
         this();
      }
   }
}
