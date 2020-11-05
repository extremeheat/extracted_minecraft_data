package net.minecraft.client.color.block;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public class BlockTintCache {
   private final ThreadLocal<BlockTintCache.LatestCacheInfo> latestChunkOnThread = ThreadLocal.withInitial(() -> {
      return new BlockTintCache.LatestCacheInfo();
   });
   private final Long2ObjectLinkedOpenHashMap<int[]> cache = new Long2ObjectLinkedOpenHashMap(256, 0.25F);
   private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

   public BlockTintCache() {
      super();
   }

   public int getColor(BlockPos var1, IntSupplier var2) {
      int var3 = var1.getX() >> 4;
      int var4 = var1.getZ() >> 4;
      BlockTintCache.LatestCacheInfo var5 = (BlockTintCache.LatestCacheInfo)this.latestChunkOnThread.get();
      if (var5.x != var3 || var5.z != var4) {
         var5.x = var3;
         var5.z = var4;
         var5.cache = this.findOrCreateChunkCache(var3, var4);
      }

      int var6 = var1.getX() & 15;
      int var7 = var1.getZ() & 15;
      int var8 = var7 << 4 | var6;
      int var9 = var5.cache[var8];
      if (var9 != -1) {
         return var9;
      } else {
         int var10 = var2.getAsInt();
         var5.cache[var8] = var10;
         return var10;
      }
   }

   public void invalidateForChunk(int var1, int var2) {
      try {
         this.lock.writeLock().lock();

         for(int var3 = -1; var3 <= 1; ++var3) {
            for(int var4 = -1; var4 <= 1; ++var4) {
               long var5 = ChunkPos.asLong(var1 + var3, var2 + var4);
               this.cache.remove(var5);
            }
         }
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   public void invalidateAll() {
      try {
         this.lock.writeLock().lock();
         this.cache.clear();
      } finally {
         this.lock.writeLock().unlock();
      }

   }

   private int[] findOrCreateChunkCache(int var1, int var2) {
      long var3 = ChunkPos.asLong(var1, var2);
      this.lock.readLock().lock();

      int[] var5;
      try {
         var5 = (int[])this.cache.get(var3);
      } finally {
         this.lock.readLock().unlock();
      }

      if (var5 != null) {
         return var5;
      } else {
         int[] var6 = new int[256];
         Arrays.fill(var6, -1);

         try {
            this.lock.writeLock().lock();
            if (this.cache.size() >= 256) {
               this.cache.removeFirst();
            }

            this.cache.put(var3, var6);
         } finally {
            this.lock.writeLock().unlock();
         }

         return var6;
      }
   }

   static class LatestCacheInfo {
      public int x;
      public int z;
      public int[] cache;

      private LatestCacheInfo() {
         super();
         this.x = -2147483648;
         this.z = -2147483648;
      }

      // $FF: synthetic method
      LatestCacheInfo(Object var1) {
         this();
      }
   }
}
