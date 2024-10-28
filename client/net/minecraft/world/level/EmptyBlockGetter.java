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
   public BlockEntity getBlockEntity(BlockPos var1) {
      return null;
   }

   public BlockState getBlockState(BlockPos var1) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos var1) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public int getMinBuildHeight() {
      return 0;
   }

   public int getHeight() {
      return 0;
   }

   // $FF: synthetic method
   private static EmptyBlockGetter[] $values() {
      return new EmptyBlockGetter[]{INSTANCE};
   }
}
