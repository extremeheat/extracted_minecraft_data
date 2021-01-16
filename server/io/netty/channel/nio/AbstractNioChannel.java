package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioChannel extends AbstractChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
   private static final ClosedChannelException DO_CLOSE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractNioChannel.class, "doClose()");
   private final SelectableChannel ch;
   protected final int readInterestOp;
   volatile SelectionKey selectionKey;
   boolean readPending;
   private final Runnable clearReadPendingRunnable = new Runnable() {
      public void run() {
         AbstractNioChannel.this.clearReadPending0();
      }
   };
   private ChannelPromise connectPromise;
   private ScheduledFuture<?> connectTimeoutFuture;
   private SocketAddress requestedRemoteAddress;

   protected AbstractNioChannel(Channel var1, SelectableChannel var2, int var3) {
      super(var1);
      this.ch = var2;
      this.readInterestOp = var3;

      try {
         var2.configureBlocking(false);
      } catch (IOException var7) {
         try {
            var2.close();
         } catch (IOException var6) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to close a partially initialized socket.", (Throwable)var6);
            }
         }

         throw new ChannelException("Failed to enter non-blocking mode.", var7);
      }
   }

   public boolean isOpen() {
      return this.ch.isOpen();
   }

   public AbstractNioChannel.NioUnsafe unsafe() {
      return (AbstractNioChannel.NioUnsafe)super.unsafe();
   }

   protected SelectableChannel javaChannel() {
      return this.ch;
   }

   public NioEventLoop eventLoop() {
      return (NioEventLoop)super.eventLoop();
   }

   protected SelectionKey selectionKey() {
      assert this.selectionKey != null;

      return this.selectionKey;
   }

   /** @deprecated */
   @Deprecated
   protected boolean isReadPending() {
      return this.readPending;
   }

   /** @deprecated */
   @Deprecated
   protected void setReadPending(final boolean var1) {
      if (this.isRegistered()) {
         NioEventLoop var2 = this.eventLoop();
         if (var2.inEventLoop()) {
            this.setReadPending0(var1);
         } else {
            var2.execute(new Runnable() {
               public void run() {
                  AbstractNioChannel.this.setReadPending0(var1);
               }
            });
         }
      } else {
         this.readPending = var1;
      }

   }

   protected final void clearReadPending() {
      if (this.isRegistered()) {
         NioEventLoop var1 = this.eventLoop();
         if (var1.inEventLoop()) {
            this.clearReadPending0();
         } else {
            var1.execute(this.clearReadPendingRunnable);
         }
      } else {
         this.readPending = false;
      }

   }

   private void setReadPending0(boolean var1) {
      this.readPending = var1;
      if (!var1) {
         ((AbstractNioChannel.AbstractNioUnsafe)this.unsafe()).removeReadOp();
      }

   }

   private void clearReadPending0() {
      this.readPending = false;
      ((AbstractNioChannel.AbstractNioUnsafe)this.unsafe()).removeReadOp();
   }

   protected boolean isCompatible(EventLoop var1) {
      return var1 instanceof NioEventLoop;
   }

   protected void doRegister() throws Exception {
      boolean var1 = false;

      while(true) {
         try {
            this.selectionKey = this.javaChannel().register(this.eventLoop().unwrappedSelector(), 0, this);
            return;
         } catch (CancelledKeyException var3) {
            if (var1) {
               throw var3;
            }

            this.eventLoop().selectNow();
            var1 = true;
         }
      }
   }

   protected void doDeregister() throws Exception {
      this.eventLoop().cancel(this.selectionKey());
   }

   protected void doBeginRead() throws Exception {
      SelectionKey var1 = this.selectionKey;
      if (var1.isValid()) {
         this.readPending = true;
         int var2 = var1.interestOps();
         if ((var2 & this.readInterestOp) == 0) {
            var1.interestOps(var2 | this.readInterestOp);
         }

      }
   }

   protected abstract boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

   protected abstract void doFinishConnect() throws Exception;

   protected final ByteBuf newDirectBuffer(ByteBuf var1) {
      int var2 = var1.readableBytes();
      if (var2 == 0) {
         ReferenceCountUtil.safeRelease(var1);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator var3 = this.alloc();
         ByteBuf var4;
         if (var3.isDirectBufferPooled()) {
            var4 = var3.directBuffer(var2);
            var4.writeBytes(var1, var1.readerIndex(), var2);
            ReferenceCountUtil.safeRelease(var1);
            return var4;
         } else {
            var4 = ByteBufUtil.threadLocalDirectBuffer();
            if (var4 != null) {
               var4.writeBytes(var1, var1.readerIndex(), var2);
               ReferenceCountUtil.safeRelease(var1);
               return var4;
            } else {
               return var1;
            }
         }
      }
   }

   protected final ByteBuf newDirectBuffer(ReferenceCounted var1, ByteBuf var2) {
      int var3 = var2.readableBytes();
      if (var3 == 0) {
         ReferenceCountUtil.safeRelease(var1);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator var4 = this.alloc();
         ByteBuf var5;
         if (var4.isDirectBufferPooled()) {
            var5 = var4.directBuffer(var3);
            var5.writeBytes(var2, var2.readerIndex(), var3);
            ReferenceCountUtil.safeRelease(var1);
            return var5;
         } else {
            var5 = ByteBufUtil.threadLocalDirectBuffer();
            if (var5 != null) {
               var5.writeBytes(var2, var2.readerIndex(), var3);
               ReferenceCountUtil.safeRelease(var1);
               return var5;
            } else {
               if (var1 != var2) {
                  var2.retain();
                  ReferenceCountUtil.safeRelease(var1);
               }

               return var2;
            }
         }
      }
   }

   protected void doClose() throws Exception {
      ChannelPromise var1 = this.connectPromise;
      if (var1 != null) {
         var1.tryFailure(DO_CLOSE_CLOSED_CHANNEL_EXCEPTION);
         this.connectPromise = null;
      }

      ScheduledFuture var2 = this.connectTimeoutFuture;
      if (var2 != null) {
         var2.cancel(false);
         this.connectTimeoutFuture = null;
      }

   }

   protected abstract class AbstractNioUnsafe extends AbstractChannel.AbstractUnsafe implements AbstractNioChannel.NioUnsafe {
      protected AbstractNioUnsafe() {
         super();
      }

      protected final void removeReadOp() {
         SelectionKey var1 = AbstractNioChannel.this.selectionKey();
         if (var1.isValid()) {
            int var2 = var1.interestOps();
            if ((var2 & AbstractNioChannel.this.readInterestOp) != 0) {
               var1.interestOps(var2 & ~AbstractNioChannel.this.readInterestOp);
            }

         }
      }

      public final SelectableChannel ch() {
         return AbstractNioChannel.this.javaChannel();
      }

      public final void connect(final SocketAddress var1, SocketAddress var2, ChannelPromise var3) {
         if (var3.setUncancellable() && this.ensureOpen(var3)) {
            try {
               if (AbstractNioChannel.this.connectPromise != null) {
                  throw new ConnectionPendingException();
               }

               boolean var4 = AbstractNioChannel.this.isActive();
               if (AbstractNioChannel.this.doConnect(var1, var2)) {
                  this.fulfillConnectPromise(var3, var4);
               } else {
                  AbstractNioChannel.this.connectPromise = var3;
                  AbstractNioChannel.this.requestedRemoteAddress = var1;
                  int var5 = AbstractNioChannel.this.config().getConnectTimeoutMillis();
                  if (var5 > 0) {
                     AbstractNioChannel.this.connectTimeoutFuture = AbstractNioChannel.this.eventLoop().schedule(new Runnable() {
                        public void run() {
                           ChannelPromise var1x = AbstractNioChannel.this.connectPromise;
                           ConnectTimeoutException var2 = new ConnectTimeoutException("connection timed out: " + var1);
                           if (var1x != null && var1x.tryFailure(var2)) {
                              AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                           }

                        }
                     }, (long)var5, TimeUnit.MILLISECONDS);
                  }

                  var3.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture var1) throws Exception {
                        if (var1.isCancelled()) {
                           if (AbstractNioChannel.this.connectTimeoutFuture != null) {
                              AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                           }

                           AbstractNioChannel.this.connectPromise = null;
                           AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                        }

                     }
                  });
               }
            } catch (Throwable var6) {
               var3.tryFailure(this.annotateConnectException(var6, var1));
               this.closeIfClosed();
            }

         }
      }

      private void fulfillConnectPromise(ChannelPromise var1, boolean var2) {
         if (var1 != null) {
            boolean var3 = AbstractNioChannel.this.isActive();
            boolean var4 = var1.trySuccess();
            if (!var2 && var3) {
               AbstractNioChannel.this.pipeline().fireChannelActive();
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

      public final void finishConnect() {
         assert AbstractNioChannel.this.eventLoop().inEventLoop();

         try {
            boolean var1 = AbstractNioChannel.this.isActive();
            AbstractNioChannel.this.doFinishConnect();
            this.fulfillConnectPromise(AbstractNioChannel.this.connectPromise, var1);
         } catch (Throwable var5) {
            this.fulfillConnectPromise(AbstractNioChannel.this.connectPromise, this.annotateConnectException(var5, AbstractNioChannel.this.requestedRemoteAddress));
         } finally {
            if (AbstractNioChannel.this.connectTimeoutFuture != null) {
               AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
            }

            AbstractNioChannel.this.connectPromise = null;
         }

      }

      protected final void flush0() {
         if (!this.isFlushPending()) {
            super.flush0();
         }

      }

      public final void forceFlush() {
         super.flush0();
      }

      private boolean isFlushPending() {
         SelectionKey var1 = AbstractNioChannel.this.selectionKey();
         return var1.isValid() && (var1.interestOps() & 4) != 0;
      }
   }

   public interface NioUnsafe extends Channel.Unsafe {
      SelectableChannel ch();

      void finishConnect();

      void read();

      void forceFlush();
   }
}
