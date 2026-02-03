package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public enum EmptyBlockGetter implements BlockGetter {
   INSTANCE;

   private EmptyBlockGetter() {
   }

   public @Nullable BlockEntity getBlockEntity(final BlockPos pos) {
      return null;
   }

   public BlockState getBlockState(final BlockPos pos) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(final BlockPos pos) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public int getMinY() {
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
