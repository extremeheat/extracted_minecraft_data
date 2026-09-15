package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.level.redstone.DefaultRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.ExperimentalRedstoneWireEvaluator;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.redstone.RedstoneWireEvaluator;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class RedstoneWireBlock extends Block {
   public static final EnumProperty<RedstoneSide> NORTH;
   public static final EnumProperty<RedstoneSide> EAST;
   public static final EnumProperty<RedstoneSide> SOUTH;
   public static final EnumProperty<RedstoneSide> WEST;
   public static final IntegerProperty POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;
   private static final int[] COLORS;
   private static final float PARTICLE_DENSITY = 0.2F;
   private final Function<BlockState, VoxelShape> shapes;
   private final BlockState crossState;
   private final RedstoneWireEvaluator evaluator = new DefaultRedstoneWireEvaluator(this);
   private boolean shouldSignal = true;

   public RedstoneWireBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, RedstoneSide.NONE)).setValue(EAST, RedstoneSide.NONE)).setValue(SOUTH, RedstoneSide.NONE)).setValue(WEST, RedstoneSide.NONE)).setValue(POWER, 0));
      this.shapes = this.makeShapes();
      this.crossState = (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE)).setValue(EAST, RedstoneSide.SIDE)).setValue(SOUTH, RedstoneSide.SIDE)).setValue(WEST, RedstoneSide.SIDE);
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      int height = 1;
      int width = 10;
      VoxelShape dot = Block.column(10.0, 0.0, 1.0);
      Map<Direction, VoxelShape> floor = Shapes.rotateHorizontal(Block.boxZ(10.0, 0.0, 1.0, 0.0, 8.0));
      Map<Direction, VoxelShape> up = Shapes.rotateHorizontal(Block.boxZ(10.0, 16.0, 0.0, 1.0));
      return this.getShapeForEachState((state) -> {
         VoxelShape shape = dot;

         for(Map.Entry<Direction, EnumProperty<RedstoneSide>> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            VoxelShape var10000;
            switch ((RedstoneSide)state.getValue((Property)entry.getValue())) {
               case UP -> var10000 = Shapes.or(shape, (VoxelShape)floor.get(entry.getKey()), (VoxelShape)up.get(entry.getKey()));
               case SIDE -> var10000 = Shapes.or(shape, (VoxelShape)floor.get(entry.getKey()));
               case NONE -> var10000 = shape;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            shape = var10000;
         }

         return shape;
      }, new Property[]{POWER});
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return (VoxelShape)this.shapes.apply(state);
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      return this.getConnectionState(context.getLevel(), this.crossState, context.getClickedPos());
   }

   private BlockState getConnectionState(final BlockGetter level, BlockState state, final BlockPos pos) {
      boolean wasDot = isDot(state);
      state = this.getMissingConnections(level, (BlockState)this.defaultBlockState().setValue(POWER, (Integer)state.getValue(POWER)), pos);
      if (wasDot && isDot(state)) {
         return state;
      } else {
         boolean north = ((RedstoneSide)state.getValue(NORTH)).isConnected();
         boolean south = ((RedstoneSide)state.getValue(SOUTH)).isConnected();
         boolean east = ((RedstoneSide)state.getValue(EAST)).isConnected();
         boolean west = ((RedstoneSide)state.getValue(WEST)).isConnected();
         boolean northSouthEmpty = !north && !south;
         boolean eastWestEmpty = !east && !west;
         if (!west && northSouthEmpty) {
            state = (BlockState)state.setValue(WEST, RedstoneSide.SIDE);
         }

         if (!east && northSouthEmpty) {
            state = (BlockState)state.setValue(EAST, RedstoneSide.SIDE);
         }

         if (!north && eastWestEmpty) {
            state = (BlockState)state.setValue(NORTH, RedstoneSide.SIDE);
         }

         if (!south && eastWestEmpty) {
            state = (BlockState)state.setValue(SOUTH, RedstoneSide.SIDE);
         }

         return state;
      }
   }

   private BlockState getMissingConnections(final BlockGetter level, BlockState state, final BlockPos pos) {
      boolean canConnectUp = !level.getBlockState(pos.above()).isRedstoneConductor(level, pos);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         if (!((RedstoneSide)state.getValue((Property)PROPERTY_BY_DIRECTION.get(direction))).isConnected()) {
            RedstoneSide sideConnection = this.getConnectingSide(level, pos, direction, canConnectUp);
            state = (BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(direction), sideConnection);
         }
      }

      return state;
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      if (directionToNeighbour == Direction.DOWN) {
         return !this.canSurviveOn(level, neighbourPos, neighbourState) ? Blocks.AIR.defaultBlockState() : state;
      } else if (directionToNeighbour == Direction.UP) {
         return this.getConnectionState(level, state, pos);
      } else {
         RedstoneSide sideConnection = this.getConnectingSide(level, pos, directionToNeighbour);
         return sideConnection.isConnected() == ((RedstoneSide)state.getValue((Property)PROPERTY_BY_DIRECTION.get(directionToNeighbour))).isConnected() && !isCross(state) ? (BlockState)state.setValue((Property)PROPERTY_BY_DIRECTION.get(directionToNeighbour), sideConnection) : this.getConnectionState(level, (BlockState)((BlockState)this.crossState.setValue(POWER, (Integer)state.getValue(POWER))).setValue((Property)PROPERTY_BY_DIRECTION.get(directionToNeighbour), sideConnection), pos);
      }
   }

   private static boolean isCross(final BlockState state) {
      return ((RedstoneSide)state.getValue(NORTH)).isConnected() && ((RedstoneSide)state.getValue(SOUTH)).isConnected() && ((RedstoneSide)state.getValue(EAST)).isConnected() && ((RedstoneSide)state.getValue(WEST)).isConnected();
   }

   private static boolean isDot(final BlockState state) {
      return !((RedstoneSide)state.getValue(NORTH)).isConnected() && !((RedstoneSide)state.getValue(SOUTH)).isConnected() && !((RedstoneSide)state.getValue(EAST)).isConnected() && !((RedstoneSide)state.getValue(WEST)).isConnected();
   }

   protected void updateIndirectNeighbourShapes(final BlockState state, final LevelAccessor level, final BlockPos pos, final @Block.UpdateFlags int updateFlags, final int updateLimit) {
      BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         RedstoneSide value = (RedstoneSide)state.getValue((Property)PROPERTY_BY_DIRECTION.get(direction));
         if (value != RedstoneSide.NONE && !level.getBlockState(blockPos.setWithOffset(pos, (Direction)direction)).is(this)) {
            blockPos.move(Direction.DOWN);
            BlockState blockStateDown = level.getBlockState(blockPos);
            if (blockStateDown.is(this)) {
               BlockPos neighborPos = blockPos.relative(direction.getOpposite());
               level.neighborShapeChanged(direction.getOpposite(), blockPos, neighborPos, level.getBlockState(neighborPos), updateFlags, updateLimit);
            }

            blockPos.setWithOffset(pos, (Direction)direction).move(Direction.UP);
            BlockState blockStateUp = level.getBlockState(blockPos);
            if (blockStateUp.is(this)) {
               BlockPos neighborPos = blockPos.relative(direction.getOpposite());
               level.neighborShapeChanged(direction.getOpposite(), blockPos, neighborPos, level.getBlockState(neighborPos), updateFlags, updateLimit);
            }
         }
      }

   }

   private RedstoneSide getConnectingSide(final BlockGetter level, final BlockPos pos, final Direction direction) {
      return this.getConnectingSide(level, pos, direction, !level.getBlockState(pos.above()).isRedstoneConductor(level, pos));
   }

   private RedstoneSide getConnectingSide(final BlockGetter level, final BlockPos pos, final Direction direction, final boolean canConnectUp) {
      BlockPos relativePos = pos.relative(direction);
      BlockState relativeState = level.getBlockState(relativePos);
      if (canConnectUp) {
         boolean isPlaceableAbove = relativeState.getBlock() instanceof TrapDoorBlock || this.canSurviveOn(level, relativePos, relativeState);
         if (isPlaceableAbove && shouldConnectTo(level, relativePos.above())) {
            if (relativeState.isFaceSturdy(level, relativePos, direction.getOpposite())) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !shouldConnectTo(relativeState, level, relativePos, direction) && (relativeState.isRedstoneConductor(level, relativePos) || !shouldConnectTo(level, relativePos.below())) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      BlockPos below = pos.below();
      BlockState belowState = level.getBlockState(below);
      return this.canSurviveOn(level, below, belowState);
   }

   private boolean canSurviveOn(final BlockGetter level, final BlockPos relativePos, final BlockState relativeState) {
      return relativeState.isFaceSturdy(level, relativePos, Direction.UP) || relativeState.is(Blocks.HOPPER);
   }

   private void updatePowerStrength(final Level level, final BlockPos pos, final BlockState state, final @Nullable Orientation orientation, final boolean shapeUpdateWiresAroundInitialPosition) {
      if (useExperimentalEvaluator(level)) {
         (new ExperimentalRedstoneWireEvaluator(this)).updatePowerStrength(level, pos, state, orientation, shapeUpdateWiresAroundInitialPosition);
      } else {
         this.evaluator.updatePowerStrength(level, pos, state, orientation, shapeUpdateWiresAroundInitialPosition);
      }

   }

   public int getBlockSignal(final Level level, final BlockPos pos) {
      this.shouldSignal = false;
      int blockSignal = level.getBestNeighborSignal(pos);
      this.shouldSignal = true;
      return blockSignal;
   }

   private void checkCornerChangeAt(final Level level, final BlockPos pos) {
      if (level.getBlockState(pos).is(this)) {
         level.updateNeighborsAt(pos, this);

         for(Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
         }

      }
   }

   protected void onPlace(final BlockState state, final Level level, final BlockPos pos, final BlockState oldState, final boolean movedByPiston) {
      if (!oldState.is(state.getBlock()) && !level.isClientSide()) {
         this.updatePowerStrength(level, pos, state, (Orientation)null, true);

         for(Direction direction : Direction.Plane.VERTICAL) {
            level.updateNeighborsAt(pos.relative(direction), this);
         }

         this.updateNeighborsOfNeighboringWires(level, pos);
      }
   }

   protected void affectNeighborsAfterRemoval(final BlockState state, final ServerLevel level, final BlockPos pos, final boolean movedByPiston) {
      if (!movedByPiston) {
         for(Direction direction : Direction.values()) {
            level.updateNeighborsAt(pos.relative(direction), this);
         }

         this.updatePowerStrength(level, pos, state, (Orientation)null, false);
         this.updateNeighborsOfNeighboringWires(level, pos);
      }
   }

   private void updateNeighborsOfNeighboringWires(final Level level, final BlockPos pos) {
      for(Direction direction : Direction.Plane.HORIZONTAL) {
         this.checkCornerChangeAt(level, pos.relative(direction));
      }

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos target = pos.relative(direction);
         if (level.getBlockState(target).isRedstoneConductor(level, target)) {
            this.checkCornerChangeAt(level, target.above());
         } else {
            this.checkCornerChangeAt(level, target.below());
         }
      }

   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      if (!level.isClientSide()) {
         if (block != this || !useExperimentalEvaluator(level)) {
            if (state.canSurvive(level, pos)) {
               this.updatePowerStrength(level, pos, state, orientation, false);
            } else {
               dropResources(state, level, pos);
               level.removeBlock(pos, false);
            }

         }
      }
   }

   private static boolean useExperimentalEvaluator(final Level level) {
      return level.enabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS);
   }

   protected int getDirectSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      return !this.shouldSignal ? 0 : state.getSignal(level, pos, direction);
   }

   protected int getSignal(final BlockState state, final BlockGetter level, final BlockPos pos, final Direction direction) {
      if (this.shouldSignal && direction != Direction.DOWN) {
         int power = this.ownSignal(state, level, pos);
         if (power == 0) {
            return 0;
         } else {
            return direction != Direction.UP && !((RedstoneSide)this.getConnectionState(level, state, pos).getValue((Property)PROPERTY_BY_DIRECTION.get(direction.getOpposite()))).isConnected() ? 0 : power;
         }
      } else {
         return 0;
      }
   }

   protected int ownSignal(final BlockState state, final BlockGetter level, final BlockPos pos) {
      return (Integer)state.getValue(POWER);
   }

   protected boolean shouldRedstoneWireConnectTo(final BlockState state, final BlockGetter level, final BlockPos pos, final @Nullable Direction direction) {
      return true;
   }

   protected static boolean shouldConnectTo(final BlockGetter level, final BlockPos pos) {
      return shouldConnectTo(level.getBlockState(pos), level, pos, (Direction)null);
   }

   protected static boolean shouldConnectTo(final BlockState state, final BlockGetter level, final BlockPos pos, final @Nullable Direction direction) {
      return state.shouldRedstoneWireConnectTo(level, pos, direction);
   }

   protected boolean isSignalSource(final BlockState state) {
      return this.shouldSignal;
   }

   public static int getColorForPower(final int power) {
      return COLORS[power];
   }

   private static void spawnParticlesAlongLine(final Level level, final RandomSource random, final BlockPos pos, final int color, final Direction side, final Direction along, final float from, final float to) {
      float span = to - from;
      if (!(random.nextFloat() >= 0.2F * span)) {
         float sideOfBlock = 0.4375F;
         float positionOnLine = from + span * random.nextFloat();
         double x = 0.5 + (double)(0.4375F * (float)side.getStepX()) + (double)(positionOnLine * (float)along.getStepX());
         double y = 0.5 + (double)(0.4375F * (float)side.getStepY()) + (double)(positionOnLine * (float)along.getStepY());
         double z = 0.5 + (double)(0.4375F * (float)side.getStepZ()) + (double)(positionOnLine * (float)along.getStepZ());
         level.addParticle(new DustParticleOptions(color, 1.0F), (double)pos.getX() + x, (double)pos.getY() + y, (double)pos.getZ() + z, 0.0, 0.0, 0.0);
      }
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      int power = (Integer)state.getValue(POWER);
      if (power != 0) {
         for(Direction horizontal : Direction.Plane.HORIZONTAL) {
            RedstoneSide connection = (RedstoneSide)state.getValue((Property)PROPERTY_BY_DIRECTION.get(horizontal));
            switch (connection) {
               case UP:
                  spawnParticlesAlongLine(level, random, pos, COLORS[power], horizontal, Direction.UP, -0.5F, 0.5F);
               case SIDE:
                  spawnParticlesAlongLine(level, random, pos, COLORS[power], Direction.DOWN, horizontal, 0.0F, 0.5F);
                  break;
               case NONE:
               default:
                  spawnParticlesAlongLine(level, random, pos, COLORS[power], Direction.DOWN, horizontal, 0.0F, 0.3F);
            }
         }

      }
   }

   protected BlockState rotate(final BlockState state, final Rotation rotation) {
      BlockState var10000;
      switch (rotation) {
         case CLOCKWISE_180 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (RedstoneSide)state.getValue(SOUTH))).setValue(EAST, (RedstoneSide)state.getValue(WEST))).setValue(SOUTH, (RedstoneSide)state.getValue(NORTH))).setValue(WEST, (RedstoneSide)state.getValue(EAST));
         case COUNTERCLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (RedstoneSide)state.getValue(EAST))).setValue(EAST, (RedstoneSide)state.getValue(SOUTH))).setValue(SOUTH, (RedstoneSide)state.getValue(WEST))).setValue(WEST, (RedstoneSide)state.getValue(NORTH));
         case CLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)state.setValue(NORTH, (RedstoneSide)state.getValue(WEST))).setValue(EAST, (RedstoneSide)state.getValue(NORTH))).setValue(SOUTH, (RedstoneSide)state.getValue(EAST))).setValue(WEST, (RedstoneSide)state.getValue(SOUTH));
         default -> var10000 = state;
      }

      return var10000;
   }

   protected BlockState mirror(final BlockState state, final Mirror mirror) {
      switch (mirror) {
         case LEFT_RIGHT -> {
            return (BlockState)((BlockState)state.setValue(NORTH, (RedstoneSide)state.getValue(SOUTH))).setValue(SOUTH, (RedstoneSide)state.getValue(NORTH));
         }
         case FRONT_BACK -> {
            return (BlockState)((BlockState)state.setValue(EAST, (RedstoneSide)state.getValue(WEST))).setValue(WEST, (RedstoneSide)state.getValue(EAST));
         }
         default -> {
            return super.mirror(state, mirror);
         }
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      if (!player.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         if (isCross(state) || isDot(state)) {
            BlockState newState = isCross(state) ? this.defaultBlockState() : this.crossState;
            newState = (BlockState)newState.setValue(POWER, (Integer)state.getValue(POWER));
            newState = this.getConnectionState(level, newState, pos);
            if (newState != state) {
               level.setBlockAndUpdate(pos, newState);
               this.updatesOnShapeChange(level, pos, state, newState);
               return InteractionResult.SUCCESS;
            }
         }

         return InteractionResult.PASS;
      }
   }

   private void updatesOnShapeChange(final Level level, final BlockPos pos, final BlockState oldState, final BlockState newState) {
      Orientation orientation = ExperimentalRedstoneUtils.initialOrientation(level, (Direction)null, Direction.UP);

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos relativePos = pos.relative(direction);
         if (((RedstoneSide)oldState.getValue((Property)PROPERTY_BY_DIRECTION.get(direction))).isConnected() != ((RedstoneSide)newState.getValue((Property)PROPERTY_BY_DIRECTION.get(direction))).isConnected() && level.getBlockState(relativePos).isRedstoneConductor(level, relativePos)) {
            level.updateNeighborsAtExceptFromFacing(relativePos, newState.getBlock(), direction.getOpposite(), ExperimentalRedstoneUtils.withFront(orientation, direction));
         }
      }

   }

   static {
      NORTH = BlockStateProperties.NORTH_REDSTONE;
      EAST = BlockStateProperties.EAST_REDSTONE;
      SOUTH = BlockStateProperties.SOUTH_REDSTONE;
      WEST = BlockStateProperties.WEST_REDSTONE;
      POWER = BlockStateProperties.POWER;
      PROPERTY_BY_DIRECTION = ImmutableMap.copyOf(Maps.newEnumMap(Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST)));
      COLORS = (int[])Util.make(new int[16], (list) -> {
         for(int i = 0; i <= 15; ++i) {
            float power = (float)i / 15.0F;
            float red = power * 0.6F + (power > 0.0F ? 0.4F : 0.3F);
            float green = Mth.clamp(power * power * 0.7F - 0.5F, 0.0F, 1.0F);
            float blue = Mth.clamp(power * power * 0.6F - 0.7F, 0.0F, 1.0F);
            list[i] = ARGB.colorFromFloat(1.0F, red, green, blue);
         }

      });
   }
}
