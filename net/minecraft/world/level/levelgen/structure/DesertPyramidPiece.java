package net.minecraft.world.level.levelgen.structure;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidPiece extends ScatteredFeaturePiece {
   private final boolean[] hasPlacedChest = new boolean[4];

   public DesertPyramidPiece(Random var1, int var2, int var3) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, var1, var2, 64, var3, 21, 15, 21);
   }

   public DesertPyramidPiece(StructureManager var1, CompoundTag var2) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, var2);
      this.hasPlacedChest[0] = var2.getBoolean("hasPlacedChest0");
      this.hasPlacedChest[1] = var2.getBoolean("hasPlacedChest1");
      this.hasPlacedChest[2] = var2.getBoolean("hasPlacedChest2");
      this.hasPlacedChest[3] = var2.getBoolean("hasPlacedChest3");
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
      var1.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
      var1.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
      var1.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
   }

   public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
      this.generateBox(var1, var4, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);

      int var6;
      for(var6 = 1; var6 <= 9; ++var6) {
         this.generateBox(var1, var4, var6, var6, var6, this.width - 1 - var6, var6, this.depth - 1 - var6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var4, var6 + 1, var6, var6 + 1, this.width - 2 - var6, var6, this.depth - 2 - var6, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      }

      for(var6 = 0; var6 < this.width; ++var6) {
         for(int var7 = 0; var7 < this.depth; ++var7) {
            boolean var8 = true;
            this.fillColumnDown(var1, Blocks.SANDSTONE.defaultBlockState(), var6, -5, var7, var4);
         }
      }

      BlockState var14 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
      BlockState var15 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
      BlockState var16 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
      BlockState var9 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
      this.generateBox(var1, var4, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(var1, var14, 2, 10, 0, var4);
      this.placeBlock(var1, var15, 2, 10, 4, var4);
      this.placeBlock(var1, var16, 0, 10, 2, var4);
      this.placeBlock(var1, var9, 4, 10, 2, var4);
      this.generateBox(var1, var4, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(var1, var14, this.width - 3, 10, 0, var4);
      this.placeBlock(var1, var15, this.width - 3, 10, 4, var4);
      this.placeBlock(var1, var16, this.width - 5, 10, 2, var4);
      this.placeBlock(var1, var9, this.width - 1, 10, 2, var4);
      this.generateBox(var1, var4, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, var4);
      this.generateBox(var1, var4, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 5, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 6, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 6, 6, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, var4);
      this.generateBox(var1, var4, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(var1, var14, 2, 4, 5, var4);
      this.placeBlock(var1, var14, 2, 3, 4, var4);
      this.placeBlock(var1, var14, this.width - 3, 4, 5, var4);
      this.placeBlock(var1, var14, this.width - 3, 3, 4, var4);
      this.generateBox(var1, var4, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.placeBlock(var1, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, var4);
      this.placeBlock(var1, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, var4);
      this.placeBlock(var1, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, var4);
      this.placeBlock(var1, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, var4);
      this.placeBlock(var1, var9, 2, 1, 2, var4);
      this.placeBlock(var1, var16, this.width - 3, 1, 2, var4);
      this.generateBox(var1, var4, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.generateBox(var1, var4, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

      int var10;
      for(var10 = 5; var10 <= 17; var10 += 2) {
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, var10, var4);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, var10, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, var10, var4);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, var10, var4);
      }

      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, var4);
      this.placeBlock(var1, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, var4);

      for(var10 = 0; var10 <= this.width - 1; var10 += this.width - 1) {
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 2, 1, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 2, 2, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 2, 3, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 3, 1, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 3, 2, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 3, 3, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 4, 1, var4);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var10, 4, 2, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 4, 3, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 5, 1, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 5, 2, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 5, 3, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 6, 1, var4);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var10, 6, 2, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 6, 3, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 7, 1, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 7, 2, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 7, 3, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 8, 1, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 8, 2, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 8, 3, var4);
      }

      for(var10 = 2; var10 <= this.width - 3; var10 += this.width - 3 - 2) {
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 - 1, 2, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 2, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 + 1, 2, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 - 1, 3, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 3, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 + 1, 3, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10 - 1, 4, 0, var4);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var10, 4, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10 + 1, 4, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 - 1, 5, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 5, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 + 1, 5, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10 - 1, 6, 0, var4);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var10, 6, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10 + 1, 6, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10 - 1, 7, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10, 7, 0, var4);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var10 + 1, 7, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 - 1, 8, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10, 8, 0, var4);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var10 + 1, 8, 0, var4);
      }

      this.generateBox(var1, var4, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 8, 6, 0, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 12, 6, 0, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, var4);
      this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, var4);
      this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, var4);
      this.generateBox(var1, var4, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
      this.generateBox(var1, var4, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(var1, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, var4);
      this.generateBox(var1, var4, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 8, -11, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 8, -10, 10, var4);
      this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 12, -11, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 12, -10, 10, var4);
      this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -11, 8, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -10, 8, var4);
      this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -11, 12, var4);
      this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -10, 12, var4);
      this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, var4);
      this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, var4);
      Iterator var17 = Direction.Plane.HORIZONTAL.iterator();

      while(var17.hasNext()) {
         Direction var11 = (Direction)var17.next();
         if (!this.hasPlacedChest[var11.get2DDataValue()]) {
            int var12 = var11.getStepX() * 2;
            int var13 = var11.getStepZ() * 2;
            this.hasPlacedChest[var11.get2DDataValue()] = this.createChest(var1, var4, var3, 10 + var12, -11, 10 + var13, BuiltInLootTables.DESERT_PYRAMID);
         }
      }

      return true;
   }
}
