package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
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

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         int var6 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.boundingBox.x0, this.boundingBox.z0);
         BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos(this.boundingBox.x0, var6, this.boundingBox.z0);

         while(var7.getY() > 0) {
            BlockState var8 = var1.getBlockState(var7);
            BlockState var9 = var1.getBlockState(var7.below());
            if (var9 == Blocks.SANDSTONE.defaultBlockState() || var9 == Blocks.STONE.defaultBlockState() || var9 == Blocks.ANDESITE.defaultBlockState() || var9 == Blocks.GRANITE.defaultBlockState() || var9 == Blocks.DIORITE.defaultBlockState()) {
               BlockState var10 = !var8.isAir() && !this.isLiquid(var8) ? var8 : Blocks.SAND.defaultBlockState();
               Direction[] var11 = Direction.values();
               int var12 = var11.length;

               for(int var13 = 0; var13 < var12; ++var13) {
                  Direction var14 = var11[var13];
                  BlockPos var15 = var7.relative(var14);
                  BlockState var16 = var1.getBlockState(var15);
                  if (var16.isAir() || this.isLiquid(var16)) {
                     BlockPos var17 = var15.below();
                     BlockState var18 = var1.getBlockState(var17);
                     if ((var18.isAir() || this.isLiquid(var18)) && var14 != Direction.UP) {
                        var1.setBlock(var15, var9, 3);
                     } else {
                        var1.setBlock(var15, var10, 3);
                     }
                  }
               }

               this.boundingBox = new BoundingBox(var7.getX(), var7.getY(), var7.getZ(), var7.getX(), var7.getY(), var7.getZ());
               return this.createChest(var1, var4, var3, var7, BuiltInLootTables.BURIED_TREASURE, (BlockState)null);
            }

            var7.move(0, -1, 0);
         }

         return false;
      }

      private boolean isLiquid(BlockState var1) {
         return var1 == Blocks.WATER.defaultBlockState() || var1 == Blocks.LAVA.defaultBlockState();
      }
   }
}
