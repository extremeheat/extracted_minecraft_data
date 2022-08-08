package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
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
   public RenderChunkRegion createRegion(Level var1, BlockPos var2, BlockPos var3, int var4) {
      int var5 = SectionPos.blockToSectionCoord(var2.getX() - var4);
      int var6 = SectionPos.blockToSectionCoord(var2.getZ() - var4);
      int var7 = SectionPos.blockToSectionCoord(var3.getX() + var4);
      int var8 = SectionPos.blockToSectionCoord(var3.getZ() + var4);
      ChunkInfo[][] var9 = new ChunkInfo[var7 - var5 + 1][var8 - var6 + 1];

      int var11;
      for(int var10 = var5; var10 <= var7; ++var10) {
         for(var11 = var6; var11 <= var8; ++var11) {
            var9[var10 - var5][var11 - var6] = (ChunkInfo)this.chunkInfoCache.computeIfAbsent(ChunkPos.asLong(var10, var11), (var1x) -> {
               return new ChunkInfo(var1.getChunk(ChunkPos.getX(var1x), ChunkPos.getZ(var1x)));
            });
         }
      }

      if (isAllEmpty(var2, var3, var5, var6, var9)) {
         return null;
      } else {
         RenderChunk[][] var13 = new RenderChunk[var7 - var5 + 1][var8 - var6 + 1];

         for(var11 = var5; var11 <= var7; ++var11) {
            for(int var12 = var6; var12 <= var8; ++var12) {
               var13[var11 - var5][var12 - var6] = var9[var11 - var5][var12 - var6].renderChunk();
            }
         }

         return new RenderChunkRegion(var1, var5, var6, var13);
      }
   }

   private static boolean isAllEmpty(BlockPos var0, BlockPos var1, int var2, int var3, ChunkInfo[][] var4) {
      int var5 = SectionPos.blockToSectionCoord(var0.getX());
      int var6 = SectionPos.blockToSectionCoord(var0.getZ());
      int var7 = SectionPos.blockToSectionCoord(var1.getX());
      int var8 = SectionPos.blockToSectionCoord(var1.getZ());

      for(int var9 = var5; var9 <= var7; ++var9) {
         for(int var10 = var6; var10 <= var8; ++var10) {
            LevelChunk var11 = var4[var9 - var2][var10 - var3].chunk();
            if (!var11.isYSpaceEmpty(var0.getY(), var1.getY())) {
               return false;
            }
         }
      }

      return true;
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
