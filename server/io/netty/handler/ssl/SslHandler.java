package io.netty.handler.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractCoalescingBufferQueue;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ChannelPromiseNotifier;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.ImmediateExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;

public class SslHandler extends ByteToMessageDecoder implements ChannelOutboundHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(SslHandler.class);
   private static final Pattern IGNORABLE_CLASS_IN_STACK = Pattern.compile("^.*(?:Socket|Datagram|Sctp|Udt)Channel.*$");
   private static final Pattern IGNORABLE_ERROR_MESSAGE = Pattern.compile("^.*(?:connection.*(?:reset|closed|abort|broken)|broken.*pipe).*$", 2);
   private static final SSLException SSLENGINE_CLOSED = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("SSLEngine closed already"), SslHandler.class, "wrap(...)");
   private static final SSLException HANDSHAKE_TIMED_OUT = (SSLException)ThrowableUtil.unknownStackTrace(new SSLException("handshake timed out"), SslHandler.class, "handshake(...)");
   private static final ClosedChannelException CHANNEL_CLOSED = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), SslHandler.class, "channelInactive(...)");
   private static final int MAX_PLAINTEXT_LENGTH = 16384;
   private volatile ChannelHandlerContext ctx;
   private final SSLEngine engine;
   private final SslHandler.SslEngineType engineType;
   private final Executor delegatedTaskExecutor;
   private final boolean jdkCompatibilityMode;
   private final ByteBuffer[] singleBuffer;
   private final boolean startTls;
   private boolean sentFirstMessage;
   private boolean flushedBeforeHandshake;
   private boolean readDuringHandshake;
   private boolean handshakeStarted;
   private SslHandler.SslHandlerCoalescingBufferQueue pendingUnencryptedWrites;
   private Promise<Channel> handshakePromise;
   private final SslHandler.LazyChannelPromise sslClosePromise;
   private boolean needsFlush;
   private boolean outboundClosed;
   private boolean closeNotify;
   private int packetLength;
   private boolean firedChannelRead;
   private volatile long handshakeTimeoutMillis;
   private volatile long closeNotifyFlushTimeoutMillis;
   private volatile long closeNotifyReadTimeoutMillis;
   volatile int wrapDataSize;

   public SslHandler(SSLEngine var1) {
      this(var1, false);
   }

   public SslHandler(SSLEngine var1, boolean var2) {
      this(var1, var2, ImmediateExecutor.INSTANCE);
   }

   /** @deprecated */
   @Deprecated
   public SslHandler(SSLEngine var1, Executor var2) {
      this(var1, false, var2);
   }

   /** @deprecated */
   @Deprecated
   public SslHandler(SSLEngine var1, boolean var2, Executor var3) {
      super();
      this.singleBuffer = new ByteBuffer[1];
      this.handshakePromise = new SslHandler.LazyChannelPromise();
      this.sslClosePromise = new SslHandler.LazyChannelPromise();
      this.handshakeTimeoutMillis = 10000L;
      this.closeNotifyFlushTimeoutMillis = 3000L;
      this.wrapDataSize = 16384;
      if (var1 == null) {
         throw new NullPointerException("engine");
      } else if (var3 == null) {
         throw new NullPointerException("delegatedTaskExecutor");
      } else {
         this.engine = var1;
         this.engineType = SslHandler.SslEngineType.forEngine(var1);
         this.delegatedTaskExecutor = var3;
         this.startTls = var2;
         this.jdkCompatibilityMode = this.engineType.jdkCompatibilityMode(var1);
         this.setCumulator(this.engineType.cumulator);
      }
   }

   public long getHandshakeTimeoutMillis() {
      return this.handshakeTimeoutMillis;
   }

   public void setHandshakeTimeout(long var1, TimeUnit var3) {
      if (var3 == null) {
         throw new NullPointerException("unit");
      } else {
         this.setHandshakeTimeoutMillis(var3.toMillis(var1));
      }
   }

   public void setHandshakeTimeoutMillis(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("handshakeTimeoutMillis: " + var1 + " (expected: >= 0)");
      } else {
         this.handshakeTimeoutMillis = var1;
      }
   }

   public final void setWrapDataSize(int var1) {
      this.wrapDataSize = var1;
   }

   /** @deprecated */
   @Deprecated
   public long getCloseNotifyTimeoutMillis() {
      return this.getCloseNotifyFlushTimeoutMillis();
   }

   /** @deprecated */
   @Deprecated
   public void setCloseNotifyTimeout(long var1, TimeUnit var3) {
      this.setCloseNotifyFlushTimeout(var1, var3);
   }

   /** @deprecated */
   @Deprecated
   public void setCloseNotifyTimeoutMillis(long var1) {
      this.setCloseNotifyFlushTimeoutMillis(var1);
   }

   public final long getCloseNotifyFlushTimeoutMillis() {
      return this.closeNotifyFlushTimeoutMillis;
   }

   public final void setCloseNotifyFlushTimeout(long var1, TimeUnit var3) {
      this.setCloseNotifyFlushTimeoutMillis(var3.toMillis(var1));
   }

   public final void setCloseNotifyFlushTimeoutMillis(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("closeNotifyFlushTimeoutMillis: " + var1 + " (expected: >= 0)");
      } else {
         this.closeNotifyFlushTimeoutMillis = var1;
      }
   }

   public final long getCloseNotifyReadTimeoutMillis() {
      return this.closeNotifyReadTimeoutMillis;
   }

   public final void setCloseNotifyReadTimeout(long var1, TimeUnit var3) {
      this.setCloseNotifyReadTimeoutMillis(var3.toMillis(var1));
   }

   public final void setCloseNotifyReadTimeoutMillis(long var1) {
      if (var1 < 0L) {
         throw new IllegalArgumentException("closeNotifyReadTimeoutMillis: " + var1 + " (expected: >= 0)");
      } else {
         this.closeNotifyReadTimeoutMillis = var1;
      }
   }

   public SSLEngine engine() {
      return this.engine;
   }

   public String applicationProtocol() {
      SSLEngine var1 = this.engine();
      return !(var1 instanceof ApplicationProtocolAccessor) ? null : ((ApplicationProtocolAccessor)var1).getNegotiatedApplicationProtocol();
   }

   public Future<Channel> handshakeFuture() {
      return this.handshakePromise;
   }

   /** @deprecated */
   @Deprecated
   public ChannelFuture close() {
      return this.close(this.ctx.newPromise());
   }

   /** @deprecated */
   @Deprecated
   public ChannelFuture close(final ChannelPromise var1) {
      final ChannelHandlerContext var2 = this.ctx;
      var2.executor().execute(new Runnable() {
         public void run() {
            SslHandler.this.outboundClosed = true;
            SslHandler.this.engine.closeOutbound();

            try {
               SslHandler.this.flush(var2, var1);
            } catch (Exception var2x) {
               if (!var1.tryFailure(var2x)) {
                  SslHandler.logger.warn("{} flush() raised a masked exception.", var2.channel(), var2x);
               }
            }

         }
      });
      return var1;
   }

   public Future<Channel> sslCloseFuture() {
      return this.sslClosePromise;
   }

   public void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      if (!this.pendingUnencryptedWrites.isEmpty()) {
         this.pendingUnencryptedWrites.releaseAndFailAll(var1, new ChannelException("Pending write on removal of SslHandler"));
      }

      this.pendingUnencryptedWrites = null;
      if (this.engine instanceof ReferenceCounted) {
         ((ReferenceCounted)this.engine).release();
      }

   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.closeOutboundAndChannel(var1, var2, true);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      this.closeOutboundAndChannel(var1, var2, false);
   }

   public void read(ChannelHandlerContext var1) throws Exception {
      if (!this.handshakePromise.isDone()) {
         this.readDuringHandshake = true;
      }

      var1.read();
   }

   private static IllegalStateException newPendingWritesNullException() {
      return new IllegalStateException("pendingUnencryptedWrites is null, handlerRemoved0 called?");
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      if (!(var2 instanceof ByteBuf)) {
         UnsupportedMessageTypeException var4 = new UnsupportedMessageTypeException(var2, new Class[]{ByteBuf.class});
         ReferenceCountUtil.safeRelease(var2);
         var3.setFailure(var4);
      } else if (this.pendingUnencryptedWrites == null) {
         ReferenceCountUtil.safeRelease(var2);
         var3.setFailure(newPendingWritesNullException());
      } else {
         this.pendingUnencryptedWrites.add((ByteBuf)var2, var3);
      }

   }

   public void flush(ChannelHandlerContext var1) throws Exception {
      if (this.startTls && !this.sentFirstMessage) {
         this.sentFirstMessage = true;
         this.pendingUnencryptedWrites.writeAndRemoveAll(var1);
         this.forceFlush(var1);
      } else {
         try {
            this.wrapAndFlush(var1);
         } catch (Throwable var3) {
            this.setHandshakeFailure(var1, var3);
            PlatformDependent.throwException(var3);
         }

      }
   }

   private void wrapAndFlush(ChannelHandlerContext var1) throws SSLException {
      if (this.pendingUnencryptedWrites.isEmpty()) {
         this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, var1.newPromise());
      }

      if (!this.handshakePromise.isDone()) {
         this.flushedBeforeHandshake = true;
      }

      try {
         this.wrap(var1, false);
      } finally {
         this.forceFlush(var1);
      }

   }

   private void wrap(ChannelHandlerContext var1, boolean var2) throws SSLException {
      ByteBuf var3 = null;
      ChannelPromise var4 = null;
      ByteBufAllocator var5 = var1.alloc();
      boolean var6 = false;
      ByteBuf var7 = null;

      try {
         int var8 = this.wrapDataSize;

         while(!var1.isRemoved()) {
            var4 = var1.newPromise();
            var7 = var8 > 0 ? this.pendingUnencryptedWrites.remove(var5, var8, var4) : this.pendingUnencryptedWrites.removeFirst(var4);
            if (var7 == null) {
               return;
            }

            if (var3 == null) {
               var3 = this.allocateOutNetBuf(var1, var7.readableBytes(), var7.nioBufferCount());
            }

            SSLEngineResult var9 = this.wrap(var5, this.engine, var7, var3);
            if (var9.getStatus() == Status.CLOSED) {
               var7.release();
               var7 = null;
               var4.tryFailure(SSLENGINE_CLOSED);
               var4 = null;
               this.pendingUnencryptedWrites.releaseAndFailAll(var1, SSLENGINE_CLOSED);
               return;
            }

            if (var7.isReadable()) {
               this.pendingUnencryptedWrites.addFirst(var7, var4);
               var4 = null;
            } else {
               var7.release();
            }

            var7 = null;
            switch(var9.getHandshakeStatus()) {
            case NEED_TASK:
               this.runDelegatedTasks();
               break;
            case FINISHED:
               this.setHandshakeSuccess();
            case NOT_HANDSHAKING:
               this.setHandshakeSuccessIfStillHandshaking();
            case NEED_WRAP:
               this.finishWrap(var1, var3, var4, var2, false);
               var4 = null;
               var3 = null;
               break;
            case NEED_UNWRAP:
               var6 = true;
               return;
            default:
               throw new IllegalStateException("Unknown handshake status: " + var9.getHandshakeStatus());
            }
         }

      } finally {
         if (var7 != null) {
            var7.release();
         }

         this.finishWrap(var1, var3, var4, var2, var6);
      }
   }

   private void finishWrap(ChannelHandlerContext var1, ByteBuf var2, ChannelPromise var3, boolean var4, boolean var5) {
      if (var2 == null) {
         var2 = Unpooled.EMPTY_BUFFER;
      } else if (!var2.isReadable()) {
         var2.release();
         var2 = Unpooled.EMPTY_BUFFER;
      }

      if (var3 != null) {
         var1.write(var2, var3);
      } else {
         var1.write(var2);
      }

      if (var4) {
         this.needsFlush = true;
      }

      if (var5) {
         this.readIfNeeded(var1);
      }

   }

   private boolean wrapNonAppData(ChannelHandlerContext var1, boolean var2) throws SSLException {
      ByteBuf var3 = null;
      ByteBufAllocator var4 = var1.alloc();

      try {
         SSLEngineResult var5;
         do {
            if (var1.isRemoved()) {
               return false;
            }

            if (var3 == null) {
               var3 = this.allocateOutNetBuf(var1, 2048, 1);
            }

            var5 = this.wrap(var4, this.engine, Unpooled.EMPTY_BUFFER, var3);
            if (var5.bytesProduced() > 0) {
               var1.write(var3);
               if (var2) {
                  this.needsFlush = true;
               }

               var3 = null;
            }

            boolean var6;
            switch(var5.getHandshakeStatus()) {
            case NEED_TASK:
               this.runDelegatedTasks();
               break;
            case FINISHED:
               this.setHandshakeSuccess();
               var6 = false;
               return var6;
            case NOT_HANDSHAKING:
               this.setHandshakeSuccessIfStillHandshaking();
               if (!var2) {
                  this.unwrapNonAppData(var1);
               }

               var6 = true;
               return var6;
            case NEED_WRAP:
               break;
            case NEED_UNWRAP:
               if (var2) {
                  var6 = false;
                  return var6;
               }

               this.unwrapNonAppData(var1);
               break;
            default:
               throw new IllegalStateException("Unknown handshake status: " + var5.getHandshakeStatus());
            }
         } while(var5.bytesProduced() != 0 && (var5.bytesConsumed() != 0 || var5.getHandshakeStatus() != HandshakeStatus.NOT_HANDSHAKING));

         return false;
      } finally {
         if (var3 != null) {
            var3.release();
         }

      }
   }

   private SSLEngineResult wrap(ByteBufAllocator var1, SSLEngine var2, ByteBuf var3, ByteBuf var4) throws SSLException {
      ByteBuf var5 = null;

      try {
         int var6 = var3.readerIndex();
         int var7 = var3.readableBytes();
         ByteBuffer[] var8;
         if (!var3.isDirect() && this.engineType.wantsDirectBuffer) {
            var5 = var1.directBuffer(var7);
            var5.writeBytes(var3, var6, var7);
            var8 = this.singleBuffer;
            var8[0] = var5.internalNioBuffer(var5.readerIndex(), var7);
         } else if (!(var3 instanceof CompositeByteBuf) && var3.nioBufferCount() == 1) {
            var8 = this.singleBuffer;
            var8[0] = var3.internalNioBuffer(var6, var7);
         } else {
            var8 = var3.nioBuffers();
         }

         while(true) {
            ByteBuffer var9 = var4.nioBuffer(var4.writerIndex(), var4.writableBytes());
            SSLEngineResult var10 = var2.wrap(var8, var9);
            var3.skipBytes(var10.bytesConsumed());
            var4.writerIndex(var4.writerIndex() + var10.bytesProduced());
            switch(var10.getStatus()) {
            case BUFFER_OVERFLOW:
               var4.ensureWritable(var2.getSession().getPacketBufferSize());
               break;
            default:
               SSLEngineResult var11 = var10;
               return var11;
            }
         }
      } finally {
         this.singleBuffer[0] = null;
         if (var5 != null) {
            var5.release();
         }

      }
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      this.setHandshakeFailure(var1, CHANNEL_CLOSED, !this.outboundClosed, this.handshakeStarted, false);
      this.notifyClosePromise(CHANNEL_CLOSED);
      super.channelInactive(var1);
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (this.ignoreException(var2)) {
         if (logger.isDebugEnabled()) {
            logger.debug("{} Swallowing a harmless 'connection reset by peer / broken pipe' error that occurred while writing close_notify in response to the peer's close_notify", var1.channel(), var2);
         }

         if (var1.channel().isActive()) {
            var1.close();
         }
      } else {
         var1.fireExceptionCaught(var2);
      }

   }

   private boolean ignoreException(Throwable var1) {
      if (!(var1 instanceof SSLException) && var1 instanceof IOException && this.sslClosePromise.isDone()) {
         String var2 = var1.getMessage();
         if (var2 != null && IGNORABLE_ERROR_MESSAGE.matcher(var2).matches()) {
            return true;
         }

         StackTraceElement[] var3 = var1.getStackTrace();
         StackTraceElement[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            StackTraceElement var7 = var4[var6];
            String var8 = var7.getClassName();
            String var9 = var7.getMethodName();
            if (!var8.startsWith("io.netty.") && "read".equals(var9)) {
               if (IGNORABLE_CLASS_IN_STACK.matcher(var8).matches()) {
                  return true;
               }

               try {
                  Class var10 = PlatformDependent.getClassLoader(this.getClass()).loadClass(var8);
                  if (SocketChannel.class.isAssignableFrom(var10) || DatagramChannel.class.isAssignableFrom(var10)) {
                     return true;
                  }

                  if (PlatformDependent.javaVersion() >= 7 && "com.sun.nio.sctp.SctpChannel".equals(var10.getSuperclass().getName())) {
                     return true;
                  }
               } catch (Throwable var11) {
                  logger.debug("Unexpected exception while loading class {} classname {}", this.getClass(), var8, var11);
               }
            }
         }
      }

      return false;
   }

   public static boolean isEncrypted(ByteBuf var0) {
      if (var0.readableBytes() < 5) {
         throw new IllegalArgumentException("buffer must have at least 5 readable bytes");
      } else {
         return SslUtils.getEncryptedPacketLength(var0, var0.readerIndex()) != -2;
      }
   }

   private void decodeJdkCompatible(ChannelHandlerContext var1, ByteBuf var2) throws NotSslRecordException {
      int var3 = this.packetLength;
      int var4;
      if (var3 > 0) {
         if (var2.readableBytes() < var3) {
            return;
         }
      } else {
         var4 = var2.readableBytes();
         if (var4 < 5) {
            return;
         }

         var3 = SslUtils.getEncryptedPacketLength(var2, var2.readerIndex());
         if (var3 == -2) {
            NotSslRecordException var5 = new NotSslRecordException("not an SSL/TLS record: " + ByteBufUtil.hexDump(var2));
            var2.skipBytes(var2.readableBytes());
            this.setHandshakeFailure(var1, var5);
            throw var5;
         }

         assert var3 > 0;

         if (var3 > var4) {
            this.packetLength = var3;
            return;
         }
      }

      this.packetLength = 0;

      try {
         var4 = this.unwrap(var1, var2, var2.readerIndex(), var3);

         assert var4 == var3 || this.engine.isInboundDone() : "we feed the SSLEngine a packets worth of data: " + var3 + " but it only consumed: " + var4;

         var2.skipBytes(var4);
      } catch (Throwable var6) {
         this.handleUnwrapThrowable(var1, var6);
      }

   }

   private void decodeNonJdkCompatible(ChannelHandlerContext var1, ByteBuf var2) {
      try {
         var2.skipBytes(this.unwrap(var1, var2, var2.readerIndex(), var2.readableBytes()));
      } catch (Throwable var4) {
         this.handleUnwrapThrowable(var1, var4);
      }

   }

   private void handleUnwrapThrowable(ChannelHandlerContext var1, Throwable var2) {
      try {
         if (this.handshakePromise.tryFailure(var2)) {
            var1.fireUserEventTriggered(new SslHandshakeCompletionEvent(var2));
         }

         this.wrapAndFlush(var1);
      } catch (SSLException var7) {
         logger.debug("SSLException during trying to call SSLEngine.wrap(...) because of an previous SSLException, ignoring...", (Throwable)var7);
      } finally {
         this.setHandshakeFailure(var1, var2, true, false, true);
      }

      PlatformDependent.throwException(var2);
   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws SSLException {
      if (this.jdkCompatibilityMode) {
         this.decodeJdkCompatible(var1, var2);
      } else {
         this.decodeNonJdkCompatible(var1, var2);
      }

   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      this.discardSomeReadBytes();
      this.flushIfNeeded(var1);
      this.readIfNeeded(var1);
      this.firedChannelRead = false;
      var1.fireChannelReadComplete();
   }

   private void readIfNeeded(ChannelHandlerContext var1) {
      if (!var1.channel().config().isAutoRead() && (!this.firedChannelRead || !this.handshakePromise.isDone())) {
         var1.read();
      }

   }

   private void flushIfNeeded(ChannelHandlerContext var1) {
      if (this.needsFlush) {
         this.forceFlush(var1);
      }

   }

   private void unwrapNonAppData(ChannelHandlerContext var1) throws SSLException {
      this.unwrap(var1, Unpooled.EMPTY_BUFFER, 0, 0);
   }

   private int unwrap(ChannelHandlerContext var1, ByteBuf var2, int var3, int var4) throws SSLException {
      boolean var6 = false;
      boolean var7 = false;
      int var8 = -1;
      ByteBuf var9 = this.allocate(var1, var4);

      try {
         while(true) {
            if (!var1.isRemoved()) {
               label197: {
                  SSLEngineResult var10 = this.engineType.unwrap(this, var2, var3, var4, var9);
                  Status var11 = var10.getStatus();
                  HandshakeStatus var12 = var10.getHandshakeStatus();
                  int var13 = var10.bytesProduced();
                  int var14 = var10.bytesConsumed();
                  var3 += var14;
                  var4 -= var14;
                  switch(var11) {
                  case BUFFER_OVERFLOW:
                     int var15 = var9.readableBytes();
                     int var16 = var8;
                     var8 = var15;
                     int var17 = this.engine.getSession().getApplicationBufferSize() - var15;
                     if (var15 > 0) {
                        this.firedChannelRead = true;
                        var1.fireChannelRead(var9);
                        var9 = null;
                        if (var17 <= 0) {
                           var17 = this.engine.getSession().getApplicationBufferSize();
                        }
                     } else {
                        var9.release();
                        var9 = null;
                     }

                     if (var15 == 0 && var16 == 0) {
                        throw new IllegalStateException("Two consecutive overflows but no content was consumed. " + SSLSession.class.getSimpleName() + " getApplicationBufferSize: " + this.engine.getSession().getApplicationBufferSize() + " maybe too small.");
                     }

                     var9 = this.allocate(var1, this.engineType.calculatePendingData(this, var17));
                     continue;
                  case CLOSED:
                     var7 = true;
                     var8 = -1;
                     break;
                  default:
                     var8 = -1;
                  }

                  switch(var12) {
                  case NEED_TASK:
                     this.runDelegatedTasks();
                     break;
                  case FINISHED:
                     this.setHandshakeSuccess();
                     var6 = true;
                     break;
                  case NOT_HANDSHAKING:
                     if (this.setHandshakeSuccessIfStillHandshaking()) {
                        var6 = true;
                        continue;
                     }

                     if (this.flushedBeforeHandshake) {
                        this.flushedBeforeHandshake = false;
                        var6 = true;
                     }

                     if (var4 == 0) {
                        break label197;
                     }
                     break;
                  case NEED_WRAP:
                     if (this.wrapNonAppData(var1, true) && var4 == 0) {
                        break label197;
                     }
                  case NEED_UNWRAP:
                     break;
                  default:
                     throw new IllegalStateException("unknown handshake status: " + var12);
                  }

                  if (var11 != Status.BUFFER_UNDERFLOW && (var14 != 0 || var13 != 0)) {
                     continue;
                  }

                  if (var12 == HandshakeStatus.NEED_UNWRAP) {
                     this.readIfNeeded(var1);
                  }
               }
            }

            if (var6) {
               this.wrap(var1, true);
            }

            if (var7) {
               this.notifyClosePromise((Throwable)null);
            }
            break;
         }
      } finally {
         if (var9 != null) {
            if (var9.isReadable()) {
               this.firedChannelRead = true;
               var1.fireChannelRead(var9);
            } else {
               var9.release();
            }
         }

      }

      return var4 - var4;
   }

   private static ByteBuffer toByteBuffer(ByteBuf var0, int var1, int var2) {
      return var0.nioBufferCount() == 1 ? var0.internalNioBuffer(var1, var2) : var0.nioBuffer(var1, var2);
   }

   private void runDelegatedTasks() {
      if (this.delegatedTaskExecutor == ImmediateExecutor.INSTANCE) {
         while(true) {
            Runnable var6 = this.engine.getDelegatedTask();
            if (var6 == null) {
               break;
            }

            var6.run();
         }
      } else {
         final ArrayList var1 = new ArrayList(2);

         while(true) {
            Runnable var2 = this.engine.getDelegatedTask();
            if (var2 == null) {
               if (var1.isEmpty()) {
                  return;
               }

               final CountDownLatch var7 = new CountDownLatch(1);
               this.delegatedTaskExecutor.execute(new Runnable() {
                  public void run() {
                     try {
                        Iterator var1x = var1.iterator();

                        while(var1x.hasNext()) {
                           Runnable var2 = (Runnable)var1x.next();
                           var2.run();
                        }
                     } catch (Exception var6) {
                        SslHandler.this.ctx.fireExceptionCaught(var6);
                     } finally {
                        var7.countDown();
                     }

                  }
               });
               boolean var3 = false;

               while(var7.getCount() != 0L) {
                  try {
                     var7.await();
                  } catch (InterruptedException var5) {
                     var3 = true;
                  }
               }

               if (var3) {
                  Thread.currentThread().interrupt();
               }
               break;
            }

            var1.add(var2);
         }
      }

   }

   private boolean setHandshakeSuccessIfStillHandshaking() {
      if (!this.handshakePromise.isDone()) {
         this.setHandshakeSuccess();
         return true;
      } else {
         return false;
      }
   }

   private void setHandshakeSuccess() {
      this.handshakePromise.trySuccess(this.ctx.channel());
      if (logger.isDebugEnabled()) {
         logger.debug("{} HANDSHAKEN: {}", this.ctx.channel(), this.engine.getSession().getCipherSuite());
      }

      this.ctx.fireUserEventTriggered(SslHandshakeCompletionEvent.SUCCESS);
      if (this.readDuringHandshake && !this.ctx.channel().config().isAutoRead()) {
         this.readDuringHandshake = false;
         this.ctx.read();
      }

   }

   private void setHandshakeFailure(ChannelHandlerContext var1, Throwable var2) {
      this.setHandshakeFailure(var1, var2, true, true, false);
   }

   private void setHandshakeFailure(ChannelHandlerContext var1, Throwable var2, boolean var3, boolean var4, boolean var5) {
      try {
         this.outboundClosed = true;
         this.engine.closeOutbound();
         if (var3) {
            try {
               this.engine.closeInbound();
            } catch (SSLException var11) {
               if (logger.isDebugEnabled()) {
                  String var7 = var11.getMessage();
                  if (var7 == null || !var7.contains("possible truncation attack")) {
                     logger.debug("{} SSLEngine.closeInbound() raised an exception.", var1.channel(), var11);
                  }
               }
            }
         }

         if (this.handshakePromise.tryFailure(var2) || var5) {
            SslUtils.handleHandshakeFailure(var1, var2, var4);
         }
      } finally {
         this.releaseAndFailAll(var2);
      }

   }

   private void releaseAndFailAll(Throwable var1) {
      if (this.pendingUnencryptedWrites != null) {
         this.pendingUnencryptedWrites.releaseAndFailAll(this.ctx, var1);
      }

   }

   private void notifyClosePromise(Throwable var1) {
      if (var1 == null) {
         if (this.sslClosePromise.trySuccess(this.ctx.channel())) {
            this.ctx.fireUserEventTriggered(SslCloseCompletionEvent.SUCCESS);
         }
      } else if (this.sslClosePromise.tryFailure(var1)) {
         this.ctx.fireUserEventTriggered(new SslCloseCompletionEvent(var1));
      }

   }

   private void closeOutboundAndChannel(ChannelHandlerContext var1, final ChannelPromise var2, boolean var3) throws Exception {
      this.outboundClosed = true;
      this.engine.closeOutbound();
      if (!var1.channel().isActive()) {
         if (var3) {
            var1.disconnect(var2);
         } else {
            var1.close(var2);
         }

      } else {
         ChannelPromise var4 = var1.newPromise();

         try {
            this.flush(var1, var4);
         } finally {
            if (!this.closeNotify) {
               this.closeNotify = true;
               this.safeClose(var1, var4, var1.newPromise().addListener(new ChannelPromiseNotifier(false, new ChannelPromise[]{var2})));
            } else {
               this.sslClosePromise.addListener(new FutureListener<Channel>() {
                  public void operationComplete(Future<Channel> var1) {
                     var2.setSuccess();
                  }
               });
            }

         }

      }
   }

   private void flush(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      if (this.pendingUnencryptedWrites != null) {
         this.pendingUnencryptedWrites.add(Unpooled.EMPTY_BUFFER, var2);
      } else {
         var2.setFailure(newPendingWritesNullException());
      }

      this.flush(var1);
   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      this.pendingUnencryptedWrites = new SslHandler.SslHandlerCoalescingBufferQueue(var1.channel(), 16);
      if (var1.channel().isActive()) {
         this.startHandshakeProcessing();
      }

   }

   private void startHandshakeProcessing() {
      this.handshakeStarted = true;
      if (this.engine.getUseClientMode()) {
         this.handshake((Promise)null);
      } else {
         this.applyHandshakeTimeout((Promise)null);
      }

   }

   public Future<Channel> renegotiate() {
      ChannelHandlerContext var1 = this.ctx;
      if (var1 == null) {
         throw new IllegalStateException();
      } else {
         return this.renegotiate(var1.executor().newPromise());
      }
   }

   public Future<Channel> renegotiate(final Promise<Channel> var1) {
      if (var1 == null) {
         throw new NullPointerException("promise");
      } else {
         ChannelHandlerContext var2 = this.ctx;
         if (var2 == null) {
            throw new IllegalStateException();
         } else {
            EventExecutor var3 = var2.executor();
            if (!var3.inEventLoop()) {
               var3.execute(new Runnable() {
                  public void run() {
                     SslHandler.this.handshake(var1);
                  }
               });
               return var1;
            } else {
               this.handshake(var1);
               return var1;
            }
         }
      }
   }

   private void handshake(final Promise<Channel> var1) {
      Promise var2;
      if (var1 != null) {
         Promise var3 = this.handshakePromise;
         if (!var3.isDone()) {
            var3.addListener(new FutureListener<Channel>() {
               public void operationComplete(Future<Channel> var1x) throws Exception {
                  if (var1x.isSuccess()) {
                     var1.setSuccess(var1x.getNow());
                  } else {
                     var1.setFailure(var1x.cause());
                  }

               }
            });
            return;
         }

         var2 = var1;
         this.handshakePromise = var1;
      } else {
         if (this.engine.getHandshakeStatus() != HandshakeStatus.NOT_HANDSHAKING) {
            return;
         }

         var2 = this.handshakePromise;

         assert !var2.isDone();
      }

      ChannelHandlerContext var10 = this.ctx;

      try {
         this.engine.beginHandshake();
         this.wrapNonAppData(var10, false);
      } catch (Throwable var8) {
         this.setHandshakeFailure(var10, var8);
      } finally {
         this.forceFlush(var10);
      }

      this.applyHandshakeTimeout(var2);
   }

   private void applyHandshakeTimeout(Promise<Channel> var1) {
      final Promise var2 = var1 == null ? this.handshakePromise : var1;
      long var3 = this.handshakeTimeoutMillis;
      if (var3 > 0L && !var2.isDone()) {
         final ScheduledFuture var5 = this.ctx.executor().schedule(new Runnable() {
            public void run() {
               if (!var2.isDone()) {
                  try {
                     if (SslHandler.this.handshakePromise.tryFailure(SslHandler.HANDSHAKE_TIMED_OUT)) {
                        SslUtils.handleHandshakeFailure(SslHandler.this.ctx, SslHandler.HANDSHAKE_TIMED_OUT, true);
                     }
                  } finally {
                     SslHandler.this.releaseAndFailAll(SslHandler.HANDSHAKE_TIMED_OUT);
                  }

               }
            }
         }, var3, TimeUnit.MILLISECONDS);
         var2.addListener(new FutureListener<Channel>() {
            public void operationComplete(Future<Channel> var1) throws Exception {
               var5.cancel(false);
            }
         });
      }
   }

   private void forceFlush(ChannelHandlerContext var1) {
      this.needsFlush = false;
      var1.flush();
   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      if (!this.startTls) {
         this.startHandshakeProcessing();
      }

      var1.fireChannelActive();
   }

   private void safeClose(final ChannelHandlerContext var1, final ChannelFuture var2, final ChannelPromise var3) {
      if (!var1.channel().isActive()) {
         var1.close(var3);
      } else {
         final ScheduledFuture var4;
         if (!var2.isDone()) {
            long var5 = this.closeNotifyFlushTimeoutMillis;
            if (var5 > 0L) {
               var4 = var1.executor().schedule(new Runnable() {
                  public void run() {
                     if (!var2.isDone()) {
                        SslHandler.logger.warn("{} Last write attempt timed out; force-closing the connection.", (Object)var1.channel());
                        SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
                     }

                  }
               }, var5, TimeUnit.MILLISECONDS);
            } else {
               var4 = null;
            }
         } else {
            var4 = null;
         }

         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               if (var4 != null) {
                  var4.cancel(false);
               }

               final long var2 = SslHandler.this.closeNotifyReadTimeoutMillis;
               if (var2 <= 0L) {
                  SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
               } else {
                  final ScheduledFuture var4x;
                  if (!SslHandler.this.sslClosePromise.isDone()) {
                     var4x = var1.executor().schedule(new Runnable() {
                        public void run() {
                           if (!SslHandler.this.sslClosePromise.isDone()) {
                              SslHandler.logger.debug("{} did not receive close_notify in {}ms; force-closing the connection.", var1.channel(), var2);
                              SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
                           }

                        }
                     }, var2, TimeUnit.MILLISECONDS);
                  } else {
                     var4x = null;
                  }

                  SslHandler.this.sslClosePromise.addListener(new FutureListener<Channel>() {
                     public void operationComplete(Future<Channel> var1x) throws Exception {
                        if (var4x != null) {
                           var4x.cancel(false);
                        }

                        SslHandler.addCloseListener(var1.close(var1.newPromise()), var3);
                     }
                  });
               }

            }
         });
      }
   }

   private static void addCloseListener(ChannelFuture var0, ChannelPromise var1) {
      var0.addListener(new ChannelPromiseNotifier(false, new ChannelPromise[]{var1}));
   }

   private ByteBuf allocate(ChannelHandlerContext var1, int var2) {
      ByteBufAllocator var3 = var1.alloc();
      return this.engineType.wantsDirectBuffer ? var3.directBuffer(var2) : var3.buffer(var2);
   }

   private ByteBuf allocateOutNetBuf(ChannelHandlerContext var1, int var2, int var3) {
      return this.allocate(var1, this.engineType.calculateWrapBufferCapacity(this, var2, var3));
   }

   private static boolean attemptCopyToCumulation(ByteBuf var0, ByteBuf var1, int var2) {
      int var3 = var1.readableBytes();
      int var4 = var0.capacity();
      if (var2 - var0.readableBytes() < var3 || (!var0.isWritable(var3) || var4 < var2) && (var4 >= var2 || !ByteBufUtil.ensureWritableSuccess(var0.ensureWritable(var3, false)))) {
         return false;
      } else {
         var0.writeBytes(var1);
         var1.release();
         return true;
      }
   }

   private final class LazyChannelPromise extends DefaultPromise<Channel> {
      private LazyChannelPromise() {
         super();
      }

      protected EventExecutor executor() {
         if (SslHandler.this.ctx == null) {
            throw new IllegalStateException();
         } else {
            return SslHandler.this.ctx.executor();
         }
      }

      protected void checkDeadLock() {
         if (SslHandler.this.ctx != null) {
            super.checkDeadLock();
         }
      }

      // $FF: synthetic method
      LazyChannelPromise(Object var2) {
         this();
      }
   }

   private final class SslHandlerCoalescingBufferQueue extends AbstractCoalescingBufferQueue {
      SslHandlerCoalescingBufferQueue(Channel var2, int var3) {
         super(var2, var3);
      }

      protected ByteBuf compose(ByteBufAllocator var1, ByteBuf var2, ByteBuf var3) {
         int var4 = SslHandler.this.wrapDataSize;
         if (!(var2 instanceof CompositeByteBuf)) {
            return SslHandler.attemptCopyToCumulation(var2, var3, var4) ? var2 : this.copyAndCompose(var1, var2, var3);
         } else {
            CompositeByteBuf var5 = (CompositeByteBuf)var2;
            int var6 = var5.numComponents();
            if (var6 == 0 || !SslHandler.attemptCopyToCumulation(var5.internalComponent(var6 - 1), var3, var4)) {
               var5.addComponent(true, var3);
            }

            return var5;
         }
      }

      protected ByteBuf composeFirst(ByteBufAllocator var1, ByteBuf var2) {
         if (var2 instanceof CompositeByteBuf) {
            CompositeByteBuf var3 = (CompositeByteBuf)var2;
            var2 = var1.directBuffer(var3.readableBytes());

            try {
               var2.writeBytes((ByteBuf)var3);
            } catch (Throwable var5) {
               var2.release();
               PlatformDependent.throwException(var5);
            }

            var3.release();
         }

         return var2;
      }

      protected ByteBuf removeEmptyValue() {
         return null;
      }
   }

   private static enum SslEngineType {
      TCNATIVE(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
         SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, int var4, ByteBuf var5) throws SSLException {
            int var6 = var2.nioBufferCount();
            int var7 = var5.writerIndex();
            SSLEngineResult var8;
            if (var6 > 1) {
               ReferenceCountedOpenSslEngine var9 = (ReferenceCountedOpenSslEngine)var1.engine;

               try {
                  var1.singleBuffer[0] = SslHandler.toByteBuffer(var5, var7, var5.writableBytes());
                  var8 = var9.unwrap(var2.nioBuffers(var3, var4), var1.singleBuffer);
               } finally {
                  var1.singleBuffer[0] = null;
               }
            } else {
               var8 = var1.engine.unwrap(SslHandler.toByteBuffer(var2, var3, var4), SslHandler.toByteBuffer(var5, var7, var5.writableBytes()));
            }

            var5.writerIndex(var7 + var8.bytesProduced());
            return var8;
         }

         int getPacketBufferSize(SslHandler var1) {
            return ((ReferenceCountedOpenSslEngine)var1.engine).maxEncryptedPacketLength0();
         }

         int calculateWrapBufferCapacity(SslHandler var1, int var2, int var3) {
            return ((ReferenceCountedOpenSslEngine)var1.engine).calculateMaxLengthForWrap(var2, var3);
         }

         int calculatePendingData(SslHandler var1, int var2) {
            int var3 = ((ReferenceCountedOpenSslEngine)var1.engine).sslPending();
            return var3 > 0 ? var3 : var2;
         }

         boolean jdkCompatibilityMode(SSLEngine var1) {
            return ((ReferenceCountedOpenSslEngine)var1).jdkCompatibilityMode;
         }
      },
      CONSCRYPT(true, ByteToMessageDecoder.COMPOSITE_CUMULATOR) {
         SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, int var4, ByteBuf var5) throws SSLException {
            int var6 = var2.nioBufferCount();
            int var7 = var5.writerIndex();
            SSLEngineResult var8;
            if (var6 > 1) {
               try {
                  var1.singleBuffer[0] = SslHandler.toByteBuffer(var5, var7, var5.writableBytes());
                  var8 = ((ConscryptAlpnSslEngine)var1.engine).unwrap(var2.nioBuffers(var3, var4), var1.singleBuffer);
               } finally {
                  var1.singleBuffer[0] = null;
               }
            } else {
               var8 = var1.engine.unwrap(SslHandler.toByteBuffer(var2, var3, var4), SslHandler.toByteBuffer(var5, var7, var5.writableBytes()));
            }

            var5.writerIndex(var7 + var8.bytesProduced());
            return var8;
         }

         int calculateWrapBufferCapacity(SslHandler var1, int var2, int var3) {
            return ((ConscryptAlpnSslEngine)var1.engine).calculateOutNetBufSize(var2, var3);
         }

         int calculatePendingData(SslHandler var1, int var2) {
            return var2;
         }

         boolean jdkCompatibilityMode(SSLEngine var1) {
            return true;
         }
      },
      JDK(false, ByteToMessageDecoder.MERGE_CUMULATOR) {
         SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, int var4, ByteBuf var5) throws SSLException {
            int var6 = var5.writerIndex();
            ByteBuffer var7 = SslHandler.toByteBuffer(var2, var3, var4);
            int var8 = var7.position();
            SSLEngineResult var9 = var1.engine.unwrap(var7, SslHandler.toByteBuffer(var5, var6, var5.writableBytes()));
            var5.writerIndex(var6 + var9.bytesProduced());
            if (var9.bytesConsumed() == 0) {
               int var10 = var7.position() - var8;
               if (var10 != var9.bytesConsumed()) {
                  return new SSLEngineResult(var9.getStatus(), var9.getHandshakeStatus(), var10, var9.bytesProduced());
               }
            }

            return var9;
         }

         int calculateWrapBufferCapacity(SslHandler var1, int var2, int var3) {
            return var1.engine.getSession().getPacketBufferSize();
         }

         int calculatePendingData(SslHandler var1, int var2) {
            return var2;
         }

         boolean jdkCompatibilityMode(SSLEngine var1) {
            return true;
         }
      };

      final boolean wantsDirectBuffer;
      final ByteToMessageDecoder.Cumulator cumulator;

      static SslHandler.SslEngineType forEngine(SSLEngine var0) {
         return var0 instanceof ReferenceCountedOpenSslEngine ? TCNATIVE : (var0 instanceof ConscryptAlpnSslEngine ? CONSCRYPT : JDK);
      }

      private SslEngineType(boolean var3, ByteToMessageDecoder.Cumulator var4) {
         this.wantsDirectBuffer = var3;
         this.cumulator = var4;
      }

      int getPacketBufferSize(SslHandler var1) {
         return var1.engine.getSession().getPacketBufferSize();
      }

      abstract SSLEngineResult unwrap(SslHandler var1, ByteBuf var2, int var3, int var4, ByteBuf var5) throws SSLException;

      abstract int calculateWrapBufferCapacity(SslHandler var1, int var2, int var3);

      abstract int calculatePendingData(SslHandler var1, int var2);

      abstract boolean jdkCompatibilityMode(SSLEngine var1);

      // $FF: synthetic method
      SslEngineType(boolean var3, ByteToMessageDecoder.Cumulator var4, Object var5) {
         this(var3, var4);
      }
   }
}
