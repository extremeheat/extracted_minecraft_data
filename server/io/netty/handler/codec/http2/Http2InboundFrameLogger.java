package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.internal.ObjectUtil;

public class Http2InboundFrameLogger implements Http2FrameReader {
   private final Http2FrameReader reader;
   private final Http2FrameLogger logger;

   public Http2InboundFrameLogger(Http2FrameReader var1, Http2FrameLogger var2) {
      super();
      this.reader = (Http2FrameReader)ObjectUtil.checkNotNull(var1, "reader");
      this.logger = (Http2FrameLogger)ObjectUtil.checkNotNull(var2, "logger");
   }

   public void readFrame(ChannelHandlerContext var1, ByteBuf var2, final Http2FrameListener var3) throws Http2Exception {
      this.reader.readFrame(var1, var2, new Http2FrameListener() {
         public int onDataRead(ChannelHandlerContext var1, int var2, ByteBuf var3x, int var4, boolean var5) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logData(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x, var4, var5);
            return var3.onDataRead(var1, var2, var3x, var4, var5);
         }

         public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3x, int var4, boolean var5) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logHeaders(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x, var4, var5);
            var3.onHeadersRead(var1, var2, var3x, var4, var5);
         }

         public void onHeadersRead(ChannelHandlerContext var1, int var2, Http2Headers var3x, int var4, short var5, boolean var6, int var7, boolean var8) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logHeaders(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x, var4, var5, var6, var7, var8);
            var3.onHeadersRead(var1, var2, var3x, var4, var5, var6, var7, var8);
         }

         public void onPriorityRead(ChannelHandlerContext var1, int var2, int var3x, short var4, boolean var5) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logPriority(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x, var4, var5);
            var3.onPriorityRead(var1, var2, var3x, var4, var5);
         }

         public void onRstStreamRead(ChannelHandlerContext var1, int var2, long var3x) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logRstStream(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x);
            var3.onRstStreamRead(var1, var2, var3x);
         }

         public void onSettingsAckRead(ChannelHandlerContext var1) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logSettingsAck(Http2FrameLogger.Direction.INBOUND, var1);
            var3.onSettingsAckRead(var1);
         }

         public void onSettingsRead(ChannelHandlerContext var1, Http2Settings var2) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logSettings(Http2FrameLogger.Direction.INBOUND, var1, var2);
            var3.onSettingsRead(var1, var2);
         }

         public void onPingRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logPing(Http2FrameLogger.Direction.INBOUND, var1, var2);
            var3.onPingRead(var1, var2);
         }

         public void onPingAckRead(ChannelHandlerContext var1, long var2) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logPingAck(Http2FrameLogger.Direction.INBOUND, var1, var2);
            var3.onPingAckRead(var1, var2);
         }

         public void onPushPromiseRead(ChannelHandlerContext var1, int var2, int var3x, Http2Headers var4, int var5) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logPushPromise(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x, var4, var5);
            var3.onPushPromiseRead(var1, var2, var3x, var4, var5);
         }

         public void onGoAwayRead(ChannelHandlerContext var1, int var2, long var3x, ByteBuf var5) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logGoAway(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x, var5);
            var3.onGoAwayRead(var1, var2, var3x, var5);
         }

         public void onWindowUpdateRead(ChannelHandlerContext var1, int var2, int var3x) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logWindowsUpdate(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x);
            var3.onWindowUpdateRead(var1, var2, var3x);
         }

         public void onUnknownFrame(ChannelHandlerContext var1, byte var2, int var3x, Http2Flags var4, ByteBuf var5) throws Http2Exception {
            Http2InboundFrameLogger.this.logger.logUnknownFrame(Http2FrameLogger.Direction.INBOUND, var1, var2, var3x, var4, var5);
            var3.onUnknownFrame(var1, var2, var3x, var4, var5);
         }
      });
   }

   public void close() {
      this.reader.close();
   }

   public Http2FrameReader.Configuration configuration() {
      return this.reader.configuration();
   }
}
