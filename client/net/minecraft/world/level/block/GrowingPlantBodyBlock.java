package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.BlockUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class GrowingPlantBodyBlock extends GrowingPlantBlock implements BonemealableBlock {
   protected GrowingPlantBodyBlock(final BlockBehaviour.Properties properties, final Direction growthDirection, final VoxelShape shape, final boolean scheduleFluidTicks) {
      super(properties, growthDirection, shape, scheduleFluidTicks);
   }

   protected abstract MapCodec<? extends GrowingPlantBodyBlock> codec();

   protected BlockState updateHeadAfterConvertedFromBody(final BlockState bodyState, final BlockState headState) {
      return headState;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == this.growthDirection.getOpposite() && !state.canSurvive(level, pos)) {
         ticks.scheduleTick(pos, (Block)this, 1);
      }

      GrowingPlantHeadBlock headBlock = this.getHeadBlock();
      if (directionToNeighbour == this.growthDirection && !neighbourState.is(this) && !neighbourState.is(headBlock)) {
         return this.updateHeadAfterConvertedFromBody(state, headBlock.getStateForPlacement(random));
      } else {
         if (this.scheduleFluidTicks) {
            ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
         }

         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }

   protected ItemStack getCloneItemStack(final LevelReader level, final BlockPos pos, final BlockState state, final boolean includeData) {
      return new ItemStack(this.getHeadBlock());
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state) {
      Optional<BlockPos> headPos = this.getHeadPos(level, pos, state.getBlock());
      if (headPos.isEmpty()) {
         return false;
      } else {
         BlockPos growthPos = ((BlockPos)headPos.get()).relative(this.growthDirection);
         return this.getHeadBlock().canGrowInto(level.getBlockState(growthPos)) && level.isInsideBuildHeight(growthPos);
      }
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state) {
      Optional<BlockPos> headPos = this.getHeadPos(level, pos, state.getBlock());
      if (headPos.isPresent()) {
         BlockState forwardState = level.getBlockState((BlockPos)headPos.get());
         ((GrowingPlantHeadBlock)forwardState.getBlock()).performBonemeal(level, random, (BlockPos)headPos.get(), forwardState);
      }

   }

   private Optional<BlockPos> getHeadPos(final BlockGetter level, final BlockPos pos, final Block bodyBlock) {
      return BlockUtil.getTopConnectedBlock(level, pos, bodyBlock, this.growthDirection, this.getHeadBlock());
   }

   protected boolean canBeReplaced(final BlockState state, final BlockPlaceContext context) {
      boolean result = super.canBeReplaced(state, context);
      return result && context.getItemInHand().is(this.getHeadBlock().asItem()) ? false : result;
   }

   protected Block getBodyBlock() {
      return this;
   }
}
