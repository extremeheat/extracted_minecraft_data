package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttp2HeadersDecoder implements Http2HeadersDecoder, Http2HeadersDecoder.Configuration {
   private static final float HEADERS_COUNT_WEIGHT_NEW = 0.2F;
   private static final float HEADERS_COUNT_WEIGHT_HISTORICAL = 0.8F;
   private final HpackDecoder hpackDecoder;
   private final boolean validateHeaders;
   private float headerArraySizeAccumulator;

   public DefaultHttp2HeadersDecoder() {
      this(true);
   }

   public DefaultHttp2HeadersDecoder(boolean var1) {
      this(var1, 8192L);
   }

   public DefaultHttp2HeadersDecoder(boolean var1, long var2) {
      this(var1, var2, 32);
   }

   public DefaultHttp2HeadersDecoder(boolean var1, long var2, int var4) {
      this(var1, new HpackDecoder(var2, var4));
   }

   DefaultHttp2HeadersDecoder(boolean var1, HpackDecoder var2) {
      super();
      this.headerArraySizeAccumulator = 8.0F;
      this.hpackDecoder = (HpackDecoder)ObjectUtil.checkNotNull(var2, "hpackDecoder");
      this.validateHeaders = var1;
   }

   public void maxHeaderTableSize(long var1) throws Http2Exception {
      this.hpackDecoder.setMaxHeaderTableSize(var1);
   }

   public long maxHeaderTableSize() {
      return this.hpackDecoder.getMaxHeaderTableSize();
   }

   public void maxHeaderListSize(long var1, long var3) throws Http2Exception {
      this.hpackDecoder.setMaxHeaderListSize(var1, var3);
   }

   public long maxHeaderListSize() {
      return this.hpackDecoder.getMaxHeaderListSize();
   }

   public long maxHeaderListSizeGoAway() {
      return this.hpackDecoder.getMaxHeaderListSizeGoAway();
   }

   public Http2HeadersDecoder.Configuration configuration() {
      return this;
   }

   public Http2Headers decodeHeaders(int var1, ByteBuf var2) throws Http2Exception {
      try {
         Http2Headers var3 = this.newHeaders();
         this.hpackDecoder.decode(var1, var2, var3, this.validateHeaders);
         this.headerArraySizeAccumulator = 0.2F * (float)var3.size() + 0.8F * this.headerArraySizeAccumulator;
         return var3;
      } catch (Http2Exception var4) {
         throw var4;
      } catch (Throwable var5) {
         throw Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, var5, var5.getMessage());
      }
   }

   protected final int numberOfHeadersGuess() {
      return (int)this.headerArraySizeAccumulator;
   }

   protected final boolean validateHeaders() {
      return this.validateHeaders;
   }

   protected Http2Headers newHeaders() {
      return new DefaultHttp2Headers(this.validateHeaders, (int)this.headerArraySizeAccumulator);
   }
}
