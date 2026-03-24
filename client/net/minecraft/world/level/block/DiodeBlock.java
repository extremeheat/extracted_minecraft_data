package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.TickPriority;
import org.jspecify.annotations.Nullable;

public abstract class DiodeBlock extends HorizontalDirectionalBlock {
   public static final BooleanProperty POWERED;
   private static final VoxelShape SHAPE;

   protected DiodeBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   protected abstract MapCodec<? extends DiodeBlock> codec();

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return SHAPE;
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos belowPos = pos.below();
      return this.canSurviveOn(level, belowPos, level.getBlockState(belowPos));
   }

   protected boolean canSurviveOn(final LevelReader level, final BlockPos neightborPos, final BlockState neighborState) {
      return neighborState.isFaceSturdy(level, neightborPos, Direction.UP, SupportType.RIGID);
   }

   protected void tick(final BlockState state, final ServerLevel level, final BlockPos pos, final RandomSource random) {
      if (!this.isLocked(level, pos, state)) {
         boolean on = (Boolean)state.getValue(POWERED);
         boolean shouldTurnOn = this.shouldTurnOn(level, pos, state);
         if (on && !shouldTurnOn) {
            level.setBlock(pos, (BlockState)state.setValue(POWERED, false), 2);
         } else if (!on) {
            level.setBlock(pos, (BlockState)state.setValue(POWERED, true), 2);
            if (!shouldTurnOn) {
               level.scheduleTick(pos, this, this.getDelay(state), TickPriority.VERY_HIGH);
            }
         }

      }
   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return state.getSignal(level, pos, direction);
   }

   protected int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      if (!(Boolean)state.getValue(POWERED)) {
         return 0;
      } else {
         return state.getValue(FACING) == direction ? this.getOutputSignal(level, pos, state) : 0;
      }
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (state.canSurvive(level, pos)) {
         this.checkTickOnNeighbor(level, pos, state);
      } else {
         BlockEntity blockEntity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
         dropResources(state, level, pos, blockEntity);
         level.removeBlock(pos, false);

         for(Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
         }

      }
   }

   protected void checkTickOnNeighbor(final Level level, final BlockPos pos, final BlockState state) {
      if (!this.isLocked(level, pos, state)) {
         boolean on = (Boolean)state.getValue(POWERED);
         boolean shouldTurnOn = this.shouldTurnOn(level, pos, state);
         if (on != shouldTurnOn && !level.getBlockTicks().willTickThisTick(pos, this)) {
            TickPriority priority = TickPriority.HIGH;
            if (this.shouldPrioritize(level, pos, state)) {
               priority = TickPriority.EXTREMELY_HIGH;
            } else if (on) {
               priority = TickPriority.VERY_HIGH;
            }

            level.scheduleTick(pos, this, this.getDelay(state), priority);
         }

      }
   }

   public boolean isLocked(final LevelReader level, final BlockPos pos, final BlockState state) {
      return false;
   }

   protected boolean shouldTurnOn(final Level level, final BlockPos pos, final BlockState state) {
      return this.getInputSignal(level, pos, state) > 0;
   }

   protected int getInputSignal(final Level level, final BlockPos pos, final BlockState state) {
      Direction direction = (Direction)state.getValue(FACING);
      BlockPos targetPos = pos.relative(direction);
      int input = level.getSignal(targetPos, direction);
      if (input >= 15) {
         return input;
      } else {
         BlockState targetBlockState = level.getBlockState(targetPos);
         return Math.max(input, targetBlockState.is(Blocks.REDSTONE_WIRE) ? (Integer)targetBlockState.getValue(RedStoneWireBlock.POWER) : 0);
      }
   }

   protected int getAlternateSignal(final SignalGetter level, final BlockPos pos, final BlockState state) {
      Direction direction = (Direction)state.getValue(FACING);
      Direction clockWise = direction.getClockWise();
      Direction counterClockWise = direction.getCounterClockWise();
      boolean sideInputDiodesOnly = this.sideInputDiodesOnly();
      return Math.max(level.getControlInputSignal(pos.relative(clockWise), clockWise, sideInputDiodesOnly), level.getControlInputSignal(pos.relative(counterClockWise), counterClockWise, sideInputDiodesOnly));
   }

   protected boolean isSignalSource(final BlockState state) {
      return true;
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public void setPlacedBy(final Level level, final BlockPos pos, final BlockState state, final @Nullable LivingEntity by, final ItemStack itemStack) {
      if (this.shouldTurnOn(level, pos, state)) {
         level.scheduleTick(pos, this, 1);
      }

   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      this.updateNeighborsInFront(level, pos, state);
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if (!movedByPiston) {
         this.updateNeighborsInFront(level, pos, state);
      }

   }

   protected void updateNeighborsInFront(final Level level, final BlockPos pos, final BlockState state) {
      Direction direction = (Direction)state.getValue(FACING);
      BlockPos oppositePos = pos.relative(direction.getOpposite());
      Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, direction.getOpposite(), Direction.UP);
      level.neighborChanged(oppositePos, this, orientation);
      level.updateNeighborsAtExceptFromFacing(oppositePos, this, direction, orientation);
   }

   protected boolean sideInputDiodesOnly() {
      return false;
   }

   protected int getOutputSignal(final BlockGetter level, final BlockPos pos, final BlockState state) {
      return 15;
   }

   public static boolean isDiode(final BlockState state) {
      return state.getBlock() instanceof DiodeBlock;
   }

   public boolean shouldPrioritize(final BlockGetter level, final BlockPos pos, final BlockState state) {
      Direction direction = ((Direction)state.getValue(FACING)).getOpposite();
      BlockState oppositeState = level.getBlockState(pos.relative(direction));
      return isDiode(oppositeState) && oppositeState.getValue(FACING) != direction;
   }

   protected abstract int getDelay(BlockState state);

   static {
      POWERED = BlockStateProperties.POWERED;
      SHAPE = Block.column(16.0, 0.0, 2.0);
   }
}
