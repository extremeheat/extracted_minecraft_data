package io.netty.buffer;

import io.netty.util.Recycler;
import io.netty.util.internal.MathUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.ByteBuffer;
import java.util.Queue;

final class PoolThreadCache {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(PoolThreadCache.class);
   final PoolArena<byte[]> heapArena;
   final PoolArena<ByteBuffer> directArena;
   private final PoolThreadCache.MemoryRegionCache<byte[]>[] tinySubPageHeapCaches;
   private final PoolThreadCache.MemoryRegionCache<byte[]>[] smallSubPageHeapCaches;
   private final PoolThreadCache.MemoryRegionCache<ByteBuffer>[] tinySubPageDirectCaches;
   private final PoolThreadCache.MemoryRegionCache<ByteBuffer>[] smallSubPageDirectCaches;
   private final PoolThreadCache.MemoryRegionCache<byte[]>[] normalHeapCaches;
   private final PoolThreadCache.MemoryRegionCache<ByteBuffer>[] normalDirectCaches;
   private final int numShiftsNormalDirect;
   private final int numShiftsNormalHeap;
   private final int freeSweepAllocationThreshold;
   private int allocations;

   PoolThreadCache(PoolArena<byte[]> var1, PoolArena<ByteBuffer> var2, int var3, int var4, int var5, int var6, int var7) {
      super();
      if (var6 < 0) {
         throw new IllegalArgumentException("maxCachedBufferCapacity: " + var6 + " (expected: >= 0)");
      } else {
         this.freeSweepAllocationThreshold = var7;
         this.heapArena = var1;
         this.directArena = var2;
         if (var2 != null) {
            this.tinySubPageDirectCaches = createSubPageCaches(var3, 32, PoolArena.SizeClass.Tiny);
            this.smallSubPageDirectCaches = createSubPageCaches(var4, var2.numSmallSubpagePools, PoolArena.SizeClass.Small);
            this.numShiftsNormalDirect = log2(var2.pageSize);
            this.normalDirectCaches = createNormalCaches(var5, var6, var2);
            var2.numThreadCaches.getAndIncrement();
         } else {
            this.tinySubPageDirectCaches = null;
            this.smallSubPageDirectCaches = null;
            this.normalDirectCaches = null;
            this.numShiftsNormalDirect = -1;
         }

         if (var1 != null) {
            this.tinySubPageHeapCaches = createSubPageCaches(var3, 32, PoolArena.SizeClass.Tiny);
            this.smallSubPageHeapCaches = createSubPageCaches(var4, var1.numSmallSubpagePools, PoolArena.SizeClass.Small);
            this.numShiftsNormalHeap = log2(var1.pageSize);
            this.normalHeapCaches = createNormalCaches(var5, var6, var1);
            var1.numThreadCaches.getAndIncrement();
         } else {
            this.tinySubPageHeapCaches = null;
            this.smallSubPageHeapCaches = null;
            this.normalHeapCaches = null;
            this.numShiftsNormalHeap = -1;
         }

         if ((this.tinySubPageDirectCaches != null || this.smallSubPageDirectCaches != null || this.normalDirectCaches != null || this.tinySubPageHeapCaches != null || this.smallSubPageHeapCaches != null || this.normalHeapCaches != null) && var7 < 1) {
            throw new IllegalArgumentException("freeSweepAllocationThreshold: " + var7 + " (expected: > 0)");
         }
      }
   }

   private static <T> PoolThreadCache.MemoryRegionCache<T>[] createSubPageCaches(int var0, int var1, PoolArena.SizeClass var2) {
      if (var0 > 0 && var1 > 0) {
         PoolThreadCache.MemoryRegionCache[] var3 = new PoolThreadCache.MemoryRegionCache[var1];

         for(int var4 = 0; var4 < var3.length; ++var4) {
            var3[var4] = new PoolThreadCache.SubPageMemoryRegionCache(var0, var2);
         }

         return var3;
      } else {
         return null;
      }
   }

   private static <T> PoolThreadCache.MemoryRegionCache<T>[] createNormalCaches(int var0, int var1, PoolArena<T> var2) {
      if (var0 > 0 && var1 > 0) {
         int var3 = Math.min(var2.chunkSize, var1);
         int var4 = Math.max(1, log2(var3 / var2.pageSize) + 1);
         PoolThreadCache.MemoryRegionCache[] var5 = new PoolThreadCache.MemoryRegionCache[var4];

         for(int var6 = 0; var6 < var5.length; ++var6) {
            var5[var6] = new PoolThreadCache.NormalMemoryRegionCache(var0);
         }

         return var5;
      } else {
         return null;
      }
   }

   private static int log2(int var0) {
      int var1;
      for(var1 = 0; var0 > 1; ++var1) {
         var0 >>= 1;
      }

      return var1;
   }

   boolean allocateTiny(PoolArena<?> var1, PooledByteBuf<?> var2, int var3, int var4) {
      return this.allocate(this.cacheForTiny(var1, var4), var2, var3);
   }

   boolean allocateSmall(PoolArena<?> var1, PooledByteBuf<?> var2, int var3, int var4) {
      return this.allocate(this.cacheForSmall(var1, var4), var2, var3);
   }

   boolean allocateNormal(PoolArena<?> var1, PooledByteBuf<?> var2, int var3, int var4) {
      return this.allocate(this.cacheForNormal(var1, var4), var2, var3);
   }

   private boolean allocate(PoolThreadCache.MemoryRegionCache<?> var1, PooledByteBuf var2, int var3) {
      if (var1 == null) {
         return false;
      } else {
         boolean var4 = var1.allocate(var2, var3);
         if (++this.allocations >= this.freeSweepAllocationThreshold) {
            this.allocations = 0;
            this.trim();
         }

         return var4;
      }
   }

   boolean add(PoolArena<?> var1, PoolChunk var2, long var3, int var5, PoolArena.SizeClass var6) {
      PoolThreadCache.MemoryRegionCache var7 = this.cache(var1, var5, var6);
      return var7 == null ? false : var7.add(var2, var3);
   }

   private PoolThreadCache.MemoryRegionCache<?> cache(PoolArena<?> var1, int var2, PoolArena.SizeClass var3) {
      switch(var3) {
      case Normal:
         return this.cacheForNormal(var1, var2);
      case Small:
         return this.cacheForSmall(var1, var2);
      case Tiny:
         return this.cacheForTiny(var1, var2);
      default:
         throw new Error();
      }
   }

   void free() {
      int var1 = free(this.tinySubPageDirectCaches) + free(this.smallSubPageDirectCaches) + free(this.normalDirectCaches) + free(this.tinySubPageHeapCaches) + free(this.smallSubPageHeapCaches) + free(this.normalHeapCaches);
      if (var1 > 0 && logger.isDebugEnabled()) {
         logger.debug("Freed {} thread-local buffer(s) from thread: {}", var1, Thread.currentThread().getName());
      }

      if (this.directArena != null) {
         this.directArena.numThreadCaches.getAndDecrement();
      }

      if (this.heapArena != null) {
         this.heapArena.numThreadCaches.getAndDecrement();
      }

   }

   private static int free(PoolThreadCache.MemoryRegionCache<?>[] var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = 0;
         PoolThreadCache.MemoryRegionCache[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            PoolThreadCache.MemoryRegionCache var5 = var2[var4];
            var1 += free(var5);
         }

         return var1;
      }
   }

   private static int free(PoolThreadCache.MemoryRegionCache<?> var0) {
      return var0 == null ? 0 : var0.free();
   }

   void trim() {
      trim(this.tinySubPageDirectCaches);
      trim(this.smallSubPageDirectCaches);
      trim(this.normalDirectCaches);
      trim(this.tinySubPageHeapCaches);
      trim(this.smallSubPageHeapCaches);
      trim(this.normalHeapCaches);
   }

   private static void trim(PoolThreadCache.MemoryRegionCache<?>[] var0) {
      if (var0 != null) {
         PoolThreadCache.MemoryRegionCache[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            PoolThreadCache.MemoryRegionCache var4 = var1[var3];
            trim(var4);
         }

      }
   }

   private static void trim(PoolThreadCache.MemoryRegionCache<?> var0) {
      if (var0 != null) {
         var0.trim();
      }
   }

   private PoolThreadCache.MemoryRegionCache<?> cacheForTiny(PoolArena<?> var1, int var2) {
      int var3 = PoolArena.tinyIdx(var2);
      return var1.isDirect() ? cache(this.tinySubPageDirectCaches, var3) : cache(this.tinySubPageHeapCaches, var3);
   }

   private PoolThreadCache.MemoryRegionCache<?> cacheForSmall(PoolArena<?> var1, int var2) {
      int var3 = PoolArena.smallIdx(var2);
      return var1.isDirect() ? cache(this.smallSubPageDirectCaches, var3) : cache(this.smallSubPageHeapCaches, var3);
   }

   private PoolThreadCache.MemoryRegionCache<?> cacheForNormal(PoolArena<?> var1, int var2) {
      int var3;
      if (var1.isDirect()) {
         var3 = log2(var2 >> this.numShiftsNormalDirect);
         return cache(this.normalDirectCaches, var3);
      } else {
         var3 = log2(var2 >> this.numShiftsNormalHeap);
         return cache(this.normalHeapCaches, var3);
      }
   }

   private static <T> PoolThreadCache.MemoryRegionCache<T> cache(PoolThreadCache.MemoryRegionCache<T>[] var0, int var1) {
      return var0 != null && var1 <= var0.length - 1 ? var0[var1] : null;
   }

   private abstract static class MemoryRegionCache<T> {
      private final int size;
      private final Queue<PoolThreadCache.MemoryRegionCache.Entry<T>> queue;
      private final PoolArena.SizeClass sizeClass;
      private int allocations;
      private static final Recycler<PoolThreadCache.MemoryRegionCache.Entry> RECYCLER = new Recycler<PoolThreadCache.MemoryRegionCache.Entry>() {
         protected PoolThreadCache.MemoryRegionCache.Entry newObject(Recycler.Handle<PoolThreadCache.MemoryRegionCache.Entry> var1) {
            return new PoolThreadCache.MemoryRegionCache.Entry(var1);
         }
      };

      MemoryRegionCache(int var1, PoolArena.SizeClass var2) {
         super();
         this.size = MathUtil.safeFindNextPositivePowerOfTwo(var1);
         this.queue = PlatformDependent.newFixedMpscQueue(this.size);
         this.sizeClass = var2;
      }

      protected abstract void initBuf(PoolChunk<T> var1, long var2, PooledByteBuf<T> var4, int var5);

      public final boolean add(PoolChunk<T> var1, long var2) {
         PoolThreadCache.MemoryRegionCache.Entry var4 = newEntry(var1, var2);
         boolean var5 = this.queue.offer(var4);
         if (!var5) {
            var4.recycle();
         }

         return var5;
      }

      public final boolean allocate(PooledByteBuf<T> var1, int var2) {
         PoolThreadCache.MemoryRegionCache.Entry var3 = (PoolThreadCache.MemoryRegionCache.Entry)this.queue.poll();
         if (var3 == null) {
            return false;
         } else {
            this.initBuf(var3.chunk, var3.handle, var1, var2);
            var3.recycle();
            ++this.allocations;
            return true;
         }
      }

      public final int free() {
         return this.free(2147483647);
      }

      private int free(int var1) {
         int var2;
         for(var2 = 0; var2 < var1; ++var2) {
            PoolThreadCache.MemoryRegionCache.Entry var3 = (PoolThreadCache.MemoryRegionCache.Entry)this.queue.poll();
            if (var3 == null) {
               return var2;
            }

            this.freeEntry(var3);
         }

         return var2;
      }

      public final void trim() {
         int var1 = this.size - this.allocations;
         this.allocations = 0;
         if (var1 > 0) {
            this.free(var1);
         }

      }

      private void freeEntry(PoolThreadCache.MemoryRegionCache.Entry var1) {
         PoolChunk var2 = var1.chunk;
         long var3 = var1.handle;
         var1.recycle();
         var2.arena.freeChunk(var2, var3, this.sizeClass);
      }

      private static PoolThreadCache.MemoryRegionCache.Entry newEntry(PoolChunk<?> var0, long var1) {
         PoolThreadCache.MemoryRegionCache.Entry var3 = (PoolThreadCache.MemoryRegionCache.Entry)RECYCLER.get();
         var3.chunk = var0;
         var3.handle = var1;
         return var3;
      }

      static final class Entry<T> {
         final Recycler.Handle<PoolThreadCache.MemoryRegionCache.Entry<?>> recyclerHandle;
         PoolChunk<T> chunk;
         long handle = -1L;

         Entry(Recycler.Handle<PoolThreadCache.MemoryRegionCache.Entry<?>> var1) {
            super();
            this.recyclerHandle = var1;
         }

         void recycle() {
            this.chunk = null;
            this.handle = -1L;
            this.recyclerHandle.recycle(this);
         }
      }
   }

   private static final class NormalMemoryRegionCache<T> extends PoolThreadCache.MemoryRegionCache<T> {
      NormalMemoryRegionCache(int var1) {
         super(var1, PoolArena.SizeClass.Normal);
      }

      protected void initBuf(PoolChunk<T> var1, long var2, PooledByteBuf<T> var4, int var5) {
         var1.initBuf(var4, var2, var5);
      }
   }

   private static final class SubPageMemoryRegionCache<T> extends PoolThreadCache.MemoryRegionCache<T> {
      SubPageMemoryRegionCache(int var1, PoolArena.SizeClass var2) {
         super(var1, var2);
      }

      protected void initBuf(PoolChunk<T> var1, long var2, PooledByteBuf<T> var4, int var5) {
         var1.initBufWithSubpage(var4, var2, var5);
      }
   }
}
