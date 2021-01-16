package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;

public abstract class AbstractHttp2ConnectionHandlerBuilder<T extends Http2ConnectionHandler, B extends AbstractHttp2ConnectionHandlerBuilder<T, B>> {
   private static final Http2HeadersEncoder.SensitivityDetector DEFAULT_HEADER_SENSITIVITY_DETECTOR;
   private Http2Settings initialSettings = Http2Settings.defaultSettings();
   private Http2FrameListener frameListener;
   private long gracefulShutdownTimeoutMillis;
   private Boolean isServer;
   private Integer maxReservedStreams;
   private Http2Connection connection;
   private Http2ConnectionDecoder decoder;
   private Http2ConnectionEncoder encoder;
   private Boolean validateHeaders;
   private Http2FrameLogger frameLogger;
   private Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector;
   private Boolean encoderEnforceMaxConcurrentStreams;
   private Boolean encoderIgnoreMaxHeaderListSize;
   private int initialHuffmanDecodeCapacity;

   public AbstractHttp2ConnectionHandlerBuilder() {
      super();
      this.gracefulShutdownTimeoutMillis = Http2CodecUtil.DEFAULT_GRACEFUL_SHUTDOWN_TIMEOUT_MILLIS;
      this.initialHuffmanDecodeCapacity = 32;
   }

   protected Http2Settings initialSettings() {
      return this.initialSettings;
   }

   protected B initialSettings(Http2Settings var1) {
      this.initialSettings = (Http2Settings)ObjectUtil.checkNotNull(var1, "settings");
      return this.self();
   }

   protected Http2FrameListener frameListener() {
      return this.frameListener;
   }

   protected B frameListener(Http2FrameListener var1) {
      this.frameListener = (Http2FrameListener)ObjectUtil.checkNotNull(var1, "frameListener");
      return this.self();
   }

   protected long gracefulShutdownTimeoutMillis() {
      return this.gracefulShutdownTimeoutMillis;
   }

   protected B gracefulShutdownTimeoutMillis(long var1) {
      if (var1 < -1L) {
         throw new IllegalArgumentException("gracefulShutdownTimeoutMillis: " + var1 + " (expected: -1 for indefinite or >= 0)");
      } else {
         this.gracefulShutdownTimeoutMillis = var1;
         return this.self();
      }
   }

   protected boolean isServer() {
      return this.isServer != null ? this.isServer : true;
   }

   protected B server(boolean var1) {
      enforceConstraint("server", "connection", this.connection);
      enforceConstraint("server", "codec", this.decoder);
      enforceConstraint("server", "codec", this.encoder);
      this.isServer = var1;
      return this.self();
   }

   protected int maxReservedStreams() {
      return this.maxReservedStreams != null ? this.maxReservedStreams : 100;
   }

   protected B maxReservedStreams(int var1) {
      enforceConstraint("server", "connection", this.connection);
      enforceConstraint("server", "codec", this.decoder);
      enforceConstraint("server", "codec", this.encoder);
      this.maxReservedStreams = ObjectUtil.checkPositiveOrZero(var1, "maxReservedStreams");
      return this.self();
   }

   protected Http2Connection connection() {
      return this.connection;
   }

   protected B connection(Http2Connection var1) {
      enforceConstraint("connection", "maxReservedStreams", this.maxReservedStreams);
      enforceConstraint("connection", "server", this.isServer);
      enforceConstraint("connection", "codec", this.decoder);
      enforceConstraint("connection", "codec", this.encoder);
      this.connection = (Http2Connection)ObjectUtil.checkNotNull(var1, "connection");
      return this.self();
   }

   protected Http2ConnectionDecoder decoder() {
      return this.decoder;
   }

   protected Http2ConnectionEncoder encoder() {
      return this.encoder;
   }

   protected B codec(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2) {
      enforceConstraint("codec", "server", this.isServer);
      enforceConstraint("codec", "maxReservedStreams", this.maxReservedStreams);
      enforceConstraint("codec", "connection", this.connection);
      enforceConstraint("codec", "frameLogger", this.frameLogger);
      enforceConstraint("codec", "validateHeaders", this.validateHeaders);
      enforceConstraint("codec", "headerSensitivityDetector", this.headerSensitivityDetector);
      enforceConstraint("codec", "encoderEnforceMaxConcurrentStreams", this.encoderEnforceMaxConcurrentStreams);
      ObjectUtil.checkNotNull(var1, "decoder");
      ObjectUtil.checkNotNull(var2, "encoder");
      if (var1.connection() != var2.connection()) {
         throw new IllegalArgumentException("The specified encoder and decoder have different connections.");
      } else {
         this.decoder = var1;
         this.encoder = var2;
         return this.self();
      }
   }

   protected boolean isValidateHeaders() {
      return this.validateHeaders != null ? this.validateHeaders : true;
   }

   protected B validateHeaders(boolean var1) {
      this.enforceNonCodecConstraints("validateHeaders");
      this.validateHeaders = var1;
      return this.self();
   }

   protected Http2FrameLogger frameLogger() {
      return this.frameLogger;
   }

   protected B frameLogger(Http2FrameLogger var1) {
      this.enforceNonCodecConstraints("frameLogger");
      this.frameLogger = (Http2FrameLogger)ObjectUtil.checkNotNull(var1, "frameLogger");
      return this.self();
   }

   protected boolean encoderEnforceMaxConcurrentStreams() {
      return this.encoderEnforceMaxConcurrentStreams != null ? this.encoderEnforceMaxConcurrentStreams : false;
   }

   protected B encoderEnforceMaxConcurrentStreams(boolean var1) {
      this.enforceNonCodecConstraints("encoderEnforceMaxConcurrentStreams");
      this.encoderEnforceMaxConcurrentStreams = var1;
      return this.self();
   }

   protected Http2HeadersEncoder.SensitivityDetector headerSensitivityDetector() {
      return this.headerSensitivityDetector != null ? this.headerSensitivityDetector : DEFAULT_HEADER_SENSITIVITY_DETECTOR;
   }

   protected B headerSensitivityDetector(Http2HeadersEncoder.SensitivityDetector var1) {
      this.enforceNonCodecConstraints("headerSensitivityDetector");
      this.headerSensitivityDetector = (Http2HeadersEncoder.SensitivityDetector)ObjectUtil.checkNotNull(var1, "headerSensitivityDetector");
      return this.self();
   }

   protected B encoderIgnoreMaxHeaderListSize(boolean var1) {
      this.enforceNonCodecConstraints("encoderIgnoreMaxHeaderListSize");
      this.encoderIgnoreMaxHeaderListSize = var1;
      return this.self();
   }

   protected B initialHuffmanDecodeCapacity(int var1) {
      this.enforceNonCodecConstraints("initialHuffmanDecodeCapacity");
      this.initialHuffmanDecodeCapacity = ObjectUtil.checkPositive(var1, "initialHuffmanDecodeCapacity");
      return this.self();
   }

   protected T build() {
      if (this.encoder != null) {
         assert this.decoder != null;

         return this.buildFromCodec(this.decoder, this.encoder);
      } else {
         Object var1 = this.connection;
         if (var1 == null) {
            var1 = new DefaultHttp2Connection(this.isServer(), this.maxReservedStreams());
         }

         return this.buildFromConnection((Http2Connection)var1);
      }
   }

   private T buildFromConnection(Http2Connection var1) {
      Long var2 = this.initialSettings.maxHeaderListSize();
      Object var3 = new DefaultHttp2FrameReader(new DefaultHttp2HeadersDecoder(this.isValidateHeaders(), var2 == null ? 8192L : var2, this.initialHuffmanDecodeCapacity));
      Object var4 = this.encoderIgnoreMaxHeaderListSize == null ? new DefaultHttp2FrameWriter(this.headerSensitivityDetector()) : new DefaultHttp2FrameWriter(this.headerSensitivityDetector(), this.encoderIgnoreMaxHeaderListSize);
      if (this.frameLogger != null) {
         var3 = new Http2InboundFrameLogger((Http2FrameReader)var3, this.frameLogger);
         var4 = new Http2OutboundFrameLogger((Http2FrameWriter)var4, this.frameLogger);
      }

      Object var5 = new DefaultHttp2ConnectionEncoder(var1, (Http2FrameWriter)var4);
      boolean var6 = this.encoderEnforceMaxConcurrentStreams();
      if (var6) {
         if (var1.isServer()) {
            ((Http2ConnectionEncoder)var5).close();
            ((Http2FrameReader)var3).close();
            throw new IllegalArgumentException("encoderEnforceMaxConcurrentStreams: " + var6 + " not supported for server");
         }

         var5 = new StreamBufferingEncoder((Http2ConnectionEncoder)var5);
      }

      DefaultHttp2ConnectionDecoder var7 = new DefaultHttp2ConnectionDecoder(var1, (Http2ConnectionEncoder)var5, (Http2FrameReader)var3);
      return this.buildFromCodec(var7, (Http2ConnectionEncoder)var5);
   }

   private T buildFromCodec(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2) {
      Http2ConnectionHandler var3;
      try {
         var3 = this.build(var1, var2, this.initialSettings);
      } catch (Throwable var5) {
         var2.close();
         var1.close();
         throw new IllegalStateException("failed to build a Http2ConnectionHandler", var5);
      }

      var3.gracefulShutdownTimeoutMillis(this.gracefulShutdownTimeoutMillis);
      if (var3.decoder().frameListener() == null) {
         var3.decoder().frameListener(this.frameListener);
      }

      return var3;
   }

   protected abstract T build(Http2ConnectionDecoder var1, Http2ConnectionEncoder var2, Http2Settings var3) throws Exception;

   protected final B self() {
      return this;
   }

   private void enforceNonCodecConstraints(String var1) {
      enforceConstraint(var1, "server/connection", this.decoder);
      enforceConstraint(var1, "server/connection", this.encoder);
   }

   private static void enforceConstraint(String var0, String var1, Object var2) {
      if (var2 != null) {
         throw new IllegalStateException(var0 + "() cannot be called because " + var1 + "() has been called already.");
      }
   }

   static {
      DEFAULT_HEADER_SENSITIVITY_DETECTOR = Http2HeadersEncoder.NEVER_SENSITIVE;
   }
}
