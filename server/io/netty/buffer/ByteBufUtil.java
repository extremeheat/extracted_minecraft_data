package io.netty.buffer;

import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import io.netty.util.Recycler;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.Arrays;
import java.util.Locale;

public final class ByteBufUtil {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(ByteBufUtil.class);
   private static final FastThreadLocal<CharBuffer> CHAR_BUFFERS = new FastThreadLocal<CharBuffer>() {
      protected CharBuffer initialValue() throws Exception {
         return CharBuffer.allocate(1024);
      }
   };
   private static final byte WRITE_UTF_UNKNOWN = 63;
   private static final int MAX_CHAR_BUFFER_SIZE;
   private static final int THREAD_LOCAL_BUFFER_SIZE;
   private static final int MAX_BYTES_PER_CHAR_UTF8;
   static final int WRITE_CHUNK_SIZE = 8192;
   static final ByteBufAllocator DEFAULT_ALLOCATOR;
   private static final ByteProcessor FIND_NON_ASCII;

   public static String hexDump(ByteBuf var0) {
      return hexDump(var0, var0.readerIndex(), var0.readableBytes());
   }

   public static String hexDump(ByteBuf var0, int var1, int var2) {
      return ByteBufUtil.HexUtil.hexDump(var0, var1, var2);
   }

   public static String hexDump(byte[] var0) {
      return hexDump((byte[])var0, 0, var0.length);
   }

   public static String hexDump(byte[] var0, int var1, int var2) {
      return ByteBufUtil.HexUtil.hexDump(var0, var1, var2);
   }

   public static byte decodeHexByte(CharSequence var0, int var1) {
      return StringUtil.decodeHexByte(var0, var1);
   }

   public static byte[] decodeHexDump(CharSequence var0) {
      return StringUtil.decodeHexDump(var0, 0, var0.length());
   }

   public static byte[] decodeHexDump(CharSequence var0, int var1, int var2) {
      return StringUtil.decodeHexDump(var0, var1, var2);
   }

   public static boolean ensureWritableSuccess(int var0) {
      return var0 == 0 || var0 == 2;
   }

   public static int hashCode(ByteBuf var0) {
      int var1 = var0.readableBytes();
      int var2 = var1 >>> 2;
      int var3 = var1 & 3;
      int var4 = 1;
      int var5 = var0.readerIndex();
      int var6;
      if (var0.order() == ByteOrder.BIG_ENDIAN) {
         for(var6 = var2; var6 > 0; --var6) {
            var4 = 31 * var4 + var0.getInt(var5);
            var5 += 4;
         }
      } else {
         for(var6 = var2; var6 > 0; --var6) {
            var4 = 31 * var4 + swapInt(var0.getInt(var5));
            var5 += 4;
         }
      }

      for(var6 = var3; var6 > 0; --var6) {
         var4 = 31 * var4 + var0.getByte(var5++);
      }

      if (var4 == 0) {
         var4 = 1;
      }

      return var4;
   }

   public static int indexOf(ByteBuf var0, ByteBuf var1) {
      int var2 = var1.readableBytes() - var0.readableBytes() + 1;

      for(int var3 = 0; var3 < var2; ++var3) {
         if (equals(var0, var0.readerIndex(), var1, var1.readerIndex() + var3, var0.readableBytes())) {
            return var1.readerIndex() + var3;
         }
      }

      return -1;
   }

   public static boolean equals(ByteBuf var0, int var1, ByteBuf var2, int var3, int var4) {
      if (var1 >= 0 && var3 >= 0 && var4 >= 0) {
         if (var0.writerIndex() - var4 >= var1 && var2.writerIndex() - var4 >= var3) {
            int var5 = var4 >>> 3;
            int var6 = var4 & 7;
            int var7;
            if (var0.order() == var2.order()) {
               for(var7 = var5; var7 > 0; --var7) {
                  if (var0.getLong(var1) != var2.getLong(var3)) {
                     return false;
                  }

                  var1 += 8;
                  var3 += 8;
               }
            } else {
               for(var7 = var5; var7 > 0; --var7) {
                  if (var0.getLong(var1) != swapLong(var2.getLong(var3))) {
                     return false;
                  }

                  var1 += 8;
                  var3 += 8;
               }
            }

            for(var7 = var6; var7 > 0; --var7) {
               if (var0.getByte(var1) != var2.getByte(var3)) {
                  return false;
               }

               ++var1;
               ++var3;
            }

            return true;
         } else {
            return false;
         }
      } else {
         throw new IllegalArgumentException("All indexes and lengths must be non-negative");
      }
   }

   public static boolean equals(ByteBuf var0, ByteBuf var1) {
      int var2 = var0.readableBytes();
      return var2 != var1.readableBytes() ? false : equals(var0, var0.readerIndex(), var1, var1.readerIndex(), var2);
   }

   public static int compare(ByteBuf var0, ByteBuf var1) {
      int var2 = var0.readableBytes();
      int var3 = var1.readableBytes();
      int var4 = Math.min(var2, var3);
      int var5 = var4 >>> 2;
      int var6 = var4 & 3;
      int var7 = var0.readerIndex();
      int var8 = var1.readerIndex();
      if (var5 > 0) {
         boolean var9 = var0.order() == ByteOrder.BIG_ENDIAN;
         int var12 = var5 << 2;
         long var10;
         if (var0.order() == var1.order()) {
            var10 = var9 ? compareUintBigEndian(var0, var1, var7, var8, var12) : compareUintLittleEndian(var0, var1, var7, var8, var12);
         } else {
            var10 = var9 ? compareUintBigEndianA(var0, var1, var7, var8, var12) : compareUintBigEndianB(var0, var1, var7, var8, var12);
         }

         if (var10 != 0L) {
            return (int)Math.min(2147483647L, Math.max(-2147483648L, var10));
         }

         var7 += var12;
         var8 += var12;
      }

      for(int var13 = var7 + var6; var7 < var13; ++var8) {
         int var14 = var0.getUnsignedByte(var7) - var1.getUnsignedByte(var8);
         if (var14 != 0) {
            return var14;
         }

         ++var7;
      }

      return var2 - var3;
   }

   private static long compareUintBigEndian(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      for(int var5 = var2 + var4; var2 < var5; var3 += 4) {
         long var6 = var0.getUnsignedInt(var2) - var1.getUnsignedInt(var3);
         if (var6 != 0L) {
            return var6;
         }

         var2 += 4;
      }

      return 0L;
   }

   private static long compareUintLittleEndian(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      for(int var5 = var2 + var4; var2 < var5; var3 += 4) {
         long var6 = var0.getUnsignedIntLE(var2) - var1.getUnsignedIntLE(var3);
         if (var6 != 0L) {
            return var6;
         }

         var2 += 4;
      }

      return 0L;
   }

   private static long compareUintBigEndianA(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      for(int var5 = var2 + var4; var2 < var5; var3 += 4) {
         long var6 = var0.getUnsignedInt(var2) - var1.getUnsignedIntLE(var3);
         if (var6 != 0L) {
            return var6;
         }

         var2 += 4;
      }

      return 0L;
   }

   private static long compareUintBigEndianB(ByteBuf var0, ByteBuf var1, int var2, int var3, int var4) {
      for(int var5 = var2 + var4; var2 < var5; var3 += 4) {
         long var6 = var0.getUnsignedIntLE(var2) - var1.getUnsignedInt(var3);
         if (var6 != 0L) {
            return var6;
         }

         var2 += 4;
      }

      return 0L;
   }

   public static int indexOf(ByteBuf var0, int var1, int var2, byte var3) {
      return var1 <= var2 ? firstIndexOf(var0, var1, var2, var3) : lastIndexOf(var0, var1, var2, var3);
   }

   public static short swapShort(short var0) {
      return Short.reverseBytes(var0);
   }

   public static int swapMedium(int var0) {
      int var1 = var0 << 16 & 16711680 | var0 & '\uff00' | var0 >>> 16 & 255;
      if ((var1 & 8388608) != 0) {
         var1 |= -16777216;
      }

      return var1;
   }

   public static int swapInt(int var0) {
      return Integer.reverseBytes(var0);
   }

   public static long swapLong(long var0) {
      return Long.reverseBytes(var0);
   }

   public static ByteBuf writeShortBE(ByteBuf var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.writeShort(var1) : var0.writeShortLE(var1);
   }

   public static ByteBuf setShortBE(ByteBuf var0, int var1, int var2) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.setShort(var1, var2) : var0.setShortLE(var1, var2);
   }

   public static ByteBuf writeMediumBE(ByteBuf var0, int var1) {
      return var0.order() == ByteOrder.BIG_ENDIAN ? var0.writeMedium(var1) : var0.writeMediumLE(var1);
   }

   public static ByteBuf readBytes(ByteBufAllocator var0, ByteBuf var1, int var2) {
      boolean var3 = true;
      ByteBuf var4 = var0.buffer(var2);

      ByteBuf var5;
      try {
         var1.readBytes(var4);
         var3 = false;
         var5 = var4;
      } finally {
         if (var3) {
            var4.release();
         }

      }

      return var5;
   }

   private static int firstIndexOf(ByteBuf var0, int var1, int var2, byte var3) {
      var1 = Math.max(var1, 0);
      return var1 < var2 && var0.capacity() != 0 ? var0.forEachByte(var1, var2 - var1, new ByteProcessor.IndexOfProcessor(var3)) : -1;
   }

   private static int lastIndexOf(ByteBuf var0, int var1, int var2, byte var3) {
      var1 = Math.min(var1, var0.capacity());
      return var1 >= 0 && var0.capacity() != 0 ? var0.forEachByteDesc(var2, var1 - var2, new ByteProcessor.IndexOfProcessor(var3)) : -1;
   }

   public static ByteBuf writeUtf8(ByteBufAllocator var0, CharSequence var1) {
      ByteBuf var2 = var0.buffer(utf8MaxBytes(var1));
      writeUtf8(var2, var1);
      return var2;
   }

   public static int writeUtf8(ByteBuf var0, CharSequence var1) {
      return reserveAndWriteUtf8(var0, var1, utf8MaxBytes(var1));
   }

   public static int reserveAndWriteUtf8(ByteBuf var0, CharSequence var1, int var2) {
      while(!(var0 instanceof AbstractByteBuf)) {
         if (!(var0 instanceof WrappedByteBuf)) {
            byte[] var3 = var1.toString().getBytes(CharsetUtil.UTF_8);
            var0.writeBytes(var3);
            return var3.length;
         }

         var0 = var0.unwrap();
      }

      AbstractByteBuf var5 = (AbstractByteBuf)var0;
      var5.ensureWritable0(var2);
      int var4 = writeUtf8(var5, var5.writerIndex, var1, var1.length());
      var5.writerIndex += var4;
      return var4;
   }

   static int writeUtf8(AbstractByteBuf var0, int var1, CharSequence var2, int var3) {
      int var4 = var1;

      for(int var5 = 0; var5 < var3; ++var5) {
         char var6 = var2.charAt(var5);
         if (var6 < 128) {
            var0._setByte(var1++, (byte)var6);
         } else if (var6 < 2048) {
            var0._setByte(var1++, (byte)(192 | var6 >> 6));
            var0._setByte(var1++, (byte)(128 | var6 & 63));
         } else if (StringUtil.isSurrogate(var6)) {
            if (!Character.isHighSurrogate(var6)) {
               var0._setByte(var1++, 63);
            } else {
               char var7;
               try {
                  ++var5;
                  var7 = var2.charAt(var5);
               } catch (IndexOutOfBoundsException var9) {
                  var0._setByte(var1++, 63);
                  break;
               }

               if (!Character.isLowSurrogate(var7)) {
                  var0._setByte(var1++, 63);
                  var0._setByte(var1++, Character.isHighSurrogate(var7) ? 63 : var7);
               } else {
                  int var8 = Character.toCodePoint(var6, var7);
                  var0._setByte(var1++, (byte)(240 | var8 >> 18));
                  var0._setByte(var1++, (byte)(128 | var8 >> 12 & 63));
                  var0._setByte(var1++, (byte)(128 | var8 >> 6 & 63));
                  var0._setByte(var1++, (byte)(128 | var8 & 63));
               }
            }
         } else {
            var0._setByte(var1++, (byte)(224 | var6 >> 12));
            var0._setByte(var1++, (byte)(128 | var6 >> 6 & 63));
            var0._setByte(var1++, (byte)(128 | var6 & 63));
         }
      }

      return var1 - var4;
   }

   public static int utf8MaxBytes(int var0) {
      return var0 * MAX_BYTES_PER_CHAR_UTF8;
   }

   public static int utf8MaxBytes(CharSequence var0) {
      return utf8MaxBytes(var0.length());
   }

   public static int utf8Bytes(CharSequence var0) {
      if (var0 instanceof AsciiString) {
         return var0.length();
      } else {
         int var1 = var0.length();

         int var2;
         for(var2 = 0; var2 < var1 && var0.charAt(var2) < 128; ++var2) {
         }

         return var2 < var1 ? var2 + utf8Bytes(var0, var2, var1) : var2;
      }
   }

   private static int utf8Bytes(CharSequence var0, int var1, int var2) {
      int var3 = 0;

      for(int var4 = var1; var4 < var2; ++var4) {
         char var5 = var0.charAt(var4);
         if (var5 < 2048) {
            var3 += (127 - var5 >>> 31) + 1;
         } else if (StringUtil.isSurrogate(var5)) {
            if (!Character.isHighSurrogate(var5)) {
               ++var3;
            } else {
               char var6;
               try {
                  ++var4;
                  var6 = var0.charAt(var4);
               } catch (IndexOutOfBoundsException var8) {
                  ++var3;
                  break;
               }

               if (!Character.isLowSurrogate(var6)) {
                  var3 += 2;
               } else {
                  var3 += 4;
               }
            }
         } else {
            var3 += 3;
         }
      }

      return var3;
   }

   public static ByteBuf writeAscii(ByteBufAllocator var0, CharSequence var1) {
      ByteBuf var2 = var0.buffer(var1.length());
      writeAscii(var2, var1);
      return var2;
   }

   public static int writeAscii(ByteBuf var0, CharSequence var1) {
      int var2 = var1.length();
      if (var1 instanceof AsciiString) {
         AsciiString var6 = (AsciiString)var1;
         var0.writeBytes(var6.array(), var6.arrayOffset(), var2);
         return var2;
      } else {
         while(!(var0 instanceof AbstractByteBuf)) {
            if (!(var0 instanceof WrappedByteBuf)) {
               byte[] var3 = var1.toString().getBytes(CharsetUtil.US_ASCII);
               var0.writeBytes(var3);
               return var3.length;
            }

            var0 = var0.unwrap();
         }

         AbstractByteBuf var5 = (AbstractByteBuf)var0;
         var5.ensureWritable0(var2);
         int var4 = writeAscii(var5, var5.writerIndex, var1, var2);
         var5.writerIndex += var4;
         return var4;
      }
   }

   static int writeAscii(AbstractByteBuf var0, int var1, CharSequence var2, int var3) {
      for(int var4 = 0; var4 < var3; ++var4) {
         var0._setByte(var1++, AsciiString.c2b(var2.charAt(var4)));
      }

      return var3;
   }

   public static ByteBuf encodeString(ByteBufAllocator var0, CharBuffer var1, Charset var2) {
      return encodeString0(var0, false, var1, var2, 0);
   }

   public static ByteBuf encodeString(ByteBufAllocator var0, CharBuffer var1, Charset var2, int var3) {
      return encodeString0(var0, false, var1, var2, var3);
   }

   static ByteBuf encodeString0(ByteBufAllocator var0, boolean var1, CharBuffer var2, Charset var3, int var4) {
      CharsetEncoder var5 = CharsetUtil.encoder(var3);
      int var6 = (int)((double)var2.remaining() * (double)var5.maxBytesPerChar()) + var4;
      boolean var7 = true;
      ByteBuf var8;
      if (var1) {
         var8 = var0.heapBuffer(var6);
      } else {
         var8 = var0.buffer(var6);
      }

      ByteBuf var12;
      try {
         ByteBuffer var9 = var8.internalNioBuffer(var8.readerIndex(), var6);
         int var10 = var9.position();
         CoderResult var11 = var5.encode(var2, var9, true);
         if (!var11.isUnderflow()) {
            var11.throwException();
         }

         var11 = var5.flush(var9);
         if (!var11.isUnderflow()) {
            var11.throwException();
         }

         var8.writerIndex(var8.writerIndex() + var9.position() - var10);
         var7 = false;
         var12 = var8;
      } catch (CharacterCodingException var16) {
         throw new IllegalStateException(var16);
      } finally {
         if (var7) {
            var8.release();
         }

      }

      return var12;
   }

   static String decodeString(ByteBuf var0, int var1, int var2, Charset var3) {
      if (var2 == 0) {
         return "";
      } else {
         CharsetDecoder var4 = CharsetUtil.decoder(var3);
         int var5 = (int)((double)var2 * (double)var4.maxCharsPerByte());
         CharBuffer var6 = (CharBuffer)CHAR_BUFFERS.get();
         if (var6.length() < var5) {
            var6 = CharBuffer.allocate(var5);
            if (var5 <= MAX_CHAR_BUFFER_SIZE) {
               CHAR_BUFFERS.set(var6);
            }
         } else {
            var6.clear();
         }

         if (var0.nioBufferCount() == 1) {
            decodeString(var4, var0.nioBuffer(var1, var2), var6);
         } else {
            ByteBuf var7 = var0.alloc().heapBuffer(var2);

            try {
               var7.writeBytes(var0, var1, var2);
               decodeString(var4, var7.internalNioBuffer(var7.readerIndex(), var2), var6);
            } finally {
               var7.release();
            }
         }

         return var6.flip().toString();
      }
   }

   private static void decodeString(CharsetDecoder var0, ByteBuffer var1, CharBuffer var2) {
      try {
         CoderResult var3 = var0.decode(var1, var2, true);
         if (!var3.isUnderflow()) {
            var3.throwException();
         }

         var3 = var0.flush(var2);
         if (!var3.isUnderflow()) {
            var3.throwException();
         }

      } catch (CharacterCodingException var4) {
         throw new IllegalStateException(var4);
      }
   }

   public static ByteBuf threadLocalDirectBuffer() {
      if (THREAD_LOCAL_BUFFER_SIZE <= 0) {
         return null;
      } else {
         return (ByteBuf)(PlatformDependent.hasUnsafe() ? ByteBufUtil.ThreadLocalUnsafeDirectByteBuf.newInstance() : ByteBufUtil.ThreadLocalDirectByteBuf.newInstance());
      }
   }

   public static byte[] getBytes(ByteBuf var0) {
      return getBytes(var0, var0.readerIndex(), var0.readableBytes());
   }

   public static byte[] getBytes(ByteBuf var0, int var1, int var2) {
      return getBytes(var0, var1, var2, true);
   }

   public static byte[] getBytes(ByteBuf var0, int var1, int var2, boolean var3) {
      if (MathUtil.isOutOfBounds(var1, var2, var0.capacity())) {
         throw new IndexOutOfBoundsException("expected: 0 <= start(" + var1 + ") <= start + length(" + var2 + ") <= buf.capacity(" + var0.capacity() + ')');
      } else if (var0.hasArray()) {
         if (!var3 && var1 == 0 && var2 == var0.capacity()) {
            return var0.array();
         } else {
            int var5 = var0.arrayOffset() + var1;
            return Arrays.copyOfRange(var0.array(), var5, var5 + var2);
         }
      } else {
         byte[] var4 = new byte[var2];
         var0.getBytes(var1, var4);
         return var4;
      }
   }

   public static void copy(AsciiString var0, ByteBuf var1) {
      copy(var0, 0, var1, var0.length());
   }

   public static void copy(AsciiString var0, int var1, ByteBuf var2, int var3, int var4) {
      if (MathUtil.isOutOfBounds(var1, var4, var0.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + var1 + ") <= srcIdx + length(" + var4 + ") <= srcLen(" + var0.length() + ')');
      } else {
         ((ByteBuf)ObjectUtil.checkNotNull(var2, "dst")).setBytes(var3, var0.array(), var1 + var0.arrayOffset(), var4);
      }
   }

   public static void copy(AsciiString var0, int var1, ByteBuf var2, int var3) {
      if (MathUtil.isOutOfBounds(var1, var3, var0.length())) {
         throw new IndexOutOfBoundsException("expected: 0 <= srcIdx(" + var1 + ") <= srcIdx + length(" + var3 + ") <= srcLen(" + var0.length() + ')');
      } else {
         ((ByteBuf)ObjectUtil.checkNotNull(var2, "dst")).writeBytes(var0.array(), var1 + var0.arrayOffset(), var3);
      }
   }

   public static String prettyHexDump(ByteBuf var0) {
      return prettyHexDump(var0, var0.readerIndex(), var0.readableBytes());
   }

   public static String prettyHexDump(ByteBuf var0, int var1, int var2) {
      return ByteBufUtil.HexUtil.prettyHexDump(var0, var1, var2);
   }

   public static void appendPrettyHexDump(StringBuilder var0, ByteBuf var1) {
      appendPrettyHexDump(var0, var1, var1.readerIndex(), var1.readableBytes());
   }

   public static void appendPrettyHexDump(StringBuilder var0, ByteBuf var1, int var2, int var3) {
      ByteBufUtil.HexUtil.appendPrettyHexDump(var0, var1, var2, var3);
   }

   public static boolean isText(ByteBuf var0, Charset var1) {
      return isText(var0, var0.readerIndex(), var0.readableBytes(), var1);
   }

   public static boolean isText(ByteBuf var0, int var1, int var2, Charset var3) {
      ObjectUtil.checkNotNull(var0, "buf");
      ObjectUtil.checkNotNull(var3, "charset");
      int var4 = var0.readerIndex() + var0.readableBytes();
      if (var1 >= 0 && var2 >= 0 && var1 <= var4 - var2) {
         if (var3.equals(CharsetUtil.UTF_8)) {
            return isUtf8(var0, var1, var2);
         } else if (var3.equals(CharsetUtil.US_ASCII)) {
            return isAscii(var0, var1, var2);
         } else {
            CharsetDecoder var5 = CharsetUtil.decoder(var3, CodingErrorAction.REPORT, CodingErrorAction.REPORT);

            try {
               if (var0.nioBufferCount() == 1) {
                  var5.decode(var0.nioBuffer(var1, var2));
               } else {
                  ByteBuf var6 = var0.alloc().heapBuffer(var2);

                  try {
                     var6.writeBytes(var0, var1, var2);
                     var5.decode(var6.internalNioBuffer(var6.readerIndex(), var2));
                  } finally {
                     var6.release();
                  }
               }

               return true;
            } catch (CharacterCodingException var11) {
               return false;
            }
         }
      } else {
         throw new IndexOutOfBoundsException("index: " + var1 + " length: " + var2);
      }
   }

   private static boolean isAscii(ByteBuf var0, int var1, int var2) {
      return var0.forEachByte(var1, var2, FIND_NON_ASCII) == -1;
   }

   private static boolean isUtf8(ByteBuf var0, int var1, int var2) {
      int var3 = var1 + var2;

      while(true) {
         while(true) {
            byte var4;
            do {
               if (var1 >= var3) {
                  return true;
               }

               var4 = var0.getByte(var1++);
            } while((var4 & 128) == 0);

            byte var5;
            if ((var4 & 224) != 192) {
               byte var6;
               if ((var4 & 240) == 224) {
                  if (var1 > var3 - 2) {
                     return false;
                  }

                  var5 = var0.getByte(var1++);
                  var6 = var0.getByte(var1++);
                  if ((var5 & 192) != 128 || (var6 & 192) != 128) {
                     return false;
                  }

                  if ((var4 & 15) == 0 && (var5 & 255) < 160) {
                     return false;
                  }

                  if ((var4 & 15) == 13 && (var5 & 255) > 159) {
                     return false;
                  }
               } else {
                  if ((var4 & 248) != 240) {
                     return false;
                  }

                  if (var1 > var3 - 3) {
                     return false;
                  }

                  var5 = var0.getByte(var1++);
                  var6 = var0.getByte(var1++);
                  byte var7 = var0.getByte(var1++);
                  if ((var5 & 192) == 128 && (var6 & 192) == 128 && (var7 & 192) == 128) {
                     if ((var4 & 255) <= 244 && ((var4 & 255) != 240 || (var5 & 255) >= 144) && ((var4 & 255) != 244 || (var5 & 255) <= 143)) {
                        continue;
                     }

                     return false;
                  }

                  return false;
               }
            } else {
               if (var1 >= var3) {
                  return false;
               }

               var5 = var0.getByte(var1++);
               if ((var5 & 192) != 128) {
                  return false;
               }

               if ((var4 & 255) < 194) {
                  return false;
               }
            }
         }
      }
   }

   static void readBytes(ByteBufAllocator var0, ByteBuffer var1, int var2, int var3, OutputStream var4) throws IOException {
      if (var1.hasArray()) {
         var4.write(var1.array(), var2 + var1.arrayOffset(), var3);
      } else {
         int var5 = Math.min(var3, 8192);
         var1.clear().position(var2);
         if (var0.isDirectBufferPooled()) {
            ByteBuf var6 = var0.heapBuffer(var5);

            try {
               byte[] var7 = var6.array();
               int var8 = var6.arrayOffset();
               getBytes(var1, var7, var8, var5, var4, var3);
            } finally {
               var6.release();
            }
         } else {
            getBytes(var1, new byte[var5], 0, var5, var4, var3);
         }
      }

   }

   private static void getBytes(ByteBuffer var0, byte[] var1, int var2, int var3, OutputStream var4, int var5) throws IOException {
      do {
         int var6 = Math.min(var3, var5);
         var0.get(var1, var2, var6);
         var4.write(var1, var2, var6);
         var5 -= var6;
      } while(var5 > 0);

   }

   private ByteBufUtil() {
      super();
   }

   static {
      MAX_BYTES_PER_CHAR_UTF8 = (int)CharsetUtil.encoder(CharsetUtil.UTF_8).maxBytesPerChar();
      String var0 = SystemPropertyUtil.get("io.netty.allocator.type", PlatformDependent.isAndroid() ? "unpooled" : "pooled");
      var0 = var0.toLowerCase(Locale.US).trim();
      Object var1;
      if ("unpooled".equals(var0)) {
         var1 = UnpooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", (Object)var0);
      } else if ("pooled".equals(var0)) {
         var1 = PooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: {}", (Object)var0);
      } else {
         var1 = PooledByteBufAllocator.DEFAULT;
         logger.debug("-Dio.netty.allocator.type: pooled (unknown: {})", (Object)var0);
      }

      DEFAULT_ALLOCATOR = (ByteBufAllocator)var1;
      THREAD_LOCAL_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.threadLocalDirectBufferSize", 0);
      logger.debug("-Dio.netty.threadLocalDirectBufferSize: {}", (Object)THREAD_LOCAL_BUFFER_SIZE);
      MAX_CHAR_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.maxThreadLocalCharBufferSize", 16384);
      logger.debug("-Dio.netty.maxThreadLocalCharBufferSize: {}", (Object)MAX_CHAR_BUFFER_SIZE);
      FIND_NON_ASCII = new ByteProcessor() {
         public boolean process(byte var1) {
            return var1 >= 0;
         }
      };
   }

   static final class ThreadLocalDirectByteBuf extends UnpooledDirectByteBuf {
      private static final Recycler<ByteBufUtil.ThreadLocalDirectByteBuf> RECYCLER = new Recycler<ByteBufUtil.ThreadLocalDirectByteBuf>() {
         protected ByteBufUtil.ThreadLocalDirectByteBuf newObject(Recycler.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> var1) {
            return new ByteBufUtil.ThreadLocalDirectByteBuf(var1);
         }
      };
      private final Recycler.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> handle;

      static ByteBufUtil.ThreadLocalDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalDirectByteBuf var0 = (ByteBufUtil.ThreadLocalDirectByteBuf)RECYCLER.get();
         var0.setRefCnt(1);
         return var0;
      }

      private ThreadLocalDirectByteBuf(Recycler.Handle<ByteBufUtil.ThreadLocalDirectByteBuf> var1) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, 2147483647);
         this.handle = var1;
      }

      protected void deallocate() {
         if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            this.handle.recycle(this);
         }

      }

      // $FF: synthetic method
      ThreadLocalDirectByteBuf(Recycler.Handle var1, Object var2) {
         this(var1);
      }
   }

   static final class ThreadLocalUnsafeDirectByteBuf extends UnpooledUnsafeDirectByteBuf {
      private static final Recycler<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> RECYCLER = new Recycler<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf>() {
         protected ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newObject(Recycler.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> var1) {
            return new ByteBufUtil.ThreadLocalUnsafeDirectByteBuf(var1);
         }
      };
      private final Recycler.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> handle;

      static ByteBufUtil.ThreadLocalUnsafeDirectByteBuf newInstance() {
         ByteBufUtil.ThreadLocalUnsafeDirectByteBuf var0 = (ByteBufUtil.ThreadLocalUnsafeDirectByteBuf)RECYCLER.get();
         var0.setRefCnt(1);
         return var0;
      }

      private ThreadLocalUnsafeDirectByteBuf(Recycler.Handle<ByteBufUtil.ThreadLocalUnsafeDirectByteBuf> var1) {
         super(UnpooledByteBufAllocator.DEFAULT, 256, 2147483647);
         this.handle = var1;
      }

      protected void deallocate() {
         if (this.capacity() > ByteBufUtil.THREAD_LOCAL_BUFFER_SIZE) {
            super.deallocate();
         } else {
            this.clear();
            this.handle.recycle(this);
         }

      }

      // $FF: synthetic method
      ThreadLocalUnsafeDirectByteBuf(Recycler.Handle var1, Object var2) {
         this(var1);
      }
   }

   private static final class HexUtil {
      private static final char[] BYTE2CHAR = new char[256];
      private static final char[] HEXDUMP_TABLE = new char[1024];
      private static final String[] HEXPADDING = new String[16];
      private static final String[] HEXDUMP_ROWPREFIXES = new String[4096];
      private static final String[] BYTE2HEX = new String[256];
      private static final String[] BYTEPADDING = new String[16];

      private HexUtil() {
         super();
      }

      private static String hexDump(ByteBuf var0, int var1, int var2) {
         if (var2 < 0) {
            throw new IllegalArgumentException("length: " + var2);
         } else if (var2 == 0) {
            return "";
         } else {
            int var3 = var1 + var2;
            char[] var4 = new char[var2 << 1];
            int var5 = var1;

            for(int var6 = 0; var5 < var3; var6 += 2) {
               System.arraycopy(HEXDUMP_TABLE, var0.getUnsignedByte(var5) << 1, var4, var6, 2);
               ++var5;
            }

            return new String(var4);
         }
      }

      private static String hexDump(byte[] var0, int var1, int var2) {
         if (var2 < 0) {
            throw new IllegalArgumentException("length: " + var2);
         } else if (var2 == 0) {
            return "";
         } else {
            int var3 = var1 + var2;
            char[] var4 = new char[var2 << 1];
            int var5 = var1;

            for(int var6 = 0; var5 < var3; var6 += 2) {
               System.arraycopy(HEXDUMP_TABLE, (var0[var5] & 255) << 1, var4, var6, 2);
               ++var5;
            }

            return new String(var4);
         }
      }

      private static String prettyHexDump(ByteBuf var0, int var1, int var2) {
         if (var2 == 0) {
            return "";
         } else {
            int var3 = var2 / 16 + (var2 % 15 == 0 ? 0 : 1) + 4;
            StringBuilder var4 = new StringBuilder(var3 * 80);
            appendPrettyHexDump(var4, var0, var1, var2);
            return var4.toString();
         }
      }

      private static void appendPrettyHexDump(StringBuilder var0, ByteBuf var1, int var2, int var3) {
         if (MathUtil.isOutOfBounds(var2, var3, var1.capacity())) {
            throw new IndexOutOfBoundsException("expected: 0 <= offset(" + var2 + ") <= offset + length(" + var3 + ") <= buf.capacity(" + var1.capacity() + ')');
         } else if (var3 != 0) {
            var0.append("         +-------------------------------------------------+" + StringUtil.NEWLINE + "         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |" + StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
            int var4 = var2;
            int var5 = var3 >>> 4;
            int var6 = var3 & 15;

            int var7;
            int var8;
            int var9;
            for(var7 = 0; var7 < var5; ++var7) {
               var8 = (var7 << 4) + var4;
               appendHexDumpRowPrefix(var0, var7, var8);
               var9 = var8 + 16;

               int var10;
               for(var10 = var8; var10 < var9; ++var10) {
                  var0.append(BYTE2HEX[var1.getUnsignedByte(var10)]);
               }

               var0.append(" |");

               for(var10 = var8; var10 < var9; ++var10) {
                  var0.append(BYTE2CHAR[var1.getUnsignedByte(var10)]);
               }

               var0.append('|');
            }

            if (var6 != 0) {
               var7 = (var5 << 4) + var4;
               appendHexDumpRowPrefix(var0, var5, var7);
               var8 = var7 + var6;

               for(var9 = var7; var9 < var8; ++var9) {
                  var0.append(BYTE2HEX[var1.getUnsignedByte(var9)]);
               }

               var0.append(HEXPADDING[var6]);
               var0.append(" |");

               for(var9 = var7; var9 < var8; ++var9) {
                  var0.append(BYTE2CHAR[var1.getUnsignedByte(var9)]);
               }

               var0.append(BYTEPADDING[var6]);
               var0.append('|');
            }

            var0.append(StringUtil.NEWLINE + "+--------+-------------------------------------------------+----------------+");
         }
      }

      private static void appendHexDumpRowPrefix(StringBuilder var0, int var1, int var2) {
         if (var1 < HEXDUMP_ROWPREFIXES.length) {
            var0.append(HEXDUMP_ROWPREFIXES[var1]);
         } else {
            var0.append(StringUtil.NEWLINE);
            var0.append(Long.toHexString((long)var2 & 4294967295L | 4294967296L));
            var0.setCharAt(var0.length() - 9, '|');
            var0.append('|');
         }

      }

      static {
         char[] var0 = "0123456789abcdef".toCharArray();

         int var1;
         for(var1 = 0; var1 < 256; ++var1) {
            HEXDUMP_TABLE[var1 << 1] = var0[var1 >>> 4 & 15];
            HEXDUMP_TABLE[(var1 << 1) + 1] = var0[var1 & 15];
         }

         int var2;
         StringBuilder var3;
         int var4;
         for(var1 = 0; var1 < HEXPADDING.length; ++var1) {
            var2 = HEXPADDING.length - var1;
            var3 = new StringBuilder(var2 * 3);

            for(var4 = 0; var4 < var2; ++var4) {
               var3.append("   ");
            }

            HEXPADDING[var1] = var3.toString();
         }

         for(var1 = 0; var1 < HEXDUMP_ROWPREFIXES.length; ++var1) {
            StringBuilder var5 = new StringBuilder(12);
            var5.append(StringUtil.NEWLINE);
            var5.append(Long.toHexString((long)(var1 << 4) & 4294967295L | 4294967296L));
            var5.setCharAt(var5.length() - 9, '|');
            var5.append('|');
            HEXDUMP_ROWPREFIXES[var1] = var5.toString();
         }

         for(var1 = 0; var1 < BYTE2HEX.length; ++var1) {
            BYTE2HEX[var1] = ' ' + StringUtil.byteToHexStringPadded(var1);
         }

         for(var1 = 0; var1 < BYTEPADDING.length; ++var1) {
            var2 = BYTEPADDING.length - var1;
            var3 = new StringBuilder(var2);

            for(var4 = 0; var4 < var2; ++var4) {
               var3.append(' ');
            }

            BYTEPADDING[var1] = var3.toString();
         }

         for(var1 = 0; var1 < BYTE2CHAR.length; ++var1) {
            if (var1 > 31 && var1 < 127) {
               BYTE2CHAR[var1] = (char)var1;
            } else {
               BYTE2CHAR[var1] = '.';
            }
         }

      }
   }
}
