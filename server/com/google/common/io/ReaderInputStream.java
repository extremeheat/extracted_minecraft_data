package com.google.common.io;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedBytes;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;

@GwtIncompatible
final class ReaderInputStream extends InputStream {
   private final Reader reader;
   private final CharsetEncoder encoder;
   private final byte[] singleByte;
   private CharBuffer charBuffer;
   private ByteBuffer byteBuffer;
   private boolean endOfInput;
   private boolean draining;
   private boolean doneFlushing;

   ReaderInputStream(Reader var1, Charset var2, int var3) {
      this(var1, var2.newEncoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE), var3);
   }

   ReaderInputStream(Reader var1, CharsetEncoder var2, int var3) {
      super();
      this.singleByte = new byte[1];
      this.reader = (Reader)Preconditions.checkNotNull(var1);
      this.encoder = (CharsetEncoder)Preconditions.checkNotNull(var2);
      Preconditions.checkArgument(var3 > 0, "bufferSize must be positive: %s", var3);
      var2.reset();
      this.charBuffer = CharBuffer.allocate(var3);
      this.charBuffer.flip();
      this.byteBuffer = ByteBuffer.allocate(var3);
   }

   public void close() throws IOException {
      this.reader.close();
   }

   public int read() throws IOException {
      return this.read(this.singleByte) == 1 ? UnsignedBytes.toInt(this.singleByte[0]) : -1;
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      Preconditions.checkPositionIndexes(var2, var2 + var3, var1.length);
      if (var3 == 0) {
         return 0;
      } else {
         int var4 = 0;
         boolean var5 = this.endOfInput;

         while(true) {
            if (this.draining) {
               var4 += this.drain(var1, var2 + var4, var3 - var4);
               if (var4 == var3 || this.doneFlushing) {
                  return var4 > 0 ? var4 : -1;
               }

               this.draining = false;
               this.byteBuffer.clear();
            }

            while(true) {
               CoderResult var6;
               if (this.doneFlushing) {
                  var6 = CoderResult.UNDERFLOW;
               } else if (var5) {
                  var6 = this.encoder.flush(this.byteBuffer);
               } else {
                  var6 = this.encoder.encode(this.charBuffer, this.byteBuffer, this.endOfInput);
               }

               if (var6.isOverflow()) {
                  this.startDraining(true);
                  break;
               }

               if (var6.isUnderflow()) {
                  if (var5) {
                     this.doneFlushing = true;
                     this.startDraining(false);
                     break;
                  }

                  if (this.endOfInput) {
                     var5 = true;
                  } else {
                     this.readMoreChars();
                  }
               } else if (var6.isError()) {
                  var6.throwException();
                  return 0;
               }
            }
         }
      }
   }

   private static CharBuffer grow(CharBuffer var0) {
      char[] var1 = Arrays.copyOf(var0.array(), var0.capacity() * 2);
      CharBuffer var2 = CharBuffer.wrap(var1);
      var2.position(var0.position());
      var2.limit(var0.limit());
      return var2;
   }

   private void readMoreChars() throws IOException {
      if (availableCapacity(this.charBuffer) == 0) {
         if (this.charBuffer.position() > 0) {
            this.charBuffer.compact().flip();
         } else {
            this.charBuffer = grow(this.charBuffer);
         }
      }

      int var1 = this.charBuffer.limit();
      int var2 = this.reader.read(this.charBuffer.array(), var1, availableCapacity(this.charBuffer));
      if (var2 == -1) {
         this.endOfInput = true;
      } else {
         this.charBuffer.limit(var1 + var2);
      }

   }

   private static int availableCapacity(Buffer var0) {
      return var0.capacity() - var0.limit();
   }

   private void startDraining(boolean var1) {
      this.byteBuffer.flip();
      if (var1 && this.byteBuffer.remaining() == 0) {
         this.byteBuffer = ByteBuffer.allocate(this.byteBuffer.capacity() * 2);
      } else {
         this.draining = true;
      }

   }

   private int drain(byte[] var1, int var2, int var3) {
      int var4 = Math.min(var3, this.byteBuffer.remaining());
      this.byteBuffer.get(var1, var2, var4);
      return var4;
   }
}
