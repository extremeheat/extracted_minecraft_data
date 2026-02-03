package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;

public class ObserverBlock extends DirectionalBlock {
   public static final MapCodec<ObserverBlock> CODEC = simpleCodec(ObserverBlock::new);
   public static final BooleanProperty POWERED;

   public MapCodec<ObserverBlock> codec() {
      return CODEC;
   }

   public ObserverBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH)).setValue(POWERED, false));
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED);
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if ((Boolean)state.getValue(POWERED)) {
         level.setBlock(pos, (BlockState)state.setValue(POWERED, false), 2);
      } else {
         level.setBlock(pos, (BlockState)state.setValue(POWERED, true), 2);
         level.scheduleTick(pos, this, 2);
      }

      this.updateNeighborsInFront(level, pos, state);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (state.getValue(FACING) == directionToNeighbour && !(Boolean)state.getValue(POWERED)) {
         this.startSignal(level, ticks, pos);
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   private void startSignal(final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos) {
      if (!level.isClientSide() && !ticks.getBlockTicks().hasScheduledTick(pos, this)) {
         ticks.scheduleTick(pos, (Block)this, 2);
      }

   }

   protected void updateNeighborsInFront(final Level level, final BlockPos pos, final BlockState state) {
      Direction direction = (Direction)state.getValue(FACING);
      BlockPos oppositePos = pos.relative(direction.getOpposite());
      Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, direction.getOpposite(), (Direction)null);
      level.neighborChanged(oppositePos, this, orientation);
      level.updateNeighborsAtExceptFromFacing(oppositePos, this, direction, orientation);
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return state.getSignal(level, pos, direction);
   }

   protected int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return (Boolean)state.getValue(POWERED) && state.getValue(FACING) == direction ? 15 : 0;
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!state.is(oldState.getBlock())) {
         if (!level.isClientSide() && (Boolean)state.getValue(POWERED) && !level.getBlockTicks().hasScheduledTick(pos, this)) {
            BlockState newState = (BlockState)state.setValue(POWERED, false);
            level.setBlock(pos, newState, 18);
            this.updateNeighborsInFront(level, pos, newState);
         }

      }
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if ((Boolean)state.getValue(POWERED) && level.getBlockTicks().hasScheduledTick(pos, this)) {
         this.updateNeighborsInFront(level, pos, (BlockState)state.setValue(POWERED, false));
      }

   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
   }

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
