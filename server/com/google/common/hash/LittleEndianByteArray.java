package com.google.common.hash;

import com.google.common.primitives.Longs;
import java.lang.reflect.Field;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;

final class LittleEndianByteArray {
   private static final LittleEndianByteArray.LittleEndianBytes byteArray;

   static long load64(byte[] var0, int var1) {
      assert var0.length >= var1 + 8;

      return byteArray.getLongLittleEndian(var0, var1);
   }

   static long load64Safely(byte[] var0, int var1, int var2) {
      long var3 = 0L;
      int var5 = Math.min(var2, 8);

      for(int var6 = 0; var6 < var5; ++var6) {
         var3 |= ((long)var0[var1 + var6] & 255L) << var6 * 8;
      }

      return var3;
   }

   static void store64(byte[] var0, int var1, long var2) {
      assert var1 >= 0 && var1 + 8 <= var0.length;

      byteArray.putLongLittleEndian(var0, var1, var2);
   }

   static int load32(byte[] var0, int var1) {
      return var0[var1] & 255 | (var0[var1 + 1] & 255) << 8 | (var0[var1 + 2] & 255) << 16 | (var0[var1 + 3] & 255) << 24;
   }

   static boolean usingUnsafe() {
      return byteArray instanceof LittleEndianByteArray.UnsafeByteArray;
   }

   private LittleEndianByteArray() {
      super();
   }

   static {
      Object var0 = LittleEndianByteArray.JavaLittleEndianBytes.INSTANCE;

      try {
         String var1 = System.getProperty("os.arch");
         if ("amd64".equals(var1) || "aarch64".equals(var1)) {
            var0 = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN) ? LittleEndianByteArray.UnsafeByteArray.UNSAFE_LITTLE_ENDIAN : LittleEndianByteArray.UnsafeByteArray.UNSAFE_BIG_ENDIAN;
         }
      } catch (Throwable var2) {
      }

      byteArray = (LittleEndianByteArray.LittleEndianBytes)var0;
   }

   private static enum JavaLittleEndianBytes implements LittleEndianByteArray.LittleEndianBytes {
      INSTANCE {
         public long getLongLittleEndian(byte[] var1, int var2) {
            return Longs.fromBytes(var1[var2 + 7], var1[var2 + 6], var1[var2 + 5], var1[var2 + 4], var1[var2 + 3], var1[var2 + 2], var1[var2 + 1], var1[var2]);
         }

         public void putLongLittleEndian(byte[] var1, int var2, long var3) {
            long var5 = 255L;

            for(int var7 = 0; var7 < 8; ++var7) {
               var1[var2 + var7] = (byte)((int)((var3 & var5) >> var7 * 8));
               var5 <<= 8;
            }

         }
      };

      private JavaLittleEndianBytes() {
      }

      // $FF: synthetic method
      JavaLittleEndianBytes(Object var3) {
         this();
      }
   }

   private static enum UnsafeByteArray implements LittleEndianByteArray.LittleEndianBytes {
      UNSAFE_LITTLE_ENDIAN {
         public long getLongLittleEndian(byte[] var1, int var2) {
            return LittleEndianByteArray.UnsafeByteArray.theUnsafe.getLong(var1, (long)var2 + (long)LittleEndianByteArray.UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET);
         }

         public void putLongLittleEndian(byte[] var1, int var2, long var3) {
            LittleEndianByteArray.UnsafeByteArray.theUnsafe.putLong(var1, (long)var2 + (long)LittleEndianByteArray.UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET, var3);
         }
      },
      UNSAFE_BIG_ENDIAN {
         public long getLongLittleEndian(byte[] var1, int var2) {
            long var3 = LittleEndianByteArray.UnsafeByteArray.theUnsafe.getLong(var1, (long)var2 + (long)LittleEndianByteArray.UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET);
            return Long.reverseBytes(var3);
         }

         public void putLongLittleEndian(byte[] var1, int var2, long var3) {
            long var5 = Long.reverseBytes(var3);
            LittleEndianByteArray.UnsafeByteArray.theUnsafe.putLong(var1, (long)var2 + (long)LittleEndianByteArray.UnsafeByteArray.BYTE_ARRAY_BASE_OFFSET, var5);
         }
      };

      private static final Unsafe theUnsafe = getUnsafe();
      private static final int BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset(byte[].class);

      private UnsafeByteArray() {
      }

      private static Unsafe getUnsafe() {
         try {
            return Unsafe.getUnsafe();
         } catch (SecurityException var2) {
            try {
               return (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction<Unsafe>() {
                  public Unsafe run() throws Exception {
                     Class var1 = Unsafe.class;
                     Field[] var2 = var1.getDeclaredFields();
                     int var3 = var2.length;

                     for(int var4 = 0; var4 < var3; ++var4) {
                        Field var5 = var2[var4];
                        var5.setAccessible(true);
                        Object var6 = var5.get((Object)null);
                        if (var1.isInstance(var6)) {
                           return (Unsafe)var1.cast(var6);
                        }
                     }

                     throw new NoSuchFieldError("the Unsafe");
                  }
               });
            } catch (PrivilegedActionException var1) {
               throw new RuntimeException("Could not initialize intrinsics", var1.getCause());
            }
         }
      }

      // $FF: synthetic method
      UnsafeByteArray(Object var3) {
         this();
      }

      static {
         if (theUnsafe.arrayIndexScale(byte[].class) != 1) {
            throw new AssertionError();
         }
      }
   }

   private interface LittleEndianBytes {
      long getLongLittleEndian(byte[] var1, int var2);

      void putLongLittleEndian(byte[] var1, int var2, long var3);
   }
}
