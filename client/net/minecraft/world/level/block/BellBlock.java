package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BellBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<BellAttachType> ATTACHMENT;
   public static final BooleanProperty POWERED;
   private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE;
   private static final VoxelShape EAST_WEST_FLOOR_SHAPE;
   private static final VoxelShape BELL_TOP_SHAPE;
   private static final VoxelShape BELL_BOTTOM_SHAPE;
   private static final VoxelShape BELL_SHAPE;
   private static final VoxelShape NORTH_SOUTH_BETWEEN;
   private static final VoxelShape EAST_WEST_BETWEEN;
   private static final VoxelShape TO_WEST;
   private static final VoxelShape TO_EAST;
   private static final VoxelShape TO_NORTH;
   private static final VoxelShape TO_SOUTH;
   private static final VoxelShape CEILING_SHAPE;
   public static final int EVENT_BELL_RING = 1;

   public BellBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(ATTACHMENT, BellAttachType.FLOOR)).setValue(POWERED, false));
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3);
      if (var7 != (Boolean)var1.getValue(POWERED)) {
         if (var7) {
            this.attemptToRing(var2, var3, (Direction)null);
         }

         var2.setBlock(var3, (BlockState)var1.setValue(POWERED, var7), 3);
      }

   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      Entity var5 = var4.getOwner();
      Player var6 = var5 instanceof Player ? (Player)var5 : null;
      this.onHit(var1, var2, var3, var6, true);
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      return this.onHit(var2, var1, var6, var4, true) ? InteractionResult.sidedSuccess(var2.isClientSide) : InteractionResult.PASS;
   }

   public boolean onHit(Level var1, BlockState var2, BlockHitResult var3, @Nullable Player var4, boolean var5) {
      Direction var6 = var3.getDirection();
      BlockPos var7 = var3.getBlockPos();
      boolean var8 = !var5 || this.isProperHit(var2, var6, var3.getLocation().field_415 - (double)var7.getY());
      if (var8) {
         boolean var9 = this.attemptToRing(var4, var1, var7, var6);
         if (var9 && var4 != null) {
            var4.awardStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean isProperHit(BlockState var1, Direction var2, double var3) {
      if (var2.getAxis() != Direction.Axis.field_501 && !(var3 > 0.8123999834060669D)) {
         Direction var5 = (Direction)var1.getValue(FACING);
         BellAttachType var6 = (BellAttachType)var1.getValue(ATTACHMENT);
         switch(var6) {
         case FLOOR:
            return var5.getAxis() == var2.getAxis();
         case SINGLE_WALL:
         case DOUBLE_WALL:
            return var5.getAxis() != var2.getAxis();
         case CEILING:
            return true;
         default:
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean attemptToRing(Level var1, BlockPos var2, @Nullable Direction var3) {
      return this.attemptToRing((Entity)null, var1, var2, var3);
   }

   public boolean attemptToRing(@Nullable Entity var1, Level var2, BlockPos var3, @Nullable Direction var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (!var2.isClientSide && var5 instanceof BellBlockEntity) {
         if (var4 == null) {
            var4 = (Direction)var2.getBlockState(var3).getValue(FACING);
         }

         ((BellBlockEntity)var5).onHit(var4);
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
         var2.gameEvent(var1, GameEvent.RING_BELL, var3);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShape getVoxelShape(BlockState var1) {
      Direction var2 = (Direction)var1.getValue(FACING);
      BellAttachType var3 = (BellAttachType)var1.getValue(ATTACHMENT);
      if (var3 == BellAttachType.FLOOR) {
         return var2 != Direction.NORTH && var2 != Direction.SOUTH ? EAST_WEST_FLOOR_SHAPE : NORTH_SOUTH_FLOOR_SHAPE;
      } else if (var3 == BellAttachType.CEILING) {
         return CEILING_SHAPE;
      } else if (var3 == BellAttachType.DOUBLE_WALL) {
         return var2 != Direction.NORTH && var2 != Direction.SOUTH ? EAST_WEST_BETWEEN : NORTH_SOUTH_BETWEEN;
      } else if (var2 == Direction.NORTH) {
         return TO_NORTH;
      } else if (var2 == Direction.SOUTH) {
         return TO_SOUTH;
      } else {
         return var2 == Direction.EAST ? TO_EAST : TO_WEST;
      }
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var3 = var1.getClickedFace();
      BlockPos var4 = var1.getClickedPos();
      Level var5 = var1.getLevel();
      Direction.Axis var6 = var3.getAxis();
      BlockState var2;
      if (var6 == Direction.Axis.field_501) {
         var2 = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHMENT, var3 == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR)).setValue(FACING, var1.getHorizontalDirection());
         if (var2.canSurvive(var1.getLevel(), var4)) {
            return var2;
         }
      } else {
         boolean var7 = var6 == Direction.Axis.field_500 && var5.getBlockState(var4.west()).isFaceSturdy(var5, var4.west(), Direction.EAST) && var5.getBlockState(var4.east()).isFaceSturdy(var5, var4.east(), Direction.WEST) || var6 == Direction.Axis.field_502 && var5.getBlockState(var4.north()).isFaceSturdy(var5, var4.north(), Direction.SOUTH) && var5.getBlockState(var4.south()).isFaceSturdy(var5, var4.south(), Direction.NORTH);
         var2 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var3.getOpposite())).setValue(ATTACHMENT, var7 ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
         if (var2.canSurvive(var1.getLevel(), var1.getClickedPos())) {
            return var2;
         }

         boolean var8 = var5.getBlockState(var4.below()).isFaceSturdy(var5, var4.below(), Direction.field_526);
         var2 = (BlockState)var2.setValue(ATTACHMENT, var8 ? BellAttachType.FLOOR : BellAttachType.CEILING);
         if (var2.canSurvive(var1.getLevel(), var1.getClickedPos())) {
            return var2;
         }
      }

      return null;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      BellAttachType var7 = (BellAttachType)var1.getValue(ATTACHMENT);
      Direction var8 = getConnectedDirection(var1).getOpposite();
      if (var8 == var2 && !var1.canSurvive(var4, var5) && var7 != BellAttachType.DOUBLE_WALL) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (var2.getAxis() == ((Direction)var1.getValue(FACING)).getAxis()) {
            if (var7 == BellAttachType.DOUBLE_WALL && !var3.isFaceSturdy(var4, var6, var2)) {
               return (BlockState)((BlockState)var1.setValue(ATTACHMENT, BellAttachType.SINGLE_WALL)).setValue(FACING, var2.getOpposite());
            }

            if (var7 == BellAttachType.SINGLE_WALL && var8.getOpposite() == var2 && var3.isFaceSturdy(var4, var6, (Direction)var1.getValue(FACING))) {
               return (BlockState)var1.setValue(ATTACHMENT, BellAttachType.DOUBLE_WALL);
            }
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = getConnectedDirection(var1).getOpposite();
      return var4 == Direction.field_526 ? Block.canSupportCenter(var2, var3.above(), Direction.DOWN) : FaceAttachedHorizontalDirectionalBlock.canAttach(var2, var3, var4);
   }

   private static Direction getConnectedDirection(BlockState var0) {
      switch((BellAttachType)var0.getValue(ATTACHMENT)) {
      case FLOOR:
         return Direction.field_526;
      case CEILING:
         return Direction.DOWN;
      default:
         return ((Direction)var0.getValue(FACING)).getOpposite();
      }
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.DESTROY;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, ATTACHMENT, POWERED);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BellBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.BELL, var1.isClientSide ? BellBlockEntity::clientTick : BellBlockEntity::serverTick);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
      POWERED = BlockStateProperties.POWERED;
      NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
      EAST_WEST_FLOOR_SHAPE = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
      BELL_TOP_SHAPE = Block.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
      BELL_BOTTOM_SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
      BELL_SHAPE = Shapes.method_31(BELL_BOTTOM_SHAPE, BELL_TOP_SHAPE);
      NORTH_SOUTH_BETWEEN = Shapes.method_31(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
      EAST_WEST_BETWEEN = Shapes.method_31(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
      TO_WEST = Shapes.method_31(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
      TO_EAST = Shapes.method_31(BELL_SHAPE, Block.box(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
      TO_NORTH = Shapes.method_31(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
      TO_SOUTH = Shapes.method_31(BELL_SHAPE, Block.box(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
      CEILING_SHAPE = Shapes.method_31(BELL_SHAPE, Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));
   }
}
