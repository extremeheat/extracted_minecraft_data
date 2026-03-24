package net.minecraft.client.renderer.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
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
   public CardinalLighting cardinalLighting;
   public LevelLightEngine lightEngine;

   public MovingBlockRenderState() {
      super();
      this.randomSeedPos = BlockPos.ZERO;
      this.blockPos = BlockPos.ZERO;
      this.blockState = Blocks.AIR.defaultBlockState();
      this.cardinalLighting = CardinalLighting.DEFAULT;
      this.lightEngine = LevelLightEngine.EMPTY;
   }

   public CardinalLighting cardinalLighting() {
      return this.cardinalLighting;
   }

   public LevelLightEngine getLightEngine() {
      return this.lightEngine;
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
