package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.unix.DomainSocketAddress;
import io.netty.channel.unix.DomainSocketChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.PeerCredentials;
import java.io.IOException;
import java.net.SocketAddress;

public final class EpollDomainSocketChannel extends AbstractEpollStreamChannel implements DomainSocketChannel {
   private final EpollDomainSocketChannelConfig config = new EpollDomainSocketChannelConfig(this);
   private volatile DomainSocketAddress local;
   private volatile DomainSocketAddress remote;

   public EpollDomainSocketChannel() {
      super(LinuxSocket.newSocketDomain(), false);
   }

   EpollDomainSocketChannel(Channel var1, FileDescriptor var2) {
      super(var1, new LinuxSocket(var2.intValue()));
   }

   public EpollDomainSocketChannel(int var1) {
      super(var1);
   }

   public EpollDomainSocketChannel(Channel var1, LinuxSocket var2) {
      super(var1, var2);
   }

   public EpollDomainSocketChannel(int var1, boolean var2) {
      super(new LinuxSocket(var1), var2);
   }

   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollDomainSocketChannel.EpollDomainUnsafe();
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

   public EpollDomainSocketChannelConfig config() {
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

   private final class EpollDomainUnsafe extends AbstractEpollStreamChannel.EpollStreamUnsafe {
      private EpollDomainUnsafe() {
         super();
      }

      void epollInReady() {
         switch(EpollDomainSocketChannel.this.config().getReadMode()) {
         case BYTES:
            super.epollInReady();
            break;
         case FILE_DESCRIPTORS:
            this.epollInReadFd();
            break;
         default:
            throw new Error();
         }

      }

      private void epollInReadFd() {
         if (EpollDomainSocketChannel.this.socket.isInputShutdown()) {
            this.clearEpollIn0();
         } else {
            EpollDomainSocketChannelConfig var1 = EpollDomainSocketChannel.this.config();
            EpollRecvByteAllocatorHandle var2 = this.recvBufAllocHandle();
            var2.edgeTriggered(EpollDomainSocketChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline var3 = EpollDomainSocketChannel.this.pipeline();
            var2.reset(var1);
            this.epollInBefore();

            while(true) {
               try {
                  var2.lastBytesRead(EpollDomainSocketChannel.this.socket.recvFd());
                  switch(var2.lastBytesRead()) {
                  case -1:
                     this.close(this.voidPromise());
                     return;
                  default:
                     var2.incMessagesRead(1);
                     this.readPending = false;
                     var3.fireChannelRead(new FileDescriptor(var2.lastBytesRead()));
                     if (var2.continueReading()) {
                        continue;
                     }
                  case 0:
                     var2.readComplete();
                     var3.fireChannelReadComplete();
                  }
               } catch (Throwable var8) {
                  var2.readComplete();
                  var3.fireChannelReadComplete();
                  var3.fireExceptionCaught(var8);
               } finally {
                  this.epollInFinally(var1);
               }

               return;
            }
         }
      }

      // $FF: synthetic method
      EpollDomainUnsafe(Object var2) {
         this();
      }
   }
}
