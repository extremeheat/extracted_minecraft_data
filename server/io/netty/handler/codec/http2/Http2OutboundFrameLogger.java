package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;

public class Http2OutboundFrameLogger implements Http2FrameWriter {
   private final Http2FrameWriter writer;
   private final Http2FrameLogger logger;

   public Http2OutboundFrameLogger(Http2FrameWriter var1, Http2FrameLogger var2) {
      super();
      this.writer = (Http2FrameWriter)ObjectUtil.checkNotNull(var1, "writer");
      this.logger = (Http2FrameLogger)ObjectUtil.checkNotNull(var2, "logger");
   }

   public ChannelFuture writeData(ChannelHandlerContext var1, int var2, ByteBuf var3, int var4, boolean var5, ChannelPromise var6) {
      this.logger.logData(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3, var4, var5);
      return this.writer.writeData(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, boolean var5, ChannelPromise var6) {
      this.logger.logHeaders(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3, var4, var5);
      return this.writer.writeHeaders(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeHeaders(ChannelHandlerContext var1, int var2, Http2Headers var3, int var4, short var5, boolean var6, int var7, boolean var8, ChannelPromise var9) {
      this.logger.logHeaders(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3, var4, var5, var6, var7, var8);
      return this.writer.writeHeaders(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public ChannelFuture writePriority(ChannelHandlerContext var1, int var2, int var3, short var4, boolean var5, ChannelPromise var6) {
      this.logger.logPriority(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3, var4, var5);
      return this.writer.writePriority(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeRstStream(ChannelHandlerContext var1, int var2, long var3, ChannelPromise var5) {
      this.logger.logRstStream(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3);
      return this.writer.writeRstStream(var1, var2, var3, var5);
   }

   public ChannelFuture writeSettings(ChannelHandlerContext var1, Http2Settings var2, ChannelPromise var3) {
      this.logger.logSettings(Http2FrameLogger.Direction.OUTBOUND, var1, var2);
      return this.writer.writeSettings(var1, var2, var3);
   }

   public ChannelFuture writeSettingsAck(ChannelHandlerContext var1, ChannelPromise var2) {
      this.logger.logSettingsAck(Http2FrameLogger.Direction.OUTBOUND, var1);
      return this.writer.writeSettingsAck(var1, var2);
   }

   public ChannelFuture writePing(ChannelHandlerContext var1, boolean var2, long var3, ChannelPromise var5) {
      if (var2) {
         this.logger.logPingAck(Http2FrameLogger.Direction.OUTBOUND, var1, var3);
      } else {
         this.logger.logPing(Http2FrameLogger.Direction.OUTBOUND, var1, var3);
      }

      return this.writer.writePing(var1, var2, var3, var5);
   }

   public ChannelFuture writePushPromise(ChannelHandlerContext var1, int var2, int var3, Http2Headers var4, int var5, ChannelPromise var6) {
      this.logger.logPushPromise(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3, var4, var5);
      return this.writer.writePushPromise(var1, var2, var3, var4, var5, var6);
   }

   public ChannelFuture writeGoAway(ChannelHandlerContext var1, int var2, long var3, ByteBuf var5, ChannelPromise var6) {
      this.logger.logGoAway(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3, var5);
      return this.writer.writeGoAway(var1, var2, var3, var5, var6);
   }

   public ChannelFuture writeWindowUpdate(ChannelHandlerContext var1, int var2, int var3, ChannelPromise var4) {
      this.logger.logWindowsUpdate(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3);
      return this.writer.writeWindowUpdate(var1, var2, var3, var4);
   }

   public ChannelFuture writeFrame(ChannelHandlerContext var1, byte var2, int var3, Http2Flags var4, ByteBuf var5, ChannelPromise var6) {
      this.logger.logUnknownFrame(Http2FrameLogger.Direction.OUTBOUND, var1, var2, var3, var4, var5);
      return this.writer.writeFrame(var1, var2, var3, var4, var5, var6);
   }

   public void close() {
      this.writer.close();
   }

   public Http2FrameWriter.Configuration configuration() {
      return this.writer.configuration();
   }
}
