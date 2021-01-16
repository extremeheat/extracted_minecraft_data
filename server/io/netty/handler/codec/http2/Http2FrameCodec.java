package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.handler.codec.http.HttpServerUpgradeHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class Http2FrameCodec extends Http2ConnectionHandler {
   private static final InternalLogger LOG = InternalLoggerFactory.getInstance(Http2FrameCodec.class);
   private final Http2Connection.PropertyKey streamKey;
   private final Http2Connection.PropertyKey upgradeKey;
   private final Integer initialFlowControlWindowSize;
   private ChannelHandlerContext ctx;
   private int numBufferedStreams;
   private Http2FrameCodec.DefaultHttp2FrameStream frameStreamToInitialize;

   Http2FrameCodec(Http2ConnectionEncoder var1, Http2ConnectionDecoder var2, Http2Settings var3) {
      super(var2, var1, var3);
      var2.frameListener(new Http2FrameCodec.FrameListener());
      this.connection().addListener(new Http2FrameCodec.ConnectionListener());
      ((Http2RemoteFlowController)this.connection().remote().flowController()).listener(new Http2FrameCodec.Http2RemoteFlowControllerListener());
      this.streamKey = this.connection().newKey();
      this.upgradeKey = this.connection().newKey();
      this.initialFlowControlWindowSize = var3.initialWindowSize();
   }

   Http2FrameCodec.DefaultHttp2FrameStream newStream() {
      return new Http2FrameCodec.DefaultHttp2FrameStream();
   }

   final void forEachActiveStream(final Http2FrameStreamVisitor var1) throws Http2Exception {
      assert this.ctx.executor().inEventLoop();

      this.connection().forEachActiveStream(new Http2StreamVisitor() {
         public boolean visit(Http2Stream var1x) {
            try {
               return var1.visit((Http2FrameStream)var1x.getProperty(Http2FrameCodec.this.streamKey));
            } catch (Throwable var3) {
               Http2FrameCodec.this.onError(Http2FrameCodec.this.ctx, false, var3);
               return false;
            }
         }
      });
   }

   public final void handlerAdded(ChannelHandlerContext var1) throws Exception {
      this.ctx = var1;
      super.handlerAdded(var1);
      this.handlerAdded0(var1);
      Http2Connection var2 = this.connection();
      if (var2.isServer()) {
         this.tryExpandConnectionFlowControlWindow(var2);
      }

   }

   private void tryExpandConnectionFlowControlWindow(Http2Connection var1) throws Http2Exception {
      if (this.initialFlowControlWindowSize != null) {
         Http2Stream var2 = var1.connectionStream();
         Http2LocalFlowController var3 = (Http2LocalFlowController)var1.local().flowController();
         int var4 = this.initialFlowControlWindowSize - var3.initialWindowSize(var2);
         if (var4 > 0) {
            var3.incrementWindowSize(var2, Math.max(var4 << 1, var4));
            this.flush(this.ctx);
         }
      }

   }

   void handlerAdded0(ChannelHandlerContext var1) throws Exception {
   }

   public final void userEventTriggered(ChannelHandlerContext var1, Object var2) throws Exception {
      if (var2 == Http2ConnectionPrefaceAndSettingsFrameWrittenEvent.INSTANCE) {
         this.tryExpandConnectionFlowControlWindow(this.connection());
      } else if (var2 instanceof HttpServerUpgradeHandler.UpgradeEvent) {
         HttpServerUpgradeHandler.UpgradeEvent var3 = (HttpServerUpgradeHandler.UpgradeEvent)var2;

         try {
            this.onUpgradeEvent(var1, var3.retain());
            Http2Stream var4 = this.connection().stream(1);
            if (var4.getProperty(this.streamKey) == null) {
               this.onStreamActive0(var4);
            }

            var3.upgradeRequest().headers().setInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text(), 1);
            var4.setProperty(this.upgradeKey, true);
            InboundHttpToHttp2Adapter.handle(var1, this.connection(), this.decoder().frameListener(), var3.upgradeRequest().retain());
         } finally {
            var3.release();
         }

         return;
      }

      super.userEventTriggered(var1, var2);
   }

   public void write(ChannelHandlerContext var1, Object var2, ChannelPromise var3) {
      if (var2 instanceof Http2DataFrame) {
         Http2DataFrame var4 = (Http2DataFrame)var2;
         this.encoder().writeData(var1, var4.stream().id(), var4.content(), var4.padding(), var4.isEndStream(), var3);
      } else if (var2 instanceof Http2HeadersFrame) {
         this.writeHeadersFrame(var1, (Http2HeadersFrame)var2, var3);
      } else if (var2 instanceof Http2WindowUpdateFrame) {
         Http2WindowUpdateFrame var8 = (Http2WindowUpdateFrame)var2;
         Http2FrameStream var5 = var8.stream();

         try {
            if (var5 == null) {
               this.increaseInitialConnectionWindow(var8.windowSizeIncrement());
            } else {
               this.consumeBytes(var5.id(), var8.windowSizeIncrement());
            }

            var3.setSuccess();
         } catch (Throwable var7) {
            var3.setFailure(var7);
         }
      } else if (var2 instanceof Http2ResetFrame) {
         Http2ResetFrame var9 = (Http2ResetFrame)var2;
         this.encoder().writeRstStream(var1, var9.stream().id(), var9.errorCode(), var3);
      } else if (var2 instanceof Http2PingFrame) {
         Http2PingFrame var10 = (Http2PingFrame)var2;
         this.encoder().writePing(var1, var10.ack(), var10.content(), var3);
      } else if (var2 instanceof Http2SettingsFrame) {
         this.encoder().writeSettings(var1, ((Http2SettingsFrame)var2).settings(), var3);
      } else if (var2 instanceof Http2GoAwayFrame) {
         this.writeGoAwayFrame(var1, (Http2GoAwayFrame)var2, var3);
      } else if (var2 instanceof Http2UnknownFrame) {
         Http2UnknownFrame var11 = (Http2UnknownFrame)var2;
         this.encoder().writeFrame(var1, var11.frameType(), var11.stream().id(), var11.flags(), var11.content(), var3);
      } else {
         if (var2 instanceof Http2Frame) {
            ReferenceCountUtil.release(var2);
            throw new UnsupportedMessageTypeException(var2, new Class[0]);
         }

         var1.write(var2, var3);
      }

   }

   private void increaseInitialConnectionWindow(int var1) throws Http2Exception {
      ((Http2LocalFlowController)this.connection().local().flowController()).incrementWindowSize(this.connection().connectionStream(), var1);
   }

   final boolean consumeBytes(int var1, int var2) throws Http2Exception {
      Http2Stream var3 = this.connection().stream(var1);
      if (var3 != null && var1 == 1) {
         Boolean var4 = (Boolean)var3.getProperty(this.upgradeKey);
         if (Boolean.TRUE.equals(var4)) {
            return false;
         }
      }

      return ((Http2LocalFlowController)this.connection().local().flowController()).consumeBytes(var3, var2);
   }

   private void writeGoAwayFrame(ChannelHandlerContext var1, Http2GoAwayFrame var2, ChannelPromise var3) {
      if (var2.lastStreamId() > -1) {
         var2.release();
         throw new IllegalArgumentException("Last stream id must not be set on GOAWAY frame");
      } else {
         int var4 = this.connection().remote().lastStreamCreated();
         long var5 = (long)var4 + (long)var2.extraStreamIds() * 2L;
         if (var5 > 2147483647L) {
            var5 = 2147483647L;
         }

         this.goAway(var1, (int)var5, var2.errorCode(), var2.content(), var3);
      }
   }

   private void writeHeadersFrame(ChannelHandlerContext var1, Http2HeadersFrame var2, final ChannelPromise var3) {
      if (Http2CodecUtil.isStreamIdValid(var2.stream().id())) {
         this.encoder().writeHeaders(var1, var2.stream().id(), var2.headers(), var2.padding(), var2.isEndStream(), var3);
      } else {
         Http2FrameCodec.DefaultHttp2FrameStream var4 = (Http2FrameCodec.DefaultHttp2FrameStream)var2.stream();
         Http2Connection var5 = this.connection();
         int var6 = var5.local().incrementAndGetNextStreamId();
         if (var6 < 0) {
            var3.setFailure(new Http2NoMoreStreamIdsException());
            return;
         }

         var4.id = var6;

         assert this.frameStreamToInitialize == null;

         this.frameStreamToInitialize = var4;
         ChannelPromise var7 = var1.newPromise();
         this.encoder().writeHeaders(var1, var6, var2.headers(), var2.padding(), var2.isEndStream(), var7);
         if (var7.isDone()) {
            notifyHeaderWritePromise(var7, var3);
         } else {
            ++this.numBufferedStreams;
            var7.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture var1) throws Exception {
                  Http2FrameCodec.this.numBufferedStreams--;
                  Http2FrameCodec.notifyHeaderWritePromise(var1, var3);
               }
            });
         }
      }

   }

   private static void notifyHeaderWritePromise(ChannelFuture var0, ChannelPromise var1) {
      Throwable var2 = var0.cause();
      if (var2 == null) {
         var1.setSuccess();
      } else {
         var1.setFailure(var2);
      }

   }

   private void onStreamActive0(Http2Stream var1) {
      if (!this.connection().local().isValidStreamId(var1.id())) {
         Http2FrameCodec.DefaultHttp2FrameStream var2 = this.newStream().setStreamAndProperty(this.streamKey, var1);
         this.onHttp2StreamStateChanged(this.ctx, var2);
      }
   }

   protected void onConnectionError(ChannelHandlerContext var1, boolean var2, Throwable var3, Http2Exception var4) {
      if (!var2) {
         var1.fireExceptionCaught(var3);
      }

      super.onConnectionError(var1, var2, var3, var4);
   }

   protected final void onStreamError(ChannelHandlerContext var1, boolean var2, Throwable var3, Http2Exception.StreamException var4) {
      int var5 = var4.streamId();
      Http2Stream var6 = this.connection().stream(var5);
      if (var6 == null) {
         this.onHttp2UnknownStreamError(var1, var3, var4);
         super.onStreamError(var1, var2, var3, var4);
      } else {
         Http2FrameStream var7 = (Http2FrameStream)var6.getProperty(this.streamKey);
         if (var7 == null) {
            LOG.warn("Stream exception thrown without stream object attached.", var3);
            super.onStreamError(var1, var2, var3, var4);
         } else {
            if (!var2) {
               this.onHttp2FrameStreamException(var1, new Http2FrameStreamException(var7, var4.error(), var3));
            }

         }
      }
   }

   void onHttp2UnknownStreamError(ChannelHandlerContext var1, Throwable var2, Http2Exception.StreamException var3) {
      LOG.warn("Stream exception thrown for unkown stream {}.", var3.streamId(), var2);
   }

   protected final boolean isGracefulShutdownComplete() {
      return super.isGracefulShutdownComplete() && this.numBufferedStreams == 0;
   }

   void onUpgradeEvent(ChannelHandlerContext var1, HttpServerUpgradeHandler.UpgradeEvent var2) {
      var1.fireUserEventTriggered(var2);
   }

   void onHttp2StreamWritabilityChanged(ChannelHandlerContext var1, Http2FrameStream var2, boolean var3) {
      var1.fireUserEventTriggered(Http2FrameStreamEvent.writabilityChanged(var2));
   }

   void onHttp2StreamStateChanged(ChannelHandlerContext var1, Http2FrameStream var2) {
      var1.fireUserEventTriggered(Http2FrameStreamEvent.stateChanged(var2));
   }

   void onHttp2Frame(ChannelHandlerContext var1, Http2Frame var2) {
      var1.fireChannelRead(var2);
   }

   void onHttp2FrameStreamException(ChannelHandlerContext var1, Http2FrameStreamException var2) {
      var1.fireExceptionCaught(var2);
   }

   final boolean isWritable(Http2FrameCodec.DefaultHttp2FrameStream var1) {
      Http2Stream var2 = var1.stream;
      return var2 != null && ((Http2RemoteFlowController)this.connection().remote().flowController()).isWritable(var2);
   }

   static class DefaultHttp2FrameStream implements Http2FrameStream {
      private volatile int id = -1;
      volatile Http2Stream stream;

      DefaultHttp2FrameStream() {
         super();
      }

      Http2FrameCodec.DefaultHttp2FrameStream setStreamAndProperty(Http2Connection.PropertyKey var1, Http2Stream var2) {
         assert this.id == -1 || var2.id() == this.id;

         this.stream = var2;
         var2.setProperty(var1, this);
         return this;
      }

      public int id() {
         Http2Stream var1 = this.stream;
         return var1 == null ? this.id : var1.id();
      }

      public Http2Stream.State state() {
         Http2Stream var1 = this.stream;
         return var1 == null ? Http2Stream.State.IDLE : var1.state();
      }

      public String toString() {
         return String.valueOf(this.id());
      }
   }

   private final class Http2RemoteFlowControllerListener implements Http2RemoteFlowController.Listener {
      private Http2RemoteFlowControllerListener() {
         super();
      }

      public void writabilityChanged(Http2Stream var1) {
         Http2FrameStream var2 = (Http2FrameStream)var1.getProperty(Http2FrameCodec.this.streamKey);
         if (var2 != null) {
            Http2FrameCodec.this.onHttp2StreamWritabilityChanged(Http2FrameCodec.this.ctx, var2, ((Http2RemoteFlowController)Http2FrameCodec.this.connection().remote().flowController()).isWritable(var1));
         }
      }

      // $FF: synthetic method
      Http2RemoteFlowControllerListener(Object var2) {
         this();
      }
   }

   private final class FrameListener implements Http2FrameListener {
      private FrameListener() {
         super();
      }

      public void onUnknownFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5) {
         Http2FrameCodec.this.onHttp2Frame(var1, (new DefaultHttp2UnknownFrame(var2, var4, var5)).stream(this.requireStream(var3)).retain());
      }

      public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) {
         Http2FrameCodec.this.onHttp2Frame(var1, new DefaultHttp2SettingsFrame(var2));
      }

      public void onPingRead(ChannelHandlerContext var1, long var2) {
         Http2FrameCodec.this.onHttp2Frame(var1, new DefaultHttp2PingFrame(var2, false));
      }

      public void onPingAckRead(ChannelHandlerContext var1, long var2) {
         Http2FrameCodec.this.onHttp2Frame(var1, new DefaultHttp2PingFrame(var2, true));
      }

      public void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3) {
         Http2FrameCodec.this.onHttp2Frame(var1, (new DefaultHttp2ResetFrame(var3)).stream(this.requireStream(var2)));
      }

      public void onWindowUpdateRead(ChannelHandlerContext var1, int var2, int var3) {
         if (var2 != 0) {
            Http2FrameCodec.this.onHttp2Frame(var1, (new DefaultHttp2WindowUpdateFrame(var3)).stream(this.requireStream(var2)));
         }
      }

      public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) {
         this.onHeadersRead(var1, var2, var3, var7, var8);
      }

      public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) {
         Http2FrameCodec.this.onHttp2Frame(var1, (new DefaultHttp2HeadersFrame(var3, var5, var4)).stream(this.requireStream(var2)));
      }

      public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) {
         Http2FrameCodec.this.onHttp2Frame(var1, (new DefaultHttp2DataFrame(var3, var5, var4)).stream(this.requireStream(var2)).retain());
         return 0;
      }

      public void onGoAwayRead(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5) {
         Http2FrameCodec.this.onHttp2Frame(var1, (new DefaultHttp2GoAwayFrame(var2, var3, var5)).retain());
      }

      public void onPriorityRead(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5) {
      }

      public void onSettingsAckRead(ChannelHandlerContext var1) {
      }

      public void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5) {
      }

      private Http2FrameStream requireStream(int var1) {
         Http2FrameStream var2 = (Http2FrameStream)Http2FrameCodec.this.connection().stream(var1).getProperty(Http2FrameCodec.this.streamKey);
         if (var2 == null) {
            throw new IllegalStateException("Stream object required for identifier: " + var1);
         } else {
            return var2;
         }
      }

      // $FF: synthetic method
      FrameListener(Object var2) {
         this();
      }
   }

   private final class ConnectionListener extends Http2ConnectionAdapter {
      private ConnectionListener() {
         super();
      }

      public void onStreamAdded(Http2Stream var1) {
         if (Http2FrameCodec.this.frameStreamToInitialize != null && var1.id() == Http2FrameCodec.this.frameStreamToInitialize.id()) {
            Http2FrameCodec.this.frameStreamToInitialize.setStreamAndProperty(Http2FrameCodec.this.streamKey, var1);
            Http2FrameCodec.this.frameStreamToInitialize = null;
         }

      }

      public void onStreamActive(Http2Stream var1) {
         Http2FrameCodec.this.onStreamActive0(var1);
      }

      public void onStreamClosed(Http2Stream var1) {
         Http2FrameCodec.DefaultHttp2FrameStream var2 = (Http2FrameCodec.DefaultHttp2FrameStream)var1.getProperty(Http2FrameCodec.this.streamKey);
         if (var2 != null) {
            Http2FrameCodec.this.onHttp2StreamStateChanged(Http2FrameCodec.this.ctx, var2);
         }

      }

      public void onStreamHalfClosed(Http2Stream var1) {
         Http2FrameCodec.DefaultHttp2FrameStream var2 = (Http2FrameCodec.DefaultHttp2FrameStream)var1.getProperty(Http2FrameCodec.this.streamKey);
         if (var2 != null) {
            Http2FrameCodec.this.onHttp2StreamStateChanged(Http2FrameCodec.this.ctx, var2);
         }

      }

      // $FF: synthetic method
      ConnectionListener(Object var2) {
         this();
      }
   }
}
