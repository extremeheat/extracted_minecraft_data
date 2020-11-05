package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class SwamplandHutPiece extends ScatteredFeaturePiece {
   private boolean spawnedWitch;
   private boolean spawnedCat;

   public SwamplandHutPiece(Random var1, int var2, int var3) {
      super(StructurePieceType.SWAMPLAND_HUT, var1, var2, 64, var3, 7, 7, 9);
   }

   public SwamplandHutPiece(StructureManager var1, CompoundTag var2) {
      super(StructurePieceType.SWAMPLAND_HUT, var2);
      this.spawnedWitch = var2.getBoolean("Witch");
      this.spawnedCat = var2.getBoolean("Cat");
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("Witch", this.spawnedWitch);
      var1.putBoolean("Cat", this.spawnedCat);
   }

   public boolean postProcess(WorldGenLevel var1, StructureFeatureManager var2, ChunkGenerator var3, Random var4, BoundingBox var5, ChunkPos var6, BlockPos var7) {
      if (!this.updateAverageGroundHeight(var1, var5, 0)) {
         return false;
      } else {
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
         BlockState var8 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState var9 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState var10 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         BlockState var11 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         this.generateBox(var1, var5, 0, 4, 1, 6, 4, 1, var8, var8, false);
         this.generateBox(var1, var5, 0, 4, 2, 0, 4, 7, var9, var9, false);
         this.generateBox(var1, var5, 6, 4, 2, 6, 4, 7, var10, var10, false);
         this.generateBox(var1, var5, 0, 4, 8, 6, 4, 8, var11, var11, false);
         this.placeBlock(var1, (BlockState)var8.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, var5);
         this.placeBlock(var1, (BlockState)var8.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, var5);
         this.placeBlock(var1, (BlockState)var11.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, var5);
         this.placeBlock(var1, (BlockState)var11.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, var5);

         int var12;
         int var13;
         for(var12 = 2; var12 <= 7; var12 += 5) {
            for(var13 = 1; var13 <= 5; var13 += 4) {
               this.fillColumnDown(var1, Blocks.OAK_LOG.defaultBlockState(), var13, -1, var12, var5);
            }
         }

         if (!this.spawnedWitch) {
            var12 = this.getWorldX(2, 5);
            var13 = this.getWorldY(2);
            int var14 = this.getWorldZ(2, 5);
            if (var5.isInside(new BlockPos(var12, var13, var14))) {
               this.spawnedWitch = true;
               Witch var15 = (Witch)EntityType.WITCH.create(var1.getLevel());
               var15.setPersistenceRequired();
               var15.moveTo((double)var12 + 0.5D, (double)var13, (double)var14 + 0.5D, 0.0F, 0.0F);
               var15.finalizeSpawn(var1, var1.getCurrentDifficultyAt(new BlockPos(var12, var13, var14)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
               var1.addFreshEntityWithPassengers(var15);
            }
         }

         this.spawnCat(var1, var5);
         return true;
      }
   }

   private void spawnCat(ServerLevelAccessor var1, BoundingBox var2) {
      if (!this.spawnedCat) {
         int var3 = this.getWorldX(2, 5);
         int var4 = this.getWorldY(2);
         int var5 = this.getWorldZ(2, 5);
         if (var2.isInside(new BlockPos(var3, var4, var5))) {
            this.spawnedCat = true;
            Cat var6 = (Cat)EntityType.CAT.create(var1.getLevel());
            var6.setPersistenceRequired();
            var6.moveTo((double)var3 + 0.5D, (double)var4, (double)var5 + 0.5D, 0.0F, 0.0F);
            var6.finalizeSpawn(var1, var1.getCurrentDifficultyAt(new BlockPos(var3, var4, var5)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
            var1.addFreshEntityWithPassengers(var6);
         }
      }

   }
}
