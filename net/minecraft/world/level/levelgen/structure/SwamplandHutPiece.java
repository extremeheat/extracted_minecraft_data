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
import net.minecraft.world.level.LevelAccessor;
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

   public boolean postProcess(LevelAccessor var1, ChunkGenerator var2, Random var3, BoundingBox var4, ChunkPos var5) {
      if (!this.updateAverageGroundHeight(var1, var4, 0)) {
         return false;
      } else {
         this.generateBox(var1, var4, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var4, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var4, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var4, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var4, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var4, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var4, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.defaultBlockState(), Blocks.SPRUCE_PLANKS.defaultBlockState(), false);
         this.generateBox(var1, var4, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(var1, var4, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(var1, var4, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.generateBox(var1, var4, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.defaultBlockState(), Blocks.OAK_LOG.defaultBlockState(), false);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 2, 3, 2, var4);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 3, 3, 7, var4);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 1, 3, 4, var4);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 3, 4, var4);
         this.placeBlock(var1, Blocks.AIR.defaultBlockState(), 5, 3, 5, var4);
         this.placeBlock(var1, Blocks.POTTED_RED_MUSHROOM.defaultBlockState(), 1, 3, 5, var4);
         this.placeBlock(var1, Blocks.CRAFTING_TABLE.defaultBlockState(), 3, 2, 6, var4);
         this.placeBlock(var1, Blocks.CAULDRON.defaultBlockState(), 4, 2, 6, var4);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 1, 2, 1, var4);
         this.placeBlock(var1, Blocks.OAK_FENCE.defaultBlockState(), 5, 2, 1, var4);
         BlockState var6 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH);
         BlockState var7 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.EAST);
         BlockState var8 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.WEST);
         BlockState var9 = (BlockState)Blocks.SPRUCE_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
         this.generateBox(var1, var4, 0, 4, 1, 6, 4, 1, var6, var6, false);
         this.generateBox(var1, var4, 0, 4, 2, 0, 4, 7, var7, var7, false);
         this.generateBox(var1, var4, 6, 4, 2, 6, 4, 7, var8, var8, false);
         this.generateBox(var1, var4, 0, 4, 8, 6, 4, 8, var9, var9, false);
         this.placeBlock(var1, (BlockState)var6.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 0, 4, 1, var4);
         this.placeBlock(var1, (BlockState)var6.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 6, 4, 1, var4);
         this.placeBlock(var1, (BlockState)var9.setValue(StairBlock.SHAPE, StairsShape.OUTER_LEFT), 0, 4, 8, var4);
         this.placeBlock(var1, (BlockState)var9.setValue(StairBlock.SHAPE, StairsShape.OUTER_RIGHT), 6, 4, 8, var4);

         int var10;
         int var11;
         for(var10 = 2; var10 <= 7; var10 += 5) {
            for(var11 = 1; var11 <= 5; var11 += 4) {
               this.fillColumnDown(var1, Blocks.OAK_LOG.defaultBlockState(), var11, -1, var10, var4);
            }
         }

         if (!this.spawnedWitch) {
            var10 = this.getWorldX(2, 5);
            var11 = this.getWorldY(2);
            int var12 = this.getWorldZ(2, 5);
            if (var4.isInside(new BlockPos(var10, var11, var12))) {
               this.spawnedWitch = true;
               Witch var13 = (Witch)EntityType.WITCH.create(var1.getLevel());
               var13.setPersistenceRequired();
               var13.moveTo((double)var10 + 0.5D, (double)var11, (double)var12 + 0.5D, 0.0F, 0.0F);
               var13.finalizeSpawn(var1, var1.getCurrentDifficultyAt(new BlockPos(var10, var11, var12)), MobSpawnType.STRUCTURE, (SpawnGroupData)null, (CompoundTag)null);
               var1.addFreshEntity(var13);
            }
         }

         this.spawnCat(var1, var4);
         return true;
      }
   }

   private void spawnCat(LevelAccessor var1, BoundingBox var2) {
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
            var1.addFreshEntity(var6);
         }
      }

   }
}
