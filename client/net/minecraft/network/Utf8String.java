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

   public static String read(final ByteBuf input, final int maxLength) {
      int maxEncodedLength = ByteBufUtil.utf8MaxBytes(maxLength);
      int bufferLength = VarInt.read(input);
      if (bufferLength > maxEncodedLength) {
         throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + bufferLength + " > " + maxEncodedLength + ")");
      } else if (bufferLength < 0) {
         throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
      } else {
         int availableBytes = input.readableBytes();
         if (bufferLength > availableBytes) {
            throw new DecoderException("Not enough bytes in buffer, expected " + bufferLength + ", but got " + availableBytes);
         } else {
            String result = input.toString(input.readerIndex(), bufferLength, StandardCharsets.UTF_8);
            input.readerIndex(input.readerIndex() + bufferLength);
            if (result.length() > maxLength) {
               int var10002 = result.length();
               throw new DecoderException("The received string length is longer than maximum allowed (" + var10002 + " > " + maxLength + ")");
            } else {
               return result;
            }
         }
      }
   }

   public static void write(final ByteBuf output, final CharSequence value, final int maxLength) {
      if (value.length() > maxLength) {
         int var10002 = value.length();
         throw new EncoderException("String too big (was " + var10002 + " characters, max " + maxLength + ")");
      } else {
         int maxEncodedValueLength = ByteBufUtil.utf8MaxBytes(value);
         ByteBuf tmp = output.alloc().buffer(maxEncodedValueLength);

         try {
            int bytesWritten = ByteBufUtil.writeUtf8(tmp, value);
            int maxAllowedEncodedLength = ByteBufUtil.utf8MaxBytes(maxLength);
            if (bytesWritten > maxAllowedEncodedLength) {
               throw new EncoderException("String too big (was " + bytesWritten + " bytes encoded, max " + maxAllowedEncodedLength + ")");
            }

            VarInt.write(output, bytesWritten);
            output.writeBytes(tmp);
         } finally {
            tmp.release();
         }

      }
   }
}
