package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class WoodlandMansionPieces {
   public WoodlandMansionPieces() {
      super();
   }

   public static void generateMansion(
      StructureTemplateManager var0, BlockPos var1, Rotation var2, List<WoodlandMansionPieces.WoodlandMansionPiece> var3, RandomSource var4
   ) {
      WoodlandMansionPieces.MansionGrid var5 = new WoodlandMansionPieces.MansionGrid(var4);
      WoodlandMansionPieces.MansionPiecePlacer var6 = new WoodlandMansionPieces.MansionPiecePlacer(var0, var4);
      var6.createMansion(var1, var2, var3, var5);
   }

   static class FirstFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      FirstFloorRoomCollection() {
         super();
      }

      @Override
      public String get1x1(RandomSource var1) {
         return "1x1_a" + (var1.nextInt(5) + 1);
      }

      @Override
      public String get1x1Secret(RandomSource var1) {
         return "1x1_as" + (var1.nextInt(4) + 1);
      }

      @Override
      public String get1x2SideEntrance(RandomSource var1, boolean var2) {
         return "1x2_a" + (var1.nextInt(9) + 1);
      }

      @Override
      public String get1x2FrontEntrance(RandomSource var1, boolean var2) {
         return "1x2_b" + (var1.nextInt(5) + 1);
      }

      @Override
      public String get1x2Secret(RandomSource var1) {
         return "1x2_s" + (var1.nextInt(2) + 1);
      }

      @Override
      public String get2x2(RandomSource var1) {
         return "2x2_a" + (var1.nextInt(4) + 1);
      }

      @Override
      public String get2x2Secret(RandomSource var1) {
         return "2x2_s1";
      }
   }

   abstract static class FloorRoomCollection {
      FloorRoomCollection() {
         super();
      }

      public abstract String get1x1(RandomSource var1);

      public abstract String get1x1Secret(RandomSource var1);

      public abstract String get1x2SideEntrance(RandomSource var1, boolean var2);

      public abstract String get1x2FrontEntrance(RandomSource var1, boolean var2);

      public abstract String get1x2Secret(RandomSource var1);

      public abstract String get2x2(RandomSource var1);

      public abstract String get2x2Secret(RandomSource var1);
   }

   static class MansionGrid {
      private static final int DEFAULT_SIZE = 11;
      private static final int CLEAR = 0;
      private static final int CORRIDOR = 1;
      private static final int ROOM = 2;
      private static final int START_ROOM = 3;
      private static final int TEST_ROOM = 4;
      private static final int BLOCKED = 5;
      private static final int ROOM_1x1 = 65536;
      private static final int ROOM_1x2 = 131072;
      private static final int ROOM_2x2 = 262144;
      private static final int ROOM_ORIGIN_FLAG = 1048576;
      private static final int ROOM_DOOR_FLAG = 2097152;
      private static final int ROOM_STAIRS_FLAG = 4194304;
      private static final int ROOM_CORRIDOR_FLAG = 8388608;
      private static final int ROOM_TYPE_MASK = 983040;
      private static final int ROOM_ID_MASK = 65535;
      private final RandomSource random;
      final WoodlandMansionPieces.SimpleGrid baseGrid;
      final WoodlandMansionPieces.SimpleGrid thirdFloorGrid;
      final WoodlandMansionPieces.SimpleGrid[] floorRooms;
      final int entranceX;
      final int entranceY;

      public MansionGrid(RandomSource var1) {
         super();
         this.random = var1;
         byte var2 = 11;
         this.entranceX = 7;
         this.entranceY = 4;
         this.baseGrid = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.baseGrid.set(this.entranceX, this.entranceY, this.entranceX + 1, this.entranceY + 1, 3);
         this.baseGrid.set(this.entranceX - 1, this.entranceY, this.entranceX - 1, this.entranceY + 1, 2);
         this.baseGrid.set(this.entranceX + 2, this.entranceY - 2, this.entranceX + 3, this.entranceY + 3, 5);
         this.baseGrid.set(this.entranceX + 1, this.entranceY - 2, this.entranceX + 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX + 1, this.entranceY + 2, this.entranceX + 1, this.entranceY + 3, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY - 1, 1);
         this.baseGrid.set(this.entranceX - 1, this.entranceY + 2, 1);
         this.baseGrid.set(0, 0, 11, 1, 5);
         this.baseGrid.set(0, 9, 11, 11, 5);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY - 2, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX, this.entranceY + 3, Direction.WEST, 6);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY - 1, Direction.WEST, 3);
         this.recursiveCorridor(this.baseGrid, this.entranceX - 2, this.entranceY + 2, Direction.WEST, 3);

         while (this.cleanEdges(this.baseGrid)) {
         }

         this.floorRooms = new WoodlandMansionPieces.SimpleGrid[3];
         this.floorRooms[0] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[1] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.floorRooms[2] = new WoodlandMansionPieces.SimpleGrid(11, 11, 5);
         this.identifyRooms(this.baseGrid, this.floorRooms[0]);
         this.identifyRooms(this.baseGrid, this.floorRooms[1]);
         this.floorRooms[0].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.floorRooms[1].set(this.entranceX + 1, this.entranceY, this.entranceX + 1, this.entranceY + 1, 8388608);
         this.thirdFloorGrid = new WoodlandMansionPieces.SimpleGrid(this.baseGrid.width, this.baseGrid.height, 5);
         this.setupThirdFloor();
         this.identifyRooms(this.thirdFloorGrid, this.floorRooms[2]);
      }

      public static boolean isHouse(WoodlandMansionPieces.SimpleGrid var0, int var1, int var2) {
         int var3 = var0.get(var1, var2);
         return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4;
      }

      public boolean isRoomId(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, int var4, int var5) {
         return (this.floorRooms[var4].get(var2, var3) & 65535) == var5;
      }

      @Nullable
      public Direction get1x2RoomDirection(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, int var4, int var5) {
         for (Direction var7 : Direction.Plane.HORIZONTAL) {
            if (this.isRoomId(var1, var2 + var7.getStepX(), var3 + var7.getStepZ(), var4, var5)) {
               return var7;
            }
         }

         return null;
      }

      private void recursiveCorridor(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, Direction var4, int var5) {
         if (var5 > 0) {
            var1.set(var2, var3, 1);
            var1.setif(var2 + var4.getStepX(), var3 + var4.getStepZ(), 0, 1);

            for (int var6 = 0; var6 < 8; var6++) {
               Direction var7 = Direction.from2DDataValue(this.random.nextInt(4));
               if (var7 != var4.getOpposite() && (var7 != Direction.EAST || !this.random.nextBoolean())) {
                  int var8 = var2 + var4.getStepX();
                  int var9 = var3 + var4.getStepZ();
                  if (var1.get(var8 + var7.getStepX(), var9 + var7.getStepZ()) == 0 && var1.get(var8 + var7.getStepX() * 2, var9 + var7.getStepZ() * 2) == 0) {
                     this.recursiveCorridor(var1, var2 + var4.getStepX() + var7.getStepX(), var3 + var4.getStepZ() + var7.getStepZ(), var7, var5 - 1);
                     break;
                  }
               }
            }

            Direction var10 = var4.getClockWise();
            Direction var11 = var4.getCounterClockWise();
            var1.setif(var2 + var10.getStepX(), var3 + var10.getStepZ(), 0, 2);
            var1.setif(var2 + var11.getStepX(), var3 + var11.getStepZ(), 0, 2);
            var1.setif(var2 + var4.getStepX() + var10.getStepX(), var3 + var4.getStepZ() + var10.getStepZ(), 0, 2);
            var1.setif(var2 + var4.getStepX() + var11.getStepX(), var3 + var4.getStepZ() + var11.getStepZ(), 0, 2);
            var1.setif(var2 + var4.getStepX() * 2, var3 + var4.getStepZ() * 2, 0, 2);
            var1.setif(var2 + var10.getStepX() * 2, var3 + var10.getStepZ() * 2, 0, 2);
            var1.setif(var2 + var11.getStepX() * 2, var3 + var11.getStepZ() * 2, 0, 2);
         }
      }

      private boolean cleanEdges(WoodlandMansionPieces.SimpleGrid var1) {
         boolean var2 = false;

         for (int var3 = 0; var3 < var1.height; var3++) {
            for (int var4 = 0; var4 < var1.width; var4++) {
               if (var1.get(var4, var3) == 0) {
                  int var5 = 0;
                  var5 += isHouse(var1, var4 + 1, var3) ? 1 : 0;
                  var5 += isHouse(var1, var4 - 1, var3) ? 1 : 0;
                  var5 += isHouse(var1, var4, var3 + 1) ? 1 : 0;
                  var5 += isHouse(var1, var4, var3 - 1) ? 1 : 0;
                  if (var5 >= 3) {
                     var1.set(var4, var3, 2);
                     var2 = true;
                  } else if (var5 == 2) {
                     int var6 = 0;
                     var6 += isHouse(var1, var4 + 1, var3 + 1) ? 1 : 0;
                     var6 += isHouse(var1, var4 - 1, var3 + 1) ? 1 : 0;
                     var6 += isHouse(var1, var4 + 1, var3 - 1) ? 1 : 0;
                     var6 += isHouse(var1, var4 - 1, var3 - 1) ? 1 : 0;
                     if (var6 <= 1) {
                        var1.set(var4, var3, 2);
                        var2 = true;
                     }
                  }
               }
            }
         }

         return var2;
      }

      private void setupThirdFloor() {
         ArrayList var1 = Lists.newArrayList();
         WoodlandMansionPieces.SimpleGrid var2 = this.floorRooms[1];

         for (int var3 = 0; var3 < this.thirdFloorGrid.height; var3++) {
            for (int var4 = 0; var4 < this.thirdFloorGrid.width; var4++) {
               int var5 = var2.get(var4, var3);
               int var6 = var5 & 983040;
               if (var6 == 131072 && (var5 & 2097152) == 2097152) {
                  var1.add(new Tuple<>(var4, var3));
               }
            }
         }

         if (var1.isEmpty()) {
            this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
         } else {
            Tuple var11 = (Tuple)var1.get(this.random.nextInt(var1.size()));
            int var12 = var2.get((Integer)var11.getA(), (Integer)var11.getB());
            var2.set((Integer)var11.getA(), (Integer)var11.getB(), var12 | 4194304);
            Direction var13 = this.get1x2RoomDirection(this.baseGrid, (Integer)var11.getA(), (Integer)var11.getB(), 1, var12 & 65535);
            int var14 = (Integer)var11.getA() + var13.getStepX();
            int var7 = (Integer)var11.getB() + var13.getStepZ();

            for (int var8 = 0; var8 < this.thirdFloorGrid.height; var8++) {
               for (int var9 = 0; var9 < this.thirdFloorGrid.width; var9++) {
                  if (!isHouse(this.baseGrid, var9, var8)) {
                     this.thirdFloorGrid.set(var9, var8, 5);
                  } else if (var9 == (Integer)var11.getA() && var8 == (Integer)var11.getB()) {
                     this.thirdFloorGrid.set(var9, var8, 3);
                  } else if (var9 == var14 && var8 == var7) {
                     this.thirdFloorGrid.set(var9, var8, 3);
                     this.floorRooms[2].set(var9, var8, 8388608);
                  }
               }
            }

            ArrayList var15 = Lists.newArrayList();

            for (Direction var10 : Direction.Plane.HORIZONTAL) {
               if (this.thirdFloorGrid.get(var14 + var10.getStepX(), var7 + var10.getStepZ()) == 0) {
                  var15.add(var10);
               }
            }

            if (var15.isEmpty()) {
               this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
               var2.set((Integer)var11.getA(), (Integer)var11.getB(), var12);
            } else {
               Direction var17 = (Direction)var15.get(this.random.nextInt(var15.size()));
               this.recursiveCorridor(this.thirdFloorGrid, var14 + var17.getStepX(), var7 + var17.getStepZ(), var17, 4);

               while (this.cleanEdges(this.thirdFloorGrid)) {
               }
            }
         }
      }

      private void identifyRooms(WoodlandMansionPieces.SimpleGrid var1, WoodlandMansionPieces.SimpleGrid var2) {
         ObjectArrayList var3 = new ObjectArrayList();

         for (int var4 = 0; var4 < var1.height; var4++) {
            for (int var5 = 0; var5 < var1.width; var5++) {
               if (var1.get(var5, var4) == 2) {
                  var3.add(new Tuple<>(var5, var4));
               }
            }
         }

         Util.shuffle(var3, this.random);
         int var19 = 10;
         ObjectListIterator var20 = var3.iterator();

         while (var20.hasNext()) {
            Tuple var6 = (Tuple)var20.next();
            int var7 = (Integer)var6.getA();
            int var8 = (Integer)var6.getB();
            if (var2.get(var7, var8) == 0) {
               int var9 = var7;
               int var10 = var7;
               int var11 = var8;
               int var12 = var8;
               int var13 = 65536;
               if (var2.get(var7 + 1, var8) == 0
                  && var2.get(var7, var8 + 1) == 0
                  && var2.get(var7 + 1, var8 + 1) == 0
                  && var1.get(var7 + 1, var8) == 2
                  && var1.get(var7, var8 + 1) == 2
                  && var1.get(var7 + 1, var8 + 1) == 2) {
                  var10 = var7 + 1;
                  var12 = var8 + 1;
                  var13 = 262144;
               } else if (var2.get(var7 - 1, var8) == 0
                  && var2.get(var7, var8 + 1) == 0
                  && var2.get(var7 - 1, var8 + 1) == 0
                  && var1.get(var7 - 1, var8) == 2
                  && var1.get(var7, var8 + 1) == 2
                  && var1.get(var7 - 1, var8 + 1) == 2) {
                  var9 = var7 - 1;
                  var12 = var8 + 1;
                  var13 = 262144;
               } else if (var2.get(var7 - 1, var8) == 0
                  && var2.get(var7, var8 - 1) == 0
                  && var2.get(var7 - 1, var8 - 1) == 0
                  && var1.get(var7 - 1, var8) == 2
                  && var1.get(var7, var8 - 1) == 2
                  && var1.get(var7 - 1, var8 - 1) == 2) {
                  var9 = var7 - 1;
                  var11 = var8 - 1;
                  var13 = 262144;
               } else if (var2.get(var7 + 1, var8) == 0 && var1.get(var7 + 1, var8) == 2) {
                  var10 = var7 + 1;
                  var13 = 131072;
               } else if (var2.get(var7, var8 + 1) == 0 && var1.get(var7, var8 + 1) == 2) {
                  var12 = var8 + 1;
                  var13 = 131072;
               } else if (var2.get(var7 - 1, var8) == 0 && var1.get(var7 - 1, var8) == 2) {
                  var9 = var7 - 1;
                  var13 = 131072;
               } else if (var2.get(var7, var8 - 1) == 0 && var1.get(var7, var8 - 1) == 2) {
                  var11 = var8 - 1;
                  var13 = 131072;
               }

               int var14 = this.random.nextBoolean() ? var9 : var10;
               int var15 = this.random.nextBoolean() ? var11 : var12;
               int var16 = 2097152;
               if (!var1.edgesTo(var14, var15, 1)) {
                  var14 = var14 == var9 ? var10 : var9;
                  var15 = var15 == var11 ? var12 : var11;
                  if (!var1.edgesTo(var14, var15, 1)) {
                     var15 = var15 == var11 ? var12 : var11;
                     if (!var1.edgesTo(var14, var15, 1)) {
                        var14 = var14 == var9 ? var10 : var9;
                        var15 = var15 == var11 ? var12 : var11;
                        if (!var1.edgesTo(var14, var15, 1)) {
                           var16 = 0;
                           var14 = var9;
                           var15 = var11;
                        }
                     }
                  }
               }

               for (int var17 = var11; var17 <= var12; var17++) {
                  for (int var18 = var9; var18 <= var10; var18++) {
                     if (var18 == var14 && var17 == var15) {
                        var2.set(var18, var17, 1048576 | var16 | var13 | var19);
                     } else {
                        var2.set(var18, var17, var13 | var19);
                     }
                  }
               }

               var19++;
            }
         }
      }
   }

   static class MansionPiecePlacer {
      private final StructureTemplateManager structureTemplateManager;
      private final RandomSource random;
      private int startX;
      private int startY;

      public MansionPiecePlacer(StructureTemplateManager var1, RandomSource var2) {
         super();
         this.structureTemplateManager = var1;
         this.random = var2;
      }

      public void createMansion(BlockPos var1, Rotation var2, List<WoodlandMansionPieces.WoodlandMansionPiece> var3, WoodlandMansionPieces.MansionGrid var4) {
         WoodlandMansionPieces.PlacementData var5 = new WoodlandMansionPieces.PlacementData();
         var5.position = var1;
         var5.rotation = var2;
         var5.wallType = "wall_flat";
         WoodlandMansionPieces.PlacementData var6 = new WoodlandMansionPieces.PlacementData();
         this.entrance(var3, var5);
         var6.position = var5.position.above(8);
         var6.rotation = var5.rotation;
         var6.wallType = "wall_window";
         if (!var3.isEmpty()) {
         }

         WoodlandMansionPieces.SimpleGrid var7 = var4.baseGrid;
         WoodlandMansionPieces.SimpleGrid var8 = var4.thirdFloorGrid;
         this.startX = var4.entranceX + 1;
         this.startY = var4.entranceY + 1;
         int var9 = var4.entranceX + 1;
         int var10 = var4.entranceY;
         this.traverseOuterWalls(var3, var5, var7, Direction.SOUTH, this.startX, this.startY, var9, var10);
         this.traverseOuterWalls(var3, var6, var7, Direction.SOUTH, this.startX, this.startY, var9, var10);
         WoodlandMansionPieces.PlacementData var11 = new WoodlandMansionPieces.PlacementData();
         var11.position = var5.position.above(19);
         var11.rotation = var5.rotation;
         var11.wallType = "wall_window";
         boolean var12 = false;

         for (int var13 = 0; var13 < var8.height && !var12; var13++) {
            for (int var14 = var8.width - 1; var14 >= 0 && !var12; var14--) {
               if (WoodlandMansionPieces.MansionGrid.isHouse(var8, var14, var13)) {
                  var11.position = var11.position.relative(var2.rotate(Direction.SOUTH), 8 + (var13 - this.startY) * 8);
                  var11.position = var11.position.relative(var2.rotate(Direction.EAST), (var14 - this.startX) * 8);
                  this.traverseWallPiece(var3, var11);
                  this.traverseOuterWalls(var3, var11, var8, Direction.SOUTH, var14, var13, var14, var13);
                  var12 = true;
               }
            }
         }

         this.createRoof(var3, var1.above(16), var2, var7, var8);
         this.createRoof(var3, var1.above(27), var2, var8, null);
         if (!var3.isEmpty()) {
         }

         WoodlandMansionPieces.FloorRoomCollection[] var33 = new WoodlandMansionPieces.FloorRoomCollection[]{
            new WoodlandMansionPieces.FirstFloorRoomCollection(),
            new WoodlandMansionPieces.SecondFloorRoomCollection(),
            new WoodlandMansionPieces.ThirdFloorRoomCollection()
         };

         for (int var34 = 0; var34 < 3; var34++) {
            BlockPos var15 = var1.above(8 * var34 + (var34 == 2 ? 3 : 0));
            WoodlandMansionPieces.SimpleGrid var16 = var4.floorRooms[var34];
            WoodlandMansionPieces.SimpleGrid var17 = var34 == 2 ? var8 : var7;
            String var18 = var34 == 0 ? "carpet_south_1" : "carpet_south_2";
            String var19 = var34 == 0 ? "carpet_west_1" : "carpet_west_2";

            for (int var20 = 0; var20 < var17.height; var20++) {
               for (int var21 = 0; var21 < var17.width; var21++) {
                  if (var17.get(var21, var20) == 1) {
                     BlockPos var22 = var15.relative(var2.rotate(Direction.SOUTH), 8 + (var20 - this.startY) * 8);
                     var22 = var22.relative(var2.rotate(Direction.EAST), (var21 - this.startX) * 8);
                     var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "corridor_floor", var22, var2));
                     if (var17.get(var21, var20 - 1) == 1 || (var16.get(var21, var20 - 1) & 8388608) == 8388608) {
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "carpet_north", var22.relative(var2.rotate(Direction.EAST), 1).above(), var2
                           )
                        );
                     }

                     if (var17.get(var21 + 1, var20) == 1 || (var16.get(var21 + 1, var20) & 8388608) == 8388608) {
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager,
                              "carpet_east",
                              var22.relative(var2.rotate(Direction.SOUTH), 1).relative(var2.rotate(Direction.EAST), 5).above(),
                              var2
                           )
                        );
                     }

                     if (var17.get(var21, var20 + 1) == 1 || (var16.get(var21, var20 + 1) & 8388608) == 8388608) {
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager,
                              var18,
                              var22.relative(var2.rotate(Direction.SOUTH), 5).relative(var2.rotate(Direction.WEST), 1),
                              var2
                           )
                        );
                     }

                     if (var17.get(var21 - 1, var20) == 1 || (var16.get(var21 - 1, var20) & 8388608) == 8388608) {
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager,
                              var19,
                              var22.relative(var2.rotate(Direction.WEST), 1).relative(var2.rotate(Direction.NORTH), 1),
                              var2
                           )
                        );
                     }
                  }
               }
            }

            String var35 = var34 == 0 ? "indoors_wall_1" : "indoors_wall_2";
            String var36 = var34 == 0 ? "indoors_door_1" : "indoors_door_2";
            ArrayList var38 = Lists.newArrayList();

            for (int var23 = 0; var23 < var17.height; var23++) {
               for (int var24 = 0; var24 < var17.width; var24++) {
                  boolean var25 = var34 == 2 && var17.get(var24, var23) == 3;
                  if (var17.get(var24, var23) == 2 || var25) {
                     int var26 = var16.get(var24, var23);
                     int var27 = var26 & 983040;
                     int var28 = var26 & 65535;
                     var25 = var25 && (var26 & 8388608) == 8388608;
                     var38.clear();
                     if ((var26 & 2097152) == 2097152) {
                        for (Direction var30 : Direction.Plane.HORIZONTAL) {
                           if (var17.get(var24 + var30.getStepX(), var23 + var30.getStepZ()) == 1) {
                              var38.add(var30);
                           }
                        }
                     }

                     Direction var40 = null;
                     if (!var38.isEmpty()) {
                        var40 = (Direction)var38.get(this.random.nextInt(var38.size()));
                     } else if ((var26 & 1048576) == 1048576) {
                        var40 = Direction.UP;
                     }

                     BlockPos var41 = var15.relative(var2.rotate(Direction.SOUTH), 8 + (var23 - this.startY) * 8);
                     var41 = var41.relative(var2.rotate(Direction.EAST), -1 + (var24 - this.startX) * 8);
                     if (WoodlandMansionPieces.MansionGrid.isHouse(var17, var24 - 1, var23) && !var4.isRoomId(var17, var24 - 1, var23, var34, var28)) {
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var40 == Direction.WEST ? var36 : var35, var41, var2)
                        );
                     }

                     if (var17.get(var24 + 1, var23) == 1 && !var25) {
                        BlockPos var31 = var41.relative(var2.rotate(Direction.EAST), 8);
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var40 == Direction.EAST ? var36 : var35, var31, var2)
                        );
                     }

                     if (WoodlandMansionPieces.MansionGrid.isHouse(var17, var24, var23 + 1) && !var4.isRoomId(var17, var24, var23 + 1, var34, var28)) {
                        BlockPos var43 = var41.relative(var2.rotate(Direction.SOUTH), 7);
                        var43 = var43.relative(var2.rotate(Direction.EAST), 7);
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, var40 == Direction.SOUTH ? var36 : var35, var43, var2.getRotated(Rotation.CLOCKWISE_90)
                           )
                        );
                     }

                     if (var17.get(var24, var23 - 1) == 1 && !var25) {
                        BlockPos var45 = var41.relative(var2.rotate(Direction.NORTH), 1);
                        var45 = var45.relative(var2.rotate(Direction.EAST), 7);
                        var3.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, var40 == Direction.NORTH ? var36 : var35, var45, var2.getRotated(Rotation.CLOCKWISE_90)
                           )
                        );
                     }

                     if (var27 == 65536) {
                        this.addRoom1x1(var3, var41, var2, var40, var33[var34]);
                     } else if (var27 == 131072 && var40 != null) {
                        Direction var48 = var4.get1x2RoomDirection(var17, var24, var23, var34, var28);
                        boolean var32 = (var26 & 4194304) == 4194304;
                        this.addRoom1x2(var3, var41, var2, var48, var40, var33[var34], var32);
                     } else if (var27 == 262144 && var40 != null && var40 != Direction.UP) {
                        Direction var47 = var40.getClockWise();
                        if (!var4.isRoomId(var17, var24 + var47.getStepX(), var23 + var47.getStepZ(), var34, var28)) {
                           var47 = var47.getOpposite();
                        }

                        this.addRoom2x2(var3, var41, var2, var47, var40, var33[var34]);
                     } else if (var27 == 262144 && var40 == Direction.UP) {
                        this.addRoom2x2Secret(var3, var41, var2, var33[var34]);
                     }
                  }
               }
            }
         }
      }

      private void traverseOuterWalls(
         List<WoodlandMansionPieces.WoodlandMansionPiece> var1,
         WoodlandMansionPieces.PlacementData var2,
         WoodlandMansionPieces.SimpleGrid var3,
         Direction var4,
         int var5,
         int var6,
         int var7,
         int var8
      ) {
         int var9 = var5;
         int var10 = var6;
         Direction var11 = var4;

         do {
            if (!WoodlandMansionPieces.MansionGrid.isHouse(var3, var9 + var4.getStepX(), var10 + var4.getStepZ())) {
               this.traverseTurn(var1, var2);
               var4 = var4.getClockWise();
               if (var9 != var7 || var10 != var8 || var11 != var4) {
                  this.traverseWallPiece(var1, var2);
               }
            } else if (WoodlandMansionPieces.MansionGrid.isHouse(var3, var9 + var4.getStepX(), var10 + var4.getStepZ())
               && WoodlandMansionPieces.MansionGrid.isHouse(
                  var3, var9 + var4.getStepX() + var4.getCounterClockWise().getStepX(), var10 + var4.getStepZ() + var4.getCounterClockWise().getStepZ()
               )) {
               this.traverseInnerTurn(var1, var2);
               var9 += var4.getStepX();
               var10 += var4.getStepZ();
               var4 = var4.getCounterClockWise();
            } else {
               var9 += var4.getStepX();
               var10 += var4.getStepZ();
               if (var9 != var7 || var10 != var8 || var11 != var4) {
                  this.traverseWallPiece(var1, var2);
               }
            }
         } while (var9 != var7 || var10 != var8 || var11 != var4);
      }

      private void createRoof(
         List<WoodlandMansionPieces.WoodlandMansionPiece> var1,
         BlockPos var2,
         Rotation var3,
         WoodlandMansionPieces.SimpleGrid var4,
         @Nullable WoodlandMansionPieces.SimpleGrid var5
      ) {
         for (int var6 = 0; var6 < var4.height; var6++) {
            for (int var7 = 0; var7 < var4.width; var7++) {
               BlockPos var8 = var2.relative(var3.rotate(Direction.SOUTH), 8 + (var6 - this.startY) * 8);
               var8 = var8.relative(var3.rotate(Direction.EAST), (var7 - this.startX) * 8);
               boolean var9 = var5 != null && WoodlandMansionPieces.MansionGrid.isHouse(var5, var7, var6);
               if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6) && !var9) {
                  var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "roof", var8.above(3), var3));
                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                     BlockPos var10 = var8.relative(var3.rotate(Direction.EAST), 6);
                     var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "roof_front", var10, var3));
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                     BlockPos var23 = var8.relative(var3.rotate(Direction.EAST), 0);
                     var23 = var23.relative(var3.rotate(Direction.SOUTH), 7);
                     var1.add(
                        new WoodlandMansionPieces.WoodlandMansionPiece(
                           this.structureTemplateManager, "roof_front", var23, var3.getRotated(Rotation.CLOCKWISE_180)
                        )
                     );
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                     BlockPos var25 = var8.relative(var3.rotate(Direction.WEST), 1);
                     var1.add(
                        new WoodlandMansionPieces.WoodlandMansionPiece(
                           this.structureTemplateManager, "roof_front", var25, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)
                        )
                     );
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                     BlockPos var26 = var8.relative(var3.rotate(Direction.EAST), 6);
                     var26 = var26.relative(var3.rotate(Direction.SOUTH), 6);
                     var1.add(
                        new WoodlandMansionPieces.WoodlandMansionPiece(
                           this.structureTemplateManager, "roof_front", var26, var3.getRotated(Rotation.CLOCKWISE_90)
                        )
                     );
                  }
               }
            }
         }

         if (var5 != null) {
            for (int var12 = 0; var12 < var4.height; var12++) {
               for (int var14 = 0; var14 < var4.width; var14++) {
                  BlockPos var17 = var2.relative(var3.rotate(Direction.SOUTH), 8 + (var12 - this.startY) * 8);
                  var17 = var17.relative(var3.rotate(Direction.EAST), (var14 - this.startX) * 8);
                  boolean var21 = WoodlandMansionPieces.MansionGrid.isHouse(var5, var14, var12);
                  if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var14, var12) && var21) {
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14 + 1, var12)) {
                        BlockPos var28 = var17.relative(var3.rotate(Direction.EAST), 7);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "small_wall", var28, var3));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14 - 1, var12)) {
                        BlockPos var29 = var17.relative(var3.rotate(Direction.WEST), 1);
                        var29 = var29.relative(var3.rotate(Direction.SOUTH), 6);
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "small_wall", var29, var3.getRotated(Rotation.CLOCKWISE_180)
                           )
                        );
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14, var12 - 1)) {
                        BlockPos var31 = var17.relative(var3.rotate(Direction.WEST), 0);
                        var31 = var31.relative(var3.rotate(Direction.NORTH), 1);
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "small_wall", var31, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)
                           )
                        );
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14, var12 + 1)) {
                        BlockPos var33 = var17.relative(var3.rotate(Direction.EAST), 6);
                        var33 = var33.relative(var3.rotate(Direction.SOUTH), 7);
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "small_wall", var33, var3.getRotated(Rotation.CLOCKWISE_90)
                           )
                        );
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14 + 1, var12)) {
                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14, var12 - 1)) {
                           BlockPos var35 = var17.relative(var3.rotate(Direction.EAST), 7);
                           var35 = var35.relative(var3.rotate(Direction.NORTH), 2);
                           var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "small_wall_corner", var35, var3));
                        }

                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14, var12 + 1)) {
                           BlockPos var37 = var17.relative(var3.rotate(Direction.EAST), 8);
                           var37 = var37.relative(var3.rotate(Direction.SOUTH), 7);
                           var1.add(
                              new WoodlandMansionPieces.WoodlandMansionPiece(
                                 this.structureTemplateManager, "small_wall_corner", var37, var3.getRotated(Rotation.CLOCKWISE_90)
                              )
                           );
                        }
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14 - 1, var12)) {
                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14, var12 - 1)) {
                           BlockPos var39 = var17.relative(var3.rotate(Direction.WEST), 2);
                           var39 = var39.relative(var3.rotate(Direction.NORTH), 1);
                           var1.add(
                              new WoodlandMansionPieces.WoodlandMansionPiece(
                                 this.structureTemplateManager, "small_wall_corner", var39, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)
                              )
                           );
                        }

                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var14, var12 + 1)) {
                           BlockPos var41 = var17.relative(var3.rotate(Direction.WEST), 1);
                           var41 = var41.relative(var3.rotate(Direction.SOUTH), 8);
                           var1.add(
                              new WoodlandMansionPieces.WoodlandMansionPiece(
                                 this.structureTemplateManager, "small_wall_corner", var41, var3.getRotated(Rotation.CLOCKWISE_180)
                              )
                           );
                        }
                     }
                  }
               }
            }
         }

         for (int var13 = 0; var13 < var4.height; var13++) {
            for (int var15 = 0; var15 < var4.width; var15++) {
               BlockPos var19 = var2.relative(var3.rotate(Direction.SOUTH), 8 + (var13 - this.startY) * 8);
               var19 = var19.relative(var3.rotate(Direction.EAST), (var15 - this.startX) * 8);
               boolean var22 = var5 != null && WoodlandMansionPieces.MansionGrid.isHouse(var5, var15, var13);
               if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var15, var13) && !var22) {
                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var15 + 1, var13)) {
                     BlockPos var43 = var19.relative(var3.rotate(Direction.EAST), 6);
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var15, var13 + 1)) {
                        BlockPos var11 = var43.relative(var3.rotate(Direction.SOUTH), 6);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "roof_corner", var11, var3));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var15 + 1, var13 + 1)) {
                        BlockPos var46 = var43.relative(var3.rotate(Direction.SOUTH), 5);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "roof_inner_corner", var46, var3));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var15, var13 - 1)) {
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "roof_corner", var43, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)
                           )
                        );
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var15 + 1, var13 - 1)) {
                        BlockPos var47 = var19.relative(var3.rotate(Direction.EAST), 9);
                        var47 = var47.relative(var3.rotate(Direction.NORTH), 2);
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "roof_inner_corner", var47, var3.getRotated(Rotation.CLOCKWISE_90)
                           )
                        );
                     }
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var15 - 1, var13)) {
                     BlockPos var44 = var19.relative(var3.rotate(Direction.EAST), 0);
                     var44 = var44.relative(var3.rotate(Direction.SOUTH), 0);
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var15, var13 + 1)) {
                        BlockPos var49 = var44.relative(var3.rotate(Direction.SOUTH), 6);
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "roof_corner", var49, var3.getRotated(Rotation.CLOCKWISE_90)
                           )
                        );
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var15 - 1, var13 + 1)) {
                        BlockPos var50 = var44.relative(var3.rotate(Direction.SOUTH), 8);
                        var50 = var50.relative(var3.rotate(Direction.WEST), 3);
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "roof_inner_corner", var50, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)
                           )
                        );
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var15, var13 - 1)) {
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "roof_corner", var44, var3.getRotated(Rotation.CLOCKWISE_180)
                           )
                        );
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var15 - 1, var13 - 1)) {
                        BlockPos var52 = var44.relative(var3.rotate(Direction.SOUTH), 1);
                        var1.add(
                           new WoodlandMansionPieces.WoodlandMansionPiece(
                              this.structureTemplateManager, "roof_inner_corner", var52, var3.getRotated(Rotation.CLOCKWISE_180)
                           )
                        );
                     }
                  }
               }
            }
         }
      }

      private void entrance(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         Direction var3 = var2.rotation.rotate(Direction.WEST);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "entrance", var2.position.relative(var3, 9), var2.rotation));
         var2.position = var2.position.relative(var2.rotation.rotate(Direction.SOUTH), 16);
      }

      private void traverseWallPiece(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         var1.add(
            new WoodlandMansionPieces.WoodlandMansionPiece(
               this.structureTemplateManager, var2.wallType, var2.position.relative(var2.rotation.rotate(Direction.EAST), 7), var2.rotation
            )
         );
         var2.position = var2.position.relative(var2.rotation.rotate(Direction.SOUTH), 8);
      }

      private void traverseTurn(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         var2.position = var2.position.relative(var2.rotation.rotate(Direction.SOUTH), -1);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, "wall_corner", var2.position, var2.rotation));
         var2.position = var2.position.relative(var2.rotation.rotate(Direction.SOUTH), -7);
         var2.position = var2.position.relative(var2.rotation.rotate(Direction.WEST), -6);
         var2.rotation = var2.rotation.getRotated(Rotation.CLOCKWISE_90);
      }

      private void traverseInnerTurn(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         var2.position = var2.position.relative(var2.rotation.rotate(Direction.SOUTH), 6);
         var2.position = var2.position.relative(var2.rotation.rotate(Direction.EAST), 8);
         var2.rotation = var2.rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
      }

      private void addRoom1x1(
         List<WoodlandMansionPieces.WoodlandMansionPiece> var1, BlockPos var2, Rotation var3, Direction var4, WoodlandMansionPieces.FloorRoomCollection var5
      ) {
         Rotation var6 = Rotation.NONE;
         String var7 = var5.get1x1(this.random);
         if (var4 != Direction.EAST) {
            if (var4 == Direction.NORTH) {
               var6 = var6.getRotated(Rotation.COUNTERCLOCKWISE_90);
            } else if (var4 == Direction.WEST) {
               var6 = var6.getRotated(Rotation.CLOCKWISE_180);
            } else if (var4 == Direction.SOUTH) {
               var6 = var6.getRotated(Rotation.CLOCKWISE_90);
            } else {
               var7 = var5.get1x1Secret(this.random);
            }
         }

         BlockPos var8 = StructureTemplate.getZeroPositionWithTransform(new BlockPos(1, 0, 0), Mirror.NONE, var6, 7, 7);
         var6 = var6.getRotated(var3);
         var8 = var8.rotate(var3);
         BlockPos var9 = var2.offset(var8.getX(), 0, var8.getZ());
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var7, var9, var6));
      }

      private void addRoom1x2(
         List<WoodlandMansionPieces.WoodlandMansionPiece> var1,
         BlockPos var2,
         Rotation var3,
         Direction var4,
         Direction var5,
         WoodlandMansionPieces.FloorRoomCollection var6,
         boolean var7
      ) {
         if (var5 == Direction.EAST && var4 == Direction.SOUTH) {
            BlockPos var29 = var2.relative(var3.rotate(Direction.EAST), 1);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var29, var3));
         } else if (var5 == Direction.EAST && var4 == Direction.NORTH) {
            BlockPos var27 = var2.relative(var3.rotate(Direction.EAST), 1);
            var27 = var27.relative(var3.rotate(Direction.SOUTH), 6);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var27, var3, Mirror.LEFT_RIGHT
               )
            );
         } else if (var5 == Direction.WEST && var4 == Direction.NORTH) {
            BlockPos var25 = var2.relative(var3.rotate(Direction.EAST), 7);
            var25 = var25.relative(var3.rotate(Direction.SOUTH), 6);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var25, var3.getRotated(Rotation.CLOCKWISE_180)
               )
            );
         } else if (var5 == Direction.WEST && var4 == Direction.SOUTH) {
            BlockPos var24 = var2.relative(var3.rotate(Direction.EAST), 7);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var24, var3, Mirror.FRONT_BACK
               )
            );
         } else if (var5 == Direction.SOUTH && var4 == Direction.EAST) {
            BlockPos var23 = var2.relative(var3.rotate(Direction.EAST), 1);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var23, var3.getRotated(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT
               )
            );
         } else if (var5 == Direction.SOUTH && var4 == Direction.WEST) {
            BlockPos var22 = var2.relative(var3.rotate(Direction.EAST), 7);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var22, var3.getRotated(Rotation.CLOCKWISE_90)
               )
            );
         } else if (var5 == Direction.NORTH && var4 == Direction.WEST) {
            BlockPos var20 = var2.relative(var3.rotate(Direction.EAST), 7);
            var20 = var20.relative(var3.rotate(Direction.SOUTH), 6);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var20, var3.getRotated(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK
               )
            );
         } else if (var5 == Direction.NORTH && var4 == Direction.EAST) {
            BlockPos var18 = var2.relative(var3.rotate(Direction.EAST), 1);
            var18 = var18.relative(var3.rotate(Direction.SOUTH), 6);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2SideEntrance(this.random, var7), var18, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)
               )
            );
         } else if (var5 == Direction.SOUTH && var4 == Direction.NORTH) {
            BlockPos var16 = var2.relative(var3.rotate(Direction.EAST), 1);
            var16 = var16.relative(var3.rotate(Direction.NORTH), 8);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var6.get1x2FrontEntrance(this.random, var7), var16, var3));
         } else if (var5 == Direction.NORTH && var4 == Direction.SOUTH) {
            BlockPos var14 = var2.relative(var3.rotate(Direction.EAST), 7);
            var14 = var14.relative(var3.rotate(Direction.SOUTH), 14);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2FrontEntrance(this.random, var7), var14, var3.getRotated(Rotation.CLOCKWISE_180)
               )
            );
         } else if (var5 == Direction.WEST && var4 == Direction.EAST) {
            BlockPos var13 = var2.relative(var3.rotate(Direction.EAST), 15);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2FrontEntrance(this.random, var7), var13, var3.getRotated(Rotation.CLOCKWISE_90)
               )
            );
         } else if (var5 == Direction.EAST && var4 == Direction.WEST) {
            BlockPos var11 = var2.relative(var3.rotate(Direction.WEST), 7);
            var11 = var11.relative(var3.rotate(Direction.SOUTH), 6);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2FrontEntrance(this.random, var7), var11, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)
               )
            );
         } else if (var5 == Direction.UP && var4 == Direction.EAST) {
            BlockPos var10 = var2.relative(var3.rotate(Direction.EAST), 15);
            var1.add(
               new WoodlandMansionPieces.WoodlandMansionPiece(
                  this.structureTemplateManager, var6.get1x2Secret(this.random), var10, var3.getRotated(Rotation.CLOCKWISE_90)
               )
            );
         } else if (var5 == Direction.UP && var4 == Direction.SOUTH) {
            BlockPos var8 = var2.relative(var3.rotate(Direction.EAST), 1);
            var8 = var8.relative(var3.rotate(Direction.NORTH), 0);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var6.get1x2Secret(this.random), var8, var3));
         }
      }

      private void addRoom2x2(
         List<WoodlandMansionPieces.WoodlandMansionPiece> var1,
         BlockPos var2,
         Rotation var3,
         Direction var4,
         Direction var5,
         WoodlandMansionPieces.FloorRoomCollection var6
      ) {
         byte var7 = 0;
         byte var8 = 0;
         Rotation var9 = var3;
         Mirror var10 = Mirror.NONE;
         if (var5 == Direction.EAST && var4 == Direction.SOUTH) {
            var7 = -7;
         } else if (var5 == Direction.EAST && var4 == Direction.NORTH) {
            var7 = -7;
            var8 = 6;
            var10 = Mirror.LEFT_RIGHT;
         } else if (var5 == Direction.NORTH && var4 == Direction.EAST) {
            var7 = 1;
            var8 = 14;
            var9 = var3.getRotated(Rotation.COUNTERCLOCKWISE_90);
         } else if (var5 == Direction.NORTH && var4 == Direction.WEST) {
            var7 = 7;
            var8 = 14;
            var9 = var3.getRotated(Rotation.COUNTERCLOCKWISE_90);
            var10 = Mirror.LEFT_RIGHT;
         } else if (var5 == Direction.SOUTH && var4 == Direction.WEST) {
            var7 = 7;
            var8 = -8;
            var9 = var3.getRotated(Rotation.CLOCKWISE_90);
         } else if (var5 == Direction.SOUTH && var4 == Direction.EAST) {
            var7 = 1;
            var8 = -8;
            var9 = var3.getRotated(Rotation.CLOCKWISE_90);
            var10 = Mirror.LEFT_RIGHT;
         } else if (var5 == Direction.WEST && var4 == Direction.NORTH) {
            var7 = 15;
            var8 = 6;
            var9 = var3.getRotated(Rotation.CLOCKWISE_180);
         } else if (var5 == Direction.WEST && var4 == Direction.SOUTH) {
            var7 = 15;
            var10 = Mirror.FRONT_BACK;
         }

         BlockPos var11 = var2.relative(var3.rotate(Direction.EAST), var7);
         var11 = var11.relative(var3.rotate(Direction.SOUTH), var8);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var6.get2x2(this.random), var11, var9, var10));
      }

      private void addRoom2x2Secret(
         List<WoodlandMansionPieces.WoodlandMansionPiece> var1, BlockPos var2, Rotation var3, WoodlandMansionPieces.FloorRoomCollection var4
      ) {
         BlockPos var5 = var2.relative(var3.rotate(Direction.EAST), 1);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureTemplateManager, var4.get2x2Secret(this.random), var5, var3, Mirror.NONE));
      }
   }

   static class PlacementData {
      public Rotation rotation;
      public BlockPos position;
      public String wallType;

      PlacementData() {
         super();
      }
   }

   static class SecondFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      SecondFloorRoomCollection() {
         super();
      }

      @Override
      public String get1x1(RandomSource var1) {
         return "1x1_b" + (var1.nextInt(4) + 1);
      }

      @Override
      public String get1x1Secret(RandomSource var1) {
         return "1x1_as" + (var1.nextInt(4) + 1);
      }

      @Override
      public String get1x2SideEntrance(RandomSource var1, boolean var2) {
         return var2 ? "1x2_c_stairs" : "1x2_c" + (var1.nextInt(4) + 1);
      }

      @Override
      public String get1x2FrontEntrance(RandomSource var1, boolean var2) {
         return var2 ? "1x2_d_stairs" : "1x2_d" + (var1.nextInt(5) + 1);
      }

      @Override
      public String get1x2Secret(RandomSource var1) {
         return "1x2_se" + (var1.nextInt(1) + 1);
      }

      @Override
      public String get2x2(RandomSource var1) {
         return "2x2_b" + (var1.nextInt(5) + 1);
      }

      @Override
      public String get2x2Secret(RandomSource var1) {
         return "2x2_s1";
      }
   }

   static class SimpleGrid {
      private final int[][] grid;
      final int width;
      final int height;
      private final int valueIfOutside;

      public SimpleGrid(int var1, int var2, int var3) {
         super();
         this.width = var1;
         this.height = var2;
         this.valueIfOutside = var3;
         this.grid = new int[var1][var2];
      }

      public void set(int var1, int var2, int var3) {
         if (var1 >= 0 && var1 < this.width && var2 >= 0 && var2 < this.height) {
            this.grid[var1][var2] = var3;
         }
      }

      public void set(int var1, int var2, int var3, int var4, int var5) {
         for (int var6 = var2; var6 <= var4; var6++) {
            for (int var7 = var1; var7 <= var3; var7++) {
               this.set(var7, var6, var5);
            }
         }
      }

      public int get(int var1, int var2) {
         return var1 >= 0 && var1 < this.width && var2 >= 0 && var2 < this.height ? this.grid[var1][var2] : this.valueIfOutside;
      }

      public void setif(int var1, int var2, int var3, int var4) {
         if (this.get(var1, var2) == var3) {
            this.set(var1, var2, var4);
         }
      }

      public boolean edgesTo(int var1, int var2, int var3) {
         return this.get(var1 - 1, var2) == var3 || this.get(var1 + 1, var2) == var3 || this.get(var1, var2 + 1) == var3 || this.get(var1, var2 - 1) == var3;
      }
   }

   static class ThirdFloorRoomCollection extends WoodlandMansionPieces.SecondFloorRoomCollection {
      ThirdFloorRoomCollection() {
         super();
      }
   }

   public static class WoodlandMansionPiece extends TemplateStructurePiece {
      public WoodlandMansionPiece(StructureTemplateManager var1, String var2, BlockPos var3, Rotation var4) {
         this(var1, var2, var3, var4, Mirror.NONE);
      }

      public WoodlandMansionPiece(StructureTemplateManager var1, String var2, BlockPos var3, Rotation var4, Mirror var5) {
         super(StructurePieceType.WOODLAND_MANSION_PIECE, 0, var1, makeLocation(var2), var2, makeSettings(var5, var4), var3);
      }

      public WoodlandMansionPiece(StructureTemplateManager var1, CompoundTag var2) {
         super(
            StructurePieceType.WOODLAND_MANSION_PIECE,
            var2,
            var1,
            var1x -> makeSettings(Mirror.valueOf(var2.getString("Mi")), Rotation.valueOf(var2.getString("Rot")))
         );
      }

      @Override
      protected ResourceLocation makeTemplateLocation() {
         return makeLocation(this.templateName);
      }

      private static ResourceLocation makeLocation(String var0) {
         return ResourceLocation.withDefaultNamespace("woodland_mansion/" + var0);
      }

      private static StructurePlaceSettings makeSettings(Mirror var0, Rotation var1) {
         return new StructurePlaceSettings().setIgnoreEntities(true).setRotation(var1).setMirror(var0).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putString("Rot", this.placeSettings.getRotation().name());
         var2.putString("Mi", this.placeSettings.getMirror().name());
      }

      @Override
      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, RandomSource var4, BoundingBox var5) {
         if (var1.startsWith("Chest")) {
            Rotation var11 = this.placeSettings.getRotation();
            BlockState var12 = Blocks.CHEST.defaultBlockState();
            if ("ChestWest".equals(var1)) {
               var12 = var12.setValue(ChestBlock.FACING, var11.rotate(Direction.WEST));
            } else if ("ChestEast".equals(var1)) {
               var12 = var12.setValue(ChestBlock.FACING, var11.rotate(Direction.EAST));
            } else if ("ChestSouth".equals(var1)) {
               var12 = var12.setValue(ChestBlock.FACING, var11.rotate(Direction.SOUTH));
            } else if ("ChestNorth".equals(var1)) {
               var12 = var12.setValue(ChestBlock.FACING, var11.rotate(Direction.NORTH));
            }

            this.createChest(var3, var5, var4, var2, BuiltInLootTables.WOODLAND_MANSION, var12);
         } else {
            ArrayList var6 = new ArrayList();
            switch (var1) {
               case "Mage":
                  var6.add(EntityType.EVOKER.create(var3.getLevel(), EntitySpawnReason.STRUCTURE));
                  break;
               case "Warrior":
                  var6.add(EntityType.VINDICATOR.create(var3.getLevel(), EntitySpawnReason.STRUCTURE));
                  break;
               case "Group of Allays":
                  int var9 = var3.getRandom().nextInt(3) + 1;

                  for (int var10 = 0; var10 < var9; var10++) {
                     var6.add(EntityType.ALLAY.create(var3.getLevel(), EntitySpawnReason.STRUCTURE));
                  }
                  break;
               default:
                  return;
            }

            for (Mob var13 : var6) {
               if (var13 != null) {
                  var13.setPersistenceRequired();
                  var13.moveTo(var2, 0.0F, 0.0F);
                  var13.finalizeSpawn(var3, var3.getCurrentDifficultyAt(var13.blockPosition()), EntitySpawnReason.STRUCTURE, null);
                  var3.addFreshEntityWithPassengers(var13);
                  var3.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
               }
            }
         }
      }
   }
}
