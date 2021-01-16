package io.netty.handler.codec.http2;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.util.internal.ObjectUtil;

public class Http2MultiplexCodecBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2MultiplexCodec, Http2MultiplexCodecBuilder> {
   final ChannelHandler childHandler;

   Http2MultiplexCodecBuilder(boolean var1, ChannelHandler var2) {
      super();
      this.server(var1);
      this.childHandler = checkSharable((ChannelHandler)ObjectUtil.checkNotNull(var2, "childHandler"));
   }

   private static ChannelHandler checkSharable(ChannelHandler var0) {
      if (var0 instanceof ChannelHandlerAdapter && !((ChannelHandlerAdapter)var0).isSharable() && !var0.getClass().isAnnotationPresent(ChannelHandler.Sharable.class)) {
         throw new IllegalArgumentException("The handler must be Sharable");
      } else {
         return var0;
      }
   }

   public static Http2MultiplexCodecBuilder forClient(ChannelHandler var0) {
      return new Http2MultiplexCodecBuilder(false, var0);
   }

   public static Http2MultiplexCodecBuilder forServer(ChannelHandler var0) {
      return new Http2MultiplexCodecBuilder(true, var0);
   }

   public Http2Settings initialSettings() {
      return super.initialSettings();
   }

   public Http2MultiplexCodecBuilder initialSettings(Http2Settings var1) {
      return (Http2MultiplexCodecBuilder)super.initialSettings(var1);
   }

   public long gracefulShutdownTimeoutMillis() {
      return super.gracefulShutdownTimeoutMillis();
   }

   public Http2MultiplexCodecBuilder gracefulShutdownTimeoutMillis(long var1) {
      return (Http2MultiplexCodecBuilder)super.gracefulShutdownTimeoutMillis(var1);
   }

   public boolean isServer() {
      return super.isServer();
   }

   public int maxReservedStreams() {
      return super.maxReservedStreams();
   }

   public Http2MultiplexCodecBuilder maxReservedStreams(int var1) {
      return (Http2MultiplexCodecBuilder)super.maxReservedStreams(var1);
   }

   public boolean isValidateHeaders() {
      return super.isValidateHeaders();
   }

   public Http2MultiplexCodecBuilder validateHeaders(boolean var1) {
      return (Http2MultiplexCodecBuilder)super.validateHeaders(var1);
   }

   public Http2FrameLogger frameLogger() {
      return super.frameLogger();
   }

   public Http2MultiplexCodecBuilder frameLogger(Http2FrameLogger var1) {
      return (Http2MultiplexCodecBuilder)super.frameLogger(var1);
   }

   public boolean encoderEnforceMaxConcurrentStreams() {
      return super.encoderEnforceMaxConcurrentStreams();
   }

   public Http2MultiplexCodecBuilder encoderEnforceMaxConcurrentStreams(boolean var1) {
      return (Http2MultiplexCodecBuilder)super.encoderEnforceMaxConcurrentStreams(var1);
   }

   public Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
      return super.headerSensitivityDetector();
   }

   public Http2MultiplexCodecBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector var1) {
      return (Http2MultiplexCodecBuilder)super.headerSensitivityDetector(var1);
   }

   public Http2MultiplexCodecBuilder encoderIgnoreMaxHeaderListSize(boolean var1) {
      return (Http2MultiplexCodecBuilder)super.encoderIgnoreMaxHeaderListSize(var1);
   }

   public Http2MultiplexCodecBuilder initialHuffmanDecodeCapacity(int var1) {
      return (Http2MultiplexCodecBuilder)super.initialHuffmanDecodeCapacity(var1);
   }

   public Http2MultiplexCodec build() {
      return (Http2MultiplexCodec)super.build();
   }

   protected Http2MultiplexCodec build(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2, Http2Settings var3) {
      return new Http2MultiplexCodec(var2, var1, var3, this.childHandler);
   }
}
