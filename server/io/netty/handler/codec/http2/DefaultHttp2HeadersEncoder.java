package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttp2HeadersEncoder implements Http2HeadersEncoder, Http2HeadersEncoder.Configuration {
   private final HpackEncoder hpackEncoder;
   private final Http2HeadersEncoder.SensitivityDetector sensitivityDetector;
   private final ByteBuf tableSizeChangeOutput;

   public DefaultHttp2HeadersEncoder() {
      this(NEVER_SENSITIVE);
   }

   public DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector var1) {
      this(var1, new HpackEncoder());
   }

   public DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector var1, boolean var2) {
      this(var1, new HpackEncoder(var2));
   }

   public DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector var1, boolean var2, int var3) {
      this(var1, new HpackEncoder(var2, var3));
   }

   DefaultHttp2HeadersEncoder(Http2HeadersEncoder.SensitivityDetector var1, HpackEncoder var2) {
      super();
      this.tableSizeChangeOutput = Unpooled.buffer();
      this.sensitivityDetector = (Http2HeadersEncoder.SensitivityDetector)ObjectUtil.checkNotNull(var1, "sensitiveDetector");
      this.hpackEncoder = (HpackEncoder)ObjectUtil.checkNotNull(var2, "hpackEncoder");
   }

   public void encodeHeaders(int var1, Http2Headers var2, ByteBuf var3) throws Http2Exception {
      try {
         if (this.tableSizeChangeOutput.isReadable()) {
            var3.writeBytes(this.tableSizeChangeOutput);
            this.tableSizeChangeOutput.clear();
         }

         this.hpackEncoder.encodeHeaders(var1, var3, var2, this.sensitivityDetector);
      } catch (Http2Exception var5) {
         throw var5;
      } catch (Throwable var6) {
         throw Http2Exception.connectionError(Http2Error.COMPRESSION_ERROR, var6, "Failed encoding headers block: %s", var6.getMessage());
      }
   }

   public void maxHeaderTableSize(long var1) throws Http2Exception {
      this.hpackEncoder.setMaxHeaderTableSize(this.tableSizeChangeOutput, var1);
   }

   public long maxHeaderTableSize() {
      return this.hpackEncoder.getMaxHeaderTableSize();
   }

   public void maxHeaderListSize(long var1) throws Http2Exception {
      this.hpackEncoder.setMaxHeaderListSize(var1);
   }

   public long maxHeaderListSize() {
      return this.hpackEncoder.getMaxHeaderListSize();
   }

   public Http2HeadersEncoder.Configuration configuration() {
      return this;
   }
}
