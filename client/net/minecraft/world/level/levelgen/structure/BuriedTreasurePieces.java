package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
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

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         int var8 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.boundingBox.x0, this.boundingBox.z0);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(this.boundingBox.x0, var8, this.boundingBox.z0);

         while(var9.getY() > var1.getMinBuildHeight()) {
            BlockState var10 = var1.getBlockState(var9);
            BlockState var11 = var1.getBlockState(var9.below());
            if (var11 == Blocks.SANDSTONE.defaultBlockState() || var11 == Blocks.STONE.defaultBlockState() || var11 == Blocks.ANDESITE.defaultBlockState() || var11 == Blocks.GRANITE.defaultBlockState() || var11 == Blocks.DIORITE.defaultBlockState()) {
               BlockState var12 = !var10.isAir() && !this.isLiquid(var10) ? var10 : Blocks.SAND.defaultBlockState();
               Direction[] var13 = Direction.values();
               int var14 = var13.length;

               for(int var15 = 0; var15 < var14; ++var15) {
                  Direction var16 = var13[var15];
                  BlockPos var17 = var9.relative(var16);
                  BlockState var18 = var1.getBlockState(var17);
                  if (var18.isAir() || this.isLiquid(var18)) {
                     BlockPos var19 = var17.below();
                     BlockState var20 = var1.getBlockState(var19);
                     if ((var20.isAir() || this.isLiquid(var20)) && var16 != Direction.UP) {
                        var1.setBlock(var17, var11, 3);
                     } else {
                        var1.setBlock(var17, var12, 3);
                     }
                  }
               }

               this.boundingBox = new BoundingBox(var9.getX(), var9.getY(), var9.getZ(), var9.getX(), var9.getY(), var9.getZ());
               return this.createChest(var1, var5, var4, var9, BuiltInLootTables.BURIED_TREASURE, (BlockState)null);
            }

            var9.move(0, -1, 0);
         }

         return false;
      }

      private boolean isLiquid(BlockState var1) {
         return var1 == Blocks.WATER.defaultBlockState() || var1 == Blocks.LAVA.defaultBlockState();
      }
   }
}
