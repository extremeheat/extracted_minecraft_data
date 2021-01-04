package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.levelgen.feature.MineshaftFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class MineShaftPieces {
   private static MineShaftPieces.MineShaftPiece createRandomShaftPiece(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, @Nullable Direction var5, int var6, MineshaftFeature.Type var7) {
      int var8 = var1.nextInt(100);
      BoundingBox var9;
      if (var8 >= 80) {
         var9 = MineShaftPieces.MineShaftCrossing.findCrossing(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new MineShaftPieces.MineShaftCrossing(var6, var9, var5, var7);
         }
      } else if (var8 >= 70) {
         var9 = MineShaftPieces.MineShaftStairs.findStairs(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new MineShaftPieces.MineShaftStairs(var6, var9, var5, var7);
         }
      } else {
         var9 = MineShaftPieces.MineShaftCorridor.findCorridorSize(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new MineShaftPieces.MineShaftCorridor(var6, var1, var9, var5, var7);
         }
      }

      return null;
   }

   private static MineShaftPieces.MineShaftPiece generateAndAddPiece(StructurePiece var0, List<StructurePiece> var1, Random var2, int var3, int var4, int var5, Direction var6, int var7) {
      if (var7 > 8) {
         return null;
      } else if (Math.abs(var3 - var0.getBoundingBox().x0) <= 80 && Math.abs(var5 - var0.getBoundingBox().z0) <= 80) {
         MineshaftFeature.Type var8 = ((MineShaftPieces.MineShaftPiece)var0).type;
         MineShaftPieces.MineShaftPiece var9 = createRandomShaftPiece(var1, var2, var3, var4, var5, var6, var7 + 1, var8);
         if (var9 != null) {
            var1.add(var9);
            var9.addChildren(var0, var1, var2);
         }

         return var9;
      } else {
         return null;
      }
   }

   public static class MineShaftStairs extends MineShaftPieces.MineShaftPiece {
      public MineShaftStairs(int var1, BoundingBox var2, Direction var3, MineshaftFeature.Type var4) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, var1, var4);
         this.setOrientation(var3);
         this.boundingBox = var2;
      }

      public MineShaftStairs(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, var2);
      }

      public static BoundingBox findStairs(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, Direction var5) {
         BoundingBox var6 = new BoundingBox(var2, var3 - 5, var4, var2, var3 + 3 - 1, var4);
         switch(var5) {
         case NORTH:
         default:
            var6.x1 = var2 + 3 - 1;
            var6.z0 = var4 - 8;
            break;
         case SOUTH:
            var6.x1 = var2 + 3 - 1;
            var6.z1 = var4 + 8;
            break;
         case WEST:
            var6.x0 = var2 - 8;
            var6.z1 = var4 + 3 - 1;
            break;
         case EAST:
            var6.x1 = var2 + 8;
            var6.z1 = var4 + 3 - 1;
         }

         return StructurePiece.findCollisionPiece(var0, var6) != null ? null : var6;
      }

      public void addChildren(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.getGenDepth();
         Direction var5 = this.getOrientation();
         if (var5 != null) {
            switch(var5) {
            case NORTH:
            default:
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
               break;
            case SOUTH:
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
               break;
            case WEST:
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0, Direction.WEST, var4);
               break;
            case EAST:
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0, Direction.EAST, var4);
            }
         }

      }

      public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         if (this.edgesLiquid(var1, var3)) {
            return false;
         } else {
            this.generateBox(var1, var3, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(var1, var3, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);

            for(int var5 = 0; var5 < 5; ++var5) {
               this.generateBox(var1, var3, 0, 5 - var5 - (var5 < 4 ? 1 : 0), 2 + var5, 2, 7 - var5, 2 + var5, CAVE_AIR, CAVE_AIR, false);
            }

            return true;
         }
      }
   }

   public static class MineShaftCrossing extends MineShaftPieces.MineShaftPiece {
      private final Direction direction;
      private final boolean isTwoFloored;

      public MineShaftCrossing(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, var2);
         this.isTwoFloored = var2.getBoolean("tf");
         this.direction = Direction.from2DDataValue(var2.getInt("D"));
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("tf", this.isTwoFloored);
         var1.putInt("D", this.direction.get2DDataValue());
      }

      public MineShaftCrossing(int var1, BoundingBox var2, @Nullable Direction var3, MineshaftFeature.Type var4) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, var1, var4);
         this.direction = var3;
         this.boundingBox = var2;
         this.isTwoFloored = var2.getYSpan() > 3;
      }

      public static BoundingBox findCrossing(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, Direction var5) {
         BoundingBox var6 = new BoundingBox(var2, var3, var4, var2, var3 + 3 - 1, var4);
         if (var1.nextInt(4) == 0) {
            var6.y1 += 4;
         }

         switch(var5) {
         case NORTH:
         default:
            var6.x0 = var2 - 1;
            var6.x1 = var2 + 3;
            var6.z0 = var4 - 4;
            break;
         case SOUTH:
            var6.x0 = var2 - 1;
            var6.x1 = var2 + 3;
            var6.z1 = var4 + 3 + 1;
            break;
         case WEST:
            var6.x0 = var2 - 4;
            var6.z0 = var4 - 1;
            var6.z1 = var4 + 3;
            break;
         case EAST:
            var6.x1 = var2 + 3 + 1;
            var6.z0 = var4 - 1;
            var6.z1 = var4 + 3;
         }

         return StructurePiece.findCollisionPiece(var0, var6) != null ? null : var6;
      }

      public void addChildren(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.getGenDepth();
         switch(this.direction) {
         case NORTH:
         default:
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, var4);
            break;
         case SOUTH:
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, var4);
            break;
         case WEST:
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.WEST, var4);
            break;
         case EAST:
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, Direction.EAST, var4);
         }

         if (this.isTwoFloored) {
            if (var3.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            }

            if (var3.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.WEST, var4);
            }

            if (var3.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z0 + 1, Direction.EAST, var4);
            }

            if (var3.nextBoolean()) {
               MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3 + 1, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            }
         }

      }

      public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         if (this.edgesLiquid(var1, var3)) {
            return false;
         } else {
            BlockState var5 = this.getPlanksBlock();
            if (this.isTwoFloored) {
               this.generateBox(var1, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(var1, var3, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y0 + 3 - 1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(var1, var3, this.boundingBox.x0 + 1, this.boundingBox.y1 - 2, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(var1, var3, this.boundingBox.x0, this.boundingBox.y1 - 2, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(var1, var3, this.boundingBox.x0 + 1, this.boundingBox.y0 + 3, this.boundingBox.z0 + 1, this.boundingBox.x1 - 1, this.boundingBox.y0 + 3, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            } else {
               this.generateBox(var1, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1 - 1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
               this.generateBox(var1, var3, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1 - 1, CAVE_AIR, CAVE_AIR, false);
            }

            this.placeSupportPillar(var1, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(var1, var3, this.boundingBox.x0 + 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);
            this.placeSupportPillar(var1, var3, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z0 + 1, this.boundingBox.y1);
            this.placeSupportPillar(var1, var3, this.boundingBox.x1 - 1, this.boundingBox.y0, this.boundingBox.z1 - 1, this.boundingBox.y1);

            for(int var6 = this.boundingBox.x0; var6 <= this.boundingBox.x1; ++var6) {
               for(int var7 = this.boundingBox.z0; var7 <= this.boundingBox.z1; ++var7) {
                  if (this.getBlock(var1, var6, this.boundingBox.y0 - 1, var7, var3).isAir() && this.isInterior(var1, var6, this.boundingBox.y0 - 1, var7, var3)) {
                     this.placeBlock(var1, var5, var6, this.boundingBox.y0 - 1, var7, var3);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupportPillar(LevelAccessor var1, BoundingBox var2, int var3, int var4, int var5, int var6) {
         if (!this.getBlock(var1, var3, var6 + 1, var5, var2).isAir()) {
            this.generateBox(var1, var2, var3, var4, var5, var3, var6, var5, this.getPlanksBlock(), CAVE_AIR, false);
         }

      }
   }

   public static class MineShaftCorridor extends MineShaftPieces.MineShaftPiece {
      private final boolean hasRails;
      private final boolean spiderCorridor;
      private boolean hasPlacedSpider;
      private final int numSections;

      public MineShaftCorridor(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, var2);
         this.hasRails = var2.getBoolean("hr");
         this.spiderCorridor = var2.getBoolean("sc");
         this.hasPlacedSpider = var2.getBoolean("hps");
         this.numSections = var2.getInt("Num");
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         var1.putBoolean("hr", this.hasRails);
         var1.putBoolean("sc", this.spiderCorridor);
         var1.putBoolean("hps", this.hasPlacedSpider);
         var1.putInt("Num", this.numSections);
      }

      public MineShaftCorridor(int var1, Random var2, BoundingBox var3, Direction var4, MineshaftFeature.Type var5) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, var1, var5);
         this.setOrientation(var4);
         this.boundingBox = var3;
         this.hasRails = var2.nextInt(3) == 0;
         this.spiderCorridor = !this.hasRails && var2.nextInt(23) == 0;
         if (this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.numSections = var3.getZSpan() / 5;
         } else {
            this.numSections = var3.getXSpan() / 5;
         }

      }

      public static BoundingBox findCorridorSize(List<StructurePiece> var0, Random var1, int var2, int var3, int var4, Direction var5) {
         BoundingBox var6 = new BoundingBox(var2, var3, var4, var2, var3 + 3 - 1, var4);

         int var7;
         for(var7 = var1.nextInt(3) + 2; var7 > 0; --var7) {
            int var8 = var7 * 5;
            switch(var5) {
            case NORTH:
            default:
               var6.x1 = var2 + 3 - 1;
               var6.z0 = var4 - (var8 - 1);
               break;
            case SOUTH:
               var6.x1 = var2 + 3 - 1;
               var6.z1 = var4 + var8 - 1;
               break;
            case WEST:
               var6.x0 = var2 - (var8 - 1);
               var6.z1 = var4 + 3 - 1;
               break;
            case EAST:
               var6.x1 = var2 + var8 - 1;
               var6.z1 = var4 + 3 - 1;
            }

            if (StructurePiece.findCollisionPiece(var0, var6) == null) {
               break;
            }
         }

         return var7 > 0 ? var6 : null;
      }

      public void addChildren(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.getGenDepth();
         int var5 = var3.nextInt(4);
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch(var6) {
            case NORTH:
            default:
               if (var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z0 - 1, var6, var4);
               } else if (var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z0, Direction.WEST, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z0, Direction.EAST, var4);
               }
               break;
            case SOUTH:
               if (var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z1 + 1, var6, var4);
               } else if (var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z1 - 3, Direction.WEST, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z1 - 3, Direction.EAST, var4);
               }
               break;
            case WEST:
               if (var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z0, var6, var4);
               } else if (var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, var4);
               }
               break;
            case EAST:
               if (var5 <= 1) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z0, var6, var4);
               } else if (var5 == 2) {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z0 - 1, Direction.NORTH, var4);
               } else {
                  MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 - 3, this.boundingBox.y0 - 1 + var3.nextInt(3), this.boundingBox.z1 + 1, Direction.SOUTH, var4);
               }
            }
         }

         if (var4 < 8) {
            int var7;
            int var8;
            if (var6 != Direction.NORTH && var6 != Direction.SOUTH) {
               for(var7 = this.boundingBox.x0 + 3; var7 + 3 <= this.boundingBox.x1; var7 += 5) {
                  var8 = var3.nextInt(5);
                  if (var8 == 0) {
                     MineShaftPieces.generateAndAddPiece(var1, var2, var3, var7, this.boundingBox.y0, this.boundingBox.z0 - 1, Direction.NORTH, var4 + 1);
                  } else if (var8 == 1) {
                     MineShaftPieces.generateAndAddPiece(var1, var2, var3, var7, this.boundingBox.y0, this.boundingBox.z1 + 1, Direction.SOUTH, var4 + 1);
                  }
               }
            } else {
               for(var7 = this.boundingBox.z0 + 3; var7 + 3 <= this.boundingBox.z1; var7 += 5) {
                  var8 = var3.nextInt(5);
                  if (var8 == 0) {
                     MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0, var7, Direction.WEST, var4 + 1);
                  } else if (var8 == 1) {
                     MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0, var7, Direction.EAST, var4 + 1);
                  }
               }
            }
         }

      }

      protected boolean createChest(LevelAccessor var1, BoundingBox var2, Random var3, int var4, int var5, int var6, ResourceLocation var7) {
         BlockPos var8 = new BlockPos(this.getWorldX(var4, var6), this.getWorldY(var5), this.getWorldZ(var4, var6));
         if (var2.isInside(var8) && var1.getBlockState(var8).isAir() && !var1.getBlockState(var8.below()).isAir()) {
            BlockState var9 = (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, var3.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.placeBlock(var1, var9, var4, var5, var6, var2);
            MinecartChest var10 = new MinecartChest(var1.getLevel(), (double)((float)var8.getX() + 0.5F), (double)((float)var8.getY() + 0.5F), (double)((float)var8.getZ() + 0.5F));
            var10.setLootTable(var7, var3.nextLong());
            var1.addFreshEntity(var10);
            return true;
         } else {
            return false;
         }
      }

      public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         if (this.edgesLiquid(var1, var3)) {
            return false;
         } else {
            boolean var5 = false;
            boolean var6 = true;
            boolean var7 = false;
            boolean var8 = true;
            int var9 = this.numSections * 5 - 1;
            BlockState var10 = this.getPlanksBlock();
            this.generateBox(var1, var3, 0, 0, 0, 2, 1, var9, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(var1, var3, var2, 0.8F, 0, 2, 0, 2, 2, var9, CAVE_AIR, CAVE_AIR, false, false);
            if (this.spiderCorridor) {
               this.generateMaybeBox(var1, var3, var2, 0.6F, 0, 0, 0, 2, 1, var9, Blocks.COBWEB.defaultBlockState(), CAVE_AIR, false, true);
            }

            int var11;
            int var12;
            for(var11 = 0; var11 < this.numSections; ++var11) {
               var12 = 2 + var11 * 5;
               this.placeSupport(var1, var3, 0, 0, var12, 2, 2, var2);
               this.placeCobWeb(var1, var3, var2, 0.1F, 0, 2, var12 - 1);
               this.placeCobWeb(var1, var3, var2, 0.1F, 2, 2, var12 - 1);
               this.placeCobWeb(var1, var3, var2, 0.1F, 0, 2, var12 + 1);
               this.placeCobWeb(var1, var3, var2, 0.1F, 2, 2, var12 + 1);
               this.placeCobWeb(var1, var3, var2, 0.05F, 0, 2, var12 - 2);
               this.placeCobWeb(var1, var3, var2, 0.05F, 2, 2, var12 - 2);
               this.placeCobWeb(var1, var3, var2, 0.05F, 0, 2, var12 + 2);
               this.placeCobWeb(var1, var3, var2, 0.05F, 2, 2, var12 + 2);
               if (var2.nextInt(100) == 0) {
                  this.createChest(var1, var3, var2, 2, 0, var12 - 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if (var2.nextInt(100) == 0) {
                  this.createChest(var1, var3, var2, 0, 0, var12 + 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if (this.spiderCorridor && !this.hasPlacedSpider) {
                  int var13 = this.getWorldY(0);
                  int var14 = var12 - 1 + var2.nextInt(3);
                  int var15 = this.getWorldX(1, var14);
                  int var16 = this.getWorldZ(1, var14);
                  BlockPos var17 = new BlockPos(var15, var13, var16);
                  if (var3.isInside(var17) && this.isInterior(var1, 1, 0, var14, var3)) {
                     this.hasPlacedSpider = true;
                     var1.setBlock(var17, Blocks.SPAWNER.defaultBlockState(), 2);
                     BlockEntity var18 = var1.getBlockEntity(var17);
                     if (var18 instanceof SpawnerBlockEntity) {
                        ((SpawnerBlockEntity)var18).getSpawner().setEntityId(EntityType.CAVE_SPIDER);
                     }
                  }
               }
            }

            for(var11 = 0; var11 <= 2; ++var11) {
               for(var12 = 0; var12 <= var9; ++var12) {
                  boolean var20 = true;
                  BlockState var22 = this.getBlock(var1, var11, -1, var12, var3);
                  if (var22.isAir() && this.isInterior(var1, var11, -1, var12, var3)) {
                     boolean var24 = true;
                     this.placeBlock(var1, var10, var11, -1, var12, var3);
                  }
               }
            }

            if (this.hasRails) {
               BlockState var19 = (BlockState)Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

               for(var12 = 0; var12 <= var9; ++var12) {
                  BlockState var21 = this.getBlock(var1, 1, -1, var12, var3);
                  if (!var21.isAir() && var21.isSolidRender(var1, new BlockPos(this.getWorldX(1, var12), this.getWorldY(-1), this.getWorldZ(1, var12)))) {
                     float var23 = this.isInterior(var1, 1, 0, var12, var3) ? 0.7F : 0.9F;
                     this.maybeGenerateBlock(var1, var3, var2, var23, 1, 0, var12, var19);
                  }
               }
            }

            return true;
         }
      }

      private void placeSupport(LevelAccessor var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, Random var8) {
         if (this.isSupportingBox(var1, var2, var3, var7, var6, var5)) {
            BlockState var9 = this.getPlanksBlock();
            BlockState var10 = this.getFenceBlock();
            this.generateBox(var1, var2, var3, var4, var5, var3, var6 - 1, var5, (BlockState)var10.setValue(FenceBlock.WEST, true), CAVE_AIR, false);
            this.generateBox(var1, var2, var7, var4, var5, var7, var6 - 1, var5, (BlockState)var10.setValue(FenceBlock.EAST, true), CAVE_AIR, false);
            if (var8.nextInt(4) == 0) {
               this.generateBox(var1, var2, var3, var6, var5, var3, var6, var5, var9, CAVE_AIR, false);
               this.generateBox(var1, var2, var7, var6, var5, var7, var6, var5, var9, CAVE_AIR, false);
            } else {
               this.generateBox(var1, var2, var3, var6, var5, var7, var6, var5, var9, CAVE_AIR, false);
               this.maybeGenerateBlock(var1, var2, var8, 0.05F, var3 + 1, var6, var5 - 1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH));
               this.maybeGenerateBlock(var1, var2, var8, 0.05F, var3 + 1, var6, var5 + 1, (BlockState)Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH));
            }

         }
      }

      private void placeCobWeb(LevelAccessor var1, BoundingBox var2, Random var3, float var4, int var5, int var6, int var7) {
         if (this.isInterior(var1, var5, var6, var7, var2)) {
            this.maybeGenerateBlock(var1, var2, var3, var4, var5, var6, var7, Blocks.COBWEB.defaultBlockState());
         }

      }
   }

   public static class MineShaftRoom extends MineShaftPieces.MineShaftPiece {
      private final List<BoundingBox> childEntranceBoxes = Lists.newLinkedList();

      public MineShaftRoom(int var1, Random var2, int var3, int var4, MineshaftFeature.Type var5) {
         super(StructurePieceType.MINE_SHAFT_ROOM, var1, var5);
         this.type = var5;
         this.boundingBox = new BoundingBox(var3, 50, var4, var3 + 7 + var2.nextInt(6), 54 + var2.nextInt(6), var4 + 7 + var2.nextInt(6));
      }

      public MineShaftRoom(StructureManager var1, CompoundTag var2) {
         super(StructurePieceType.MINE_SHAFT_ROOM, var2);
         ListTag var3 = var2.getList("Entrances", 11);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            this.childEntranceBoxes.add(new BoundingBox(var3.getIntArray(var4)));
         }

      }

      public void addChildren(StructurePiece var1, List<StructurePiece> var2, Random var3) {
         int var4 = this.getGenDepth();
         int var6 = this.boundingBox.getYSpan() - 3 - 1;
         if (var6 <= 0) {
            var6 = 1;
         }

         int var5;
         MineShaftPieces.MineShaftPiece var7;
         BoundingBox var8;
         for(var5 = 0; var5 < this.boundingBox.getXSpan(); var5 += 4) {
            var5 += var3.nextInt(this.boundingBox.getXSpan());
            if (var5 + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            var7 = MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var5, this.boundingBox.y0 + var3.nextInt(var6) + 1, this.boundingBox.z0 - 1, Direction.NORTH, var4);
            if (var7 != null) {
               var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(var8.x0, var8.y0, this.boundingBox.z0, var8.x1, var8.y1, this.boundingBox.z0 + 1));
            }
         }

         for(var5 = 0; var5 < this.boundingBox.getXSpan(); var5 += 4) {
            var5 += var3.nextInt(this.boundingBox.getXSpan());
            if (var5 + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            var7 = MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 + var5, this.boundingBox.y0 + var3.nextInt(var6) + 1, this.boundingBox.z1 + 1, Direction.SOUTH, var4);
            if (var7 != null) {
               var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(var8.x0, var8.y0, this.boundingBox.z1 - 1, var8.x1, var8.y1, this.boundingBox.z1));
            }
         }

         for(var5 = 0; var5 < this.boundingBox.getZSpan(); var5 += 4) {
            var5 += var3.nextInt(this.boundingBox.getZSpan());
            if (var5 + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            var7 = MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x0 - 1, this.boundingBox.y0 + var3.nextInt(var6) + 1, this.boundingBox.z0 + var5, Direction.WEST, var4);
            if (var7 != null) {
               var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x0, var8.y0, var8.z0, this.boundingBox.x0 + 1, var8.y1, var8.z1));
            }
         }

         for(var5 = 0; var5 < this.boundingBox.getZSpan(); var5 += 4) {
            var5 += var3.nextInt(this.boundingBox.getZSpan());
            if (var5 + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            var7 = MineShaftPieces.generateAndAddPiece(var1, var2, var3, this.boundingBox.x1 + 1, this.boundingBox.y0 + var3.nextInt(var6) + 1, this.boundingBox.z0 + var5, Direction.EAST, var4);
            if (var7 != null) {
               var8 = var7.getBoundingBox();
               this.childEntranceBoxes.add(new BoundingBox(this.boundingBox.x1 - 1, var8.y0, var8.z0, this.boundingBox.x1, var8.y1, var8.z1));
            }
         }

      }

      public boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4) {
         if (this.edgesLiquid(var1, var3)) {
            return false;
         } else {
            this.generateBox(var1, var3, this.boundingBox.x0, this.boundingBox.y0, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y0, this.boundingBox.z1, Blocks.DIRT.defaultBlockState(), CAVE_AIR, true);
            this.generateBox(var1, var3, this.boundingBox.x0, this.boundingBox.y0 + 1, this.boundingBox.z0, this.boundingBox.x1, Math.min(this.boundingBox.y0 + 3, this.boundingBox.y1), this.boundingBox.z1, CAVE_AIR, CAVE_AIR, false);
            Iterator var5 = this.childEntranceBoxes.iterator();

            while(var5.hasNext()) {
               BoundingBox var6 = (BoundingBox)var5.next();
               this.generateBox(var1, var3, var6.x0, var6.y1 - 2, var6.z0, var6.x1, var6.y1, var6.z1, CAVE_AIR, CAVE_AIR, false);
            }

            this.generateUpperHalfSphere(var1, var3, this.boundingBox.x0, this.boundingBox.y0 + 4, this.boundingBox.z0, this.boundingBox.x1, this.boundingBox.y1, this.boundingBox.z1, CAVE_AIR, false);
            return true;
         }
      }

      public void move(int var1, int var2, int var3) {
         super.move(var1, var2, var3);
         Iterator var4 = this.childEntranceBoxes.iterator();

         while(var4.hasNext()) {
            BoundingBox var5 = (BoundingBox)var4.next();
            var5.move(var1, var2, var3);
         }

      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         super.addAdditionalSaveData(var1);
         ListTag var2 = new ListTag();
         Iterator var3 = this.childEntranceBoxes.iterator();

         while(var3.hasNext()) {
            BoundingBox var4 = (BoundingBox)var3.next();
            var2.add(var4.createTag());
         }

         var1.put("Entrances", var2);
      }
   }

   abstract static class MineShaftPiece extends StructurePiece {
      protected MineshaftFeature.Type type;

      public MineShaftPiece(StructurePieceType var1, int var2, MineshaftFeature.Type var3) {
         super(var1, var2);
         this.type = var3;
      }

      public MineShaftPiece(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
         this.type = MineshaftFeature.Type.byId(var2.getInt("MST"));
      }

      protected void addAdditionalSaveData(CompoundTag var1) {
         var1.putInt("MST", this.type.ordinal());
      }

      protected BlockState getPlanksBlock() {
         switch(this.type) {
         case NORMAL:
         default:
            return Blocks.OAK_PLANKS.defaultBlockState();
         case MESA:
            return Blocks.DARK_OAK_PLANKS.defaultBlockState();
         }
      }

      protected BlockState getFenceBlock() {
         switch(this.type) {
         case NORMAL:
         default:
            return Blocks.OAK_FENCE.defaultBlockState();
         case MESA:
            return Blocks.DARK_OAK_FENCE.defaultBlockState();
         }
      }

      protected boolean isSupportingBox(BlockGetter var1, BoundingBox var2, int var3, int var4, int var5, int var6) {
         for(int var7 = var3; var7 <= var4; ++var7) {
            if (this.getBlock(var1, var7, var5 + 1, var6, var2).isAir()) {
               return false;
            }
         }

         return true;
      }
   }
}
