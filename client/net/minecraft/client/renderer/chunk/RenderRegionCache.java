package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

public class RenderRegionCache {
   private final Long2ObjectMap<ChunkInfo> chunkInfoCache = new Long2ObjectOpenHashMap();

   public RenderRegionCache() {
      super();
   }

   @Nullable
   public RenderChunkRegion createRegion(Level var1, SectionPos var2) {
      ChunkInfo var3 = this.getChunkInfo(var1, var2.x(), var2.z());
      if (var3.chunk().isSectionEmpty(var2.y())) {
         return null;
      } else {
         int var4 = var2.x() - 1;
         int var5 = var2.z() - 1;
         int var6 = var2.x() + 1;
         int var7 = var2.z() + 1;
         RenderChunk[] var8 = new RenderChunk[9];

         for(int var9 = var5; var9 <= var7; ++var9) {
            for(int var10 = var4; var10 <= var6; ++var10) {
               int var11 = RenderChunkRegion.index(var4, var5, var10, var9);
               ChunkInfo var12 = var10 == var2.x() && var9 == var2.z() ? var3 : this.getChunkInfo(var1, var10, var9);
               var8[var11] = var12.renderChunk();
            }
         }

         return new RenderChunkRegion(var1, var4, var5, var8);
      }
   }

   private ChunkInfo getChunkInfo(Level var1, int var2, int var3) {
      return (ChunkInfo)this.chunkInfoCache.computeIfAbsent(ChunkPos.asLong(var2, var3), (var1x) -> {
         return new ChunkInfo(var1.getChunk(ChunkPos.getX(var1x), ChunkPos.getZ(var1x)));
      });
   }

   private static final class ChunkInfo {
      private final LevelChunk chunk;
      @Nullable
      private RenderChunk renderChunk;

      ChunkInfo(LevelChunk var1) {
         super();
         this.chunk = var1;
      }

      public LevelChunk chunk() {
         return this.chunk;
      }

      public RenderChunk renderChunk() {
         if (this.renderChunk == null) {
            this.renderChunk = new RenderChunk(this.chunk);
         }

         return this.renderChunk;
      }
   }
}
