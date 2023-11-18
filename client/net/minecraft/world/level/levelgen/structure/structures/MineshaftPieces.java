package net.minecraft.world.level.levelgen.structure.structures;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePieceAccessor;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import org.slf4j.Logger;

public class MineshaftPieces {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_SHAFT_WIDTH = 3;
   private static final int DEFAULT_SHAFT_HEIGHT = 3;
   private static final int DEFAULT_SHAFT_LENGTH = 5;
   private static final int MAX_PILLAR_HEIGHT = 20;
   private static final int MAX_CHAIN_HEIGHT = 50;
   private static final int MAX_DEPTH = 8;
   public static final int MAGIC_START_Y = 50;

   public MineshaftPieces() {
      super();
   }

   private static MineshaftPieces.MineShaftPiece createRandomShaftPiece(
      StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, @Nullable Direction var5, int var6, MineshaftStructure.Type var7
   ) {
      int var8 = var1.nextInt(100);
      if (var8 >= 80) {
         BoundingBox var9 = MineshaftPieces.MineShaftCrossing.findCrossing(var0, var1, var2, var3, var4, var5);
         if (var9 != null) {
            return new MineshaftPieces.MineShaftCrossing(var6, var9, var5, var7);
         }
      } else if (var8 >= 70) {
         BoundingBox var10 = MineshaftPieces.MineShaftStairs.findStairs(var0, var1, var2, var3, var4, var5);
         if (var10 != null) {
            return new MineshaftPieces.MineShaftStairs(var6, var10, var5, var7);
         }
      } else {
         BoundingBox var11 = MineshaftPieces.MineShaftCorridor.findCorridorSize(var0, var1, var2, var3, var4, var5);
         if (var11 != null) {
            return new MineshaftPieces.MineShaftCorridor(var6, var1, var11, var5, var7);
         }
      }

      return null;
   }

   static MineshaftPieces.MineShaftPiece generateAndAddPiece(
      StructurePiece var0, StructurePieceAccessor var1, RandomSource var2, int var3, int var4, int var5, Direction var6, int var7
   ) {
      if (var7 > 8) {
         return null;
      } else if (Math.abs(var3 - var0.getBoundingBox().minX()) <= 80 && Math.abs(var5 - var0.getBoundingBox().minZ()) <= 80) {
         MineshaftStructure.Type var8 = ((MineshaftPieces.MineShaftPiece)var0).type;
         MineshaftPieces.MineShaftPiece var9 = createRandomShaftPiece(var1, var2, var3, var4, var5, var6, var7 + 1, var8);
         if (var9 != null) {
            var1.addPiece(var9);
            var9.addChildren(var0, var1, var2);
         }

         return var9;
      } else {
         return null;
      }
   }

   public static class MineShaftCorridor extends MineshaftPieces.MineShaftPiece {
      private final boolean hasRails;
      private final boolean spiderCorridor;
      private boolean hasPlacedSpider;
      private final int numSections;

      public MineShaftCorridor(CompoundTag var1) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, var1);
         this.hasRails = var1.getBoolean("hr");
         this.spiderCorridor = var1.getBoolean("sc");
         this.hasPlacedSpider = var1.getBoolean("hps");
         this.numSections = var1.getInt("Num");
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("hr", this.hasRails);
         var2.putBoolean("sc", this.spiderCorridor);
         var2.putBoolean("hps", this.hasPlacedSpider);
         var2.putInt("Num", this.numSections);
      }

      public MineShaftCorridor(int var1, RandomSource var2, BoundingBox var3, Direction var4, MineshaftStructure.Type var5) {
         super(StructurePieceType.MINE_SHAFT_CORRIDOR, var1, var5, var3);
         this.setOrientation(var4);
         this.hasRails = var2.nextInt(3) == 0;
         this.spiderCorridor = !this.hasRails && var2.nextInt(23) == 0;
         if (this.getOrientation().getAxis() == Direction.Axis.Z) {
            this.numSections = var3.getZSpan() / 5;
         } else {
            this.numSections = var3.getXSpan() / 5;
         }
      }

      @Nullable
      public static BoundingBox findCorridorSize(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5) {
         for(int var6 = var1.nextInt(3) + 2; var6 > 0; --var6) {
            int var8 = var6 * 5;

            BoundingBox var7 = switch(var5) {
               default -> new BoundingBox(0, 0, -(var8 - 1), 2, 2, 0);
               case SOUTH -> new BoundingBox(0, 0, 0, 2, 2, var8 - 1);
               case WEST -> new BoundingBox(-(var8 - 1), 0, 0, 0, 2, 2);
               case EAST -> new BoundingBox(0, 0, 0, var8 - 1, 2, 2);
            };
            var7.move(var2, var3, var4);
            if (var0.findCollisionPiece(var7) == null) {
               return var7;
            }
         }

         return null;
      }

      @Override
      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         int var4 = this.getGenDepth();
         int var5 = var3.nextInt(4);
         Direction var6 = this.getOrientation();
         if (var6 != null) {
            switch(var6) {
               case NORTH:
               default:
                  if (var5 <= 1) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, this.boundingBox.minX(), this.boundingBox.minY() - 1 + var3.nextInt(3), this.boundingBox.minZ() - 1, var6, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX() - 1,
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.minZ(),
                        Direction.WEST,
                        var4
                     );
                  } else {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX() + 1,
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.minZ(),
                        Direction.EAST,
                        var4
                     );
                  }
                  break;
               case SOUTH:
                  if (var5 <= 1) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, this.boundingBox.minX(), this.boundingBox.minY() - 1 + var3.nextInt(3), this.boundingBox.maxZ() + 1, var6, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX() - 1,
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ() - 3,
                        Direction.WEST,
                        var4
                     );
                  } else {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX() + 1,
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ() - 3,
                        Direction.EAST,
                        var4
                     );
                  }
                  break;
               case WEST:
                  if (var5 <= 1) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() - 1 + var3.nextInt(3), this.boundingBox.minZ(), var6, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX(),
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.minZ() - 1,
                        Direction.NORTH,
                        var4
                     );
                  } else {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.minX(),
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ() + 1,
                        Direction.SOUTH,
                        var4
                     );
                  }
                  break;
               case EAST:
                  if (var5 <= 1) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() - 1 + var3.nextInt(3), this.boundingBox.minZ(), var6, var4
                     );
                  } else if (var5 == 2) {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX() - 3,
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.minZ() - 1,
                        Direction.NORTH,
                        var4
                     );
                  } else {
                     MineshaftPieces.generateAndAddPiece(
                        var1,
                        var2,
                        var3,
                        this.boundingBox.maxX() - 3,
                        this.boundingBox.minY() - 1 + var3.nextInt(3),
                        this.boundingBox.maxZ() + 1,
                        Direction.SOUTH,
                        var4
                     );
                  }
            }
         }

         if (var4 < 8) {
            if (var6 != Direction.NORTH && var6 != Direction.SOUTH) {
               for(int var9 = this.boundingBox.minX() + 3; var9 + 3 <= this.boundingBox.maxX(); var9 += 5) {
                  int var10 = var3.nextInt(5);
                  if (var10 == 0) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, var9, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, var4 + 1
                     );
                  } else if (var10 == 1) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, var9, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, var4 + 1
                     );
                  }
               }
            } else {
               for(int var7 = this.boundingBox.minZ() + 3; var7 + 3 <= this.boundingBox.maxZ(); var7 += 5) {
                  int var8 = var3.nextInt(5);
                  if (var8 == 0) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY(), var7, Direction.WEST, var4 + 1
                     );
                  } else if (var8 == 1) {
                     MineshaftPieces.generateAndAddPiece(
                        var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY(), var7, Direction.EAST, var4 + 1
                     );
                  }
               }
            }
         }
      }

      @Override
      protected boolean createChest(WorldGenLevel var1, BoundingBox var2, RandomSource var3, int var4, int var5, int var6, ResourceLocation var7) {
         BlockPos.MutableBlockPos var8 = this.getWorldPos(var4, var5, var6);
         if (var2.isInside(var8) && var1.getBlockState(var8).isAir() && !var1.getBlockState(var8.below()).isAir()) {
            BlockState var9 = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, var3.nextBoolean() ? RailShape.NORTH_SOUTH : RailShape.EAST_WEST);
            this.placeBlock(var1, var9, var4, var5, var6, var2);
            MinecartChest var10 = new MinecartChest(var1.getLevel(), (double)var8.getX() + 0.5, (double)var8.getY() + 0.5, (double)var8.getZ() + 0.5);
            var10.setLootTable(var7, var3.nextLong());
            var1.addFreshEntity(var10);
            return true;
         } else {
            return false;
         }
      }

      // $QF: Could not properly define all variable types!
      // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
      @Override
      public void postProcess(
         WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7
      ) {
         if (!this.isInInvalidLocation(var1, var5)) {
            boolean var8 = false;
            boolean var9 = true;
            boolean var10 = false;
            boolean var11 = true;
            int var12 = this.numSections * 5 - 1;
            BlockState var13 = this.type.getPlanksState();
            this.generateBox(var1, var5, 0, 0, 0, 2, 1, var12, CAVE_AIR, CAVE_AIR, false);
            this.generateMaybeBox(var1, var5, var4, 0.8F, 0, 2, 0, 2, 2, var12, CAVE_AIR, CAVE_AIR, false, false);
            if (this.spiderCorridor) {
               this.generateMaybeBox(var1, var5, var4, 0.6F, 0, 0, 0, 2, 1, var12, Blocks.COBWEB.defaultBlockState(), CAVE_AIR, false, true);
            }

            for(int var14 = 0; var14 < this.numSections; ++var14) {
               int var15 = 2 + var14 * 5;
               this.placeSupport(var1, var5, 0, 0, var15, 2, 2, var4);
               this.maybePlaceCobWeb(var1, var5, var4, 0.1F, 0, 2, var15 - 1);
               this.maybePlaceCobWeb(var1, var5, var4, 0.1F, 2, 2, var15 - 1);
               this.maybePlaceCobWeb(var1, var5, var4, 0.1F, 0, 2, var15 + 1);
               this.maybePlaceCobWeb(var1, var5, var4, 0.1F, 2, 2, var15 + 1);
               this.maybePlaceCobWeb(var1, var5, var4, 0.05F, 0, 2, var15 - 2);
               this.maybePlaceCobWeb(var1, var5, var4, 0.05F, 2, 2, var15 - 2);
               this.maybePlaceCobWeb(var1, var5, var4, 0.05F, 0, 2, var15 + 2);
               this.maybePlaceCobWeb(var1, var5, var4, 0.05F, 2, 2, var15 + 2);
               if (var4.nextInt(100) == 0) {
                  this.createChest(var1, var5, var4, 2, 0, var15 - 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if (var4.nextInt(100) == 0) {
                  this.createChest(var1, var5, var4, 0, 0, var15 + 1, BuiltInLootTables.ABANDONED_MINESHAFT);
               }

               if (this.spiderCorridor && !this.hasPlacedSpider) {
                  boolean var16 = true;
                  int var17 = var15 - 1 + var4.nextInt(3);
                  BlockPos.MutableBlockPos var18 = this.getWorldPos(1, 0, var17);
                  if (var5.isInside(var18) && this.isInterior(var1, 1, 0, var17, var5)) {
                     this.hasPlacedSpider = true;
                     var1.setBlock(var18, Blocks.SPAWNER.defaultBlockState(), 2);
                     BlockEntity var19 = var1.getBlockEntity(var18);
                     if (var19 instanceof SpawnerBlockEntity var20) {
                        var20.setEntityId(EntityType.CAVE_SPIDER, var4);
                     }
                  }
               }
            }

            for(int var21 = 0; var21 <= 2; ++var21) {
               for(int var23 = 0; var23 <= var12; ++var23) {
                  this.setPlanksBlock(var1, var5, var13, var21, -1, var23);
               }
            }

            boolean var22 = true;
            this.placeDoubleLowerOrUpperSupport(var1, var5, 0, -1, 2);
            if (this.numSections > 1) {
               int var24 = var12 - 2;
               this.placeDoubleLowerOrUpperSupport(var1, var5, 0, -1, var24);
            }

            if (this.hasRails) {
               BlockState var25 = Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_SOUTH);

               for(int var26 = 0; var26 <= var12; ++var26) {
                  BlockState var27 = this.getBlock(var1, 1, -1, var26, var5);
                  if (!var27.isAir() && var27.isSolidRender(var1, this.getWorldPos(1, -1, var26))) {
                     float var28 = this.isInterior(var1, 1, 0, var26, var5) ? 0.7F : 0.9F;
                     this.maybeGenerateBlock(var1, var5, var4, var28, 1, 0, var26, var25);
                  }
               }
            }
         }
      }

      private void placeDoubleLowerOrUpperSupport(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5) {
         BlockState var6 = this.type.getWoodState();
         BlockState var7 = this.type.getPlanksState();
         if (this.getBlock(var1, var3, var4, var5, var2).is(var7.getBlock())) {
            this.fillPillarDownOrChainUp(var1, var6, var3, var4, var5, var2);
         }

         if (this.getBlock(var1, var3 + 2, var4, var5, var2).is(var7.getBlock())) {
            this.fillPillarDownOrChainUp(var1, var6, var3 + 2, var4, var5, var2);
         }
      }

      @Override
      protected void fillColumnDown(WorldGenLevel var1, BlockState var2, int var3, int var4, int var5, BoundingBox var6) {
         BlockPos.MutableBlockPos var7 = this.getWorldPos(var3, var4, var5);
         if (var6.isInside(var7)) {
            int var8 = var7.getY();

            while(this.isReplaceableByStructures(var1.getBlockState(var7)) && var7.getY() > var1.getMinBuildHeight() + 1) {
               var7.move(Direction.DOWN);
            }

            if (this.canPlaceColumnOnTopOf(var1, var7, var1.getBlockState(var7))) {
               while(var7.getY() < var8) {
                  var7.move(Direction.UP);
                  var1.setBlock(var7, var2, 2);
               }
            }
         }
      }

      protected void fillPillarDownOrChainUp(WorldGenLevel var1, BlockState var2, int var3, int var4, int var5, BoundingBox var6) {
         BlockPos.MutableBlockPos var7 = this.getWorldPos(var3, var4, var5);
         if (var6.isInside(var7)) {
            int var8 = var7.getY();
            int var9 = 1;
            boolean var10 = true;

            for(boolean var11 = true; var10 || var11; ++var9) {
               if (var10) {
                  var7.setY(var8 - var9);
                  BlockState var12 = var1.getBlockState(var7);
                  boolean var13 = this.isReplaceableByStructures(var12) && !var12.is(Blocks.LAVA);
                  if (!var13 && this.canPlaceColumnOnTopOf(var1, var7, var12)) {
                     fillColumnBetween(var1, var2, var7, var8 - var9 + 1, var8);
                     return;
                  }

                  var10 = var9 <= 20 && var13 && var7.getY() > var1.getMinBuildHeight() + 1;
               }

               if (var11) {
                  var7.setY(var8 + var9);
                  BlockState var14 = var1.getBlockState(var7);
                  boolean var15 = this.isReplaceableByStructures(var14);
                  if (!var15 && this.canHangChainBelow(var1, var7, var14)) {
                     var1.setBlock(var7.setY(var8 + 1), this.type.getFenceState(), 2);
                     fillColumnBetween(var1, Blocks.CHAIN.defaultBlockState(), var7, var8 + 2, var8 + var9);
                     return;
                  }

                  var11 = var9 <= 50 && var15 && var7.getY() < var1.getMaxBuildHeight() - 1;
               }
            }
         }
      }

      private static void fillColumnBetween(WorldGenLevel var0, BlockState var1, BlockPos.MutableBlockPos var2, int var3, int var4) {
         for(int var5 = var3; var5 < var4; ++var5) {
            var0.setBlock(var2.setY(var5), var1, 2);
         }
      }

      private boolean canPlaceColumnOnTopOf(LevelReader var1, BlockPos var2, BlockState var3) {
         return var3.isFaceSturdy(var1, var2, Direction.UP);
      }

      private boolean canHangChainBelow(LevelReader var1, BlockPos var2, BlockState var3) {
         return Block.canSupportCenter(var1, var2, Direction.DOWN) && !(var3.getBlock() instanceof FallingBlock);
      }

      private void placeSupport(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6, int var7, RandomSource var8) {
         if (this.isSupportingBox(var1, var2, var3, var7, var6, var5)) {
            BlockState var9 = this.type.getPlanksState();
            BlockState var10 = this.type.getFenceState();
            this.generateBox(var1, var2, var3, var4, var5, var3, var6 - 1, var5, var10.setValue(FenceBlock.WEST, Boolean.valueOf(true)), CAVE_AIR, false);
            this.generateBox(var1, var2, var7, var4, var5, var7, var6 - 1, var5, var10.setValue(FenceBlock.EAST, Boolean.valueOf(true)), CAVE_AIR, false);
            if (var8.nextInt(4) == 0) {
               this.generateBox(var1, var2, var3, var6, var5, var3, var6, var5, var9, CAVE_AIR, false);
               this.generateBox(var1, var2, var7, var6, var5, var7, var6, var5, var9, CAVE_AIR, false);
            } else {
               this.generateBox(var1, var2, var3, var6, var5, var7, var6, var5, var9, CAVE_AIR, false);
               this.maybeGenerateBlock(
                  var1, var2, var8, 0.05F, var3 + 1, var6, var5 - 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.SOUTH)
               );
               this.maybeGenerateBlock(
                  var1, var2, var8, 0.05F, var3 + 1, var6, var5 + 1, Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.NORTH)
               );
            }
         }
      }

      private void maybePlaceCobWeb(WorldGenLevel var1, BoundingBox var2, RandomSource var3, float var4, int var5, int var6, int var7) {
         if (this.isInterior(var1, var5, var6, var7, var2) && var3.nextFloat() < var4 && this.hasSturdyNeighbours(var1, var2, var5, var6, var7, 2)) {
            this.placeBlock(var1, Blocks.COBWEB.defaultBlockState(), var5, var6, var7, var2);
         }
      }

      private boolean hasSturdyNeighbours(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6) {
         BlockPos.MutableBlockPos var7 = this.getWorldPos(var3, var4, var5);
         int var8 = 0;

         for(Direction var12 : Direction.values()) {
            var7.move(var12);
            if (var2.isInside(var7) && var1.getBlockState(var7).isFaceSturdy(var1, var7, var12.getOpposite())) {
               if (++var8 >= var6) {
                  return true;
               }
            }

            var7.move(var12.getOpposite());
         }

         return false;
      }
   }

   public static class MineShaftCrossing extends MineshaftPieces.MineShaftPiece {
      private final Direction direction;
      private final boolean isTwoFloored;

      public MineShaftCrossing(CompoundTag var1) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, var1);
         this.isTwoFloored = var1.getBoolean("tf");
         this.direction = Direction.from2DDataValue(var1.getInt("D"));
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         var2.putBoolean("tf", this.isTwoFloored);
         var2.putInt("D", this.direction.get2DDataValue());
      }

      public MineShaftCrossing(int var1, BoundingBox var2, @Nullable Direction var3, MineshaftStructure.Type var4) {
         super(StructurePieceType.MINE_SHAFT_CROSSING, var1, var4, var2);
         this.direction = var3;
         this.isTwoFloored = var2.getYSpan() > 3;
      }

      @Nullable
      public static BoundingBox findCrossing(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5) {
         byte var6;
         if (var1.nextInt(4) == 0) {
            var6 = 6;
         } else {
            var6 = 2;
         }
         BoundingBox var7 = switch(var5) {
            default -> new BoundingBox(-1, 0, -4, 3, var6, 0);
            case SOUTH -> new BoundingBox(-1, 0, 0, 3, var6, 4);
            case WEST -> new BoundingBox(-4, 0, -1, 0, var6, 3);
            case EAST -> new BoundingBox(0, 0, -1, 4, var6, 3);
         };
         var7.move(var2, var3, var4);
         return var0.findCollisionPiece(var7) != null ? null : var7;
      }

      @Override
      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         int var4 = this.getGenDepth();
         switch(this.direction) {
            case NORTH:
            default:
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.WEST, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.EAST, var4
               );
               break;
            case SOUTH:
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.WEST, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.EAST, var4
               );
               break;
            case WEST:
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.WEST, var4
               );
               break;
            case EAST:
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, var4
               );
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, Direction.EAST, var4
               );
         }

         if (this.isTwoFloored) {
            if (var3.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() - 1, Direction.NORTH, var4
               );
            }

            if (var3.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() + 1, Direction.WEST, var4
               );
            }

            if (var3.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.minZ() + 1, Direction.EAST, var4
               );
            }

            if (var3.nextBoolean()) {
               MineshaftPieces.generateAndAddPiece(
                  var1, var2, var3, this.boundingBox.minX() + 1, this.boundingBox.minY() + 3 + 1, this.boundingBox.maxZ() + 1, Direction.SOUTH, var4
               );
            }
         }
      }

      @Override
      public void postProcess(
         WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7
      ) {
         if (!this.isInInvalidLocation(var1, var5)) {
            BlockState var8 = this.type.getPlanksState();
            if (this.isTwoFloored) {
               this.generateBox(
                  var1,
                  var5,
                  this.boundingBox.minX() + 1,
                  this.boundingBox.minY(),
                  this.boundingBox.minZ(),
                  this.boundingBox.maxX() - 1,
                  this.boundingBox.minY() + 3 - 1,
                  this.boundingBox.maxZ(),
                  CAVE_AIR,
                  CAVE_AIR,
                  false
               );
               this.generateBox(
                  var1,
                  var5,
                  this.boundingBox.minX(),
                  this.boundingBox.minY(),
                  this.boundingBox.minZ() + 1,
                  this.boundingBox.maxX(),
                  this.boundingBox.minY() + 3 - 1,
                  this.boundingBox.maxZ() - 1,
                  CAVE_AIR,
                  CAVE_AIR,
                  false
               );
               this.generateBox(
                  var1,
                  var5,
                  this.boundingBox.minX() + 1,
                  this.boundingBox.maxY() - 2,
                  this.boundingBox.minZ(),
                  this.boundingBox.maxX() - 1,
                  this.boundingBox.maxY(),
                  this.boundingBox.maxZ(),
                  CAVE_AIR,
                  CAVE_AIR,
                  false
               );
               this.generateBox(
                  var1,
                  var5,
                  this.boundingBox.minX(),
                  this.boundingBox.maxY() - 2,
                  this.boundingBox.minZ() + 1,
                  this.boundingBox.maxX(),
                  this.boundingBox.maxY(),
                  this.boundingBox.maxZ() - 1,
                  CAVE_AIR,
                  CAVE_AIR,
                  false
               );
               this.generateBox(
                  var1,
                  var5,
                  this.boundingBox.minX() + 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.minZ() + 1,
                  this.boundingBox.maxX() - 1,
                  this.boundingBox.minY() + 3,
                  this.boundingBox.maxZ() - 1,
                  CAVE_AIR,
                  CAVE_AIR,
                  false
               );
            } else {
               this.generateBox(
                  var1,
                  var5,
                  this.boundingBox.minX() + 1,
                  this.boundingBox.minY(),
                  this.boundingBox.minZ(),
                  this.boundingBox.maxX() - 1,
                  this.boundingBox.maxY(),
                  this.boundingBox.maxZ(),
                  CAVE_AIR,
                  CAVE_AIR,
                  false
               );
               this.generateBox(
                  var1,
                  var5,
                  this.boundingBox.minX(),
                  this.boundingBox.minY(),
                  this.boundingBox.minZ() + 1,
                  this.boundingBox.maxX(),
                  this.boundingBox.maxY(),
                  this.boundingBox.maxZ() - 1,
                  CAVE_AIR,
                  CAVE_AIR,
                  false
               );
            }

            this.placeSupportPillar(var1, var5, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxY());
            this.placeSupportPillar(var1, var5, this.boundingBox.minX() + 1, this.boundingBox.minY(), this.boundingBox.maxZ() - 1, this.boundingBox.maxY());
            this.placeSupportPillar(var1, var5, this.boundingBox.maxX() - 1, this.boundingBox.minY(), this.boundingBox.minZ() + 1, this.boundingBox.maxY());
            this.placeSupportPillar(var1, var5, this.boundingBox.maxX() - 1, this.boundingBox.minY(), this.boundingBox.maxZ() - 1, this.boundingBox.maxY());
            int var9 = this.boundingBox.minY() - 1;

            for(int var10 = this.boundingBox.minX(); var10 <= this.boundingBox.maxX(); ++var10) {
               for(int var11 = this.boundingBox.minZ(); var11 <= this.boundingBox.maxZ(); ++var11) {
                  this.setPlanksBlock(var1, var5, var8, var10, var9, var11);
               }
            }
         }
      }

      private void placeSupportPillar(WorldGenLevel var1, BoundingBox var2, int var3, int var4, int var5, int var6) {
         if (!this.getBlock(var1, var3, var6 + 1, var5, var2).isAir()) {
            this.generateBox(var1, var2, var3, var4, var5, var3, var6, var5, this.type.getPlanksState(), CAVE_AIR, false);
         }
      }
   }

   abstract static class MineShaftPiece extends StructurePiece {
      protected MineshaftStructure.Type type;

      public MineShaftPiece(StructurePieceType var1, int var2, MineshaftStructure.Type var3, BoundingBox var4) {
         super(var1, var2, var4);
         this.type = var3;
      }

      public MineShaftPiece(StructurePieceType var1, CompoundTag var2) {
         super(var1, var2);
         this.type = MineshaftStructure.Type.byId(var2.getInt("MST"));
      }

      @Override
      protected boolean canBeReplaced(LevelReader var1, int var2, int var3, int var4, BoundingBox var5) {
         BlockState var6 = this.getBlock(var1, var2, var3, var4, var5);
         return !var6.is(this.type.getPlanksState().getBlock())
            && !var6.is(this.type.getWoodState().getBlock())
            && !var6.is(this.type.getFenceState().getBlock())
            && !var6.is(Blocks.CHAIN);
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         var2.putInt("MST", this.type.ordinal());
      }

      protected boolean isSupportingBox(BlockGetter var1, BoundingBox var2, int var3, int var4, int var5, int var6) {
         for(int var7 = var3; var7 <= var4; ++var7) {
            if (this.getBlock(var1, var7, var5 + 1, var6, var2).isAir()) {
               return false;
            }
         }

         return true;
      }

      protected boolean isInInvalidLocation(LevelAccessor var1, BoundingBox var2) {
         int var3 = Math.max(this.boundingBox.minX() - 1, var2.minX());
         int var4 = Math.max(this.boundingBox.minY() - 1, var2.minY());
         int var5 = Math.max(this.boundingBox.minZ() - 1, var2.minZ());
         int var6 = Math.min(this.boundingBox.maxX() + 1, var2.maxX());
         int var7 = Math.min(this.boundingBox.maxY() + 1, var2.maxY());
         int var8 = Math.min(this.boundingBox.maxZ() + 1, var2.maxZ());
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos((var3 + var6) / 2, (var4 + var7) / 2, (var5 + var8) / 2);
         if (var1.getBiome(var9).is(BiomeTags.MINESHAFT_BLOCKING)) {
            return true;
         } else {
            for(int var10 = var3; var10 <= var6; ++var10) {
               for(int var11 = var5; var11 <= var8; ++var11) {
                  if (var1.getBlockState(var9.set(var10, var4, var11)).liquid()) {
                     return true;
                  }

                  if (var1.getBlockState(var9.set(var10, var7, var11)).liquid()) {
                     return true;
                  }
               }
            }

            for(int var12 = var3; var12 <= var6; ++var12) {
               for(int var14 = var4; var14 <= var7; ++var14) {
                  if (var1.getBlockState(var9.set(var12, var14, var5)).liquid()) {
                     return true;
                  }

                  if (var1.getBlockState(var9.set(var12, var14, var8)).liquid()) {
                     return true;
                  }
               }
            }

            for(int var13 = var5; var13 <= var8; ++var13) {
               for(int var15 = var4; var15 <= var7; ++var15) {
                  if (var1.getBlockState(var9.set(var3, var15, var13)).liquid()) {
                     return true;
                  }

                  if (var1.getBlockState(var9.set(var6, var15, var13)).liquid()) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      protected void setPlanksBlock(WorldGenLevel var1, BoundingBox var2, BlockState var3, int var4, int var5, int var6) {
         if (this.isInterior(var1, var4, var5, var6, var2)) {
            BlockPos.MutableBlockPos var7 = this.getWorldPos(var4, var5, var6);
            BlockState var8 = var1.getBlockState(var7);
            if (!var8.isFaceSturdy(var1, var7, Direction.UP)) {
               var1.setBlock(var7, var3, 2);
            }
         }
      }
   }

   public static class MineShaftRoom extends MineshaftPieces.MineShaftPiece {
      private final List<BoundingBox> childEntranceBoxes = Lists.newLinkedList();

      public MineShaftRoom(int var1, RandomSource var2, int var3, int var4, MineshaftStructure.Type var5) {
         super(
            StructurePieceType.MINE_SHAFT_ROOM,
            var1,
            var5,
            new BoundingBox(var3, 50, var4, var3 + 7 + var2.nextInt(6), 54 + var2.nextInt(6), var4 + 7 + var2.nextInt(6))
         );
         this.type = var5;
      }

      public MineShaftRoom(CompoundTag var1) {
         super(StructurePieceType.MINE_SHAFT_ROOM, var1);
         BoundingBox.CODEC
            .listOf()
            .parse(NbtOps.INSTANCE, var1.getList("Entrances", 11))
            .resultOrPartial(MineshaftPieces.LOGGER::error)
            .ifPresent(this.childEntranceBoxes::addAll);
      }

      @Override
      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         int var4 = this.getGenDepth();
         int var6 = this.boundingBox.getYSpan() - 3 - 1;
         if (var6 <= 0) {
            var6 = 1;
         }

         int var9;
         for(var9 = 0; var9 < this.boundingBox.getXSpan(); var9 += 4) {
            var9 += var3.nextInt(this.boundingBox.getXSpan());
            if (var9 + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineshaftPieces.MineShaftPiece var7 = MineshaftPieces.generateAndAddPiece(
               var1,
               var2,
               var3,
               this.boundingBox.minX() + var9,
               this.boundingBox.minY() + var3.nextInt(var6) + 1,
               this.boundingBox.minZ() - 1,
               Direction.NORTH,
               var4
            );
            if (var7 != null) {
               BoundingBox var8 = var7.getBoundingBox();
               this.childEntranceBoxes
                  .add(new BoundingBox(var8.minX(), var8.minY(), this.boundingBox.minZ(), var8.maxX(), var8.maxY(), this.boundingBox.minZ() + 1));
            }
         }

         for(var9 = 0; var9 < this.boundingBox.getXSpan(); var9 += 4) {
            var9 += var3.nextInt(this.boundingBox.getXSpan());
            if (var9 + 3 > this.boundingBox.getXSpan()) {
               break;
            }

            MineshaftPieces.MineShaftPiece var16 = MineshaftPieces.generateAndAddPiece(
               var1,
               var2,
               var3,
               this.boundingBox.minX() + var9,
               this.boundingBox.minY() + var3.nextInt(var6) + 1,
               this.boundingBox.maxZ() + 1,
               Direction.SOUTH,
               var4
            );
            if (var16 != null) {
               BoundingBox var19 = var16.getBoundingBox();
               this.childEntranceBoxes
                  .add(new BoundingBox(var19.minX(), var19.minY(), this.boundingBox.maxZ() - 1, var19.maxX(), var19.maxY(), this.boundingBox.maxZ()));
            }
         }

         for(var9 = 0; var9 < this.boundingBox.getZSpan(); var9 += 4) {
            var9 += var3.nextInt(this.boundingBox.getZSpan());
            if (var9 + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            MineshaftPieces.MineShaftPiece var17 = MineshaftPieces.generateAndAddPiece(
               var1,
               var2,
               var3,
               this.boundingBox.minX() - 1,
               this.boundingBox.minY() + var3.nextInt(var6) + 1,
               this.boundingBox.minZ() + var9,
               Direction.WEST,
               var4
            );
            if (var17 != null) {
               BoundingBox var20 = var17.getBoundingBox();
               this.childEntranceBoxes
                  .add(new BoundingBox(this.boundingBox.minX(), var20.minY(), var20.minZ(), this.boundingBox.minX() + 1, var20.maxY(), var20.maxZ()));
            }
         }

         for(var9 = 0; var9 < this.boundingBox.getZSpan(); var9 += 4) {
            var9 += var3.nextInt(this.boundingBox.getZSpan());
            if (var9 + 3 > this.boundingBox.getZSpan()) {
               break;
            }

            MineshaftPieces.MineShaftPiece var18 = MineshaftPieces.generateAndAddPiece(
               var1,
               var2,
               var3,
               this.boundingBox.maxX() + 1,
               this.boundingBox.minY() + var3.nextInt(var6) + 1,
               this.boundingBox.minZ() + var9,
               Direction.EAST,
               var4
            );
            if (var18 != null) {
               BoundingBox var21 = var18.getBoundingBox();
               this.childEntranceBoxes
                  .add(new BoundingBox(this.boundingBox.maxX() - 1, var21.minY(), var21.minZ(), this.boundingBox.maxX(), var21.maxY(), var21.maxZ()));
            }
         }
      }

      @Override
      public void postProcess(
         WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7
      ) {
         if (!this.isInInvalidLocation(var1, var5)) {
            this.generateBox(
               var1,
               var5,
               this.boundingBox.minX(),
               this.boundingBox.minY() + 1,
               this.boundingBox.minZ(),
               this.boundingBox.maxX(),
               Math.min(this.boundingBox.minY() + 3, this.boundingBox.maxY()),
               this.boundingBox.maxZ(),
               CAVE_AIR,
               CAVE_AIR,
               false
            );

            for(BoundingBox var9 : this.childEntranceBoxes) {
               this.generateBox(var1, var5, var9.minX(), var9.maxY() - 2, var9.minZ(), var9.maxX(), var9.maxY(), var9.maxZ(), CAVE_AIR, CAVE_AIR, false);
            }

            this.generateUpperHalfSphere(
               var1,
               var5,
               this.boundingBox.minX(),
               this.boundingBox.minY() + 4,
               this.boundingBox.minZ(),
               this.boundingBox.maxX(),
               this.boundingBox.maxY(),
               this.boundingBox.maxZ(),
               CAVE_AIR,
               false
            );
         }
      }

      @Override
      public void move(int var1, int var2, int var3) {
         super.move(var1, var2, var3);

         for(BoundingBox var5 : this.childEntranceBoxes) {
            var5.move(var1, var2, var3);
         }
      }

      @Override
      protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
         super.addAdditionalSaveData(var1, var2);
         BoundingBox.CODEC
            .listOf()
            .encodeStart(NbtOps.INSTANCE, this.childEntranceBoxes)
            .resultOrPartial(MineshaftPieces.LOGGER::error)
            .ifPresent(var1x -> var2.put("Entrances", var1x));
      }
   }

   public static class MineShaftStairs extends MineshaftPieces.MineShaftPiece {
      public MineShaftStairs(int var1, BoundingBox var2, Direction var3, MineshaftStructure.Type var4) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, var1, var4, var2);
         this.setOrientation(var3);
      }

      public MineShaftStairs(CompoundTag var1) {
         super(StructurePieceType.MINE_SHAFT_STAIRS, var1);
      }

      @Nullable
      public static BoundingBox findStairs(StructurePieceAccessor var0, RandomSource var1, int var2, int var3, int var4, Direction var5) {
         BoundingBox var6 = switch(var5) {
            default -> new BoundingBox(0, -5, -8, 2, 2, 0);
            case SOUTH -> new BoundingBox(0, -5, 0, 2, 2, 8);
            case WEST -> new BoundingBox(-8, -5, 0, 0, 2, 2);
            case EAST -> new BoundingBox(0, -5, 0, 8, 2, 2);
         };
         var6.move(var2, var3, var4);
         return var0.findCollisionPiece(var6) != null ? null : var6;
      }

      @Override
      public void addChildren(StructurePiece var1, StructurePieceAccessor var2, RandomSource var3) {
         int var4 = this.getGenDepth();
         Direction var5 = this.getOrientation();
         if (var5 != null) {
            switch(var5) {
               case NORTH:
               default:
                  MineshaftPieces.generateAndAddPiece(
                     var1, var2, var3, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.minZ() - 1, Direction.NORTH, var4
                  );
                  break;
               case SOUTH:
                  MineshaftPieces.generateAndAddPiece(
                     var1, var2, var3, this.boundingBox.minX(), this.boundingBox.minY(), this.boundingBox.maxZ() + 1, Direction.SOUTH, var4
                  );
                  break;
               case WEST:
                  MineshaftPieces.generateAndAddPiece(
                     var1, var2, var3, this.boundingBox.minX() - 1, this.boundingBox.minY(), this.boundingBox.minZ(), Direction.WEST, var4
                  );
                  break;
               case EAST:
                  MineshaftPieces.generateAndAddPiece(
                     var1, var2, var3, this.boundingBox.maxX() + 1, this.boundingBox.minY(), this.boundingBox.minZ(), Direction.EAST, var4
                  );
            }
         }
      }

      @Override
      public void postProcess(
         WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7
      ) {
         if (!this.isInInvalidLocation(var1, var5)) {
            this.generateBox(var1, var5, 0, 5, 0, 2, 7, 1, CAVE_AIR, CAVE_AIR, false);
            this.generateBox(var1, var5, 0, 0, 7, 2, 2, 8, CAVE_AIR, CAVE_AIR, false);

            for(int var8 = 0; var8 < 5; ++var8) {
               this.generateBox(var1, var5, 0, 5 - var8 - (var8 < 4 ? 1 : 0), 2 + var8, 2, 7 - var8, 2 + var8, CAVE_AIR, CAVE_AIR, false);
            }
         }
      }
   }
}
