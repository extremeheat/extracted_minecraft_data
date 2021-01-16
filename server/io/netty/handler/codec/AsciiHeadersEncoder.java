package io.netty.handler.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import java.util.Map.Entry;

public final class AsciiHeadersEncoder {
   private final ByteBuf buf;
   private final AsciiHeadersEncoder.SeparatorType separatorType;
   private final AsciiHeadersEncoder.NewlineType newlineType;

   public AsciiHeadersEncoder(ByteBuf var1) {
      this(var1, AsciiHeadersEncoder.SeparatorType.COLON_SPACE, AsciiHeadersEncoder.NewlineType.CRLF);
   }

   public AsciiHeadersEncoder(ByteBuf var1, AsciiHeadersEncoder.SeparatorType var2, AsciiHeadersEncoder.NewlineType var3) {
      super();
      if (var1 == null) {
         throw new NullPointerException("buf");
      } else if (var2 == null) {
         throw new NullPointerException("separatorType");
      } else if (var3 == null) {
         throw new NullPointerException("newlineType");
      } else {
         this.buf = var1;
         this.separatorType = var2;
         this.newlineType = var3;
      }
   }

   public void encode(Entry<CharSequence, CharSequence> var1) {
      CharSequence var2 = (CharSequence)var1.getKey();
      CharSequence var3 = (CharSequence)var1.getValue();
      ByteBuf var4 = this.buf;
      int var5 = var2.length();
      int var6 = var3.length();
      int var7 = var5 + var6 + 4;
      int var8 = var4.writerIndex();
      var4.ensureWritable(var7);
      writeAscii(var4, var8, var2);
      var8 += var5;
      switch(this.separatorType) {
      case COLON:
         var4.setByte(var8++, 58);
         break;
      case COLON_SPACE:
         var4.setByte(var8++, 58);
         var4.setByte(var8++, 32);
         break;
      default:
         throw new Error();
      }

      writeAscii(var4, var8, var3);
      var8 += var6;
      switch(this.newlineType) {
      case LF:
         var4.setByte(var8++, 10);
         break;
      case CRLF:
         var4.setByte(var8++, 13);
         var4.setByte(var8++, 10);
         break;
      default:
         throw new Error();
      }

      var4.writerIndex(var8);
   }

   private static void writeAscii(ByteBuf var0, int var1, CharSequence var2) {
      if (var2 instanceof AsciiString) {
         ByteBufUtil.copy((AsciiString)var2, 0, var0, var1, var2.length());
      } else {
         var0.setCharSequence(var1, var2, CharsetUtil.US_ASCII);
      }

   }

   public static enum NewlineType {
      LF,
      CRLF;

      private NewlineType() {
      }
   }

   public static enum SeparatorType {
      COLON,
      COLON_SPACE;

      private SeparatorType() {
      }
   }
}
