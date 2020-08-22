package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class StrongholdPieces {
   private static final StrongholdPieces.PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new StrongholdPieces.PieceWeight[]{new StrongholdPieces.PieceWeight(StrongholdPieces.Straight.class, 40, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.PrisonHall.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.LeftTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RightTurn.class, 20, 0), new StrongholdPieces.PieceWeight(StrongholdPieces.RoomCrossing.class, 10, 6), new StrongholdPieces.PieceWeight(StrongholdPieces.StraightStairsDown.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.StairsDown.class, 5, 5), new StrongholdPieces.PieceWeight(StrongholdPieces.FiveCrossing.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.ChestCorridor.class, 5, 4), new StrongholdPieces.PieceWeight(StrongholdPieces.Library.class, 10, 2) {
      public boolean doPlace(int var1) {
         return super.doPlace(var1) && var1 > 4;
      }
   }, new StrongholdPieces.PieceWeight(StrongholdPieces.PortalRoom.class, 20, 1) {
      public boolean doPlace(int var1) {
         return super.doPlace(var1) && var1 > 5;
      }
   }};
   private static List currentPieces;
   private static Class imposedPiece;
   private static int totalWeight;
   private static final StrongholdPieces.SmoothStoneSelector SMOOTH_STONE_SELECTOR = new StrongholdPieces.SmoothStoneSelector();

   public static void resetPieces() {
      currentPieces = Lists.newArrayList();
      StrongholdPieces.PieceWeight[] var0 = STRONGHOLD_PIECE_WEIGHTS;
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         StrongholdPieces.PieceWeight var3 = var0[var2];
         var3.placeCount = 0;
         currentPieces.add(var3);
      }

      imposedPiece = null;
   }

   private static boolean updatePieceWeight() {
      boolean var0 = false;
      totalWeight = 0;

      StrongholdPieces.PieceWeight var2;
      for(Iterator var1 = currentPieces.iterator(); var1.hasNext(); totalWeight += var2.weight) {
         var2 = (StrongholdPieces.PieceWeight)var1.next();
         if (var2.maxPlaceCount > 0 && var2.placeCount < var2.maxPlaceCount) {
            var0 = true;
         }
      }

      return var0;
   }

   private static StrongholdPieces.StrongholdPiece findAndCreatePieceFactory(Class var0, List var1, Random var2, int var3, int var4, int var5, @Nullable Direction var6, int var7) {
      Object var8 = null;
      if (var0 == StrongholdPieces.Straight.class) {
         var8 = StrongholdPieces.Straight.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.PrisonHall.class) {
         var8 = StrongholdPieces.PrisonHall.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.LeftTurn.class) {
         var8 = StrongholdPieces.LeftTurn.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.RightTurn.class) {
         var8 = StrongholdPieces.RightTurn.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.RoomCrossing.class) {
         var8 = StrongholdPieces.RoomCrossing.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.StraightStairsDown.class) {
         var8 = StrongholdPieces.StraightStairsDown.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.StairsDown.class) {
         var8 = StrongholdPieces.StairsDown.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.FiveCrossing.class) {
         var8 = StrongholdPieces.FiveCrossing.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.ChestCorridor.class) {
         var8 = StrongholdPieces.ChestCorridor.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.Library.class) {
         var8 = StrongholdPieces.Library.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StrongholdPieces.PortalRoom.class) {
         var8 = StrongholdPieces.PortalRoom.createPiece(var1, var3, var4, var5, var6, var7);
      }

      return (StrongholdPieces.StrongholdPiece)var8;
   }

   private static StrongholdPieces.StrongholdPiece generatePieceFromSmallDoor(StrongholdPieces.StartPiece var0, List var1, Random var2, int var3, int var4, int var5, Direction var6, int var7) {
      if (!updatePieceWeight()) {
         return null;
      } else {
         if (imposedPiece != null) {
            StrongholdPieces.StrongholdPiece var8 = findAndCreatePieceFactory(imposedPiece, var1, var2, var3, var4, var5, var6, var7);
            imposedPiece = null;
            if (var8 != null) {
               return var8;
            }
         }

         int var13 = 0;

         while(var13 < 5) {
            ++var13;
            int var9 = var2.nextInt(totalWeight);
            Iterator var10 = currentPieces.iterator();

            while(var10.hasNext()) {
               StrongholdPieces.PieceWeight var11 = (StrongholdPieces.PieceWeight)var10.next();
               var9 -= var11.weight;
               if (var9 < 0) {
                  if (!var11.doPlace(var7) || var11 == var0.previousPiece) {
                     break;
                  }

                  StrongholdPieces.StrongholdPiece var12 = findAndCreatePieceFactory(var11.pieceClass, var1, var2, var3, var4, var5, var6, var7);
                  if (var12 != null) {
                     ++var11.placeCount;
                     var0.previousPiece = var11;
                     if (!var11.isValid()) {
                        currentPieces.remove(var11);
                     }

                     return var12;
                  }
               }
            }
         }

         BoundingBox var14 = StrongholdPieces.FillerCorridor.findPieceBox(var1, var2, var3, var4, var5, var6);
         if (var14 != null && var14.y0 > 1) {
            return new StrongholdPieces.FillerCorridor(var7, var14, var6);
         } else {
            return null;
         }
      }
   }

   private static StructurePiece generateAndAddPiece(StrongholdPieces.StartPiece var0, List var1, Random var2, int var3, int var4, int var5, @Nullable Direction var6, int var7) {
      if (var7 > 50) {
         return null;
      } else if (Math.abs(var3 - var0.getBoundingBox().x0) <= 112 && Math.abs(var5 - var0.getBoundingBox().z0) <= 112) {
         StrongholdPieces.StrongholdPiece var8 = generatePieceFromSmallDoor(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.add(var8);
            var0.pendingChildren.add(var8);
         }

         return var8;
      } else {
         return null;
      }
   }

   static class SmoothStoneSelector extends StructurePiece.BlockSelector {
      private SmoothStoneSelector() {
      }

      public void next(Random var1, int var2, int var3, int var4, boolean var5) {
         if (var5) {
            float var6 = var1.nextFloat();
            if (var6 < 0.2F) {
               this.next = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            } else if (var6 < 0.5F) {
               this.next = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            } else if (var6 < 0.55F) {
               this.next = Blocks.INFESTED_STONE_BRICKS.defaultBlockState();
            } else {
               this.next = Blocks.STONE_BRICKS.defaultBlockState();
            }
         } else {
            this.next = Blocks.CAVE_AIR.defaultBlockState();
         }

      }

      // $FF: synthetic method
      SmoothStoneSelector(Object var1) {
         this();
      }
   }

   public static class PortalRoom extends StrongholdPieces.StrongholdPiece {
      private boolean hasPlacedSpawner;

      public PortalRoom(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, var1);
         this.setOrientation(var3);
         this.boundingBox = var2;
      }

      public PortalRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, var2);
         this.hasPlacedSpawner = var2.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         if (var1 != null) {
            ((StrongholdPieces.StartPiece)var1).portalRoomPiece = this;
         }

      }

      public static StrongholdPieces.PortalRoom createPiece(List var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -4, -1, 0, 11, 8, 16, var4);
         return isOkBox(var6) && StructurePiece.findCollisionPiece(var0, var6) == null ? new StrongholdPieces.PortalRoom(var5, var6, var4) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 10, 7, 15, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, StrongholdPieces.StrongholdPiece.SmallDoorType.GRATES, 4, 1, 0);
         byte var6 = 6;
         this.generateBox(var1, var4, 1, var6, 1, 1, var6, 14, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 9, var6, 1, 9, var6, 14, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 2, var6, 1, 8, var6, 2, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 2, var6, 14, 8, var6, 14, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 1, 1, 1, 2, 1, 4, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 8, 1, 1, 9, 1, 4, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(var1, var4, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(var1, var4, 3, 1, 8, 7, 1, 12, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         BlockState var7 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true);
         BlockState var8 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true);

         int var9;
         for(var9 = 3; var9 < 14; var9 += 2) {
            this.generateBox(var1, var4, 0, 3, var9, 0, 4, var9, var7, var7, false);
            this.generateBox(var1, var4, 10, 3, var9, 10, 4, var9, var7, var7, false);
         }

         for(var9 = 2; var9 < 9; var9 += 2) {
            this.generateBox(var1, var4, var9, 3, 15, var9, 4, 15, var8, var8, false);
         }

         BlockState var19 = (BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         this.generateBox(var1, var4, 4, 1, 5, 6, 1, 7, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 4, 2, 6, 6, 2, 7, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 4, 3, 7, 6, 3, 7, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int var10 = 4; var10 <= 6; ++var10) {
            this.placeBlock(var1, var19, var10, 1, 4, var4);
            this.placeBlock(var1, var19, var10, 2, 5, var4);
            this.placeBlock(var1, var19, var10, 3, 6, var4);
         }

         BlockState var20 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
         BlockState var11 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
         BlockState var12 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
         BlockState var13 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
         boolean var14 = true;
         boolean[] var15 = new boolean[12];

         for(int var16 = 0; var16 < var15.length; ++var16) {
            var15[var16] = var3.nextFloat() > 0.9F;
            var14 &= var15[var16];
         }

         this.placeBlock(var1, (BlockState)var20.setValue(EndPortalFrameBlock.HAS_EYE, var15[0]), 4, 3, 8, var4);
         this.placeBlock(var1, (BlockState)var20.setValue(EndPortalFrameBlock.HAS_EYE, var15[1]), 5, 3, 8, var4);
         this.placeBlock(var1, (BlockState)var20.setValue(EndPortalFrameBlock.HAS_EYE, var15[2]), 6, 3, 8, var4);
         this.placeBlock(var1, (BlockState)var11.setValue(EndPortalFrameBlock.HAS_EYE, var15[3]), 4, 3, 12, var4);
         this.placeBlock(var1, (BlockState)var11.setValue(EndPortalFrameBlock.HAS_EYE, var15[4]), 5, 3, 12, var4);
         this.placeBlock(var1, (BlockState)var11.setValue(EndPortalFrameBlock.HAS_EYE, var15[5]), 6, 3, 12, var4);
         this.placeBlock(var1, (BlockState)var12.setValue(EndPortalFrameBlock.HAS_EYE, var15[6]), 3, 3, 9, var4);
         this.placeBlock(var1, (BlockState)var12.setValue(EndPortalFrameBlock.HAS_EYE, var15[7]), 3, 3, 10, var4);
         this.placeBlock(var1, (BlockState)var12.setValue(EndPortalFrameBlock.HAS_EYE, var15[8]), 3, 3, 11, var4);
         this.placeBlock(var1, (BlockState)var13.setValue(EndPortalFrameBlock.HAS_EYE, var15[9]), 7, 3, 9, var4);
         this.placeBlock(var1, (BlockState)var13.setValue(EndPortalFrameBlock.HAS_EYE, var15[10]), 7, 3, 10, var4);
         this.placeBlock(var1, (BlockState)var13.setValue(EndPortalFrameBlock.HAS_EYE, var15[11]), 7, 3, 11, var4);
         if (var14) {
            BlockState var21 = Blocks.END_PORTAL.defaultBlockState();
            this.placeBlock(var1, var21, 4, 3, 9, var4);
            this.placeBlock(var1, var21, 5, 3, 9, var4);
            this.placeBlock(var1, var21, 6, 3, 9, var4);
            this.placeBlock(var1, var21, 4, 3, 10, var4);
            this.placeBlock(var1, var21, 5, 3, 10, var4);
            this.placeBlock(var1, var21, 6, 3, 10, var4);
            this.placeBlock(var1, var21, 4, 3, 11, var4);
            this.placeBlock(var1, var21, 5, 3, 11, var4);
            this.placeBlock(var1, var21, 6, 3, 11, var4);
         }

         if (!this.hasPlacedSpawner) {
            int var18 = this.getWorldY(3);
            BlockPos var22 = new BlockPos(this.getWorldX(5, 6), var18, this.getWorldZ(5, 6));
            if (var4.isInside(var22)) {
               this.hasPlacedSpawner = true;
               var1.setBlock(var22, Blocks.SPAWNER.defaultBlockState(), 2);
               BlockEntity var17 = var1.getBlockEntity(var22);
               if (var17 instanceof SpawnerBlockEntity) {
                  ((SpawnerBlockEntity)var17).getSpawner().setEntityId(EntityType.SILVERFISH);
               }
            }
         }

         return true;
      }
   }

   public static class FiveCrossing extends StrongholdPieces.StrongholdPiece {
      private final boolean leftLow;
      private final boolean leftHigh;
      private final boolean rightLow;
      private final boolean rightHigh;

      public FiveCrossing(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
         this.leftLow = var2.nextBoolean();
         this.leftHigh = var2.nextBoolean();
         this.rightLow = var2.nextBoolean();
         this.rightHigh = var2.nextInt(3) > 0;
      }

      public FiveCrossing(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, var2);
         this.leftLow = var2.getBoolean("leftLow");
         this.leftHigh = var2.getBoolean("leftHigh");
         this.rightLow = var2.getBoolean("rightLow");
         this.rightHigh = var2.getBoolean("rightHigh");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("leftLow", this.leftLow);
         var1.putBoolean("leftHigh", this.leftHigh);
         var1.putBoolean("rightLow", this.rightLow);
         var1.putBoolean("rightHigh", this.rightHigh);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         int var4 = 3;
         int var5 = 5;
         Direction var6 = this.getOrientation();
         if (var6 == Direction.WEST || var6 == Direction.NORTH) {
            var4 = 8 - var4;
            var5 = 8 - var5;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)var1, var2, var3, 5, 1);
         if (this.leftLow) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)var1, var2, var3, var4, 1);
         }

         if (this.leftHigh) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)var1, var2, var3, var5, 7);
         }

         if (this.rightLow) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)var1, var2, var3, var4, 1);
         }

         if (this.rightHigh) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)var1, var2, var3, var5, 7);
         }

      }

      public static StrongholdPieces.FiveCrossing createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -3, 0, 10, 9, 11, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.FiveCrossing(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 9, 8, 10, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 4, 3, 0);
         if (this.leftLow) {
            this.generateBox(var1, var4, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightLow) {
            this.generateBox(var1, var4, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.leftHigh) {
            this.generateBox(var1, var4, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightHigh) {
            this.generateBox(var1, var4, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         this.generateBox(var1, var4, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var4, 1, 2, 1, 8, 2, 6, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 4, 1, 5, 4, 4, 9, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 8, 1, 5, 8, 4, 9, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 1, 4, 7, 3, 4, 9, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 1, 3, 5, 3, 3, 6, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var4, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var4, 5, 1, 7, 7, 1, 8, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var4, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var4, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var4, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var4, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
         this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, var4);
         return true;
      }
   }

   public static class Library extends StrongholdPieces.StrongholdPiece {
      private final boolean isTall;

      public Library(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
         this.isTall = var3.getYSpan() > 6;
      }

      public Library(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, var2);
         this.isTall = var2.getBoolean("Tall");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("Tall", this.isTall);
      }

      public static StrongholdPieces.Library createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 14, 11, 15, var5);
         if (!isOkBox(var7) || StructurePiece.findCollisionPiece(var0, var7) != null) {
            var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 14, 6, 15, var5);
            if (!isOkBox(var7) || StructurePiece.findCollisionPiece(var0, var7) != null) {
               return null;
            }
         }

         return new StrongholdPieces.Library(var6, var1, var7, var5);
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         byte var6 = 11;
         if (!this.isTall) {
            var6 = 6;
         }

         this.generateBox(var1, var4, 0, 0, 0, 13, var6 - 1, 14, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 4, 1, 0);
         this.generateMaybeBox(var1, var4, var3, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false);
         boolean var7 = true;
         boolean var8 = true;

         int var9;
         for(var9 = 1; var9 <= 13; ++var9) {
            if ((var9 - 1) % 4 == 0) {
               this.generateBox(var1, var4, 1, 1, var9, 1, 4, var9, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.generateBox(var1, var4, 12, 1, var9, 12, 4, var9, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, var9, var4);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, var9, var4);
               if (this.isTall) {
                  this.generateBox(var1, var4, 1, 6, var9, 1, 9, var9, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                  this.generateBox(var1, var4, 12, 6, var9, 12, 9, var9, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               }
            } else {
               this.generateBox(var1, var4, 1, 1, var9, 1, 4, var9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               this.generateBox(var1, var4, 12, 1, var9, 12, 4, var9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               if (this.isTall) {
                  this.generateBox(var1, var4, 1, 6, var9, 1, 9, var9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                  this.generateBox(var1, var4, 12, 6, var9, 12, 9, var9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               }
            }
         }

         for(var9 = 3; var9 < 12; var9 += 2) {
            this.generateBox(var1, var4, 3, 1, var9, 4, 3, var9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(var1, var4, 6, 1, var9, 7, 3, var9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(var1, var4, 9, 1, var9, 10, 3, var9, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
         }

         if (this.isTall) {
            this.generateBox(var1, var4, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(var1, var4, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(var1, var4, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(var1, var4, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, var4);
            this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, var4);
            this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, var4);
            BlockState var18 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState var10 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.generateBox(var1, var4, 3, 6, 3, 3, 6, 11, var10, var10, false);
            this.generateBox(var1, var4, 10, 6, 3, 10, 6, 9, var10, var10, false);
            this.generateBox(var1, var4, 4, 6, 2, 9, 6, 2, var18, var18, false);
            this.generateBox(var1, var4, 4, 6, 12, 7, 6, 12, var18, var18, false);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 2, var4);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 12, var4);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 10, 6, 2, var4);

            for(int var11 = 0; var11 <= 2; ++var11) {
               this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 8 + var11, 6, 12 - var11, var4);
               if (var11 != 2) {
                  this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 8 + var11, 6, 11 - var11, var4);
               }
            }

            BlockState var19 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
            this.placeBlock(var1, var19, 10, 1, 13, var4);
            this.placeBlock(var1, var19, 10, 2, 13, var4);
            this.placeBlock(var1, var19, 10, 3, 13, var4);
            this.placeBlock(var1, var19, 10, 4, 13, var4);
            this.placeBlock(var1, var19, 10, 5, 13, var4);
            this.placeBlock(var1, var19, 10, 6, 13, var4);
            this.placeBlock(var1, var19, 10, 7, 13, var4);
            boolean var12 = true;
            boolean var13 = true;
            BlockState var14 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true);
            this.placeBlock(var1, var14, 6, 9, 7, var4);
            BlockState var15 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true);
            this.placeBlock(var1, var15, 7, 9, 7, var4);
            this.placeBlock(var1, var14, 6, 8, 7, var4);
            this.placeBlock(var1, var15, 7, 8, 7, var4);
            BlockState var16 = (BlockState)((BlockState)var10.setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            this.placeBlock(var1, var16, 6, 7, 7, var4);
            this.placeBlock(var1, var16, 7, 7, 7, var4);
            this.placeBlock(var1, var14, 5, 7, 7, var4);
            this.placeBlock(var1, var15, 8, 7, 7, var4);
            this.placeBlock(var1, (BlockState)var14.setValue(FenceBlock.NORTH, true), 6, 7, 6, var4);
            this.placeBlock(var1, (BlockState)var14.setValue(FenceBlock.SOUTH, true), 6, 7, 8, var4);
            this.placeBlock(var1, (BlockState)var15.setValue(FenceBlock.NORTH, true), 7, 7, 6, var4);
            this.placeBlock(var1, (BlockState)var15.setValue(FenceBlock.SOUTH, true), 7, 7, 8, var4);
            BlockState var17 = Blocks.TORCH.defaultBlockState();
            this.placeBlock(var1, var17, 5, 8, 7, var4);
            this.placeBlock(var1, var17, 8, 8, 7, var4);
            this.placeBlock(var1, var17, 6, 8, 6, var4);
            this.placeBlock(var1, var17, 6, 8, 8, var4);
            this.placeBlock(var1, var17, 7, 8, 6, var4);
            this.placeBlock(var1, var17, 7, 8, 8, var4);
         }

         this.createChest(var1, var4, var3, 3, 3, 5, BuiltInLootTables.STRONGHOLD_LIBRARY);
         if (this.isTall) {
            this.placeBlock(var1, CAVE_AIR, 12, 9, 1, var4);
            this.createChest(var1, var4, var3, 12, 8, 1, BuiltInLootTables.STRONGHOLD_LIBRARY);
         }

         return true;
      }
   }

   public static class PrisonHall extends StrongholdPieces.StrongholdPiece {
      public PrisonHall(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
      }

      public PrisonHall(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, var2);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.PrisonHall createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 9, 5, 11, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.PrisonHall(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 8, 4, 10, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 1, 1, 0);
         this.generateBox(var1, var4, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var4, 4, 1, 1, 4, 3, 1, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 4, 1, 3, 4, 3, 3, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 4, 1, 7, 4, 3, 7, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var4, 4, 1, 9, 4, 3, 9, false, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int var6 = 1; var6 <= 3; ++var6) {
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, var6, 4, var4);
            this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)).setValue(IronBarsBlock.EAST, true), 4, var6, 5, var4);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, var6, 6, var4);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 5, var6, 5, var4);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 6, var6, 5, var4);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 7, var6, 5, var4);
         }

         this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 2, var4);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 8, var4);
         BlockState var8 = (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
         BlockState var7 = (BlockState)((BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST)).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
         this.placeBlock(var1, var8, 4, 1, 2, var4);
         this.placeBlock(var1, var7, 4, 2, 2, var4);
         this.placeBlock(var1, var8, 4, 1, 8, var4);
         this.placeBlock(var1, var7, 4, 2, 8, var4);
         return true;
      }
   }

   public static class RoomCrossing extends StrongholdPieces.StrongholdPiece {
      protected final int type;

      public RoomCrossing(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
         this.type = var2.nextInt(5);
      }

      public RoomCrossing(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, var2);
         this.type = var2.getInt("Type");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putInt("Type", this.type);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)var1, var2, var3, 4, 1);
         this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)var1, var2, var3, 1, 4);
         this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)var1, var2, var3, 1, 4);
      }

      public static StrongholdPieces.RoomCrossing createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 11, 7, 11, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.RoomCrossing(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 10, 6, 10, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 4, 1, 0);
         this.generateBox(var1, var4, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var4, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var4, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
         int var6;
         switch(this.type) {
         case 0:
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, var4);
            this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, var4);
            this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, var4);
            this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, var4);
            this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, var4);
            this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, var4);
            break;
         case 1:
            for(var6 = 0; var6 < 5; ++var6) {
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + var6, var4);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + var6, var4);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3 + var6, 1, 3, var4);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3 + var6, 1, 7, var4);
            }

            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, var4);
            this.placeBlock(var1, Blocks.WATER.defaultBlockState(), 5, 4, 5, var4);
            break;
         case 2:
            for(var6 = 1; var6 <= 9; ++var6) {
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, var6, var4);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, var6, var4);
            }

            for(var6 = 1; var6 <= 9; ++var6) {
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), var6, 3, 1, var4);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), var6, 3, 9, var4);
            }

            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, var4);
            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, var4);
            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, var4);
            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, var4);
            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, var4);
            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, var4);
            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, var4);
            this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, var4);

            for(var6 = 1; var6 <= 3; ++var6) {
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, var6, 4, var4);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, var6, 4, var4);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, var6, 6, var4);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, var6, 6, var4);
            }

            this.placeBlock(var1, Blocks.TORCH.defaultBlockState(), 5, 3, 5, var4);

            for(var6 = 2; var6 <= 8; ++var6) {
               this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, var6, var4);
               this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, var6, var4);
               if (var6 <= 3 || var6 >= 7) {
                  this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, var6, var4);
                  this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, var6, var4);
                  this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, var6, var4);
               }

               this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, var6, var4);
               this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, var6, var4);
            }

            BlockState var7 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
            this.placeBlock(var1, var7, 9, 1, 3, var4);
            this.placeBlock(var1, var7, 9, 2, 3, var4);
            this.placeBlock(var1, var7, 9, 3, 3, var4);
            this.createChest(var1, var4, var3, 3, 4, 8, BuiltInLootTables.STRONGHOLD_CROSSING);
         }

         return true;
      }
   }

   public static class RightTurn extends StrongholdPieces.Turn {
      public RightTurn(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
      }

      public RightTurn(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, var2);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         Direction var4 = this.getOrientation();
         if (var4 != Direction.NORTH && var4 != Direction.EAST) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
         } else {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
         }

      }

      public static StrongholdPieces.RightTurn createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.RightTurn(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 4, 4, 4, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 1, 1, 0);
         Direction var6 = this.getOrientation();
         if (var6 != Direction.NORTH && var6 != Direction.EAST) {
            this.generateBox(var1, var4, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(var1, var4, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class LeftTurn extends StrongholdPieces.Turn {
      public LeftTurn(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
      }

      public LeftTurn(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, var2);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         Direction var4 = this.getOrientation();
         if (var4 != Direction.NORTH && var4 != Direction.EAST) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
         } else {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
         }

      }

      public static StrongholdPieces.LeftTurn createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.LeftTurn(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 4, 4, 4, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 1, 1, 0);
         Direction var6 = this.getOrientation();
         if (var6 != Direction.NORTH && var6 != Direction.EAST) {
            this.generateBox(var1, var4, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(var1, var4, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public abstract static class Turn extends StrongholdPieces.StrongholdPiece {
      protected Turn(StructurePieceType var1, int var2) {
         super(var1, var2);
      }

      public Turn(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
      }
   }

   public static class StraightStairsDown extends StrongholdPieces.StrongholdPiece {
      public StraightStairsDown(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
      }

      public StraightStairsDown(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, var2);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.StraightStairsDown createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -7, 0, 5, 11, 8, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.StraightStairsDown(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 4, 10, 7, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(var1, var3, var4, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 7);
         BlockState var6 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);

         for(int var7 = 0; var7 < 6; ++var7) {
            this.placeBlock(var1, var6, 1, 6 - var7, 1 + var7, var4);
            this.placeBlock(var1, var6, 2, 6 - var7, 1 + var7, var4);
            this.placeBlock(var1, var6, 3, 6 - var7, 1 + var7, var4);
            if (var7 < 5) {
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - var7, 1 + var7, var4);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - var7, 1 + var7, var4);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - var7, 1 + var7, var4);
            }
         }

         return true;
      }
   }

   public static class ChestCorridor extends StrongholdPieces.StrongholdPiece {
      private boolean hasPlacedChest;

      public ChestCorridor(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
      }

      public ChestCorridor(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, var2);
         this.hasPlacedChest = var2.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("Chest", this.hasPlacedChest);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.ChestCorridor createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.ChestCorridor(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 4, 4, 6, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(var1, var3, var4, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         this.generateBox(var1, var4, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, var4);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, var4);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, var4);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, var4);

         for(int var6 = 2; var6 <= 4; ++var6) {
            this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, var6, var4);
         }

         if (!this.hasPlacedChest && var4.isInside(new BlockPos(this.getWorldX(3, 3), this.getWorldY(2), this.getWorldZ(3, 3)))) {
            this.hasPlacedChest = true;
            this.createChest(var1, var4, var3, 3, 2, 3, BuiltInLootTables.STRONGHOLD_CORRIDOR);
         }

         return true;
      }
   }

   public static class Straight extends StrongholdPieces.StrongholdPiece {
      private final boolean leftChild;
      private final boolean rightChild;

      public Straight(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, var1);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
         this.leftChild = var2.nextInt(2) == 0;
         this.rightChild = var2.nextInt(2) == 0;
      }

      public Straight(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, var2);
         this.leftChild = var2.getBoolean("Left");
         this.rightChild = var2.getBoolean("Right");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("Left", this.leftChild);
         var1.putBoolean("Right", this.rightChild);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
         if (this.leftChild) {
            this.generateSmallDoorChildLeft((StrongholdPieces.StartPiece)var1, var2, var3, 1, 2);
         }

         if (this.rightChild) {
            this.generateSmallDoorChildRight((StrongholdPieces.StartPiece)var1, var2, var3, 1, 2);
         }

      }

      public static StrongholdPieces.Straight createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.Straight(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 4, 4, 6, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(var1, var3, var4, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         BlockState var6 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
         BlockState var7 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
         this.maybeGenerateBlock(var1, var4, var3, 0.1F, 1, 2, 1, var6);
         this.maybeGenerateBlock(var1, var4, var3, 0.1F, 3, 2, 1, var7);
         this.maybeGenerateBlock(var1, var4, var3, 0.1F, 1, 2, 5, var6);
         this.maybeGenerateBlock(var1, var4, var3, 0.1F, 3, 2, 5, var7);
         if (this.leftChild) {
            this.generateBox(var1, var4, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightChild) {
            this.generateBox(var1, var4, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         return true;
      }
   }

   public static class StartPiece extends StrongholdPieces.StairsDown {
      public StrongholdPieces.PieceWeight previousPiece;
      @Nullable
      public StrongholdPieces.PortalRoom portalRoomPiece;
      public final List pendingChildren = Lists.newArrayList();

      public StartPiece(Random var1, int var2, int var3) {
         super(StructurePieceType.STRONGHOLD_START, 0, var1, var2, var3);
      }

      public StartPiece(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_START, var2);
      }
   }

   public static class StairsDown extends StrongholdPieces.StrongholdPiece {
      private final boolean isSource;

      public StairsDown(StructurePieceType var1, int var2, Random var3, int var4, int var5) {
         super(var1, var2);
         this.isSource = true;
         this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(var3));
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
         if (this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.boundingBox = new BoundingBox(var4, 64, var5, var4 + 5 - 1, 74, var5 + 5 - 1);
         } else {
            this.boundingBox = new BoundingBox(var4, 64, var5, var4 + 5 - 1, 74, var5 + 5 - 1);
         }

      }

      public StairsDown(int var1, Random var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_STAIRS_DOWN, var1);
         this.isSource = false;
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.boundingBox = var3;
      }

      public StairsDown(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
         this.isSource = var2.getBoolean("Source");
      }

      public StairsDown(StructureManager var1, CompoundTag var2) {
         this(StructurePieceType.STRONGHOLD_STAIRS_DOWN, var2);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("Source", this.isSource);
      }

      public void addChildren(StructurePiece var1, List var2, Random var3) {
         if (this.isSource) {
            StrongholdPieces.imposedPiece = StrongholdPieces.FiveCrossing.class;
         }

         this.generateSmallDoorChildForward((StrongholdPieces.StartPiece)var1, var2, var3, 1, 1);
      }

      public static StrongholdPieces.StairsDown createPiece(List var0, Random var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -7, 0, 5, 11, 5, var5);
         return isOkBox(var7) && StructurePiece.findCollisionPiece(var0, var7) == null ? new StrongholdPieces.StairsDown(var6, var1, var7, var5) : null;
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         this.generateBox(var1, var4, 0, 0, 0, 4, 10, 4, true, var3, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var3, var4, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(var1, var3, var4, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, var4);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, var4);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, var4);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, var4);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, var4);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, var4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, var4);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, var4);
         return true;
      }
   }

   public static class FillerCorridor extends StrongholdPieces.StrongholdPiece {
      private final int steps;

      public FillerCorridor(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, var1);
         this.setOrientation(var3);
         this.boundingBox = var2;
         this.steps = var3 != Direction.NORTH && var3 != Direction.SOUTH ? var2.getXSpan() : var2.getZSpan();
      }

      public FillerCorridor(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, var2);
         this.steps = var2.getInt("Steps");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putInt("Steps", this.steps);
      }

      public static BoundingBox findPieceBox(List var0, Random var1, int var2, int var3, int var4, Direction var5) {
         boolean var6 = true;
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 4, var5);
         StructurePiece var8 = StructurePiece.findCollisionPiece(var0, var7);
         if (var8 == null) {
            return null;
         } else {
            if (var8.getBoundingBox().y0 == var7.y0) {
               for(int var9 = 3; var9 >= 1; --var9) {
                  var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, var9 - 1, var5);
                  if (!var8.getBoundingBox().intersects(var7)) {
                     return BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, var9, var5);
                  }
               }
            }

            return null;
         }
      }

      public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
         for(int var6 = 0; var6 < this.steps; ++var6) {
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, var6, var4);

            for(int var7 = 1; var7 <= 3; ++var7) {
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 0, var7, var6, var4);
               this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), 1, var7, var6, var4);
               this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), 2, var7, var6, var4);
               this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), 3, var7, var6, var4);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 4, var7, var6, var4);
            }

            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, var6, var4);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, var6, var4);
         }

         return true;
      }
   }

   abstract static class StrongholdPiece extends StructurePiece {
      protected StrongholdPieces.StrongholdPiece.SmallDoorType entryDoor;

      protected StrongholdPiece(StructurePieceType var1, int var2) {
         super(var1, var2);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
      }

      public StrongholdPiece(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.valueOf(var2.getString("EntryDoor"));
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         var1.putString("EntryDoor", this.entryDoor.name());
      }

      protected void generateSmallDoor(LevelAccessor var1, Random var2, BoundingBox var3, StrongholdPieces.StrongholdPiece.SmallDoorType var4, int var5, int var6, int var7) {
         switch(var4) {
         case OPENING:
            this.generateBox(var1, var3, var5, var6, var7, var5 + 3 - 1, var6 + 3 - 1, var7, CAVE_AIR, CAVE_AIR, false);
            break;
         case WOOD_DOOR:
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 1, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 2, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 1, var6 + 2, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 2, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 1, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6, var7, var3);
            this.placeBlock(var1, Blocks.OAK_DOOR.defaultBlockState(), var5 + 1, var6, var7, var3);
            this.placeBlock(var1, (BlockState)Blocks.OAK_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), var5 + 1, var6 + 1, var7, var3);
            break;
         case GRATES:
            this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), var5 + 1, var6, var7, var3);
            this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), var5 + 1, var6 + 1, var7, var3);
            this.placeBlock(var1, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true), var5, var6, var7, var3);
            this.placeBlock(var1, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true), var5, var6 + 1, var7, var3);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), var5, var6 + 2, var7, var3);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), var5 + 1, var6 + 2, var7, var3);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true)).setValue(IronBarsBlock.WEST, true), var5 + 2, var6 + 2, var7, var3);
            this.placeBlock(var1, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true), var5 + 2, var6 + 1, var7, var3);
            this.placeBlock(var1, (BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.EAST, true), var5 + 2, var6, var7, var3);
            break;
         case IRON_DOOR:
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 1, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5, var6 + 2, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 1, var6 + 2, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 2, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6 + 1, var7, var3);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), var5 + 2, var6, var7, var3);
            this.placeBlock(var1, Blocks.IRON_DOOR.defaultBlockState(), var5 + 1, var6, var7, var3);
            this.placeBlock(var1, (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), var5 + 1, var6 + 1, var7, var3);
            this.placeBlock(var1, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.NORTH), var5 + 2, var6 + 1, var7 + 1, var3);
            this.placeBlock(var1, (BlockState)Blocks.STONE_BUTTON.defaultBlockState().setValue(ButtonBlock.FACING, Direction.SOUTH), var5 + 2, var6 + 1, var7 - 1, var3);
         }

      }

      protected StrongholdPieces.StrongholdPiece.SmallDoorType randomSmallDoor(Random var1) {
         int var2 = var1.nextInt(5);
         switch(var2) {
         case 0:
         case 1:
         default:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
         case 2:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.WOOD_DOOR;
         case 3:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.GRATES;
         case 4:
            return StrongholdPieces.StrongholdPiece.SmallDoorType.IRON_DOOR;
         }
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildForward(StrongholdPieces.StartPiece var1, List var2, Random var3, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var4, this.boundingBox.y0 + var5, this.boundingBox.z0 - 1, var6, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var4, this.boundingBox.y0 + var5, this.boundingBox.z1 + 1, var6, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 + var5, this.boundingBox.z0 + var4, var6, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 + var5, this.boundingBox.z0 + var4, var6, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildLeft(StrongholdPieces.StartPiece var1, List var2, Random var3, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.WEST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.WEST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z0 - 1, Direction.NORTH, this.getGenDepth());
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildRight(StrongholdPieces.StartPiece var1, List var2, Random var3, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.EAST, this.getGenDepth());
            case SOUTH:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 + var4, this.boundingBox.z0 + var5, Direction.EAST, this.getGenDepth());
            case WEST:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
            case EAST:
               return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var5, this.boundingBox.y0 + var4, this.boundingBox.z1 + 1, Direction.SOUTH, this.getGenDepth());
            }
         }

         return null;
      }

      protected static boolean isOkBox(BoundingBox var0) {
         return var0 != null && var0.y0 > 10;
      }

      public static enum SmallDoorType {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;
      }
   }

   static class PieceWeight {
      public final Class pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;

      public PieceWeight(Class var1, int var2, int var3) {
         this.pieceClass = var1;
         this.weight = var2;
         this.maxPlaceCount = var3;
      }

      public boolean doPlace(int var1) {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }

      public boolean isValid() {
         return this.maxPlaceCount == 0 || this.placeCount < this.maxPlaceCount;
      }
   }
}
