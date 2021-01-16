package io.netty.buffer;

import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PooledByteBufAllocator extends AbstractByteBufAllocator implements ByteBufAllocatorMetricProvider {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PooledByteBufAllocator.class);
   private static final int DEFAULT_NUM_HEAP_ARENA;
   private static final int DEFAULT_NUM_DIRECT_ARENA;
   private static final int DEFAULT_PAGE_SIZE;
   private static final int DEFAULT_MAX_ORDER;
   private static final int DEFAULT_TINY_CACHE_SIZE;
   private static final int DEFAULT_SMALL_CACHE_SIZE;
   private static final int DEFAULT_NORMAL_CACHE_SIZE;
   private static final int DEFAULT_MAX_CACHED_BUFFER_CAPACITY;
   private static final int DEFAULT_CACHE_TRIM_INTERVAL;
   private static final boolean DEFAULT_USE_CACHE_FOR_ALL_THREADS;
   private static final int DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT;
   private static final int MIN_PAGE_SIZE = 4096;
   private static final int MAX_CHUNK_SIZE = 1073741824;
   public static final PooledByteBufAllocator DEFAULT;
   private final PoolArena<byte[]>[] heapArenas;
   private final PoolArena<ByteBuffer>[] directArenas;
   private final int tinyCacheSize;
   private final int smallCacheSize;
   private final int normalCacheSize;
   private final List<PoolArenaMetric> heapArenaMetrics;
   private final List<PoolArenaMetric> directArenaMetrics;
   private final PooledByteBufAllocator.PoolThreadLocalCache threadCache;
   private final int chunkSize;
   private final PooledByteBufAllocatorMetric metric;

   public PooledByteBufAllocator() {
      this(false);
   }

   public PooledByteBufAllocator(boolean var1) {
      this(var1, DEFAULT_NUM_HEAP_ARENA, DEFAULT_NUM_DIRECT_ARENA, DEFAULT_PAGE_SIZE, DEFAULT_MAX_ORDER);
   }

   public PooledByteBufAllocator(int var1, int var2, int var3, int var4) {
      this(false, var1, var2, var3, var4);
   }

   /** @deprecated */
   @Deprecated
   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5) {
      this(var1, var2, var3, var4, var5, DEFAULT_TINY_CACHE_SIZE, DEFAULT_SMALL_CACHE_SIZE, DEFAULT_NORMAL_CACHE_SIZE);
   }

   /** @deprecated */
   @Deprecated
   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, DEFAULT_USE_CACHE_FOR_ALL_THREADS, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
   }

   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9) {
      this(var1, var2, var3, var4, var5, var6, var7, var8, var9, DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT);
   }

   public PooledByteBufAllocator(boolean var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, int var10) {
      super(var1);
      this.threadCache = new PooledByteBufAllocator.PoolThreadLocalCache(var9);
      this.tinyCacheSize = var6;
      this.smallCacheSize = var7;
      this.normalCacheSize = var8;
      this.chunkSize = validateAndCalculateChunkSize(var4, var5);
      if (var2 < 0) {
         throw new IllegalArgumentException("nHeapArena: " + var2 + " (expected: >= 0)");
      } else if (var3 < 0) {
         throw new IllegalArgumentException("nDirectArea: " + var3 + " (expected: >= 0)");
      } else if (var10 < 0) {
         throw new IllegalArgumentException("directMemoryCacheAlignment: " + var10 + " (expected: >= 0)");
      } else if (var10 > 0 && !isDirectMemoryCacheAlignmentSupported()) {
         throw new IllegalArgumentException("directMemoryCacheAlignment is not supported");
      } else if ((var10 & -var10) != var10) {
         throw new IllegalArgumentException("directMemoryCacheAlignment: " + var10 + " (expected: power of two)");
      } else {
         int var11 = validateAndCalculatePageShifts(var4);
         ArrayList var12;
         int var13;
         if (var2 > 0) {
            this.heapArenas = newArenaArray(var2);
            var12 = new ArrayList(this.heapArenas.length);

            for(var13 = 0; var13 < this.heapArenas.length; ++var13) {
               PoolArena.HeapArena var14 = new PoolArena.HeapArena(this, var4, var5, var11, this.chunkSize, var10);
               this.heapArenas[var13] = var14;
               var12.add(var14);
            }

            this.heapArenaMetrics = Collections.unmodifiableList(var12);
         } else {
            this.heapArenas = null;
            this.heapArenaMetrics = Collections.emptyList();
         }

         if (var3 > 0) {
            this.directArenas = newArenaArray(var3);
            var12 = new ArrayList(this.directArenas.length);

            for(var13 = 0; var13 < this.directArenas.length; ++var13) {
               PoolArena.DirectArena var15 = new PoolArena.DirectArena(this, var4, var5, var11, this.chunkSize, var10);
               this.directArenas[var13] = var15;
               var12.add(var15);
            }

            this.directArenaMetrics = Collections.unmodifiableList(var12);
         } else {
            this.directArenas = null;
            this.directArenaMetrics = Collections.emptyList();
         }

         this.metric = new PooledByteBufAllocatorMetric(this);
      }
   }

   private static <T> PoolArena<T>[] newArenaArray(int var0) {
      return new PoolArena[var0];
   }

   private static int validateAndCalculatePageShifts(int var0) {
      if (var0 < 4096) {
         throw new IllegalArgumentException("pageSize: " + var0 + " (expected: " + 4096 + ")");
      } else if ((var0 & var0 - 1) != 0) {
         throw new IllegalArgumentException("pageSize: " + var0 + " (expected: power of 2)");
      } else {
         return 31 - Integer.numberOfLeadingZeros(var0);
      }
   }

   private static int validateAndCalculateChunkSize(int var0, int var1) {
      if (var1 > 14) {
         throw new IllegalArgumentException("maxOrder: " + var1 + " (expected: 0-14)");
      } else {
         int var2 = var0;

         for(int var3 = var1; var3 > 0; --var3) {
            if (var2 > 536870912) {
               throw new IllegalArgumentException(String.format("pageSize (%d) << maxOrder (%d) must not exceed %d", var0, var1, 1073741824));
            }

            var2 <<= 1;
         }

         return var2;
      }
   }

   protected ByteBuf newHeapBuffer(int var1, int var2) {
      PoolThreadCache var3 = (PoolThreadCache)this.threadCache.get();
      PoolArena var4 = var3.heapArena;
      Object var5;
      if (var4 != null) {
         var5 = var4.allocate(var3, var1, var2);
      } else {
         var5 = PlatformDependent.hasUnsafe() ? new UnpooledUnsafeHeapByteBuf(this, var1, var2) : new UnpooledHeapByteBuf(this, var1, var2);
      }

      return toLeakAwareBuffer((ByteBuf)var5);
   }

   protected ByteBuf newDirectBuffer(int var1, int var2) {
      PoolThreadCache var3 = (PoolThreadCache)this.threadCache.get();
      PoolArena var4 = var3.directArena;
      Object var5;
      if (var4 != null) {
         var5 = var4.allocate(var3, var1, var2);
      } else {
         var5 = PlatformDependent.hasUnsafe() ? UnsafeByteBufUtil.newUnsafeDirectByteBuf(this, var1, var2) : new UnpooledDirectByteBuf(this, var1, var2);
      }

      return toLeakAwareBuffer((ByteBuf)var5);
   }

   public static int defaultNumHeapArena() {
      return DEFAULT_NUM_HEAP_ARENA;
   }

   public static int defaultNumDirectArena() {
      return DEFAULT_NUM_DIRECT_ARENA;
   }

   public static int defaultPageSize() {
      return DEFAULT_PAGE_SIZE;
   }

   public static int defaultMaxOrder() {
      return DEFAULT_MAX_ORDER;
   }

   public static boolean defaultUseCacheForAllThreads() {
      return DEFAULT_USE_CACHE_FOR_ALL_THREADS;
   }

   public static boolean defaultPreferDirect() {
      return PlatformDependent.directBufferPreferred();
   }

   public static int defaultTinyCacheSize() {
      return DEFAULT_TINY_CACHE_SIZE;
   }

   public static int defaultSmallCacheSize() {
      return DEFAULT_SMALL_CACHE_SIZE;
   }

   public static int defaultNormalCacheSize() {
      return DEFAULT_NORMAL_CACHE_SIZE;
   }

   public static boolean isDirectMemoryCacheAlignmentSupported() {
      return PlatformDependent.hasUnsafe();
   }

   public boolean isDirectBufferPooled() {
      return this.directArenas != null;
   }

   /** @deprecated */
   @Deprecated
   public boolean hasThreadLocalCache() {
      return this.threadCache.isSet();
   }

   /** @deprecated */
   @Deprecated
   public void freeThreadLocalCache() {
      this.threadCache.remove();
   }

   public PooledByteBufAllocatorMetric metric() {
      return this.metric;
   }

   /** @deprecated */
   @Deprecated
   public int numHeapArenas() {
      return this.heapArenaMetrics.size();
   }

   /** @deprecated */
   @Deprecated
   public int numDirectArenas() {
      return this.directArenaMetrics.size();
   }

   /** @deprecated */
   @Deprecated
   public List<PoolArenaMetric> heapArenas() {
      return this.heapArenaMetrics;
   }

   /** @deprecated */
   @Deprecated
   public List<PoolArenaMetric> directArenas() {
      return this.directArenaMetrics;
   }

   /** @deprecated */
   @Deprecated
   public int numThreadLocalCaches() {
      PoolArena[] var1 = this.heapArenas != null ? this.heapArenas : this.directArenas;
      if (var1 == null) {
         return 0;
      } else {
         int var2 = 0;
         PoolArena[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            PoolArena var6 = var3[var5];
            var2 += var6.numThreadCaches.get();
         }

         return var2;
      }
   }

   /** @deprecated */
   @Deprecated
   public int tinyCacheSize() {
      return this.tinyCacheSize;
   }

   /** @deprecated */
   @Deprecated
   public int smallCacheSize() {
      return this.smallCacheSize;
   }

   /** @deprecated */
   @Deprecated
   public int normalCacheSize() {
      return this.normalCacheSize;
   }

   /** @deprecated */
   @Deprecated
   public final int chunkSize() {
      return this.chunkSize;
   }

   final long usedHeapMemory() {
      return usedMemory(this.heapArenas);
   }

   final long usedDirectMemory() {
      return usedMemory(this.directArenas);
   }

   private static long usedMemory(PoolArena<?>... var0) {
      if (var0 == null) {
         return -1L;
      } else {
         long var1 = 0L;
         PoolArena[] var3 = var0;
         int var4 = var0.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            PoolArena var6 = var3[var5];
            var1 += var6.numActiveBytes();
            if (var1 < 0L) {
               return 9223372036854775807L;
            }
         }

         return var1;
      }
   }

   final PoolThreadCache threadCache() {
      PoolThreadCache var1 = (PoolThreadCache)this.threadCache.get();

      assert var1 != null;

      return var1;
   }

   public String dumpStats() {
      int var1 = this.heapArenas == null ? 0 : this.heapArenas.length;
      StringBuilder var2 = (new StringBuilder(512)).append(var1).append(" heap arena(s):").append(StringUtil.NEWLINE);
      int var5;
      if (var1 > 0) {
         PoolArena[] var3 = this.heapArenas;
         int var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            PoolArena var6 = var3[var5];
            var2.append(var6);
         }
      }

      int var8 = this.directArenas == null ? 0 : this.directArenas.length;
      var2.append(var8).append(" direct arena(s):").append(StringUtil.NEWLINE);
      if (var8 > 0) {
         PoolArena[] var9 = this.directArenas;
         var5 = var9.length;

         for(int var10 = 0; var10 < var5; ++var10) {
            PoolArena var7 = var9[var10];
            var2.append(var7);
         }
      }

      return var2.toString();
   }

   static {
      int var0 = SystemPropertyUtil.getInt("io.netty.allocator.pageSize", 8192);
      Throwable var1 = null;

      try {
         validateAndCalculatePageShifts(var0);
      } catch (Throwable var8) {
         var1 = var8;
         var0 = 8192;
      }

      DEFAULT_PAGE_SIZE = var0;
      int var2 = SystemPropertyUtil.getInt("io.netty.allocator.maxOrder", 11);
      Throwable var3 = null;

      try {
         validateAndCalculateChunkSize(DEFAULT_PAGE_SIZE, var2);
      } catch (Throwable var7) {
         var3 = var7;
         var2 = 11;
      }

      DEFAULT_MAX_ORDER = var2;
      Runtime var4 = Runtime.getRuntime();
      int var5 = NettyRuntime.availableProcessors() * 2;
      int var6 = DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER;
      DEFAULT_NUM_HEAP_ARENA = Math.max(0, SystemPropertyUtil.getInt("io.netty.allocator.numHeapArenas", (int)Math.min((long)var5, var4.maxMemory() / (long)var6 / 2L / 3L)));
      DEFAULT_NUM_DIRECT_ARENA = Math.max(0, SystemPropertyUtil.getInt("io.netty.allocator.numDirectArenas", (int)Math.min((long)var5, PlatformDependent.maxDirectMemory() / (long)var6 / 2L / 3L)));
      DEFAULT_TINY_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.tinyCacheSize", 512);
      DEFAULT_SMALL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.smallCacheSize", 256);
      DEFAULT_NORMAL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.normalCacheSize", 64);
      DEFAULT_MAX_CACHED_BUFFER_CAPACITY = SystemPropertyUtil.getInt("io.netty.allocator.maxCachedBufferCapacity", 32768);
      DEFAULT_CACHE_TRIM_INTERVAL = SystemPropertyUtil.getInt("io.netty.allocator.cacheTrimInterval", 8192);
      DEFAULT_USE_CACHE_FOR_ALL_THREADS = SystemPropertyUtil.getBoolean("io.netty.allocator.useCacheForAllThreads", true);
      DEFAULT_DIRECT_MEMORY_CACHE_ALIGNMENT = SystemPropertyUtil.getInt("io.netty.allocator.directMemoryCacheAlignment", 0);
      if (logger.isDebugEnabled()) {
         logger.debug("-Dio.netty.allocator.numHeapArenas: {}", (Object)DEFAULT_NUM_HEAP_ARENA);
         logger.debug("-Dio.netty.allocator.numDirectArenas: {}", (Object)DEFAULT_NUM_DIRECT_ARENA);
         if (var1 == null) {
            logger.debug("-Dio.netty.allocator.pageSize: {}", (Object)DEFAULT_PAGE_SIZE);
         } else {
            logger.debug("-Dio.netty.allocator.pageSize: {}", DEFAULT_PAGE_SIZE, var1);
         }

         if (var3 == null) {
            logger.debug("-Dio.netty.allocator.maxOrder: {}", (Object)DEFAULT_MAX_ORDER);
         } else {
            logger.debug("-Dio.netty.allocator.maxOrder: {}", DEFAULT_MAX_ORDER, var3);
         }

         logger.debug("-Dio.netty.allocator.chunkSize: {}", (Object)(DEFAULT_PAGE_SIZE << DEFAULT_MAX_ORDER));
         logger.debug("-Dio.netty.allocator.tinyCacheSize: {}", (Object)DEFAULT_TINY_CACHE_SIZE);
         logger.debug("-Dio.netty.allocator.smallCacheSize: {}", (Object)DEFAULT_SMALL_CACHE_SIZE);
         logger.debug("-Dio.netty.allocator.normalCacheSize: {}", (Object)DEFAULT_NORMAL_CACHE_SIZE);
         logger.debug("-Dio.netty.allocator.maxCachedBufferCapacity: {}", (Object)DEFAULT_MAX_CACHED_BUFFER_CAPACITY);
         logger.debug("-Dio.netty.allocator.cacheTrimInterval: {}", (Object)DEFAULT_CACHE_TRIM_INTERVAL);
         logger.debug("-Dio.netty.allocator.useCacheForAllThreads: {}", (Object)DEFAULT_USE_CACHE_FOR_ALL_THREADS);
      }

      DEFAULT = new PooledByteBufAllocator(PlatformDependent.directBufferPreferred());
   }

   final class PoolThreadLocalCache extends FastThreadLocal<PoolThreadCache> {
      private final boolean useCacheForAllThreads;

      PoolThreadLocalCache(boolean var2) {
         super();
         this.useCacheForAllThreads = var2;
      }

      protected synchronized PoolThreadCache initialValue() {
         PoolArena var1 = this.leastUsedArena(PooledByteBufAllocator.this.heapArenas);
         PoolArena var2 = this.leastUsedArena(PooledByteBufAllocator.this.directArenas);
         Thread var3 = Thread.currentThread();
         return !this.useCacheForAllThreads && !(var3 instanceof FastThreadLocalThread) ? new PoolThreadCache(var1, var2, 0, 0, 0, 0, 0) : new PoolThreadCache(var1, var2, PooledByteBufAllocator.this.tinyCacheSize, PooledByteBufAllocator.this.smallCacheSize, PooledByteBufAllocator.this.normalCacheSize, PooledByteBufAllocator.DEFAULT_MAX_CACHED_BUFFER_CAPACITY, PooledByteBufAllocator.DEFAULT_CACHE_TRIM_INTERVAL);
      }

      protected void onRemoval(PoolThreadCache var1) {
         var1.free();
      }

      private <T> PoolArena<T> leastUsedArena(PoolArena<T>[] var1) {
         if (var1 != null && var1.length != 0) {
            PoolArena var2 = var1[0];

            for(int var3 = 1; var3 < var1.length; ++var3) {
               PoolArena var4 = var1[var3];
               if (var4.numThreadCaches.get() < var2.numThreadCaches.get()) {
                  var2 = var4;
               }
            }

            return var2;
         } else {
            return null;
         }
      }
   }
}
