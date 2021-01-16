package io.netty.handler.codec.http2;

import io.netty.buffer.ByteBuf;

public interface Http2HeadersDecoder {
   Http2Headers decodeHeaders(int var1, ByteBuf var2) throws Http2Exception;

   Http2HeadersDecoder.Configuration configuration();

   public interface Configuration {
      void maxHeaderTableSize(long var1) throws Http2Exception;

      long maxHeaderTableSize();

      void maxHeaderListSize(long var1, long var3) throws Http2Exception;

      long maxHeaderListSize();

      long maxHeaderListSizeGoAway();
   }
}
