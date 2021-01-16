package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

abstract class SpdyHeaderBlockDecoder {
   SpdyHeaderBlockDecoder() {
      super();
   }

   static SpdyHeaderBlockDecoder newInstance(SpdyVersion var0, int var1) {
      return new SpdyHeaderBlockZlibDecoder(var0, var1);
   }

   abstract void decode(ByteBufAllocator var1, ByteBuf var2, SpdyHeadersFrame var3) throws Exception;

   abstract void endHeaderBlock(SpdyHeadersFrame var1) throws Exception;

   abstract void end();
}
