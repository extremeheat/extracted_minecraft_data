package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public interface SpawnPlacementTypes {
   SpawnPlacementType NO_RESTRICTIONS = (var0, var1, var2) -> {
      return true;
   };
   SpawnPlacementType IN_WATER = (var0, var1, var2) -> {
      if (var2 != null && var0.getWorldBorder().isWithinBounds(var1)) {
         BlockPos var3 = var1.above();
         return var0.getFluidState(var1).is(FluidTags.WATER) && !var0.getBlockState(var3).isRedstoneConductor(var0, var3);
      } else {
         return false;
      }
   };
   SpawnPlacementType IN_LAVA = (var0, var1, var2) -> {
      return var2 != null && var0.getWorldBorder().isWithinBounds(var1) ? var0.getFluidState(var1).is(FluidTags.LAVA) : false;
   };
   SpawnPlacementType ON_GROUND = new SpawnPlacementType() {
      public boolean isSpawnPositionOk(LevelReader var1, BlockPos var2, @Nullable EntityType<?> var3) {
         if (var3 != null && var1.getWorldBorder().isWithinBounds(var2)) {
            BlockPos var4 = var2.above();
            BlockPos var5 = var2.below();
            BlockState var6 = var1.getBlockState(var5);
            if (!var6.isValidSpawn(var1, var5, var3)) {
               return false;
            } else {
               return this.isValidEmptySpawnBlock(var1, var2, var3) && this.isValidEmptySpawnBlock(var1, var4, var3);
            }
         } else {
            return false;
         }
      }

      private boolean isValidEmptySpawnBlock(LevelReader var1, BlockPos var2, EntityType<?> var3) {
         BlockState var4 = var1.getBlockState(var2);
         return NaturalSpawner.isValidEmptySpawnBlock(var1, var2, var4, var4.getFluidState(), var3);
      }

      public BlockPos adjustSpawnPosition(LevelReader var1, BlockPos var2) {
         BlockPos var3 = var2.below();
         return var1.getBlockState(var3).isPathfindable(PathComputationType.LAND) ? var3 : var2;
      }
   };
}
