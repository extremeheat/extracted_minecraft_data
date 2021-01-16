package io.netty.buffer;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ReadOnlyBufferException;

final class UnsafeByteBufUtil {
   private static final boolean UNALIGNED = PlatformDependent.isUnaligned();
   private static final byte ZERO = 0;

   static byte getByte(long var0) {
      return PlatformDependent.getByte(var0);
   }

   static short getShort(long var0) {
      if (UNALIGNED) {
         short var2 = PlatformDependent.getShort(var0);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Short.reverseBytes(var2);
      } else {
         return (short)(PlatformDependent.getByte(var0) << 8 | PlatformDependent.getByte(var0 + 1L) & 255);
      }
   }

   static short getShortLE(long var0) {
      if (UNALIGNED) {
         short var2 = PlatformDependent.getShort(var0);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(var2) : var2;
      } else {
         return (short)(PlatformDependent.getByte(var0) & 255 | PlatformDependent.getByte(var0 + 1L) << 8);
      }
   }

   static int getUnsignedMedium(long var0) {
      return UNALIGNED ? (PlatformDependent.getByte(var0) & 255) << 16 | (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? PlatformDependent.getShort(var0 + 1L) : Short.reverseBytes(PlatformDependent.getShort(var0 + 1L))) & '\uffff' : (PlatformDependent.getByte(var0) & 255) << 16 | (PlatformDependent.getByte(var0 + 1L) & 255) << 8 | PlatformDependent.getByte(var0 + 2L) & 255;
   }

   static int getUnsignedMediumLE(long var0) {
      return UNALIGNED ? PlatformDependent.getByte(var0) & 255 | ((PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(PlatformDependent.getShort(var0 + 1L)) : PlatformDependent.getShort(var0 + 1L)) & '\uffff') << 8 : PlatformDependent.getByte(var0) & 255 | (PlatformDependent.getByte(var0 + 1L) & 255) << 8 | (PlatformDependent.getByte(var0 + 2L) & 255) << 16;
   }

   static int getInt(long var0) {
      if (UNALIGNED) {
         int var2 = PlatformDependent.getInt(var0);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Integer.reverseBytes(var2);
      } else {
         return PlatformDependent.getByte(var0) << 24 | (PlatformDependent.getByte(var0 + 1L) & 255) << 16 | (PlatformDependent.getByte(var0 + 2L) & 255) << 8 | PlatformDependent.getByte(var0 + 3L) & 255;
      }
   }

   static int getIntLE(long var0) {
      if (UNALIGNED) {
         int var2 = PlatformDependent.getInt(var0);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(var2) : var2;
      } else {
         return PlatformDependent.getByte(var0) & 255 | (PlatformDependent.getByte(var0 + 1L) & 255) << 8 | (PlatformDependent.getByte(var0 + 2L) & 255) << 16 | PlatformDependent.getByte(var0 + 3L) << 24;
      }
   }

   static long getLong(long var0) {
      if (UNALIGNED) {
         long var2 = PlatformDependent.getLong(var0);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Long.reverseBytes(var2);
      } else {
         return (long)PlatformDependent.getByte(var0) << 56 | ((long)PlatformDependent.getByte(var0 + 1L) & 255L) << 48 | ((long)PlatformDependent.getByte(var0 + 2L) & 255L) << 40 | ((long)PlatformDependent.getByte(var0 + 3L) & 255L) << 32 | ((long)PlatformDependent.getByte(var0 + 4L) & 255L) << 24 | ((long)PlatformDependent.getByte(var0 + 5L) & 255L) << 16 | ((long)PlatformDependent.getByte(var0 + 6L) & 255L) << 8 | (long)PlatformDependent.getByte(var0 + 7L) & 255L;
      }
   }

   static long getLongLE(long var0) {
      if (UNALIGNED) {
         long var2 = PlatformDependent.getLong(var0);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(var2) : var2;
      } else {
         return (long)PlatformDependent.getByte(var0) & 255L | ((long)PlatformDependent.getByte(var0 + 1L) & 255L) << 8 | ((long)PlatformDependent.getByte(var0 + 2L) & 255L) << 16 | ((long)PlatformDependent.getByte(var0 + 3L) & 255L) << 24 | ((long)PlatformDependent.getByte(var0 + 4L) & 255L) << 32 | ((long)PlatformDependent.getByte(var0 + 5L) & 255L) << 40 | ((long)PlatformDependent.getByte(var0 + 6L) & 255L) << 48 | (long)PlatformDependent.getByte(var0 + 7L) << 56;
      }
   }

   static void setByte(long var0, int var2) {
      PlatformDependent.putByte(var0, (byte)var2);
   }

   static void setShort(long var0, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putShort(var0, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)var2 : Short.reverseBytes((short)var2));
      } else {
         PlatformDependent.putByte(var0, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0 + 1L, (byte)var2);
      }

   }

   static void setShortLE(long var0, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putShort(var0, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)var2) : (short)var2);
      } else {
         PlatformDependent.putByte(var0, (byte)var2);
         PlatformDependent.putByte(var0 + 1L, (byte)(var2 >>> 8));
      }

   }

   static void setMedium(long var0, int var2) {
      PlatformDependent.putByte(var0, (byte)(var2 >>> 16));
      if (UNALIGNED) {
         PlatformDependent.putShort(var0 + 1L, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)var2 : Short.reverseBytes((short)var2));
      } else {
         PlatformDependent.putByte(var0 + 1L, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0 + 2L, (byte)var2);
      }

   }

   static void setMediumLE(long var0, int var2) {
      PlatformDependent.putByte(var0, (byte)var2);
      if (UNALIGNED) {
         PlatformDependent.putShort(var0 + 1L, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)(var2 >>> 8)) : (short)(var2 >>> 8));
      } else {
         PlatformDependent.putByte(var0 + 1L, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0 + 2L, (byte)(var2 >>> 16));
      }

   }

   static void setInt(long var0, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putInt(var0, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Integer.reverseBytes(var2));
      } else {
         PlatformDependent.putByte(var0, (byte)(var2 >>> 24));
         PlatformDependent.putByte(var0 + 1L, (byte)(var2 >>> 16));
         PlatformDependent.putByte(var0 + 2L, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0 + 3L, (byte)var2);
      }

   }

   static void setIntLE(long var0, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putInt(var0, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(var2) : var2);
      } else {
         PlatformDependent.putByte(var0, (byte)var2);
         PlatformDependent.putByte(var0 + 1L, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0 + 2L, (byte)(var2 >>> 16));
         PlatformDependent.putByte(var0 + 3L, (byte)(var2 >>> 24));
      }

   }

   static void setLong(long var0, long var2) {
      if (UNALIGNED) {
         PlatformDependent.putLong(var0, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Long.reverseBytes(var2));
      } else {
         PlatformDependent.putByte(var0, (byte)((int)(var2 >>> 56)));
         PlatformDependent.putByte(var0 + 1L, (byte)((int)(var2 >>> 48)));
         PlatformDependent.putByte(var0 + 2L, (byte)((int)(var2 >>> 40)));
         PlatformDependent.putByte(var0 + 3L, (byte)((int)(var2 >>> 32)));
         PlatformDependent.putByte(var0 + 4L, (byte)((int)(var2 >>> 24)));
         PlatformDependent.putByte(var0 + 5L, (byte)((int)(var2 >>> 16)));
         PlatformDependent.putByte(var0 + 6L, (byte)((int)(var2 >>> 8)));
         PlatformDependent.putByte(var0 + 7L, (byte)((int)var2));
      }

   }

   static void setLongLE(long var0, long var2) {
      if (UNALIGNED) {
         PlatformDependent.putLong(var0, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(var2) : var2);
      } else {
         PlatformDependent.putByte(var0, (byte)((int)var2));
         PlatformDependent.putByte(var0 + 1L, (byte)((int)(var2 >>> 8)));
         PlatformDependent.putByte(var0 + 2L, (byte)((int)(var2 >>> 16)));
         PlatformDependent.putByte(var0 + 3L, (byte)((int)(var2 >>> 24)));
         PlatformDependent.putByte(var0 + 4L, (byte)((int)(var2 >>> 32)));
         PlatformDependent.putByte(var0 + 5L, (byte)((int)(var2 >>> 40)));
         PlatformDependent.putByte(var0 + 6L, (byte)((int)(var2 >>> 48)));
         PlatformDependent.putByte(var0 + 7L, (byte)((int)(var2 >>> 56)));
      }

   }

   static byte getByte(byte[] var0, int var1) {
      return PlatformDependent.getByte(var0, var1);
   }

   static short getShort(byte[] var0, int var1) {
      if (UNALIGNED) {
         short var2 = PlatformDependent.getShort(var0, var1);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Short.reverseBytes(var2);
      } else {
         return (short)(PlatformDependent.getByte(var0, var1) << 8 | PlatformDependent.getByte(var0, var1 + 1) & 255);
      }
   }

   static short getShortLE(byte[] var0, int var1) {
      if (UNALIGNED) {
         short var2 = PlatformDependent.getShort(var0, var1);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(var2) : var2;
      } else {
         return (short)(PlatformDependent.getByte(var0, var1) & 255 | PlatformDependent.getByte(var0, var1 + 1) << 8);
      }
   }

   static int getUnsignedMedium(byte[] var0, int var1) {
      return UNALIGNED ? (PlatformDependent.getByte(var0, var1) & 255) << 16 | (PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? PlatformDependent.getShort(var0, var1 + 1) : Short.reverseBytes(PlatformDependent.getShort(var0, var1 + 1))) & '\uffff' : (PlatformDependent.getByte(var0, var1) & 255) << 16 | (PlatformDependent.getByte(var0, var1 + 1) & 255) << 8 | PlatformDependent.getByte(var0, var1 + 2) & 255;
   }

   static int getUnsignedMediumLE(byte[] var0, int var1) {
      return UNALIGNED ? PlatformDependent.getByte(var0, var1) & 255 | ((PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes(PlatformDependent.getShort(var0, var1 + 1)) : PlatformDependent.getShort(var0, var1 + 1)) & '\uffff') << 8 : PlatformDependent.getByte(var0, var1) & 255 | (PlatformDependent.getByte(var0, var1 + 1) & 255) << 8 | (PlatformDependent.getByte(var0, var1 + 2) & 255) << 16;
   }

   static int getInt(byte[] var0, int var1) {
      if (UNALIGNED) {
         int var2 = PlatformDependent.getInt(var0, var1);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Integer.reverseBytes(var2);
      } else {
         return PlatformDependent.getByte(var0, var1) << 24 | (PlatformDependent.getByte(var0, var1 + 1) & 255) << 16 | (PlatformDependent.getByte(var0, var1 + 2) & 255) << 8 | PlatformDependent.getByte(var0, var1 + 3) & 255;
      }
   }

   static int getIntLE(byte[] var0, int var1) {
      if (UNALIGNED) {
         int var2 = PlatformDependent.getInt(var0, var1);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(var2) : var2;
      } else {
         return PlatformDependent.getByte(var0, var1) & 255 | (PlatformDependent.getByte(var0, var1 + 1) & 255) << 8 | (PlatformDependent.getByte(var0, var1 + 2) & 255) << 16 | PlatformDependent.getByte(var0, var1 + 3) << 24;
      }
   }

   static long getLong(byte[] var0, int var1) {
      if (UNALIGNED) {
         long var2 = PlatformDependent.getLong(var0, var1);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Long.reverseBytes(var2);
      } else {
         return (long)PlatformDependent.getByte(var0, var1) << 56 | ((long)PlatformDependent.getByte(var0, var1 + 1) & 255L) << 48 | ((long)PlatformDependent.getByte(var0, var1 + 2) & 255L) << 40 | ((long)PlatformDependent.getByte(var0, var1 + 3) & 255L) << 32 | ((long)PlatformDependent.getByte(var0, var1 + 4) & 255L) << 24 | ((long)PlatformDependent.getByte(var0, var1 + 5) & 255L) << 16 | ((long)PlatformDependent.getByte(var0, var1 + 6) & 255L) << 8 | (long)PlatformDependent.getByte(var0, var1 + 7) & 255L;
      }
   }

   static long getLongLE(byte[] var0, int var1) {
      if (UNALIGNED) {
         long var2 = PlatformDependent.getLong(var0, var1);
         return PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(var2) : var2;
      } else {
         return (long)PlatformDependent.getByte(var0, var1) & 255L | ((long)PlatformDependent.getByte(var0, var1 + 1) & 255L) << 8 | ((long)PlatformDependent.getByte(var0, var1 + 2) & 255L) << 16 | ((long)PlatformDependent.getByte(var0, var1 + 3) & 255L) << 24 | ((long)PlatformDependent.getByte(var0, var1 + 4) & 255L) << 32 | ((long)PlatformDependent.getByte(var0, var1 + 5) & 255L) << 40 | ((long)PlatformDependent.getByte(var0, var1 + 6) & 255L) << 48 | (long)PlatformDependent.getByte(var0, var1 + 7) << 56;
      }
   }

   static void setByte(byte[] var0, int var1, int var2) {
      PlatformDependent.putByte(var0, var1, (byte)var2);
   }

   static void setShort(byte[] var0, int var1, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putShort(var0, var1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)var2 : Short.reverseBytes((short)var2));
      } else {
         PlatformDependent.putByte(var0, var1, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0, var1 + 1, (byte)var2);
      }

   }

   static void setShortLE(byte[] var0, int var1, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putShort(var0, var1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)var2) : (short)var2);
      } else {
         PlatformDependent.putByte(var0, var1, (byte)var2);
         PlatformDependent.putByte(var0, var1 + 1, (byte)(var2 >>> 8));
      }

   }

   static void setMedium(byte[] var0, int var1, int var2) {
      PlatformDependent.putByte(var0, var1, (byte)(var2 >>> 16));
      if (UNALIGNED) {
         PlatformDependent.putShort(var0, var1 + 1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? (short)var2 : Short.reverseBytes((short)var2));
      } else {
         PlatformDependent.putByte(var0, var1 + 1, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0, var1 + 2, (byte)var2);
      }

   }

   static void setMediumLE(byte[] var0, int var1, int var2) {
      PlatformDependent.putByte(var0, var1, (byte)var2);
      if (UNALIGNED) {
         PlatformDependent.putShort(var0, var1 + 1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Short.reverseBytes((short)(var2 >>> 8)) : (short)(var2 >>> 8));
      } else {
         PlatformDependent.putByte(var0, var1 + 1, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0, var1 + 2, (byte)(var2 >>> 16));
      }

   }

   static void setInt(byte[] var0, int var1, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putInt(var0, var1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Integer.reverseBytes(var2));
      } else {
         PlatformDependent.putByte(var0, var1, (byte)(var2 >>> 24));
         PlatformDependent.putByte(var0, var1 + 1, (byte)(var2 >>> 16));
         PlatformDependent.putByte(var0, var1 + 2, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0, var1 + 3, (byte)var2);
      }

   }

   static void setIntLE(byte[] var0, int var1, int var2) {
      if (UNALIGNED) {
         PlatformDependent.putInt(var0, var1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Integer.reverseBytes(var2) : var2);
      } else {
         PlatformDependent.putByte(var0, var1, (byte)var2);
         PlatformDependent.putByte(var0, var1 + 1, (byte)(var2 >>> 8));
         PlatformDependent.putByte(var0, var1 + 2, (byte)(var2 >>> 16));
         PlatformDependent.putByte(var0, var1 + 3, (byte)(var2 >>> 24));
      }

   }

   static void setLong(byte[] var0, int var1, long var2) {
      if (UNALIGNED) {
         PlatformDependent.putLong(var0, var1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? var2 : Long.reverseBytes(var2));
      } else {
         PlatformDependent.putByte(var0, var1, (byte)((int)(var2 >>> 56)));
         PlatformDependent.putByte(var0, var1 + 1, (byte)((int)(var2 >>> 48)));
         PlatformDependent.putByte(var0, var1 + 2, (byte)((int)(var2 >>> 40)));
         PlatformDependent.putByte(var0, var1 + 3, (byte)((int)(var2 >>> 32)));
         PlatformDependent.putByte(var0, var1 + 4, (byte)((int)(var2 >>> 24)));
         PlatformDependent.putByte(var0, var1 + 5, (byte)((int)(var2 >>> 16)));
         PlatformDependent.putByte(var0, var1 + 6, (byte)((int)(var2 >>> 8)));
         PlatformDependent.putByte(var0, var1 + 7, (byte)((int)var2));
      }

   }

   static void setLongLE(byte[] var0, int var1, long var2) {
      if (UNALIGNED) {
         PlatformDependent.putLong(var0, var1, PlatformDependent.BIG_ENDIAN_NATIVE_ORDER ? Long.reverseBytes(var2) : var2);
      } else {
         PlatformDependent.putByte(var0, var1, (byte)((int)var2));
         PlatformDependent.putByte(var0, var1 + 1, (byte)((int)(var2 >>> 8)));
         PlatformDependent.putByte(var0, var1 + 2, (byte)((int)(var2 >>> 16)));
         PlatformDependent.putByte(var0, var1 + 3, (byte)((int)(var2 >>> 24)));
         PlatformDependent.putByte(var0, var1 + 4, (byte)((int)(var2 >>> 32)));
         PlatformDependent.putByte(var0, var1 + 5, (byte)((int)(var2 >>> 40)));
         PlatformDependent.putByte(var0, var1 + 6, (byte)((int)(var2 >>> 48)));
         PlatformDependent.putByte(var0, var1 + 7, (byte)((int)(var2 >>> 56)));
      }

   }

   static void setZero(byte[] var0, int var1, int var2) {
      if (var2 != 0) {
         PlatformDependent.setMemory(var0, var1, (long)var2, (byte)0);
      }
   }

   static ByteBuf copy(AbstractByteBuf var0, long var1, int var3, int var4) {
      var0.checkIndex(var3, var4);
      ByteBuf var5 = var0.alloc().directBuffer(var4, var0.maxCapacity());
      if (var4 != 0) {
         if (var5.hasMemoryAddress()) {
            PlatformDependent.copyMemory(var1, var5.memoryAddress(), (long)var4);
            var5.setIndex(0, var4);
         } else {
            var5.writeBytes((ByteBuf)var0, var3, var4);
         }
      }

      return var5;
   }

   static int setBytes(AbstractByteBuf var0, long var1, int var3, InputStream var4, int var5) throws IOException {
      var0.checkIndex(var3, var5);
      ByteBuf var6 = var0.alloc().heapBuffer(var5);

      int var10;
      try {
         byte[] var7 = var6.array();
         int var8 = var6.arrayOffset();
         int var9 = var4.read(var7, var8, var5);
         if (var9 > 0) {
            PlatformDependent.copyMemory(var7, var8, var1, (long)var9);
         }

         var10 = var9;
      } finally {
         var6.release();
      }

      return var10;
   }

   static void getBytes(AbstractByteBuf var0, long var1, int var3, ByteBuf var4, int var5, int var6) {
      var0.checkIndex(var3, var6);
      ObjectUtil.checkNotNull(var4, "dst");
      if (MathUtil.isOutOfBounds(var5, var6, var4.capacity())) {
         throw new IndexOutOfBoundsException("dstIndex: " + var5);
      } else {
         if (var4.hasMemoryAddress()) {
            PlatformDependent.copyMemory(var1, var4.memoryAddress() + (long)var5, (long)var6);
         } else if (var4.hasArray()) {
            PlatformDependent.copyMemory(var1, var4.array(), var4.arrayOffset() + var5, (long)var6);
         } else {
            var4.setBytes(var5, (ByteBuf)var0, var3, var6);
         }

      }
   }

   static void getBytes(AbstractByteBuf var0, long var1, int var3, byte[] var4, int var5, int var6) {
      var0.checkIndex(var3, var6);
      ObjectUtil.checkNotNull(var4, "dst");
      if (MathUtil.isOutOfBounds(var5, var6, var4.length)) {
         throw new IndexOutOfBoundsException("dstIndex: " + var5);
      } else {
         if (var6 != 0) {
            PlatformDependent.copyMemory(var1, var4, var5, (long)var6);
         }

      }
   }

   static void getBytes(AbstractByteBuf var0, long var1, int var3, ByteBuffer var4) {
      var0.checkIndex(var3, var4.remaining());
      if (var4.remaining() != 0) {
         if (var4.isDirect()) {
            if (var4.isReadOnly()) {
               throw new ReadOnlyBufferException();
            }

            long var5 = PlatformDependent.directBufferAddress(var4);
            PlatformDependent.copyMemory(var1, var5 + (long)var4.position(), (long)var4.remaining());
            var4.position(var4.position() + var4.remaining());
         } else if (var4.hasArray()) {
            PlatformDependent.copyMemory(var1, var4.array(), var4.arrayOffset() + var4.position(), (long)var4.remaining());
            var4.position(var4.position() + var4.remaining());
         } else {
            var4.put(var0.nioBuffer());
         }

      }
   }

   static void setBytes(AbstractByteBuf var0, long var1, int var3, ByteBuf var4, int var5, int var6) {
      var0.checkIndex(var3, var6);
      ObjectUtil.checkNotNull(var4, "src");
      if (MathUtil.isOutOfBounds(var5, var6, var4.capacity())) {
         throw new IndexOutOfBoundsException("srcIndex: " + var5);
      } else {
         if (var6 != 0) {
            if (var4.hasMemoryAddress()) {
               PlatformDependent.copyMemory(var4.memoryAddress() + (long)var5, var1, (long)var6);
            } else if (var4.hasArray()) {
               PlatformDependent.copyMemory(var4.array(), var4.arrayOffset() + var5, var1, (long)var6);
            } else {
               var4.getBytes(var5, (ByteBuf)var0, var3, var6);
            }
         }

      }
   }

   static void setBytes(AbstractByteBuf var0, long var1, int var3, byte[] var4, int var5, int var6) {
      var0.checkIndex(var3, var6);
      if (var6 != 0) {
         PlatformDependent.copyMemory(var4, var5, var1, (long)var6);
      }

   }

   static void setBytes(AbstractByteBuf var0, long var1, int var3, ByteBuffer var4) {
      int var5 = var4.remaining();
      if (var5 != 0) {
         if (var4.isDirect()) {
            var0.checkIndex(var3, var5);
            long var6 = PlatformDependent.directBufferAddress(var4);
            PlatformDependent.copyMemory(var6 + (long)var4.position(), var1, (long)var5);
            var4.position(var4.position() + var5);
         } else if (var4.hasArray()) {
            var0.checkIndex(var3, var5);
            PlatformDependent.copyMemory(var4.array(), var4.arrayOffset() + var4.position(), var1, (long)var5);
            var4.position(var4.position() + var5);
         } else if (var5 < 8) {
            setSingleBytes(var0, var1, var3, var4, var5);
         } else {
            assert var0.nioBufferCount() == 1;

            ByteBuffer var8 = var0.internalNioBuffer(var3, var5);
            var8.put(var4);
         }

      }
   }

   private static void setSingleBytes(AbstractByteBuf var0, long var1, int var3, ByteBuffer var4, int var5) {
      var0.checkIndex(var3, var5);
      int var6 = var4.position();
      int var7 = var4.limit();
      long var8 = var1;

      for(int var10 = var6; var10 < var7; ++var10) {
         byte var11 = var4.get(var10);
         PlatformDependent.putByte(var8, var11);
         ++var8;
      }

      var4.position(var7);
   }

   static void getBytes(AbstractByteBuf var0, long var1, int var3, OutputStream var4, int var5) throws IOException {
      var0.checkIndex(var3, var5);
      if (var5 != 0) {
         int var6 = Math.min(var5, 8192);
         if (var0.alloc().isDirectBufferPooled()) {
            ByteBuf var7 = var0.alloc().heapBuffer(var6);

            try {
               byte[] var8 = var7.array();
               int var9 = var7.arrayOffset();
               getBytes(var1, var8, var9, var6, var4, var5);
            } finally {
               var7.release();
            }
         } else {
            getBytes(var1, new byte[var6], 0, var6, var4, var5);
         }
      }

   }

   private static void getBytes(long var0, byte[] var2, int var3, int var4, OutputStream var5, int var6) throws IOException {
      do {
         int var7 = Math.min(var4, var6);
         PlatformDependent.copyMemory(var0, var2, var3, (long)var7);
         var5.write(var2, var3, var7);
         var6 -= var7;
         var0 += (long)var7;
      } while(var6 > 0);

   }

   static void setZero(long var0, int var2) {
      if (var2 != 0) {
         PlatformDependent.setMemory(var0, (long)var2, (byte)0);
      }
   }

   static UnpooledUnsafeDirectByteBuf newUnsafeDirectByteBuf(ByteBufAllocator var0, int var1, int var2) {
      return (UnpooledUnsafeDirectByteBuf)(PlatformDependent.useDirectBufferNoCleaner() ? new UnpooledUnsafeNoCleanerDirectByteBuf(var0, var1, var2) : new UnpooledUnsafeDirectByteBuf(var0, var1, var2));
   }

   private UnsafeByteBufUtil() {
      super();
   }
}
