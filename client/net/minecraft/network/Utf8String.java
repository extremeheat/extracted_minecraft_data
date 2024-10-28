package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.nio.charset.StandardCharsets;

public class Utf8String {
   public Utf8String() {
      super();
   }

   public static String read(ByteBuf var0, int var1) {
      int var2 = ByteBufUtil.utf8MaxBytes(var1);
      int var3 = VarInt.read(var0);
      if (var3 > var2) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + var3 + " > " + var2 + ")");
      } else if (var3 < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         int var4 = var0.readableBytes();
         if (var3 > var4) {
            throw new DecoderException("Not enough bytes in buffer, expected " + var3 + ", but got " + var4);
         } else {
            String var5 = var0.toString(var0.readerIndex(), var3, StandardCharsets.UTF_8);
            var0.readerIndex(var0.readerIndex() + var3);
            if (var5.length() > var1) {
               int var10002 = var5.length();
               throw new DecoderException("The received string length is longer than maximum allowed (" + var10002 + " > " + var1 + ")");
            } else {
               return var5;
            }
         }
      }
   }

   public static void write(ByteBuf var0, CharSequence var1, int var2) {
      if (var1.length() > var2) {
         int var10002 = var1.length();
         throw new EncoderException("String too big (was " + var10002 + " characters, max " + var2 + ")");
      } else {
         int var3 = ByteBufUtil.utf8MaxBytes(var1);
         ByteBuf var4 = var0.alloc().buffer(var3);

         try {
            int var5 = ByteBufUtil.writeUtf8(var4, var1);
            int var6 = ByteBufUtil.utf8MaxBytes(var2);
            if (var5 > var6) {
               throw new EncoderException("String too big (was " + var5 + " bytes encoded, max " + var6 + ")");
            }

            VarInt.write(var0, var5);
            var0.writeBytes(var4);
         } finally {
            var4.release();
         }

      }
   }
}
