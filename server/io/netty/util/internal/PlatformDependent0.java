package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

final class PlatformDependent0 {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent0.class);
   private static final long ADDRESS_FIELD_OFFSET;
   private static final long BYTE_ARRAY_BASE_OFFSET;
   private static final Constructor<?> DIRECT_BUFFER_CONSTRUCTOR;
   private static final Throwable EXPLICIT_NO_UNSAFE_CAUSE = explicitNoUnsafeCause0();
   private static final Method ALLOCATE_ARRAY_METHOD;
   private static final int JAVA_VERSION = javaVersion0();
   private static final boolean IS_ANDROID = isAndroid0();
   private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE;
   private static final Object INTERNAL_UNSAFE;
   private static final boolean IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE = explicitTryReflectionSetAccessible0();
   static final Unsafe UNSAFE;
   static final int HASH_CODE_ASCII_SEED = -1028477387;
   static final int HASH_CODE_C1 = -862048943;
   static final int HASH_CODE_C2 = 461845907;
   private static final long UNSAFE_COPY_THRESHOLD = 1048576L;
   private static final boolean UNALIGNED;

   static boolean isExplicitNoUnsafe() {
      return EXPLICIT_NO_UNSAFE_CAUSE == null;
   }

   private static Throwable explicitNoUnsafeCause0() {
      boolean var0 = SystemPropertyUtil.getBoolean("io.netty.noUnsafe", false);
      logger.debug("-Dio.netty.noUnsafe: {}", (Object)var0);
      if (var0) {
         logger.debug("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
         return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (io.netty.noUnsafe)");
      } else {
         String var1;
         if (SystemPropertyUtil.contains("io.netty.tryUnsafe")) {
            var1 = "io.netty.tryUnsafe";
         } else {
            var1 = "org.jboss.netty.tryUnsafe";
         }

         if (!SystemPropertyUtil.getBoolean(var1, true)) {
            String var2 = "sun.misc.Unsafe: unavailable (" + var1 + ")";
            logger.debug(var2);
            return new UnsupportedOperationException(var2);
         } else {
            return null;
         }
      }
   }

   static boolean isUnaligned() {
      return UNALIGNED;
   }

   static boolean hasUnsafe() {
      return UNSAFE != null;
   }

   static Throwable getUnsafeUnavailabilityCause() {
      return UNSAFE_UNAVAILABILITY_CAUSE;
   }

   static boolean unalignedAccess() {
      return UNALIGNED;
   }

   static void throwException(Throwable var0) {
      UNSAFE.throwException((Throwable)ObjectUtil.checkNotNull(var0, "cause"));
   }

   static boolean hasDirectBufferNoCleanerConstructor() {
      return DIRECT_BUFFER_CONSTRUCTOR != null;
   }

   static ByteBuffer reallocateDirectNoCleaner(ByteBuffer var0, int var1) {
      return newDirectBuffer(UNSAFE.reallocateMemory(directBufferAddress(var0), (long)var1), var1);
   }

   static ByteBuffer allocateDirectNoCleaner(int var0) {
      return newDirectBuffer(UNSAFE.allocateMemory((long)var0), var0);
   }

   static boolean hasAllocateArrayMethod() {
      return ALLOCATE_ARRAY_METHOD != null;
   }

   static byte[] allocateUninitializedArray(int var0) {
      try {
         return (byte[])((byte[])ALLOCATE_ARRAY_METHOD.invoke(INTERNAL_UNSAFE, Byte.TYPE, var0));
      } catch (IllegalAccessException var2) {
         throw new Error(var2);
      } catch (InvocationTargetException var3) {
         throw new Error(var3);
      }
   }

   static ByteBuffer newDirectBuffer(long var0, int var2) {
      ObjectUtil.checkPositiveOrZero(var2, "capacity");

      try {
         return (ByteBuffer)DIRECT_BUFFER_CONSTRUCTOR.newInstance(var0, var2);
      } catch (Throwable var4) {
         if (var4 instanceof Error) {
            throw (Error)var4;
         } else {
            throw new Error(var4);
         }
      }
   }

   static long directBufferAddress(ByteBuffer var0) {
      return getLong(var0, ADDRESS_FIELD_OFFSET);
   }

   static long byteArrayBaseOffset() {
      return BYTE_ARRAY_BASE_OFFSET;
   }

   static Object getObject(Object var0, long var1) {
      return UNSAFE.getObject(var0, var1);
   }

   static int getInt(Object var0, long var1) {
      return UNSAFE.getInt(var0, var1);
   }

   private static long getLong(Object var0, long var1) {
      return UNSAFE.getLong(var0, var1);
   }

   static long objectFieldOffset(Field var0) {
      return UNSAFE.objectFieldOffset(var0);
   }

   static byte getByte(long var0) {
      return UNSAFE.getByte(var0);
   }

   static short getShort(long var0) {
      return UNSAFE.getShort(var0);
   }

   static int getInt(long var0) {
      return UNSAFE.getInt(var0);
   }

   static long getLong(long var0) {
      return UNSAFE.getLong(var0);
   }

   static byte getByte(byte[] var0, int var1) {
      return UNSAFE.getByte(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static short getShort(byte[] var0, int var1) {
      return UNSAFE.getShort(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static int getInt(byte[] var0, int var1) {
      return UNSAFE.getInt(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static long getLong(byte[] var0, int var1) {
      return UNSAFE.getLong(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1);
   }

   static void putByte(long var0, byte var2) {
      UNSAFE.putByte(var0, var2);
   }

   static void putShort(long var0, short var2) {
      UNSAFE.putShort(var0, var2);
   }

   static void putInt(long var0, int var2) {
      UNSAFE.putInt(var0, var2);
   }

   static void putLong(long var0, long var2) {
      UNSAFE.putLong(var0, var2);
   }

   static void putByte(byte[] var0, int var1, byte var2) {
      UNSAFE.putByte(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void putShort(byte[] var0, int var1, short var2) {
      UNSAFE.putShort(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void putInt(byte[] var0, int var1, int var2) {
      UNSAFE.putInt(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void putLong(byte[] var0, int var1, long var2) {
      UNSAFE.putLong(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2);
   }

   static void copyMemory(long var0, long var2, long var4) {
      while(var4 > 0L) {
         long var6 = Math.min(var4, 1048576L);
         UNSAFE.copyMemory(var0, var2, var6);
         var4 -= var6;
         var0 += var6;
         var2 += var6;
      }

   }

   static void copyMemory(Object var0, long var1, Object var3, long var4, long var6) {
      while(var6 > 0L) {
         long var8 = Math.min(var6, 1048576L);
         UNSAFE.copyMemory(var0, var1, var3, var4, var8);
         var6 -= var8;
         var1 += var8;
         var4 += var8;
      }

   }

   static void setMemory(long var0, long var2, byte var4) {
      UNSAFE.setMemory(var0, var2, var4);
   }

   static void setMemory(Object var0, long var1, long var3, byte var5) {
      UNSAFE.setMemory(var0, var1, var3, var5);
   }

   static boolean equals(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      if (var4 <= 0) {
         return true;
      } else {
         long var5 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
         long var7 = BYTE_ARRAY_BASE_OFFSET + (long)var3;
         int var9 = var4 & 7;
         long var10 = var5 + (long)var9;
         long var12 = var5 - 8L + (long)var4;

         for(long var14 = var7 - 8L + (long)var4; var12 >= var10; var14 -= 8L) {
            if (UNSAFE.getLong(var0, var12) != UNSAFE.getLong(var2, var14)) {
               return false;
            }

            var12 -= 8L;
         }

         if (var9 >= 4) {
            var9 -= 4;
            if (UNSAFE.getInt(var0, var5 + (long)var9) != UNSAFE.getInt(var2, var7 + (long)var9)) {
               return false;
            }
         }

         if (var9 < 2) {
            return var0[var1] == var2[var3];
         } else {
            return UNSAFE.getChar(var0, var5) == UNSAFE.getChar(var2, var7) && (var9 == 2 || var0[var1 + 2] == var2[var3 + 2]);
         }
      }
   }

   static int equalsConstantTime(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      long var5 = 0L;
      long var7 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
      long var9 = BYTE_ARRAY_BASE_OFFSET + (long)var3;
      int var11 = var4 & 7;
      long var12 = var7 + (long)var11;
      long var14 = var7 - 8L + (long)var4;

      for(long var16 = var9 - 8L + (long)var4; var14 >= var12; var16 -= 8L) {
         var5 |= UNSAFE.getLong(var0, var14) ^ UNSAFE.getLong(var2, var16);
         var14 -= 8L;
      }

      switch(var11) {
      case 1:
         return ConstantTimeUtils.equalsConstantTime(var5 | (long)(UNSAFE.getByte(var0, var7) ^ UNSAFE.getByte(var2, var9)), 0L);
      case 2:
         return ConstantTimeUtils.equalsConstantTime(var5 | (long)(UNSAFE.getChar(var0, var7) ^ UNSAFE.getChar(var2, var9)), 0L);
      case 3:
         return ConstantTimeUtils.equalsConstantTime(var5 | (long)(UNSAFE.getChar(var0, var7 + 1L) ^ UNSAFE.getChar(var2, var9 + 1L)) | (long)(UNSAFE.getByte(var0, var7) ^ UNSAFE.getByte(var2, var9)), 0L);
      case 4:
         return ConstantTimeUtils.equalsConstantTime(var5 | (long)(UNSAFE.getInt(var0, var7) ^ UNSAFE.getInt(var2, var9)), 0L);
      case 5:
         return ConstantTimeUtils.equalsConstantTime(var5 | (long)(UNSAFE.getInt(var0, var7 + 1L) ^ UNSAFE.getInt(var2, var9 + 1L)) | (long)(UNSAFE.getByte(var0, var7) ^ UNSAFE.getByte(var2, var9)), 0L);
      case 6:
         return ConstantTimeUtils.equalsConstantTime(var5 | (long)(UNSAFE.getInt(var0, var7 + 2L) ^ UNSAFE.getInt(var2, var9 + 2L)) | (long)(UNSAFE.getChar(var0, var7) ^ UNSAFE.getChar(var2, var9)), 0L);
      case 7:
         return ConstantTimeUtils.equalsConstantTime(var5 | (long)(UNSAFE.getInt(var0, var7 + 3L) ^ UNSAFE.getInt(var2, var9 + 3L)) | (long)(UNSAFE.getChar(var0, var7 + 1L) ^ UNSAFE.getChar(var2, var9 + 1L)) | (long)(UNSAFE.getByte(var0, var7) ^ UNSAFE.getByte(var2, var9)), 0L);
      default:
         return ConstantTimeUtils.equalsConstantTime(var5, 0L);
      }
   }

   static boolean isZero(byte[] var0, int var1, int var2) {
      if (var2 <= 0) {
         return true;
      } else {
         long var3 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
         int var5 = var2 & 7;
         long var6 = var3 + (long)var5;

         for(long var8 = var3 - 8L + (long)var2; var8 >= var6; var8 -= 8L) {
            if (UNSAFE.getLong(var0, var8) != 0L) {
               return false;
            }
         }

         if (var5 >= 4) {
            var5 -= 4;
            if (UNSAFE.getInt(var0, var3 + (long)var5) != 0) {
               return false;
            }
         }

         if (var5 < 2) {
            return var0[var1] == 0;
         } else {
            return UNSAFE.getChar(var0, var3) == 0 && (var5 == 2 || var0[var1 + 2] == 0);
         }
      }
   }

   static int hashCodeAscii(byte[] var0, int var1, int var2) {
      int var3 = -1028477387;
      long var4 = BYTE_ARRAY_BASE_OFFSET + (long)var1;
      int var6 = var2 & 7;
      long var7 = var4 + (long)var6;

      for(long var9 = var4 - 8L + (long)var2; var9 >= var7; var9 -= 8L) {
         var3 = hashCodeAsciiCompute(UNSAFE.getLong(var0, var9), var3);
      }

      switch(var6) {
      case 1:
         return var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getByte(var0, var4));
      case 2:
         return var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getShort(var0, var4));
      case 3:
         return (var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getByte(var0, var4))) * 461845907 + hashCodeAsciiSanitize(UNSAFE.getShort(var0, var4 + 1L));
      case 4:
         return var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getInt(var0, var4));
      case 5:
         return (var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getByte(var0, var4))) * 461845907 + hashCodeAsciiSanitize(UNSAFE.getInt(var0, var4 + 1L));
      case 6:
         return (var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getShort(var0, var4))) * 461845907 + hashCodeAsciiSanitize(UNSAFE.getInt(var0, var4 + 2L));
      case 7:
         return ((var3 * -862048943 + hashCodeAsciiSanitize(UNSAFE.getByte(var0, var4))) * 461845907 + hashCodeAsciiSanitize(UNSAFE.getShort(var0, var4 + 1L))) * -862048943 + hashCodeAsciiSanitize(UNSAFE.getInt(var0, var4 + 3L));
      default:
         return var3;
      }
   }

   static int hashCodeAsciiCompute(long var0, int var2) {
      return var2 * -862048943 + hashCodeAsciiSanitize((int)var0) * 461845907 + (int)((var0 & 2242545357458243584L) >>> 32);
   }

   static int hashCodeAsciiSanitize(int var0) {
      return var0 & 522133279;
   }

   static int hashCodeAsciiSanitize(short var0) {
      return var0 & 7967;
   }

   static int hashCodeAsciiSanitize(byte var0) {
      return var0 & 31;
   }

   static ClassLoader getClassLoader(final Class<?> var0) {
      return System.getSecurityManager() == null ? var0.getClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return var0.getClassLoader();
         }
      });
   }

   static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return ClassLoader.getSystemClassLoader();
         }
      });
   }

   static int addressSize() {
      return UNSAFE.addressSize();
   }

   static long allocateMemory(long var0) {
      return UNSAFE.allocateMemory(var0);
   }

   static void freeMemory(long var0) {
      UNSAFE.freeMemory(var0);
   }

   static long reallocateMemory(long var0, long var2) {
      return UNSAFE.reallocateMemory(var0, var2);
   }

   static boolean isAndroid() {
      return IS_ANDROID;
   }

   private static boolean isAndroid0() {
      String var0 = SystemPropertyUtil.get("java.vm.name");
      boolean var1 = "Dalvik".equals(var0);
      if (var1) {
         logger.debug("Platform: Android");
      }

      return var1;
   }

   private static boolean explicitTryReflectionSetAccessible0() {
      return SystemPropertyUtil.getBoolean("io.netty.tryReflectionSetAccessible", javaVersion() < 9);
   }

   static boolean isExplicitTryReflectionSetAccessible() {
      return IS_EXPLICIT_TRY_REFLECTION_SET_ACCESSIBLE;
   }

   static int javaVersion() {
      return JAVA_VERSION;
   }

   private static int javaVersion0() {
      int var0;
      if (isAndroid0()) {
         var0 = 6;
      } else {
         var0 = majorVersionFromJavaSpecificationVersion();
      }

      logger.debug("Java version: {}", (Object)var0);
      return var0;
   }

   static int majorVersionFromJavaSpecificationVersion() {
      return majorVersion(SystemPropertyUtil.get("java.specification.version", "1.6"));
   }

   static int majorVersion(String var0) {
      String[] var1 = var0.split("\\.");
      int[] var2 = new int[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = Integer.parseInt(var1[var3]);
      }

      if (var2[0] == 1) {
         assert var2[1] >= 6;

         return var2[1];
      } else {
         return var2[0];
      }
   }

   private PlatformDependent0() {
      super();
   }

   static {
      Field var1 = null;
      Method var2 = null;
      Object var3 = null;
      Object var5 = null;
      final ByteBuffer var0;
      final Unsafe var4;
      long var7;
      if ((var3 = EXPLICIT_NO_UNSAFE_CAUSE) != null) {
         var0 = null;
         var1 = null;
         var4 = null;
         var5 = null;
      } else {
         var0 = ByteBuffer.allocateDirect(1);
         Object var6 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               try {
                  Field var1 = Unsafe.class.getDeclaredField("theUnsafe");
                  Throwable var2 = ReflectionUtil.trySetAccessible(var1, false);
                  return var2 != null ? var2 : var1.get((Object)null);
               } catch (NoSuchFieldException var3) {
                  return var3;
               } catch (SecurityException var4) {
                  return var4;
               } catch (IllegalAccessException var5) {
                  return var5;
               } catch (NoClassDefFoundError var6) {
                  return var6;
               }
            }
         });
         if (var6 instanceof Throwable) {
            var4 = null;
            var3 = (Throwable)var6;
            logger.debug("sun.misc.Unsafe.theUnsafe: unavailable", (Throwable)var6);
         } else {
            var4 = (Unsafe)var6;
            logger.debug("sun.misc.Unsafe.theUnsafe: available");
         }

         Object var8;
         if (var4 != null) {
            var8 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  try {
                     var4.getClass().getDeclaredMethod("copyMemory", Object.class, Long.TYPE, Object.class, Long.TYPE, Long.TYPE);
                     return null;
                  } catch (NoSuchMethodException var2) {
                     return var2;
                  } catch (SecurityException var3) {
                     return var3;
                  }
               }
            });
            if (var8 == null) {
               logger.debug("sun.misc.Unsafe.copyMemory: available");
            } else {
               var4 = null;
               var3 = (Throwable)var8;
               logger.debug("sun.misc.Unsafe.copyMemory: unavailable", (Throwable)var8);
            }
         }

         if (var4 != null) {
            var8 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  try {
                     Field var1 = Buffer.class.getDeclaredField("address");
                     long var2 = var4.objectFieldOffset(var1);
                     long var4x = var4.getLong(var0, var2);
                     return var4x == 0L ? null : var1;
                  } catch (NoSuchFieldException var6) {
                     return var6;
                  } catch (SecurityException var7) {
                     return var7;
                  }
               }
            });
            if (var8 instanceof Field) {
               var1 = (Field)var8;
               logger.debug("java.nio.Buffer.address: available");
            } else {
               var3 = (Throwable)var8;
               logger.debug("java.nio.Buffer.address: unavailable", (Throwable)var8);
               var4 = null;
            }
         }

         if (var4 != null) {
            var7 = (long)var4.arrayIndexScale(byte[].class);
            if (var7 != 1L) {
               logger.debug("unsafe.arrayIndexScale is {} (expected: 1). Not using unsafe.", (Object)var7);
               var3 = new UnsupportedOperationException("Unexpected unsafe.arrayIndexScale");
               var4 = null;
            }
         }
      }

      UNSAFE_UNAVAILABILITY_CAUSE = (Throwable)var3;
      UNSAFE = var4;
      if (var4 == null) {
         ADDRESS_FIELD_OFFSET = -1L;
         BYTE_ARRAY_BASE_OFFSET = -1L;
         UNALIGNED = false;
         DIRECT_BUFFER_CONSTRUCTOR = null;
         ALLOCATE_ARRAY_METHOD = null;
      } else {
         var7 = -1L;

         Constructor var27;
         try {
            Object var9 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  try {
                     Constructor var1 = var0.getClass().getDeclaredConstructor(Long.TYPE, Integer.TYPE);
                     Throwable var2 = ReflectionUtil.trySetAccessible(var1, true);
                     return var2 != null ? var2 : var1;
                  } catch (NoSuchMethodException var3) {
                     return var3;
                  } catch (SecurityException var4) {
                     return var4;
                  }
               }
            });
            if (var9 instanceof Constructor) {
               var7 = UNSAFE.allocateMemory(1L);

               try {
                  ((Constructor)var9).newInstance(var7, 1);
                  var27 = (Constructor)var9;
                  logger.debug("direct buffer constructor: available");
               } catch (InstantiationException var23) {
                  var27 = null;
               } catch (IllegalAccessException var24) {
                  var27 = null;
               } catch (InvocationTargetException var25) {
                  var27 = null;
               }
            } else {
               logger.debug("direct buffer constructor: unavailable", (Throwable)var9);
               var27 = null;
            }
         } finally {
            if (var7 != -1L) {
               UNSAFE.freeMemory(var7);
            }

         }

         DIRECT_BUFFER_CONSTRUCTOR = var27;
         ADDRESS_FIELD_OFFSET = objectFieldOffset(var1);
         BYTE_ARRAY_BASE_OFFSET = (long)UNSAFE.arrayBaseOffset(byte[].class);
         Object var10 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
               try {
                  Class var1 = Class.forName("java.nio.Bits", false, PlatformDependent0.getSystemClassLoader());
                  Method var2 = var1.getDeclaredMethod("unaligned");
                  Throwable var3 = ReflectionUtil.trySetAccessible(var2, true);
                  return var3 != null ? var3 : var2.invoke((Object)null);
               } catch (NoSuchMethodException var4) {
                  return var4;
               } catch (SecurityException var5) {
                  return var5;
               } catch (IllegalAccessException var6) {
                  return var6;
               } catch (ClassNotFoundException var7) {
                  return var7;
               } catch (InvocationTargetException var8) {
                  return var8;
               }
            }
         });
         boolean var28;
         if (var10 instanceof Boolean) {
            var28 = (Boolean)var10;
            logger.debug("java.nio.Bits.unaligned: available, {}", (Object)var28);
         } else {
            String var11 = SystemPropertyUtil.get("os.arch", "");
            var28 = var11.matches("^(i[3-6]86|x86(_64)?|x64|amd64)$");
            Throwable var12 = (Throwable)var10;
            logger.debug("java.nio.Bits.unaligned: unavailable {}", var28, var12);
         }

         UNALIGNED = var28;
         if (javaVersion() >= 9) {
            final Object var29 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  try {
                     Class var1 = PlatformDependent0.getClassLoader(PlatformDependent0.class).loadClass("jdk.internal.misc.Unsafe");
                     Method var2 = var1.getDeclaredMethod("getUnsafe");
                     return var2.invoke((Object)null);
                  } catch (Throwable var3) {
                     return var3;
                  }
               }
            });
            if (!(var29 instanceof Throwable)) {
               var5 = var29;
               Object var30 = var29;
               var29 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
                  public Object run() {
                     try {
                        return var29.getClass().getDeclaredMethod("allocateUninitializedArray", Class.class, Integer.TYPE);
                     } catch (NoSuchMethodException var2) {
                        return var2;
                     } catch (SecurityException var3) {
                        return var3;
                     }
                  }
               });
               if (var29 instanceof Method) {
                  try {
                     Method var13 = (Method)var29;
                     byte[] var14 = (byte[])((byte[])var13.invoke(var30, Byte.TYPE, 8));

                     assert var14.length == 8;

                     var2 = var13;
                  } catch (IllegalAccessException var21) {
                     var29 = var21;
                  } catch (InvocationTargetException var22) {
                     var29 = var22;
                  }
               }
            }

            if (var29 instanceof Throwable) {
               logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable", (Throwable)var29);
            } else {
               logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): available");
            }
         } else {
            logger.debug("jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9");
         }

         ALLOCATE_ARRAY_METHOD = var2;
      }

      INTERNAL_UNSAFE = var5;
      logger.debug("java.nio.DirectByteBuffer.<init>(long, int): {}", (Object)(DIRECT_BUFFER_CONSTRUCTOR != null ? "available" : "unavailable"));
   }
}
