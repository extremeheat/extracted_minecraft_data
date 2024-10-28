package net.minecraft.world.level.levelgen.structure.structures;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class DesertPyramidPiece extends ScatteredFeaturePiece {
   public static final int WIDTH = 21;
   public static final int DEPTH = 21;
   private final boolean[] hasPlacedChest = new boolean[4];
   private final List<BlockPos> potentialSuspiciousSandWorldPositions = new ArrayList();
   private BlockPos randomCollapsedRoofPos;

   public DesertPyramidPiece(RandomSource var1, int var2, int var3) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, var2, 64, var3, 21, 15, 21, getRandomHorizontalDirection(var1));
      this.randomCollapsedRoofPos = BlockPos.ZERO;
   }

   public DesertPyramidPiece(CompoundTag var1) {
      super(StructurePieceType.DESERT_PYRAMID_PIECE, var1);
      this.randomCollapsedRoofPos = BlockPos.ZERO;
      this.hasPlacedChest[0] = var1.getBoolean("hasPlacedChest0");
      this.hasPlacedChest[1] = var1.getBoolean("hasPlacedChest1");
      this.hasPlacedChest[2] = var1.getBoolean("hasPlacedChest2");
      this.hasPlacedChest[3] = var1.getBoolean("hasPlacedChest3");
   }

   protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      super.addAdditionalSaveData(var1, var2);
      var2.putBoolean("hasPlacedChest0", this.hasPlacedChest[0]);
      var2.putBoolean("hasPlacedChest1", this.hasPlacedChest[1]);
      var2.putBoolean("hasPlacedChest2", this.hasPlacedChest[2]);
      var2.putBoolean("hasPlacedChest3", this.hasPlacedChest[3]);
   }

   public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      if (this.updateHeightPositionToLowestGroundHeight(var1, -var4.nextInt(3))) {
         this.generateBox(var1, var5, 0, -4, 0, this.width - 1, 0, this.depth - 1, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);

         int var8;
         for(var8 = 1; var8 <= 9; ++var8) {
            this.generateBox(var1, var5, var8, var8, var8, this.width - 1 - var8, var8, this.depth - 1 - var8, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
            this.generateBox(var1, var5, var8 + 1, var8, var8 + 1, this.width - 2 - var8, var8, this.depth - 2 - var8, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         }

         for(var8 = 0; var8 < this.width; ++var8) {
            for(int var9 = 0; var9 < this.depth; ++var9) {
               boolean var10 = true;
               this.fillColumnDown(var1, Blocks.SANDSTONE.defaultBlockState(), var8, -5, var9, var5);
            }
         }

         BlockState var16 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState var17 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState var18 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState var11 = (BlockState)Blocks.SANDSTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         this.generateBox(var1, var5, 0, 0, 0, 4, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 10, 1, 3, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(var1, var16, 2, 10, 0, var5);
         this.placeBlock(var1, var17, 2, 10, 4, var5);
         this.placeBlock(var1, var18, 0, 10, 2, var5);
         this.placeBlock(var1, var11, 4, 10, 2, var5);
         this.generateBox(var1, var5, this.width - 5, 0, 0, this.width - 1, 9, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 4, 10, 1, this.width - 2, 10, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(var1, var16, this.width - 3, 10, 0, var5);
         this.placeBlock(var1, var17, this.width - 3, 10, 4, var5);
         this.placeBlock(var1, var18, this.width - 5, 10, 2, var5);
         this.placeBlock(var1, var11, this.width - 1, 10, 2, var5);
         this.generateBox(var1, var5, 8, 0, 0, 12, 4, 4, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, 1, 0, 11, 3, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 1, 1, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 2, 1, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 9, 3, 1, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, 3, 1, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 3, 1, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 2, 1, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 11, 1, 1, var5);
         this.generateBox(var1, var5, 4, 1, 1, 8, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 1, 2, 8, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 12, 1, 1, 16, 3, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 12, 1, 2, 16, 2, 2, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 4, 5, this.width - 6, 4, this.depth - 6, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, 4, 9, 11, 4, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 1, 8, 8, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 12, 1, 8, 12, 3, 8, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 1, 12, 8, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 12, 1, 12, 12, 3, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 1, 5, 4, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 5, 1, 5, this.width - 2, 4, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 6, 7, 9, 6, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 7, 7, 9, this.width - 7, 7, 11, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 5, 9, 5, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 6, 5, 9, this.width - 6, 7, 11, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 5, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 6, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 6, 6, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), this.width - 6, 5, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), this.width - 6, 6, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), this.width - 7, 6, 10, var5);
         this.generateBox(var1, var5, 2, 4, 4, 2, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 3, 4, 4, this.width - 3, 6, 4, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(var1, var16, 2, 4, 5, var5);
         this.placeBlock(var1, var16, 2, 3, 4, var5);
         this.placeBlock(var1, var16, this.width - 3, 4, 5, var5);
         this.placeBlock(var1, var16, this.width - 3, 3, 4, var5);
         this.generateBox(var1, var5, 1, 1, 3, 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 3, 1, 3, this.width - 2, 2, 3, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.SANDSTONE.defaultBlockState(), 1, 1, 2, var5);
         this.placeBlock(var1, Blocks.SANDSTONE.defaultBlockState(), this.width - 2, 1, 2, var5);
         this.placeBlock(var1, Blocks.SANDSTONE_SLAB.defaultBlockState(), 1, 2, 2, var5);
         this.placeBlock(var1, Blocks.SANDSTONE_SLAB.defaultBlockState(), this.width - 2, 2, 2, var5);
         this.placeBlock(var1, var11, 2, 1, 2, var5);
         this.placeBlock(var1, var18, this.width - 3, 1, 2, var5);
         this.generateBox(var1, var5, 4, 3, 5, 4, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 5, 3, 5, this.width - 5, 3, 17, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 3, 1, 5, 4, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.generateBox(var1, var5, this.width - 6, 1, 5, this.width - 5, 2, 16, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);

         int var12;
         for(var12 = 5; var12 <= 17; var12 += 2) {
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 4, 1, var12, var5);
            this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 4, 2, var12, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), this.width - 5, 1, var12, var5);
            this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), this.width - 5, 2, var12, var5);
         }

         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 7, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 8, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 9, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 9, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 8, 0, 10, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 12, 0, 10, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 7, 0, 10, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 13, 0, 10, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 0, 11, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 0, 11, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 12, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 10, 0, 13, var5);
         this.placeBlock(var1, Blocks.BLUE_TERRACOTTA.defaultBlockState(), 10, 0, 10, var5);

         for(var12 = 0; var12 <= this.width - 1; var12 += this.width - 1) {
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 2, 1, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 2, 2, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 2, 3, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 3, 1, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 3, 2, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 3, 3, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 4, 1, var5);
            this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var12, 4, 2, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 4, 3, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 5, 1, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 5, 2, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 5, 3, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 6, 1, var5);
            this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var12, 6, 2, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 6, 3, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 7, 1, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 7, 2, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 7, 3, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 8, 1, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 8, 2, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 8, 3, var5);
         }

         for(var12 = 2; var12 <= this.width - 3; var12 += this.width - 3 - 2) {
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 - 1, 2, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 2, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 + 1, 2, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 - 1, 3, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 3, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 + 1, 3, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12 - 1, 4, 0, var5);
            this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var12, 4, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12 + 1, 4, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 - 1, 5, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 5, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 + 1, 5, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12 - 1, 6, 0, var5);
            this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), var12, 6, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12 + 1, 6, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12 - 1, 7, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12, 7, 0, var5);
            this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), var12 + 1, 7, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 - 1, 8, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12, 8, 0, var5);
            this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), var12 + 1, 8, 0, var5);
         }

         this.generateBox(var1, var5, 8, 4, 0, 12, 6, 0, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 8, 6, 0, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 12, 6, 0, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 9, 5, 0, var5);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, 5, 0, var5);
         this.placeBlock(var1, Blocks.ORANGE_TERRACOTTA.defaultBlockState(), 11, 5, 0, var5);
         this.generateBox(var1, var5, 8, -14, 8, 12, -11, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, -10, 8, 12, -10, 12, Blocks.CHISELED_SANDSTONE.defaultBlockState(), Blocks.CHISELED_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, -9, 8, 12, -9, 12, Blocks.CUT_SANDSTONE.defaultBlockState(), Blocks.CUT_SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, -8, 8, 12, -1, 12, Blocks.SANDSTONE.defaultBlockState(), Blocks.SANDSTONE.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, -11, 9, 11, -1, 11, Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.STONE_PRESSURE_PLATE.defaultBlockState(), 10, -11, 10, var5);
         this.generateBox(var1, var5, 9, -13, 9, 11, -13, 11, Blocks.TNT.defaultBlockState(), Blocks.AIR.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 8, -11, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 8, -10, 10, var5);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 7, -10, 10, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 7, -11, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 12, -11, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 12, -10, 10, var5);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 13, -10, 10, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 13, -11, 10, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -11, 8, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -10, 8, var5);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 7, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 7, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -11, 12, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, -10, 12, var5);
         this.placeBlock(var1, Blocks.CHISELED_SANDSTONE.defaultBlockState(), 10, -10, 13, var5);
         this.placeBlock(var1, Blocks.CUT_SANDSTONE.defaultBlockState(), 10, -11, 13, var5);
         Iterator var19 = Direction.Plane.HORIZONTAL.iterator();

         while(var19.hasNext()) {
            Direction var13 = (Direction)var19.next();
            if (!this.hasPlacedChest[var13.get2DDataValue()]) {
               int var14 = var13.getStepX() * 2;
               int var15 = var13.getStepZ() * 2;
               this.hasPlacedChest[var13.get2DDataValue()] = this.createChest(var1, var5, var4, 10 + var14, -11, 10 + var15, BuiltInLootTables.DESERT_PYRAMID);
            }
         }

         this.addCellar(var1, var5);
      }
   }

   private void addCellar(WorldGenLevel var1, BoundingBox var2) {
      BlockPos var3 = new BlockPos(16, -4, 13);
      this.addCellarStairs(var3, var1, var2);
      this.addCellarRoom(var3, var1, var2);
   }

   private void addCellarStairs(BlockPos var1, WorldGenLevel var2, BoundingBox var3) {
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();
      BlockState var7 = Blocks.SANDSTONE_STAIRS.defaultBlockState();
      this.placeBlock(var2, var7.rotate(Rotation.COUNTERCLOCKWISE_90), 13, -1, 17, var3);
      this.placeBlock(var2, var7.rotate(Rotation.COUNTERCLOCKWISE_90), 14, -2, 17, var3);
      this.placeBlock(var2, var7.rotate(Rotation.COUNTERCLOCKWISE_90), 15, -3, 17, var3);
      BlockState var8 = Blocks.SAND.defaultBlockState();
      BlockState var9 = Blocks.SANDSTONE.defaultBlockState();
      boolean var10 = var2.getRandom().nextBoolean();
      this.placeBlock(var2, var8, var4 - 4, var5 + 4, var6 + 4, var3);
      this.placeBlock(var2, var8, var4 - 3, var5 + 4, var6 + 4, var3);
      this.placeBlock(var2, var8, var4 - 2, var5 + 4, var6 + 4, var3);
      this.placeBlock(var2, var8, var4 - 1, var5 + 4, var6 + 4, var3);
      this.placeBlock(var2, var8, var4, var5 + 4, var6 + 4, var3);
      this.placeBlock(var2, var8, var4 - 2, var5 + 3, var6 + 4, var3);
      this.placeBlock(var2, var10 ? var8 : var9, var4 - 1, var5 + 3, var6 + 4, var3);
      this.placeBlock(var2, !var10 ? var8 : var9, var4, var5 + 3, var6 + 4, var3);
      this.placeBlock(var2, var8, var4 - 1, var5 + 2, var6 + 4, var3);
      this.placeBlock(var2, var9, var4, var5 + 2, var6 + 4, var3);
      this.placeBlock(var2, var8, var4, var5 + 1, var6 + 4, var3);
   }

   private void addCellarRoom(BlockPos var1, WorldGenLevel var2, BoundingBox var3) {
      int var4 = var1.getX();
      int var5 = var1.getY();
      int var6 = var1.getZ();
      BlockState var7 = Blocks.CUT_SANDSTONE.defaultBlockState();
      BlockState var8 = Blocks.CHISELED_SANDSTONE.defaultBlockState();
      this.generateBox(var2, var3, var4 - 3, var5 + 1, var6 - 3, var4 - 3, var5 + 1, var6 + 2, var7, var7, true);
      this.generateBox(var2, var3, var4 + 3, var5 + 1, var6 - 3, var4 + 3, var5 + 1, var6 + 2, var7, var7, true);
      this.generateBox(var2, var3, var4 - 3, var5 + 1, var6 - 3, var4 + 3, var5 + 1, var6 - 2, var7, var7, true);
      this.generateBox(var2, var3, var4 - 3, var5 + 1, var6 + 3, var4 + 3, var5 + 1, var6 + 3, var7, var7, true);
      this.generateBox(var2, var3, var4 - 3, var5 + 2, var6 - 3, var4 - 3, var5 + 2, var6 + 2, var8, var8, true);
      this.generateBox(var2, var3, var4 + 3, var5 + 2, var6 - 3, var4 + 3, var5 + 2, var6 + 2, var8, var8, true);
      this.generateBox(var2, var3, var4 - 3, var5 + 2, var6 - 3, var4 + 3, var5 + 2, var6 - 2, var8, var8, true);
      this.generateBox(var2, var3, var4 - 3, var5 + 2, var6 + 3, var4 + 3, var5 + 2, var6 + 3, var8, var8, true);
      this.generateBox(var2, var3, var4 - 3, -1, var6 - 3, var4 - 3, -1, var6 + 2, var7, var7, true);
      this.generateBox(var2, var3, var4 + 3, -1, var6 - 3, var4 + 3, -1, var6 + 2, var7, var7, true);
      this.generateBox(var2, var3, var4 - 3, -1, var6 - 3, var4 + 3, -1, var6 - 2, var7, var7, true);
      this.generateBox(var2, var3, var4 - 3, -1, var6 + 3, var4 + 3, -1, var6 + 3, var7, var7, true);
      this.placeSandBox(var4 - 2, var5 + 1, var6 - 2, var4 + 2, var5 + 3, var6 + 2);
      this.placeCollapsedRoof(var2, var3, var4 - 2, var5 + 4, var6 - 2, var4 + 2, var6 + 2);
      BlockState var9 = Blocks.ORANGE_TERRACOTTA.defaultBlockState();
      BlockState var10 = Blocks.BLUE_TERRACOTTA.defaultBlockState();
      this.placeBlock(var2, var10, var4, var5, var6, var3);
      this.placeBlock(var2, var9, var4 + 1, var5, var6 - 1, var3);
      this.placeBlock(var2, var9, var4 + 1, var5, var6 + 1, var3);
      this.placeBlock(var2, var9, var4 - 1, var5, var6 - 1, var3);
      this.placeBlock(var2, var9, var4 - 1, var5, var6 + 1, var3);
      this.placeBlock(var2, var9, var4 + 2, var5, var6, var3);
      this.placeBlock(var2, var9, var4 - 2, var5, var6, var3);
      this.placeBlock(var2, var9, var4, var5, var6 + 2, var3);
      this.placeBlock(var2, var9, var4, var5, var6 - 2, var3);
      this.placeBlock(var2, var9, var4 + 3, var5, var6, var3);
      this.placeSand(var4 + 3, var5 + 1, var6);
      this.placeSand(var4 + 3, var5 + 2, var6);
      this.placeBlock(var2, var7, var4 + 4, var5 + 1, var6, var3);
      this.placeBlock(var2, var8, var4 + 4, var5 + 2, var6, var3);
      this.placeBlock(var2, var9, var4 - 3, var5, var6, var3);
      this.placeSand(var4 - 3, var5 + 1, var6);
      this.placeSand(var4 - 3, var5 + 2, var6);
      this.placeBlock(var2, var7, var4 - 4, var5 + 1, var6, var3);
      this.placeBlock(var2, var8, var4 - 4, var5 + 2, var6, var3);
      this.placeBlock(var2, var9, var4, var5, var6 + 3, var3);
      this.placeSand(var4, var5 + 1, var6 + 3);
      this.placeSand(var4, var5 + 2, var6 + 3);
      this.placeBlock(var2, var9, var4, var5, var6 - 3, var3);
      this.placeSand(var4, var5 + 1, var6 - 3);
      this.placeSand(var4, var5 + 2, var6 - 3);
      this.placeBlock(var2, var7, var4, var5 + 1, var6 - 4, var3);
      this.placeBlock(var2, var8, var4, -2, var6 - 4, var3);
   }

   private void placeSand(int var1, int var2, int var3) {
      BlockPos.MutableBlockPos var4 = this.getWorldPos(var1, var2, var3);
      this.potentialSuspiciousSandWorldPositions.add(var4);
   }

   private void placeSandBox(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = var2; var7 <= var5; ++var7) {
         for(int var8 = var1; var8 <= var4; ++var8) {
            for(int var9 = var3; var9 <= var6; ++var9) {
               this.placeSand(var8, var7, var9);
            }
         }
      }

   }

   private void placeCollapsedRoofPiece(WorldGenLevel var1, int var2, int var3, int var4, BoundingBox var5) {
      BlockState var6;
      if (var1.getRandom().nextFloat() < 0.33F) {
         var6 = Blocks.SANDSTONE.defaultBlockState();
         this.placeBlock(var1, var6, var2, var3, var4, var5);
      } else {
         var6 = Blocks.SAND.defaultBlockState();
         this.placeBlock(var1, var6, var2, var3, var4, var5);
      }

   }

   private void placeCollapsedRoof(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7) {
      int var9;
      for(int var8 = var3; var8 <= var6; ++var8) {
         for(var9 = var5; var9 <= var7; ++var9) {
            this.placeCollapsedRoofPiece(var1, var8, var4, var9, var2);
         }
      }

      RandomSource var11 = RandomSource.create(var1.getSeed()).forkPositional().at(this.getWorldPos(var3, var4, var5));
      var9 = var11.nextIntBetweenInclusive(var3, var6);
      int var10 = var11.nextIntBetweenInclusive(var5, var7);
      this.randomCollapsedRoofPos = new BlockPos(this.getWorldX(var9, var10), this.getWorldY(var4), this.getWorldZ(var9, var10));
   }

   public List<BlockPos> getPotentialSuspiciousSandWorldPositions() {
      return this.potentialSuspiciousSandWorldPositions;
   }

   public BlockPos getRandomCollapsedRoofPos() {
      return this.randomCollapsedRoofPos;
   }
}
