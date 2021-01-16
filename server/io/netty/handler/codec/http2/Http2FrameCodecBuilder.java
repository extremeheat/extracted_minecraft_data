package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

public class Http2FrameCodecBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2FrameCodec, Http2FrameCodecBuilder> {
   private Http2FrameWriter frameWriter;

   Http2FrameCodecBuilder(boolean var1) {
      super();
      this.server(var1);
   }

   public static Http2FrameCodecBuilder forClient() {
      return new Http2FrameCodecBuilder(false);
   }

   public static Http2FrameCodecBuilder forServer() {
      return new Http2FrameCodecBuilder(true);
   }

   Http2FrameCodecBuilder frameWriter(Http2FrameWriter var1) {
      this.frameWriter = (Http2FrameWriter)ObjectUtil.checkNotNull(var1, "frameWriter");
      return this;
   }

   public Http2Settings initialSettings() {
      return super.initialSettings();
   }

   public Http2FrameCodecBuilder initialSettings(Http2Settings var1) {
      return (Http2FrameCodecBuilder)super.initialSettings(var1);
   }

   public long gracefulShutdownTimeoutMillis() {
      return super.gracefulShutdownTimeoutMillis();
   }

   public Http2FrameCodecBuilder gracefulShutdownTimeoutMillis(long var1) {
      return (Http2FrameCodecBuilder)super.gracefulShutdownTimeoutMillis(var1);
   }

   public boolean isServer() {
      return super.isServer();
   }

   public int maxReservedStreams() {
      return super.maxReservedStreams();
   }

   public Http2FrameCodecBuilder maxReservedStreams(int var1) {
      return (Http2FrameCodecBuilder)super.maxReservedStreams(var1);
   }

   public boolean isValidateHeaders() {
      return super.isValidateHeaders();
   }

   public Http2FrameCodecBuilder validateHeaders(boolean var1) {
      return (Http2FrameCodecBuilder)super.validateHeaders(var1);
   }

   public Http2FrameLogger frameLogger() {
      return super.frameLogger();
   }

   public Http2FrameCodecBuilder frameLogger(Http2FrameLogger var1) {
      return (Http2FrameCodecBuilder)super.frameLogger(var1);
   }

   public boolean encoderEnforceMaxConcurrentStreams() {
      return super.encoderEnforceMaxConcurrentStreams();
   }

   public Http2FrameCodecBuilder encoderEnforceMaxConcurrentStreams(boolean var1) {
      return (Http2FrameCodecBuilder)super.encoderEnforceMaxConcurrentStreams(var1);
   }

   public Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
      return super.headerSensitivityDetector();
   }

   public Http2FrameCodecBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector var1) {
      return (Http2FrameCodecBuilder)super.headerSensitivityDetector(var1);
   }

   public Http2FrameCodecBuilder encoderIgnoreMaxHeaderListSize(boolean var1) {
      return (Http2FrameCodecBuilder)super.encoderIgnoreMaxHeaderListSize(var1);
   }

   public Http2FrameCodecBuilder initialHuffmanDecodeCapacity(int var1) {
      return (Http2FrameCodecBuilder)super.initialHuffmanDecodeCapacity(var1);
   }

   public Http2FrameCodec build() {
      Object var1 = this.frameWriter;
      if (var1 != null) {
         DefaultHttp2Connection var2 = new DefaultHttp2Connection(this.isServer(), this.maxReservedStreams());
         Long var3 = this.initialSettings().maxHeaderListSize();
         Object var4 = new DefaultHttp2FrameReader(var3 == null ? new DefaultHttp2HeadersDecoder(true) : new DefaultHttp2HeadersDecoder(true, var3));
         if (this.frameLogger() != null) {
            var1 = new Http2OutboundFrameLogger((Http2FrameWriter)var1, this.frameLogger());
            var4 = new Http2InboundFrameLogger((Http2FrameReader)var4, this.frameLogger());
         }

         Object var5 = new DefaultHttp2ConnectionEncoder(var2, (Http2FrameWriter)var1);
         if (this.encoderEnforceMaxConcurrentStreams()) {
            var5 = new StreamBufferingEncoder((Http2ConnectionEncoder)var5);
         }

         DefaultHttp2ConnectionDecoder var6 = new DefaultHttp2ConnectionDecoder(var2, (Http2ConnectionEncoder)var5, (Http2FrameReader)var4);
         return this.build(var6, (Http2ConnectionEncoder)var5, this.initialSettings());
      } else {
         return (Http2FrameCodec)super.build();
      }
   }

   protected Http2FrameCodec build(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2, Http2Settings var3) {
      return new Http2FrameCodec(var2, var1, var3);
   }
}
