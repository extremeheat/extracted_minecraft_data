package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BuriedTreasurePieces {
   public static class BuriedTreasurePiece extends StructurePiece {
      public BuriedTreasurePiece(BlockPos var1) {
         super(StructurePieceType.BURIED_TREASURE_PIECE, 0);
         this.boundingBox = new BoundingBox(var1.getX(), var1.getY(), var1.getZ(), var1.getX(), var1.getY(), var1.getZ());
      }

      public BuriedTreasurePiece(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.BURIED_TREASURE_PIECE, var2);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
      }

      public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         int var5 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.boundingBox.x0, this.boundingBox.z0);
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos(this.boundingBox.x0, var5, this.boundingBox.z0);

         while(var6.getY() > 0) {
            BlockState var7 = var1.getBlockState(var6);
            BlockState var8 = var1.getBlockState(var6.below());
            if (var8 == Blocks.SANDSTONE.defaultBlockState() || var8 == Blocks.STONE.defaultBlockState() || var8 == Blocks.ANDESITE.defaultBlockState() || var8 == Blocks.GRANITE.defaultBlockState() || var8 == Blocks.DIORITE.defaultBlockState()) {
               BlockState var9 = !var7.isAir() && !this.isLiquid(var7) ? var7 : Blocks.SAND.defaultBlockState();
               Direction[] var10 = Direction.values();
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  Direction var13 = var10[var12];
                  BlockPos var14 = var6.relative(var13);
                  BlockState var15 = var1.getBlockState(var14);
                  if (var15.isAir() || this.isLiquid(var15)) {
                     BlockPos var16 = var14.below();
                     BlockState var17 = var1.getBlockState(var16);
                     if ((var17.isAir() || this.isLiquid(var17)) && var13 != Direction.UP) {
                        var1.setBlock(var14, var8, 3);
                     } else {
                        var1.setBlock(var14, var9, 3);
                     }
                  }
               }

               this.boundingBox = new BoundingBox(var6.getX(), var6.getY(), var6.getZ(), var6.getX(), var6.getY(), var6.getZ());
               return this.createChest(var1, var3, var2, var6, BuiltInLootTables.BURIED_TREASURE, (BlockState)null);
            }

            var6.move(0, -1, 0);
         }

         return false;
      }

      private boolean isLiquid(BlockState var1) {
         return var1 == Blocks.WATER.defaultBlockState() || var1 == Blocks.LAVA.defaultBlockState();
      }
   }
}
