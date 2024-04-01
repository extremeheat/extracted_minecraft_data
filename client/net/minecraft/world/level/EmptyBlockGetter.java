package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public enum EmptyBlockGetter implements BlockGetter {
   INSTANCE;

   private EmptyBlockGetter() {
   }

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos var1) {
      return null;
   }

   @Override
   public BlockState getBlockState(BlockPos var1) {
      return Blocks.AIR.defaultBlockState();
   }

   @Override
   public FluidState getFluidState(BlockPos var1) {
      return Fluids.EMPTY.defaultFluidState();
   }

   @Override
   public int getMinBuildHeight() {
      return 0;
   }

   @Override
   public int getHeight() {
      return 0;
   }

   @Override
   public boolean isPotato() {
      return false;
   }
}
