package org.apache.logging.log4j.core.layout;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.util.Objects;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.status.StatusLogger;

public class LockingStringBuilderEncoder implements Encoder<StringBuilder> {
   private final Charset charset;
   private final CharsetEncoder charsetEncoder;
   private final CharBuffer cachedCharBuffer;

   public LockingStringBuilderEncoder(Charset var1) {
      this(var1, Constants.ENCODER_CHAR_BUFFER_SIZE);
   }

   public LockingStringBuilderEncoder(Charset var1, int var2) {
      super();
      this.charset = (Charset)Objects.requireNonNull(var1, "charset");
      this.charsetEncoder = var1.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
      this.cachedCharBuffer = CharBuffer.wrap(new char[var2]);
   }

   private CharBuffer getCharBuffer() {
      return this.cachedCharBuffer;
   }

   public void encode(StringBuilder var1, ByteBufferDestination var2) {
      synchronized(var2) {
         try {
            TextEncoderHelper.encodeText(this.charsetEncoder, this.cachedCharBuffer, var2.getByteBuffer(), var1, var2);
         } catch (Exception var6) {
            this.logEncodeTextException(var6, var1, var2);
            TextEncoderHelper.encodeTextFallBack(this.charset, var1, var2);
         }

      }
   }

   private void logEncodeTextException(Exception var1, StringBuilder var2, ByteBufferDestination var3) {
      StatusLogger.getLogger().error("Recovering from LockingStringBuilderEncoder.encode('{}') error", var2, var1);
   }
}
