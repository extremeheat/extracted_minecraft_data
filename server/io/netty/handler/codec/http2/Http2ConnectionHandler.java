package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Http2ConnectionHandler extends ByteToMessageDecoder implements Http2LifecycleManager, ChannelOutboundHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Http2ConnectionHandler.class);
   private static final Http2Headers HEADERS_TOO_LARGE_HEADERS;
   private static final ByteBuf HTTP_1_X_BUF;
   private final Http2ConnectionDecoder decoder;
   private final Http2ConnectionEncoder encoder;
   private final Http2Settings initialSettings;
   private ChannelFutureListener closeListener;
   private Http2ConnectionHandler.BaseDecoder byteDecoder;
   private long gracefulShutdownTimeoutMillis;

   protected Http2ConnectionHandler(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2, Http2Settings var3) {
      super();
      this.initialSettings = (Http2Settings)ObjectUtil.checkNotNull(var3, "initialSettings");
      this.decoder = (Http2ConnectionDecoder)ObjectUtil.checkNotNull(var1, "decoder");
      this.encoder = (Http2ConnectionEncoder)ObjectUtil.checkNotNull(var2, "encoder");
      if (var2.connection() != var1.connection()) {
         throw new IllegalArgumentException("Encoder and Decoder do not share the same connection object");
      }
   }

   Http2ConnectionHandler(boolean var1, Http2FrameWriter var2, Http2FrameLogger var3, Http2Settings var4) {
      super();
      this.initialSettings = (Http2Settings)ObjectUtil.checkNotNull(var4, "initialSettings");
      DefaultHttp2Connection var5 = new DefaultHttp2Connection(var1);
      Long var6 = var4.maxHeaderListSize();
      Object var7 = new DefaultHttp2FrameReader(var6 == null ? new DefaultHttp2HeadersDecoder(true) : new DefaultHttp2HeadersDecoder(true, var6));
      if (var3 != null) {
         var2 = new Http2OutboundFrameLogger((Http2FrameWriter)var2, var3);
         var7 = new Http2InboundFrameLogger((Http2FrameReader)var7, var3);
      }

      this.encoder = new DefaultHttp2ConnectionEncoder(var5, (Http2FrameWriter)var2);
      this.decoder = new DefaultHttp2ConnectionDecoder(var5, this.encoder, (Http2FrameReader)var7);
   }

   public long gracefulShutdownTimeoutMillis() {
      return this.gracefulShutdownTimeoutMillis;
   }

   public void gracefulShutdownTimeoutMillis(long var1) {
      if (var1 < -1L) {
         throw new IllegalArgumentException("gracefulShutdownTimeoutMillis: " + var1 + " (expected: -1 for indefinite or >= 0)");
      } else {
         this.gracefulShutdownTimeoutMillis = var1;
      }
   }

   public Http2Connection connection() {
      return this.encoder.connection();
   }

   public Http2ConnectionDecoder decoder() {
      return this.decoder;
   }

   public Http2ConnectionEncoder encoder() {
      return this.encoder;
   }

   private boolean prefaceSent() {
      return this.byteDecoder != null && this.byteDecoder.prefaceSent();
   }

   public void onHttpClientUpgrade() throws Http2Exception {
      if (this.connection().isServer()) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Client-side HTTP upgrade requested for a server");
      } else if (!this.prefaceSent()) {
         throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "HTTP upgrade must occur after preface was sent");
      } else if (this.decoder.prefaceReceived()) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP upgrade must occur before HTTP/2 preface is received");
      } else {
         this.connection().local().createStream(1, true);
      }
   }

   public void onHttpServerUpgrade(Http2Settings var1) throws Http2Exception {
      if (!this.connection().isServer()) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server-side HTTP upgrade requested for a client");
      } else if (!this.prefaceSent()) {
         throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, "HTTP upgrade must occur after preface was sent");
      } else if (this.decoder.prefaceReceived()) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP upgrade must occur before HTTP/2 preface is received");
      } else {
         this.encoder.remoteSettings(var1);
         this.connection().remote().createStream(1, true);
      }
   }

   public void flush(ChannelHandlerContext var1) {
      try {
         this.encoder.flowController().writePendingBytes();
         var1.flush();
      } catch (Http2Exception var3) {
         this.onError(var1, true, var3);
      } catch (Throwable var4) {
         this.onError(var1, true, Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, var4, "Error flushing"));
      }

   }

   public void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.encoder.lifecycleManager(this);
      this.decoder.lifecycleManager(this);
      this.encoder.flowController().channelHandlerContext(var1);
      this.decoder.flowController().channelHandlerContext(var1);
      this.byteDecoder = new Http2ConnectionHandler.PrefaceDecoder(var1);
   }

   protected void handlerRemoved0(ChannelHandlerContext var1) throws Exception {
      if (this.byteDecoder != null) {
         this.byteDecoder.handlerRemoved(var1);
         this.byteDecoder = null;
      }

   }

   public void channelActive(ChannelHandlerContext var1) throws Exception {
      if (this.byteDecoder == null) {
         this.byteDecoder = new Http2ConnectionHandler.PrefaceDecoder(var1);
      }

      this.byteDecoder.channelActive(var1);
      super.channelActive(var1);
   }

   public void channelInactive(ChannelHandlerContext var1) throws Exception {
      super.channelInactive(var1);
      if (this.byteDecoder != null) {
         this.byteDecoder.channelInactive(var1);
         this.byteDecoder = null;
      }

   }

   public void channelWritabilityChanged(ChannelHandlerContext var1) throws Exception {
      try {
         if (var1.channel().isWritable()) {
            this.flush(var1);
         }

         this.encoder.flowController().channelWritabilityChanged();
      } finally {
         super.channelWritabilityChanged(var1);
      }

   }

   protected void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
      this.byteDecoder.decode(var1, var2, var3);
   }

   public void bind(ChannelHandlerContext var1, SocketAddress var2, ChannelPromise var3) throws Exception {
      var1.bind(var2, var3);
   }

   public void connect(ChannelHandlerContext var1, SocketAddress var2, SocketAddress var3, ChannelPromise var4) throws Exception {
      var1.connect(var2, var3, var4);
   }

   public void disconnect(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.disconnect(var2);
   }

   public void close(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var2 = var2.unvoid();
      if (!var1.channel().isActive()) {
         var1.close(var2);
      } else {
         ChannelFuture var3 = this.connection().goAwaySent() ? var1.write(Unpooled.EMPTY_BUFFER) : this.goAway(var1, (Http2Exception)null);
         var1.flush();
         this.doGracefulShutdown(var1, var3, var2);
      }
   }

   private void doGracefulShutdown(ChannelHandlerContext var1, ChannelFuture var2, ChannelPromise var3) {
      if (this.isGracefulShutdownComplete()) {
         var2.addListener(new Http2ConnectionHandler.ClosingChannelFutureListener(var1, var3));
      } else if (this.gracefulShutdownTimeoutMillis < 0L) {
         this.closeListener = new Http2ConnectionHandler.ClosingChannelFutureListener(var1, var3);
      } else {
         this.closeListener = new Http2ConnectionHandler.ClosingChannelFutureListener(var1, var3, this.gracefulShutdownTimeoutMillis, TimeUnit.MILLISECONDS);
      }

   }

   public void deregister(ChannelHandlerContext var1, ChannelPromise var2) throws Exception {
      var1.deregister(var2);
   }

   public void read(ChannelHandlerContext var1) throws Exception {
      var1.read();
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) throws Exception {
      var1.write(var2, var3);
   }

   public void channelReadComplete(ChannelHandlerContext var1) throws Exception {
      try {
         this.channelReadComplete0(var1);
      } finally {
         this.flush(var1);
      }

   }

   void channelReadComplete0(ChannelHandlerContext var1) throws Exception {
      super.channelReadComplete(var1);
   }

   public void exceptionCaught(ChannelHandlerContext var1, Throwable var2) throws Exception {
      if (Http2CodecUtil.getEmbeddedHttp2Exception(var2) != null) {
         this.onError(var1, false, var2);
      } else {
         super.exceptionCaught(var1, var2);
      }

   }

   public void closeStreamLocal(Http2Stream var1, ChannelFuture var2) {
      switch(var1.state()) {
      case HALF_CLOSED_LOCAL:
      case OPEN:
         var1.closeLocalSide();
         break;
      default:
         this.closeStream(var1, var2);
      }

   }

   public void closeStreamRemote(Http2Stream var1, ChannelFuture var2) {
      switch(var1.state()) {
      case OPEN:
      case HALF_CLOSED_REMOTE:
         var1.closeRemoteSide();
         break;
      default:
         this.closeStream(var1, var2);
      }

   }

   public void closeStream(Http2Stream var1, ChannelFuture var2) {
      var1.close();
      if (var2.isDone()) {
         this.checkCloseConnection(var2);
      } else {
         var2.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1) throws Exception {
               Http2ConnectionHandler.this.checkCloseConnection(var1);
            }
         });
      }

   }

   public void onError(ChannelHandlerContext var1, boolean var2, Throwable var3) {
      Http2Exception var4 = Http2CodecUtil.getEmbeddedHttp2Exception(var3);
      if (Http2Exception.isStreamError(var4)) {
         this.onStreamError(var1, var2, var3, (Http2Exception.StreamException)var4);
      } else if (var4 instanceof Http2Exception.CompositeStreamException) {
         Http2Exception.CompositeStreamException var5 = (Http2Exception.CompositeStreamException)var4;
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Http2Exception.StreamException var7 = (Http2Exception.StreamException)var6.next();
            this.onStreamError(var1, var2, var3, var7);
         }
      } else {
         this.onConnectionError(var1, var2, var3, var4);
      }

      var1.flush();
   }

   protected boolean isGracefulShutdownComplete() {
      return this.connection().numActiveStreams() == 0;
   }

   protected void onConnectionError(ChannelHandlerContext var1, boolean var2, Throwable var3, Http2Exception var4) {
      if (var4 == null) {
         var4 = new Http2Exception(Http2Error.INTERNAL_ERROR, var3.getMessage(), var3);
      }

      ChannelPromise var5 = var1.newPromise();
      ChannelFuture var6 = this.goAway(var1, var4);
      switch(var4.shutdownHint()) {
      case GRACEFUL_SHUTDOWN:
         this.doGracefulShutdown(var1, var6, var5);
         break;
      default:
         var6.addListener(new Http2ConnectionHandler.ClosingChannelFutureListener(var1, var5));
      }

   }

   protected void onStreamError(ChannelHandlerContext var1, boolean var2, Throwable var3, Http2Exception.StreamException var4) {
      int var5 = var4.streamId();
      Http2Stream var6 = this.connection().stream(var5);
      if (var4 instanceof Http2Exception.HeaderListSizeException && ((Http2Exception.HeaderListSizeException)var4).duringDecode() && this.connection().isServer()) {
         if (var6 == null) {
            try {
               var6 = this.encoder.connection().remote().createStream(var5, true);
            } catch (Http2Exception var9) {
               this.resetUnknownStream(var1, var5, var4.error().code(), var1.newPromise());
               return;
            }
         }

         if (var6 != null && !var6.isHeadersSent()) {
            try {
               this.handleServerHeaderDecodeSizeError(var1, var6);
            } catch (Throwable var8) {
               this.onError(var1, var2, Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, var8, "Error DecodeSizeError"));
            }
         }
      }

      if (var6 == null) {
         this.resetUnknownStream(var1, var5, var4.error().code(), var1.newPromise());
      } else {
         this.resetStream(var1, var6, var4.error().code(), var1.newPromise());
      }

   }

   protected void handleServerHeaderDecodeSizeError(ChannelHandlerContext var1, Http2Stream var2) {
      this.encoder().writeHeaders(var1, var2.id(), HEADERS_TOO_LARGE_HEADERS, 0, true, var1.newPromise());
   }

   protected Http2FrameWriter frameWriter() {
      return this.encoder().frameWriter();
   }

   private ChannelFuture resetUnknownStream(final ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5) {
      ChannelFuture var6 = this.frameWriter().writeRstStream(var1, var2, var3, var5);
      if (var6.isDone()) {
         this.closeConnectionOnError(var1, var6);
      } else {
         var6.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture var1x) throws Exception {
               Http2ConnectionHandler.this.closeConnectionOnError(var1, var1x);
            }
         });
      }

      return var6;
   }

   public ChannelFuture resetStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5) {
      Http2Stream var6 = this.connection().stream(var2);
      return var6 == null ? this.resetUnknownStream(var1, var2, var3, var5.unvoid()) : this.resetStream(var1, var6, var3, var5);
   }

   private ChannelFuture resetStream(final ChannelHandlerContext var1, final Http2Stream var2, long var3, ChannelPromise var5) {
      var5 = var5.unvoid();
      if (var2.isResetSent()) {
         return var5.setSuccess();
      } else {
         Object var6;
         if (var2.state() != Http2Stream.State.IDLE && (!this.connection().local().created(var2) || var2.isHeadersSent() || var2.isPushPromiseSent())) {
            var6 = this.frameWriter().writeRstStream(var1, var2.id(), var3, var5);
         } else {
            var6 = var5.setSuccess();
         }

         var2.resetSent();
         if (((ChannelFuture)var6).isDone()) {
            this.processRstStreamWriteResult(var1, var2, (ChannelFuture)var6);
         } else {
            ((ChannelFuture)var6).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  Http2ConnectionHandler.this.processRstStreamWriteResult(var1, var2, var1x);
               }
            });
         }

         return (ChannelFuture)var6;
      }
   }

   public ChannelFuture goAway(final ChannelHandlerContext var1, final int var2, final long var3, final ByteBuf var5, ChannelPromise var6) {
      try {
         var6 = var6.unvoid();
         Http2Connection var7 = this.connection();
         if (this.connection().goAwaySent()) {
            if (var2 == this.connection().remote().lastStreamKnownByPeer()) {
               var5.release();
               return var6.setSuccess();
            }

            if (var2 > var7.remote().lastStreamKnownByPeer()) {
               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Last stream identifier must not increase between sending multiple GOAWAY frames (was '%d', is '%d').", var7.remote().lastStreamKnownByPeer(), var2);
            }
         }

         var7.goAwaySent(var2, var3, var5);
         var5.retain();
         ChannelFuture var8 = this.frameWriter().writeGoAway(var1, var2, var3, var5, var6);
         if (var8.isDone()) {
            processGoAwayWriteResult(var1, var2, var3, var5, var8);
         } else {
            var8.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1x) throws Exception {
                  Http2ConnectionHandler.processGoAwayWriteResult(var1, var2, var3, var5, var1x);
               }
            });
         }

         return var8;
      } catch (Throwable var9) {
         var5.release();
         return var6.setFailure(var9);
      }
   }

   private void checkCloseConnection(ChannelFuture var1) {
      if (this.closeListener != null && this.isGracefulShutdownComplete()) {
         ChannelFutureListener var2 = this.closeListener;
         this.closeListener = null;

         try {
            var2.operationComplete(var1);
         } catch (Exception var4) {
            throw new IllegalStateException("Close listener threw an unexpected exception", var4);
         }
      }

   }

   private ChannelFuture goAway(ChannelHandlerContext var1, Http2Exception var2) {
      long var3 = var2 != null ? var2.error().code() : Http2Error.NO_ERROR.code();
      int var5 = this.connection().remote().lastStreamCreated();
      return this.goAway(var1, var5, var3, Http2CodecUtil.toByteBuf(var1, var2), var1.newPromise());
   }

   private void processRstStreamWriteResult(ChannelHandlerContext var1, Http2Stream var2, ChannelFuture var3) {
      if (var3.isSuccess()) {
         this.closeStream(var2, var3);
      } else {
         this.onConnectionError(var1, true, var3.cause(), (Http2Exception)null);
      }

   }

   private void closeConnectionOnError(ChannelHandlerContext var1, ChannelFuture var2) {
      if (!var2.isSuccess()) {
         this.onConnectionError(var1, true, var2.cause(), (Http2Exception)null);
      }

   }

   private static ByteBuf clientPrefaceString(Http2Connection var0) {
      return var0.isServer() ? Http2CodecUtil.connectionPrefaceBuf() : null;
   }

   private static void processGoAwayWriteResult(ChannelHandlerContext var0, int var1, long var2, ByteBuf var4, ChannelFuture var5) {
      try {
         if (var5.isSuccess()) {
            if (var2 != Http2Error.NO_ERROR.code()) {
               if (logger.isDebugEnabled()) {
                  logger.debug("{} Sent GOAWAY: lastStreamId '{}', errorCode '{}', debugData '{}'. Forcing shutdown of the connection.", var0.channel(), var1, var2, var4.toString(CharsetUtil.UTF_8), var5.cause());
               }

               var0.close();
            }
         } else {
            if (logger.isDebugEnabled()) {
               logger.debug("{} Sending GOAWAY failed: lastStreamId '{}', errorCode '{}', debugData '{}'. Forcing shutdown of the connection.", var0.channel(), var1, var2, var4.toString(CharsetUtil.UTF_8), var5.cause());
            }

            var0.close();
         }
      } finally {
         var4.release();
      }

   }

   static {
      HEADERS_TOO_LARGE_HEADERS = ReadOnlyHttp2Headers.serverHeaders(false, HttpResponseStatus.REQUEST_HEADER_FIELDS_TOO_LARGE.codeAsText());
      HTTP_1_X_BUF = Unpooled.unreleasableBuffer(Unpooled.wrappedBuffer(new byte[]{72, 84, 84, 80, 47, 49, 46})).asReadOnly();
   }

   private static final class ClosingChannelFutureListener implements ChannelFutureListener {
      private final ChannelHandlerContext ctx;
      private final ChannelPromise promise;
      private final ScheduledFuture<?> timeoutTask;

      ClosingChannelFutureListener(ChannelHandlerContext var1, ChannelPromise var2) {
         super();
         this.ctx = var1;
         this.promise = var2;
         this.timeoutTask = null;
      }

      ClosingChannelFutureListener(final ChannelHandlerContext var1, final ChannelPromise var2, long var3, TimeUnit var5) {
         super();
         this.ctx = var1;
         this.promise = var2;
         this.timeoutTask = var1.executor().schedule(new Runnable() {
            public void run() {
               var1.close(var2);
            }
         }, var3, var5);
      }

      public void operationComplete(ChannelFuture var1) throws Exception {
         if (this.timeoutTask != null) {
            this.timeoutTask.cancel(false);
         }

         this.ctx.close(this.promise);
      }
   }

   private final class FrameDecoder extends Http2ConnectionHandler.BaseDecoder {
      private FrameDecoder() {
         super(null);
      }

      public void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         try {
            Http2ConnectionHandler.this.decoder.decodeFrame(var1, var2, var3);
         } catch (Throwable var5) {
            Http2ConnectionHandler.this.onError(var1, false, var5);
         }

      }

      // $FF: synthetic method
      FrameDecoder(Object var2) {
         this();
      }
   }

   private final class PrefaceDecoder extends Http2ConnectionHandler.BaseDecoder {
      private ByteBuf clientPrefaceString;
      private boolean prefaceSent;

      public PrefaceDecoder(ChannelHandlerContext var2) throws Exception {
         super(null);
         this.clientPrefaceString = Http2ConnectionHandler.clientPrefaceString(Http2ConnectionHandler.this.encoder.connection());
         this.sendPreface(var2);
      }

      public boolean prefaceSent() {
         return this.prefaceSent;
      }

      public void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception {
         try {
            if (var1.channel().isActive() && this.readClientPrefaceString(var2) && this.verifyFirstFrameIsSettings(var2)) {
               Http2ConnectionHandler.this.byteDecoder = Http2ConnectionHandler.this.new FrameDecoder();
               Http2ConnectionHandler.this.byteDecoder.decode(var1, var2, var3);
            }
         } catch (Throwable var5) {
            Http2ConnectionHandler.this.onError(var1, false, var5);
         }

      }

      public void channelActive(ChannelHandlerContext var1) throws Exception {
         this.sendPreface(var1);
      }

      public void channelInactive(ChannelHandlerContext var1) throws Exception {
         this.cleanup();
         super.channelInactive(var1);
      }

      public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
         this.cleanup();
      }

      private void cleanup() {
         if (this.clientPrefaceString != null) {
            this.clientPrefaceString.release();
            this.clientPrefaceString = null;
         }

      }

      private boolean readClientPrefaceString(ByteBuf var1) throws Http2Exception {
         if (this.clientPrefaceString == null) {
            return true;
         } else {
            int var2 = this.clientPrefaceString.readableBytes();
            int var3 = Math.min(var1.readableBytes(), var2);
            if (var3 != 0 && ByteBufUtil.equals(var1, var1.readerIndex(), this.clientPrefaceString, this.clientPrefaceString.readerIndex(), var3)) {
               var1.skipBytes(var3);
               this.clientPrefaceString.skipBytes(var3);
               if (!this.clientPrefaceString.isReadable()) {
                  this.clientPrefaceString.release();
                  this.clientPrefaceString = null;
                  return true;
               } else {
                  return false;
               }
            } else {
               short var4 = 1024;
               int var5 = ByteBufUtil.indexOf(Http2ConnectionHandler.HTTP_1_X_BUF, var1.slice(var1.readerIndex(), Math.min(var1.readableBytes(), var4)));
               String var6;
               if (var5 != -1) {
                  var6 = var1.toString(var1.readerIndex(), var5 - var1.readerIndex(), CharsetUtil.US_ASCII);
                  throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Unexpected HTTP/1.x request: %s", var6);
               } else {
                  var6 = ByteBufUtil.hexDump(var1, var1.readerIndex(), Math.min(var1.readableBytes(), this.clientPrefaceString.readableBytes()));
                  throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "HTTP/2 client preface string missing or corrupt. Hex dump for received bytes: %s", var6);
               }
            }
         }
      }

      private boolean verifyFirstFrameIsSettings(ByteBuf var1) throws Http2Exception {
         if (var1.readableBytes() < 5) {
            return false;
         } else {
            short var2 = var1.getUnsignedByte(var1.readerIndex() + 3);
            short var3 = var1.getUnsignedByte(var1.readerIndex() + 4);
            if (var2 == 4 && (var3 & 1) == 0) {
               return true;
            } else {
               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "First received frame was not SETTINGS. Hex dump for first 5 bytes: %s", ByteBufUtil.hexDump((ByteBuf)var1, var1.readerIndex(), 5));
            }
         }
      }

      private void sendPreface(ChannelHandlerContext var1) throws Exception {
         if (!this.prefaceSent && var1.channel().isActive()) {
            this.prefaceSent = true;
            boolean var2 = !Http2ConnectionHandler.this.connection().isServer();
            if (var2) {
               var1.write(Http2CodecUtil.connectionPrefaceBuf()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

            Http2ConnectionHandler.this.encoder.writeSettings(var1, Http2ConnectionHandler.this.initialSettings, var1.newPromise()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            if (var2) {
               Http2ConnectionHandler.this.userEventTriggered(var1, Http2ConnectionPrefaceAndSettingsFrameWrittenEvent.INSTANCE);
            }

         }
      }
   }

   private abstract class BaseDecoder {
      private BaseDecoder() {
         super();
      }

      public abstract void decode(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Exception;

      public void handlerRemoved(ChannelHandlerContext var1) throws Exception {
      }

      public void channelActive(ChannelHandlerContext var1) throws Exception {
      }

      public void channelInactive(ChannelHandlerContext var1) throws Exception {
         Http2ConnectionHandler.this.encoder().close();
         Http2ConnectionHandler.this.decoder().close();
         Http2ConnectionHandler.this.connection().close(var1.voidPromise());
      }

      public boolean prefaceSent() {
         return true;
      }

      // $FF: synthetic method
      BaseDecoder(Object var2) {
         this();
      }
   }
}
