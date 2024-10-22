package net.minecraft.client.renderer.entity.state;

import javax.annotation.Nullable;
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

public class FallingBlockRenderState extends EntityRenderState implements BlockAndTintGetter {
   public BlockPos startBlockPos = BlockPos.ZERO;
   public BlockPos blockPos = BlockPos.ZERO;
   public BlockState blockState = Blocks.SAND.defaultBlockState();
   @Nullable
   public Holder<Biome> biome;
   public BlockAndTintGetter level = EmptyBlockAndTintGetter.INSTANCE;

   public FallingBlockRenderState() {
      super();
   }

   @Override
   public float getShade(Direction var1, boolean var2) {
      return this.level.getShade(var1, var2);
   }

   @Override
   public LevelLightEngine getLightEngine() {
      return this.level.getLightEngine();
   }

   @Override
   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      return this.biome == null ? -1 : var2.getColor(this.biome.value(), (double)var1.getX(), (double)var1.getZ());
   }

   @Nullable
   @Override
   public BlockEntity getBlockEntity(BlockPos var1) {
      return null;
   }

   @Override
   public BlockState getBlockState(BlockPos var1) {
      return var1.equals(this.blockPos) ? this.blockState : Blocks.AIR.defaultBlockState();
   }

   @Override
   public FluidState getFluidState(BlockPos var1) {
      return this.getBlockState(var1).getFluidState();
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public int getMinY() {
      return this.blockPos.getY();
   }
}
