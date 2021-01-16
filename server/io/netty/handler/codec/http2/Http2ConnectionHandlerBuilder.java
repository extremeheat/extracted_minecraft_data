package io.netty.handler.codec.http2;

public final class Http2ConnectionHandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<Http2ConnectionHandler, Http2ConnectionHandlerBuilder> {
   public Http2ConnectionHandlerBuilder() {
      super();
   }

   public Http2ConnectionHandlerBuilder validateHeaders(boolean var1) {
      return (Http2ConnectionHandlerBuilder)super.validateHeaders(var1);
   }

   public Http2ConnectionHandlerBuilder initialSettings(Http2Settings var1) {
      return (Http2ConnectionHandlerBuilder)super.initialSettings(var1);
   }

   public Http2ConnectionHandlerBuilder frameListener(Http2FrameListener var1) {
      return (Http2ConnectionHandlerBuilder)super.frameListener(var1);
   }

   public Http2ConnectionHandlerBuilder gracefulShutdownTimeoutMillis(long var1) {
      return (Http2ConnectionHandlerBuilder)super.gracefulShutdownTimeoutMillis(var1);
   }

   public Http2ConnectionHandlerBuilder server(boolean var1) {
      return (Http2ConnectionHandlerBuilder)super.server(var1);
   }

   public Http2ConnectionHandlerBuilder connection(Http2Connection var1) {
      return (Http2ConnectionHandlerBuilder)super.connection(var1);
   }

   public Http2ConnectionHandlerBuilder maxReservedStreams(int var1) {
      return (Http2ConnectionHandlerBuilder)super.maxReservedStreams(var1);
   }

   public Http2ConnectionHandlerBuilder codec(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2) {
      return (Http2ConnectionHandlerBuilder)super.codec(var1, var2);
   }

   public Http2ConnectionHandlerBuilder frameLogger(Http2FrameLogger var1) {
      return (Http2ConnectionHandlerBuilder)super.frameLogger(var1);
   }

   public Http2ConnectionHandlerBuilder encoderEnforceMaxConcurrentStreams(boolean var1) {
      return (Http2ConnectionHandlerBuilder)super.encoderEnforceMaxConcurrentStreams(var1);
   }

   public Http2ConnectionHandlerBuilder encoderIgnoreMaxHeaderListSize(boolean var1) {
      return (Http2ConnectionHandlerBuilder)super.encoderIgnoreMaxHeaderListSize(var1);
   }

   public Http2ConnectionHandlerBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector var1) {
      return (Http2ConnectionHandlerBuilder)super.headerSensitivityDetector(var1);
   }

   public Http2ConnectionHandlerBuilder initialHuffmanDecodeCapacity(int var1) {
      return (Http2ConnectionHandlerBuilder)super.initialHuffmanDecodeCapacity(var1);
   }

   public Http2ConnectionHandler build() {
      return super.build();
   }

   protected Http2ConnectionHandler build(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2, Http2Settings var3) {
      return new Http2ConnectionHandler(var1, var2, var3);
   }
}
