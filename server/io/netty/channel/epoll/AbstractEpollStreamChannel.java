package io.netty.channel.epoll;

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
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DuplexChannel;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.channel.unix.SocketWritableByteChannel;
import io.netty.channel.unix.UnixChannelUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;
import java.util.Queue;
import java.util.concurrent.Executor;

public abstract class AbstractEpollStreamChannel extends AbstractEpollChannel implements DuplexChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(DefaultFileRegion.class) + ')';
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractEpollStreamChannel.class);
   private static final ClosedChannelException CLEAR_SPLICE_QUEUE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "clearSpliceQueue()");
   private static final ClosedChannelException SPLICE_TO_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "spliceTo(...)");
   private static final ClosedChannelException FAIL_SPLICE_IF_CLOSED_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), AbstractEpollStreamChannel.class, "failSpliceIfClosed(...)");
   private final Runnable flushTask;
   private Queue<AbstractEpollStreamChannel.SpliceInTask> spliceQueue;
   private FileDescriptor pipeIn;
   private FileDescriptor pipeOut;
   private WritableByteChannel byteChannel;

   protected AbstractEpollStreamChannel(Channel var1, int var2) {
      this(var1, new LinuxSocket(var2));
   }

   protected AbstractEpollStreamChannel(int var1) {
      this(new LinuxSocket(var1));
   }

   AbstractEpollStreamChannel(LinuxSocket var1) {
      this(var1, isSoErrorZero(var1));
   }

   AbstractEpollStreamChannel(Channel var1, LinuxSocket var2) {
      super(var1, var2, Native.EPOLLIN, true);
      this.flushTask = new Runnable() {
         public void run() {
            ((AbstractEpollChannel.AbstractEpollUnsafe)AbstractEpollStreamChannel.this.unsafe()).flush0();
         }
      };
      this.flags |= Native.EPOLLRDHUP;
   }

   AbstractEpollStreamChannel(Channel var1, LinuxSocket var2, SocketAddress var3) {
      super(var1, var2, Native.EPOLLIN, var3);
      this.flushTask = new Runnable() {
         public void run() {
            ((AbstractEpollChannel.AbstractEpollUnsafe)AbstractEpollStreamChannel.this.unsafe()).flush0();
         }
      };
      this.flags |= Native.EPOLLRDHUP;
   }

   protected AbstractEpollStreamChannel(LinuxSocket var1, boolean var2) {
      super((Channel)null, var1, Native.EPOLLIN, var2);
      this.flushTask = new Runnable() {
         public void run() {
            ((AbstractEpollChannel.AbstractEpollUnsafe)AbstractEpollStreamChannel.this.unsafe()).flush0();
         }
      };
      this.flags |= Native.EPOLLRDHUP;
   }

   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new AbstractEpollStreamChannel.EpollStreamUnsafe();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public final ChannelFuture spliceTo(AbstractEpollStreamChannel var1, int var2) {
      return this.spliceTo(var1, var2, this.newPromise());
   }

   public final ChannelFuture spliceTo(AbstractEpollStreamChannel var1, int var2, ChannelPromise var3) {
      if (var1.eventLoop() != this.eventLoop()) {
         throw new IllegalArgumentException("EventLoops are not the same.");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("len: " + var2 + " (expected: >= 0)");
      } else if (var1.config().getEpollMode() == EpollMode.LEVEL_TRIGGERED && this.config().getEpollMode() == EpollMode.LEVEL_TRIGGERED) {
         ObjectUtil.checkNotNull(var3, "promise");
         if (!this.isOpen()) {
            var3.tryFailure(SPLICE_TO_CLOSED_CHANNEL_EXCEPTION);
         } else {
            this.addToSpliceQueue(new AbstractEpollStreamChannel.SpliceInChannelTask(var1, var2, var3));
            this.failSpliceIfClosed(var3);
         }

         return var3;
      } else {
         throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
      }
   }

   public final ChannelFuture spliceTo(FileDescriptor var1, int var2, int var3) {
      return this.spliceTo(var1, var2, var3, this.newPromise());
   }

   public final ChannelFuture spliceTo(FileDescriptor var1, int var2, int var3, ChannelPromise var4) {
      if (var3 < 0) {
         throw new IllegalArgumentException("len: " + var3 + " (expected: >= 0)");
      } else if (var2 < 0) {
         throw new IllegalArgumentException("offset must be >= 0 but was " + var2);
      } else if (this.config().getEpollMode() != EpollMode.LEVEL_TRIGGERED) {
         throw new IllegalStateException("spliceTo() supported only when using " + EpollMode.LEVEL_TRIGGERED);
      } else {
         ObjectUtil.checkNotNull(var4, "promise");
         if (!this.isOpen()) {
            var4.tryFailure(SPLICE_TO_CLOSED_CHANNEL_EXCEPTION);
         } else {
            this.addToSpliceQueue(new AbstractEpollStreamChannel.SpliceFdTask(var1, var2, var3, var4));
            this.failSpliceIfClosed(var4);
         }

         return var4;
      }
   }

   private void failSpliceIfClosed(ChannelPromise var1) {
      if (!this.isOpen() && var1.tryFailure(FAIL_SPLICE_IF_CLOSED_CLOSED_CHANNEL_EXCEPTION)) {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               AbstractEpollStreamChannel.this.clearSpliceQueue();
            }
         });
      }

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
            this.byteChannel = new AbstractEpollStreamChannel.EpollSocketWritableByteChannel();
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
               this.clearFlag(Native.EPOLLOUT);
               return;
            }

            var2 -= this.doWriteSingle(var1);
         }
      } while(var2 > 0);

      if (var2 == 0) {
         this.clearFlag(Native.EPOLLOUT);
         this.eventLoop().execute(this.flushTask);
      } else {
         this.setFlag(Native.EPOLLOUT);
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
      } else if (var2 instanceof AbstractEpollStreamChannel.SpliceOutTask) {
         if (!((AbstractEpollStreamChannel.SpliceOutTask)var2).spliceOut()) {
            return 2147483647;
         } else {
            var1.remove();
            return 1;
         }
      } else {
         throw new Error();
      }
   }

   private int doWriteMultiple(ChannelOutboundBuffer var1) throws Exception {
      long var2 = this.config().getMaxBytesPerGatheringWrite();
      if (PlatformDependent.hasUnsafe()) {
         IovArray var4 = ((EpollEventLoop)this.eventLoop()).cleanArray();
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
      } else if (!(var1 instanceof FileRegion) && !(var1 instanceof AbstractEpollStreamChannel.SpliceOutTask)) {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(var1) + EXPECTED_TYPES);
      } else {
         return var1;
      }
   }

   protected final void doShutdownOutput() throws Exception {
      this.socket.shutdown(false, true);
   }

   private void shutdownInput0(ChannelPromise var1) {
      try {
         this.socket.shutdown(true, false);
         var1.setSuccess();
      } catch (Throwable var3) {
         var1.setFailure(var3);
      }

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
               ((AbstractChannel.AbstractUnsafe)AbstractEpollStreamChannel.this.unsafe()).shutdownOutput(var1);
            }
         });
      }

      return var1;
   }

   public ChannelFuture shutdownInput() {
      return this.shutdownInput(this.newPromise());
   }

   public ChannelFuture shutdownInput(final ChannelPromise var1) {
      Executor var2 = ((AbstractEpollStreamChannel.EpollStreamUnsafe)this.unsafe()).prepareToClose();
      if (var2 != null) {
         var2.execute(new Runnable() {
            public void run() {
               AbstractEpollStreamChannel.this.shutdownInput0(var1);
            }
         });
      } else {
         EventLoop var3 = this.eventLoop();
         if (var3.inEventLoop()) {
            this.shutdownInput0(var1);
         } else {
            var3.execute(new Runnable() {
               public void run() {
                  AbstractEpollStreamChannel.this.shutdownInput0(var1);
               }
            });
         }
      }

      return var1;
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
               AbstractEpollStreamChannel.this.shutdownOutputDone(var1x, var1);
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
               AbstractEpollStreamChannel.shutdownDone(var1, var1x, var2);
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

   protected void doClose() throws Exception {
      try {
         super.doClose();
      } finally {
         safeClosePipe(this.pipeIn);
         safeClosePipe(this.pipeOut);
         this.clearSpliceQueue();
      }

   }

   private void clearSpliceQueue() {
      if (this.spliceQueue != null) {
         while(true) {
            AbstractEpollStreamChannel.SpliceInTask var1 = (AbstractEpollStreamChannel.SpliceInTask)this.spliceQueue.poll();
            if (var1 == null) {
               return;
            }

            var1.promise.tryFailure(CLEAR_SPLICE_QUEUE_CLOSED_CHANNEL_EXCEPTION);
         }
      }
   }

   private static void safeClosePipe(FileDescriptor var0) {
      if (var0 != null) {
         try {
            var0.close();
         } catch (IOException var2) {
            if (logger.isWarnEnabled()) {
               logger.warn("Error while closing a pipe", (Throwable)var2);
            }
         }
      }

   }

   private void addToSpliceQueue(final AbstractEpollStreamChannel.SpliceInTask var1) {
      EventLoop var2 = this.eventLoop();
      if (var2.inEventLoop()) {
         this.addToSpliceQueue0(var1);
      } else {
         var2.execute(new Runnable() {
            public void run() {
               AbstractEpollStreamChannel.this.addToSpliceQueue0(var1);
            }
         });
      }

   }

   private void addToSpliceQueue0(AbstractEpollStreamChannel.SpliceInTask var1) {
      if (this.spliceQueue == null) {
         this.spliceQueue = PlatformDependent.newMpscQueue();
      }

      this.spliceQueue.add(var1);
   }

   private final class EpollSocketWritableByteChannel extends SocketWritableByteChannel {
      EpollSocketWritableByteChannel() {
         super(AbstractEpollStreamChannel.this.socket);
      }

      protected ByteBufAllocator alloc() {
         return AbstractEpollStreamChannel.this.alloc();
      }
   }

   private final class SpliceFdTask extends AbstractEpollStreamChannel.SpliceInTask {
      private final FileDescriptor fd;
      private final ChannelPromise promise;
      private final int offset;

      SpliceFdTask(FileDescriptor var2, int var3, int var4, ChannelPromise var5) {
         super(var4, var5);
         this.fd = var2;
         this.promise = var5;
         this.offset = var3;
      }

      public boolean spliceIn(RecvByteBufAllocator.Handle var1) {
         assert AbstractEpollStreamChannel.this.eventLoop().inEventLoop();

         if (this.len == 0) {
            this.promise.setSuccess();
            return true;
         } else {
            try {
               FileDescriptor[] var2 = FileDescriptor.pipe();
               FileDescriptor var3 = var2[0];
               FileDescriptor var4 = var2[1];

               try {
                  int var5 = this.spliceIn(var4, var1);
                  boolean var12;
                  if (var5 > 0) {
                     if (this.len != 2147483647) {
                        this.len -= var5;
                     }

                     while(true) {
                        int var6 = Native.splice(var3.intValue(), -1L, this.fd.intValue(), (long)this.offset, (long)var5);
                        var5 -= var6;
                        if (var5 <= 0) {
                           if (this.len == 0) {
                              this.promise.setSuccess();
                              var12 = true;
                              return var12;
                           }
                           break;
                        }
                     }
                  }

                  var12 = false;
                  return var12;
               } finally {
                  AbstractEpollStreamChannel.safeClosePipe(var3);
                  AbstractEpollStreamChannel.safeClosePipe(var4);
               }
            } catch (Throwable var11) {
               this.promise.setFailure(var11);
               return true;
            }
         }
      }
   }

   private final class SpliceOutTask {
      private final AbstractEpollStreamChannel ch;
      private final boolean autoRead;
      private int len;

      SpliceOutTask(AbstractEpollStreamChannel var2, int var3, boolean var4) {
         super();
         this.ch = var2;
         this.len = var3;
         this.autoRead = var4;
      }

      public boolean spliceOut() throws Exception {
         assert this.ch.eventLoop().inEventLoop();

         try {
            int var1 = Native.splice(this.ch.pipeIn.intValue(), -1L, this.ch.socket.intValue(), -1L, (long)this.len);
            this.len -= var1;
            if (this.len == 0) {
               if (this.autoRead) {
                  AbstractEpollStreamChannel.this.config().setAutoRead(true);
               }

               return true;
            } else {
               return false;
            }
         } catch (IOException var2) {
            if (this.autoRead) {
               AbstractEpollStreamChannel.this.config().setAutoRead(true);
            }

            throw var2;
         }
      }
   }

   private final class SpliceInChannelTask extends AbstractEpollStreamChannel.SpliceInTask implements ChannelFutureListener {
      private final AbstractEpollStreamChannel ch;

      SpliceInChannelTask(AbstractEpollStreamChannel var2, int var3, ChannelPromise var4) {
         super(var3, var4);
         this.ch = var2;
      }

      public void operationComplete(ChannelFuture var1) throws Exception {
         if (!var1.isSuccess()) {
            this.promise.setFailure(var1.cause());
         }

      }

      public boolean spliceIn(RecvByteBufAllocator.Handle var1) {
         assert this.ch.eventLoop().inEventLoop();

         if (this.len == 0) {
            this.promise.setSuccess();
            return true;
         } else {
            try {
               FileDescriptor var2 = this.ch.pipeOut;
               if (var2 == null) {
                  FileDescriptor[] var3 = FileDescriptor.pipe();
                  this.ch.pipeIn = var3[0];
                  var2 = this.ch.pipeOut = var3[1];
               }

               int var7 = this.spliceIn(var2, var1);
               if (var7 > 0) {
                  if (this.len != 2147483647) {
                     this.len -= var7;
                  }

                  ChannelPromise var4;
                  if (this.len == 0) {
                     var4 = this.promise;
                  } else {
                     var4 = this.ch.newPromise().addListener(this);
                  }

                  boolean var5 = AbstractEpollStreamChannel.this.config().isAutoRead();
                  this.ch.unsafe().write(AbstractEpollStreamChannel.this.new SpliceOutTask(this.ch, var7, var5), var4);
                  this.ch.unsafe().flush();
                  if (var5 && !var4.isDone()) {
                     AbstractEpollStreamChannel.this.config().setAutoRead(false);
                  }
               }

               return this.len == 0;
            } catch (Throwable var6) {
               this.promise.setFailure(var6);
               return true;
            }
         }
      }
   }

   protected abstract class SpliceInTask {
      final ChannelPromise promise;
      int len;

      protected SpliceInTask(int var2, ChannelPromise var3) {
         super();
         this.promise = var3;
         this.len = var2;
      }

      abstract boolean spliceIn(RecvByteBufAllocator.Handle var1);

      protected final int spliceIn(FileDescriptor var1, RecvByteBufAllocator.Handle var2) throws IOException {
         int var3 = Math.min(var2.guess(), this.len);
         int var4 = 0;

         while(true) {
            int var5 = Native.splice(AbstractEpollStreamChannel.this.socket.intValue(), -1L, var1.intValue(), -1L, (long)var3);
            if (var5 == 0) {
               return var4;
            }

            var4 += var5;
            var3 -= var5;
         }
      }
   }

   class EpollStreamUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      EpollStreamUnsafe() {
         super();
      }

      protected Executor prepareToClose() {
         return super.prepareToClose();
      }

      private void handleReadException(ChannelPipeline var1, ByteBuf var2, Throwable var3, boolean var4, EpollRecvByteAllocatorHandle var5) {
         if (var2 != null) {
            if (var2.isReadable()) {
               this.readPending = false;
               var1.fireChannelRead(var2);
            } else {
               var2.release();
            }
         }

         var5.readComplete();
         var1.fireChannelReadComplete();
         var1.fireExceptionCaught(var3);
         if (var4 || var3 instanceof IOException) {
            this.shutdownInput(false);
         }

      }

      EpollRecvByteAllocatorHandle newEpollHandle(RecvByteBufAllocator.ExtendedHandle var1) {
         return new EpollRecvByteAllocatorStreamingHandle(var1);
      }

      void epollInReady() {
         EpollChannelConfig var1 = AbstractEpollStreamChannel.this.config();
         if (AbstractEpollStreamChannel.this.shouldBreakEpollInReady(var1)) {
            this.clearEpollIn0();
         } else {
            EpollRecvByteAllocatorHandle var2 = this.recvBufAllocHandle();
            var2.edgeTriggered(AbstractEpollStreamChannel.this.isFlagSet(Native.EPOLLET));
            ChannelPipeline var3 = AbstractEpollStreamChannel.this.pipeline();
            ByteBufAllocator var4 = var1.getAllocator();
            var2.reset(var1);
            this.epollInBefore();
            ByteBuf var5 = null;
            boolean var6 = false;

            try {
               do {
                  if (AbstractEpollStreamChannel.this.spliceQueue != null) {
                     AbstractEpollStreamChannel.SpliceInTask var7 = (AbstractEpollStreamChannel.SpliceInTask)AbstractEpollStreamChannel.this.spliceQueue.peek();
                     if (var7 != null) {
                        if (!var7.spliceIn(var2)) {
                           break;
                        }

                        if (AbstractEpollStreamChannel.this.isActive()) {
                           AbstractEpollStreamChannel.this.spliceQueue.remove();
                        }
                        continue;
                     }
                  }

                  var5 = var2.allocate(var4);
                  var2.lastBytesRead(AbstractEpollStreamChannel.this.doReadBytes(var5));
                  if (var2.lastBytesRead() <= 0) {
                     var5.release();
                     var5 = null;
                     var6 = var2.lastBytesRead() < 0;
                     if (var6) {
                        this.readPending = false;
                     }
                     break;
                  }

                  var2.incMessagesRead(1);
                  this.readPending = false;
                  var3.fireChannelRead(var5);
                  var5 = null;
                  if (AbstractEpollStreamChannel.this.shouldBreakEpollInReady(var1)) {
                     break;
                  }
               } while(var2.continueReading());

               var2.readComplete();
               var3.fireChannelReadComplete();
               if (var6) {
                  this.shutdownInput(false);
               }
            } catch (Throwable var11) {
               this.handleReadException(var3, var5, var11, var6, var2);
            } finally {
               this.epollInFinally(var1);
            }

         }
      }
   }
}
