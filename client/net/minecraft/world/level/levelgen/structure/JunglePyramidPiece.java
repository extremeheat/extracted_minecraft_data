package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
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

   public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
      if (!this.updateAverageGroundHeight(var1, var3, 0)) {
         return false;
      } else {
         this.generateBox(var1, var3, 0, -4, 0, this.width - 1, 0, this.depth - 1, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 2, 1, 2, 9, 2, 2, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 2, 1, 12, 9, 2, 12, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 2, 1, 3, 2, 2, 11, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 9, 1, 3, 9, 2, 11, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 1, 3, 1, 10, 6, 1, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 1, 3, 13, 10, 6, 13, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 1, 3, 2, 1, 6, 12, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 10, 3, 2, 10, 6, 12, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 2, 3, 2, 9, 3, 12, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 2, 6, 2, 9, 6, 12, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 3, 7, 3, 8, 7, 11, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 4, 8, 4, 7, 8, 10, false, var2, STONE_SELECTOR);
         this.generateAirBox(var1, var3, 3, 1, 3, 8, 2, 11);
         this.generateAirBox(var1, var3, 4, 3, 6, 7, 3, 9);
         this.generateAirBox(var1, var3, 2, 4, 2, 9, 5, 12);
         this.generateAirBox(var1, var3, 4, 6, 5, 7, 6, 9);
         this.generateAirBox(var1, var3, 5, 7, 6, 6, 7, 8);
         this.generateAirBox(var1, var3, 5, 1, 2, 6, 2, 2);
         this.generateAirBox(var1, var3, 5, 2, 12, 6, 2, 12);
         this.generateAirBox(var1, var3, 5, 5, 1, 6, 5, 1);
         this.generateAirBox(var1, var3, 5, 5, 13, 6, 5, 13);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 1, 5, 5, var3);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, 5, 5, var3);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 1, 5, 9, var3);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 10, 5, 9, var3);

         int var5;
         for(var5 = 0; var5 <= 14; var5 += 14) {
            this.generateBox(var1, var3, 2, 4, var5, 2, 5, var5, false, var2, STONE_SELECTOR);
            this.generateBox(var1, var3, 4, 4, var5, 4, 5, var5, false, var2, STONE_SELECTOR);
            this.generateBox(var1, var3, 7, 4, var5, 7, 5, var5, false, var2, STONE_SELECTOR);
            this.generateBox(var1, var3, 9, 4, var5, 9, 5, var5, false, var2, STONE_SELECTOR);
         }

         this.generateBox(var1, var3, 5, 6, 0, 6, 6, 0, false, var2, STONE_SELECTOR);

         for(var5 = 0; var5 <= 11; var5 += 11) {
            for(int var6 = 2; var6 <= 12; var6 += 2) {
               this.generateBox(var1, var3, var5, 4, var6, var5, 5, var6, false, var2, STONE_SELECTOR);
            }

            this.generateBox(var1, var3, var5, 6, 5, var5, 6, 5, false, var2, STONE_SELECTOR);
            this.generateBox(var1, var3, var5, 6, 9, var5, 6, 9, false, var2, STONE_SELECTOR);
         }

         this.generateBox(var1, var3, 2, 7, 2, 2, 9, 2, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 9, 7, 2, 9, 9, 2, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 2, 7, 12, 2, 9, 12, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 9, 7, 12, 9, 9, 12, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 4, 9, 4, 4, 9, 4, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 7, 9, 4, 7, 9, 4, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 4, 9, 10, 4, 9, 10, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 7, 9, 10, 7, 9, 10, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 5, 9, 7, 6, 9, 7, false, var2, STONE_SELECTOR);
         BlockState var11 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState var12 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         BlockState var7 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         BlockState var8 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         this.placeBlock(var1, var8, 5, 9, 6, var3);
         this.placeBlock(var1, var8, 6, 9, 6, var3);
         this.placeBlock(var1, var7, 5, 9, 8, var3);
         this.placeBlock(var1, var7, 6, 9, 8, var3);
         this.placeBlock(var1, var8, 4, 0, 0, var3);
         this.placeBlock(var1, var8, 5, 0, 0, var3);
         this.placeBlock(var1, var8, 6, 0, 0, var3);
         this.placeBlock(var1, var8, 7, 0, 0, var3);
         this.placeBlock(var1, var8, 4, 1, 8, var3);
         this.placeBlock(var1, var8, 4, 2, 9, var3);
         this.placeBlock(var1, var8, 4, 3, 10, var3);
         this.placeBlock(var1, var8, 7, 1, 8, var3);
         this.placeBlock(var1, var8, 7, 2, 9, var3);
         this.placeBlock(var1, var8, 7, 3, 10, var3);
         this.generateBox(var1, var3, 4, 1, 9, 4, 1, 9, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 7, 1, 9, 7, 1, 9, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 4, 1, 10, 7, 2, 10, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 5, 4, 5, 6, 4, 5, false, var2, STONE_SELECTOR);
         this.placeBlock(var1, var11, 4, 4, 5, var3);
         this.placeBlock(var1, var12, 7, 4, 5, var3);

         int var9;
         for(var9 = 0; var9 < 4; ++var9) {
            this.placeBlock(var1, var7, 5, 0 - var9, 6 + var9, var3);
            this.placeBlock(var1, var7, 6, 0 - var9, 6 + var9, var3);
            this.generateAirBox(var1, var3, 5, 0 - var9, 7 + var9, 6, 0 - var9, 9 + var9);
         }

         this.generateAirBox(var1, var3, 1, -3, 12, 10, -1, 13);
         this.generateAirBox(var1, var3, 1, -3, 1, 3, -1, 13);
         this.generateAirBox(var1, var3, 1, -3, 1, 9, -1, 5);

         for(var9 = 1; var9 <= 13; var9 += 2) {
            this.generateBox(var1, var3, 1, -3, var9, 1, -2, var9, false, var2, STONE_SELECTOR);
         }

         for(var9 = 2; var9 <= 12; var9 += 2) {
            this.generateBox(var1, var3, 1, -1, var9, 3, -1, var9, false, var2, STONE_SELECTOR);
         }

         this.generateBox(var1, var3, 2, -2, 1, 5, -2, 1, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 7, -2, 1, 9, -2, 1, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 6, -3, 1, 6, -3, 1, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 6, -1, 1, 6, -1, 1, false, var2, STONE_SELECTOR);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.EAST)).setValue(TripWireHookBlock.ATTACHED, true), 1, -3, 8, var3);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.WEST)).setValue(TripWireHookBlock.ATTACHED, true), 4, -3, 8, var3);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, true)).setValue(TripWireBlock.WEST, true)).setValue(TripWireBlock.ATTACHED, true), 2, -3, 8, var3);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.EAST, true)).setValue(TripWireBlock.WEST, true)).setValue(TripWireBlock.ATTACHED, true), 3, -3, 8, var3);
         BlockState var13 = (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE);
         this.placeBlock(var1, (BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 5, -3, 7, var3);
         this.placeBlock(var1, var13, 5, -3, 6, var3);
         this.placeBlock(var1, var13, 5, -3, 5, var3);
         this.placeBlock(var1, var13, 5, -3, 4, var3);
         this.placeBlock(var1, var13, 5, -3, 3, var3);
         this.placeBlock(var1, var13, 5, -3, 2, var3);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE), 5, -3, 1, var3);
         this.placeBlock(var1, (BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE), 4, -3, 1, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 3, -3, 1, var3);
         if (!this.placedTrap1) {
            this.placedTrap1 = this.createDispenser(var1, var3, var2, 3, -2, 1, Direction.NORTH, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
         }

         this.placeBlock(var1, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.SOUTH, true), 3, -2, 2, var3);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.NORTH)).setValue(TripWireHookBlock.ATTACHED, true), 7, -3, 1, var3);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.TRIPWIRE_HOOK.defaultBlockState().setValue(TripWireHookBlock.FACING, Direction.SOUTH)).setValue(TripWireHookBlock.ATTACHED, true), 7, -3, 5, var3);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 2, var3);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 3, var3);
         this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.TRIPWIRE.defaultBlockState().setValue(TripWireBlock.NORTH, true)).setValue(TripWireBlock.SOUTH, true)).setValue(TripWireBlock.ATTACHED, true), 7, -3, 4, var3);
         this.placeBlock(var1, (BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.EAST, RedstoneSide.SIDE), 8, -3, 6, var3);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.WEST, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 9, -3, 6, var3);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE)).setValue(RedStoneWireBlock.SOUTH, RedstoneSide.UP), 9, -3, 5, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 4, var3);
         this.placeBlock(var1, (BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE), 9, -2, 4, var3);
         if (!this.placedTrap2) {
            this.placedTrap2 = this.createDispenser(var1, var3, var2, 9, -2, 3, Direction.WEST, BuiltInLootTables.JUNGLE_TEMPLE_DISPENSER);
         }

         this.placeBlock(var1, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true), 8, -1, 3, var3);
         this.placeBlock(var1, (BlockState)Blocks.VINE.defaultBlockState().setValue(VineBlock.EAST, true), 8, -2, 3, var3);
         if (!this.placedMainChest) {
            this.placedMainChest = this.createChest(var1, var3, var2, 8, -3, 3, BuiltInLootTables.JUNGLE_TEMPLE);
         }

         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 9, -3, 2, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 1, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 4, -3, 5, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -2, 5, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 5, -1, 5, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 6, -3, 5, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -2, 5, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 7, -1, 5, var3);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 8, -3, 5, var3);
         this.generateBox(var1, var3, 9, -1, 1, 9, -1, 5, false, var2, STONE_SELECTOR);
         this.generateAirBox(var1, var3, 8, -3, 8, 10, -1, 10);
         this.placeBlock(var1, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 8, -2, 11, var3);
         this.placeBlock(var1, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 9, -2, 11, var3);
         this.placeBlock(var1, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 10, -2, 11, var3);
         BlockState var10 = (BlockState)((BlockState)Blocks.LEVER.defaultBlockState().setValue(LeverBlock.FACING, Direction.NORTH)).setValue(LeverBlock.FACE, AttachFace.WALL);
         this.placeBlock(var1, var10, 8, -2, 12, var3);
         this.placeBlock(var1, var10, 9, -2, 12, var3);
         this.placeBlock(var1, var10, 10, -2, 12, var3);
         this.generateBox(var1, var3, 8, -3, 8, 8, -3, 10, false, var2, STONE_SELECTOR);
         this.generateBox(var1, var3, 10, -3, 8, 10, -3, 10, false, var2, STONE_SELECTOR);
         this.placeBlock(var1, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 10, -2, 9, var3);
         this.placeBlock(var1, (BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.NORTH, RedstoneSide.SIDE), 8, -2, 9, var3);
         this.placeBlock(var1, (BlockState)Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.SOUTH, RedstoneSide.SIDE), 8, -2, 10, var3);
         this.placeBlock(var1, Blocks.REDSTONE_WIRE.defaultBlockState(), 10, -1, 9, var3);
         this.placeBlock(var1, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.UP), 9, -2, 8, var3);
         this.placeBlock(var1, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -2, 8, var3);
         this.placeBlock(var1, (BlockState)Blocks.STICKY_PISTON.defaultBlockState().setValue(PistonBaseBlock.FACING, Direction.WEST), 10, -1, 8, var3);
         this.placeBlock(var1, (BlockState)Blocks.REPEATER.defaultBlockState().setValue(RepeaterBlock.FACING, Direction.NORTH), 10, -2, 10, var3);
         if (!this.placedHiddenChest) {
            this.placedHiddenChest = this.createChest(var1, var3, var2, 9, -3, 10, BuiltInLootTables.JUNGLE_TEMPLE);
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
