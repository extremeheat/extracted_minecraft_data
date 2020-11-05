package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanMonumentPieces {
   static class FitDoubleYZRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleYZRoom() {
         super();
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition var1) {
         if (var1.hasOpening[Direction.NORTH.get3DDataValue()] && !var1.connections[Direction.NORTH.get3DDataValue()].claimed && var1.hasOpening[Direction.UP.get3DDataValue()] && !var1.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition var2 = var1.connections[Direction.NORTH.get3DDataValue()];
            return var2.hasOpening[Direction.UP.get3DDataValue()] && !var2.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.claimed = true;
         var2.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         var2.connections[Direction.UP.get3DDataValue()].claimed = true;
         var2.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleYZRoom(var1, var2);
      }

      // $FF: synthetic method
      FitDoubleYZRoom(Object var1) {
         this();
      }
   }

   static class FitDoubleXYRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleXYRoom() {
         super();
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition var1) {
         if (var1.hasOpening[Direction.EAST.get3DDataValue()] && !var1.connections[Direction.EAST.get3DDataValue()].claimed && var1.hasOpening[Direction.UP.get3DDataValue()] && !var1.connections[Direction.UP.get3DDataValue()].claimed) {
            OceanMonumentPieces.RoomDefinition var2 = var1.connections[Direction.EAST.get3DDataValue()];
            return var2.hasOpening[Direction.UP.get3DDataValue()] && !var2.connections[Direction.UP.get3DDataValue()].claimed;
         } else {
            return false;
         }
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.claimed = true;
         var2.connections[Direction.EAST.get3DDataValue()].claimed = true;
         var2.connections[Direction.UP.get3DDataValue()].claimed = true;
         var2.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleXYRoom(var1, var2);
      }

      // $FF: synthetic method
      FitDoubleXYRoom(Object var1) {
         this();
      }
   }

   static class FitDoubleZRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleZRoom() {
         super();
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition var1) {
         return var1.hasOpening[Direction.NORTH.get3DDataValue()] && !var1.connections[Direction.NORTH.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         OceanMonumentPieces.RoomDefinition var4 = var2;
         if (!var2.hasOpening[Direction.NORTH.get3DDataValue()] || var2.connections[Direction.NORTH.get3DDataValue()].claimed) {
            var4 = var2.connections[Direction.SOUTH.get3DDataValue()];
         }

         var4.claimed = true;
         var4.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleZRoom(var1, var4);
      }

      // $FF: synthetic method
      FitDoubleZRoom(Object var1) {
         this();
      }
   }

   static class FitDoubleXRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleXRoom() {
         super();
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition var1) {
         return var1.hasOpening[Direction.EAST.get3DDataValue()] && !var1.connections[Direction.EAST.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.claimed = true;
         var2.connections[Direction.EAST.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleXRoom(var1, var2);
      }

      // $FF: synthetic method
      FitDoubleXRoom(Object var1) {
         this();
      }
   }

   static class FitDoubleYRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitDoubleYRoom() {
         super();
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition var1) {
         return var1.hasOpening[Direction.UP.get3DDataValue()] && !var1.connections[Direction.UP.get3DDataValue()].claimed;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.claimed = true;
         var2.connections[Direction.UP.get3DDataValue()].claimed = true;
         return new OceanMonumentPieces.OceanMonumentDoubleYRoom(var1, var2);
      }

      // $FF: synthetic method
      FitDoubleYRoom(Object var1) {
         this();
      }
   }

   static class FitSimpleTopRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitSimpleTopRoom() {
         super();
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition var1) {
         return !var1.hasOpening[Direction.WEST.get3DDataValue()] && !var1.hasOpening[Direction.EAST.get3DDataValue()] && !var1.hasOpening[Direction.NORTH.get3DDataValue()] && !var1.hasOpening[Direction.SOUTH.get3DDataValue()] && !var1.hasOpening[Direction.UP.get3DDataValue()];
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.claimed = true;
         return new OceanMonumentPieces.OceanMonumentSimpleTopRoom(var1, var2);
      }

      // $FF: synthetic method
      FitSimpleTopRoom(Object var1) {
         this();
      }
   }

   static class FitSimpleRoom implements OceanMonumentPieces.MonumentRoomFitter {
      private FitSimpleRoom() {
         super();
      }

      public boolean fits(OceanMonumentPieces.RoomDefinition var1) {
         return true;
      }

      public OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         var2.claimed = true;
         return new OceanMonumentPieces.OceanMonumentSimpleRoom(var1, var2, var3);
      }

      // $FF: synthetic method
      FitSimpleRoom(Object var1) {
         this();
      }
   }

   interface MonumentRoomFitter {
      boolean fits(OceanMonumentPieces.RoomDefinition var1);

      OceanMonumentPieces.OceanMonumentPiece create(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3);
   }

   static class RoomDefinition {
      private final int index;
      private final OceanMonumentPieces.RoomDefinition[] connections = new OceanMonumentPieces.RoomDefinition[6];
      private final boolean[] hasOpening = new boolean[6];
      private boolean claimed;
      private boolean isSource;
      private int scanIndex;

      public RoomDefinition(int var1) {
         super();
         this.index = var1;
      }

      public void setConnection(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         this.connections[var1.get3DDataValue()] = var2;
         var2.connections[var1.getOpposite().get3DDataValue()] = this;
      }

      public void updateOpenings() {
         for(int var1 = 0; var1 < 6; ++var1) {
            this.hasOpening[var1] = this.connections[var1] != null;
         }

      }

      public boolean findSource(int var1) {
         if (this.isSource) {
            return true;
         } else {
            this.scanIndex = var1;

            for(int var2 = 0; var2 < 6; ++var2) {
               if (this.connections[var2] != null && this.hasOpening[var2] && this.connections[var2].scanIndex != var1 && this.connections[var2].findSource(var1)) {
                  return true;
               }
            }

            return false;
         }
      }

      public boolean isSpecial() {
         return this.index >= 75;
      }

      public int countOpenings() {
         int var1 = 0;

         for(int var2 = 0; var2 < 6; ++var2) {
            if (this.hasOpening[var2]) {
               ++var1;
            }
         }

         return var1;
      }
   }

   public static class OceanMonumentPenthouse extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentPenthouse(Direction var1, BoundingBox var2) {
         super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, var1, var2);
      }

      public OceanMonumentPenthouse(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_PENTHOUSE, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 2, -1, 2, 11, -1, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, -1, 0, 1, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 12, -1, 0, 13, -1, 11, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 2, -1, 0, 11, -1, 1, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 2, -1, 12, 11, -1, 13, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 0, 0, 0, 0, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 13, 0, 0, 13, 0, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 0, 0, 12, 0, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 0, 13, 12, 0, 13, BASE_LIGHT, BASE_LIGHT, false);

         for(int var8 = 2; var8 <= 11; var8 += 3) {
            this.placeBlock(var1, LAMP_BLOCK, 0, 0, var8, var5);
            this.placeBlock(var1, LAMP_BLOCK, 13, 0, var8, var5);
            this.placeBlock(var1, LAMP_BLOCK, var8, 0, 0, var5);
         }

         this.generateBox(var1, var5, 2, 0, 3, 4, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 9, 0, 3, 11, 0, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 4, 0, 9, 9, 0, 11, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(var1, BASE_LIGHT, 5, 0, 8, var5);
         this.placeBlock(var1, BASE_LIGHT, 8, 0, 8, var5);
         this.placeBlock(var1, BASE_LIGHT, 10, 0, 10, var5);
         this.placeBlock(var1, BASE_LIGHT, 3, 0, 10, var5);
         this.generateBox(var1, var5, 3, 0, 3, 3, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 10, 0, 3, 10, 0, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 6, 0, 10, 7, 0, 10, BASE_BLACK, BASE_BLACK, false);
         byte var11 = 3;

         for(int var9 = 0; var9 < 2; ++var9) {
            for(int var10 = 2; var10 <= 8; var10 += 3) {
               this.generateBox(var1, var5, var11, 0, var10, var11, 2, var10, BASE_LIGHT, BASE_LIGHT, false);
            }

            var11 = 10;
         }

         this.generateBox(var1, var5, 5, 0, 10, 5, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 8, 0, 10, 8, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, -1, 7, 7, -1, 8, BASE_BLACK, BASE_BLACK, false);
         this.generateWaterBox(var1, var5, 6, -1, 3, 7, -1, 4);
         this.spawnElder(var1, var5, 6, 1, 6);
         return true;
      }
   }

   public static class OceanMonumentWingRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int mainDesign;

      public OceanMonumentWingRoom(Direction var1, BoundingBox var2, int var3) {
         super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, var1, var2);
         this.mainDesign = var3 & 1;
      }

      public OceanMonumentWingRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_WING_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         if (this.mainDesign == 0) {
            int var8;
            for(var8 = 0; var8 < 4; ++var8) {
               this.generateBox(var1, var5, 10 - var8, 3 - var8, 20 - var8, 12 + var8, 3 - var8, 20, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(var1, var5, 7, 0, 6, 15, 0, 16, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 6, 0, 6, 6, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 16, 0, 6, 16, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 1, 7, 7, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 15, 1, 7, 15, 1, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 1, 6, 9, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 13, 1, 6, 15, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 8, 1, 7, 9, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 13, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 9, 0, 5, 13, 0, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 10, 0, 7, 12, 0, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 8, 0, 10, 8, 0, 12, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 14, 0, 10, 14, 0, 12, BASE_BLACK, BASE_BLACK, false);

            for(var8 = 18; var8 >= 7; var8 -= 3) {
               this.placeBlock(var1, LAMP_BLOCK, 6, 3, var8, var5);
               this.placeBlock(var1, LAMP_BLOCK, 16, 3, var8, var5);
            }

            this.placeBlock(var1, LAMP_BLOCK, 10, 0, 10, var5);
            this.placeBlock(var1, LAMP_BLOCK, 12, 0, 10, var5);
            this.placeBlock(var1, LAMP_BLOCK, 10, 0, 12, var5);
            this.placeBlock(var1, LAMP_BLOCK, 12, 0, 12, var5);
            this.placeBlock(var1, LAMP_BLOCK, 8, 3, 6, var5);
            this.placeBlock(var1, LAMP_BLOCK, 14, 3, 6, var5);
            this.placeBlock(var1, BASE_LIGHT, 4, 2, 4, var5);
            this.placeBlock(var1, LAMP_BLOCK, 4, 1, 4, var5);
            this.placeBlock(var1, BASE_LIGHT, 4, 0, 4, var5);
            this.placeBlock(var1, BASE_LIGHT, 18, 2, 4, var5);
            this.placeBlock(var1, LAMP_BLOCK, 18, 1, 4, var5);
            this.placeBlock(var1, BASE_LIGHT, 18, 0, 4, var5);
            this.placeBlock(var1, BASE_LIGHT, 4, 2, 18, var5);
            this.placeBlock(var1, LAMP_BLOCK, 4, 1, 18, var5);
            this.placeBlock(var1, BASE_LIGHT, 4, 0, 18, var5);
            this.placeBlock(var1, BASE_LIGHT, 18, 2, 18, var5);
            this.placeBlock(var1, LAMP_BLOCK, 18, 1, 18, var5);
            this.placeBlock(var1, BASE_LIGHT, 18, 0, 18, var5);
            this.placeBlock(var1, BASE_LIGHT, 9, 7, 20, var5);
            this.placeBlock(var1, BASE_LIGHT, 13, 7, 20, var5);
            this.generateBox(var1, var5, 6, 0, 21, 7, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 15, 0, 21, 16, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.spawnElder(var1, var5, 11, 2, 16);
         } else if (this.mainDesign == 1) {
            this.generateBox(var1, var5, 9, 3, 18, 13, 3, 20, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 9, 0, 18, 9, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 13, 0, 18, 13, 2, 18, BASE_LIGHT, BASE_LIGHT, false);
            byte var12 = 9;
            boolean var9 = true;
            boolean var10 = true;

            int var11;
            for(var11 = 0; var11 < 2; ++var11) {
               this.placeBlock(var1, BASE_LIGHT, var12, 6, 20, var5);
               this.placeBlock(var1, LAMP_BLOCK, var12, 5, 20, var5);
               this.placeBlock(var1, BASE_LIGHT, var12, 4, 20, var5);
               var12 = 13;
            }

            this.generateBox(var1, var5, 7, 3, 7, 15, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
            var12 = 10;

            for(var11 = 0; var11 < 2; ++var11) {
               this.generateBox(var1, var5, var12, 0, 10, var12, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, 0, 12, var12, 6, 12, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(var1, LAMP_BLOCK, var12, 0, 10, var5);
               this.placeBlock(var1, LAMP_BLOCK, var12, 0, 12, var5);
               this.placeBlock(var1, LAMP_BLOCK, var12, 4, 10, var5);
               this.placeBlock(var1, LAMP_BLOCK, var12, 4, 12, var5);
               var12 = 12;
            }

            var12 = 8;

            for(var11 = 0; var11 < 2; ++var11) {
               this.generateBox(var1, var5, var12, 0, 7, var12, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, 0, 14, var12, 2, 14, BASE_LIGHT, BASE_LIGHT, false);
               var12 = 14;
            }

            this.generateBox(var1, var5, 8, 3, 8, 8, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 14, 3, 8, 14, 3, 13, BASE_BLACK, BASE_BLACK, false);
            this.spawnElder(var1, var5, 11, 5, 13);
         }

         return true;
      }
   }

   public static class OceanMonumentCoreRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentCoreRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, 1, var1, var2, 2, 2, 2);
      }

      public OceanMonumentCoreRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_CORE_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBoxOnFillOnly(var1, var5, 1, 8, 0, 14, 8, 14, BASE_GRAY);
         boolean var8 = true;
         BlockState var9 = BASE_LIGHT;
         this.generateBox(var1, var5, 0, 7, 0, 0, 7, 15, var9, var9, false);
         this.generateBox(var1, var5, 15, 7, 0, 15, 7, 15, var9, var9, false);
         this.generateBox(var1, var5, 1, 7, 0, 15, 7, 0, var9, var9, false);
         this.generateBox(var1, var5, 1, 7, 15, 14, 7, 15, var9, var9, false);

         int var11;
         for(var11 = 1; var11 <= 6; ++var11) {
            var9 = BASE_LIGHT;
            if (var11 == 2 || var11 == 6) {
               var9 = BASE_GRAY;
            }

            for(int var10 = 0; var10 <= 15; var10 += 15) {
               this.generateBox(var1, var5, var10, var11, 0, var10, var11, 1, var9, var9, false);
               this.generateBox(var1, var5, var10, var11, 6, var10, var11, 9, var9, var9, false);
               this.generateBox(var1, var5, var10, var11, 14, var10, var11, 15, var9, var9, false);
            }

            this.generateBox(var1, var5, 1, var11, 0, 1, var11, 0, var9, var9, false);
            this.generateBox(var1, var5, 6, var11, 0, 9, var11, 0, var9, var9, false);
            this.generateBox(var1, var5, 14, var11, 0, 14, var11, 0, var9, var9, false);
            this.generateBox(var1, var5, 1, var11, 15, 14, var11, 15, var9, var9, false);
         }

         this.generateBox(var1, var5, 6, 3, 6, 9, 6, 9, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 7, 4, 7, 8, 5, 8, Blocks.GOLD_BLOCK.defaultBlockState(), Blocks.GOLD_BLOCK.defaultBlockState(), false);

         for(var11 = 3; var11 <= 6; var11 += 3) {
            for(int var12 = 6; var12 <= 9; var12 += 3) {
               this.placeBlock(var1, LAMP_BLOCK, var12, var11, 6, var5);
               this.placeBlock(var1, LAMP_BLOCK, var12, var11, 9, var5);
            }
         }

         this.generateBox(var1, var5, 5, 1, 6, 5, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 1, 9, 5, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 1, 6, 10, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 1, 9, 10, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 1, 5, 6, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 9, 1, 5, 9, 2, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 1, 10, 6, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 9, 1, 10, 9, 2, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 2, 5, 5, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 2, 10, 5, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 2, 5, 10, 6, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 2, 10, 10, 6, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 7, 1, 5, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 7, 1, 10, 7, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 7, 9, 5, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 7, 9, 10, 7, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 7, 5, 6, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 7, 10, 6, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 9, 7, 5, 14, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 9, 7, 10, 14, 7, 10, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 2, 1, 2, 2, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 3, 1, 2, 3, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 13, 1, 2, 13, 1, 3, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 12, 1, 2, 12, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 2, 1, 12, 2, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 3, 1, 13, 3, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 13, 1, 12, 13, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 12, 1, 13, 12, 1, 13, BASE_LIGHT, BASE_LIGHT, false);
         return true;
      }
   }

   public static class OceanMonumentDoubleYZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleYZRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, 1, var1, var2, 1, 2, 2);
      }

      public OceanMonumentDoubleYZRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_YZ_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         OceanMonumentPieces.RoomDefinition var8 = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var9 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition var10 = var8.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var11 = var9.connections[Direction.UP.get3DDataValue()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(var1, var5, 0, 8, var8.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(var1, var5, 0, 0, var9.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (var11.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 8, 1, 6, 8, 7, BASE_GRAY);
         }

         if (var10.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 8, 8, 6, 8, 14, BASE_GRAY);
         }

         int var12;
         BlockState var13;
         for(var12 = 1; var12 <= 7; ++var12) {
            var13 = BASE_LIGHT;
            if (var12 == 2 || var12 == 6) {
               var13 = BASE_GRAY;
            }

            this.generateBox(var1, var5, 0, var12, 0, 0, var12, 15, var13, var13, false);
            this.generateBox(var1, var5, 7, var12, 0, 7, var12, 15, var13, var13, false);
            this.generateBox(var1, var5, 1, var12, 0, 6, var12, 0, var13, var13, false);
            this.generateBox(var1, var5, 1, var12, 15, 6, var12, 15, var13, var13, false);
         }

         for(var12 = 1; var12 <= 7; ++var12) {
            var13 = BASE_BLACK;
            if (var12 == 2 || var12 == 6) {
               var13 = LAMP_BLOCK;
            }

            this.generateBox(var1, var5, 3, var12, 7, 4, var12, 8, var13, var13, false);
         }

         if (var9.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 0, 4, 2, 0);
         }

         if (var9.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 7, 1, 3, 7, 2, 4);
         }

         if (var9.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 1, 3, 0, 2, 4);
         }

         if (var8.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 15, 4, 2, 15);
         }

         if (var8.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 1, 11, 0, 2, 12);
         }

         if (var8.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 7, 1, 11, 7, 2, 12);
         }

         if (var11.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 5, 0, 4, 6, 0);
         }

         if (var11.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 7, 5, 3, 7, 6, 4);
            this.generateBox(var1, var5, 5, 4, 2, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 6, 1, 2, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 6, 1, 5, 6, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (var11.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 5, 3, 0, 6, 4);
            this.generateBox(var1, var5, 1, 4, 2, 2, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 1, 2, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 1, 5, 1, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (var10.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 5, 15, 4, 6, 15);
         }

         if (var10.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 5, 11, 0, 6, 12);
            this.generateBox(var1, var5, 1, 4, 10, 2, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 1, 10, 1, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 1, 13, 1, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         if (var10.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 7, 5, 11, 7, 6, 12);
            this.generateBox(var1, var5, 5, 4, 10, 6, 4, 13, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 6, 1, 10, 6, 3, 10, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 6, 1, 13, 6, 3, 13, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleXYRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleXYRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, 1, var1, var2, 2, 2, 1);
      }

      public OceanMonumentDoubleXYRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_XY_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         OceanMonumentPieces.RoomDefinition var8 = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var9 = this.roomDefinition;
         OceanMonumentPieces.RoomDefinition var10 = var9.connections[Direction.UP.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var11 = var8.connections[Direction.UP.get3DDataValue()];
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(var1, var5, 8, 0, var8.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(var1, var5, 0, 0, var9.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (var10.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 8, 1, 7, 8, 6, BASE_GRAY);
         }

         if (var11.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 8, 8, 1, 14, 8, 6, BASE_GRAY);
         }

         for(int var12 = 1; var12 <= 7; ++var12) {
            BlockState var13 = BASE_LIGHT;
            if (var12 == 2 || var12 == 6) {
               var13 = BASE_GRAY;
            }

            this.generateBox(var1, var5, 0, var12, 0, 0, var12, 7, var13, var13, false);
            this.generateBox(var1, var5, 15, var12, 0, 15, var12, 7, var13, var13, false);
            this.generateBox(var1, var5, 1, var12, 0, 15, var12, 0, var13, var13, false);
            this.generateBox(var1, var5, 1, var12, 7, 14, var12, 7, var13, var13, false);
         }

         this.generateBox(var1, var5, 2, 1, 3, 2, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 3, 1, 2, 4, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 3, 1, 5, 4, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 13, 1, 3, 13, 7, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 11, 1, 2, 12, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 11, 1, 5, 12, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 1, 3, 5, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 1, 3, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 7, 2, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 5, 2, 5, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 5, 2, 10, 7, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 5, 5, 5, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 10, 5, 5, 10, 7, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(var1, BASE_LIGHT, 6, 6, 2, var5);
         this.placeBlock(var1, BASE_LIGHT, 9, 6, 2, var5);
         this.placeBlock(var1, BASE_LIGHT, 6, 6, 5, var5);
         this.placeBlock(var1, BASE_LIGHT, 9, 6, 5, var5);
         this.generateBox(var1, var5, 5, 4, 3, 6, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 9, 4, 3, 10, 4, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(var1, LAMP_BLOCK, 5, 4, 2, var5);
         this.placeBlock(var1, LAMP_BLOCK, 5, 4, 5, var5);
         this.placeBlock(var1, LAMP_BLOCK, 10, 4, 2, var5);
         this.placeBlock(var1, LAMP_BLOCK, 10, 4, 5, var5);
         if (var9.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 0, 4, 2, 0);
         }

         if (var9.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 7, 4, 2, 7);
         }

         if (var9.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 1, 3, 0, 2, 4);
         }

         if (var8.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 11, 1, 0, 12, 2, 0);
         }

         if (var8.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 11, 1, 7, 12, 2, 7);
         }

         if (var8.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 15, 1, 3, 15, 2, 4);
         }

         if (var10.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 5, 0, 4, 6, 0);
         }

         if (var10.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 5, 7, 4, 6, 7);
         }

         if (var10.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 5, 3, 0, 6, 4);
         }

         if (var11.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 11, 5, 0, 12, 6, 0);
         }

         if (var11.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 11, 5, 7, 12, 6, 7);
         }

         if (var11.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 15, 5, 3, 15, 6, 4);
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleZRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleZRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, 1, var1, var2, 1, 1, 2);
      }

      public OceanMonumentDoubleZRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Z_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         OceanMonumentPieces.RoomDefinition var8 = this.roomDefinition.connections[Direction.NORTH.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var9 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(var1, var5, 0, 8, var8.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(var1, var5, 0, 0, var9.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (var9.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 4, 1, 6, 4, 7, BASE_GRAY);
         }

         if (var8.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 4, 8, 6, 4, 14, BASE_GRAY);
         }

         this.generateBox(var1, var5, 0, 3, 0, 0, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 7, 3, 0, 7, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 15, 6, 3, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, 2, 0, 0, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 7, 2, 0, 7, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 1, 2, 0, 7, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 1, 2, 15, 6, 2, 15, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 0, 1, 0, 0, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 7, 1, 0, 7, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 0, 7, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 15, 6, 1, 15, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 1, 1, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 1, 1, 6, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 1, 1, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 3, 1, 6, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 13, 1, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 1, 13, 6, 1, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 13, 1, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 3, 13, 6, 3, 14, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 2, 1, 6, 2, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 1, 6, 5, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 2, 1, 9, 2, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 1, 9, 5, 3, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 3, 2, 6, 4, 2, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 3, 2, 9, 4, 2, 9, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 2, 2, 7, 2, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 2, 7, 5, 2, 8, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(var1, LAMP_BLOCK, 2, 2, 5, var5);
         this.placeBlock(var1, LAMP_BLOCK, 5, 2, 5, var5);
         this.placeBlock(var1, LAMP_BLOCK, 2, 2, 10, var5);
         this.placeBlock(var1, LAMP_BLOCK, 5, 2, 10, var5);
         this.placeBlock(var1, BASE_LIGHT, 2, 3, 5, var5);
         this.placeBlock(var1, BASE_LIGHT, 5, 3, 5, var5);
         this.placeBlock(var1, BASE_LIGHT, 2, 3, 10, var5);
         this.placeBlock(var1, BASE_LIGHT, 5, 3, 10, var5);
         if (var9.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 0, 4, 2, 0);
         }

         if (var9.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 7, 1, 3, 7, 2, 4);
         }

         if (var9.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 1, 3, 0, 2, 4);
         }

         if (var8.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 15, 4, 2, 15);
         }

         if (var8.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 1, 11, 0, 2, 12);
         }

         if (var8.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 7, 1, 11, 7, 2, 12);
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleXRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleXRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, 1, var1, var2, 2, 1, 1);
      }

      public OceanMonumentDoubleXRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_X_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         OceanMonumentPieces.RoomDefinition var8 = this.roomDefinition.connections[Direction.EAST.get3DDataValue()];
         OceanMonumentPieces.RoomDefinition var9 = this.roomDefinition;
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(var1, var5, 8, 0, var8.hasOpening[Direction.DOWN.get3DDataValue()]);
            this.generateDefaultFloor(var1, var5, 0, 0, var9.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (var9.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 4, 1, 7, 4, 6, BASE_GRAY);
         }

         if (var8.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 8, 4, 1, 14, 4, 6, BASE_GRAY);
         }

         this.generateBox(var1, var5, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 15, 3, 0, 15, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 0, 15, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 7, 14, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, 2, 0, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 15, 2, 0, 15, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 1, 2, 0, 15, 2, 0, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 1, 2, 7, 14, 2, 7, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 15, 1, 0, 15, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 0, 15, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 7, 14, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 1, 0, 10, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 2, 0, 9, 2, 3, BASE_GRAY, BASE_GRAY, false);
         this.generateBox(var1, var5, 5, 3, 0, 10, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         this.placeBlock(var1, LAMP_BLOCK, 6, 2, 3, var5);
         this.placeBlock(var1, LAMP_BLOCK, 9, 2, 3, var5);
         if (var9.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 0, 4, 2, 0);
         }

         if (var9.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 7, 4, 2, 7);
         }

         if (var9.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 1, 3, 0, 2, 4);
         }

         if (var8.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 11, 1, 0, 12, 2, 0);
         }

         if (var8.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 11, 1, 7, 12, 2, 7);
         }

         if (var8.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 15, 1, 3, 15, 2, 4);
         }

         return true;
      }
   }

   public static class OceanMonumentDoubleYRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentDoubleYRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, 1, var1, var2, 1, 2, 1);
      }

      public OceanMonumentDoubleYRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_DOUBLE_Y_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(var1, var5, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         OceanMonumentPieces.RoomDefinition var8 = this.roomDefinition.connections[Direction.UP.get3DDataValue()];
         if (var8.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 8, 1, 6, 8, 6, BASE_GRAY);
         }

         this.generateBox(var1, var5, 0, 4, 0, 0, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 7, 4, 0, 7, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 4, 0, 6, 4, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 4, 7, 6, 4, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 2, 4, 1, 2, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 4, 2, 1, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 4, 1, 5, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 4, 2, 6, 4, 2, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 2, 4, 5, 2, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 4, 5, 1, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 4, 5, 5, 4, 6, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 4, 5, 6, 4, 5, BASE_LIGHT, BASE_LIGHT, false);
         OceanMonumentPieces.RoomDefinition var9 = this.roomDefinition;

         for(int var10 = 1; var10 <= 5; var10 += 4) {
            byte var11 = 0;
            if (var9.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(var1, var5, 2, var10, var11, 2, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 5, var10, var11, 5, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 3, var10 + 2, var11, 4, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, 0, var10, var11, 7, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 0, var10 + 1, var11, 7, var10 + 1, var11, BASE_GRAY, BASE_GRAY, false);
            }

            var11 = 7;
            if (var9.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(var1, var5, 2, var10, var11, 2, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 5, var10, var11, 5, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 3, var10 + 2, var11, 4, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, 0, var10, var11, 7, var10 + 2, var11, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 0, var10 + 1, var11, 7, var10 + 1, var11, BASE_GRAY, BASE_GRAY, false);
            }

            byte var12 = 0;
            if (var9.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(var1, var5, var12, var10, 2, var12, var10 + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, var10, 5, var12, var10 + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, var10 + 2, 3, var12, var10 + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, var12, var10, 0, var12, var10 + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, var10 + 1, 0, var12, var10 + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            var12 = 7;
            if (var9.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(var1, var5, var12, var10, 2, var12, var10 + 2, 2, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, var10, 5, var12, var10 + 2, 5, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, var10 + 2, 3, var12, var10 + 2, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, var12, var10, 0, var12, var10 + 2, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, var12, var10 + 1, 0, var12, var10 + 1, 7, BASE_GRAY, BASE_GRAY, false);
            }

            var9 = var8;
         }

         return true;
      }
   }

   public static class OceanMonumentSimpleTopRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentSimpleTopRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, 1, var1, var2, 1, 1, 1);
      }

      public OceanMonumentSimpleTopRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_TOP_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(var1, var5, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         for(int var8 = 1; var8 <= 6; ++var8) {
            for(int var9 = 1; var9 <= 6; ++var9) {
               if (var4.nextInt(3) != 0) {
                  int var10 = 2 + (var4.nextInt(4) == 0 ? 0 : 1);
                  BlockState var11 = Blocks.WET_SPONGE.defaultBlockState();
                  this.generateBox(var1, var5, var8, var10, var9, var8, 3, var9, var11, var11, false);
               }
            }
         }

         this.generateBox(var1, var5, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
         this.generateBox(var1, var5, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
         if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 0, 4, 2, 0);
         }

         return true;
      }
   }

   public static class OceanMonumentSimpleRoom extends OceanMonumentPieces.OceanMonumentPiece {
      private int mainDesign;

      public OceanMonumentSimpleRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2, Random var3) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, 1, var1, var2, 1, 1, 1);
         this.mainDesign = var3.nextInt(3);
      }

      public OceanMonumentSimpleRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_SIMPLE_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         if (this.roomDefinition.index / 25 > 0) {
            this.generateDefaultFloor(var1, var5, 0, 0, this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()]);
         }

         if (this.roomDefinition.connections[Direction.UP.get3DDataValue()] == null) {
            this.generateBoxOnFillOnly(var1, var5, 1, 4, 1, 6, 4, 6, BASE_GRAY);
         }

         boolean var8 = this.mainDesign != 0 && var4.nextBoolean() && !this.roomDefinition.hasOpening[Direction.DOWN.get3DDataValue()] && !this.roomDefinition.hasOpening[Direction.UP.get3DDataValue()] && this.roomDefinition.countOpenings() > 1;
         if (this.mainDesign == 0) {
            this.generateBox(var1, var5, 0, 1, 0, 2, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 3, 0, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 2, 0, 0, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var5, 1, 2, 0, 2, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(var1, LAMP_BLOCK, 1, 2, 1, var5);
            this.generateBox(var1, var5, 5, 1, 0, 7, 1, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 5, 3, 0, 7, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 2, 0, 7, 2, 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var5, 5, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(var1, LAMP_BLOCK, 6, 2, 1, var5);
            this.generateBox(var1, var5, 0, 1, 5, 2, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 3, 5, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 2, 5, 0, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var5, 1, 2, 7, 2, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(var1, LAMP_BLOCK, 1, 2, 6, var5);
            this.generateBox(var1, var5, 5, 1, 5, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 5, 3, 5, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 2, 5, 7, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var5, 5, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(var1, LAMP_BLOCK, 6, 2, 6, var5);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(var1, var5, 3, 3, 0, 4, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, 3, 3, 0, 4, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 3, 2, 0, 4, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 3, 1, 0, 4, 1, 1, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(var1, var5, 3, 3, 7, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, 3, 3, 6, 4, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 3, 2, 7, 4, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 3, 1, 6, 4, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(var1, var5, 0, 3, 3, 0, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, 0, 3, 3, 1, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 0, 2, 3, 0, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 0, 1, 3, 1, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(var1, var5, 7, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
            } else {
               this.generateBox(var1, var5, 6, 3, 3, 7, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 7, 2, 3, 7, 2, 4, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 6, 1, 3, 7, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.mainDesign == 1) {
            this.generateBox(var1, var5, 2, 1, 2, 2, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 2, 1, 5, 2, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 5, 1, 5, 5, 3, 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 5, 1, 2, 5, 3, 2, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(var1, LAMP_BLOCK, 2, 2, 2, var5);
            this.placeBlock(var1, LAMP_BLOCK, 2, 2, 5, var5);
            this.placeBlock(var1, LAMP_BLOCK, 5, 2, 5, var5);
            this.placeBlock(var1, LAMP_BLOCK, 5, 2, 2, var5);
            this.generateBox(var1, var5, 0, 1, 0, 1, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 1, 1, 0, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 1, 7, 1, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 1, 6, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 6, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 1, 6, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 6, 1, 0, 7, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 1, 1, 7, 3, 1, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(var1, BASE_GRAY, 1, 2, 0, var5);
            this.placeBlock(var1, BASE_GRAY, 0, 2, 1, var5);
            this.placeBlock(var1, BASE_GRAY, 1, 2, 7, var5);
            this.placeBlock(var1, BASE_GRAY, 0, 2, 6, var5);
            this.placeBlock(var1, BASE_GRAY, 6, 2, 7, var5);
            this.placeBlock(var1, BASE_GRAY, 7, 2, 6, var5);
            this.placeBlock(var1, BASE_GRAY, 6, 2, 0, var5);
            this.placeBlock(var1, BASE_GRAY, 7, 2, 1, var5);
            if (!this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateBox(var1, var5, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 1, 2, 0, 6, 2, 0, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateBox(var1, var5, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 1, 2, 7, 6, 2, 7, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateBox(var1, var5, 0, 3, 1, 0, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 0, 2, 1, 0, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 0, 1, 1, 0, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }

            if (!this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateBox(var1, var5, 7, 3, 1, 7, 3, 6, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var5, 7, 2, 1, 7, 2, 6, BASE_GRAY, BASE_GRAY, false);
               this.generateBox(var1, var5, 7, 1, 1, 7, 1, 6, BASE_LIGHT, BASE_LIGHT, false);
            }
         } else if (this.mainDesign == 2) {
            this.generateBox(var1, var5, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 1, 0, 6, 1, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 1, 7, 6, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 2, 0, 0, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 7, 2, 0, 7, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 1, 2, 0, 6, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 1, 2, 7, 6, 2, 7, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 0, 3, 0, 0, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 7, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 3, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 1, 3, 7, 6, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 0, 1, 3, 0, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 7, 1, 3, 7, 2, 4, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 3, 1, 0, 4, 2, 0, BASE_BLACK, BASE_BLACK, false);
            this.generateBox(var1, var5, 3, 1, 7, 4, 2, 7, BASE_BLACK, BASE_BLACK, false);
            if (this.roomDefinition.hasOpening[Direction.SOUTH.get3DDataValue()]) {
               this.generateWaterBox(var1, var5, 3, 1, 0, 4, 2, 0);
            }

            if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
               this.generateWaterBox(var1, var5, 3, 1, 7, 4, 2, 7);
            }

            if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
               this.generateWaterBox(var1, var5, 0, 1, 3, 0, 2, 4);
            }

            if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
               this.generateWaterBox(var1, var5, 7, 1, 3, 7, 2, 4);
            }
         }

         if (var8) {
            this.generateBox(var1, var5, 3, 1, 3, 4, 1, 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var5, 3, 2, 3, 4, 2, 4, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var5, 3, 3, 3, 4, 3, 4, BASE_LIGHT, BASE_LIGHT, false);
         }

         return true;
      }
   }

   public static class OceanMonumentEntryRoom extends OceanMonumentPieces.OceanMonumentPiece {
      public OceanMonumentEntryRoom(Direction var1, OceanMonumentPieces.RoomDefinition var2) {
         super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, 1, var1, var2, 1, 1, 1);
      }

      public OceanMonumentEntryRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_ENTRY_ROOM, var2);
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         this.generateBox(var1, var5, 0, 3, 0, 2, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 3, 0, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, 2, 0, 1, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 6, 2, 0, 7, 2, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, 1, 0, 0, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 7, 1, 0, 7, 1, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 0, 1, 7, 7, 3, 7, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 1, 1, 0, 2, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         this.generateBox(var1, var5, 5, 1, 0, 6, 3, 0, BASE_LIGHT, BASE_LIGHT, false);
         if (this.roomDefinition.hasOpening[Direction.NORTH.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 3, 1, 7, 4, 2, 7);
         }

         if (this.roomDefinition.hasOpening[Direction.WEST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 0, 1, 3, 1, 2, 4);
         }

         if (this.roomDefinition.hasOpening[Direction.EAST.get3DDataValue()]) {
            this.generateWaterBox(var1, var5, 6, 1, 3, 7, 2, 4);
         }

         return true;
      }
   }

   public static class MonumentBuilding extends OceanMonumentPieces.OceanMonumentPiece {
      private OceanMonumentPieces.RoomDefinition sourceRoom;
      private OceanMonumentPieces.RoomDefinition coreRoom;
      private final List<OceanMonumentPieces.OceanMonumentPiece> childPieces = Lists.newArrayList();

      public MonumentBuilding(Random var1, int var2, int var3, Direction var4) {
         super(StructurePieceType.OCEAN_MONUMENT_BUILDING, 0);
         this.setOrientation(var4);
         Direction var5 = this.getOrientation();
         if (var5.getAxis() == Direction.Axis.Z) {
            this.boundingBox = new BoundingBox(var2, 39, var3, var2 + 58 - 1, 61, var3 + 58 - 1);
         } else {
            this.boundingBox = new BoundingBox(var2, 39, var3, var2 + 58 - 1, 61, var3 + 58 - 1);
         }

         List var6 = this.generateRoomGraph(var1);
         this.sourceRoom.claimed = true;
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentEntryRoom(var5, this.sourceRoom));
         this.childPieces.add(new OceanMonumentPieces.OceanMonumentCoreRoom(var5, this.coreRoom));
         ArrayList var7 = Lists.newArrayList();
         var7.add(new OceanMonumentPieces.FitDoubleXYRoom());
         var7.add(new OceanMonumentPieces.FitDoubleYZRoom());
         var7.add(new OceanMonumentPieces.FitDoubleZRoom());
         var7.add(new OceanMonumentPieces.FitDoubleXRoom());
         var7.add(new OceanMonumentPieces.FitDoubleYRoom());
         var7.add(new OceanMonumentPieces.FitSimpleTopRoom());
         var7.add(new OceanMonumentPieces.FitSimpleRoom());
         Iterator var8 = var6.iterator();

         while(true) {
            while(true) {
               OceanMonumentPieces.RoomDefinition var9;
               do {
                  do {
                     if (!var8.hasNext()) {
                        int var15 = this.boundingBox.y0;
                        int var16 = this.getWorldX(9, 22);
                        int var17 = this.getWorldZ(9, 22);
                        Iterator var18 = this.childPieces.iterator();

                        while(var18.hasNext()) {
                           OceanMonumentPieces.OceanMonumentPiece var12 = (OceanMonumentPieces.OceanMonumentPiece)var18.next();
                           var12.getBoundingBox().move(var16, var15, var17);
                        }

                        BoundingBox var19 = BoundingBox.createProper(this.getWorldX(1, 1), this.getWorldY(1), this.getWorldZ(1, 1), this.getWorldX(23, 21), this.getWorldY(8), this.getWorldZ(23, 21));
                        BoundingBox var20 = BoundingBox.createProper(this.getWorldX(34, 1), this.getWorldY(1), this.getWorldZ(34, 1), this.getWorldX(56, 21), this.getWorldY(8), this.getWorldZ(56, 21));
                        BoundingBox var13 = BoundingBox.createProper(this.getWorldX(22, 22), this.getWorldY(13), this.getWorldZ(22, 22), this.getWorldX(35, 35), this.getWorldY(17), this.getWorldZ(35, 35));
                        int var14 = var1.nextInt();
                        this.childPieces.add(new OceanMonumentPieces.OceanMonumentWingRoom(var5, var19, var14++));
                        this.childPieces.add(new OceanMonumentPieces.OceanMonumentWingRoom(var5, var20, var14++));
                        this.childPieces.add(new OceanMonumentPieces.OceanMonumentPenthouse(var5, var13));
                        return;
                     }

                     var9 = (OceanMonumentPieces.RoomDefinition)var8.next();
                  } while(var9.claimed);
               } while(var9.isSpecial());

               Iterator var10 = var7.iterator();

               while(var10.hasNext()) {
                  OceanMonumentPieces.MonumentRoomFitter var11 = (OceanMonumentPieces.MonumentRoomFitter)var10.next();
                  if (var11.fits(var9)) {
                     this.childPieces.add(var11.create(var5, var9, var1));
                     break;
                  }
               }
            }
         }
      }

      public MonumentBuilding(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.OCEAN_MONUMENT_BUILDING, var2);
      }

      private List<OceanMonumentPieces.RoomDefinition> generateRoomGraph(Random var1) {
         OceanMonumentPieces.RoomDefinition[] var2 = new OceanMonumentPieces.RoomDefinition[75];

         int var3;
         int var4;
         boolean var5;
         int var6;
         for(var3 = 0; var3 < 5; ++var3) {
            for(var4 = 0; var4 < 4; ++var4) {
               var5 = false;
               var6 = getRoomIndex(var3, 0, var4);
               var2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         for(var3 = 0; var3 < 5; ++var3) {
            for(var4 = 0; var4 < 4; ++var4) {
               var5 = true;
               var6 = getRoomIndex(var3, 1, var4);
               var2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         for(var3 = 1; var3 < 4; ++var3) {
            for(var4 = 0; var4 < 2; ++var4) {
               var5 = true;
               var6 = getRoomIndex(var3, 2, var4);
               var2[var6] = new OceanMonumentPieces.RoomDefinition(var6);
            }
         }

         this.sourceRoom = var2[GRIDROOM_SOURCE_INDEX];

         int var8;
         int var9;
         int var11;
         int var12;
         int var13;
         for(var3 = 0; var3 < 5; ++var3) {
            for(var4 = 0; var4 < 5; ++var4) {
               for(int var16 = 0; var16 < 3; ++var16) {
                  var6 = getRoomIndex(var3, var16, var4);
                  if (var2[var6] != null) {
                     Direction[] var7 = Direction.values();
                     var8 = var7.length;

                     for(var9 = 0; var9 < var8; ++var9) {
                        Direction var10 = var7[var9];
                        var11 = var3 + var10.getStepX();
                        var12 = var16 + var10.getStepY();
                        var13 = var4 + var10.getStepZ();
                        if (var11 >= 0 && var11 < 5 && var13 >= 0 && var13 < 5 && var12 >= 0 && var12 < 3) {
                           int var14 = getRoomIndex(var11, var12, var13);
                           if (var2[var14] != null) {
                              if (var13 == var4) {
                                 var2[var6].setConnection(var10, var2[var14]);
                              } else {
                                 var2[var6].setConnection(var10.getOpposite(), var2[var14]);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         OceanMonumentPieces.RoomDefinition var15 = new OceanMonumentPieces.RoomDefinition(1003);
         OceanMonumentPieces.RoomDefinition var17 = new OceanMonumentPieces.RoomDefinition(1001);
         OceanMonumentPieces.RoomDefinition var18 = new OceanMonumentPieces.RoomDefinition(1002);
         var2[GRIDROOM_TOP_CONNECT_INDEX].setConnection(Direction.UP, var15);
         var2[GRIDROOM_LEFTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, var17);
         var2[GRIDROOM_RIGHTWING_CONNECT_INDEX].setConnection(Direction.SOUTH, var18);
         var15.claimed = true;
         var17.claimed = true;
         var18.claimed = true;
         this.sourceRoom.isSource = true;
         this.coreRoom = var2[getRoomIndex(var1.nextInt(4), 0, 2)];
         this.coreRoom.claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         this.coreRoom.connections[Direction.EAST.get3DDataValue()].connections[Direction.NORTH.get3DDataValue()].connections[Direction.UP.get3DDataValue()].claimed = true;
         ArrayList var19 = Lists.newArrayList();
         OceanMonumentPieces.RoomDefinition[] var20 = var2;
         var8 = var2.length;

         for(var9 = 0; var9 < var8; ++var9) {
            OceanMonumentPieces.RoomDefinition var23 = var20[var9];
            if (var23 != null) {
               var23.updateOpenings();
               var19.add(var23);
            }
         }

         var15.updateOpenings();
         Collections.shuffle(var19, var1);
         int var21 = 1;
         Iterator var22 = var19.iterator();

         label95:
         while(var22.hasNext()) {
            OceanMonumentPieces.RoomDefinition var24 = (OceanMonumentPieces.RoomDefinition)var22.next();
            int var25 = 0;
            var11 = 0;

            while(true) {
               while(true) {
                  do {
                     if (var25 >= 2 || var11 >= 5) {
                        continue label95;
                     }

                     ++var11;
                     var12 = var1.nextInt(6);
                  } while(!var24.hasOpening[var12]);

                  var13 = Direction.from3DDataValue(var12).getOpposite().get3DDataValue();
                  var24.hasOpening[var12] = false;
                  var24.connections[var12].hasOpening[var13] = false;
                  if (var24.findSource(var21++) && var24.connections[var12].findSource(var21++)) {
                     ++var25;
                  } else {
                     var24.hasOpening[var12] = true;
                     var24.connections[var12].hasOpening[var13] = true;
                  }
               }
            }
         }

         var19.add(var15);
         var19.add(var17);
         var19.add(var18);
         return var19;
      }

      public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
         int var8 = Math.max(var1.getSeaLevel(), 64) - this.boundingBox.y0;
         this.generateWaterBox(var1, var5, 0, 0, 0, 58, var8, 58);
         this.generateWing(false, 0, var1, var4, var5);
         this.generateWing(true, 33, var1, var4, var5);
         this.generateEntranceArchs(var1, var4, var5);
         this.generateEntranceWall(var1, var4, var5);
         this.generateRoofPiece(var1, var4, var5);
         this.generateLowerWall(var1, var4, var5);
         this.generateMiddleWall(var1, var4, var5);
         this.generateUpperWall(var1, var4, var5);

         int var9;
         label72:
         for(var9 = 0; var9 < 7; ++var9) {
            int var10 = 0;

            while(true) {
               while(true) {
                  if (var10 >= 7) {
                     continue label72;
                  }

                  if (var10 == 0 && var9 == 3) {
                     var10 = 6;
                  }

                  int var11 = var9 * 9;
                  int var12 = var10 * 9;

                  for(int var13 = 0; var13 < 4; ++var13) {
                     for(int var14 = 0; var14 < 4; ++var14) {
                        this.placeBlock(var1, BASE_LIGHT, var11 + var13, 0, var12 + var14, var5);
                        this.fillColumnDown(var1, BASE_LIGHT, var11 + var13, -1, var12 + var14, var5);
                     }
                  }

                  if (var9 != 0 && var9 != 6) {
                     var10 += 6;
                  } else {
                     ++var10;
                  }
               }
            }
         }

         for(var9 = 0; var9 < 5; ++var9) {
            this.generateWaterBox(var1, var5, -1 - var9, 0 + var9 * 2, -1 - var9, -1 - var9, 23, 58 + var9);
            this.generateWaterBox(var1, var5, 58 + var9, 0 + var9 * 2, -1 - var9, 58 + var9, 23, 58 + var9);
            this.generateWaterBox(var1, var5, 0 - var9, 0 + var9 * 2, -1 - var9, 57 + var9, 23, -1 - var9);
            this.generateWaterBox(var1, var5, 0 - var9, 0 + var9 * 2, 58 + var9, 57 + var9, 23, 58 + var9);
         }

         Iterator var15 = this.childPieces.iterator();

         while(var15.hasNext()) {
            OceanMonumentPieces.OceanMonumentPiece var16 = (OceanMonumentPieces.OceanMonumentPiece)var15.next();
            if (var16.getBoundingBox().intersects(var5)) {
               var16.postProcess(var1, var2, var3, var4, var5, var6, var7);
            }
         }

         return true;
      }

      private void generateWing(boolean var1, int var2, WorldGenLevel var3, Random var4, BoundingBox var5) {
         boolean var6 = true;
         if (this.chunkIntersects(var5, var2, 0, var2 + 23, 20)) {
            this.generateBox(var3, var5, var2 + 0, 0, 0, var2 + 24, 0, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var3, var5, var2 + 0, 1, 0, var2 + 24, 10, 20);

            int var7;
            for(var7 = 0; var7 < 4; ++var7) {
               this.generateBox(var3, var5, var2 + var7, var7 + 1, var7, var2 + var7, var7 + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var3, var5, var2 + var7 + 7, var7 + 5, var7 + 7, var2 + var7 + 7, var7 + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var3, var5, var2 + 17 - var7, var7 + 5, var7 + 7, var2 + 17 - var7, var7 + 5, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var3, var5, var2 + 24 - var7, var7 + 1, var7, var2 + 24 - var7, var7 + 1, 20, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var3, var5, var2 + var7 + 1, var7 + 1, var7, var2 + 23 - var7, var7 + 1, var7, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var3, var5, var2 + var7 + 8, var7 + 5, var7 + 7, var2 + 16 - var7, var7 + 5, var7 + 7, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(var3, var5, var2 + 4, 4, 4, var2 + 6, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var3, var5, var2 + 7, 4, 4, var2 + 17, 4, 6, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var3, var5, var2 + 18, 4, 4, var2 + 20, 4, 20, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var3, var5, var2 + 11, 8, 11, var2 + 13, 8, 20, BASE_GRAY, BASE_GRAY, false);
            this.placeBlock(var3, DOT_DECO_DATA, var2 + 12, 9, 12, var5);
            this.placeBlock(var3, DOT_DECO_DATA, var2 + 12, 9, 15, var5);
            this.placeBlock(var3, DOT_DECO_DATA, var2 + 12, 9, 18, var5);
            var7 = var2 + (var1 ? 19 : 5);
            int var8 = var2 + (var1 ? 5 : 19);

            int var9;
            for(var9 = 20; var9 >= 5; var9 -= 3) {
               this.placeBlock(var3, DOT_DECO_DATA, var7, 5, var9, var5);
            }

            for(var9 = 19; var9 >= 7; var9 -= 3) {
               this.placeBlock(var3, DOT_DECO_DATA, var8, 5, var9, var5);
            }

            for(var9 = 0; var9 < 4; ++var9) {
               int var10 = var1 ? var2 + 24 - (17 - var9 * 3) : var2 + 17 - var9 * 3;
               this.placeBlock(var3, DOT_DECO_DATA, var10, 5, 5, var5);
            }

            this.placeBlock(var3, DOT_DECO_DATA, var8, 5, 5, var5);
            this.generateBox(var3, var5, var2 + 11, 1, 12, var2 + 13, 7, 12, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var3, var5, var2 + 12, 1, 11, var2 + 12, 7, 13, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateEntranceArchs(WorldGenLevel var1, Random var2, BoundingBox var3) {
         if (this.chunkIntersects(var3, 22, 5, 35, 17)) {
            this.generateWaterBox(var1, var3, 25, 0, 0, 32, 8, 20);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, 24, 2, 5 + var4 * 4, 24, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var3, 22, 4, 5 + var4 * 4, 23, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(var1, BASE_LIGHT, 25, 5, 5 + var4 * 4, var3);
               this.placeBlock(var1, BASE_LIGHT, 26, 6, 5 + var4 * 4, var3);
               this.placeBlock(var1, LAMP_BLOCK, 26, 5, 5 + var4 * 4, var3);
               this.generateBox(var1, var3, 33, 2, 5 + var4 * 4, 33, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var3, 34, 4, 5 + var4 * 4, 35, 4, 5 + var4 * 4, BASE_LIGHT, BASE_LIGHT, false);
               this.placeBlock(var1, BASE_LIGHT, 32, 5, 5 + var4 * 4, var3);
               this.placeBlock(var1, BASE_LIGHT, 31, 6, 5 + var4 * 4, var3);
               this.placeBlock(var1, LAMP_BLOCK, 31, 5, 5 + var4 * 4, var3);
               this.generateBox(var1, var3, 27, 6, 5 + var4 * 4, 30, 6, 5 + var4 * 4, BASE_GRAY, BASE_GRAY, false);
            }
         }

      }

      private void generateEntranceWall(WorldGenLevel var1, Random var2, BoundingBox var3) {
         if (this.chunkIntersects(var3, 15, 20, 42, 21)) {
            this.generateBox(var1, var3, 15, 0, 21, 42, 0, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 26, 1, 21, 31, 3, 21);
            this.generateBox(var1, var3, 21, 12, 21, 36, 12, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 17, 11, 21, 40, 11, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 16, 10, 21, 41, 10, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 15, 7, 21, 42, 9, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 16, 6, 21, 41, 6, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 17, 5, 21, 40, 5, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 21, 4, 21, 36, 4, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 22, 3, 21, 26, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 31, 3, 21, 35, 3, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 23, 2, 21, 25, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 32, 2, 21, 34, 2, 21, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 28, 4, 20, 29, 4, 21, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(var1, BASE_LIGHT, 27, 3, 21, var3);
            this.placeBlock(var1, BASE_LIGHT, 30, 3, 21, var3);
            this.placeBlock(var1, BASE_LIGHT, 26, 2, 21, var3);
            this.placeBlock(var1, BASE_LIGHT, 31, 2, 21, var3);
            this.placeBlock(var1, BASE_LIGHT, 25, 1, 21, var3);
            this.placeBlock(var1, BASE_LIGHT, 32, 1, 21, var3);

            int var4;
            for(var4 = 0; var4 < 7; ++var4) {
               this.placeBlock(var1, BASE_BLACK, 28 - var4, 6 + var4, 21, var3);
               this.placeBlock(var1, BASE_BLACK, 29 + var4, 6 + var4, 21, var3);
            }

            for(var4 = 0; var4 < 4; ++var4) {
               this.placeBlock(var1, BASE_BLACK, 28 - var4, 9 + var4, 21, var3);
               this.placeBlock(var1, BASE_BLACK, 29 + var4, 9 + var4, 21, var3);
            }

            this.placeBlock(var1, BASE_BLACK, 28, 12, 21, var3);
            this.placeBlock(var1, BASE_BLACK, 29, 12, 21, var3);

            for(var4 = 0; var4 < 3; ++var4) {
               this.placeBlock(var1, BASE_BLACK, 22 - var4 * 2, 8, 21, var3);
               this.placeBlock(var1, BASE_BLACK, 22 - var4 * 2, 9, 21, var3);
               this.placeBlock(var1, BASE_BLACK, 35 + var4 * 2, 8, 21, var3);
               this.placeBlock(var1, BASE_BLACK, 35 + var4 * 2, 9, 21, var3);
            }

            this.generateWaterBox(var1, var3, 15, 13, 21, 42, 15, 21);
            this.generateWaterBox(var1, var3, 15, 1, 21, 15, 6, 21);
            this.generateWaterBox(var1, var3, 16, 1, 21, 16, 5, 21);
            this.generateWaterBox(var1, var3, 17, 1, 21, 20, 4, 21);
            this.generateWaterBox(var1, var3, 21, 1, 21, 21, 3, 21);
            this.generateWaterBox(var1, var3, 22, 1, 21, 22, 2, 21);
            this.generateWaterBox(var1, var3, 23, 1, 21, 24, 1, 21);
            this.generateWaterBox(var1, var3, 42, 1, 21, 42, 6, 21);
            this.generateWaterBox(var1, var3, 41, 1, 21, 41, 5, 21);
            this.generateWaterBox(var1, var3, 37, 1, 21, 40, 4, 21);
            this.generateWaterBox(var1, var3, 36, 1, 21, 36, 3, 21);
            this.generateWaterBox(var1, var3, 33, 1, 21, 34, 1, 21);
            this.generateWaterBox(var1, var3, 35, 1, 21, 35, 2, 21);
         }

      }

      private void generateRoofPiece(WorldGenLevel var1, Random var2, BoundingBox var3) {
         if (this.chunkIntersects(var3, 21, 21, 36, 36)) {
            this.generateBox(var1, var3, 21, 0, 22, 36, 0, 36, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 21, 1, 22, 36, 23, 36);

            for(int var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, 21 + var4, 13 + var4, 21 + var4, 36 - var4, 13 + var4, 21 + var4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var3, 21 + var4, 13 + var4, 36 - var4, 36 - var4, 13 + var4, 36 - var4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var3, 21 + var4, 13 + var4, 22 + var4, 21 + var4, 13 + var4, 35 - var4, BASE_LIGHT, BASE_LIGHT, false);
               this.generateBox(var1, var3, 36 - var4, 13 + var4, 22 + var4, 36 - var4, 13 + var4, 35 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(var1, var3, 25, 16, 25, 32, 16, 32, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 25, 17, 25, 25, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var3, 32, 17, 25, 32, 19, 25, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var3, 25, 17, 32, 25, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var3, 32, 17, 32, 32, 19, 32, BASE_LIGHT, BASE_LIGHT, false);
            this.placeBlock(var1, BASE_LIGHT, 26, 20, 26, var3);
            this.placeBlock(var1, BASE_LIGHT, 27, 21, 27, var3);
            this.placeBlock(var1, LAMP_BLOCK, 27, 20, 27, var3);
            this.placeBlock(var1, BASE_LIGHT, 26, 20, 31, var3);
            this.placeBlock(var1, BASE_LIGHT, 27, 21, 30, var3);
            this.placeBlock(var1, LAMP_BLOCK, 27, 20, 30, var3);
            this.placeBlock(var1, BASE_LIGHT, 31, 20, 31, var3);
            this.placeBlock(var1, BASE_LIGHT, 30, 21, 30, var3);
            this.placeBlock(var1, LAMP_BLOCK, 30, 20, 30, var3);
            this.placeBlock(var1, BASE_LIGHT, 31, 20, 26, var3);
            this.placeBlock(var1, BASE_LIGHT, 30, 21, 27, var3);
            this.placeBlock(var1, LAMP_BLOCK, 30, 20, 27, var3);
            this.generateBox(var1, var3, 28, 21, 27, 29, 21, 27, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 27, 21, 28, 27, 21, 29, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 28, 21, 30, 29, 21, 30, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 30, 21, 28, 30, 21, 29, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateLowerWall(WorldGenLevel var1, Random var2, BoundingBox var3) {
         int var4;
         if (this.chunkIntersects(var3, 0, 21, 6, 58)) {
            this.generateBox(var1, var3, 0, 0, 21, 6, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 0, 1, 21, 6, 7, 57);
            this.generateBox(var1, var3, 4, 4, 21, 6, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, var4, var4 + 1, 21, var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(var4 = 23; var4 < 53; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, 5, 5, var4, var3);
            }

            this.placeBlock(var1, DOT_DECO_DATA, 5, 5, 52, var3);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, var4, var4 + 1, 21, var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            this.generateBox(var1, var3, 4, 1, 52, 6, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 5, 1, 51, 5, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if (this.chunkIntersects(var3, 51, 21, 58, 58)) {
            this.generateBox(var1, var3, 51, 0, 21, 57, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 51, 1, 21, 57, 7, 57);
            this.generateBox(var1, var3, 51, 4, 21, 53, 4, 53, BASE_GRAY, BASE_GRAY, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, 57 - var4, var4 + 1, 21, 57 - var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(var4 = 23; var4 < 53; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, 52, 5, var4, var3);
            }

            this.placeBlock(var1, DOT_DECO_DATA, 52, 5, 52, var3);
            this.generateBox(var1, var3, 51, 1, 52, 53, 3, 52, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 52, 1, 51, 52, 3, 53, BASE_GRAY, BASE_GRAY, false);
         }

         if (this.chunkIntersects(var3, 0, 51, 57, 57)) {
            this.generateBox(var1, var3, 7, 0, 51, 50, 0, 57, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 7, 1, 51, 50, 10, 57);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, var4 + 1, var4 + 1, 57 - var4, 56 - var4, var4 + 1, 57 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }
         }

      }

      private void generateMiddleWall(WorldGenLevel var1, Random var2, BoundingBox var3) {
         int var4;
         if (this.chunkIntersects(var3, 7, 21, 13, 50)) {
            this.generateBox(var1, var3, 7, 0, 21, 13, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 7, 1, 21, 13, 10, 50);
            this.generateBox(var1, var3, 11, 8, 21, 13, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, var4 + 7, var4 + 5, 21, var4 + 7, var4 + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(var4 = 21; var4 <= 45; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, 12, 9, var4, var3);
            }
         }

         if (this.chunkIntersects(var3, 44, 21, 50, 54)) {
            this.generateBox(var1, var3, 44, 0, 21, 50, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 44, 1, 21, 50, 10, 50);
            this.generateBox(var1, var3, 44, 8, 21, 46, 8, 53, BASE_GRAY, BASE_GRAY, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, 50 - var4, var4 + 5, 21, 50 - var4, var4 + 5, 54, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(var4 = 21; var4 <= 45; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, 45, 9, var4, var3);
            }
         }

         if (this.chunkIntersects(var3, 8, 44, 49, 54)) {
            this.generateBox(var1, var3, 14, 0, 44, 43, 0, 50, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 14, 1, 44, 43, 10, 50);

            for(var4 = 12; var4 <= 45; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, var4, 9, 45, var3);
               this.placeBlock(var1, DOT_DECO_DATA, var4, 9, 52, var3);
               if (var4 == 12 || var4 == 18 || var4 == 24 || var4 == 33 || var4 == 39 || var4 == 45) {
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 9, 47, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 9, 50, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 10, 45, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 10, 46, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 10, 51, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 10, 52, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 11, 47, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 11, 50, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 12, 48, var3);
                  this.placeBlock(var1, DOT_DECO_DATA, var4, 12, 49, var3);
               }
            }

            for(var4 = 0; var4 < 3; ++var4) {
               this.generateBox(var1, var3, 8 + var4, 5 + var4, 54, 49 - var4, 5 + var4, 54, BASE_GRAY, BASE_GRAY, false);
            }

            this.generateBox(var1, var3, 11, 8, 54, 46, 8, 54, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var3, 14, 8, 44, 43, 8, 53, BASE_GRAY, BASE_GRAY, false);
         }

      }

      private void generateUpperWall(WorldGenLevel var1, Random var2, BoundingBox var3) {
         int var4;
         if (this.chunkIntersects(var3, 14, 21, 20, 43)) {
            this.generateBox(var1, var3, 14, 0, 21, 20, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 14, 1, 22, 20, 14, 43);
            this.generateBox(var1, var3, 18, 12, 22, 20, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 18, 12, 21, 20, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, var4 + 14, var4 + 9, 21, var4 + 14, var4 + 9, 43 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(var4 = 23; var4 <= 39; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, 19, 13, var4, var3);
            }
         }

         if (this.chunkIntersects(var3, 37, 21, 43, 43)) {
            this.generateBox(var1, var3, 37, 0, 21, 43, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 37, 1, 22, 43, 14, 43);
            this.generateBox(var1, var3, 37, 12, 22, 39, 12, 39, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var3, 37, 12, 21, 39, 12, 21, BASE_LIGHT, BASE_LIGHT, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, 43 - var4, var4 + 9, 21, 43 - var4, var4 + 9, 43 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(var4 = 23; var4 <= 39; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, 38, 13, var4, var3);
            }
         }

         if (this.chunkIntersects(var3, 15, 37, 42, 43)) {
            this.generateBox(var1, var3, 21, 0, 37, 36, 0, 43, BASE_GRAY, BASE_GRAY, false);
            this.generateWaterBox(var1, var3, 21, 1, 37, 36, 14, 43);
            this.generateBox(var1, var3, 21, 12, 37, 36, 12, 39, BASE_GRAY, BASE_GRAY, false);

            for(var4 = 0; var4 < 4; ++var4) {
               this.generateBox(var1, var3, 15 + var4, var4 + 9, 43 - var4, 42 - var4, var4 + 9, 43 - var4, BASE_LIGHT, BASE_LIGHT, false);
            }

            for(var4 = 21; var4 <= 36; var4 += 3) {
               this.placeBlock(var1, DOT_DECO_DATA, var4, 13, 38, var3);
            }
         }

      }
   }

   public abstract static class OceanMonumentPiece extends StructurePiece {
      protected static final BlockState BASE_GRAY;
      protected static final BlockState BASE_LIGHT;
      protected static final BlockState BASE_BLACK;
      protected static final BlockState DOT_DECO_DATA;
      protected static final BlockState LAMP_BLOCK;
      protected static final BlockState FILL_BLOCK;
      protected static final Set<Block> FILL_KEEP;
      protected static final int GRIDROOM_SOURCE_INDEX;
      protected static final int GRIDROOM_TOP_CONNECT_INDEX;
      protected static final int GRIDROOM_LEFTWING_CONNECT_INDEX;
      protected static final int GRIDROOM_RIGHTWING_CONNECT_INDEX;
      protected OceanMonumentPieces.RoomDefinition roomDefinition;

      protected static final int getRoomIndex(int var0, int var1, int var2) {
         return var1 * 25 + var2 * 5 + var0;
      }

      public OceanMonumentPiece(StructurePieceType var1, int var2) {
         super(var1, var2);
      }

      public OceanMonumentPiece(StructurePieceType var1, Direction var2, BoundingBox var3) {
         super(var1, 1);
         this.setOrientation(var2);
         this.boundingBox = var3;
      }

      protected OceanMonumentPiece(StructurePieceType var1, int var2, Direction var3, OceanMonumentPieces.RoomDefinition var4, int var5, int var6, int var7) {
         super(var1, var2);
         this.setOrientation(var3);
         this.roomDefinition = var4;
         int var8 = var4.index;
         int var9 = var8 % 5;
         int var10 = var8 / 5 % 5;
         int var11 = var8 / 25;
         if (var3 != Direction.NORTH && var3 != Direction.SOUTH) {
            this.boundingBox = new BoundingBox(0, 0, 0, var7 * 8 - 1, var6 * 4 - 1, var5 * 8 - 1);
         } else {
            this.boundingBox = new BoundingBox(0, 0, 0, var5 * 8 - 1, var6 * 4 - 1, var7 * 8 - 1);
         }

         switch(var3) {
         case NORTH:
            this.boundingBox.move(var9 * 8, var11 * 4, -(var10 + var7) * 8 + 1);
            break;
         case SOUTH:
            this.boundingBox.move(var9 * 8, var11 * 4, var10 * 8);
            break;
         case WEST:
            this.boundingBox.move(-(var10 + var7) * 8 + 1, var11 * 4, var9 * 8);
            break;
         default:
            this.boundingBox.move(var10 * 8, var11 * 4, var9 * 8);
         }

      }

      public OceanMonumentPiece(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
      }

      protected void generateWaterBox(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8) {
         for(int var9 = var4; var9 <= var7; ++var9) {
            for(int var10 = var3; var10 <= var6; ++var10) {
               for(int var11 = var5; var11 <= var8; ++var11) {
                  BlockState var12 = this.getBlock(var1, var10, var9, var11, var2);
                  if (!FILL_KEEP.contains(var12.getBlock())) {
                     if (this.getWorldY(var9) >= var1.getSeaLevel() && var12 != FILL_BLOCK) {
                        this.placeBlock(var1, Blocks.AIR.defaultBlockState(), var10, var9, var11, var2);
                     } else {
                        this.placeBlock(var1, FILL_BLOCK, var10, var9, var11, var2);
                     }
                  }
               }
            }
         }

      }

      protected void generateDefaultFloor(WorldGenLevel var1, BoundingBox var2, int var3, int var4, boolean var5) {
         if (var5) {
            this.generateBox(var1, var2, var3 + 0, 0, var4 + 0, var3 + 2, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var2, var3 + 5, 0, var4 + 0, var3 + 8 - 1, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var2, var3 + 3, 0, var4 + 0, var3 + 4, 0, var4 + 2, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var2, var3 + 3, 0, var4 + 5, var3 + 4, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
            this.generateBox(var1, var2, var3 + 3, 0, var4 + 2, var3 + 4, 0, var4 + 2, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var2, var3 + 3, 0, var4 + 5, var3 + 4, 0, var4 + 5, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var2, var3 + 2, 0, var4 + 3, var3 + 2, 0, var4 + 4, BASE_LIGHT, BASE_LIGHT, false);
            this.generateBox(var1, var2, var3 + 5, 0, var4 + 3, var3 + 5, 0, var4 + 4, BASE_LIGHT, BASE_LIGHT, false);
         } else {
            this.generateBox(var1, var2, var3 + 0, 0, var4 + 0, var3 + 8 - 1, 0, var4 + 8 - 1, BASE_GRAY, BASE_GRAY, false);
         }

      }

      protected void generateBoxOnFillOnly(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, int var8, BlockState var9) {
         for(int var10 = var4; var10 <= var7; ++var10) {
            for(int var11 = var3; var11 <= var6; ++var11) {
               for(int var12 = var5; var12 <= var8; ++var12) {
                  if (this.getBlock(var1, var11, var10, var12, var2) == FILL_BLOCK) {
                     this.placeBlock(var1, var9, var11, var10, var12, var2);
                  }
               }
            }
         }

      }

      protected boolean chunkIntersects(BoundingBox var1, int var2, int var3, int var4, int var5) {
         int var6 = this.getWorldX(var2, var3);
         int var7 = this.getWorldZ(var2, var3);
         int var8 = this.getWorldX(var4, var5);
         int var9 = this.getWorldZ(var4, var5);
         return var1.intersects(Math.min(var6, var8), Math.min(var7, var9), Math.max(var6, var8), Math.max(var7, var9));
      }

      protected boolean spawnElder(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5) {
         int var6 = this.getWorldX(var3, var5);
         int var7 = this.getWorldY(var4);
         int var8 = this.getWorldZ(var3, var5);
         if (var2.isInside(new BlockPos(var6, var7, var8))) {
            ElderGuardian var9 = (ElderGuardian)EntityType.ELDER_GUARDIAN.create(var1.getLevel());
            var9.heal(var9.getMaxHealth());
            var9.moveTo((double)var6 + 0.5D, (double)var7, (double)var8 + 0.5D, 0.0F, 0.0F);
            var9.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var9.blockPosition()), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            var1.addFreshEntityWithPassengers(var9);
            return true;
         } else {
            return false;
         }
      }

      static {
         BASE_GRAY = Blocks.PRISMARINE.defaultBlockState();
         BASE_LIGHT = Blocks.PRISMARINE_BRICKS.defaultBlockState();
         BASE_BLACK = Blocks.DARK_PRISMARINE.defaultBlockState();
         DOT_DECO_DATA = BASE_LIGHT;
         LAMP_BLOCK = Blocks.SEA_LANTERN.defaultBlockState();
         FILL_BLOCK = Blocks.WATER.defaultBlockState();
         FILL_KEEP = ImmutableSet.builder().add(Blocks.ICE).add(Blocks.PACKED_ICE).add(Blocks.BLUE_ICE).add(FILL_BLOCK.getBlock()).build();
         GRIDROOM_SOURCE_INDEX = getRoomIndex(2, 0, 0);
         GRIDROOM_TOP_CONNECT_INDEX = getRoomIndex(2, 2, 0);
         GRIDROOM_LEFTWING_CONNECT_INDEX = getRoomIndex(0, 1, 0);
         GRIDROOM_RIGHTWING_CONNECT_INDEX = getRoomIndex(4, 1, 0);
      }
   }
}
