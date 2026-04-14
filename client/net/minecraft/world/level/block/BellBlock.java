package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class BellBlock extends BaseEntityBlock {
   public static final MapCodec<BellBlock> CODEC = simpleCodec(BellBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final EnumProperty<BellAttachType> ATTACHMENT;
   public static final BooleanProperty POWERED;
   private static final VoxelShape BELL_SHAPE;
   private static final VoxelShape SHAPE_CEILING;
   private static final Map<Direction.Axis, VoxelShape> SHAPE_FLOOR;
   private static final Map<Direction.Axis, VoxelShape> SHAPE_DOUBLE_WALL;
   private static final Map<Direction, VoxelShape> SHAPE_SINGLE_WALL;
   public static final int EVENT_BELL_RING = 1;

   public MapCodec<BellBlock> codec() {
      return CODEC;
   }

   public BellBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(ATTACHMENT, BellAttachType.FLOOR)).setValue(POWERED, false));
   }

   protected void neighborChanged(final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston) {
      boolean signal = level.hasNeighborSignal(pos);
      if (signal != (Boolean)state.getValue(POWERED)) {
         if (signal) {
            this.attemptToRing(level, pos, (Direction)null);
         }

         level.setBlock(pos, (BlockState)state.setValue(POWERED, signal), 3);
      }

   }

   protected void onProjectileHit(final Level level, final BlockState state, final BlockHitResult hitResult, final Projectile projectile) {
      Entity owner = projectile.getOwner();
      Player var10000;
      if (owner instanceof Player player) {
         var10000 = player;
      } else {
         var10000 = null;
      }

      Player playerOwner = var10000;
      this.onHit(level, state, hitResult, playerOwner, true);
   }

   protected InteractionResult useWithoutItem(final BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
      return (InteractionResult)(this.onHit(level, state, hitResult, player, true) ? InteractionResult.SUCCESS : InteractionResult.PASS);
   }

   public boolean onHit(final Level level, final BlockState state, final BlockHitResult hitResult, final @Nullable Player player, final boolean requireHitFromCorrectSide) {
      Direction direction = hitResult.getDirection();
      BlockPos blockPos = hitResult.getBlockPos();
      boolean properHit = !requireHitFromCorrectSide || this.isProperHit(state, direction, hitResult.getLocation().y - (double)blockPos.getY());
      if (properHit) {
         boolean didRing = this.attemptToRing(player, level, blockPos, direction);
         if (didRing && player != null) {
            player.awardStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isProperHit(final BlockState state, final Direction clickedDirection, final double clickY) {
      if (clickedDirection.getAxis() != Direction.Axis.Y && !(clickY > 0.8123999834060669)) {
         Direction facing = (Direction)state.getValue(FACING);
         BellAttachType attachType = (BellAttachType)state.getValue(ATTACHMENT);
         switch (attachType) {
            case FLOOR:
               return facing.getAxis() == clickedDirection.getAxis();
            case SINGLE_WALL:
            case DOUBLE_WALL:
               return facing.getAxis() != clickedDirection.getAxis();
            case CEILING:
               return true;
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   public boolean attemptToRing(final Level level, final BlockPos pos, final @Nullable Direction direction) {
      return this.attemptToRing((Entity)null, level, pos, direction);
   }

   public boolean attemptToRing(final @Nullable Entity ringingEntity, final Level level, final BlockPos pos, @Nullable Direction direction) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      if (!level.isClientSide() && blockEntity instanceof BellBlockEntity) {
         if (direction == null) {
            direction = (Direction)level.getBlockState(pos).getValue(FACING);
         }

         ((BellBlockEntity)blockEntity).onHit(direction);
         level.playSound((Entity)null, (BlockPos)pos, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
         level.gameEvent(ringingEntity, GameEvent.BLOCK_CHANGE, pos);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShape getVoxelShape(final BlockState state) {
      Direction facing = (Direction)state.getValue(FACING);
      VoxelShape var10000;
      switch ((BellAttachType)state.getValue(ATTACHMENT)) {
         case FLOOR -> var10000 = (VoxelShape)SHAPE_FLOOR.get(facing.getAxis());
         case SINGLE_WALL -> var10000 = (VoxelShape)SHAPE_SINGLE_WALL.get(facing);
         case DOUBLE_WALL -> var10000 = (VoxelShape)SHAPE_DOUBLE_WALL.get(facing.getAxis());
         case CEILING -> var10000 = SHAPE_CEILING;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   protected VoxelShape getCollisionShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return this.getVoxelShape(state);
   }

   protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
      return this.getVoxelShape(state);
   }

   public @Nullable BlockState getStateForPlacement(final BlockPlaceContext context) {
      Direction clickedFace = context.getClickedFace();
      BlockPos pos = context.getClickedPos();
      Level level = context.getLevel();
      Direction.Axis axis = clickedFace.getAxis();
      if (axis == Direction.Axis.Y) {
         BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHMENT, clickedFace == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR)).setValue(FACING, context.getHorizontalDirection());
         if (state.canSurvive(context.getLevel(), pos)) {
            return state;
         }
      } else {
         boolean doubleAttached = axis == Direction.Axis.X && level.getBlockState(pos.west()).isFaceSturdy(level, pos.west(), Direction.EAST) && level.getBlockState(pos.east()).isFaceSturdy(level, pos.east(), Direction.WEST) || axis == Direction.Axis.Z && level.getBlockState(pos.north()).isFaceSturdy(level, pos.north(), Direction.SOUTH) && level.getBlockState(pos.south()).isFaceSturdy(level, pos.south(), Direction.NORTH);
         BlockState state = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, clickedFace.getOpposite())).setValue(ATTACHMENT, doubleAttached ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
         if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
            return state;
         }

         boolean canAttachBelow = level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
         state = (BlockState)state.setValue(ATTACHMENT, canAttachBelow ? BellAttachType.FLOOR : BellAttachType.CEILING);
         if (state.canSurvive(context.getLevel(), context.getClickedPos())) {
            return state;
         }
      }

      return null;
   }

   protected void onExplosionHit(final BlockState state, final ServerLevel level, final BlockPos pos, final Explosion explosion, final BiConsumer<ItemStack, BlockPos> onHit) {
      if (explosion.canTriggerBlocks()) {
         this.attemptToRing(level, pos, (Direction)null);
      }

      super.onExplosionHit(state, level, pos, explosion, onHit);
   }

   protected BlockState updateShape(final BlockState state, final LevelReader level, final ScheduledTickAccess ticks, final BlockPos pos, final Direction directionToNeighbour, final BlockPos neighbourPos, final BlockState neighbourState, final RandomSource random) {
      BellAttachType attachment = (BellAttachType)state.getValue(ATTACHMENT);
      Direction connectedDirection = getConnectedDirection(state).getOpposite();
      if (connectedDirection == directionToNeighbour && !state.canSurvive(level, pos) && attachment != BellAttachType.DOUBLE_WALL) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (directionToNeighbour.getAxis() == ((Direction)state.getValue(FACING)).getAxis()) {
            if (attachment == BellAttachType.DOUBLE_WALL && !neighbourState.isFaceSturdy(level, neighbourPos, directionToNeighbour)) {
               return (BlockState)((BlockState)state.setValue(ATTACHMENT, BellAttachType.SINGLE_WALL)).setValue(FACING, directionToNeighbour.getOpposite());
            }

            if (attachment == BellAttachType.SINGLE_WALL && connectedDirection.getOpposite() == directionToNeighbour && neighbourState.isFaceSturdy(level, neighbourPos, (Direction)state.getValue(FACING))) {
               return (BlockState)state.setValue(ATTACHMENT, BellAttachType.DOUBLE_WALL);
            }
         }

         return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
      }
   }

   protected boolean canSurvive(final BlockState state, final LevelReader level, final BlockPos pos) {
      Direction connectionDir = getConnectedDirection(state).getOpposite();
      return connectionDir == Direction.UP ? Block.canSupportCenter(level, pos.above(), Direction.DOWN) : FaceAttachedHorizontalDirectionalBlock.canAttach(level, pos, connectionDir);
   }

   private static Direction getConnectedDirection(final BlockState state) {
      switch ((BellAttachType)state.getValue(ATTACHMENT)) {
         case FLOOR -> {
            return Direction.UP;
         }
         case CEILING -> {
            return Direction.DOWN;
         }
         default -> {
            return ((Direction)state.getValue(FACING)).getOpposite();
         }
      }
   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING, ATTACHMENT, POWERED);
   }

   public @Nullable BlockEntity newBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      return new BellBlockEntity(worldPosition, blockState);
   }

   public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(final Level level, final BlockState blockState, final BlockEntityType<T> type) {
      return createTickerHelper(type, BlockEntityTypes.BELL, level.isClientSide() ? BellBlockEntity::clientTick : BellBlockEntity::serverTick);
   }

   protected boolean isPathfindable(final BlockState state, final PathComputationType type) {
      return false;
   }

   public BlockState rotate(final BlockState state, final Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(final BlockState state, final Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
      POWERED = BlockStateProperties.POWERED;
      BELL_SHAPE = Shapes.or(Block.column(6.0, 6.0, 13.0), Block.column(8.0, 4.0, 6.0));
      SHAPE_CEILING = Shapes.or(BELL_SHAPE, Block.column(2.0, 13.0, 16.0));
      SHAPE_FLOOR = Shapes.rotateHorizontalAxis(Block.cube(16.0, 16.0, 8.0));
      SHAPE_DOUBLE_WALL = Shapes.rotateHorizontalAxis(Shapes.or(BELL_SHAPE, Block.column(2.0, 16.0, 13.0, 15.0)));
      SHAPE_SINGLE_WALL = Shapes.rotateHorizontal(Shapes.or(BELL_SHAPE, Block.boxZ(2.0, 13.0, 15.0, 0.0, 13.0)));
   }
}
