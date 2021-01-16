package org.apache.logging.log4j.core.layout;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public class TextEncoderHelper {
   private TextEncoderHelper() {
      super();
   }

   static void encodeTextFallBack(Charset var0, StringBuilder var1, ByteBufferDestination var2) {
      byte[] var3 = var1.toString().getBytes(var0);
      synchronized(var2) {
         ByteBuffer var5 = var2.getByteBuffer();
         int var6 = 0;

         do {
            int var7 = Math.min(var3.length - var6, var5.remaining());
            var5.put(var3, var6, var7);
            var6 += var7;
            if (var6 < var3.length) {
               var5 = var2.drain(var5);
            }
         } while(var6 < var3.length);

      }
   }

   static void encodeTextWithCopy(CharsetEncoder var0, CharBuffer var1, ByteBuffer var2, StringBuilder var3, ByteBufferDestination var4) {
      encodeText(var0, var1, var2, var3, var4);
      copyDataToDestination(var2, var4);
   }

   private static void copyDataToDestination(ByteBuffer var0, ByteBufferDestination var1) {
      synchronized(var1) {
         ByteBuffer var3 = var1.getByteBuffer();
         if (var3 != var0) {
            var0.flip();
            if (var0.remaining() > var3.remaining()) {
               var3 = var1.drain(var3);
            }

            var3.put(var0);
            var0.clear();
         }

      }
   }

   static void encodeText(CharsetEncoder var0, CharBuffer var1, ByteBuffer var2, StringBuilder var3, ByteBufferDestination var4) {
      var0.reset();
      ByteBuffer var5 = var2;
      int var6 = 0;
      int var7 = var3.length();
      boolean var8 = true;

      do {
         var1.clear();
         int var9 = copy(var3, var6, var1);
         var6 += var9;
         var7 -= var9;
         var8 = var7 <= 0;
         var1.flip();
         var5 = encode(var0, var1, var8, var4, var5);
      } while(!var8);

   }

   /** @deprecated */
   @Deprecated
   public static void encodeText(CharsetEncoder var0, CharBuffer var1, ByteBufferDestination var2) {
      synchronized(var2) {
         var0.reset();
         ByteBuffer var4 = var2.getByteBuffer();
         encode(var0, var1, true, var2, var4);
      }
   }

   private static ByteBuffer encode(CharsetEncoder var0, CharBuffer var1, boolean var2, ByteBufferDestination var3, ByteBuffer var4) {
      try {
         var4 = encodeAsMuchAsPossible(var0, var1, var2, var3, var4);
         if (var2) {
            var4 = flushRemainingBytes(var0, var3, var4);
         }

         return var4;
      } catch (CharacterCodingException var6) {
         throw new IllegalStateException(var6);
      }
   }

   private static ByteBuffer encodeAsMuchAsPossible(CharsetEncoder var0, CharBuffer var1, boolean var2, ByteBufferDestination var3, ByteBuffer var4) throws CharacterCodingException {
      CoderResult var5;
      do {
         var5 = var0.encode(var1, var4, var2);
         var4 = drainIfByteBufferFull(var3, var4, var5);
      } while(var5.isOverflow());

      if (!var5.isUnderflow()) {
         var5.throwException();
      }

      return var4;
   }

   private static ByteBuffer drainIfByteBufferFull(ByteBufferDestination var0, ByteBuffer var1, CoderResult var2) {
      if (var2.isOverflow()) {
         ByteBuffer var3 = var0.getByteBuffer();
         if (var3 != var1) {
            var1.flip();
            var3.put(var1);
            var1.clear();
         }

         var3 = var0.drain(var3);
         var1 = var3;
      }

      return var1;
   }

   private static ByteBuffer flushRemainingBytes(CharsetEncoder var0, ByteBufferDestination var1, ByteBuffer var2) throws CharacterCodingException {
      CoderResult var3;
      do {
         var3 = var0.flush(var2);
         var2 = drainIfByteBufferFull(var1, var2, var3);
      } while(var3.isOverflow());

      if (!var3.isUnderflow()) {
         var3.throwException();
      }

      return var2;
   }

   static int copy(StringBuilder var0, int var1, CharBuffer var2) {
      int var3 = Math.min(var0.length() - var1, var2.remaining());
      char[] var4 = var2.array();
      int var5 = var2.position();
      var0.getChars(var1, var1 + var3, var4, var5);
      var2.position(var5 + var3);
      return var3;
   }
}
