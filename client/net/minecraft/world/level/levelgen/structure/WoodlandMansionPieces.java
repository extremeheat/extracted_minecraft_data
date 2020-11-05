package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockIgnoreProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class WoodlandMansionPieces {
   public static void generateMansion(StructureManager var0, BlockPos var1, Rotation var2, List<WoodlandMansionPieces.WoodlandMansionPiece> var3, Random var4) {
      WoodlandMansionPieces.MansionGrid var5 = new WoodlandMansionPieces.MansionGrid(var4);
      WoodlandMansionPieces.MansionPiecePlacer var6 = new WoodlandMansionPieces.MansionPiecePlacer(var0, var4);
      var6.createMansion(var1, var2, var3, var5);
   }

   static class ThirdFloorRoomCollection extends WoodlandMansionPieces.SecondFloorRoomCollection {
      private ThirdFloorRoomCollection() {
         super(null);
      }

      // $FF: synthetic method
      ThirdFloorRoomCollection(Object var1) {
         this();
      }
   }

   static class SecondFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      private SecondFloorRoomCollection() {
         super(null);
      }

      public String get1x1(Random var1) {
         return "1x1_b" + (var1.nextInt(4) + 1);
      }

      public String get1x1Secret(Random var1) {
         return "1x1_as" + (var1.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random var1, boolean var2) {
         return var2 ? "1x2_c_stairs" : "1x2_c" + (var1.nextInt(4) + 1);
      }

      public String get1x2FrontEntrance(Random var1, boolean var2) {
         return var2 ? "1x2_d_stairs" : "1x2_d" + (var1.nextInt(5) + 1);
      }

      public String get1x2Secret(Random var1) {
         return "1x2_se" + (var1.nextInt(1) + 1);
      }

      public String get2x2(Random var1) {
         return "2x2_b" + (var1.nextInt(5) + 1);
      }

      public String get2x2Secret(Random var1) {
         return "2x2_s1";
      }

      // $FF: synthetic method
      SecondFloorRoomCollection(Object var1) {
         this();
      }
   }

   static class FirstFloorRoomCollection extends WoodlandMansionPieces.FloorRoomCollection {
      private FirstFloorRoomCollection() {
         super(null);
      }

      public String get1x1(Random var1) {
         return "1x1_a" + (var1.nextInt(5) + 1);
      }

      public String get1x1Secret(Random var1) {
         return "1x1_as" + (var1.nextInt(4) + 1);
      }

      public String get1x2SideEntrance(Random var1, boolean var2) {
         return "1x2_a" + (var1.nextInt(9) + 1);
      }

      public String get1x2FrontEntrance(Random var1, boolean var2) {
         return "1x2_b" + (var1.nextInt(5) + 1);
      }

      public String get1x2Secret(Random var1) {
         return "1x2_s" + (var1.nextInt(2) + 1);
      }

      public String get2x2(Random var1) {
         return "2x2_a" + (var1.nextInt(4) + 1);
      }

      public String get2x2Secret(Random var1) {
         return "2x2_s1";
      }

      // $FF: synthetic method
      FirstFloorRoomCollection(Object var1) {
         this();
      }
   }

   abstract static class FloorRoomCollection {
      private FloorRoomCollection() {
         super();
      }

      public abstract String get1x1(Random var1);

      public abstract String get1x1Secret(Random var1);

      public abstract String get1x2SideEntrance(Random var1, boolean var2);

      public abstract String get1x2FrontEntrance(Random var1, boolean var2);

      public abstract String get1x2Secret(Random var1);

      public abstract String get2x2(Random var1);

      public abstract String get2x2Secret(Random var1);

      // $FF: synthetic method
      FloorRoomCollection(Object var1) {
         this();
      }
   }

   static class SimpleGrid {
      private final int[][] grid;
      private final int width;
      private final int height;
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
         for(int var6 = var2; var6 <= var4; ++var6) {
            for(int var7 = var1; var7 <= var3; ++var7) {
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

   static class MansionGrid {
      private final Random random;
      private final WoodlandMansionPieces.SimpleGrid baseGrid;
      private final WoodlandMansionPieces.SimpleGrid thirdFloorGrid;
      private final WoodlandMansionPieces.SimpleGrid[] floorRooms;
      private final int entranceX;
      private final int entranceY;

      public MansionGrid(Random var1) {
         super();
         this.random = var1;
         boolean var2 = true;
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

         while(this.cleanEdges(this.baseGrid)) {
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
         return (this.floorRooms[var4].get(var2, var3) & '\uffff') == var5;
      }

      @Nullable
      public Direction get1x2RoomDirection(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, int var4, int var5) {
         Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

         Direction var7;
         do {
            if (!var6.hasNext()) {
               return null;
            }

            var7 = (Direction)var6.next();
         } while(!this.isRoomId(var1, var2 + var7.getStepX(), var3 + var7.getStepZ(), var4, var5));

         return var7;
      }

      private void recursiveCorridor(WoodlandMansionPieces.SimpleGrid var1, int var2, int var3, Direction var4, int var5) {
         if (var5 > 0) {
            var1.set(var2, var3, 1);
            var1.setif(var2 + var4.getStepX(), var3 + var4.getStepZ(), 0, 1);

            Direction var7;
            for(int var6 = 0; var6 < 8; ++var6) {
               var7 = Direction.from2DDataValue(this.random.nextInt(4));
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
            var7 = var4.getCounterClockWise();
            var1.setif(var2 + var10.getStepX(), var3 + var10.getStepZ(), 0, 2);
            var1.setif(var2 + var7.getStepX(), var3 + var7.getStepZ(), 0, 2);
            var1.setif(var2 + var4.getStepX() + var10.getStepX(), var3 + var4.getStepZ() + var10.getStepZ(), 0, 2);
            var1.setif(var2 + var4.getStepX() + var7.getStepX(), var3 + var4.getStepZ() + var7.getStepZ(), 0, 2);
            var1.setif(var2 + var4.getStepX() * 2, var3 + var4.getStepZ() * 2, 0, 2);
            var1.setif(var2 + var10.getStepX() * 2, var3 + var10.getStepZ() * 2, 0, 2);
            var1.setif(var2 + var7.getStepX() * 2, var3 + var7.getStepZ() * 2, 0, 2);
         }
      }

      private boolean cleanEdges(WoodlandMansionPieces.SimpleGrid var1) {
         boolean var2 = false;

         for(int var3 = 0; var3 < var1.height; ++var3) {
            for(int var4 = 0; var4 < var1.width; ++var4) {
               if (var1.get(var4, var3) == 0) {
                  byte var5 = 0;
                  int var7 = var5 + (isHouse(var1, var4 + 1, var3) ? 1 : 0);
                  var7 += isHouse(var1, var4 - 1, var3) ? 1 : 0;
                  var7 += isHouse(var1, var4, var3 + 1) ? 1 : 0;
                  var7 += isHouse(var1, var4, var3 - 1) ? 1 : 0;
                  if (var7 >= 3) {
                     var1.set(var4, var3, 2);
                     var2 = true;
                  } else if (var7 == 2) {
                     byte var6 = 0;
                     int var8 = var6 + (isHouse(var1, var4 + 1, var3 + 1) ? 1 : 0);
                     var8 += isHouse(var1, var4 - 1, var3 + 1) ? 1 : 0;
                     var8 += isHouse(var1, var4 + 1, var3 - 1) ? 1 : 0;
                     var8 += isHouse(var1, var4 - 1, var3 - 1) ? 1 : 0;
                     if (var8 <= 1) {
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

         int var4;
         int var6;
         for(int var3 = 0; var3 < this.thirdFloorGrid.height; ++var3) {
            for(var4 = 0; var4 < this.thirdFloorGrid.width; ++var4) {
               int var5 = var2.get(var4, var3);
               var6 = var5 & 983040;
               if (var6 == 131072 && (var5 & 2097152) == 2097152) {
                  var1.add(new Tuple(var4, var3));
               }
            }
         }

         if (var1.isEmpty()) {
            this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
         } else {
            Tuple var11 = (Tuple)var1.get(this.random.nextInt(var1.size()));
            var4 = var2.get((Integer)var11.getA(), (Integer)var11.getB());
            var2.set((Integer)var11.getA(), (Integer)var11.getB(), var4 | 4194304);
            Direction var12 = this.get1x2RoomDirection(this.baseGrid, (Integer)var11.getA(), (Integer)var11.getB(), 1, var4 & '\uffff');
            var6 = (Integer)var11.getA() + var12.getStepX();
            int var7 = (Integer)var11.getB() + var12.getStepZ();

            for(int var8 = 0; var8 < this.thirdFloorGrid.height; ++var8) {
               for(int var9 = 0; var9 < this.thirdFloorGrid.width; ++var9) {
                  if (!isHouse(this.baseGrid, var9, var8)) {
                     this.thirdFloorGrid.set(var9, var8, 5);
                  } else if (var9 == (Integer)var11.getA() && var8 == (Integer)var11.getB()) {
                     this.thirdFloorGrid.set(var9, var8, 3);
                  } else if (var9 == var6 && var8 == var7) {
                     this.thirdFloorGrid.set(var9, var8, 3);
                     this.floorRooms[2].set(var9, var8, 8388608);
                  }
               }
            }

            ArrayList var13 = Lists.newArrayList();
            Iterator var14 = Direction.Plane.HORIZONTAL.iterator();

            while(var14.hasNext()) {
               Direction var10 = (Direction)var14.next();
               if (this.thirdFloorGrid.get(var6 + var10.getStepX(), var7 + var10.getStepZ()) == 0) {
                  var13.add(var10);
               }
            }

            if (var13.isEmpty()) {
               this.thirdFloorGrid.set(0, 0, this.thirdFloorGrid.width, this.thirdFloorGrid.height, 5);
               var2.set((Integer)var11.getA(), (Integer)var11.getB(), var4);
            } else {
               Direction var15 = (Direction)var13.get(this.random.nextInt(var13.size()));
               this.recursiveCorridor(this.thirdFloorGrid, var6 + var15.getStepX(), var7 + var15.getStepZ(), var15, 4);

               while(this.cleanEdges(this.thirdFloorGrid)) {
               }

            }
         }
      }

      private void identifyRooms(WoodlandMansionPieces.SimpleGrid var1, WoodlandMansionPieces.SimpleGrid var2) {
         ArrayList var3 = Lists.newArrayList();

         int var4;
         for(var4 = 0; var4 < var1.height; ++var4) {
            for(int var5 = 0; var5 < var1.width; ++var5) {
               if (var1.get(var5, var4) == 2) {
                  var3.add(new Tuple(var5, var4));
               }
            }
         }

         Collections.shuffle(var3, this.random);
         var4 = 10;
         Iterator var19 = var3.iterator();

         while(true) {
            int var7;
            int var8;
            do {
               if (!var19.hasNext()) {
                  return;
               }

               Tuple var6 = (Tuple)var19.next();
               var7 = (Integer)var6.getA();
               var8 = (Integer)var6.getB();
            } while(var2.get(var7, var8) != 0);

            int var9 = var7;
            int var10 = var7;
            int var11 = var8;
            int var12 = var8;
            int var13 = 65536;
            if (var2.get(var7 + 1, var8) == 0 && var2.get(var7, var8 + 1) == 0 && var2.get(var7 + 1, var8 + 1) == 0 && var1.get(var7 + 1, var8) == 2 && var1.get(var7, var8 + 1) == 2 && var1.get(var7 + 1, var8 + 1) == 2) {
               var10 = var7 + 1;
               var12 = var8 + 1;
               var13 = 262144;
            } else if (var2.get(var7 - 1, var8) == 0 && var2.get(var7, var8 + 1) == 0 && var2.get(var7 - 1, var8 + 1) == 0 && var1.get(var7 - 1, var8) == 2 && var1.get(var7, var8 + 1) == 2 && var1.get(var7 - 1, var8 + 1) == 2) {
               var9 = var7 - 1;
               var12 = var8 + 1;
               var13 = 262144;
            } else if (var2.get(var7 - 1, var8) == 0 && var2.get(var7, var8 - 1) == 0 && var2.get(var7 - 1, var8 - 1) == 0 && var1.get(var7 - 1, var8) == 2 && var1.get(var7, var8 - 1) == 2 && var1.get(var7 - 1, var8 - 1) == 2) {
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

            for(int var17 = var11; var17 <= var12; ++var17) {
               for(int var18 = var9; var18 <= var10; ++var18) {
                  if (var18 == var14 && var17 == var15) {
                     var2.set(var18, var17, 1048576 | var16 | var13 | var4);
                  } else {
                     var2.set(var18, var17, var13 | var4);
                  }
               }
            }

            ++var4;
         }
      }
   }

   static class MansionPiecePlacer {
      private final StructureManager structureManager;
      private final Random random;
      private int startX;
      private int startY;

      public MansionPiecePlacer(StructureManager var1, Random var2) {
         super();
         this.structureManager = var1;
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

         int var14;
         for(int var13 = 0; var13 < var8.height && !var12; ++var13) {
            for(var14 = var8.width - 1; var14 >= 0 && !var12; --var14) {
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
         this.createRoof(var3, var1.above(27), var2, var8, (WoodlandMansionPieces.SimpleGrid)null);
         if (!var3.isEmpty()) {
         }

         WoodlandMansionPieces.FloorRoomCollection[] var33 = new WoodlandMansionPieces.FloorRoomCollection[]{new WoodlandMansionPieces.FirstFloorRoomCollection(), new WoodlandMansionPieces.SecondFloorRoomCollection(), new WoodlandMansionPieces.ThirdFloorRoomCollection()};

         for(var14 = 0; var14 < 3; ++var14) {
            BlockPos var15 = var1.above(8 * var14 + (var14 == 2 ? 3 : 0));
            WoodlandMansionPieces.SimpleGrid var16 = var4.floorRooms[var14];
            WoodlandMansionPieces.SimpleGrid var17 = var14 == 2 ? var8 : var7;
            String var18 = var14 == 0 ? "carpet_south_1" : "carpet_south_2";
            String var19 = var14 == 0 ? "carpet_west_1" : "carpet_west_2";

            for(int var20 = 0; var20 < var17.height; ++var20) {
               for(int var21 = 0; var21 < var17.width; ++var21) {
                  if (var17.get(var21, var20) == 1) {
                     BlockPos var22 = var15.relative(var2.rotate(Direction.SOUTH), 8 + (var20 - this.startY) * 8);
                     var22 = var22.relative(var2.rotate(Direction.EAST), (var21 - this.startX) * 8);
                     var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "corridor_floor", var22, var2));
                     if (var17.get(var21, var20 - 1) == 1 || (var16.get(var21, var20 - 1) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "carpet_north", var22.relative((Direction)var2.rotate(Direction.EAST), 1).above(), var2));
                     }

                     if (var17.get(var21 + 1, var20) == 1 || (var16.get(var21 + 1, var20) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "carpet_east", var22.relative((Direction)var2.rotate(Direction.SOUTH), 1).relative((Direction)var2.rotate(Direction.EAST), 5).above(), var2));
                     }

                     if (var17.get(var21, var20 + 1) == 1 || (var16.get(var21, var20 + 1) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var18, var22.relative((Direction)var2.rotate(Direction.SOUTH), 5).relative((Direction)var2.rotate(Direction.WEST), 1), var2));
                     }

                     if (var17.get(var21 - 1, var20) == 1 || (var16.get(var21 - 1, var20) & 8388608) == 8388608) {
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var19, var22.relative((Direction)var2.rotate(Direction.WEST), 1).relative((Direction)var2.rotate(Direction.NORTH), 1), var2));
                     }
                  }
               }
            }

            String var34 = var14 == 0 ? "indoors_wall_1" : "indoors_wall_2";
            String var35 = var14 == 0 ? "indoors_door_1" : "indoors_door_2";
            ArrayList var36 = Lists.newArrayList();

            for(int var23 = 0; var23 < var17.height; ++var23) {
               for(int var24 = 0; var24 < var17.width; ++var24) {
                  boolean var25 = var14 == 2 && var17.get(var24, var23) == 3;
                  if (var17.get(var24, var23) == 2 || var25) {
                     int var26 = var16.get(var24, var23);
                     int var27 = var26 & 983040;
                     int var28 = var26 & '\uffff';
                     var25 = var25 && (var26 & 8388608) == 8388608;
                     var36.clear();
                     if ((var26 & 2097152) == 2097152) {
                        Iterator var29 = Direction.Plane.HORIZONTAL.iterator();

                        while(var29.hasNext()) {
                           Direction var30 = (Direction)var29.next();
                           if (var17.get(var24 + var30.getStepX(), var23 + var30.getStepZ()) == 1) {
                              var36.add(var30);
                           }
                        }
                     }

                     Direction var37 = null;
                     if (!var36.isEmpty()) {
                        var37 = (Direction)var36.get(this.random.nextInt(var36.size()));
                     } else if ((var26 & 1048576) == 1048576) {
                        var37 = Direction.UP;
                     }

                     BlockPos var38 = var15.relative(var2.rotate(Direction.SOUTH), 8 + (var23 - this.startY) * 8);
                     var38 = var38.relative(var2.rotate(Direction.EAST), -1 + (var24 - this.startX) * 8);
                     if (WoodlandMansionPieces.MansionGrid.isHouse(var17, var24 - 1, var23) && !var4.isRoomId(var17, var24 - 1, var23, var14, var28)) {
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var37 == Direction.WEST ? var35 : var34, var38, var2));
                     }

                     BlockPos var31;
                     if (var17.get(var24 + 1, var23) == 1 && !var25) {
                        var31 = var38.relative((Direction)var2.rotate(Direction.EAST), 8);
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var37 == Direction.EAST ? var35 : var34, var31, var2));
                     }

                     if (WoodlandMansionPieces.MansionGrid.isHouse(var17, var24, var23 + 1) && !var4.isRoomId(var17, var24, var23 + 1, var14, var28)) {
                        var31 = var38.relative((Direction)var2.rotate(Direction.SOUTH), 7);
                        var31 = var31.relative((Direction)var2.rotate(Direction.EAST), 7);
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var37 == Direction.SOUTH ? var35 : var34, var31, var2.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if (var17.get(var24, var23 - 1) == 1 && !var25) {
                        var31 = var38.relative((Direction)var2.rotate(Direction.NORTH), 1);
                        var31 = var31.relative((Direction)var2.rotate(Direction.EAST), 7);
                        var3.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var37 == Direction.NORTH ? var35 : var34, var31, var2.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if (var27 == 65536) {
                        this.addRoom1x1(var3, var38, var2, var37, var33[var14]);
                     } else {
                        Direction var39;
                        if (var27 == 131072 && var37 != null) {
                           var39 = var4.get1x2RoomDirection(var17, var24, var23, var14, var28);
                           boolean var32 = (var26 & 4194304) == 4194304;
                           this.addRoom1x2(var3, var38, var2, var39, var37, var33[var14], var32);
                        } else if (var27 == 262144 && var37 != null && var37 != Direction.UP) {
                           var39 = var37.getClockWise();
                           if (!var4.isRoomId(var17, var24 + var39.getStepX(), var23 + var39.getStepZ(), var14, var28)) {
                              var39 = var39.getOpposite();
                           }

                           this.addRoom2x2(var3, var38, var2, var39, var37, var33[var14]);
                        } else if (var27 == 262144 && var37 == Direction.UP) {
                           this.addRoom2x2Secret(var3, var38, var2, var33[var14]);
                        }
                     }
                  }
               }
            }
         }

      }

      private void traverseOuterWalls(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2, WoodlandMansionPieces.SimpleGrid var3, Direction var4, int var5, int var6, int var7, int var8) {
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
            } else if (WoodlandMansionPieces.MansionGrid.isHouse(var3, var9 + var4.getStepX(), var10 + var4.getStepZ()) && WoodlandMansionPieces.MansionGrid.isHouse(var3, var9 + var4.getStepX() + var4.getCounterClockWise().getStepX(), var10 + var4.getStepZ() + var4.getCounterClockWise().getStepZ())) {
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
         } while(var9 != var7 || var10 != var8 || var11 != var4);

      }

      private void createRoof(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, BlockPos var2, Rotation var3, WoodlandMansionPieces.SimpleGrid var4, @Nullable WoodlandMansionPieces.SimpleGrid var5) {
         int var6;
         int var7;
         BlockPos var8;
         boolean var9;
         BlockPos var10;
         for(var6 = 0; var6 < var4.height; ++var6) {
            for(var7 = 0; var7 < var4.width; ++var7) {
               var8 = var2.relative(var3.rotate(Direction.SOUTH), 8 + (var6 - this.startY) * 8);
               var8 = var8.relative(var3.rotate(Direction.EAST), (var7 - this.startX) * 8);
               var9 = var5 != null && WoodlandMansionPieces.MansionGrid.isHouse(var5, var7, var6);
               if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6) && !var9) {
                  var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof", var8.above(3), var3));
                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                     var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 6);
                     var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, var3));
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                     var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 0);
                     var10 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 7);
                     var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, var3.getRotated(Rotation.CLOCKWISE_180)));
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                     var10 = var8.relative((Direction)var3.rotate(Direction.WEST), 1);
                     var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                     var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 6);
                     var10 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 6);
                     var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_front", var10, var3.getRotated(Rotation.CLOCKWISE_90)));
                  }
               }
            }
         }

         if (var5 != null) {
            for(var6 = 0; var6 < var4.height; ++var6) {
               for(var7 = 0; var7 < var4.width; ++var7) {
                  var8 = var2.relative(var3.rotate(Direction.SOUTH), 8 + (var6 - this.startY) * 8);
                  var8 = var8.relative(var3.rotate(Direction.EAST), (var7 - this.startX) * 8);
                  var9 = WoodlandMansionPieces.MansionGrid.isHouse(var5, var7, var6);
                  if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6) && var9) {
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                        var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 7);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, var3));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                        var10 = var8.relative((Direction)var3.rotate(Direction.WEST), 1);
                        var10 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 6);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, var3.getRotated(Rotation.CLOCKWISE_180)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                        var10 = var8.relative((Direction)var3.rotate(Direction.WEST), 0);
                        var10 = var10.relative((Direction)var3.rotate(Direction.NORTH), 1);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                        var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 6);
                        var10 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 7);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall", var10, var3.getRotated(Rotation.CLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                           var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 7);
                           var10 = var10.relative((Direction)var3.rotate(Direction.NORTH), 2);
                           var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, var3));
                        }

                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                           var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 8);
                           var10 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 7);
                           var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, var3.getRotated(Rotation.CLOCKWISE_90)));
                        }
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                           var10 = var8.relative((Direction)var3.rotate(Direction.WEST), 2);
                           var10 = var10.relative((Direction)var3.rotate(Direction.NORTH), 1);
                           var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                        }

                        if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                           var10 = var8.relative((Direction)var3.rotate(Direction.WEST), 1);
                           var10 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 8);
                           var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "small_wall_corner", var10, var3.getRotated(Rotation.CLOCKWISE_180)));
                        }
                     }
                  }
               }
            }
         }

         for(var6 = 0; var6 < var4.height; ++var6) {
            for(var7 = 0; var7 < var4.width; ++var7) {
               var8 = var2.relative(var3.rotate(Direction.SOUTH), 8 + (var6 - this.startY) * 8);
               var8 = var8.relative(var3.rotate(Direction.EAST), (var7 - this.startX) * 8);
               var9 = var5 != null && WoodlandMansionPieces.MansionGrid.isHouse(var5, var7, var6);
               if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6) && !var9) {
                  BlockPos var11;
                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6)) {
                     var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 6);
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                        var11 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 6);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var11, var3));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6 + 1)) {
                        var11 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 5);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, var3));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var10, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 + 1, var6 - 1)) {
                        var11 = var8.relative((Direction)var3.rotate(Direction.EAST), 9);
                        var11 = var11.relative((Direction)var3.rotate(Direction.NORTH), 2);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, var3.getRotated(Rotation.CLOCKWISE_90)));
                     }
                  }

                  if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6)) {
                     var10 = var8.relative((Direction)var3.rotate(Direction.EAST), 0);
                     var10 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 0);
                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 + 1)) {
                        var11 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 6);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var11, var3.getRotated(Rotation.CLOCKWISE_90)));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6 + 1)) {
                        var11 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 8);
                        var11 = var11.relative((Direction)var3.rotate(Direction.WEST), 3);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)));
                     }

                     if (!WoodlandMansionPieces.MansionGrid.isHouse(var4, var7, var6 - 1)) {
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_corner", var10, var3.getRotated(Rotation.CLOCKWISE_180)));
                     } else if (WoodlandMansionPieces.MansionGrid.isHouse(var4, var7 - 1, var6 - 1)) {
                        var11 = var10.relative((Direction)var3.rotate(Direction.SOUTH), 1);
                        var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "roof_inner_corner", var11, var3.getRotated(Rotation.CLOCKWISE_180)));
                     }
                  }
               }
            }
         }

      }

      private void entrance(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         Direction var3 = var2.rotation.rotate(Direction.WEST);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "entrance", var2.position.relative((Direction)var3, 9), var2.rotation));
         var2.position = var2.position.relative((Direction)var2.rotation.rotate(Direction.SOUTH), 16);
      }

      private void traverseWallPiece(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var2.wallType, var2.position.relative((Direction)var2.rotation.rotate(Direction.EAST), 7), var2.rotation));
         var2.position = var2.position.relative((Direction)var2.rotation.rotate(Direction.SOUTH), 8);
      }

      private void traverseTurn(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         var2.position = var2.position.relative((Direction)var2.rotation.rotate(Direction.SOUTH), -1);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, "wall_corner", var2.position, var2.rotation));
         var2.position = var2.position.relative((Direction)var2.rotation.rotate(Direction.SOUTH), -7);
         var2.position = var2.position.relative((Direction)var2.rotation.rotate(Direction.WEST), -6);
         var2.rotation = var2.rotation.getRotated(Rotation.CLOCKWISE_90);
      }

      private void traverseInnerTurn(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, WoodlandMansionPieces.PlacementData var2) {
         var2.position = var2.position.relative((Direction)var2.rotation.rotate(Direction.SOUTH), 6);
         var2.position = var2.position.relative((Direction)var2.rotation.rotate(Direction.EAST), 8);
         var2.rotation = var2.rotation.getRotated(Rotation.COUNTERCLOCKWISE_90);
      }

      private void addRoom1x1(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, BlockPos var2, Rotation var3, Direction var4, WoodlandMansionPieces.FloorRoomCollection var5) {
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
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var7, var9, var6));
      }

      private void addRoom1x2(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, BlockPos var2, Rotation var3, Direction var4, Direction var5, WoodlandMansionPieces.FloorRoomCollection var6, boolean var7) {
         BlockPos var8;
         if (var5 == Direction.EAST && var4 == Direction.SOUTH) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 1);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3));
         } else if (var5 == Direction.EAST && var4 == Direction.NORTH) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 1);
            var8 = var8.relative((Direction)var3.rotate(Direction.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3, Mirror.LEFT_RIGHT));
         } else if (var5 == Direction.WEST && var4 == Direction.NORTH) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 7);
            var8 = var8.relative((Direction)var3.rotate(Direction.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3.getRotated(Rotation.CLOCKWISE_180)));
         } else if (var5 == Direction.WEST && var4 == Direction.SOUTH) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 7);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3, Mirror.FRONT_BACK));
         } else if (var5 == Direction.SOUTH && var4 == Direction.EAST) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 1);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3.getRotated(Rotation.CLOCKWISE_90), Mirror.LEFT_RIGHT));
         } else if (var5 == Direction.SOUTH && var4 == Direction.WEST) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 7);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3.getRotated(Rotation.CLOCKWISE_90)));
         } else if (var5 == Direction.NORTH && var4 == Direction.WEST) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 7);
            var8 = var8.relative((Direction)var3.rotate(Direction.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3.getRotated(Rotation.CLOCKWISE_90), Mirror.FRONT_BACK));
         } else if (var5 == Direction.NORTH && var4 == Direction.EAST) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 1);
            var8 = var8.relative((Direction)var3.rotate(Direction.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2SideEntrance(this.random, var7), var8, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)));
         } else if (var5 == Direction.SOUTH && var4 == Direction.NORTH) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 1);
            var8 = var8.relative((Direction)var3.rotate(Direction.NORTH), 8);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2FrontEntrance(this.random, var7), var8, var3));
         } else if (var5 == Direction.NORTH && var4 == Direction.SOUTH) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 7);
            var8 = var8.relative((Direction)var3.rotate(Direction.SOUTH), 14);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2FrontEntrance(this.random, var7), var8, var3.getRotated(Rotation.CLOCKWISE_180)));
         } else if (var5 == Direction.WEST && var4 == Direction.EAST) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 15);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2FrontEntrance(this.random, var7), var8, var3.getRotated(Rotation.CLOCKWISE_90)));
         } else if (var5 == Direction.EAST && var4 == Direction.WEST) {
            var8 = var2.relative((Direction)var3.rotate(Direction.WEST), 7);
            var8 = var8.relative((Direction)var3.rotate(Direction.SOUTH), 6);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2FrontEntrance(this.random, var7), var8, var3.getRotated(Rotation.COUNTERCLOCKWISE_90)));
         } else if (var5 == Direction.UP && var4 == Direction.EAST) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 15);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2Secret(this.random), var8, var3.getRotated(Rotation.CLOCKWISE_90)));
         } else if (var5 == Direction.UP && var4 == Direction.SOUTH) {
            var8 = var2.relative((Direction)var3.rotate(Direction.EAST), 1);
            var8 = var8.relative((Direction)var3.rotate(Direction.NORTH), 0);
            var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get1x2Secret(this.random), var8, var3));
         }

      }

      private void addRoom2x2(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, BlockPos var2, Rotation var3, Direction var4, Direction var5, WoodlandMansionPieces.FloorRoomCollection var6) {
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

         BlockPos var11 = var2.relative((Direction)var3.rotate(Direction.EAST), var7);
         var11 = var11.relative((Direction)var3.rotate(Direction.SOUTH), var8);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var6.get2x2(this.random), var11, var9, var10));
      }

      private void addRoom2x2Secret(List<WoodlandMansionPieces.WoodlandMansionPiece> var1, BlockPos var2, Rotation var3, WoodlandMansionPieces.FloorRoomCollection var4) {
         BlockPos var5 = var2.relative((Direction)var3.rotate(Direction.EAST), 1);
         var1.add(new WoodlandMansionPieces.WoodlandMansionPiece(this.structureManager, var4.get2x2Secret(this.random), var5, var3, Mirror.NONE));
      }
   }

   static class PlacementData {
      public Rotation rotation;
      public BlockPos position;
      public String wallType;

      private PlacementData() {
         super();
      }

      // $FF: synthetic method
      PlacementData(Object var1) {
         this();
      }
   }

   public static class WoodlandMansionPiece extends TemplateStructurePiece {
      private final String templateName;
      private final Rotation rotation;
      private final Mirror mirror;

      public WoodlandMansionPiece(StructureManager var1, String var2, BlockPos var3, Rotation var4) {
         this(var1, var2, var3, var4, Mirror.NONE);
      }

      public WoodlandMansionPiece(StructureManager var1, String var2, BlockPos var3, Rotation var4, Mirror var5) {
         super(StructurePieceType.WOODLAND_MANSION_PIECE, 0);
         this.templateName = var2;
         this.templatePosition = var3;
         this.rotation = var4;
         this.mirror = var5;
         this.loadTemplate(var1);
      }

      public WoodlandMansionPiece(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.WOODLAND_MANSION_PIECE, var2);
         this.templateName = var2.getString("Template");
         this.rotation = Rotation.valueOf(var2.getString("Rot"));
         this.mirror = Mirror.valueOf(var2.getString("Mi"));
         this.loadTemplate(var1);
      }

      private void loadTemplate(StructureManager var1) {
         StructureTemplate var2 = var1.getOrCreate(new ResourceLocation("woodland_mansion/" + this.templateName));
         StructurePlaceSettings var3 = (new StructurePlaceSettings()).setIgnoreEntities(true).setRotation(this.rotation).setMirror(this.mirror).addProcessor(BlockIgnoreProcessor.STRUCTURE_BLOCK);
         this.setup(var2, this.templatePosition, var3);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putString("Template", this.templateName);
         var1.putString("Rot", this.placeSettings.getRotation().name());
         var1.putString("Mi", this.placeSettings.getMirror().name());
      }

      protected void handleDataMarker(String var1, BlockPos var2, ServerLevelAccessor var3, Random var4, BoundingBox var5) {
         if (var1.startsWith("Chest")) {
            Rotation var6 = this.placeSettings.getRotation();
            BlockState var7 = Blocks.CHEST.defaultBlockState();
            if ("ChestWest".equals(var1)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.WEST));
            } else if ("ChestEast".equals(var1)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.EAST));
            } else if ("ChestSouth".equals(var1)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.SOUTH));
            } else if ("ChestNorth".equals(var1)) {
               var7 = (BlockState)var7.setValue(ChestBlock.FACING, var6.rotate(Direction.NORTH));
            }

            this.createChest(var3, var5, var4, var2, BuiltInLootTables.WOODLAND_MANSION, var7);
         } else {
            byte var8 = -1;
            switch(var1.hashCode()) {
            case -1505748702:
               if (var1.equals("Warrior")) {
                  var8 = 1;
               }
               break;
            case 2390418:
               if (var1.equals("Mage")) {
                  var8 = 0;
               }
            }

            AbstractIllager var9;
            switch(var8) {
            case 0:
               var9 = (AbstractIllager)EntityType.EVOKER.create(var3.getLevel());
               break;
            case 1:
               var9 = (AbstractIllager)EntityType.VINDICATOR.create(var3.getLevel());
               break;
            default:
               return;
            }

            var9.setPersistenceRequired();
            var9.moveTo(var2, 0.0F, 0.0F);
            var9.finalizeSpawn(var3, var3.getCurrentDifficultyAt(var9.blockPosition()), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            var3.addFreshEntityWithPassengers(var9);
            var3.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
         }

      }
   }
}
