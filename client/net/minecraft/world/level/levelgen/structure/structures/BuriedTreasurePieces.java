package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class BuriedTreasurePieces {
   public BuriedTreasurePieces() {
      super();
   }

   public static class BuriedTreasurePiece extends StructurePiece {
      public BuriedTreasurePiece(BlockPos var1) {
         super(StructurePieceType.BURIED_TREASURE_PIECE, 0, new BoundingBox(var1));
      }

      public BuriedTreasurePiece(CompoundTag var1) {
         super(StructurePieceType.BURIED_TREASURE_PIECE, var1);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         int var8 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, this.boundingBox.minX(), this.boundingBox.minZ());
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(this.boundingBox.minX(), var8, this.boundingBox.minZ());

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

               this.boundingBox = new BoundingBox(var9);
               this.createChest(var1, var5, var4, var9, BuiltInLootTables.BURIED_TREASURE, (BlockState)null);
               return;
            }

            var9.move(0, -1, 0);
         }

      }

      private boolean isLiquid(BlockState var1) {
         return var1 == Blocks.WATER.defaultBlockState() || var1 == Blocks.LAVA.defaultBlockState();
      }
   }
}
