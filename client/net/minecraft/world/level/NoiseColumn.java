package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public final class NoiseColumn implements BlockGetter {
   private final BlockState[] column;

   public NoiseColumn(BlockState[] var1) {
      super();
      this.column = var1;
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos var1) {
      return null;
   }

   public BlockState getBlockState(BlockPos var1) {
      int var2 = var1.getY();
      return var2 >= 0 && var2 < this.column.length ? this.column[var2] : Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos var1) {
      return this.getBlockState(var1).getFluidState();
   }

   public int getSectionsCount() {
      return 16;
   }

   public int getMinSection() {
      return 0;
   }
}
