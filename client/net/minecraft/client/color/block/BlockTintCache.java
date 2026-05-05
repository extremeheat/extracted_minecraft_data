package net.minecraft.client.color.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.ToIntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import org.jspecify.annotations.Nullable;

public class BlockTintCache {
   private static final int MAX_CACHE_ENTRIES = 256;
   private final ThreadLocal<LatestCacheInfo> latestChunkOnThread = ThreadLocal.withInitial(LatestCacheInfo::new);
   private final Long2ObjectLinkedOpenHashMap<CacheData> cache = new Long2ObjectLinkedOpenHashMap(256, 0.25F);
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
   private final ToIntFunction<BlockPos> source;

   public BlockTintCache(final ToIntFunction<BlockPos> source) {
      super();
      this.source = source;
   }

   public int getColor(final BlockPos pos) {
      int chunkX = SectionPos.blockToSectionCoord(pos.getX());
      int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
      LatestCacheInfo chunkInfo = (LatestCacheInfo)this.latestChunkOnThread.get();
      if (chunkInfo.x != chunkX || chunkInfo.z != chunkZ || chunkInfo.cache == null || chunkInfo.cache.isInvalidated()) {
         chunkInfo.x = chunkX;
         chunkInfo.z = chunkZ;
         chunkInfo.cache = this.findOrCreateChunkCache(chunkX, chunkZ);
      }

      int[] layer = chunkInfo.cache.getLayer(pos.getY());
      int x = pos.getX() & 15;
      int z = pos.getZ() & 15;
      int index = z << 4 | x;
      int cached = layer[index];
      if (cached != -1) {
         return cached;
      } else {
         int calculated = this.source.applyAsInt(pos);
         layer[index] = calculated;
         return calculated;
      }
   }

   public void invalidateForChunk(final int chunkX, final int chunkZ) {
      try {
         this.lock.writeLock().lock();

         for(int offsetX = -1; offsetX <= 1; ++offsetX) {
            for(int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
               long key = ChunkPos.pack(chunkX + offsetX, chunkZ + offsetZ);
               CacheData removed = (CacheData)this.cache.remove(key);
               if (removed != null) {
                  removed.invalidate();
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

   private CacheData findOrCreateChunkCache(final int x, final int z) {
      long key = ChunkPos.pack(x, z);
      this.lock.readLock().lock();

      try {
         CacheData existing = (CacheData)this.cache.get(key);
         if (existing != null) {
            CacheData var6 = existing;
            return var6;
         }
      } finally {
         this.lock.readLock().unlock();
      }

      this.lock.writeLock().lock();

      CacheData newCache;
      try {
         CacheData existingNow = (CacheData)this.cache.get(key);
         if (existingNow == null) {
            newCache = new CacheData();
            if (this.cache.size() >= 256) {
               CacheData cacheData = (CacheData)this.cache.removeFirst();
               if (cacheData != null) {
                  cacheData.invalidate();
               }
            }

            this.cache.put(key, newCache);
            CacheData var18 = newCache;
            return var18;
         }

         newCache = existingNow;
      } finally {
         this.lock.writeLock().unlock();
      }

      return newCache;
   }

   private static class CacheData {
      private final Int2ObjectArrayMap<int[]> cache = new Int2ObjectArrayMap(16);
      private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
      private static final int BLOCKS_PER_LAYER = Mth.square(16);
      private volatile boolean invalidated;

      private CacheData() {
         super();
      }

      public int[] getLayer(final int y) {
         this.lock.readLock().lock();

         try {
            int[] existing = (int[])this.cache.get(y);
            if (existing != null) {
               int[] var3 = existing;
               return var3;
            }
         } finally {
            this.lock.readLock().unlock();
         }

         this.lock.writeLock().lock();

         int[] var12;
         try {
            var12 = (int[])this.cache.computeIfAbsent(y, (n) -> this.allocateLayer());
         } finally {
            this.lock.writeLock().unlock();
         }

         return var12;
      }

      private int[] allocateLayer() {
         int[] newCache = new int[BLOCKS_PER_LAYER];
         Arrays.fill(newCache, -1);
         return newCache;
      }

      public boolean isInvalidated() {
         return this.invalidated;
      }

      public void invalidate() {
         this.invalidated = true;
      }
   }

   private static class LatestCacheInfo {
      private int x = -2147483648;
      private int z = -2147483648;
      private @Nullable CacheData cache;

      private LatestCacheInfo() {
         super();
      }
   }
}
