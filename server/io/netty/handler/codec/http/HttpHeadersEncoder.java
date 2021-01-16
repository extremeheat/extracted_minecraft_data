package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

final class HttpHeadersEncoder {
   private static final int COLON_AND_SPACE_SHORT = 14880;

   private HttpHeadersEncoder() {
      super();
   }

   static void encoderHeader(CharSequence var0, CharSequence var1, ByteBuf var2) {
      int var3 = var0.length();
      int var4 = var1.length();
      int var5 = var3 + var4 + 4;
      var2.ensureWritable(var5);
      int var6 = var2.writerIndex();
      writeAscii(var2, var6, var0);
      var6 += var3;
      ByteBufUtil.setShortBE(var2, var6, 14880);
      var6 += 2;
      writeAscii(var2, var6, var1);
      var6 += var4;
      ByteBufUtil.setShortBE(var2, var6, 3338);
      var6 += 2;
      var2.writerIndex(var6);
   }

   private static void writeAscii(ByteBuf var0, int var1, CharSequence var2) {
      if (var2 instanceof AsciiString) {
         ByteBufUtil.copy((AsciiString)var2, 0, var0, var1, var2.length());
      } else {
         var0.setCharSequence(var1, var2, CharsetUtil.US_ASCII);
      }

   }
}
