package net.minecraft.client.color.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

public class BlockTintCache {
   private static final int MAX_CACHE_ENTRIES = 256;
   private final ThreadLocal<LatestCacheInfo> latestChunkOnThread = ThreadLocal.withInitial(LatestCacheInfo::new);
   private final Long2ObjectLinkedOpenHashMap<CacheData> cache = new Long2ObjectLinkedOpenHashMap(256, 0.25F);
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private final ToIntFunction<BlockPos> source;

   public BlockTintCache(ToIntFunction<BlockPos> var1) {
      super();
      this.source = var1;
   }

   public int getColor(BlockPos var1) {
      int var2 = SectionPos.blockToSectionCoord(var1.getX());
      int var3 = SectionPos.blockToSectionCoord(var1.getZ());
      LatestCacheInfo var4 = (LatestCacheInfo)this.latestChunkOnThread.get();
      if (var4.x != var2 || var4.z != var3 || var4.cache == null || var4.cache.isInvalidated()) {
         var4.x = var2;
         var4.z = var3;
         var4.cache = this.findOrCreateChunkCache(var2, var3);
      }

      int[] var5 = var4.cache.getLayer(var1.getY());
      int var6 = var1.getX() & 15;
      int var7 = var1.getZ() & 15;
      int var8 = var7 << 4 | var6;
      int var9 = var5[var8];
      if (var9 != -1) {
         return var9;
      } else {
         int var10 = this.source.applyAsInt(var1);
         var5[var8] = var10;
         return var10;
      }
   }

   public void invalidateForChunk(int var1, int var2) {
      try {
         this.lock.writeLock().lock();

         for(int var3 = -1; var3 <= 1; ++var3) {
            for(int var4 = -1; var4 <= 1; ++var4) {
               long var5 = ChunkPos.asLong(var1 + var3, var2 + var4);
               CacheData var7 = (CacheData)this.cache.remove(var5);
               if (var7 != null) {
                  var7.invalidate();
               }
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public void invalidateAll() {
      try {
         this.lock.writeLock().lock();
         this.cache.values().forEach(CacheData::invalidate);
         this.cache.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   private CacheData findOrCreateChunkCache(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      this.lock.readLock().lock();

      try {
         CacheData var5 = (CacheData)this.cache.get(var3);
         if (var5 != null) {
            CacheData var6 = var5;
            return var6;
         }
      } finally {
         this.lock.readLock().unlock();
      }

      this.lock.writeLock().lock();

      CacheData var16;
      try {
         CacheData var15 = (CacheData)this.cache.get(var3);
         if (var15 == null) {
            var16 = new CacheData();
            if (this.cache.size() >= 256) {
               CacheData var7 = (CacheData)this.cache.removeFirst();
               if (var7 != null) {
                  var7.invalidate();
               }
            }

            this.cache.put(var3, var16);
            CacheData var18 = var16;
            return var18;
         }

         var16 = var15;
      } finally {
         this.lock.writeLock().unlock();
      }

      return var16;
   }

   static class CacheData {
      private final Int2ObjectArrayMap<int[]> cache = new Int2ObjectArrayMap(16);
      private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
      private static final int BLOCKS_PER_LAYER = Mth.square(16);
      private volatile boolean invalidated;

      CacheData() {
         super();
      }

      public int[] getLayer(int var1) {
         this.lock.readLock().lock();

         try {
            int[] var2 = (int[])this.cache.get(var1);
            if (var2 != null) {
               int[] var3 = var2;
               return var3;
            }
         } finally {
            this.lock.readLock().unlock();
         }

         this.lock.writeLock().lock();

         int[] var12;
         try {
            var12 = (int[])this.cache.computeIfAbsent(var1, (var1x) -> this.allocateLayer());
         } finally {
            this.lock.writeLock().unlock();
         }

         return var12;
      }

      private int[] allocateLayer() {
         int[] var1 = new int[BLOCKS_PER_LAYER];
         Arrays.fill(var1, -1);
         return var1;
      }

      public boolean isInvalidated() {
         return this.invalidated;
      }

      public void invalidate() {
         this.invalidated = true;
      }
   }

   static class LatestCacheInfo {
      public int x = -2147483648;
      public int z = -2147483648;
      @Nullable
      CacheData cache;

      private LatestCacheInfo() {
         super();
      }
   }
}
