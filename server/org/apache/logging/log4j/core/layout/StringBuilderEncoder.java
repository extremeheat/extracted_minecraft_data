package org.apache.logging.log4j.core.layout;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Objects;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.status.StatusLogger;

public class StringBuilderEncoder implements Encoder<StringBuilder> {
   private static final int DEFAULT_BYTE_BUFFER_SIZE = 8192;
   private final ThreadLocal<CharBuffer> charBufferThreadLocal;
   private final ThreadLocal<ByteBuffer> byteBufferThreadLocal;
   private final ThreadLocal<CharsetEncoder> charsetEncoderThreadLocal;
   private final Charset charset;
   private final int charBufferSize;
   private final int byteBufferSize;

   public StringBuilderEncoder(Charset var1) {
      this(var1, Constants.ENCODER_CHAR_BUFFER_SIZE, 8192);
   }

   public StringBuilderEncoder(Charset var1, int var2, int var3) {
      super();
      this.charBufferThreadLocal = new ThreadLocal();
      this.byteBufferThreadLocal = new ThreadLocal();
      this.charsetEncoderThreadLocal = new ThreadLocal();
      this.charBufferSize = var2;
      this.byteBufferSize = var3;
      this.charset = (Charset)Objects.requireNonNull(var1, "charset");
   }

   public void encode(StringBuilder var1, ByteBufferDestination var2) {
      ByteBuffer var3 = this.getByteBuffer();
      var3.clear();
      var3.limit(Math.min(var3.capacity(), var2.getByteBuffer().capacity()));
      CharsetEncoder var4 = this.getCharsetEncoder();
      int var5 = estimateBytes(var1.length(), var4.maxBytesPerChar());
      if (var3.remaining() < var5) {
         this.encodeSynchronized(this.getCharsetEncoder(), this.getCharBuffer(), var1, var2);
      } else {
         this.encodeWithThreadLocals(var4, this.getCharBuffer(), var3, var1, var2);
      }

   }

   private void encodeWithThreadLocals(CharsetEncoder var1, CharBuffer var2, ByteBuffer var3, StringBuilder var4, ByteBufferDestination var5) {
      try {
         TextEncoderHelper.encodeTextWithCopy(var1, var2, var3, var4, var5);
      } catch (Exception var7) {
         this.logEncodeTextException(var7, var4, var5);
         TextEncoderHelper.encodeTextFallBack(this.charset, var4, var5);
      }

   }

   private static int estimateBytes(int var0, float var1) {
      return (int)((double)var0 * (double)var1);
   }

   private void encodeSynchronized(CharsetEncoder var1, CharBuffer var2, StringBuilder var3, ByteBufferDestination var4) {
      synchronized(var4) {
         try {
            TextEncoderHelper.encodeText(var1, var2, var4.getByteBuffer(), var3, var4);
         } catch (Exception var8) {
            this.logEncodeTextException(var8, var3, var4);
            TextEncoderHelper.encodeTextFallBack(this.charset, var3, var4);
         }

      }
   }

   private CharsetEncoder getCharsetEncoder() {
      CharsetEncoder var1 = (CharsetEncoder)this.charsetEncoderThreadLocal.get();
      if (var1 == null) {
         var1 = this.charset.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
         this.charsetEncoderThreadLocal.set(var1);
      }

      return var1;
   }

   private CharBuffer getCharBuffer() {
      CharBuffer var1 = (CharBuffer)this.charBufferThreadLocal.get();
      if (var1 == null) {
         var1 = CharBuffer.wrap(new char[this.charBufferSize]);
         this.charBufferThreadLocal.set(var1);
      }

      return var1;
   }

   private ByteBuffer getByteBuffer() {
      ByteBuffer var1 = (ByteBuffer)this.byteBufferThreadLocal.get();
      if (var1 == null) {
         var1 = ByteBuffer.wrap(new byte[this.byteBufferSize]);
         this.byteBufferThreadLocal.set(var1);
      }

      return var1;
   }

   private void logEncodeTextException(Exception var1, StringBuilder var2, ByteBufferDestination var3) {
      StatusLogger.getLogger().error("Recovering from StringBuilderEncoder.encode('{}') error: {}", var2, var1, var1);
   }
}
