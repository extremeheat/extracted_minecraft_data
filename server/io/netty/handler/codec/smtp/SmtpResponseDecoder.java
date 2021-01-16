package io.netty.handler.codec.smtp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SmtpResponseDecoder extends LineBasedFrameDecoder {
   private List<CharSequence> details;

   public SmtpResponseDecoder(int var1) {
      super(var1);
   }

   protected SmtpResponse decode(ChannelHandlerContext var1, ByteBuf var2) throws Exception {
      ByteBuf var3 = (ByteBuf)super.decode(var1, var2);
      if (var3 == null) {
         return null;
      } else {
         try {
            int var4 = var3.readableBytes();
            int var5 = var3.readerIndex();
            if (var4 < 3) {
               throw newDecoderException(var2, var5, var4);
            }

            int var6 = parseCode(var3);
            byte var7 = var3.readByte();
            String var8 = var3.isReadable() ? var3.toString(CharsetUtil.US_ASCII) : null;
            Object var9 = this.details;
            switch(var7) {
            case 32:
               this.details = null;
               if (var9 != null) {
                  if (var8 != null) {
                     ((List)var9).add(var8);
                  }
               } else if (var8 == null) {
                  var9 = Collections.emptyList();
               } else {
                  var9 = Collections.singletonList(var8);
               }

               DefaultSmtpResponse var10 = new DefaultSmtpResponse(var6, (List)var9);
               return var10;
            case 45:
               if (var8 != null) {
                  if (var9 == null) {
                     this.details = (List)(var9 = new ArrayList(4));
                  }

                  ((List)var9).add(var8);
               }
               break;
            default:
               throw newDecoderException(var2, var5, var4);
            }
         } finally {
            var3.release();
         }

         return null;
      }
   }

   private static DecoderException newDecoderException(ByteBuf var0, int var1, int var2) {
      return new DecoderException("Received invalid line: '" + var0.toString(var1, var2, CharsetUtil.US_ASCII) + '\'');
   }

   private static int parseCode(ByteBuf var0) {
      int var1 = parseNumber(var0.readByte()) * 100;
      int var2 = parseNumber(var0.readByte()) * 10;
      int var3 = parseNumber(var0.readByte());
      return var1 + var2 + var3;
   }

   private static int parseNumber(byte var0) {
      return Character.digit((char)var0, 10);
   }
}
