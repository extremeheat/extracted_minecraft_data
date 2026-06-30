package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public abstract class BaseRailBlock extends Block implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED;
   private static final VoxelShape SHAPE_FLAT;
   private static final VoxelShape SHAPE_SLOPE;
   private final boolean isStraight;

   public static boolean isRail(final Level level, final BlockPos pos) {
      return isRail(level.getBlockState(pos));
   }

   public static boolean isRail(final BlockState state) {
      return state.is(BlockTags.RAILS) && state.getBlock() instanceof BaseRailBlock;
   }

   protected BaseRailBlock(final boolean isStraight, final BlockBehaviour.Properties properties) {
      super(properties);
      this.isStraight = isStraight;
   }

   public boolean isStraight() {
      return this.isStraight;
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return ((RailShape)state.getValue(this.getShapeProperty())).isSlope() ? SHAPE_SLOPE : SHAPE_FLAT;
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      return canSupportRigidBlock(level, pos.below());
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!oldState.is(state.getBlock())) {
         this.updateState(state, level, pos, movedByPiston);
      }
   }

   protected BlockState updateState(BlockState state, final Level level, final BlockPos pos, final boolean movedByPiston) {
      state = this.updateDir(level, pos, state, true);
      if (this.isStraight) {
         level.neighborChanged(state, pos, this, (Orientation)null, movedByPiston);
      }

      return state;
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (!level.isClientSide() && level.getBlockState(pos).is(this)) {
         RailShape shape = (RailShape)state.getValue(this.getShapeProperty());
         if (shouldBeRemoved(pos, level, shape)) {
            dropResources(state, level, pos);
            level.removeBlock(pos, movedByPiston);
         } else {
            this.updateState(state, level, pos, block);
         }

      }
   }

   private static boolean shouldBeRemoved(final BlockPos pos, final Level level, final RailShape shape) {
      if (!canSupportRigidBlock(level, pos.below())) {
         return true;
      } else {
         boolean var10000;
         switch (shape) {
            case ASCENDING_EAST -> var10000 = !canSupportRigidBlock(level, pos.east());
            case ASCENDING_WEST -> var10000 = !canSupportRigidBlock(level, pos.west());
            case ASCENDING_NORTH -> var10000 = !canSupportRigidBlock(level, pos.north());
            case ASCENDING_SOUTH -> var10000 = !canSupportRigidBlock(level, pos.south());
            default -> var10000 = false;
         }

         return var10000;
      }
   }

   protected void updateState(final BlockState state, final Level level, final BlockPos pos, final Block block) {
   }

   protected BlockState updateDir(final Level level, final BlockPos pos, final BlockState state, final boolean first) {
      if (level.isClientSide()) {
         return state;
      } else {
         RailShape current = (RailShape)state.getValue(this.getShapeProperty());
         return (new RailState(level, pos, state)).place(level.hasNeighborSignal(pos), first, current).getState();
      }
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if (!movedByPiston) {
         if (((RailShape)state.getValue(this.getShapeProperty())).isSlope()) {
            level.updateNeighborsAt(pos.above(), this);
         }

         if (this.isStraight) {
            level.updateNeighborsAt(pos, this);
            level.updateNeighborsAt(pos.below(), this);
         }

      }
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      FluidState replacedFluidState = context.getLevel().getFluidState(context.getClickedPos());
      boolean isWaterSource = replacedFluidState.is(Fluids.WATER);
      BlockState state = super.defaultBlockState();
      Direction direction = context.getHorizontalDirection();
      boolean isEastWest = direction == Direction.EAST || direction == Direction.WEST;
      return (BlockState)((BlockState)state.setValue(this.getShapeProperty(), isEastWest ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, isWaterSource);
   }

   public abstract Property<RailShape> getShapeProperty();

   protected RailShape rotate(final RailShape shape, final Rotation rotation) {
      RailShape var10000;
      switch (rotation) {
         case CLOCKWISE_180:
            switch (shape) {
               case ASCENDING_EAST:
                  var10000 = RailShape.ASCENDING_WEST;
                  return var10000;
               case ASCENDING_WEST:
                  var10000 = RailShape.ASCENDING_EAST;
                  return var10000;
               case ASCENDING_NORTH:
                  var10000 = RailShape.ASCENDING_SOUTH;
                  return var10000;
               case ASCENDING_SOUTH:
                  var10000 = RailShape.ASCENDING_NORTH;
                  return var10000;
               case NORTH_SOUTH:
                  var10000 = RailShape.NORTH_SOUTH;
                  return var10000;
               case EAST_WEST:
                  var10000 = RailShape.EAST_WEST;
                  return var10000;
               case SOUTH_EAST:
                  var10000 = RailShape.NORTH_WEST;
                  return var10000;
               case SOUTH_WEST:
                  var10000 = RailShape.NORTH_EAST;
                  return var10000;
               case NORTH_WEST:
                  var10000 = RailShape.SOUTH_EAST;
                  return var10000;
               case NORTH_EAST:
                  var10000 = RailShape.SOUTH_WEST;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case COUNTERCLOCKWISE_90:
            switch (shape) {
               case ASCENDING_EAST:
                  var10000 = RailShape.ASCENDING_NORTH;
                  return var10000;
               case ASCENDING_WEST:
                  var10000 = RailShape.ASCENDING_SOUTH;
                  return var10000;
               case ASCENDING_NORTH:
                  var10000 = RailShape.ASCENDING_WEST;
                  return var10000;
               case ASCENDING_SOUTH:
                  var10000 = RailShape.ASCENDING_EAST;
                  return var10000;
               case NORTH_SOUTH:
                  var10000 = RailShape.EAST_WEST;
                  return var10000;
               case EAST_WEST:
                  var10000 = RailShape.NORTH_SOUTH;
                  return var10000;
               case SOUTH_EAST:
                  var10000 = RailShape.NORTH_EAST;
                  return var10000;
               case SOUTH_WEST:
                  var10000 = RailShape.SOUTH_EAST;
                  return var10000;
               case NORTH_WEST:
                  var10000 = RailShape.SOUTH_WEST;
                  return var10000;
               case NORTH_EAST:
                  var10000 = RailShape.NORTH_WEST;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         case CLOCKWISE_90:
            switch (shape) {
               case ASCENDING_EAST:
                  var10000 = RailShape.ASCENDING_SOUTH;
                  return var10000;
               case ASCENDING_WEST:
                  var10000 = RailShape.ASCENDING_NORTH;
                  return var10000;
               case ASCENDING_NORTH:
                  var10000 = RailShape.ASCENDING_EAST;
                  return var10000;
               case ASCENDING_SOUTH:
                  var10000 = RailShape.ASCENDING_WEST;
                  return var10000;
               case NORTH_SOUTH:
                  var10000 = RailShape.EAST_WEST;
                  return var10000;
               case EAST_WEST:
                  var10000 = RailShape.NORTH_SOUTH;
                  return var10000;
               case SOUTH_EAST:
                  var10000 = RailShape.SOUTH_WEST;
                  return var10000;
               case SOUTH_WEST:
                  var10000 = RailShape.NORTH_WEST;
                  return var10000;
               case NORTH_WEST:
                  var10000 = RailShape.NORTH_EAST;
                  return var10000;
               case NORTH_EAST:
                  var10000 = RailShape.SOUTH_EAST;
                  return var10000;
               default:
                  throw new MatchException((String)null, (Throwable)null);
            }
         default:
            var10000 = shape;
            return var10000;
      }
   }

   protected RailShape mirror(final RailShape shape, final Mirror mirror) {
      RailShape var10000;
      switch (mirror) {
         case LEFT_RIGHT:
            switch (shape) {
               case ASCENDING_NORTH:
                  var10000 = RailShape.ASCENDING_SOUTH;
                  return var10000;
               case ASCENDING_SOUTH:
                  var10000 = RailShape.ASCENDING_NORTH;
                  return var10000;
               case NORTH_SOUTH:
               case EAST_WEST:
               default:
                  var10000 = shape;
                  return var10000;
               case SOUTH_EAST:
                  var10000 = RailShape.NORTH_EAST;
                  return var10000;
               case SOUTH_WEST:
                  var10000 = RailShape.NORTH_WEST;
                  return var10000;
               case NORTH_WEST:
                  var10000 = RailShape.SOUTH_WEST;
                  return var10000;
               case NORTH_EAST:
                  var10000 = RailShape.SOUTH_EAST;
                  return var10000;
            }
         case FRONT_BACK:
            switch (shape) {
               case ASCENDING_EAST:
                  var10000 = RailShape.ASCENDING_WEST;
                  return var10000;
               case ASCENDING_WEST:
                  var10000 = RailShape.ASCENDING_EAST;
                  return var10000;
               case ASCENDING_NORTH:
               case ASCENDING_SOUTH:
               case NORTH_SOUTH:
               case EAST_WEST:
               default:
                  var10000 = shape;
                  return var10000;
               case SOUTH_EAST:
                  var10000 = RailShape.SOUTH_WEST;
                  return var10000;
               case SOUTH_WEST:
                  var10000 = RailShape.SOUTH_EAST;
                  return var10000;
               case NORTH_WEST:
                  var10000 = RailShape.NORTH_EAST;
                  return var10000;
               case NORTH_EAST:
                  var10000 = RailShape.NORTH_WEST;
                  return var10000;
            }
         default:
            var10000 = shape;
            return var10000;
      }
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if ((Boolean)state.getValue(WATERLOGGED)) {
         ticks.scheduleTick(pos, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
   }

   protected FluidState getFluidState(final BlockState state) {
      return (Boolean)state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
      SHAPE_FLAT = Block.column(16.0, 0.0, 2.0);
      SHAPE_SLOPE = Block.column(16.0, 0.0, 8.0);
   }
}
