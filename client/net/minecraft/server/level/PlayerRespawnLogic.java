package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;

public class PlayerRespawnLogic {
   public PlayerRespawnLogic() {
      super();
   }

   @Nullable
   protected static BlockPos getOverworldRespawnPos(ServerLevel var0, int var1, int var2) {
      boolean var3 = var0.dimensionType().hasCeiling();
      LevelChunk var4 = var0.getChunk(SectionPos.blockToSectionCoord(var1), SectionPos.blockToSectionCoord(var2));
      int var5 = var3 ? var0.getChunkSource().getGenerator().getSpawnHeight(var0) : var4.getHeight(Heightmap.Types.MOTION_BLOCKING, var1 & 15, var2 & 15);
      if (var5 < var0.getMinBuildHeight()) {
         return null;
      } else {
         int var6 = var4.getHeight(Heightmap.Types.WORLD_SURFACE, var1 & 15, var2 & 15);
         if (var6 <= var5 && var6 > var4.getHeight(Heightmap.Types.OCEAN_FLOOR, var1 & 15, var2 & 15)) {
            return null;
         } else {
            BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

            for(int var8 = var5 + 1; var8 >= var0.getMinBuildHeight(); --var8) {
               var7.set(var1, var8, var2);
               BlockState var9 = var0.getBlockState(var7);
               if (!var9.getFluidState().isEmpty()) {
                  break;
               }

               if (Block.isFaceFull(var9.getCollisionShape(var0, var7), Direction.UP)) {
                  return var7.above().immutable();
               }
            }

            return null;
         }
      }
   }

   @Nullable
   public static BlockPos getSpawnPosInChunk(ServerLevel var0, ChunkPos var1) {
      if (SharedConstants.debugVoidTerrain(var1)) {
         return null;
      } else {
         for(int var2 = var1.getMinBlockX(); var2 <= var1.getMaxBlockX(); ++var2) {
            for(int var3 = var1.getMinBlockZ(); var3 <= var1.getMaxBlockZ(); ++var3) {
               BlockPos var4 = getOverworldRespawnPos(var0, var2, var3);
               if (var4 != null) {
                  return var4;
               }
            }
         }

         return null;
      }
   }
}
