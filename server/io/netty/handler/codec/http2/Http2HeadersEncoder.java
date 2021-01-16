package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;

public interface Http2HeadersEncoder {
   Http2HeadersEncoder.SensitivityDetector NEVER_SENSITIVE = new Http2HeadersEncoder.SensitivityDetector() {
      public boolean isSensitive(CharSequence var1, CharSequence var2) {
         return false;
      }
   };
   Http2HeadersEncoder.SensitivityDetector ALWAYS_SENSITIVE = new Http2HeadersEncoder.SensitivityDetector() {
      public boolean isSensitive(CharSequence var1, CharSequence var2) {
         return true;
      }
   };

   void encodeHeaders(int var1, Http2Headers var2, ByteBuf var3) throws Http2Exception;

   Http2HeadersEncoder.Configuration configuration();

   public interface SensitivityDetector {
      boolean isSensitive(CharSequence var1, CharSequence var2);
   }

   public interface Configuration {
      void maxHeaderTableSize(long var1) throws Http2Exception;

      long maxHeaderTableSize();

      void maxHeaderListSize(long var1) throws Http2Exception;

      long maxHeaderListSize();
   }
}
