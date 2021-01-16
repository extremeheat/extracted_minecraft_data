package io.netty.handler.codec.http2;

public final class HttpToHttp2ConnectionHandlerBuilder extends AbstractHttp2ConnectionHandlerBuilder<HttpToHttp2ConnectionHandler, HttpToHttp2ConnectionHandlerBuilder> {
   public HttpToHttp2ConnectionHandlerBuilder() {
      super();
   }

   public HttpToHttp2ConnectionHandlerBuilder validateHeaders(boolean var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.validateHeaders(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder initialSettings(Http2Settings var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.initialSettings(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder frameListener(Http2FrameListener var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.frameListener(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder gracefulShutdownTimeoutMillis(long var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.gracefulShutdownTimeoutMillis(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder server(boolean var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.server(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder connection(Http2Connection var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.connection(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder codec(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.codec(var1, var2);
   }

   public HttpToHttp2ConnectionHandlerBuilder frameLogger(Http2FrameLogger var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.frameLogger(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder encoderEnforceMaxConcurrentStreams(boolean var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.encoderEnforceMaxConcurrentStreams(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.headerSensitivityDetector(var1);
   }

   public HttpToHttp2ConnectionHandlerBuilder initialHuffmanDecodeCapacity(int var1) {
      return (HttpToHttp2ConnectionHandlerBuilder)super.initialHuffmanDecodeCapacity(var1);
   }

   public HttpToHttp2ConnectionHandler build() {
      return (HttpToHttp2ConnectionHandler)super.build();
   }

   protected HttpToHttp2ConnectionHandler build(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2, Http2Settings var3) {
      return new HttpToHttp2ConnectionHandler(var1, var2, var3, this.isValidateHeaders());
   }
}
