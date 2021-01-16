package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscChunkedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MpscUnboundedArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.SpscLinkedQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscGrowableAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.MpscUnboundedAtomicArrayQueue;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.SpscLinkedAtomicQueue;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;
import java.io.File;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlatformDependent {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependent.class);
   private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile("\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");
   private static final boolean IS_WINDOWS = isWindows0();
   private static final boolean IS_OSX = isOsx0();
   private static final boolean MAYBE_SUPER_USER;
   private static final boolean CAN_ENABLE_TCP_NODELAY_BY_DEFAULT = !isAndroid();
   private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE = unsafeUnavailabilityCause0();
   private static final boolean DIRECT_BUFFER_PREFERRED;
   private static final long MAX_DIRECT_MEMORY;
   private static final int MPSC_CHUNK_SIZE = 1024;
   private static final int MIN_MAX_MPSC_CAPACITY = 2048;
   private static final int MAX_ALLOWED_MPSC_CAPACITY = 1073741824;
   private static final long BYTE_ARRAY_BASE_OFFSET;
   private static final File TMPDIR;
   private static final int BIT_MODE;
   private static final String NORMALIZED_ARCH;
   private static final String NORMALIZED_OS;
   private static final int ADDRESS_SIZE;
   private static final boolean USE_DIRECT_BUFFER_NO_CLEANER;
   private static final AtomicLong DIRECT_MEMORY_COUNTER;
   private static final long DIRECT_MEMORY_LIMIT;
   private static final PlatformDependent.ThreadLocalRandomProvider RANDOM_PROVIDER;
   private static final Cleaner CLEANER;
   private static final int UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD;
   public static final boolean BIG_ENDIAN_NATIVE_ORDER;
   private static final Cleaner NOOP;

   public static boolean hasDirectBufferNoCleanerConstructor() {
      return PlatformDependent0.hasDirectBufferNoCleanerConstructor();
   }

   public static byte[] allocateUninitializedArray(int var0) {
      return UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD >= 0 && UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD <= var0 ? PlatformDependent0.allocateUninitializedArray(var0) : new byte[var0];
   }

   public static boolean isAndroid() {
      return PlatformDependent0.isAndroid();
   }

   public static boolean isWindows() {
      return IS_WINDOWS;
   }

   public static boolean isOsx() {
      return IS_OSX;
   }

   public static boolean maybeSuperUser() {
      return MAYBE_SUPER_USER;
   }

   public static int javaVersion() {
      return PlatformDependent0.javaVersion();
   }

   public static boolean canEnableTcpNoDelayByDefault() {
      return CAN_ENABLE_TCP_NODELAY_BY_DEFAULT;
   }

   public static boolean hasUnsafe() {
      return UNSAFE_UNAVAILABILITY_CAUSE == null;
   }

   public static Throwable getUnsafeUnavailabilityCause() {
      return UNSAFE_UNAVAILABILITY_CAUSE;
   }

   public static boolean isUnaligned() {
      return PlatformDependent0.isUnaligned();
   }

   public static boolean directBufferPreferred() {
      return DIRECT_BUFFER_PREFERRED;
   }

   public static long maxDirectMemory() {
      return MAX_DIRECT_MEMORY;
   }

   public static File tmpdir() {
      return TMPDIR;
   }

   public static int bitMode() {
      return BIT_MODE;
   }

   public static int addressSize() {
      return ADDRESS_SIZE;
   }

   public static long allocateMemory(long var0) {
      return PlatformDependent0.allocateMemory(var0);
   }

   public static void freeMemory(long var0) {
      PlatformDependent0.freeMemory(var0);
   }

   public static long reallocateMemory(long var0, long var2) {
      return PlatformDependent0.reallocateMemory(var0, var2);
   }

   public static void throwException(Throwable var0) {
      if (hasUnsafe()) {
         PlatformDependent0.throwException(var0);
      } else {
         throwException0(var0);
      }

   }

   private static <E extends Throwable> void throwException0(Throwable var0) throws E {
      throw var0;
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap() {
      return new ConcurrentHashMap();
   }

   public static LongCounter newLongCounter() {
      return (LongCounter)(javaVersion() >= 8 ? new LongAdderCounter() : new PlatformDependent.AtomicLongCounter());
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int var0) {
      return new ConcurrentHashMap(var0);
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int var0, float var1) {
      return new ConcurrentHashMap(var0, var1);
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(int var0, float var1, int var2) {
      return new ConcurrentHashMap(var0, var1, var2);
   }

   public static <K, V> ConcurrentMap<K, V> newConcurrentHashMap(Map<? extends K, ? extends V> var0) {
      return new ConcurrentHashMap(var0);
   }

   public static void freeDirectBuffer(ByteBuffer var0) {
      CLEANER.freeDirectBuffer(var0);
   }

   public static long directBufferAddress(ByteBuffer var0) {
      return PlatformDependent0.directBufferAddress(var0);
   }

   public static ByteBuffer directBuffer(long var0, int var2) {
      if (PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
         return PlatformDependent0.newDirectBuffer(var0, var2);
      } else {
         throw new UnsupportedOperationException("sun.misc.Unsafe or java.nio.DirectByteBuffer.<init>(long, int) not available");
      }
   }

   public static int getInt(Object var0, long var1) {
      return PlatformDependent0.getInt(var0, var1);
   }

   public static byte getByte(long var0) {
      return PlatformDependent0.getByte(var0);
   }

   public static short getShort(long var0) {
      return PlatformDependent0.getShort(var0);
   }

   public static int getInt(long var0) {
      return PlatformDependent0.getInt(var0);
   }

   public static long getLong(long var0) {
      return PlatformDependent0.getLong(var0);
   }

   public static byte getByte(byte[] var0, int var1) {
      return PlatformDependent0.getByte(var0, var1);
   }

   public static short getShort(byte[] var0, int var1) {
      return PlatformDependent0.getShort(var0, var1);
   }

   public static int getInt(byte[] var0, int var1) {
      return PlatformDependent0.getInt(var0, var1);
   }

   public static long getLong(byte[] var0, int var1) {
      return PlatformDependent0.getLong(var0, var1);
   }

   private static long getLongSafe(byte[] var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER ? (long)var0[var1] << 56 | ((long)var0[var1 + 1] & 255L) << 48 | ((long)var0[var1 + 2] & 255L) << 40 | ((long)var0[var1 + 3] & 255L) << 32 | ((long)var0[var1 + 4] & 255L) << 24 | ((long)var0[var1 + 5] & 255L) << 16 | ((long)var0[var1 + 6] & 255L) << 8 | (long)var0[var1 + 7] & 255L : (long)var0[var1] & 255L | ((long)var0[var1 + 1] & 255L) << 8 | ((long)var0[var1 + 2] & 255L) << 16 | ((long)var0[var1 + 3] & 255L) << 24 | ((long)var0[var1 + 4] & 255L) << 32 | ((long)var0[var1 + 5] & 255L) << 40 | ((long)var0[var1 + 6] & 255L) << 48 | (long)var0[var1 + 7] << 56;
   }

   private static int getIntSafe(byte[] var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER ? var0[var1] << 24 | (var0[var1 + 1] & 255) << 16 | (var0[var1 + 2] & 255) << 8 | var0[var1 + 3] & 255 : var0[var1] & 255 | (var0[var1 + 1] & 255) << 8 | (var0[var1 + 2] & 255) << 16 | var0[var1 + 3] << 24;
   }

   private static short getShortSafe(byte[] var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER ? (short)(var0[var1] << 8 | var0[var1 + 1] & 255) : (short)(var0[var1] & 255 | var0[var1 + 1] << 8);
   }

   private static int hashCodeAsciiCompute(CharSequence var0, int var1, int var2) {
      return BIG_ENDIAN_NATIVE_ORDER ? var2 * -862048943 + hashCodeAsciiSanitizeInt(var0, var1 + 4) * 461845907 + hashCodeAsciiSanitizeInt(var0, var1) : var2 * -862048943 + hashCodeAsciiSanitizeInt(var0, var1) * 461845907 + hashCodeAsciiSanitizeInt(var0, var1 + 4);
   }

   private static int hashCodeAsciiSanitizeInt(CharSequence var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER ? var0.charAt(var1 + 3) & 31 | (var0.charAt(var1 + 2) & 31) << 8 | (var0.charAt(var1 + 1) & 31) << 16 | (var0.charAt(var1) & 31) << 24 : (var0.charAt(var1 + 3) & 31) << 24 | (var0.charAt(var1 + 2) & 31) << 16 | (var0.charAt(var1 + 1) & 31) << 8 | var0.charAt(var1) & 31;
   }

   private static int hashCodeAsciiSanitizeShort(CharSequence var0, int var1) {
      return BIG_ENDIAN_NATIVE_ORDER ? var0.charAt(var1 + 1) & 31 | (var0.charAt(var1) & 31) << 8 : (var0.charAt(var1 + 1) & 31) << 8 | var0.charAt(var1) & 31;
   }

   private static int hashCodeAsciiSanitizeByte(char var0) {
      return var0 & 31;
   }

   public static void putByte(long var0, byte var2) {
      PlatformDependent0.putByte(var0, var2);
   }

   public static void putShort(long var0, short var2) {
      PlatformDependent0.putShort(var0, var2);
   }

   public static void putInt(long var0, int var2) {
      PlatformDependent0.putInt(var0, var2);
   }

   public static void putLong(long var0, long var2) {
      PlatformDependent0.putLong(var0, var2);
   }

   public static void putByte(byte[] var0, int var1, byte var2) {
      PlatformDependent0.putByte(var0, var1, var2);
   }

   public static void putShort(byte[] var0, int var1, short var2) {
      PlatformDependent0.putShort(var0, var1, var2);
   }

   public static void putInt(byte[] var0, int var1, int var2) {
      PlatformDependent0.putInt(var0, var1, var2);
   }

   public static void putLong(byte[] var0, int var1, long var2) {
      PlatformDependent0.putLong(var0, var1, var2);
   }

   public static void copyMemory(long var0, long var2, long var4) {
      PlatformDependent0.copyMemory(var0, var2, var4);
   }

   public static void copyMemory(byte[] var0, int var1, long var2, long var4) {
      PlatformDependent0.copyMemory(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, (Object)null, var2, var4);
   }

   public static void copyMemory(long var0, byte[] var2, int var3, long var4) {
      PlatformDependent0.copyMemory((Object)null, var0, var2, BYTE_ARRAY_BASE_OFFSET + (long)var3, var4);
   }

   public static void setMemory(byte[] var0, int var1, long var2, byte var4) {
      PlatformDependent0.setMemory(var0, BYTE_ARRAY_BASE_OFFSET + (long)var1, var2, var4);
   }

   public static void setMemory(long var0, long var2, byte var4) {
      PlatformDependent0.setMemory(var0, var2, var4);
   }

   public static ByteBuffer allocateDirectNoCleaner(int var0) {
      assert USE_DIRECT_BUFFER_NO_CLEANER;

      incrementMemoryCounter(var0);

      try {
         return PlatformDependent0.allocateDirectNoCleaner(var0);
      } catch (Throwable var2) {
         decrementMemoryCounter(var0);
         throwException(var2);
         return null;
      }
   }

   public static ByteBuffer reallocateDirectNoCleaner(ByteBuffer var0, int var1) {
      assert USE_DIRECT_BUFFER_NO_CLEANER;

      int var2 = var1 - var0.capacity();
      incrementMemoryCounter(var2);

      try {
         return PlatformDependent0.reallocateDirectNoCleaner(var0, var1);
      } catch (Throwable var4) {
         decrementMemoryCounter(var2);
         throwException(var4);
         return null;
      }
   }

   public static void freeDirectNoCleaner(ByteBuffer var0) {
      assert USE_DIRECT_BUFFER_NO_CLEANER;

      int var1 = var0.capacity();
      PlatformDependent0.freeMemory(PlatformDependent0.directBufferAddress(var0));
      decrementMemoryCounter(var1);
   }

   private static void incrementMemoryCounter(int var0) {
      long var1;
      long var3;
      if (DIRECT_MEMORY_COUNTER != null) {
         do {
            var1 = DIRECT_MEMORY_COUNTER.get();
            var3 = var1 + (long)var0;
            if (var3 > DIRECT_MEMORY_LIMIT) {
               throw new OutOfDirectMemoryError("failed to allocate " + var0 + " byte(s) of direct memory (used: " + var1 + ", max: " + DIRECT_MEMORY_LIMIT + ')');
            }
         } while(!DIRECT_MEMORY_COUNTER.compareAndSet(var1, var3));
      }

   }

   private static void decrementMemoryCounter(int var0) {
      if (DIRECT_MEMORY_COUNTER != null) {
         long var1 = DIRECT_MEMORY_COUNTER.addAndGet((long)(-var0));

         assert var1 >= 0L;
      }

   }

   public static boolean useDirectBufferNoCleaner() {
      return USE_DIRECT_BUFFER_NO_CLEANER;
   }

   public static boolean equals(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess() ? PlatformDependent0.equals(var0, var1, var2, var3, var4) : equalsSafe(var0, var1, var2, var3, var4);
   }

   public static boolean isZero(byte[] var0, int var1, int var2) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess() ? PlatformDependent0.isZero(var0, var1, var2) : isZeroSafe(var0, var1, var2);
   }

   public static int equalsConstantTime(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess() ? PlatformDependent0.equalsConstantTime(var0, var1, var2, var3, var4) : ConstantTimeUtils.equalsConstantTime(var0, var1, var2, var3, var4);
   }

   public static int hashCodeAscii(byte[] var0, int var1, int var2) {
      return hasUnsafe() && PlatformDependent0.unalignedAccess() ? PlatformDependent0.hashCodeAscii(var0, var1, var2) : hashCodeAsciiSafe(var0, var1, var2);
   }

   public static int hashCodeAscii(CharSequence var0) {
      int var1 = -1028477387;
      int var2 = var0.length() & 7;
      switch(var0.length()) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
         break;
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
         var1 = hashCodeAsciiCompute(var0, var0.length() - 8, var1);
         break;
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
         var1 = hashCodeAsciiCompute(var0, var0.length() - 16, hashCodeAsciiCompute(var0, var0.length() - 8, var1));
         break;
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
         var1 = hashCodeAsciiCompute(var0, var0.length() - 24, hashCodeAsciiCompute(var0, var0.length() - 16, hashCodeAsciiCompute(var0, var0.length() - 8, var1)));
         break;
      default:
         for(int var3 = var0.length() - 8; var3 >= var2; var3 -= 8) {
            var1 = hashCodeAsciiCompute(var0, var3, var1);
         }
      }

      switch(var2) {
      case 1:
         return var1 * -862048943 + hashCodeAsciiSanitizeByte(var0.charAt(0));
      case 2:
         return var1 * -862048943 + hashCodeAsciiSanitizeShort(var0, 0);
      case 3:
         return (var1 * -862048943 + hashCodeAsciiSanitizeByte(var0.charAt(0))) * 461845907 + hashCodeAsciiSanitizeShort(var0, 1);
      case 4:
         return var1 * -862048943 + hashCodeAsciiSanitizeInt(var0, 0);
      case 5:
         return (var1 * -862048943 + hashCodeAsciiSanitizeByte(var0.charAt(0))) * 461845907 + hashCodeAsciiSanitizeInt(var0, 1);
      case 6:
         return (var1 * -862048943 + hashCodeAsciiSanitizeShort(var0, 0)) * 461845907 + hashCodeAsciiSanitizeInt(var0, 2);
      case 7:
         return ((var1 * -862048943 + hashCodeAsciiSanitizeByte(var0.charAt(0))) * 461845907 + hashCodeAsciiSanitizeShort(var0, 1)) * -862048943 + hashCodeAsciiSanitizeInt(var0, 3);
      default:
         return var1;
      }
   }

   public static <T> Queue<T> newMpscQueue() {
      return PlatformDependent.Mpsc.newMpscQueue();
   }

   public static <T> Queue<T> newMpscQueue(int var0) {
      return PlatformDependent.Mpsc.newMpscQueue(var0);
   }

   public static <T> Queue<T> newSpscQueue() {
      return (Queue)(hasUnsafe() ? new SpscLinkedQueue() : new SpscLinkedAtomicQueue());
   }

   public static <T> Queue<T> newFixedMpscQueue(int var0) {
      return (Queue)(hasUnsafe() ? new MpscArrayQueue(var0) : new MpscAtomicArrayQueue(var0));
   }

   public static ClassLoader getClassLoader(Class<?> var0) {
      return PlatformDependent0.getClassLoader(var0);
   }

   public static ClassLoader getContextClassLoader() {
      return PlatformDependent0.getContextClassLoader();
   }

   public static ClassLoader getSystemClassLoader() {
      return PlatformDependent0.getSystemClassLoader();
   }

   public static <C> Deque<C> newConcurrentDeque() {
      return (Deque)(javaVersion() < 7 ? new LinkedBlockingDeque() : new ConcurrentLinkedDeque());
   }

   public static Random threadLocalRandom() {
      return RANDOM_PROVIDER.current();
   }

   private static boolean isWindows0() {
      boolean var0 = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US).contains("win");
      if (var0) {
         logger.debug("Platform: Windows");
      }

      return var0;
   }

   private static boolean isOsx0() {
      String var0 = SystemPropertyUtil.get("os.name", "").toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
      boolean var1 = var0.startsWith("macosx") || var0.startsWith("osx");
      if (var1) {
         logger.debug("Platform: MacOS");
      }

      return var1;
   }

   private static boolean maybeSuperUser0() {
      String var0 = SystemPropertyUtil.get("user.name");
      if (isWindows()) {
         return "Administrator".equals(var0);
      } else {
         return "root".equals(var0) || "toor".equals(var0);
      }
   }

   private static Throwable unsafeUnavailabilityCause0() {
      if (isAndroid()) {
         logger.debug("sun.misc.Unsafe: unavailable (Android)");
         return new UnsupportedOperationException("sun.misc.Unsafe: unavailable (Android)");
      } else {
         Throwable var0 = PlatformDependent0.getUnsafeUnavailabilityCause();
         if (var0 != null) {
            return var0;
         } else {
            try {
               boolean var1 = PlatformDependent0.hasUnsafe();
               logger.debug("sun.misc.Unsafe: {}", (Object)(var1 ? "available" : "unavailable"));
               return var1 ? null : PlatformDependent0.getUnsafeUnavailabilityCause();
            } catch (Throwable var2) {
               logger.trace("Could not determine if Unsafe is available", var2);
               return new UnsupportedOperationException("Could not determine if Unsafe is available", var2);
            }
         }
      }
   }

   private static long maxDirectMemory0() {
      long var0 = 0L;
      ClassLoader var2 = null;

      Class var3;
      try {
         var2 = getSystemClassLoader();
         if (!SystemPropertyUtil.get("os.name", "").toLowerCase().contains("z/os")) {
            var3 = Class.forName("sun.misc.VM", true, var2);
            Method var4 = var3.getDeclaredMethod("maxDirectMemory");
            var0 = ((Number)var4.invoke((Object)null)).longValue();
         }
      } catch (Throwable var9) {
      }

      if (var0 > 0L) {
         return var0;
      } else {
         try {
            var3 = Class.forName("java.lang.management.ManagementFactory", true, var2);
            Class var11 = Class.forName("java.lang.management.RuntimeMXBean", true, var2);
            Object var5 = var3.getDeclaredMethod("getRuntimeMXBean").invoke((Object)null);
            List var6 = (List)var11.getDeclaredMethod("getInputArguments").invoke(var5);

            label43:
            for(int var7 = var6.size() - 1; var7 >= 0; --var7) {
               Matcher var8 = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher((CharSequence)var6.get(var7));
               if (var8.matches()) {
                  var0 = Long.parseLong(var8.group(1));
                  switch(var8.group(2).charAt(0)) {
                  case 'G':
                  case 'g':
                     var0 *= 1073741824L;
                     break label43;
                  case 'K':
                  case 'k':
                     var0 *= 1024L;
                     break label43;
                  case 'M':
                  case 'm':
                     var0 *= 1048576L;
                  default:
                     break label43;
                  }
               }
            }
         } catch (Throwable var10) {
         }

         if (var0 <= 0L) {
            var0 = Runtime.getRuntime().maxMemory();
            logger.debug("maxDirectMemory: {} bytes (maybe)", (Object)var0);
         } else {
            logger.debug("maxDirectMemory: {} bytes", (Object)var0);
         }

         return var0;
      }
   }

   private static File tmpdir0() {
      File var0;
      try {
         var0 = toDirectory(SystemPropertyUtil.get("io.netty.tmpdir"));
         if (var0 != null) {
            logger.debug("-Dio.netty.tmpdir: {}", (Object)var0);
            return var0;
         }

         var0 = toDirectory(SystemPropertyUtil.get("java.io.tmpdir"));
         if (var0 != null) {
            logger.debug("-Dio.netty.tmpdir: {} (java.io.tmpdir)", (Object)var0);
            return var0;
         }

         if (isWindows()) {
            var0 = toDirectory(System.getenv("TEMP"));
            if (var0 != null) {
               logger.debug("-Dio.netty.tmpdir: {} (%TEMP%)", (Object)var0);
               return var0;
            }

            String var1 = System.getenv("USERPROFILE");
            if (var1 != null) {
               var0 = toDirectory(var1 + "\\AppData\\Local\\Temp");
               if (var0 != null) {
                  logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\AppData\\Local\\Temp)", (Object)var0);
                  return var0;
               }

               var0 = toDirectory(var1 + "\\Local Settings\\Temp");
               if (var0 != null) {
                  logger.debug("-Dio.netty.tmpdir: {} (%USERPROFILE%\\Local Settings\\Temp)", (Object)var0);
                  return var0;
               }
            }
         } else {
            var0 = toDirectory(System.getenv("TMPDIR"));
            if (var0 != null) {
               logger.debug("-Dio.netty.tmpdir: {} ($TMPDIR)", (Object)var0);
               return var0;
            }
         }
      } catch (Throwable var2) {
      }

      if (isWindows()) {
         var0 = new File("C:\\Windows\\Temp");
      } else {
         var0 = new File("/tmp");
      }

      logger.warn("Failed to get the temporary directory; falling back to: {}", (Object)var0);
      return var0;
   }

   private static File toDirectory(String var0) {
      if (var0 == null) {
         return null;
      } else {
         File var1 = new File(var0);
         var1.mkdirs();
         if (!var1.isDirectory()) {
            return null;
         } else {
            try {
               return var1.getAbsoluteFile();
            } catch (Exception var3) {
               return var1;
            }
         }
      }
   }

   private static int bitMode0() {
      int var0 = SystemPropertyUtil.getInt("io.netty.bitMode", 0);
      if (var0 > 0) {
         logger.debug("-Dio.netty.bitMode: {}", (Object)var0);
         return var0;
      } else {
         var0 = SystemPropertyUtil.getInt("sun.arch.data.model", 0);
         if (var0 > 0) {
            logger.debug("-Dio.netty.bitMode: {} (sun.arch.data.model)", (Object)var0);
            return var0;
         } else {
            var0 = SystemPropertyUtil.getInt("com.ibm.vm.bitmode", 0);
            if (var0 > 0) {
               logger.debug("-Dio.netty.bitMode: {} (com.ibm.vm.bitmode)", (Object)var0);
               return var0;
            } else {
               String var1 = SystemPropertyUtil.get("os.arch", "").toLowerCase(Locale.US).trim();
               if (!"amd64".equals(var1) && !"x86_64".equals(var1)) {
                  if ("i386".equals(var1) || "i486".equals(var1) || "i586".equals(var1) || "i686".equals(var1)) {
                     var0 = 32;
                  }
               } else {
                  var0 = 64;
               }

               if (var0 > 0) {
                  logger.debug("-Dio.netty.bitMode: {} (os.arch: {})", var0, var1);
               }

               String var2 = SystemPropertyUtil.get("java.vm.name", "").toLowerCase(Locale.US);
               Pattern var3 = Pattern.compile("([1-9][0-9]+)-?bit");
               Matcher var4 = var3.matcher(var2);
               return var4.find() ? Integer.parseInt(var4.group(1)) : 64;
            }
         }
      }
   }

   private static int addressSize0() {
      return !hasUnsafe() ? -1 : PlatformDependent0.addressSize();
   }

   private static long byteArrayBaseOffset0() {
      return !hasUnsafe() ? -1L : PlatformDependent0.byteArrayBaseOffset();
   }

   private static boolean equalsSafe(byte[] var0, int var1, byte[] var2, int var3, int var4) {
      for(int var5 = var1 + var4; var1 < var5; ++var3) {
         if (var0[var1] != var2[var3]) {
            return false;
         }

         ++var1;
      }

      return true;
   }

   private static boolean isZeroSafe(byte[] var0, int var1, int var2) {
      for(int var3 = var1 + var2; var1 < var3; ++var1) {
         if (var0[var1] != 0) {
            return false;
         }
      }

      return true;
   }

   static int hashCodeAsciiSafe(byte[] var0, int var1, int var2) {
      int var3 = -1028477387;
      int var4 = var2 & 7;
      int var5 = var1 + var4;

      for(int var6 = var1 - 8 + var2; var6 >= var5; var6 -= 8) {
         var3 = PlatformDependent0.hashCodeAsciiCompute(getLongSafe(var0, var6), var3);
      }

      switch(var4) {
      case 1:
         return var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1]);
      case 2:
         return var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1));
      case 3:
         return (var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1 + 1));
      case 4:
         return var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1));
      case 5:
         return (var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1 + 1));
      case 6:
         return (var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1))) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1 + 2));
      case 7:
         return ((var3 * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(var0[var1])) * 461845907 + PlatformDependent0.hashCodeAsciiSanitize(getShortSafe(var0, var1 + 1))) * -862048943 + PlatformDependent0.hashCodeAsciiSanitize(getIntSafe(var0, var1 + 3));
      default:
         return var3;
      }
   }

   public static String normalizedArch() {
      return NORMALIZED_ARCH;
   }

   public static String normalizedOs() {
      return NORMALIZED_OS;
   }

   private static String normalize(String var0) {
      return var0.toLowerCase(Locale.US).replaceAll("[^a-z0-9]+", "");
   }

   private static String normalizeArch(String var0) {
      var0 = normalize(var0);
      if (var0.matches("^(x8664|amd64|ia32e|em64t|x64)$")) {
         return "x86_64";
      } else if (var0.matches("^(x8632|x86|i[3-6]86|ia32|x32)$")) {
         return "x86_32";
      } else if (var0.matches("^(ia64|itanium64)$")) {
         return "itanium_64";
      } else if (var0.matches("^(sparc|sparc32)$")) {
         return "sparc_32";
      } else if (var0.matches("^(sparcv9|sparc64)$")) {
         return "sparc_64";
      } else if (var0.matches("^(arm|arm32)$")) {
         return "arm_32";
      } else if ("aarch64".equals(var0)) {
         return "aarch_64";
      } else if (var0.matches("^(ppc|ppc32)$")) {
         return "ppc_32";
      } else if ("ppc64".equals(var0)) {
         return "ppc_64";
      } else if ("ppc64le".equals(var0)) {
         return "ppcle_64";
      } else if ("s390".equals(var0)) {
         return "s390_32";
      } else {
         return "s390x".equals(var0) ? "s390_64" : "unknown";
      }
   }

   private static String normalizeOs(String var0) {
      var0 = normalize(var0);
      if (var0.startsWith("aix")) {
         return "aix";
      } else if (var0.startsWith("hpux")) {
         return "hpux";
      } else if (!var0.startsWith("os400") || var0.length() > 5 && Character.isDigit(var0.charAt(5))) {
         if (var0.startsWith("linux")) {
            return "linux";
         } else if (!var0.startsWith("macosx") && !var0.startsWith("osx")) {
            if (var0.startsWith("freebsd")) {
               return "freebsd";
            } else if (var0.startsWith("openbsd")) {
               return "openbsd";
            } else if (var0.startsWith("netbsd")) {
               return "netbsd";
            } else if (!var0.startsWith("solaris") && !var0.startsWith("sunos")) {
               return var0.startsWith("windows") ? "windows" : "unknown";
            } else {
               return "sunos";
            }
         } else {
            return "osx";
         }
      } else {
         return "os400";
      }
   }

   private PlatformDependent() {
      super();
   }

   static {
      DIRECT_BUFFER_PREFERRED = UNSAFE_UNAVAILABILITY_CAUSE == null && !SystemPropertyUtil.getBoolean("io.netty.noPreferDirect", false);
      MAX_DIRECT_MEMORY = maxDirectMemory0();
      BYTE_ARRAY_BASE_OFFSET = byteArrayBaseOffset0();
      TMPDIR = tmpdir0();
      BIT_MODE = bitMode0();
      NORMALIZED_ARCH = normalizeArch(SystemPropertyUtil.get("os.arch", ""));
      NORMALIZED_OS = normalizeOs(SystemPropertyUtil.get("os.name", ""));
      ADDRESS_SIZE = addressSize0();
      BIG_ENDIAN_NATIVE_ORDER = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
      NOOP = new Cleaner() {
         public void freeDirectBuffer(ByteBuffer var1) {
         }
      };
      if (javaVersion() >= 7) {
         RANDOM_PROVIDER = new PlatformDependent.ThreadLocalRandomProvider() {
            public Random current() {
               return java.util.concurrent.ThreadLocalRandom.current();
            }
         };
      } else {
         RANDOM_PROVIDER = new PlatformDependent.ThreadLocalRandomProvider() {
            public Random current() {
               return ThreadLocalRandom.current();
            }
         };
      }

      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.noPreferDirect: {}", (Object)(!DIRECT_BUFFER_PREFERRED));
      }

      if (!hasUnsafe() && !isAndroid() && !PlatformDependent0.isExplicitNoUnsafe()) {
         logger.info("Your platform does not provide complete low-level API for accessing direct buffers reliably. Unless explicitly requested, heap buffer will always be preferred to avoid potential system instability.");
      }

      long var0 = SystemPropertyUtil.getLong("io.netty.maxDirectMemory", -1L);
      if (var0 != 0L && hasUnsafe() && PlatformDependent0.hasDirectBufferNoCleanerConstructor()) {
         USE_DIRECT_BUFFER_NO_CLEANER = true;
         if (var0 < 0L) {
            var0 = maxDirectMemory0();
            if (var0 <= 0L) {
               DIRECT_MEMORY_COUNTER = null;
            } else {
               DIRECT_MEMORY_COUNTER = new AtomicLong();
            }
         } else {
            DIRECT_MEMORY_COUNTER = new AtomicLong();
         }
      } else {
         USE_DIRECT_BUFFER_NO_CLEANER = false;
         DIRECT_MEMORY_COUNTER = null;
      }

      DIRECT_MEMORY_LIMIT = var0;
      logger.debug("-Dio.netty.maxDirectMemory: {} bytes", (Object)var0);
      int var2 = SystemPropertyUtil.getInt("io.netty.uninitializedArrayAllocationThreshold", 1024);
      UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD = javaVersion() >= 9 && PlatformDependent0.hasAllocateArrayMethod() ? var2 : -1;
      logger.debug("-Dio.netty.uninitializedArrayAllocationThreshold: {}", (Object)UNINITIALIZED_ARRAY_ALLOCATION_THRESHOLD);
      MAYBE_SUPER_USER = maybeSuperUser0();
      if (!isAndroid() && hasUnsafe()) {
         if (javaVersion() >= 9) {
            CLEANER = (Cleaner)(CleanerJava9.isSupported() ? new CleanerJava9() : NOOP);
         } else {
            CLEANER = (Cleaner)(CleanerJava6.isSupported() ? new CleanerJava6() : NOOP);
         }
      } else {
         CLEANER = NOOP;
      }

   }

   private interface ThreadLocalRandomProvider {
      Random current();
   }

   private static final class AtomicLongCounter extends AtomicLong implements LongCounter {
      private static final long serialVersionUID = 4074772784610639305L;

      private AtomicLongCounter() {
         super();
      }

      public void add(long var1) {
         this.addAndGet(var1);
      }

      public void increment() {
         this.incrementAndGet();
      }

      public void decrement() {
         this.decrementAndGet();
      }

      public long value() {
         return this.get();
      }

      // $FF: synthetic method
      AtomicLongCounter(Object var1) {
         this();
      }
   }

   private static final class Mpsc {
      private static final boolean USE_MPSC_CHUNKED_ARRAY_QUEUE;

      private Mpsc() {
         super();
      }

      static <T> Queue<T> newMpscQueue(int var0) {
         int var1 = Math.max(Math.min(var0, 1073741824), 2048);
         return (Queue)(USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscChunkedArrayQueue(1024, var1) : new MpscGrowableAtomicArrayQueue(1024, var1));
      }

      static <T> Queue<T> newMpscQueue() {
         return (Queue)(USE_MPSC_CHUNKED_ARRAY_QUEUE ? new MpscUnboundedArrayQueue(1024) : new MpscUnboundedAtomicArrayQueue(1024));
      }

      static {
         Object var0 = null;
         if (PlatformDependent.hasUnsafe()) {
            var0 = AccessController.doPrivileged(new PrivilegedAction<Object>() {
               public Object run() {
                  return UnsafeAccess.UNSAFE;
               }
            });
         }

         if (var0 == null) {
            PlatformDependent.logger.debug("org.jctools-core.MpscChunkedArrayQueue: unavailable");
            USE_MPSC_CHUNKED_ARRAY_QUEUE = false;
         } else {
            PlatformDependent.logger.debug("org.jctools-core.MpscChunkedArrayQueue: available");
            USE_MPSC_CHUNKED_ARRAY_QUEUE = true;
         }

      }
   }
}
