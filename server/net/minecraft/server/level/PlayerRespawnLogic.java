package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class PlayerRespawnLogic {
   @Nullable
   protected static BlockPos getOverworldRespawnPos(ServerLevel var0, int var1, int var2, boolean var3) {
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos(var1, 0, var2);
      Biome var5 = var0.getBiome(var4);
      boolean var6 = var0.dimensionType().hasCeiling();
      BlockState var7 = var5.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial();
      if (var3 && !var7.getBlock().is((Tag)BlockTags.VALID_SPAWN)) {
         return null;
      } else {
         LevelChunk var8 = var0.getChunk(var1 >> 4, var2 >> 4);
         int var9 = var6 ? var0.getChunkSource().getGenerator().getSpawnHeight() : var8.getHeight(Heightmap.Types.MOTION_BLOCKING, var1 & 15, var2 & 15);
         if (var9 < 0) {
            return null;
         } else {
            int var10 = var8.getHeight(Heightmap.Types.WORLD_SURFACE, var1 & 15, var2 & 15);
            if (var10 <= var9 && var10 > var8.getHeight(Heightmap.Types.OCEAN_FLOOR, var1 & 15, var2 & 15)) {
               return null;
            } else {
               for(int var11 = var9 + 1; var11 >= 0; --var11) {
                  var4.set(var1, var11, var2);
                  BlockState var12 = var0.getBlockState(var4);
                  if (!var12.getFluidState().isEmpty()) {
                     break;
                  }

                  if (var12.equals(var7)) {
                     return var4.above().immutable();
                  }
               }

               return null;
            }
         }
      }
   }

   @Nullable
   public static BlockPos getSpawnPosInChunk(ServerLevel var0, ChunkPos var1, boolean var2) {
      for(int var3 = var1.getMinBlockX(); var3 <= var1.getMaxBlockX(); ++var3) {
         for(int var4 = var1.getMinBlockZ(); var4 <= var1.getMaxBlockZ(); ++var4) {
            BlockPos var5 = getOverworldRespawnPos(var0, var3, var4, var2);
            if (var5 != null) {
               return var5;
            }
         }
      }

      return null;
   }
}
