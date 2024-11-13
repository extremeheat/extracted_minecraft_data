package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class StrongholdPieces {
   private static final int SMALL_DOOR_WIDTH = 3;
   private static final int SMALL_DOOR_HEIGHT = 3;
   private static final int MAX_DEPTH = 50;
   private static final int LOWEST_Y_POSITION = 10;
   private static final boolean CHECK_AIR = true;
   public static final int MAGIC_START_Y = 64;
   private static final PieceWeight[] STRONGHOLD_PIECE_WEIGHTS = new PieceWeight[]{new PieceWeight(Straight.class, 40, 0), new PieceWeight(PrisonHall.class, 5, 5), new PieceWeight(LeftTurn.class, 20, 0), new PieceWeight(RightTurn.class, 20, 0), new PieceWeight(RoomCrossing.class, 10, 6), new PieceWeight(StraightStairsDown.class, 5, 5), new PieceWeight(StairsDown.class, 5, 5), new PieceWeight(FiveCrossing.class, 5, 4), new PieceWeight(ChestCorridor.class, 5, 4), new PieceWeight(Library.class, 10, 2) {
      public boolean doPlace(int var1) {
         return super.doPlace(var1) && var1 > 4;
      }
   }, new PieceWeight(PortalRoom.class, 20, 1) {
      public boolean doPlace(int var1) {
         return super.doPlace(var1) && var1 > 5;
      }
   }};
   private static List<PieceWeight> currentPieces;
   static Class<? extends StrongholdPiece> imposedPiece;
   private static int totalWeight;
   static final SmoothStoneSelector SMOOTH_STONE_SELECTOR = new SmoothStoneSelector();

   public StrongholdPieces() {
      super();
   }

   public static void resetPieces() {
      currentPieces = Lists.newArrayList();

      for(PieceWeight var3 : STRONGHOLD_PIECE_WEIGHTS) {
         var3.placeCount = 0;
         currentPieces.add(var3);
      }

      imposedPiece = null;
   }

   private static boolean updatePieceWeight() {
      boolean var0 = false;
      totalWeight = 0;

      for(PieceWeight var2 : currentPieces) {
         if (var2.maxPlaceCount > 0 && var2.placeCount < var2.maxPlaceCount) {
            var0 = true;
         }

         totalWeight += var2.weight;
      }

      return var0;
   }

   private static StrongholdPiece findAndCreatePieceFactory(Class<? extends StrongholdPiece> var0, StructurePieceAccessor var1, RandomSource var2, int var3, int var4, int var5, @Nullable Direction var6, int var7) {
      Object var8 = null;
      if (var0 == Straight.class) {
         var8 = StrongholdPieces.Straight.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == PrisonHall.class) {
         var8 = StrongholdPieces.PrisonHall.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == LeftTurn.class) {
         var8 = StrongholdPieces.LeftTurn.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == RightTurn.class) {
         var8 = StrongholdPieces.RightTurn.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == RoomCrossing.class) {
         var8 = StrongholdPieces.RoomCrossing.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StraightStairsDown.class) {
         var8 = StrongholdPieces.StraightStairsDown.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == StairsDown.class) {
         var8 = StrongholdPieces.StairsDown.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == FiveCrossing.class) {
         var8 = StrongholdPieces.FiveCrossing.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == ChestCorridor.class) {
         var8 = StrongholdPieces.ChestCorridor.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == Library.class) {
         var8 = StrongholdPieces.Library.createPiece(var1, var2, var3, var4, var5, var6, var7);
      } else if (var0 == PortalRoom.class) {
         var8 = StrongholdPieces.PortalRoom.createPiece(var1, var3, var4, var5, var6, var7);
      }

      return (StrongholdPiece)var8;
   }

   private static StrongholdPiece generatePieceFromSmallDoor(StartPiece var0, StructurePieceAccessor var1, RandomSource var2, int var3, int var4, int var5, Direction var6, int var7) {
      if (!updatePieceWeight()) {
         return null;
      } else {
         if (imposedPiece != null) {
            StrongholdPiece var8 = findAndCreatePieceFactory(imposedPiece, var1, var2, var3, var4, var5, var6, var7);
            imposedPiece = null;
            if (var8 != null) {
               return var8;
            }
         }

         int var13 = 0;

         while(var13 < 5) {
            ++var13;
            int var9 = var2.nextInt(totalWeight);

            for(PieceWeight var11 : currentPieces) {
               var9 -= var11.weight;
               if (var9 < 0) {
                  if (!var11.doPlace(var7) || var11 == var0.previousPiece) {
                     break;
                  }

                  StrongholdPiece var12 = findAndCreatePieceFactory(var11.pieceClass, var1, var2, var3, var4, var5, var6, var7);
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
         if (var14 != null && var14.minY() > 1) {
            return new FillerCorridor(var7, var14, var6);
         } else {
            return null;
         }
      }
   }

   static StructurePiece generateAndAddPiece(StartPiece var0, StructurePieceAccessor var1, RandomSource var2, int var3, int var4, int var5, @Nullable Direction var6, int var7) {
      if (var7 > 50) {
         return null;
      } else if (Math.abs(var3 - var0.getBoundingBox().minX()) <= 112 && Math.abs(var5 - var0.getBoundingBox().minZ()) <= 112) {
         StrongholdPiece var8 = generatePieceFromSmallDoor(var0, var1, var2, var3, var4, var5, var6, var7 + 1);
         if (var8 != null) {
            var1.addPiece(var8);
            var0.pendingChildren.add(var8);
         }

         return var8;
      } else {
         return null;
      }
   }

   static class PieceWeight {
      public final Class<? extends StrongholdPiece> pieceClass;
      public final int weight;
      public int placeCount;
      public final int maxPlaceCount;

      public PieceWeight(Class<? extends StrongholdPiece> var1, int var2, int var3) {
         super();
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

   abstract static class StrongholdPiece extends StructurePiece {
      protected SmallDoorType entryDoor;

      protected StrongholdPiece(StructurePieceType var1, int var2, BoundingBox var3) {
         super(var1, var2, var3);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
      }

      public StrongholdPiece(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.valueOf(var2.getString("EntryDoor"));
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         var2.putString("EntryDoor", this.entryDoor.name());
      }

      protected void generateSmallDoor(WorldGenLevel var1, RandomSource var2, BoundingBox var3, SmallDoorType var4, int var5, int var6, int var7) {
         switch (var4.ordinal()) {
            case 0:
               this.generateBox(var1, var3, var5, var6, var7, var5 + 3 - 1, var6 + 3 - 1, var7, CAVE_AIR, CAVE_AIR, false);
               break;
            case 1:
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
            case 2:
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
            case 3:
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

      protected SmallDoorType randomSmallDoor(RandomSource var1) {
         int var2 = var1.nextInt(5);
         switch (var2) {
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
      protected StructurePiece generateSmallDoorChildForward(StartPiece var1, StructurePieceAccessor var2, RandomSource var3, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch (var6) {
               case NORTH -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var4, this.boundingBox.minY() + var5, this.boundingBox.minZ() - 1, var6, this.getGenDepth());
               }
               case SOUTH -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var4, this.boundingBox.minY() + var5, this.boundingBox.maxZ() + 1, var6, this.getGenDepth());
               }
               case WEST -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() + var5, this.boundingBox.minZ() + var4, var6, this.getGenDepth());
               }
               case EAST -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() + var5, this.boundingBox.minZ() + var4, var6, this.getGenDepth());
               }
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildLeft(StartPiece var1, StructurePieceAccessor var2, RandomSource var3, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch (var6) {
               case NORTH -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.WEST, this.getGenDepth());
               }
               case SOUTH -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.WEST, this.getGenDepth());
               }
               case WEST -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth());
               }
               case EAST -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.minZ() - 1, Direction.NORTH, this.getGenDepth());
               }
            }
         }

         return null;
      }

      @Nullable
      protected StructurePiece generateSmallDoorChildRight(StartPiece var1, StructurePieceAccessor var2, RandomSource var3, int var4, int var5) {
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch (var6) {
               case NORTH -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.EAST, this.getGenDepth());
               }
               case SOUTH -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() + var4, this.boundingBox.minZ() + var5, Direction.EAST, this.getGenDepth());
               }
               case WEST -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth());
               }
               case EAST -> {
                  return StrongholdPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.minX() + var5, this.boundingBox.minY() + var4, this.boundingBox.maxZ() + 1, Direction.SOUTH, this.getGenDepth());
               }
            }
         }

         return null;
      }

      protected static boolean isOkBox(BoundingBox var0) {
         return var0 != null && var0.minY() > 10;
      }

      protected static enum SmallDoorType {
         OPENING,
         WOOD_DOOR,
         GRATES,
         IRON_DOOR;

         private SmallDoorType() {
         }

         // $FF: synthetic method
         private static SmallDoorType[] $values() {
            return new SmallDoorType[]{OPENING, WOOD_DOOR, GRATES, IRON_DOOR};
         }
      }
   }

   public static class FillerCorridor extends StrongholdPiece {
      private final int steps;

      public FillerCorridor(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, var1, var2);
         this.setOrientation(var3);
         this.steps = var3 != Direction.NORTH && var3 != Direction.SOUTH ? var2.getXSpan() : var2.getZSpan();
      }

      public FillerCorridor(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_FILLER_CORRIDOR, var1);
         this.steps = var1.getInt("Steps");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putInt("Steps", this.steps);
      }

      public static BoundingBox findPieceBox(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5) {
         boolean var6 = true;
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 4, var5);
         StructurePiece var8 = var0.findCollisionPiece(var7);
         if (var8 == null) {
            return null;
         } else {
            if (var8.getBoundingBox().minY() == var7.minY()) {
               for(int var9 = 2; var9 >= 1; --var9) {
                  var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, var9, var5);
                  if (!var8.getBoundingBox().intersects(var7)) {
                     return BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, var9 + 1, var5);
                  }
               }
            }

            return null;
         }
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         for(int var8 = 0; var8 < this.steps; ++var8) {
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 0, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 0, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 0, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 4, 0, var8, var5);

            for(int var9 = 1; var9 <= 3; ++var9) {
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 0, var9, var8, var5);
               this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), 1, var9, var8, var5);
               this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), 2, var9, var8, var5);
               this.placeBlock(var1, Blocks.CAVE_AIR.defaultBlockState(), 3, var9, var8, var5);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 4, var9, var8, var5);
            }

            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 0, 4, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 4, var8, var5);
            this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 4, 4, var8, var5);
         }

      }
   }

   public static class StairsDown extends StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 11;
      private static final int DEPTH = 5;
      private final boolean isSource;

      public StairsDown(StructurePieceType var1, int var2, int var3, int var4, Direction var5) {
         super(var1, var2, makeBoundingBox(var3, 64, var4, var5, 5, 11, 5));
         this.isSource = true;
         this.setOrientation(var5);
         this.entryDoor = StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING;
      }

      public StairsDown(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_STAIRS_DOWN, var1, var3);
         this.isSource = false;
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
      }

      public StairsDown(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
         this.isSource = var2.getBoolean("Source");
      }

      public StairsDown(CompoundTag var1) {
         this(StructurePieceType.STRONGHOLD_STAIRS_DOWN, var1);
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Source", this.isSource);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         if (this.isSource) {
            StrongholdPieces.imposedPiece = FiveCrossing.class;
         }

         this.generateSmallDoorChildForward((StartPiece)var1, var2, var3, 1, 1);
      }

      public static StairsDown createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -7, 0, 5, 11, 5, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new StairsDown(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 10, 4, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(var1, var4, var5, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 4);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 6, 1, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 1, var5);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 6, 1, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5, 2, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 4, 3, var5);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 5, 3, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 4, 3, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 3, var5);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 4, 3, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 3, 2, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 2, 1, var5);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 3, 3, 1, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 2, 1, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 1, var5);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 2, 1, var5);
         this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 1, 2, var5);
         this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 1, 1, 3, var5);
      }
   }

   public static class StartPiece extends StairsDown {
      public PieceWeight previousPiece;
      @Nullable
      public PortalRoom portalRoomPiece;
      public final List<StructurePiece> pendingChildren = Lists.newArrayList();

      public StartPiece(RandomSource var1, int var2, int var3) {
         super(StructurePieceType.STRONGHOLD_START, 0, var2, var3, getRandomHorizontalDirection(var1));
      }

      public StartPiece(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_START, var1);
      }

      public BlockPos getLocatorPosition() {
         return this.portalRoomPiece != null ? this.portalRoomPiece.getLocatorPosition() : super.getLocatorPosition();
      }
   }

   public static class Straight extends StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 5;
      private static final int DEPTH = 7;
      private final boolean leftChild;
      private final boolean rightChild;

      public Straight(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.leftChild = var2.nextInt(2) == 0;
         this.rightChild = var2.nextInt(2) == 0;
      }

      public Straight(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT, var1);
         this.leftChild = var1.getBoolean("Left");
         this.rightChild = var1.getBoolean("Right");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Left", this.leftChild);
         var2.putBoolean("Right", this.rightChild);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         this.generateSmallDoorChildForward((StartPiece)var1, var2, var3, 1, 1);
         if (this.leftChild) {
            this.generateSmallDoorChildLeft((StartPiece)var1, var2, var3, 1, 2);
         }

         if (this.rightChild) {
            this.generateSmallDoorChildRight((StartPiece)var1, var2, var3, 1, 2);
         }

      }

      public static Straight createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new Straight(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 4, 6, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(var1, var4, var5, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         BlockState var8 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST);
         BlockState var9 = (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST);
         this.maybeGenerateBlock(var1, var5, var4, 0.1F, 1, 2, 1, var8);
         this.maybeGenerateBlock(var1, var5, var4, 0.1F, 3, 2, 1, var9);
         this.maybeGenerateBlock(var1, var5, var4, 0.1F, 1, 2, 5, var8);
         this.maybeGenerateBlock(var1, var5, var4, 0.1F, 3, 2, 5, var9);
         if (this.leftChild) {
            this.generateBox(var1, var5, 0, 1, 2, 0, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightChild) {
            this.generateBox(var1, var5, 4, 1, 2, 4, 3, 4, CAVE_AIR, CAVE_AIR, false);
         }

      }
   }

   public static class ChestCorridor extends StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 5;
      private static final int DEPTH = 7;
      private boolean hasPlacedChest;

      public ChestCorridor(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
      }

      public ChestCorridor(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_CHEST_CORRIDOR, var1);
         this.hasPlacedChest = var1.getBoolean("Chest");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Chest", this.hasPlacedChest);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         this.generateSmallDoorChildForward((StartPiece)var1, var2, var3, 1, 1);
      }

      public static ChestCorridor createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 7, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new ChestCorridor(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 4, 6, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 1, 1, 0);
         this.generateSmallDoor(var1, var4, var5, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 6);
         this.generateBox(var1, var5, 3, 1, 2, 3, 1, 4, Blocks.STONE_BRICKS.defaultBlockState(), Blocks.STONE_BRICKS.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 1, var5);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 1, 5, var5);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 2, var5);
         this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 3, 2, 4, var5);

         for(int var8 = 2; var8 <= 4; ++var8) {
            this.placeBlock(var1, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2, 1, var8, var5);
         }

         if (!this.hasPlacedChest && var5.isInside(this.getWorldPos(3, 2, 3))) {
            this.hasPlacedChest = true;
            this.createChest(var1, var5, var4, 3, 2, 3, BuiltInLootTables.STRONGHOLD_CORRIDOR);
         }

      }
   }

   public static class StraightStairsDown extends StrongholdPiece {
      private static final int WIDTH = 5;
      private static final int HEIGHT = 11;
      private static final int DEPTH = 8;

      public StraightStairsDown(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
      }

      public StraightStairsDown(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_STRAIGHT_STAIRS_DOWN, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         this.generateSmallDoorChildForward((StartPiece)var1, var2, var3, 1, 1);
      }

      public static StraightStairsDown createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -7, 0, 5, 11, 8, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new StraightStairsDown(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 10, 7, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 1, 7, 0);
         this.generateSmallDoor(var1, var4, var5, StrongholdPieces.StrongholdPiece.SmallDoorType.OPENING, 1, 1, 7);
         BlockState var8 = (BlockState)Blocks.COBBLESTONE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);

         for(int var9 = 0; var9 < 6; ++var9) {
            this.placeBlock(var1, var8, 1, 6 - var9, 1 + var9, var5);
            this.placeBlock(var1, var8, 2, 6 - var9, 1 + var9, var5);
            this.placeBlock(var1, var8, 3, 6 - var9, 1 + var9, var5);
            if (var9 < 5) {
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 1, 5 - var9, 1 + var9, var5);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 2, 5 - var9, 1 + var9, var5);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 5 - var9, 1 + var9, var5);
            }
         }

      }
   }

   public abstract static class Turn extends StrongholdPiece {
      protected static final int WIDTH = 5;
      protected static final int HEIGHT = 5;
      protected static final int DEPTH = 5;

      protected Turn(StructurePieceType var1, int var2, BoundingBox var3) {
         super(var1, var2, var3);
      }

      public Turn(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
      }
   }

   public static class LeftTurn extends Turn {
      public LeftTurn(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
      }

      public LeftTurn(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_LEFT_TURN, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         Direction var4 = this.getOrientation();
         if (var4 != Direction.NORTH && var4 != Direction.EAST) {
            this.generateSmallDoorChildRight((StartPiece)var1, var2, var3, 1, 1);
         } else {
            this.generateSmallDoorChildLeft((StartPiece)var1, var2, var3, 1, 1);
         }

      }

      public static LeftTurn createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new LeftTurn(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 4, 4, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 1, 1, 0);
         Direction var8 = this.getOrientation();
         if (var8 != Direction.NORTH && var8 != Direction.EAST) {
            this.generateBox(var1, var5, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(var1, var5, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

      }
   }

   public static class RightTurn extends Turn {
      public RightTurn(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
      }

      public RightTurn(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_RIGHT_TURN, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         Direction var4 = this.getOrientation();
         if (var4 != Direction.NORTH && var4 != Direction.EAST) {
            this.generateSmallDoorChildLeft((StartPiece)var1, var2, var3, 1, 1);
         } else {
            this.generateSmallDoorChildRight((StartPiece)var1, var2, var3, 1, 1);
         }

      }

      public static RightTurn createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 5, 5, 5, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new RightTurn(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 4, 4, 4, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 1, 1, 0);
         Direction var8 = this.getOrientation();
         if (var8 != Direction.NORTH && var8 != Direction.EAST) {
            this.generateBox(var1, var5, 0, 1, 1, 0, 3, 3, CAVE_AIR, CAVE_AIR, false);
         } else {
            this.generateBox(var1, var5, 4, 1, 1, 4, 3, 3, CAVE_AIR, CAVE_AIR, false);
         }

      }
   }

   public static class RoomCrossing extends StrongholdPiece {
      protected static final int WIDTH = 11;
      protected static final int HEIGHT = 7;
      protected static final int DEPTH = 11;
      protected final int type;

      public RoomCrossing(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.type = var2.nextInt(5);
      }

      public RoomCrossing(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_ROOM_CROSSING, var1);
         this.type = var1.getInt("Type");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putInt("Type", this.type);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         this.generateSmallDoorChildForward((StartPiece)var1, var2, var3, 4, 1);
         this.generateSmallDoorChildLeft((StartPiece)var1, var2, var3, 1, 4);
         this.generateSmallDoorChildRight((StartPiece)var1, var2, var3, 1, 4);
      }

      public static RoomCrossing createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 11, 7, 11, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new RoomCrossing(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 10, 6, 10, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 4, 1, 0);
         this.generateBox(var1, var5, 4, 1, 10, 6, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var5, 0, 1, 4, 0, 3, 6, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var5, 10, 1, 4, 10, 3, 6, CAVE_AIR, CAVE_AIR, false);
         switch (this.type) {
            case 0:
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, var5);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, var5);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, var5);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 4, 3, 5, var5);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 6, 3, 5, var5);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 5, 3, 4, var5);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH), 5, 3, 6, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 4, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 5, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 4, 1, 6, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 4, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 5, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 6, 1, 6, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 4, var5);
               this.placeBlock(var1, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), 5, 1, 6, var5);
               break;
            case 1:
               for(int var13 = 0; var13 < 5; ++var13) {
                  this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3, 1, 3 + var13, var5);
                  this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 7, 1, 3 + var13, var5);
                  this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3 + var13, 1, 3, var5);
                  this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 3 + var13, 1, 7, var5);
               }

               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 1, 5, var5);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 2, 5, var5);
               this.placeBlock(var1, Blocks.STONE_BRICKS.defaultBlockState(), 5, 3, 5, var5);
               this.placeBlock(var1, Blocks.WATER.defaultBlockState(), 5, 4, 5, var5);
               break;
            case 2:
               for(int var8 = 1; var8 <= 9; ++var8) {
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 1, 3, var8, var5);
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 9, 3, var8, var5);
               }

               for(int var9 = 1; var9 <= 9; ++var9) {
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), var9, 3, 1, var5);
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), var9, 3, 9, var5);
               }

               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 4, var5);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 1, 6, var5);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 4, var5);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 5, 3, 6, var5);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, 1, 5, var5);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, 1, 5, var5);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, 3, 5, var5);
               this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, 3, 5, var5);

               for(int var10 = 1; var10 <= 3; ++var10) {
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, var10, 4, var5);
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, var10, 4, var5);
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 4, var10, 6, var5);
                  this.placeBlock(var1, Blocks.COBBLESTONE.defaultBlockState(), 6, var10, 6, var5);
               }

               this.placeBlock(var1, Blocks.WALL_TORCH.defaultBlockState(), 5, 3, 5, var5);

               for(int var11 = 2; var11 <= 8; ++var11) {
                  this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 2, 3, var11, var5);
                  this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 3, 3, var11, var5);
                  if (var11 <= 3 || var11 >= 7) {
                     this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 4, 3, var11, var5);
                     this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 5, 3, var11, var5);
                     this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 6, 3, var11, var5);
                  }

                  this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 7, 3, var11, var5);
                  this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 8, 3, var11, var5);
               }

               BlockState var12 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.WEST);
               this.placeBlock(var1, var12, 9, 1, 3, var5);
               this.placeBlock(var1, var12, 9, 2, 3, var5);
               this.placeBlock(var1, var12, 9, 3, 3, var5);
               this.createChest(var1, var5, var4, 3, 4, 8, BuiltInLootTables.STRONGHOLD_CROSSING);
         }

      }
   }

   public static class PrisonHall extends StrongholdPiece {
      protected static final int WIDTH = 9;
      protected static final int HEIGHT = 5;
      protected static final int DEPTH = 11;

      public PrisonHall(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
      }

      public PrisonHall(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_PRISON_HALL, var1);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         this.generateSmallDoorChildForward((StartPiece)var1, var2, var3, 1, 1);
      }

      public static PrisonHall createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -1, -1, 0, 9, 5, 11, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new PrisonHall(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 8, 4, 10, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 1, 1, 0);
         this.generateBox(var1, var5, 1, 1, 10, 3, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var5, 4, 1, 1, 4, 3, 1, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 1, 3, 4, 3, 3, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 1, 7, 4, 3, 7, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 1, 9, 4, 3, 9, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int var8 = 1; var8 <= 3; ++var8) {
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, var8, 4, var5);
            this.placeBlock(var1, (BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true)).setValue(IronBarsBlock.EAST, true), 4, var8, 5, var5);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, var8, 6, var5);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 5, var8, 5, var5);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 6, var8, 5, var5);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true), 7, var8, 5, var5);
         }

         this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 2, var5);
         this.placeBlock(var1, (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true), 4, 3, 8, var5);
         BlockState var10 = (BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST);
         BlockState var9 = (BlockState)((BlockState)Blocks.IRON_DOOR.defaultBlockState().setValue(DoorBlock.FACING, Direction.WEST)).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
         this.placeBlock(var1, var10, 4, 1, 2, var5);
         this.placeBlock(var1, var9, 4, 2, 2, var5);
         this.placeBlock(var1, var10, 4, 1, 8, var5);
         this.placeBlock(var1, var9, 4, 2, 8, var5);
      }
   }

   public static class Library extends StrongholdPiece {
      protected static final int WIDTH = 14;
      protected static final int HEIGHT = 6;
      protected static final int TALL_HEIGHT = 11;
      protected static final int DEPTH = 15;
      private final boolean isTall;

      public Library(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.isTall = var3.getYSpan() > 6;
      }

      public Library(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_LIBRARY, var1);
         this.isTall = var1.getBoolean("Tall");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Tall", this.isTall);
      }

      public static Library createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 14, 11, 15, var5);
         if (!isOkBox(var7) || var0.findCollisionPiece(var7) != null) {
            var7 = BoundingBox.orientBox(var2, var3, var4, -4, -1, 0, 14, 6, 15, var5);
            if (!isOkBox(var7) || var0.findCollisionPiece(var7) != null) {
               return null;
            }
         }

         return new Library(var6, var1, var7, var5);
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         byte var8 = 11;
         if (!this.isTall) {
            var8 = 6;
         }

         this.generateBox(var1, var5, 0, 0, 0, 13, var8 - 1, 14, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 4, 1, 0);
         this.generateMaybeBox(var1, var5, var4, 0.07F, 2, 1, 1, 11, 4, 13, Blocks.COBWEB.defaultBlockState(), Blocks.COBWEB.defaultBlockState(), false, false);
         boolean var9 = true;
         boolean var10 = true;

         for(int var11 = 1; var11 <= 13; ++var11) {
            if ((var11 - 1) % 4 == 0) {
               this.generateBox(var1, var5, 1, 1, var11, 1, 4, var11, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.generateBox(var1, var5, 12, 1, var11, 12, 4, var11, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST), 2, 3, var11, var5);
               this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST), 11, 3, var11, var5);
               if (this.isTall) {
                  this.generateBox(var1, var5, 1, 6, var11, 1, 9, var11, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
                  this.generateBox(var1, var5, 12, 6, var11, 12, 9, var11, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
               }
            } else {
               this.generateBox(var1, var5, 1, 1, var11, 1, 4, var11, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               this.generateBox(var1, var5, 12, 1, var11, 12, 4, var11, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               if (this.isTall) {
                  this.generateBox(var1, var5, 1, 6, var11, 1, 9, var11, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
                  this.generateBox(var1, var5, 12, 6, var11, 12, 9, var11, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
               }
            }
         }

         for(int var20 = 3; var20 < 12; var20 += 2) {
            this.generateBox(var1, var5, 3, 1, var20, 4, 3, var20, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(var1, var5, 6, 1, var20, 7, 3, var20, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
            this.generateBox(var1, var5, 9, 1, var20, 10, 3, var20, Blocks.BOOKSHELF.defaultBlockState(), Blocks.BOOKSHELF.defaultBlockState(), false);
         }

         if (this.isTall) {
            this.generateBox(var1, var5, 1, 5, 1, 3, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(var1, var5, 10, 5, 1, 12, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(var1, var5, 4, 5, 1, 9, 5, 2, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.generateBox(var1, var5, 4, 5, 12, 9, 5, 13, Blocks.OAK_PLANKS.defaultBlockState(), Blocks.OAK_PLANKS.defaultBlockState(), false);
            this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 11, var5);
            this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 8, 5, 11, var5);
            this.placeBlock(var1, Blocks.OAK_PLANKS.defaultBlockState(), 9, 5, 10, var5);
            BlockState var21 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            BlockState var12 = (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.SOUTH, true);
            this.generateBox(var1, var5, 3, 6, 3, 3, 6, 11, var12, var12, false);
            this.generateBox(var1, var5, 10, 6, 3, 10, 6, 9, var12, var12, false);
            this.generateBox(var1, var5, 4, 6, 2, 9, 6, 2, var21, var21, false);
            this.generateBox(var1, var5, 4, 6, 12, 7, 6, 12, var21, var21, false);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 2, var5);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.EAST, true), 3, 6, 12, var5);
            this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.WEST, true), 10, 6, 2, var5);

            for(int var13 = 0; var13 <= 2; ++var13) {
               this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.SOUTH, true)).setValue(FenceBlock.WEST, true), 8 + var13, 6, 12 - var13, var5);
               if (var13 != 2) {
                  this.placeBlock(var1, (BlockState)((BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.NORTH, true)).setValue(FenceBlock.EAST, true), 8 + var13, 6, 11 - var13, var5);
               }
            }

            BlockState var22 = (BlockState)Blocks.LADDER.defaultBlockState().setValue(LadderBlock.FACING, Direction.SOUTH);
            this.placeBlock(var1, var22, 10, 1, 13, var5);
            this.placeBlock(var1, var22, 10, 2, 13, var5);
            this.placeBlock(var1, var22, 10, 3, 13, var5);
            this.placeBlock(var1, var22, 10, 4, 13, var5);
            this.placeBlock(var1, var22, 10, 5, 13, var5);
            this.placeBlock(var1, var22, 10, 6, 13, var5);
            this.placeBlock(var1, var22, 10, 7, 13, var5);
            boolean var14 = true;
            boolean var15 = true;
            BlockState var16 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.EAST, true);
            this.placeBlock(var1, var16, 6, 9, 7, var5);
            BlockState var17 = (BlockState)Blocks.OAK_FENCE.defaultBlockState().setValue(FenceBlock.WEST, true);
            this.placeBlock(var1, var17, 7, 9, 7, var5);
            this.placeBlock(var1, var16, 6, 8, 7, var5);
            this.placeBlock(var1, var17, 7, 8, 7, var5);
            BlockState var18 = (BlockState)((BlockState)var12.setValue(FenceBlock.WEST, true)).setValue(FenceBlock.EAST, true);
            this.placeBlock(var1, var18, 6, 7, 7, var5);
            this.placeBlock(var1, var18, 7, 7, 7, var5);
            this.placeBlock(var1, var16, 5, 7, 7, var5);
            this.placeBlock(var1, var17, 8, 7, 7, var5);
            this.placeBlock(var1, (BlockState)var16.setValue(FenceBlock.NORTH, true), 6, 7, 6, var5);
            this.placeBlock(var1, (BlockState)var16.setValue(FenceBlock.SOUTH, true), 6, 7, 8, var5);
            this.placeBlock(var1, (BlockState)var17.setValue(FenceBlock.NORTH, true), 7, 7, 6, var5);
            this.placeBlock(var1, (BlockState)var17.setValue(FenceBlock.SOUTH, true), 7, 7, 8, var5);
            BlockState var19 = Blocks.TORCH.defaultBlockState();
            this.placeBlock(var1, var19, 5, 8, 7, var5);
            this.placeBlock(var1, var19, 8, 8, 7, var5);
            this.placeBlock(var1, var19, 6, 8, 6, var5);
            this.placeBlock(var1, var19, 6, 8, 8, var5);
            this.placeBlock(var1, var19, 7, 8, 6, var5);
            this.placeBlock(var1, var19, 7, 8, 8, var5);
         }

         this.createChest(var1, var5, var4, 3, 3, 5, BuiltInLootTables.STRONGHOLD_LIBRARY);
         if (this.isTall) {
            this.placeBlock(var1, CAVE_AIR, 12, 9, 1, var5);
            this.createChest(var1, var5, var4, 12, 8, 1, BuiltInLootTables.STRONGHOLD_LIBRARY);
         }

      }
   }

   public static class FiveCrossing extends StrongholdPiece {
      protected static final int WIDTH = 10;
      protected static final int HEIGHT = 9;
      protected static final int DEPTH = 11;
      private final boolean leftLow;
      private final boolean leftHigh;
      private final boolean rightLow;
      private final boolean rightHigh;

      public FiveCrossing(int var1, RandomSource var2, BoundingBox var3, Direction var4) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, var1, var3);
         this.setOrientation(var4);
         this.entryDoor = this.randomSmallDoor(var2);
         this.leftLow = var2.nextBoolean();
         this.leftHigh = var2.nextBoolean();
         this.rightLow = var2.nextBoolean();
         this.rightHigh = var2.nextInt(3) > 0;
      }

      public FiveCrossing(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_FIVE_CROSSING, var1);
         this.leftLow = var1.getBoolean("leftLow");
         this.leftHigh = var1.getBoolean("leftHigh");
         this.rightLow = var1.getBoolean("rightLow");
         this.rightHigh = var1.getBoolean("rightHigh");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("leftLow", this.leftLow);
         var2.putBoolean("leftHigh", this.leftHigh);
         var2.putBoolean("rightLow", this.rightLow);
         var2.putBoolean("rightHigh", this.rightHigh);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         int var4 = 3;
         int var5 = 5;
         Direction var6 = this.getOrientation();
         if (var6 == Direction.WEST || var6 == Direction.NORTH) {
            var4 = 8 - var4;
            var5 = 8 - var5;
         }

         this.generateSmallDoorChildForward((StartPiece)var1, var2, var3, 5, 1);
         if (this.leftLow) {
            this.generateSmallDoorChildLeft((StartPiece)var1, var2, var3, var4, 1);
         }

         if (this.leftHigh) {
            this.generateSmallDoorChildLeft((StartPiece)var1, var2, var3, var5, 7);
         }

         if (this.rightLow) {
            this.generateSmallDoorChildRight((StartPiece)var1, var2, var3, var4, 1);
         }

         if (this.rightHigh) {
            this.generateSmallDoorChildRight((StartPiece)var1, var2, var3, var5, 7);
         }

      }

      public static FiveCrossing createPiece(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5, int var6) {
         BoundingBox var7 = BoundingBox.orientBox(var2, var3, var4, -4, -3, 0, 10, 9, 11, var5);
         return isOkBox(var7) && var0.findCollisionPiece(var7) == null ? new FiveCrossing(var6, var1, var7, var5) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 9, 8, 10, true, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, this.entryDoor, 4, 3, 0);
         if (this.leftLow) {
            this.generateBox(var1, var5, 0, 3, 1, 0, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightLow) {
            this.generateBox(var1, var5, 9, 3, 1, 9, 5, 3, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.leftHigh) {
            this.generateBox(var1, var5, 0, 5, 7, 0, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         if (this.rightHigh) {
            this.generateBox(var1, var5, 9, 5, 7, 9, 7, 9, CAVE_AIR, CAVE_AIR, false);
         }

         this.generateBox(var1, var5, 5, 1, 10, 7, 3, 10, CAVE_AIR, CAVE_AIR, false);
         this.generateBox(var1, var5, 1, 2, 1, 8, 2, 6, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 1, 5, 4, 4, 9, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 8, 1, 5, 8, 4, 9, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 4, 7, 3, 4, 9, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 3, 5, 3, 3, 6, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 3, 4, 3, 3, 4, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 4, 6, 3, 4, 6, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 1, 7, 7, 1, 8, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 5, 1, 9, 7, 1, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 2, 7, 7, 2, 7, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var5, 4, 5, 7, 4, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var5, 8, 5, 7, 8, 5, 9, Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), Blocks.SMOOTH_STONE_SLAB.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 5, 7, 7, 5, 9, (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), (BlockState)Blocks.SMOOTH_STONE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE), false);
         this.placeBlock(var1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH), 6, 5, 6, var5);
      }
   }

   public static class PortalRoom extends StrongholdPiece {
      protected static final int WIDTH = 11;
      protected static final int HEIGHT = 8;
      protected static final int DEPTH = 16;
      private boolean hasPlacedSpawner;

      public PortalRoom(int var1, BoundingBox var2, Direction var3) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, var1, var2);
         this.setOrientation(var3);
      }

      public PortalRoom(CompoundTag var1) {
         super(StructurePieceType.STRONGHOLD_PORTAL_ROOM, var1);
         this.hasPlacedSpawner = var1.getBoolean("Mob");
      }

      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("Mob", this.hasPlacedSpawner);
      }

      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         if (var1 != null) {
            ((StartPiece)var1).portalRoomPiece = this;
         }

      }

      public static PortalRoom createPiece(StructurePieceAccessor var0, int var1, int var2, int var3, Direction var4, int var5) {
         BoundingBox var6 = BoundingBox.orientBox(var1, var2, var3, -4, -1, 0, 11, 8, 16, var4);
         return isOkBox(var6) && var0.findCollisionPiece(var6) == null ? new PortalRoom(var5, var6, var4) : null;
      }

      public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 0, 0, 10, 7, 15, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateSmallDoor(var1, var4, var5, StrongholdPieces.StrongholdPiece.SmallDoorType.GRATES, 4, 1, 0);
         boolean var8 = true;
         this.generateBox(var1, var5, 1, 6, 1, 1, 6, 14, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 9, 6, 1, 9, 6, 14, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 6, 1, 8, 6, 2, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 2, 6, 14, 8, 6, 14, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 1, 1, 2, 1, 4, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 8, 1, 1, 9, 1, 4, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 1, 1, 1, 1, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(var1, var5, 9, 1, 1, 9, 1, 3, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         this.generateBox(var1, var5, 3, 1, 8, 7, 1, 12, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 1, 9, 6, 1, 11, Blocks.LAVA.defaultBlockState(), Blocks.LAVA.defaultBlockState(), false);
         BlockState var9 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, true)).setValue(IronBarsBlock.SOUTH, true);
         BlockState var10 = (BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.WEST, true)).setValue(IronBarsBlock.EAST, true);

         for(int var11 = 3; var11 < 14; var11 += 2) {
            this.generateBox(var1, var5, 0, 3, var11, 0, 4, var11, var9, var9, false);
            this.generateBox(var1, var5, 10, 3, var11, 10, 4, var11, var9, var9, false);
         }

         for(int var21 = 2; var21 < 9; var21 += 2) {
            this.generateBox(var1, var5, var21, 3, 15, var21, 4, 15, var10, var10, false);
         }

         BlockState var22 = (BlockState)Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         this.generateBox(var1, var5, 4, 1, 5, 6, 1, 7, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 2, 6, 6, 2, 7, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);
         this.generateBox(var1, var5, 4, 3, 7, 6, 3, 7, false, var4, StrongholdPieces.SMOOTH_STONE_SELECTOR);

         for(int var12 = 4; var12 <= 6; ++var12) {
            this.placeBlock(var1, var22, var12, 1, 4, var5);
            this.placeBlock(var1, var22, var12, 2, 5, var5);
            this.placeBlock(var1, var22, var12, 3, 6, var5);
         }

         BlockState var23 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.NORTH);
         BlockState var13 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.SOUTH);
         BlockState var14 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.EAST);
         BlockState var15 = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.FACING, Direction.WEST);
         boolean var16 = true;
         boolean[] var17 = new boolean[12];

         for(int var18 = 0; var18 < var17.length; ++var18) {
            var17[var18] = var4.nextFloat() > 0.9F;
            var16 &= var17[var18];
         }

         this.placeBlock(var1, (BlockState)var23.setValue(EndPortalFrameBlock.HAS_EYE, var17[0]), 4, 3, 8, var5);
         this.placeBlock(var1, (BlockState)var23.setValue(EndPortalFrameBlock.HAS_EYE, var17[1]), 5, 3, 8, var5);
         this.placeBlock(var1, (BlockState)var23.setValue(EndPortalFrameBlock.HAS_EYE, var17[2]), 6, 3, 8, var5);
         this.placeBlock(var1, (BlockState)var13.setValue(EndPortalFrameBlock.HAS_EYE, var17[3]), 4, 3, 12, var5);
         this.placeBlock(var1, (BlockState)var13.setValue(EndPortalFrameBlock.HAS_EYE, var17[4]), 5, 3, 12, var5);
         this.placeBlock(var1, (BlockState)var13.setValue(EndPortalFrameBlock.HAS_EYE, var17[5]), 6, 3, 12, var5);
         this.placeBlock(var1, (BlockState)var14.setValue(EndPortalFrameBlock.HAS_EYE, var17[6]), 3, 3, 9, var5);
         this.placeBlock(var1, (BlockState)var14.setValue(EndPortalFrameBlock.HAS_EYE, var17[7]), 3, 3, 10, var5);
         this.placeBlock(var1, (BlockState)var14.setValue(EndPortalFrameBlock.HAS_EYE, var17[8]), 3, 3, 11, var5);
         this.placeBlock(var1, (BlockState)var15.setValue(EndPortalFrameBlock.HAS_EYE, var17[9]), 7, 3, 9, var5);
         this.placeBlock(var1, (BlockState)var15.setValue(EndPortalFrameBlock.HAS_EYE, var17[10]), 7, 3, 10, var5);
         this.placeBlock(var1, (BlockState)var15.setValue(EndPortalFrameBlock.HAS_EYE, var17[11]), 7, 3, 11, var5);
         if (var16) {
            BlockState var24 = Blocks.END_PORTAL.defaultBlockState();
            this.placeBlock(var1, var24, 4, 3, 9, var5);
            this.placeBlock(var1, var24, 5, 3, 9, var5);
            this.placeBlock(var1, var24, 6, 3, 9, var5);
            this.placeBlock(var1, var24, 4, 3, 10, var5);
            this.placeBlock(var1, var24, 5, 3, 10, var5);
            this.placeBlock(var1, var24, 6, 3, 10, var5);
            this.placeBlock(var1, var24, 4, 3, 11, var5);
            this.placeBlock(var1, var24, 5, 3, 11, var5);
            this.placeBlock(var1, var24, 6, 3, 11, var5);
         }

         if (!this.hasPlacedSpawner) {
            BlockPos.MutableBlockPos var25 = this.getWorldPos(5, 3, 6);
            if (var5.isInside(var25)) {
               this.hasPlacedSpawner = true;
               var1.setBlock(var25, Blocks.SPAWNER.defaultBlockState(), 2);
               BlockEntity var19 = var1.getBlockEntity(var25);
               if (var19 instanceof SpawnerBlockEntity) {
                  SpawnerBlockEntity var20 = (SpawnerBlockEntity)var19;
                  var20.setEntityId(EntityType.SILVERFISH, var4);
               }
            }
         }

      }
   }

   static class SmoothStoneSelector extends StructurePiece.BlockSelector {
      SmoothStoneSelector() {
         super();
      }

      public void next(RandomSource var1, int var2, int var3, int var4, boolean var5) {
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
   }
}
