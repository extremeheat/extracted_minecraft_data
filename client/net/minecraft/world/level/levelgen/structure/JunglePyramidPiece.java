package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TripWireBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class JunglePyramidPiece extends ScatteredFeaturePiece {
   private boolean placedMainChest;
   private boolean placedHiddenChest;
   private boolean placedTrap1;
   private boolean placedTrap2;
   private static final JunglePyramidPiece.MossStoneSelector STONE_SELECTOR = new JunglePyramidPiece.MossStoneSelector();

   public JunglePyramidPiece(Random var1, int var2, int var3) {
      super(StructurePieceType.JUNGLE_PYRAMID_PIECE, var1, var2, 64, var3, 12, 10, 15);
   }

   public JunglePyramidPiece(StructureManager var1, CompoundTag var2) {
      super(StructurePieceType.JUNGLE_PYRAMID_PIECE, var2);
      this.placedMainChest = var2.getBoolean("placedMainChest");
      this.placedHiddenChest = var2.getBoolean("placedHiddenChest");
      this.placedTrap1 = var2.getBoolean("placedTrap1");
      this.placedTrap2 = var2.getBoolean("placedTrap2");
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("placedMainChest", this.placedMainChest);
      var1.putBoolean("placedHiddenChest", this.placedHiddenChest);
      var1.putBoolean("placedTrap1", this.placedTrap1);
      var1.putBoolean("placedTrap2", this.placedTrap2);
   }

   public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      if (!this.updateAverageGroundHeight(var1, var5, 0)) {
         return false;
      } else {
         this.generateBox(var1, var5, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 1, 2, 9, 2, 2, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 1, 12, 9, 2, 12, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 1, 3, 2, 2, 11, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 9, 1, 3, 9, 2, 11, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 3, 1, 10, 6, 1, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 3, 13, 10, 6, 13, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 3, 2, 1, 6, 12, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 10, 3, 2, 10, 6, 12, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 3, 2, 9, 3, 12, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 6, 2, 9, 6, 12, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 3, 7, 3, 8, 7, 11, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 8, 4, 7, 8, 10, false, var4, STONE_SELECTOR);
         this.generateAirBox(var1, var5, 3, 1, 3, 8, 2, 11);
         this.generateAirBox(var1, var5, 4, 3, 6, 7, 3, 9);
         this.generateAirBox(var1, var5, 2, 4, 2, 9, 5, 12);
         this.generateAirBox(var1, var5, 4, 6, 5, 7, 6, 9);
         this.generateAirBox(var1, var5, 5, 7, 6, 6, 7, 8);
         this.generateAirBox(var1, var5, 5, 1, 2, 6, 2, 2);
         this.generateAirBox(var1, var5, 5, 2, 12, 6, 2, 12);
         this.generateAirBox(var1, var5, 5, 5, 1, 6, 5, 1);
         this.generateAirBox(var1, var5, 5, 5, 13, 6, 5, 13);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 1, 5, 5, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, 5, 5, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 1, 5, 9, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, 5, 9, var5);

         int var8;
         for(var8 = 0; var8 <= 14; var8 += 14) {
            this.generateBox(var1, var5, 2, 4, var8, 2, 5, var8, false, var4, STONE_SELECTOR);
            this.generateBox(var1, var5, 4, 4, var8, 4, 5, var8, false, var4, STONE_SELECTOR);
            this.generateBox(var1, var5, 7, 4, var8, 7, 5, var8, false, var4, STONE_SELECTOR);
            this.generateBox(var1, var5, 9, 4, var8, 9, 5, var8, false, var4, STONE_SELECTOR);
         }

         this.generateBox(var1, var5, 5, 6, 0, 6, 6, 0, false, var4, STONE_SELECTOR);

         for(var8 = 0; var8 <= 11; var8 += 11) {
            for(int var9 = 2; var9 <= 12; var9 += 2) {
               this.generateBox(var1, var5, var8, 4, var9, var8, 5, var9, false, var4, STONE_SELECTOR);
            }

            this.generateBox(var1, var5, var8, 6, 5, var8, 6, 5, false, var4, STONE_SELECTOR);
            this.generateBox(var1, var5, var8, 6, 9, var8, 6, 9, false, var4, STONE_SELECTOR);
         }

         this.generateBox(var1, var5, 2, 7, 2, 2, 9, 2, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 9, 7, 2, 9, 9, 2, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 7, 12, 2, 9, 12, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 9, 7, 12, 9, 9, 12, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 9, 4, 4, 9, 4, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 7, 9, 4, 7, 9, 4, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 9, 10, 4, 9, 10, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 7, 9, 10, 7, 9, 10, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 5, 9, 7, 6, 9, 7, false, var4, STONE_SELECTOR);
         BlockState var14 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState var15 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         BlockState var10 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState var11 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         this.placeBlock(var1, var11, 5, 9, 6, var5);
         this.placeBlock(var1, var11, 6, 9, 6, var5);
         this.placeBlock(var1, var10, 5, 9, 8, var5);
         this.placeBlock(var1, var10, 6, 9, 8, var5);
         this.placeBlock(var1, var11, 4, 0, 0, var5);
         this.placeBlock(var1, var11, 5, 0, 0, var5);
         this.placeBlock(var1, var11, 6, 0, 0, var5);
         this.placeBlock(var1, var11, 7, 0, 0, var5);
         this.placeBlock(var1, var11, 4, 1, 8, var5);
         this.placeBlock(var1, var11, 4, 2, 9, var5);
         this.placeBlock(var1, var11, 4, 3, 10, var5);
         this.placeBlock(var1, var11, 7, 1, 8, var5);
         this.placeBlock(var1, var11, 7, 2, 9, var5);
         this.placeBlock(var1, var11, 7, 3, 10, var5);
         this.generateBox(var1, var5, 4, 1, 9, 4, 1, 9, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 7, 1, 9, 7, 1, 9, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 1, 10, 7, 2, 10, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 5, 4, 5, 6, 4, 5, false, var4, STONE_SELECTOR);
         this.placeBlock(var1, var14, 4, 4, 5, var5);
         this.placeBlock(var1, var15, 7, 4, 5, var5);

         int var12;
         for(var12 = 0; var12 < 4; ++var12) {
            this.placeBlock(var1, var10, 5, 0 - var12, 6 + var12, var5);
            this.placeBlock(var1, var10, 6, 0 - var12, 6 + var12, var5);
            this.generateAirBox(var1, var5, 5, 0 - var12, 7 + var12, 6, 0 - var12, 9 + var12);
         }

         this.generateAirBox(var1, var5, 1, -3, 12, 10, -1, 13);
         this.generateAirBox(var1, var5, 1, -3, 1, 3, -1, 13);
         this.generateAirBox(var1, var5, 1, -3, 1, 9, -1, 5);

         for(var12 = 1; var12 <= 13; var12 += 2) {
            this.generateBox(var1, var5, 1, -3, var12, 1, -2, var12, false, var4, STONE_SELECTOR);
         }

         for(var12 = 2; var12 <= 12; var12 += 2) {
            this.generateBox(var1, var5, 1, -1, var12, 3, -1, var12, false, var4, STONE_SELECTOR);
         }

         this.generateBox(var1, var5, 2, -2, 1, 5, -2, 1, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 7, -2, 1, 9, -2, 1, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 6, -3, 1, 6, -3, 1, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 6, -1, 1, 6, -1, 1, false, var4, STONE_SELECTOR);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.EAST)).setValue(TripWireHookBlock.ATTACHED, true), 1, -3, 8, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.WEST)).setValue(TripWireHookBlock.ATTACHED, true), 4, -3, 8, var5);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, true)).setValue(TripWireBlock.WEST, true)).setValue(TripWireBlock.ATTACHED, true), 2, -3, 8, var5);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, true)).setValue(TripWireBlock.WEST, true)).setValue(TripWireBlock.ATTACHED, true), 3, -3, 8, var5);
         BlockState var16 = (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE);
         this.placeBlock(var1, var16, 5, -3, 7, var5);
         this.placeBlock(var1, var16, 5, -3, 6, var5);
         this.placeBlock(var1, var16, 5, -3, 5, var5);
         this.placeBlock(var1, var16, 5, -3, 4, var5);
         this.placeBlock(var1, var16, 5, -3, 3, var5);
         this.placeBlock(var1, var16, 5, -3, 2, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 5, -3, 1, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 4, -3, 1, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3, -3, 1, var5);
         if (!this.placedTrap1) {
            this.placedTrap1 = this.createDispenser(var1, var5, var4, 3, -2, 1, Direction.NORTH, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
         }

         this.placeBlock(var1, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.SOUTH, true), 3, -2, 2, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.NORTH)).setValue(TripWireHookBlock.ATTACHED, true), 7, -3, 1, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.SOUTH)).setValue(TripWireHookBlock.ATTACHED, true), 7, -3, 5, var5);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 2, var5);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 3, var5);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 4, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 8, -3, 6, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 9, -3, 6, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.UP), 9, -3, 5, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 4, var5);
         this.placeBlock(var1, var16, 9, -2, 4, var5);
         if (!this.placedTrap2) {
            this.placedTrap2 = this.createDispenser(var1, var5, var4, 9, -2, 3, Direction.WEST, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
         }

         this.placeBlock(var1, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true), 8, -1, 3, var5);
         this.placeBlock(var1, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true), 8, -2, 3, var5);
         if (!this.placedMainChest) {
            this.placedMainChest = this.createChest(var1, var5, var4, 8, -3, 3, BuiltInLootTables.JUNGLE_TEMPLE);
         }

         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 2, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 1, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 4, -3, 5, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -2, 5, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -1, 5, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 6, -3, 5, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -2, 5, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -1, 5, var5);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 5, var5);
         this.generateBox(var1, var5, 9, -1, 1, 9, -1, 5, false, var4, STONE_SELECTOR);
         this.generateAirBox(var1, var5, 8, -3, 8, 10, -1, 10);
         this.placeBlock(var1, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 8, -2, 11, var5);
         this.placeBlock(var1, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 9, -2, 11, var5);
         this.placeBlock(var1, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 10, -2, 11, var5);
         BlockState var13 = (BlockState)((BlockState)Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.NORTH)).setValue(LeverBlock.FACE, AttachFace.WALL);
         this.placeBlock(var1, var13, 8, -2, 12, var5);
         this.placeBlock(var1, var13, 9, -2, 12, var5);
         this.placeBlock(var1, var13, 10, -2, 12, var5);
         this.generateBox(var1, var5, 8, -3, 8, 8, -3, 10, false, var4, STONE_SELECTOR);
         this.generateBox(var1, var5, 10, -3, 8, 10, -3, 10, false, var4, STONE_SELECTOR);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -2, 9, var5);
         this.placeBlock(var1, var16, 8, -2, 9, var5);
         this.placeBlock(var1, var16, 8, -2, 10, var5);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 10, -1, 9, var5);
         this.placeBlock(var1, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.UP), 9, -2, 8, var5);
         this.placeBlock(var1, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -2, 8, var5);
         this.placeBlock(var1, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -1, 8, var5);
         this.placeBlock(var1, (BlockState)Blocks.REPEATER.defaultBlockState().setValue(RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, var5);
         if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.createChest(var1, var5, var4, 9, -3, 10, BuiltInLootTables.JUNGLE_TEMPLE);
         }

         return true;
      }
   }

   static class MossStoneSelector extends StructurePiece.BlockSelector {
      private MossStoneSelector() {
         super();
      }

      public void next(Random var1, int var2, int var3, int var4, boolean var5) {
         if (var1.nextFloat() < 0.4F) {
            this.next = Blocks.COBBLESTONE.defaultBlockState();
         } else {
            this.next = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
         }

      }

      // $FF: synthetic method
      MossStoneSelector(Object var1) {
         this();
      }
   }
}
