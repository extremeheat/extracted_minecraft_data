package net.minecraft.world.level.levelgen.structure.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ScatteredFeaturePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public class SwampHutPiece extends ScatteredFeaturePiece {
   private boolean spawnedWitch;
   private boolean spawnedCat;

   public SwampHutPiece(RandomSource var1, int var2, int var3) {
      super(StructurePieceType.SWAMPLAND_HUT, var2, 64, var3, 7, 7, 9, getRandomHorizontalDirection(var1));
   }

   public SwampHutPiece(CompoundTag var1) {
      super(StructurePieceType.SWAMPLAND_HUT, var1);
      this.spawnedWitch = var1.getBoolean("Witch");
      this.spawnedCat = var1.getBoolean("Cat");
   }

   @Override
   protected void addAdditionalSaveData(StructurePieceSerializationContext var1, CompoundTag var2) {
      super.addAdditionalSaveData(var1, var2);
      var2.putBoolean("Witch", this.spawnedWitch);
      var2.putBoolean("Cat", this.spawnedCat);
   }

   @Override
   public void postProcess(WorldGenLevel var1, StructureManager var2, ChunkGenerator var3, RandomSource var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      if (this.updateAverageGroundHeight(var1, var5, 0)) {
         this.generateBox(var1, var5, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(var1, var5, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(var1, var5, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, var5);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 1, 3, 4, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 3, 4, var5);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 3, 5, var5);
         this.placeBlock(var1, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, var5);
         this.placeBlock(var1, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, var5);
         this.placeBlock(var1, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, var5);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, var5);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, var5);
         BlockState var8 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState var9 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState var10 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         BlockState var11 = Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         this.generateBox(var1, var5, 0, 4, 1, 6, 4, 1, var8, var8, false);
         this.generateBox(var1, var5, 0, 4, 2, 0, 4, 7, var9, var9, false);
         this.generateBox(var1, var5, 6, 4, 2, 6, 4, 7, var10, var10, false);
         this.generateBox(var1, var5, 0, 4, 8, 6, 4, 8, var11, var11, false);
         this.placeBlock(var1, var8.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, var5);
         this.placeBlock(var1, var8.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, var5);
         this.placeBlock(var1, var11.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, var5);
         this.placeBlock(var1, var11.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, var5);

         for (byte var12 = 2; var12 <= 7; var12 += 5) {
            for (byte var13 = 1; var13 <= 5; var13 += 4) {
               this.fillColumnDown(var1, Blocks.OAK_LOG.defaultBlockState(), var13, -1, var12, var5);
            }
         }

         if (!this.spawnedWitch) {
            BlockPos.MutableBlockPos var14 = this.getWorldPos(2, 2, 5);
            if (var5.isInside(var14)) {
               this.spawnedWitch = true;
               Witch var15 = EntityType.WITCH.create(var1.getLevel());
               if (var15 != null) {
                  var15.setPersistenceRequired();
                  var15.moveTo((double)var14.getX() + 0.5, (double)var14.getY(), (double)var14.getZ() + 0.5, 0.0F, 0.0F);
                  var15.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var14), MobSpawnType.STRUCTURE, null);
                  var1.addFreshEntityWithPassengers(var15);
               }
            }
         }

         this.spawnCat(var1, var5);
      }
   }

   private void spawnCat(ServerLevelAccessor var1, BoundingBox var2) {
      if (!this.spawnedCat) {
         BlockPos.MutableBlockPos var3 = this.getWorldPos(2, 2, 5);
         if (var2.isInside(var3)) {
            this.spawnedCat = true;
            Cat var4 = EntityType.CAT.create(var1.getLevel());
            if (var4 != null) {
               var4.setPersistenceRequired();
               var4.moveTo((double)var3.getX() + 0.5, (double)var3.getY(), (double)var3.getZ() + 0.5, 0.0F, 0.0F);
               var4.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var3), MobSpawnType.STRUCTURE, null);
               var1.addFreshEntityWithPassengers(var4);
            }
         }
      }
   }
}
