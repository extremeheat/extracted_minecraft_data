package io.netty.channel.kqueue;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.SocketWritableByteChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.Executor;

public abstract class AbstractKQueueStreamChannel extends AbstractKQueueChannel implements DuplexChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractKQueueStreamChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
   private WritableByteChannel byteChannel;
   private final Runnable flushTask;

   AbstractKQueueStreamChannel(Channel var1, BsdSocket var2, boolean var3) {
      super(var1, var2, var3);
      this.flushTask = new Runnable() {
         public void run() {
            ((AbstractKQueueChannel.AbstractKQueueUnsafe)AbstractKQueueStreamChannel.this.unsafe()).flush0();
         }
      };
   }

   AbstractKQueueStreamChannel(Channel var1, BsdSocket var2, SocketAddress var3) {
      super(var1, var2, var3);
      this.flushTask = new Runnable() {
         public void run() {
            ((AbstractKQueueChannel.AbstractKQueueUnsafe)AbstractKQueueStreamChannel.this.unsafe()).flush0();
         }
      };
   }

   AbstractKQueueStreamChannel(BsdSocket var1) {
      this((Channel)null, var1, isSoErrorZero(var1));
   }

   protected AbstractKQueueChannel.AbstractKQueueUnsafe newUnsafe() {
      return new AbstractKQueueStreamChannel.KQueueStreamUnsafe();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   private int writeBytes(ChannelOutboundBuffer var1, ByteBuf var2) throws Exception {
      int var3 = var2.readableBytes();
      if (var3 == 0) {
         var1.remove();
         return 0;
      } else if (!var2.hasMemoryAddress() && var2.nioBufferCount() != 1) {
         ByteBuffer[] var4 = var2.nioBuffers();
         return this.writeBytesMultiple(var1, var4, var4.length, (long)var3, this.config().getMaxBytesPerGatheringWrite());
      } else {
         return this.doWriteBytes(var1, var2);
      }
   }

   private void adjustMaxBytesPerGatheringWrite(long var1, long var3, long var5) {
      if (var1 == var3) {
         if (var1 << 1 > var5) {
            this.config().setMaxBytesPerGatheringWrite(var1 << 1);
         }
      } else if (var1 > 4096L && var3 < var1 >>> 1) {
         this.config().setMaxBytesPerGatheringWrite(var1 >>> 1);
      }

   }

   private int writeBytesMultiple(ChannelOutboundBuffer var1, IovArray var2) throws IOException {
      long var3 = var2.size();

      assert var3 != 0L;

      int var5 = var2.count();

      assert var5 != 0;

      long var6 = this.socket.writevAddresses(var2.memoryAddress(0), var5);
      if (var6 > 0L) {
         this.adjustMaxBytesPerGatheringWrite(var3, var6, var2.maxBytes());
         var1.removeBytes(var6);
         return 1;
      } else {
         return 2147483647;
      }
   }

   private int writeBytesMultiple(ChannelOutboundBuffer var1, ByteBuffer[] var2, int var3, long var4, long var6) throws IOException {
      assert var4 != 0L;

      if (var4 > var6) {
         var4 = var6;
      }

      long var8 = this.socket.writev(var2, 0, var3, var4);
      if (var8 > 0L) {
         this.adjustMaxBytesPerGatheringWrite(var4, var8, var6);
         var1.removeBytes(var8);
         return 1;
      } else {
         return 2147483647;
      }
   }

   private int writeDefaultFileRegion(ChannelOutboundBuffer var1, DefaultFileRegion var2) throws Exception {
      long var3 = var2.count();
      if (var2.transferred() >= var3) {
         var1.remove();
         return 0;
      } else {
         long var5 = var2.transferred();
         long var7 = this.socket.sendFile(var2, var2.position(), var5, var3 - var5);
         if (var7 > 0L) {
            var1.progress(var7);
            if (var2.transferred() >= var3) {
               var1.remove();
            }

            return 1;
         } else {
            return 2147483647;
         }
      }
   }

   private int writeFileRegion(ChannelOutboundBuffer var1, FileRegion var2) throws Exception {
      if (var2.transferred() >= var2.count()) {
         var1.remove();
         return 0;
      } else {
         if (this.byteChannel == null) {
            this.byteChannel = new AbstractKQueueStreamChannel.KQueueSocketWritableByteChannel();
         }

         long var3 = var2.transferTo(this.byteChannel, var2.transferred());
         if (var3 > 0L) {
            var1.progress(var3);
            if (var2.transferred() >= var2.count()) {
               var1.remove();
            }

            return 1;
         } else {
            return 2147483647;
         }
      }
   }

   protected void doWrite(ChannelOutboundBuffer var1) throws Exception {
      int var2 = this.config().getWriteSpinCount();

      do {
         int var3 = var1.size();
         if (var3 > 1 && var1.current() instanceof ByteBuf) {
            var2 -= this.doWriteMultiple(var1);
         } else {
            if (var3 == 0) {
               this.writeFilter(false);
               return;
            }

            var2 -= this.doWriteSingle(var1);
         }
      } while(var2 > 0);

      if (var2 == 0) {
         this.writeFilter(false);
         this.eventLoop().execute(this.flushTask);
      } else {
         this.writeFilter(true);
      }

   }

   protected int doWriteSingle(ChannelOutboundBuffer var1) throws Exception {
      Object var2 = var1.current();
      if (var2 instanceof ByteBuf) {
         return this.writeBytes(var1, (ByteBuf)var2);
      } else if (var2 instanceof DefaultFileRegion) {
         return this.writeDefaultFileRegion(var1, (DefaultFileRegion)var2);
      } else if (var2 instanceof FileRegion) {
         return this.writeFileRegion(var1, (FileRegion)var2);
      } else {
         throw new Error();
      }
   }

   private int doWriteMultiple(ChannelOutboundBuffer var1) throws Exception {
      long var2 = this.config().getMaxBytesPerGatheringWrite();
      if (PlatformDependent.hasUnsafe()) {
         IovArray var4 = ((KQueueEventLoop)this.eventLoop()).cleanArray();
         var4.maxBytes(var2);
         var1.forEachFlushedMessage(var4);
         if (var4.count() >= 1) {
            return this.writeBytesMultiple(var1, var4);
         }
      } else {
         ByteBuffer[] var6 = var1.nioBuffers();
         int var5 = var1.nioBufferCount();
         if (var5 >= 1) {
            return this.writeBytesMultiple(var1, var6, var5, var1.nioBufferSize(), var2);
         }
      }

      var1.removeBytes(0L);
      return 0;
   }

   protected Object filterOutboundMessage(Object var1) {
      if (var1 instanceof ByteBuf) {
         ByteBuf var2 = (ByteBuf)var1;
         return UnixChannelUtil.isBufferCopyNeededForWrite(var2) ? this.newDirectBuffer(var2) : var2;
      } else if (var1 instanceof FileRegion) {
         return var1;
      } else {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      }
   }

   protected final void doShutdownOutput() throws Exception {
      this.socket.shutdown(false, true);
   }

   public boolean isOutputShutdown() {
      return this.socket.isOutputShutdown();
   }

   public boolean isInputShutdown() {
      return this.socket.isInputShutdown();
   }

   public boolean isShutdown() {
      return this.socket.isShutdown();
   }

   public ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   public ChannelFuture shutdownOutput(final ChannelPromise var1) {
      EventLoop var2 = this.eventLoop();
      if (var2.inEventLoop()) {
         ((AbstractChannel.AbstractUnsafe)this.unsafe()).shutdownOutput(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               ((AbstractChannel.AbstractUnsafe)AbstractKQueueStreamChannel.this.unsafe()).shutdownOutput(var1);
            }
         });
      }

      return var1;
   }

   public ChannelFuture shutdownInput() {
      return this.shutdownInput(this.newPromise());
   }

   public ChannelFuture shutdownInput(final ChannelPromise var1) {
      EventLoop var2 = this.eventLoop();
      if (var2.inEventLoop()) {
         this.shutdownInput0(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               AbstractKQueueStreamChannel.this.shutdownInput0(var1);
            }
         });
      }

      return var1;
   }

   private void shutdownInput0(ChannelPromise var1) {
      try {
         this.socket.shutdown(true, false);
      } catch (Throwable var3) {
         var1.setFailure(var3);
         return;
      }

      var1.setSuccess();
   }

   public ChannelFuture shutdown() {
      return this.shutdown(this.newPromise());
   }

   public ChannelFuture shutdown(final ChannelPromise var1) {
      ChannelFuture var2 = this.shutdownOutput();
      if (var2.isDone()) {
         this.shutdownOutputDone(var2, var1);
      } else {
         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               AbstractKQueueStreamChannel.this.shutdownOutputDone(var1x, var1);
            }
         });
      }

      return var1;
   }

   private void shutdownOutputDone(final ChannelFuture var1, final ChannelPromise var2) {
      ChannelFuture var3 = this.shutdownInput();
      if (var3.isDone()) {
         shutdownDone(var1, var3, var2);
      } else {
         var3.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               AbstractKQueueStreamChannel.shutdownDone(var1, var1x, var2);
            }
         });
      }

   }

   private static void shutdownDone(ChannelFuture var0, ChannelFuture var1, ChannelPromise var2) {
      Throwable var3 = var0.cause();
      Throwable var4 = var1.cause();
      if (var3 != null) {
         if (var4 != null) {
            logger.debug("Exception suppressed because a previous exception occurred.", var4);
         }

         var2.setFailure(var3);
      } else if (var4 != null) {
         var2.setFailure(var4);
      } else {
         var2.setSuccess();
      }

   }

   private final class KQueueSocketWritableByteChannel extends SocketWritableByteChannel {
      KQueueSocketWritableByteChannel() {
         super(AbstractKQueueStreamChannel.this.socket);
      }

      protected ByteBufAllocator alloc() {
         return AbstractKQueueStreamChannel.this.alloc();
      }
   }

   class KQueueStreamUnsafe extends AbstractKQueueChannel.AbstractKQueueUnsafe {
      KQueueStreamUnsafe() {
         super();
      }

      protected Executor prepareToClose() {
         return super.prepareToClose();
      }

      void readReady(KQueueRecvByteAllocatorHandle var1) {
         KQueueChannelConfig var2 = AbstractKQueueStreamChannel.this.config();
         if (AbstractKQueueStreamChannel.this.shouldBreakReadReady(var2)) {
            this.clearReadFilter0();
         } else {
            ChannelPipeline var3 = AbstractKQueueStreamChannel.this.pipeline();
            ByteBufAllocator var4 = var2.getAllocator();
            var1.reset(var2);
            this.readReadyBefore();
            ByteBuf var5 = null;
            boolean var6 = false;

            try {
               do {
                  var5 = var1.allocate(var4);
                  var1.lastBytesRead(AbstractKQueueStreamChannel.this.doReadBytes(var5));
                  if (var1.lastBytesRead() <= 0) {
                     var5.release();
                     var5 = null;
                     var6 = var1.lastBytesRead() < 0;
                     if (var6) {
                        this.readPending = false;
                     }
                     break;
                  }

                  var1.incMessagesRead(1);
                  this.readPending = false;
                  var3.fireChannelRead(var5);
                  var5 = null;
               } while(!AbstractKQueueStreamChannel.this.shouldBreakReadReady(var2) && var1.continueReading());

               var1.readComplete();
               var3.fireChannelReadComplete();
               if (var6) {
                  this.shutdownInput(false);
               }
            } catch (Throwable var11) {
               this.handleReadException(var3, var5, var11, var6, var1);
            } finally {
               this.readReadyFinally(var2);
            }

         }
      }

      private void handleReadException(ChannelPipeline var1, ByteBuf var2, Throwable var3, boolean var4, KQueueRecvByteAllocatorHandle var5) {
         if (var2 != null) {
            if (var2.isReadable()) {
               this.readPending = false;
               var1.fireChannelRead(var2);
            } else {
               var2.release();
            }
         }

         if (!this.failConnectPromise(var3)) {
            var5.readComplete();
            var1.fireChannelReadComplete();
            var1.fireExceptionCaught(var3);
            if (var4 || var3 instanceof IOException) {
               this.shutdownInput(false);
            }
         }

      }
   }
}
