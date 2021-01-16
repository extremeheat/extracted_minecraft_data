package io.netty.channel.local;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class LocalChannel extends AbstractChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(LocalChannel.class);
   private static final AtomicReferenceFieldUpdater<LocalChannel, Future> FINISH_READ_FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(LocalChannel.class, Future.class, "finishReadFuture");
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final int MAX_READER_STACK_DEPTH = 8;
   private static final ClosedChannelException DO_WRITE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), LocalChannel.class, "doWrite(...)");
   private static final ClosedChannelException DO_CLOSE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), LocalChannel.class, "doClose()");
   private final ChannelConfig config = new DefaultChannelConfig(this);
   final Queue<Object> inboundBuffer = PlatformDependent.newSpscQueue();
   private final Runnable readTask = new Runnable() {
      public void run() {
         if (!LocalChannel.this.inboundBuffer.isEmpty()) {
            LocalChannel.this.readInbound();
         }

      }
   };
   private final Runnable shutdownHook = new Runnable() {
      public void run() {
         LocalChannel.this.unsafe().close(LocalChannel.this.unsafe().voidPromise());
      }
   };
   private volatile LocalChannel.State state;
   private volatile LocalChannel peer;
   private volatile LocalAddress localAddress;
   private volatile LocalAddress remoteAddress;
   private volatile ChannelPromise connectPromise;
   private volatile boolean readInProgress;
   private volatile boolean writeInProgress;
   private volatile Future<?> finishReadFuture;

   public LocalChannel() {
      super((Channel)null);
      this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
   }

   protected LocalChannel(LocalServerChannel var1, LocalChannel var2) {
      super(var1);
      this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
      this.peer = var2;
      this.localAddress = var1.localAddress();
      this.remoteAddress = var2.localAddress();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public ChannelConfig config() {
      return this.config;
   }

   public LocalServerChannel parent() {
      return (LocalServerChannel)super.parent();
   }

   public LocalAddress localAddress() {
      return (LocalAddress)super.localAddress();
   }

   public LocalAddress remoteAddress() {
      return (LocalAddress)super.remoteAddress();
   }

   public boolean isOpen() {
      return this.state != LocalChannel.State.CLOSED;
   }

   public boolean isActive() {
      return this.state == LocalChannel.State.CONNECTED;
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new LocalChannel.LocalUnsafe();
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof SingleThreadEventLoop;
   }

   protected SocketAddress localAddress0() {
      return this.localAddress;
   }

   protected SocketAddress remoteAddress0() {
      return this.remoteAddress;
   }

   protected void doRegister() throws Exception {
      if (this.peer != null && this.parent() != null) {
         final LocalChannel var1 = this.peer;
         this.state = LocalChannel.State.CONNECTED;
         var1.remoteAddress = this.parent() == null ? null : this.parent().localAddress();
         var1.state = LocalChannel.State.CONNECTED;
         var1.eventLoop().execute(new Runnable() {
            public void run() {
               ChannelPromise var1x = var1.connectPromise;
               if (var1x != null && var1x.trySuccess()) {
                  var1.pipeline().fireChannelActive();
               }

            }
         });
      }

      ((SingleThreadEventExecutor)this.eventLoop()).addShutdownHook(this.shutdownHook);
   }

   protected void doBind(SocketAddress var1) throws Exception {
      this.localAddress = LocalChannelRegistry.register(this, this.localAddress, var1);
      this.state = LocalChannel.State.BOUND;
   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      final LocalChannel var1 = this.peer;
      LocalChannel.State var2 = this.state;

      try {
         if (var2 != LocalChannel.State.CLOSED) {
            if (this.localAddress != null) {
               if (this.parent() == null) {
                  LocalChannelRegistry.unregister(this.localAddress);
               }

               this.localAddress = null;
            }

            this.state = LocalChannel.State.CLOSED;
            if (this.writeInProgress && var1 != null) {
               this.finishPeerRead(var1);
            }

            ChannelPromise var3 = this.connectPromise;
            if (var3 != null) {
               var3.tryFailure(DO_CLOSE_CLOSED_CHANNEL_EXCEPTION);
               this.connectPromise = null;
            }
         }

         if (var1 != null) {
            this.peer = null;
            EventLoop var11 = var1.eventLoop();
            final boolean var4 = var1.isActive();

            try {
               var11.execute(new Runnable() {
                  public void run() {
                     var1.tryClose(var4);
                  }
               });
            } catch (Throwable var9) {
               logger.warn("Releasing Inbound Queues for channels {}-{} because exception occurred!", this, var1, var9);
               if (var11.inEventLoop()) {
                  var1.releaseInboundBuffers();
               } else {
                  var1.close();
               }

               PlatformDependent.throwException(var9);
            }
         }
      } finally {
         if (var2 != null && var2 != LocalChannel.State.CLOSED) {
            this.releaseInboundBuffers();
         }

      }

   }

   private void tryClose(boolean var1) {
      if (var1) {
         this.unsafe().close(this.unsafe().voidPromise());
      } else {
         this.releaseInboundBuffers();
      }

   }

   protected void doDeregister() throws Exception {
      ((SingleThreadEventExecutor)this.eventLoop()).removeShutdownHook(this.shutdownHook);
   }

   private void readInbound() {
      RecvByteBufAllocator.Handle var1 = this.unsafe().recvBufAllocHandle();
      var1.reset(this.config());
      ChannelPipeline var2 = this.pipeline();

      do {
         Object var3 = this.inboundBuffer.poll();
         if (var3 == null) {
            break;
         }

         var2.fireChannelRead(var3);
      } while(var1.continueReading());

      var2.fireChannelReadComplete();
   }

   protected void doBeginRead() throws Exception {
      if (!this.readInProgress) {
         Queue var1 = this.inboundBuffer;
         if (var1.isEmpty()) {
            this.readInProgress = true;
         } else {
            InternalThreadLocalMap var2 = InternalThreadLocalMap.get();
            Integer var3 = var2.localChannelReaderStackDepth();
            if (var3 < 8) {
               var2.setLocalChannelReaderStackDepth(var3 + 1);

               try {
                  this.readInbound();
               } finally {
                  var2.setLocalChannelReaderStackDepth(var3);
               }
            } else {
               try {
                  this.eventLoop().execute(this.readTask);
               } catch (Throwable var7) {
                  logger.warn("Closing Local channels {}-{} because exception occurred!", this, this.peer, var7);
                  this.close();
                  this.peer.close();
                  PlatformDependent.throwException(var7);
               }
            }

         }
      }
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      switch(this.state) {
      case OPEN:
      case BOUND:
         throw new NotYetConnectedException();
      case CLOSED:
         throw DO_WRITE_CLOSED_CHANNEL_EXCEPTION;
      case CONNECTED:
      default:
         LocalChannel var2 = this.peer;
         this.writeInProgress = true;

         try {
            while(true) {
               Object var3 = var1.current();
               if (var3 == null) {
                  break;
               }

               try {
                  if (var2.state == LocalChannel.State.CONNECTED) {
                     var2.inboundBuffer.add(ReferenceCountUtil.retain(var3));
                     var1.remove();
                  } else {
                     var1.remove(DO_WRITE_CLOSED_CHANNEL_EXCEPTION);
                  }
               } catch (Throwable var8) {
                  var1.remove(var8);
               }
            }
         } finally {
            this.writeInProgress = false;
         }

         this.finishPeerRead(var2);
      }
   }

   private void finishPeerRead(LocalChannel var1) {
      if (var1.eventLoop() == this.eventLoop() && !var1.writeInProgress) {
         this.finishPeerRead0(var1);
      } else {
         this.runFinishPeerReadTask(var1);
      }

   }

   private void runFinishPeerReadTask(final LocalChannel var1) {
      Runnable var2 = new Runnable() {
         public void run() {
            LocalChannel.this.finishPeerRead0(var1);
         }
      };

      try {
         if (var1.writeInProgress) {
            var1.finishReadFuture = var1.eventLoop().submit(var2);
         } else {
            var1.eventLoop().execute(var2);
         }
      } catch (Throwable var4) {
         logger.warn("Closing Local channels {}-{} because exception occurred!", this, var1, var4);
         this.close();
         var1.close();
         PlatformDependent.throwException(var4);
      }

   }

   private void releaseInboundBuffers() {
      assert this.eventLoop() == null || this.eventLoop().inEventLoop();

      this.readInProgress = false;
      Queue var1 = this.inboundBuffer;

      Object var2;
      while((var2 = var1.poll()) != null) {
         ReferenceCountUtil.release(var2);
      }

   }

   private void finishPeerRead0(LocalChannel var1) {
      Future var2 = var1.finishReadFuture;
      if (var2 != null) {
         if (!var2.isDone()) {
            this.runFinishPeerReadTask(var1);
            return;
         }

         FINISH_READ_FUTURE_UPDATER.compareAndSet(var1, var2, (Object)null);
      }

      if (var1.readInProgress && !var1.inboundBuffer.isEmpty()) {
         var1.readInProgress = false;
         var1.readInbound();
      }

   }

   private class LocalUnsafe extends AbstractChannel.AbstractUnsafe {
      private LocalUnsafe() {
         super();
      }

      public void connect(SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         if (var3.setUncancellable() && this.ensureOpen(var3)) {
            if (LocalChannel.this.state == LocalChannel.State.CONNECTED) {
               AlreadyConnectedException var8 = new AlreadyConnectedException();
               this.safeSetFailure(var3, var8);
               LocalChannel.this.pipeline().fireExceptionCaught(var8);
            } else if (LocalChannel.this.connectPromise != null) {
               throw new ConnectionPendingException();
            } else {
               LocalChannel.this.connectPromise = var3;
               if (LocalChannel.this.state != LocalChannel.State.BOUND && var2 == null) {
                  var2 = new LocalAddress(LocalChannel.this);
               }

               if (var2 != null) {
                  try {
                     LocalChannel.this.doBind((SocketAddress)var2);
                  } catch (Throwable var6) {
                     this.safeSetFailure(var3, var6);
                     this.close(this.voidPromise());
                     return;
                  }
               }

               Channel var4 = LocalChannelRegistry.get(var1);
               if (!(var4 instanceof LocalServerChannel)) {
                  ConnectException var7 = new ConnectException("connection refused: " + var1);
                  this.safeSetFailure(var3, var7);
                  this.close(this.voidPromise());
               } else {
                  LocalServerChannel var5 = (LocalServerChannel)var4;
                  LocalChannel.this.peer = var5.serve(LocalChannel.this);
               }
            }
         }
      }

      // $FF: synthetic method
      LocalUnsafe(Object var2) {
         this();
      }
   }

   private static enum State {
      OPEN,
      BOUND,
      CONNECTED,
      CLOSED;

      private State() {
      }
   }
}
