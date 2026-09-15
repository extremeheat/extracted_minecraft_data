package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class HangingRootsBlock extends Block implements SimpleWaterloggedBlock {
   private static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE;

   protected HangingRootsBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(WATERLOGGED);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      if (state != null) {
         FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
         return (BlockState)state.setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
      } else {
         return null;
      }
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos attachedToPos = pos.above();
      BlockState attachedToState = level.getBlockState(attachedToPos);
      return attachedToState.isFaceSturdy(level, attachedToPos, Direction.DOWN);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == Direction.UP && !this.canSurvive(state, level, pos)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if ((Boolean)state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
         }

         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE = Block.column(12.0, 10.0, 16.0);
   }
}
