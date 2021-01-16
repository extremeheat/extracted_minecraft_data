package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpStatusClass;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.List;

public class DefaultHttp2ConnectionDecoder implements Http2ConnectionDecoder {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultHttp2ConnectionDecoder.class);
   private Http2FrameListener internalFrameListener;
   private final Http2Connection connection;
   private Http2LifecycleManager lifecycleManager;
   private final Http2ConnectionEncoder encoder;
   private final Http2FrameReader frameReader;
   private Http2FrameListener listener;
   private final Http2PromisedRequestVerifier requestVerifier;

   public DefaultHttp2ConnectionDecoder(Http2Connection var1, Http2ConnectionEncoder var2, Http2FrameReader var3) {
      this(var1, var2, var3, Http2PromisedRequestVerifier.ALWAYS_VERIFY);
   }

   public DefaultHttp2ConnectionDecoder(Http2Connection var1, Http2ConnectionEncoder var2, Http2FrameReader var3, Http2PromisedRequestVerifier var4) {
      super();
      this.internalFrameListener = new DefaultHttp2ConnectionDecoder.PrefaceFrameListener();
      this.connection = (Http2Connection)ObjectUtil.checkNotNull(var1, "connection");
      this.frameReader = (Http2FrameReader)ObjectUtil.checkNotNull(var3, "frameReader");
      this.encoder = (Http2ConnectionEncoder)ObjectUtil.checkNotNull(var2, "encoder");
      this.requestVerifier = (Http2PromisedRequestVerifier)ObjectUtil.checkNotNull(var4, "requestVerifier");
      if (var1.local().flowController() == null) {
         var1.local().flowController(new DefaultHttp2LocalFlowController(var1));
      }

      ((Http2LocalFlowController)var1.local().flowController()).frameWriter(var2.frameWriter());
   }

   public void lifecycleManager(Http2LifecycleManager var1) {
      this.lifecycleManager = (Http2LifecycleManager)ObjectUtil.checkNotNull(var1, "lifecycleManager");
   }

   public Http2Connection connection() {
      return this.connection;
   }

   public final Http2LocalFlowController flowController() {
      return (Http2LocalFlowController)this.connection.local().flowController();
   }

   public void frameListener(Http2FrameListener var1) {
      this.listener = (Http2FrameListener)ObjectUtil.checkNotNull(var1, "listener");
   }

   public Http2FrameListener frameListener() {
      return this.listener;
   }

   Http2FrameListener internalFrameListener() {
      return this.internalFrameListener;
   }

   public boolean prefaceReceived() {
      return DefaultHttp2ConnectionDecoder.FrameReadListener.class == this.internalFrameListener.getClass();
   }

   public void decodeFrame(ChannelHandlerContext var1, ByteBuf var2, List<Object> var3) throws Http2Exception {
      this.frameReader.readFrame(var1, var2, this.internalFrameListener);
   }

   public Http2Settings localSettings() {
      Http2Settings var1 = new Http2Settings();
      Http2FrameReader.Configuration var2 = this.frameReader.configuration();
      Http2HeadersDecoder.Configuration var3 = var2.headersConfiguration();
      Http2FrameSizePolicy var4 = var2.frameSizePolicy();
      var1.initialWindowSize(this.flowController().initialWindowSize());
      var1.maxConcurrentStreams((long)this.connection.remote().maxActiveStreams());
      var1.headerTableSize(var3.maxHeaderTableSize());
      var1.maxFrameSize(var4.maxFrameSize());
      var1.maxHeaderListSize(var3.maxHeaderListSize());
      if (!this.connection.isServer()) {
         var1.pushEnabled(this.connection.local().allowPushTo());
      }

      return var1;
   }

   public void close() {
      this.frameReader.close();
   }

   protected long calculateMaxHeaderListSizeGoAway(long var1) {
      return Http2CodecUtil.calculateMaxHeaderListSizeGoAway(var1);
   }

   private int unconsumedBytes(Http2Stream var1) {
      return this.flowController().unconsumedBytes(var1);
   }

   void onGoAwayRead0(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5) throws Http2Exception {
      if (this.connection.goAwayReceived() && this.connection.local().lastStreamKnownByPeer() < var2) {
         throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "lastStreamId MUST NOT increase. Current value: %d new value: %d", this.connection.local().lastStreamKnownByPeer(), var2);
      } else {
         this.listener.onGoAwayRead(var1, var2, var3, var5);
         this.connection.goAwayReceived(var2, var3, var5);
      }
   }

   void onUnknownFrame0(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5) throws Http2Exception {
      this.listener.onUnknownFrame(var1, var2, var3, var4, var5);
   }

   private final class PrefaceFrameListener implements Http2FrameListener {
      private PrefaceFrameListener() {
         super();
      }

      private void verifyPrefaceReceived() throws Http2Exception {
         if (!DefaultHttp2ConnectionDecoder.this.prefaceReceived()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Received non-SETTINGS as first frame.");
         }
      }

      public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) throws Http2Exception {
         this.verifyPrefaceReceived();
         return DefaultHttp2ConnectionDecoder.this.internalFrameListener.onDataRead(var1, var2, var3, var4, var5);
      }

      public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onHeadersRead(var1, var2, var3, var4, var5);
      }

      public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onHeadersRead(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      public void onPriorityRead(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onPriorityRead(var1, var2, var3, var4, var5);
      }

      public void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onRstStreamRead(var1, var2, var3);
      }

      public void onSettingsAckRead(ChannelHandlerContext var1) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onSettingsAckRead(var1);
      }

      public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) throws Http2Exception {
         if (!DefaultHttp2ConnectionDecoder.this.prefaceReceived()) {
            DefaultHttp2ConnectionDecoder.this.internalFrameListener = DefaultHttp2ConnectionDecoder.this.new FrameReadListener();
         }

         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onSettingsRead(var1, var2);
      }

      public void onPingRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onPingRead(var1, var2);
      }

      public void onPingAckRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onPingAckRead(var1, var2);
      }

      public void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onPushPromiseRead(var1, var2, var3, var4, var5);
      }

      public void onGoAwayRead(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.onGoAwayRead0(var1, var2, var3, var5);
      }

      public void onWindowUpdateRead(ChannelHandlerContext var1, int var2, int var3) throws Http2Exception {
         this.verifyPrefaceReceived();
         DefaultHttp2ConnectionDecoder.this.internalFrameListener.onWindowUpdateRead(var1, var2, var3);
      }

      public void onUnknownFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.onUnknownFrame0(var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      PrefaceFrameListener(Object var2) {
         this();
      }
   }

   private final class FrameReadListener implements Http2FrameListener {
      private FrameReadListener() {
         super();
      }

      public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5) throws Http2Exception {
         Http2Stream var6 = DefaultHttp2ConnectionDecoder.this.connection.stream(var2);
         Http2LocalFlowController var7 = DefaultHttp2ConnectionDecoder.this.flowController();
         int var8 = var3.readableBytes() + var4;

         boolean var9;
         try {
            var9 = this.shouldIgnoreHeadersOrDataFrame(var1, var2, var6, "DATA");
         } catch (Http2Exception var20) {
            var7.receiveFlowControlledFrame(var6, var3, var4, var5);
            var7.consumeBytes(var6, var8);
            throw var20;
         } catch (Throwable var21) {
            throw Http2Exception.connectionError(Http2Error.INTERNAL_ERROR, var21, "Unhandled error on data stream id %d", var2);
         }

         if (var9) {
            var7.receiveFlowControlledFrame(var6, var3, var4, var5);
            var7.consumeBytes(var6, var8);
            this.verifyStreamMayHaveExisted(var2);
            return var8;
         } else {
            Http2Exception var10 = null;
            switch(var6.state()) {
            case OPEN:
            case HALF_CLOSED_LOCAL:
               break;
            case HALF_CLOSED_REMOTE:
            case CLOSED:
               var10 = Http2Exception.streamError(var6.id(), Http2Error.STREAM_CLOSED, "Stream %d in unexpected state: %s", var6.id(), var6.state());
               break;
            default:
               var10 = Http2Exception.streamError(var6.id(), Http2Error.PROTOCOL_ERROR, "Stream %d in unexpected state: %s", var6.id(), var6.state());
            }

            int var11 = DefaultHttp2ConnectionDecoder.this.unconsumedBytes(var6);

            int var12;
            try {
               int var13;
               try {
                  var7.receiveFlowControlledFrame(var6, var3, var4, var5);
                  DefaultHttp2ConnectionDecoder.this.unconsumedBytes(var6);
                  if (var10 != null) {
                     throw var10;
                  }

                  var8 = DefaultHttp2ConnectionDecoder.this.listener.onDataRead(var1, var2, var3, var4, var5);
                  var12 = var8;
               } catch (Http2Exception var22) {
                  var13 = var11 - DefaultHttp2ConnectionDecoder.this.unconsumedBytes(var6);
                  var8 -= var13;
                  throw var22;
               } catch (RuntimeException var23) {
                  var13 = var11 - DefaultHttp2ConnectionDecoder.this.unconsumedBytes(var6);
                  var8 -= var13;
                  throw var23;
               }
            } finally {
               var7.consumeBytes(var6, var8);
               if (var5) {
                  DefaultHttp2ConnectionDecoder.this.lifecycleManager.closeStreamRemote(var6, var1.newSucceededFuture());
               }

            }

            return var12;
         }
      }

      public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5) throws Http2Exception {
         this.onHeadersRead(var1, var2, var3, 0, (short)16, false, var4, var5);
      }

      public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception {
         Http2Stream var9 = DefaultHttp2ConnectionDecoder.this.connection.stream(var2);
         boolean var10 = false;
         if (var9 == null && !DefaultHttp2ConnectionDecoder.this.connection.streamMayHaveExisted(var2)) {
            var9 = DefaultHttp2ConnectionDecoder.this.connection.remote().createStream(var2, var8);
            var10 = var9.state() == Http2Stream.State.HALF_CLOSED_REMOTE;
         }

         if (!this.shouldIgnoreHeadersOrDataFrame(var1, var2, var9, "HEADERS")) {
            boolean var11 = !DefaultHttp2ConnectionDecoder.this.connection.isServer() && HttpStatusClass.valueOf(var3.status()) == HttpStatusClass.INFORMATIONAL;
            if ((!var11 && var8 || !var9.isHeadersReceived()) && !var9.isTrailersReceived()) {
               switch(var9.state()) {
               case OPEN:
               case HALF_CLOSED_LOCAL:
                  break;
               case HALF_CLOSED_REMOTE:
                  if (!var10) {
                     throw Http2Exception.streamError(var9.id(), Http2Error.STREAM_CLOSED, "Stream %d in unexpected state: %s", var9.id(), var9.state());
                  }
                  break;
               case CLOSED:
                  throw Http2Exception.streamError(var9.id(), Http2Error.STREAM_CLOSED, "Stream %d in unexpected state: %s", var9.id(), var9.state());
               case RESERVED_REMOTE:
                  var9.open(var8);
                  break;
               default:
                  throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d in unexpected state: %s", var9.id(), var9.state());
               }

               var9.headersReceived(var11);
               DefaultHttp2ConnectionDecoder.this.encoder.flowController().updateDependencyTree(var2, var4, var5, var6);
               DefaultHttp2ConnectionDecoder.this.listener.onHeadersRead(var1, var2, var3, var4, var5, var6, var7, var8);
               if (var8) {
                  DefaultHttp2ConnectionDecoder.this.lifecycleManager.closeStreamRemote(var9, var1.newSucceededFuture());
               }

            } else {
               throw Http2Exception.streamError(var2, Http2Error.PROTOCOL_ERROR, "Stream %d received too many headers EOS: %s state: %s", var2, var8, var9.state());
            }
         }
      }

      public void onPriorityRead(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.encoder.flowController().updateDependencyTree(var2, var3, var4, var5);
         DefaultHttp2ConnectionDecoder.this.listener.onPriorityRead(var1, var2, var3, var4, var5);
      }

      public void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3) throws Http2Exception {
         Http2Stream var5 = DefaultHttp2ConnectionDecoder.this.connection.stream(var2);
         if (var5 == null) {
            this.verifyStreamMayHaveExisted(var2);
         } else {
            switch(var5.state()) {
            case CLOSED:
               return;
            case IDLE:
               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "RST_STREAM received for IDLE stream %d", var2);
            default:
               DefaultHttp2ConnectionDecoder.this.listener.onRstStreamRead(var1, var2, var3);
               DefaultHttp2ConnectionDecoder.this.lifecycleManager.closeStream(var5, var1.newSucceededFuture());
            }
         }
      }

      public void onSettingsAckRead(ChannelHandlerContext var1) throws Http2Exception {
         Http2Settings var2 = DefaultHttp2ConnectionDecoder.this.encoder.pollSentSettings();
         if (var2 != null) {
            this.applyLocalSettings(var2);
         }

         DefaultHttp2ConnectionDecoder.this.listener.onSettingsAckRead(var1);
      }

      private void applyLocalSettings(Http2Settings var1) throws Http2Exception {
         Boolean var2 = var1.pushEnabled();
         Http2FrameReader.Configuration var3 = DefaultHttp2ConnectionDecoder.this.frameReader.configuration();
         Http2HeadersDecoder.Configuration var4 = var3.headersConfiguration();
         Http2FrameSizePolicy var5 = var3.frameSizePolicy();
         if (var2 != null) {
            if (DefaultHttp2ConnectionDecoder.this.connection.isServer()) {
               throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Server sending SETTINGS frame with ENABLE_PUSH specified");
            }

            DefaultHttp2ConnectionDecoder.this.connection.local().allowPushTo(var2);
         }

         Long var6 = var1.maxConcurrentStreams();
         if (var6 != null) {
            DefaultHttp2ConnectionDecoder.this.connection.remote().maxActiveStreams((int)Math.min(var6, 2147483647L));
         }

         Long var7 = var1.headerTableSize();
         if (var7 != null) {
            var4.maxHeaderTableSize(var7);
         }

         Long var8 = var1.maxHeaderListSize();
         if (var8 != null) {
            var4.maxHeaderListSize(var8, DefaultHttp2ConnectionDecoder.this.calculateMaxHeaderListSizeGoAway(var8));
         }

         Integer var9 = var1.maxFrameSize();
         if (var9 != null) {
            var5.maxFrameSize(var9);
         }

         Integer var10 = var1.initialWindowSize();
         if (var10 != null) {
            DefaultHttp2ConnectionDecoder.this.flowController().initialWindowSize(var10);
         }

      }

      public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.encoder.writeSettingsAck(var1, var1.newPromise());
         DefaultHttp2ConnectionDecoder.this.encoder.remoteSettings(var2);
         DefaultHttp2ConnectionDecoder.this.listener.onSettingsRead(var1, var2);
      }

      public void onPingRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.encoder.writePing(var1, true, var2, var1.newPromise());
         DefaultHttp2ConnectionDecoder.this.listener.onPingRead(var1, var2);
      }

      public void onPingAckRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.listener.onPingAckRead(var1, var2);
      }

      public void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5) throws Http2Exception {
         if (DefaultHttp2ConnectionDecoder.this.connection().isServer()) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "A client cannot push.");
         } else {
            Http2Stream var6 = DefaultHttp2ConnectionDecoder.this.connection.stream(var2);
            if (!this.shouldIgnoreHeadersOrDataFrame(var1, var2, var6, "PUSH_PROMISE")) {
               if (var6 == null) {
                  throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d does not exist", var2);
               } else {
                  switch(var6.state()) {
                  case OPEN:
                  case HALF_CLOSED_LOCAL:
                     if (!DefaultHttp2ConnectionDecoder.this.requestVerifier.isAuthoritative(var1, var4)) {
                        throw Http2Exception.streamError(var3, Http2Error.PROTOCOL_ERROR, "Promised request on stream %d for promised stream %d is not authoritative", var2, var3);
                     } else if (!DefaultHttp2ConnectionDecoder.this.requestVerifier.isCacheable(var4)) {
                        throw Http2Exception.streamError(var3, Http2Error.PROTOCOL_ERROR, "Promised request on stream %d for promised stream %d is not known to be cacheable", var2, var3);
                     } else {
                        if (!DefaultHttp2ConnectionDecoder.this.requestVerifier.isSafe(var4)) {
                           throw Http2Exception.streamError(var3, Http2Error.PROTOCOL_ERROR, "Promised request on stream %d for promised stream %d is not known to be safe", var2, var3);
                        }

                        DefaultHttp2ConnectionDecoder.this.connection.remote().reservePushStream(var3, var6);
                        DefaultHttp2ConnectionDecoder.this.listener.onPushPromiseRead(var1, var2, var3, var4, var5);
                        return;
                     }
                  default:
                     throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d in unexpected state for receiving push promise: %s", var6.id(), var6.state());
                  }
               }
            }
         }
      }

      public void onGoAwayRead(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.onGoAwayRead0(var1, var2, var3, var5);
      }

      public void onWindowUpdateRead(ChannelHandlerContext var1, int var2, int var3) throws Http2Exception {
         Http2Stream var4 = DefaultHttp2ConnectionDecoder.this.connection.stream(var2);
         if (var4 != null && var4.state() != Http2Stream.State.CLOSED && !this.streamCreatedAfterGoAwaySent(var2)) {
            DefaultHttp2ConnectionDecoder.this.encoder.flowController().incrementWindowSize(var4, var3);
            DefaultHttp2ConnectionDecoder.this.listener.onWindowUpdateRead(var1, var2, var3);
         } else {
            this.verifyStreamMayHaveExisted(var2);
         }
      }

      public void onUnknownFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5) throws Http2Exception {
         DefaultHttp2ConnectionDecoder.this.onUnknownFrame0(var1, var2, var3, var4, var5);
      }

      private boolean shouldIgnoreHeadersOrDataFrame(ChannelHandlerContext var1, int var2, Http2Stream var3, String var4) throws Http2Exception {
         if (var3 == null) {
            if (this.streamCreatedAfterGoAwaySent(var2)) {
               DefaultHttp2ConnectionDecoder.logger.info("{} ignoring {} frame for stream {}. Stream sent after GOAWAY sent", var1.channel(), var4, var2);
               return true;
            } else {
               throw Http2Exception.streamError(var2, Http2Error.STREAM_CLOSED, "Received %s frame for an unknown stream %d", var4, var2);
            }
         } else if (!var3.isResetSent() && !this.streamCreatedAfterGoAwaySent(var2)) {
            return false;
         } else {
            if (DefaultHttp2ConnectionDecoder.logger.isInfoEnabled()) {
               DefaultHttp2ConnectionDecoder.logger.info("{} ignoring {} frame for stream {} {}", var1.channel(), var4, var3.isResetSent() ? "RST_STREAM sent." : "Stream created after GOAWAY sent. Last known stream by peer " + DefaultHttp2ConnectionDecoder.this.connection.remote().lastStreamKnownByPeer());
            }

            return true;
         }
      }

      private boolean streamCreatedAfterGoAwaySent(int var1) {
         Http2Connection.Endpoint var2 = DefaultHttp2ConnectionDecoder.this.connection.remote();
         return DefaultHttp2ConnectionDecoder.this.connection.goAwaySent() && var2.isValidStreamId(var1) && var1 > var2.lastStreamKnownByPeer();
      }

      private void verifyStreamMayHaveExisted(int var1) throws Http2Exception {
         if (!DefaultHttp2ConnectionDecoder.this.connection.streamMayHaveExisted(var1)) {
            throw Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "Stream %d does not exist", var1);
         }
      }

      // $FF: synthetic method
      FrameReadListener(Object var2) {
         this();
      }
   }
}
