package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.UnixChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.UnresolvedAddressException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

abstract class AbstractKQueueChannel extends AbstractChannel implements UnixChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private ChannelPromise connectPromise;
   private ScheduledFuture<?> connectTimeoutFuture;
   private SocketAddress requestedRemoteAddress;
   final BsdSocket socket;
   private boolean readFilterEnabled = true;
   private boolean writeFilterEnabled;
   boolean readReadyRunnablePending;
   boolean inputClosedSeenErrorOnRead;
   long jniSelfPtr;
   protected volatile boolean active;
   private volatile SocketAddress local;
   private volatile SocketAddress remote;

   AbstractKQueueChannel(Channel var1, BsdSocket var2, boolean var3) {
      super(var1);
      this.socket = (BsdSocket)ObjectUtil.checkNotNull(var2, "fd");
      this.active = var3;
      if (var3) {
         this.local = var2.localAddress();
         this.remote = var2.remoteAddress();
      }

   }

   AbstractKQueueChannel(Channel var1, BsdSocket var2, SocketAddress var3) {
      super(var1);
      this.socket = (BsdSocket)ObjectUtil.checkNotNull(var2, "fd");
      this.active = true;
      this.remote = var3;
      this.local = var2.localAddress();
   }

   static boolean isSoErrorZero(BsdSocket var0) {
      try {
         return var0.getSoError() == 0;
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public final FileDescriptor fd() {
      return this.socket;
   }

   public boolean isActive() {
      return this.active;
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected void doClose() throws Exception {
      this.active = false;
      this.inputClosedSeenErrorOnRead = true;

      try {
         if (this.isRegistered()) {
            EventLoop var1 = this.eventLoop();
            if (var1.inEventLoop()) {
               this.doDeregister();
            } else {
               var1.execute(new Runnable() {
                  public void run() {
                     try {
                        AbstractKQueueChannel.this.doDeregister();
                     } catch (Throwable var2) {
                        AbstractKQueueChannel.this.pipeline().fireExceptionCaught(var2);
                     }

                  }
               });
            }
         }
      } finally {
         this.socket.close();
      }

   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof KQueueEventLoop;
   }

   public boolean isOpen() {
      return this.socket.isOpen();
   }

   protected void doDeregister() throws Exception {
      this.readFilter(false);
      this.writeFilter(false);
      this.evSet0(Native.EVFILT_SOCK, Native.EV_DELETE, 0);
      ((KQueueEventLoop)this.eventLoop()).remove(this);
      this.readFilterEnabled = true;
   }

   protected final void doBeginRead() throws Exception {
      AbstractKQueueChannel.AbstractKQueueUnsafe var1 = (AbstractKQueueChannel.AbstractKQueueUnsafe)this.unsafe();
      var1.readPending = true;
      this.readFilter(true);
      if (var1.maybeMoreDataToRead) {
         var1.executeReadReadyRunnable(this.config());
      }

   }

   protected void doRegister() throws Exception {
      this.readReadyRunnablePending = false;
      if (this.writeFilterEnabled) {
         this.evSet0(Native.EVFILT_WRITE, Native.EV_ADD_CLEAR_ENABLE);
      }

      if (this.readFilterEnabled) {
         this.evSet0(Native.EVFILT_READ, Native.EV_ADD_CLEAR_ENABLE);
      }

      this.evSet0(Native.EVFILT_SOCK, Native.EV_ADD, Native.NOTE_RDHUP);
   }

   protected abstract AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe();

   public abstract KQueueChannelConfig config();

   protected final ByteBuf newDirectBuffer(ByteBuf var1) {
      return this.newDirectBuffer(var1, var1);
   }

   protected final ByteBuf newDirectBuffer(Object var1, ByteBuf var2) {
      int var3 = var2.readableBytes();
      if (var3 == 0) {
         ReferenceCountUtil.release(var1);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator var4 = this.alloc();
         if (var4.isDirectBufferPooled()) {
            return newDirectBuffer0(var1, var2, var4, var3);
         } else {
            ByteBuf var5 = ByteBufUtil.threadLocalDirectBuffer();
            if (var5 == null) {
               return newDirectBuffer0(var1, var2, var4, var3);
            } else {
               var5.writeBytes(var2, var2.readerIndex(), var3);
               ReferenceCountUtil.safeRelease(var1);
               return var5;
            }
         }
      }
   }

   private static ByteBuf newDirectBuffer0(Object var0, ByteBuf var1, ByteBufAllocator var2, int var3) {
      ByteBuf var4 = var2.directBuffer(var3);
      var4.writeBytes(var1, var1.readerIndex(), var3);
      ReferenceCountUtil.safeRelease(var0);
      return var4;
   }

   protected static void checkResolvable(InetSocketAddress var0) {
      if (var0.isUnresolved()) {
         throw new UnresolvedAddressException();
      }
   }

   protected final int doReadBytes(ByteBuf var1) throws Exception {
      int var2 = var1.writerIndex();
      this.unsafe().recvBufAllocHandle().attemptedBytesRead(var1.writableBytes());
      int var3;
      if (var1.hasMemoryAddress()) {
         var3 = this.socket.readAddress(var1.memoryAddress(), var2, var1.capacity());
      } else {
         ByteBuffer var4 = var1.internalNioBuffer(var2, var1.writableBytes());
         var3 = this.socket.read(var4, var4.position(), var4.limit());
      }

      if (var3 > 0) {
         var1.writerIndex(var2 + var3);
      }

      return var3;
   }

   protected final int doWriteBytes(ChannelOutboundBuffer var1, ByteBuf var2) throws Exception {
      if (var2.hasMemoryAddress()) {
         int var3 = this.socket.writeAddress(var2.memoryAddress(), var2.readerIndex(), var2.writerIndex());
         if (var3 > 0) {
            var1.removeBytes((long)var3);
            return 1;
         }
      } else {
         ByteBuffer var5 = var2.nioBufferCount() == 1 ? var2.internalNioBuffer(var2.readerIndex(), var2.readableBytes()) : var2.nioBuffer();
         int var4 = this.socket.write(var5, var5.position(), var5.limit());
         if (var4 > 0) {
            var5.position(var5.position() + var4);
            var1.removeBytes((long)var4);
            return 1;
         }
      }

      return 2147483647;
   }

   final boolean shouldBreakReadReady(ChannelConfig var1) {
      return this.socket.isInputShutdown() && (this.inputClosedSeenErrorOnRead || !isAllowHalfClosure(var1));
   }

   private static boolean isAllowHalfClosure(ChannelConfig var0) {
      return var0 instanceof SocketChannelConfig && ((SocketChannelConfig)var0).isAllowHalfClosure();
   }

   final void clearReadFilter() {
      if (this.isRegistered()) {
         EventLoop var1 = this.eventLoop();
         final AbstractKQueueChannel.AbstractKQueueUnsafe var2 = (AbstractKQueueChannel.AbstractKQueueUnsafe)this.unsafe();
         if (var1.inEventLoop()) {
            var2.clearReadFilter0();
         } else {
            var1.execute(new Runnable() {
               public void run() {
                  if (!var2.readPending && !AbstractKQueueChannel.this.config().isAutoRead()) {
                     var2.clearReadFilter0();
                  }

               }
            });
         }
      } else {
         this.readFilterEnabled = false;
      }

   }

   void readFilter(boolean var1) throws IOException {
      if (this.readFilterEnabled != var1) {
         this.readFilterEnabled = var1;
         this.evSet(Native.EVFILT_READ, var1 ? Native.EV_ADD_CLEAR_ENABLE : Native.EV_DELETE_DISABLE);
      }

   }

   void writeFilter(boolean var1) throws IOException {
      if (this.writeFilterEnabled != var1) {
         this.writeFilterEnabled = var1;
         this.evSet(Native.EVFILT_WRITE, var1 ? Native.EV_ADD_CLEAR_ENABLE : Native.EV_DELETE_DISABLE);
      }

   }

   private void evSet(short var1, short var2) {
      if (this.isOpen() && this.isRegistered()) {
         this.evSet0(var1, var2);
      }

   }

   private void evSet0(short var1, short var2) {
      this.evSet0(var1, var2, 0);
   }

   private void evSet0(short var1, short var2, int var3) {
      ((KQueueEventLoop)this.eventLoop()).evSet(this, var1, var2, var3);
   }

   protected void doBind(SocketAddress var1) throws Exception {
      if (var1 instanceof InetSocketAddress) {
         checkResolvable((InetSocketAddress)var1);
      }

      this.socket.bind(var1);
      this.local = this.socket.localAddress();
   }

   protected boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception {
      if (var2 instanceof InetSocketAddress) {
         checkResolvable((InetSocketAddress)var2);
      }

      InetSocketAddress var3 = var1 instanceof InetSocketAddress ? (InetSocketAddress)var1 : null;
      if (var3 != null) {
         checkResolvable(var3);
      }

      if (this.remote != null) {
         throw new AlreadyConnectedException();
      } else {
         if (var2 != null) {
            this.socket.bind(var2);
         }

         boolean var4 = this.doConnect0(var1);
         if (var4) {
            this.remote = (SocketAddress)(var3 == null ? var1 : UnixChannelUtil.computeRemoteAddr(var3, this.socket.remoteAddress()));
         }

         this.local = this.socket.localAddress();
         return var4;
      }
   }

   private boolean doConnect0(SocketAddress var1) throws Exception {
      boolean var2 = false;

      boolean var4;
      try {
         boolean var3 = this.socket.connect(var1);
         if (!var3) {
            this.writeFilter(true);
         }

         var2 = true;
         var4 = var3;
      } finally {
         if (!var2) {
            this.doClose();
         }

      }

      return var4;
   }

   protected SocketAddress localAddress0() {
      return this.local;
   }

   protected SocketAddress remoteAddress0() {
      return this.remote;
   }

   abstract class AbstractKQueueUnsafe extends AbstractChannel.AbstractUnsafe {
      boolean readPending;
      boolean maybeMoreDataToRead;
      private KQueueRecvByteAllocatorHandle allocHandle;
      private final Runnable readReadyRunnable = new Runnable() {
         public void run() {
            AbstractKQueueChannel.this.readReadyRunnablePending = false;
            AbstractKQueueUnsafe.this.readReady(AbstractKQueueUnsafe.this.recvBufAllocHandle());
         }
      };

      AbstractKQueueUnsafe() {
         super();
      }

      final void readReady(long var1) {
         KQueueRecvByteAllocatorHandle var3 = this.recvBufAllocHandle();
         var3.numberBytesPending(var1);
         this.readReady(var3);
      }

      abstract void readReady(KQueueRecvByteAllocatorHandle var1);

      final void readReadyBefore() {
         this.maybeMoreDataToRead = false;
      }

      final void readReadyFinally(ChannelConfig var1) {
         this.maybeMoreDataToRead = this.allocHandle.maybeMoreDataToRead();
         if (!this.readPending && !var1.isAutoRead()) {
            this.clearReadFilter0();
         } else if (this.readPending && this.maybeMoreDataToRead) {
            this.executeReadReadyRunnable(var1);
         }

      }

      final boolean failConnectPromise(Throwable var1) {
         if (AbstractKQueueChannel.this.connectPromise != null) {
            ChannelPromise var2 = AbstractKQueueChannel.this.connectPromise;
            AbstractKQueueChannel.this.connectPromise = null;
            if (var2.tryFailure(var1 instanceof ConnectException ? var1 : (new ConnectException("failed to connect")).initCause(var1))) {
               this.closeIfClosed();
               return true;
            }
         }

         return false;
      }

      final void writeReady() {
         if (AbstractKQueueChannel.this.connectPromise != null) {
            this.finishConnect();
         } else if (!AbstractKQueueChannel.this.socket.isOutputShutdown()) {
            super.flush0();
         }

      }

      void shutdownInput(boolean var1) {
         if (var1 && AbstractKQueueChannel.this.connectPromise != null) {
            this.finishConnect();
         }

         if (!AbstractKQueueChannel.this.socket.isInputShutdown()) {
            if (AbstractKQueueChannel.isAllowHalfClosure(AbstractKQueueChannel.this.config())) {
               try {
                  AbstractKQueueChannel.this.socket.shutdown(true, false);
               } catch (IOException var3) {
                  this.fireEventAndClose(ChannelInputShutdownEvent.INSTANCE);
                  return;
               } catch (NotYetConnectedException var4) {
               }

               AbstractKQueueChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
            } else {
               this.close(this.voidPromise());
            }
         } else if (!var1) {
            AbstractKQueueChannel.this.inputClosedSeenErrorOnRead = true;
            AbstractKQueueChannel.this.pipeline().fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
         }

      }

      final void readEOF() {
         KQueueRecvByteAllocatorHandle var1 = this.recvBufAllocHandle();
         var1.readEOF();
         if (AbstractKQueueChannel.this.isActive()) {
            this.readReady(var1);
         } else {
            this.shutdownInput(true);
         }

      }

      public KQueueRecvByteAllocatorHandle recvBufAllocHandle() {
         if (this.allocHandle == null) {
            this.allocHandle = new KQueueRecvByteAllocatorHandle((RecvByteBufAllocator.ExtendedHandle)super.recvBufAllocHandle());
         }

         return this.allocHandle;
      }

      protected final void flush0() {
         if (!AbstractKQueueChannel.this.writeFilterEnabled) {
            super.flush0();
         }

      }

      final void executeReadReadyRunnable(ChannelConfig var1) {
         if (!AbstractKQueueChannel.this.readReadyRunnablePending && AbstractKQueueChannel.this.isActive() && !AbstractKQueueChannel.this.shouldBreakReadReady(var1)) {
            AbstractKQueueChannel.this.readReadyRunnablePending = true;
            AbstractKQueueChannel.this.eventLoop().execute(this.readReadyRunnable);
         }
      }

      protected final void clearReadFilter0() {
         assert AbstractKQueueChannel.this.eventLoop().inEventLoop();

         try {
            this.readPending = false;
            AbstractKQueueChannel.this.readFilter(false);
         } catch (IOException var2) {
            AbstractKQueueChannel.this.pipeline().fireExceptionCaught(var2);
            AbstractKQueueChannel.this.unsafe().close(AbstractKQueueChannel.this.unsafe().voidPromise());
         }

      }

      private void fireEventAndClose(Object var1) {
         AbstractKQueueChannel.this.pipeline().fireUserEventTriggered(var1);
         this.close(this.voidPromise());
      }

      public void connect(final SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         if (var3.setUncancellable() && this.ensureOpen(var3)) {
            try {
               if (AbstractKQueueChannel.this.connectPromise != null) {
                  throw new ConnectionPendingException();
               }

               boolean var4 = AbstractKQueueChannel.this.isActive();
               if (AbstractKQueueChannel.this.doConnect(var1, var2)) {
                  this.fulfillConnectPromise(var3, var4);
               } else {
                  AbstractKQueueChannel.this.connectPromise = var3;
                  AbstractKQueueChannel.this.requestedRemoteAddress = var1;
                  int var5 = AbstractKQueueChannel.this.config().getConnectTimeoutMillis();
                  if (var5 > 0) {
                     AbstractKQueueChannel.this.connectTimeoutFuture = AbstractKQueueChannel.this.eventLoop().schedule(new Runnable() {
                        public void run() {
                           ChannelPromise var1x = AbstractKQueueChannel.this.connectPromise;
                           ConnectTimeoutException var2 = new ConnectTimeoutException("connection timed out: " + var1);
                           if (var1x != null && var1x.tryFailure(var2)) {
                              AbstractKQueueUnsafe.this.close(AbstractKQueueUnsafe.this.voidPromise());
                           }

                        }
                     }, (long)var5, TimeUnit.MILLISECONDS);
                  }

                  var3.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1) throws Exception {
                        if (var1.isCancelled()) {
                           if (AbstractKQueueChannel.this.connectTimeoutFuture != null) {
                              AbstractKQueueChannel.this.connectTimeoutFuture.cancel(false);
                           }

                           AbstractKQueueChannel.this.connectPromise = null;
                           AbstractKQueueUnsafe.this.close(AbstractKQueueUnsafe.this.voidPromise());
                        }

                     }
                  });
               }
            } catch (Throwable var6) {
               this.closeIfClosed();
               var3.tryFailure(this.annotateConnectException(var6, var1));
            }

         }
      }

      private void fulfillConnectPromise(ChannelPromise var1, boolean var2) {
         if (var1 != null) {
            AbstractKQueueChannel.this.active = true;
            boolean var3 = AbstractKQueueChannel.this.isActive();
            boolean var4 = var1.trySuccess();
            if (!var2 && var3) {
               AbstractKQueueChannel.this.pipeline().fireChannelActive();
            }

            if (!var4) {
               this.close(this.voidPromise());
            }

         }
      }

      private void fulfillConnectPromise(ChannelPromise var1, Throwable var2) {
         if (var1 != null) {
            var1.tryFailure(var2);
            this.closeIfClosed();
         }
      }

      private void finishConnect() {
         assert AbstractKQueueChannel.this.eventLoop().inEventLoop();

         boolean var1 = false;

         try {
            boolean var2 = AbstractKQueueChannel.this.isActive();
            if (!this.doFinishConnect()) {
               var1 = true;
               return;
            }

            this.fulfillConnectPromise(AbstractKQueueChannel.this.connectPromise, var2);
         } catch (Throwable var6) {
            this.fulfillConnectPromise(AbstractKQueueChannel.this.connectPromise, this.annotateConnectException(var6, AbstractKQueueChannel.this.requestedRemoteAddress));
         } finally {
            if (!var1) {
               if (AbstractKQueueChannel.this.connectTimeoutFuture != null) {
                  AbstractKQueueChannel.this.connectTimeoutFuture.cancel(false);
               }

               AbstractKQueueChannel.this.connectPromise = null;
            }

         }

      }

      private boolean doFinishConnect() throws Exception {
         if (AbstractKQueueChannel.this.socket.finishConnect()) {
            AbstractKQueueChannel.this.writeFilter(false);
            if (AbstractKQueueChannel.this.requestedRemoteAddress instanceof InetSocketAddress) {
               AbstractKQueueChannel.this.remote = UnixChannelUtil.computeRemoteAddr((InetSocketAddress)AbstractKQueueChannel.this.requestedRemoteAddress, AbstractKQueueChannel.this.socket.remoteAddress());
            }

            AbstractKQueueChannel.this.requestedRemoteAddress = null;
            return true;
         } else {
            AbstractKQueueChannel.this.writeFilter(true);
            return false;
         }
      }
   }
}
