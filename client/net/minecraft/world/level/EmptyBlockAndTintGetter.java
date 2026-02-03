package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

public enum EmptyBlockAndTintGetter implements BlockAndTintGetter {
   INSTANCE;

   private EmptyBlockAndTintGetter() {
   }

   public float getShade(final Direction direction, final boolean shade) {
      return 1.0F;
   }

   public LevelLightEngine getLightEngine() {
      return LevelLightEngine.EMPTY;
   }

   public int getBlockTint(final BlockPos pos, final ColorResolver color) {
      return -1;
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

   public int getHeight() {
      return 0;
   }

   public int getMinY() {
      return 0;
   }

   // $FF: synthetic method
   private static EmptyBlockAndTintGetter[] $values() {
      return new EmptyBlockAndTintGetter[]{INSTANCE};
   }
}
