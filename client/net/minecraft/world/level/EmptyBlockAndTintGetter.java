package net.minecraft.world.level;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public enum EmptyBlockAndTintGetter implements BlockAndTintGetter {
   INSTANCE;

   private EmptyBlockAndTintGetter() {
   }

   public float getShade(Direction var1, boolean var2) {
      return 1.0F;
   }

   public LevelLightEngine getLightEngine() {
      return LevelLightEngine.EMPTY;
   }

   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      return -1;
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
