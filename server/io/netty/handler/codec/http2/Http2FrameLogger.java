package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.logging.LogLevel;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class Http2FrameLogger extends ChannelHandlerAdapter {
   private static final int BUFFER_LENGTH_THRESHOLD = 64;
   private final InternalLogger logger;
   private final InternalLogLevel level;

   public Http2FrameLogger(LogLevel var1) {
      this(var1.toInternalLevel(), InternalLoggerFactory.getInstance(Http2FrameLogger.class));
   }

   public Http2FrameLogger(LogLevel var1, String var2) {
      this(var1.toInternalLevel(), InternalLoggerFactory.getInstance(var2));
   }

   public Http2FrameLogger(LogLevel var1, Class<?> var2) {
      this(var1.toInternalLevel(), InternalLoggerFactory.getInstance(var2));
   }

   private Http2FrameLogger(InternalLogLevel var1, InternalLogger var2) {
      super();
      this.level = (InternalLogLevel)ObjectUtil.checkNotNull(var1, "level");
      this.logger = (InternalLogger)ObjectUtil.checkNotNull(var2, "logger");
   }

   public void logData(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, ByteBuf var4, int var5, boolean var6) {
      this.logger.log(this.level, "{} {} DATA: streamId={} padding={} endStream={} length={} bytes={}", var2.channel(), var1.name(), var3, var5, var6, var4.readableBytes(), this.toString(var4));
   }

   public void logHeaders(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, Http2Headers var4, int var5, boolean var6) {
      this.logger.log(this.level, "{} {} HEADERS: streamId={} headers={} padding={} endStream={}", var2.channel(), var1.name(), var3, var4, var5, var6);
   }

   public void logHeaders(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, Http2Headers var4, int var5, short var6, boolean var7, int var8, boolean var9) {
      this.logger.log(this.level, "{} {} HEADERS: streamId={} headers={} streamDependency={} weight={} exclusive={} padding={} endStream={}", var2.channel(), var1.name(), var3, var4, var5, var6, var7, var8, var9);
   }

   public void logPriority(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, int var4, short var5, boolean var6) {
      this.logger.log(this.level, "{} {} PRIORITY: streamId={} streamDependency={} weight={} exclusive={}", var2.channel(), var1.name(), var3, var4, var5, var6);
   }

   public void logRstStream(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, long var4) {
      this.logger.log(this.level, "{} {} RST_STREAM: streamId={} errorCode={}", var2.channel(), var1.name(), var3, var4);
   }

   public void logSettingsAck(Http2FrameLogger.Direction var1, ChannelHandlerContext var2) {
      this.logger.log(this.level, "{} {} SETTINGS: ack=true", var2.channel(), var1.name());
   }

   public void logSettings(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, Http2Settings var3) {
      this.logger.log(this.level, "{} {} SETTINGS: ack=false settings={}", var2.channel(), var1.name(), var3);
   }

   public void logPing(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, long var3) {
      this.logger.log(this.level, "{} {} PING: ack=false bytes={}", var2.channel(), var1.name(), var3);
   }

   public void logPingAck(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, long var3) {
      this.logger.log(this.level, "{} {} PING: ack=true bytes={}", var2.channel(), var1.name(), var3);
   }

   public void logPushPromise(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, int var4, Http2Headers var5, int var6) {
      this.logger.log(this.level, "{} {} PUSH_PROMISE: streamId={} promisedStreamId={} headers={} padding={}", var2.channel(), var1.name(), var3, var4, var5, var6);
   }

   public void logGoAway(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, long var4, ByteBuf var6) {
      this.logger.log(this.level, "{} {} GO_AWAY: lastStreamId={} errorCode={} length={} bytes={}", var2.channel(), var1.name(), var3, var4, var6.readableBytes(), this.toString(var6));
   }

   public void logWindowsUpdate(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, int var3, int var4) {
      this.logger.log(this.level, "{} {} WINDOW_UPDATE: streamId={} windowSizeIncrement={}", var2.channel(), var1.name(), var3, var4);
   }

   public void logUnknownFrame(Http2FrameLogger.Direction var1, ChannelHandlerContext var2, byte var3, int var4, Http2Flags var5, ByteBuf var6) {
      this.logger.log(this.level, "{} {} UNKNOWN: frameType={} streamId={} flags={} length={} bytes={}", var2.channel(), var1.name(), var3 & 255, var4, var5.value(), var6.readableBytes(), this.toString(var6));
   }

   private String toString(ByteBuf var1) {
      if (!this.logger.isEnabled(this.level)) {
         return "";
      } else if (this.level != InternalLogLevel.TRACE && var1.readableBytes() > 64) {
         int var2 = Math.min(var1.readableBytes(), 64);
         return ByteBufUtil.hexDump(var1, var1.readerIndex(), var2) + "...";
      } else {
         return ByteBufUtil.hexDump(var1);
      }
   }

   public static enum Direction {
      INBOUND,
      OUTBOUND;

      private Direction() {
      }
   }
}
