package net.minecraft.client.renderer.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import org.jspecify.annotations.Nullable;

public class MovingBlockRenderState implements BlockAndTintGetter {
   public BlockPos randomSeedPos;
   public BlockPos blockPos;
   public BlockState blockState;
   public @Nullable Holder<Biome> biome;
   public BlockAndTintGetter level;

   public MovingBlockRenderState() {
      super();
      this.randomSeedPos = BlockPos.ZERO;
      this.blockPos = BlockPos.ZERO;
      this.blockState = Blocks.AIR.defaultBlockState();
      this.level = EmptyBlockAndTintGetter.INSTANCE;
   }

   public float getShade(final Direction direction, final boolean shade) {
      return this.level.getShade(direction, shade);
   }

   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   public int getBlockTint(final BlockPos pos, final ColorResolver color) {
      return this.biome == null ? -1 : color.getColor(this.biome.value(), (double)pos.getX(), (double)pos.getZ());
   }

   public @Nullable BlockEntity getBlockEntity(final BlockPos pos) {
      return null;
   }

   public BlockState getBlockState(final BlockPos pos) {
      return pos.equals(this.blockPos) ? this.blockState : Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(final BlockPos pos) {
      return this.getBlockState(pos).getFluidState();
   }

   public int getHeight() {
      return 1;
   }

   public int getMinY() {
      return this.blockPos.getY();
   }
}
