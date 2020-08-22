package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.Dimension;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

public interface LevelReader extends BlockAndTintGetter, CollisionGetter, BiomeManager.NoiseBiomeSource {
   @Nullable
   ChunkAccess getChunk(int var1, int var2, ChunkStatus var3, boolean var4);

   @Deprecated
   boolean hasChunk(int var1, int var2);

   int getHeight(Heightmap.Types var1, int var2, int var3);

   int getSkyDarken();

   BiomeManager getBiomeManager();

   default Biome getBiome(BlockPos var1) {
      return this.getBiomeManager().getBiome(var1);
   }

   default int getBlockTint(BlockPos var1, ColorResolver var2) {
      return var2.getColor(this.getBiome(var1), (double)var1.getX(), (double)var1.getZ());
   }

   default Biome getNoiseBiome(int var1, int var2, int var3) {
      ChunkAccess var4 = this.getChunk(var1 >> 2, var3 >> 2, ChunkStatus.BIOMES, false);
      return var4 != null && var4.getBiomes() != null ? var4.getBiomes().getNoiseBiome(var1, var2, var3) : this.getUncachedNoiseBiome(var1, var2, var3);
   }

   Biome getUncachedNoiseBiome(int var1, int var2, int var3);

   boolean isClientSide();

   int getSeaLevel();

   Dimension getDimension();

   default BlockPos getHeightmapPos(Heightmap.Types var1, BlockPos var2) {
      return new BlockPos(var2.getX(), this.getHeight(var1, var2.getX(), var2.getZ()), var2.getZ());
   }

   default boolean isEmptyBlock(BlockPos var1) {
      return this.getBlockState(var1).isAir();
   }

   default boolean canSeeSkyFromBelowWater(BlockPos var1) {
      if (var1.getY() >= this.getSeaLevel()) {
         return this.canSeeSky(var1);
      } else {
         BlockPos var2 = new BlockPos(var1.getX(), this.getSeaLevel(), var1.getZ());
         if (!this.canSeeSky(var2)) {
            return false;
         } else {
            for(var2 = var2.below(); var2.getY() > var1.getY(); var2 = var2.below()) {
               BlockState var3 = this.getBlockState(var2);
               if (var3.getLightBlock(this, var2) > 0 && !var3.getMaterial().isLiquid()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Deprecated
   default float getBrightness(BlockPos var1) {
      return this.getDimension().getBrightness(this.getMaxLocalRawBrightness(var1));
   }

   default int getDirectSignal(BlockPos var1, Direction var2) {
      return this.getBlockState(var1).getDirectSignal(this, var1, var2);
   }

   default ChunkAccess getChunk(BlockPos var1) {
      return this.getChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   default ChunkAccess getChunk(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.FULL, true);
   }

   default ChunkAccess getChunk(int var1, int var2, ChunkStatus var3) {
      return this.getChunk(var1, var2, var3, true);
   }

   @Nullable
   default BlockGetter getChunkForCollisions(int var1, int var2) {
      return this.getChunk(var1, var2, ChunkStatus.EMPTY, false);
   }

   default boolean isWaterAt(BlockPos var1) {
      return this.getFluidState(var1).is(FluidTags.WATER);
   }

   default boolean containsAnyLiquid(AABB var1) {
      int var2 = Mth.floor(var1.minX);
      int var3 = Mth.ceil(var1.maxX);
      int var4 = Mth.floor(var1.minY);
      int var5 = Mth.ceil(var1.maxY);
      int var6 = Mth.floor(var1.minZ);
      int var7 = Mth.ceil(var1.maxZ);
      BlockPos.PooledMutableBlockPos var8 = BlockPos.PooledMutableBlockPos.acquire();
      Throwable var9 = null;

      try {
         for(int var10 = var2; var10 < var3; ++var10) {
            for(int var11 = var4; var11 < var5; ++var11) {
               for(int var12 = var6; var12 < var7; ++var12) {
                  BlockState var13 = this.getBlockState(var8.set(var10, var11, var12));
                  if (!var13.getFluidState().isEmpty()) {
                     boolean var14 = true;
                     return var14;
                  }
               }
            }
         }

         return false;
      } catch (Throwable var24) {
         var9 = var24;
         throw var24;
      } finally {
         if (var8 != null) {
            if (var9 != null) {
               try {
                  var8.close();
               } catch (Throwable var23) {
                  var9.addSuppressed(var23);
               }
            } else {
               var8.close();
            }
         }

      }
   }

   default int getMaxLocalRawBrightness(BlockPos var1) {
      return this.getMaxLocalRawBrightness(var1, this.getSkyDarken());
   }

   default int getMaxLocalRawBrightness(BlockPos var1, int var2) {
      return var1.getX() >= -30000000 && var1.getZ() >= -30000000 && var1.getX() < 30000000 && var1.getZ() < 30000000 ? this.getRawBrightness(var1, var2) : 15;
   }

   @Deprecated
   default boolean hasChunkAt(BlockPos var1) {
      return this.hasChunk(var1.getX() >> 4, var1.getZ() >> 4);
   }

   @Deprecated
   default boolean hasChunksAt(BlockPos var1, BlockPos var2) {
      return this.hasChunksAt(var1.getX(), var1.getY(), var1.getZ(), var2.getX(), var2.getY(), var2.getZ());
   }

   @Deprecated
   default boolean hasChunksAt(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (var5 >= 0 && var2 < 256) {
         var1 >>= 4;
         var3 >>= 4;
         var4 >>= 4;
         var6 >>= 4;

         for(int var7 = var1; var7 <= var4; ++var7) {
            for(int var8 = var3; var8 <= var6; ++var8) {
               if (!this.hasChunk(var7, var8)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }
}
