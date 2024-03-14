package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BellBlock extends BaseEntityBlock {
   public static final MapCodec<BellBlock> CODEC = simpleCodec(BellBlock::new);
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<BellAttachType> ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   private static final VoxelShape NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 12.0);
   private static final VoxelShape EAST_WEST_FLOOR_SHAPE = Block.box(4.0, 0.0, 0.0, 12.0, 16.0, 16.0);
   private static final VoxelShape BELL_TOP_SHAPE = Block.box(5.0, 6.0, 5.0, 11.0, 13.0, 11.0);
   private static final VoxelShape BELL_BOTTOM_SHAPE = Block.box(4.0, 4.0, 4.0, 12.0, 6.0, 12.0);
   private static final VoxelShape BELL_SHAPE = Shapes.or(BELL_BOTTOM_SHAPE, BELL_TOP_SHAPE);
   private static final VoxelShape NORTH_SOUTH_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 0.0, 9.0, 15.0, 16.0));
   private static final VoxelShape EAST_WEST_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(0.0, 13.0, 7.0, 16.0, 15.0, 9.0));
   private static final VoxelShape TO_WEST = Shapes.or(BELL_SHAPE, Block.box(0.0, 13.0, 7.0, 13.0, 15.0, 9.0));
   private static final VoxelShape TO_EAST = Shapes.or(BELL_SHAPE, Block.box(3.0, 13.0, 7.0, 16.0, 15.0, 9.0));
   private static final VoxelShape TO_NORTH = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 0.0, 9.0, 15.0, 13.0));
   private static final VoxelShape TO_SOUTH = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 3.0, 9.0, 15.0, 16.0));
   private static final VoxelShape CEILING_SHAPE = Shapes.or(BELL_SHAPE, Block.box(7.0, 13.0, 7.0, 9.0, 16.0, 9.0));
   public static final int EVENT_BELL_RING = 1;

   @Override
   public MapCodec<BellBlock> codec() {
      return CODEC;
   }

   public BellBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ATTACHMENT, BellAttachType.FLOOR).setValue(POWERED, Boolean.valueOf(false))
      );
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      boolean var7 = var2.hasNeighborSignal(var3);
      if (var7 != var1.getValue(POWERED)) {
         if (var7) {
            this.attemptToRing(var2, var3, null);
         }

         var2.setBlock(var3, var1.setValue(POWERED, Boolean.valueOf(var7)), 3);
      }
   }

   @Override
   protected void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      Entity var5 = var4.getOwner();
      Player var6 = var5 instanceof Player ? (Player)var5 : null;
      this.onHit(var1, var2, var3, var6, true);
   }

   @Override
   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      return this.onHit(var2, var1, var5, var4, true) ? InteractionResult.sidedSuccess(var2.isClientSide) : InteractionResult.PASS;
   }

   public boolean onHit(Level var1, BlockState var2, BlockHitResult var3, @Nullable Player var4, boolean var5) {
      Direction var6 = var3.getDirection();
      BlockPos var7 = var3.getBlockPos();
      boolean var8 = !var5 || this.isProperHit(var2, var6, var3.getLocation().y - (double)var7.getY());
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
      if (var2.getAxis() != Direction.Axis.Y && !(var3 > 0.8123999834060669)) {
         Direction var5 = var1.getValue(FACING);
         BellAttachType var6 = var1.getValue(ATTACHMENT);
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
      return this.attemptToRing(null, var1, var2, var3);
   }

   public boolean attemptToRing(@Nullable Entity var1, Level var2, BlockPos var3, @Nullable Direction var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (!var2.isClientSide && var5 instanceof BellBlockEntity) {
         if (var4 == null) {
            var4 = var2.getBlockState(var3).getValue(FACING);
         }

         ((BellBlockEntity)var5).onHit(var4);
         var2.playSound(null, var3, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
         var2.gameEvent(var1, GameEvent.BLOCK_CHANGE, var3);
         return true;
      } else {
         return false;
      }
   }

   private VoxelShape getVoxelShape(BlockState var1) {
      Direction var2 = var1.getValue(FACING);
      BellAttachType var3 = var1.getValue(ATTACHMENT);
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

   @Override
   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   @Override
   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var3 = var1.getClickedFace();
      BlockPos var4 = var1.getClickedPos();
      Level var5 = var1.getLevel();
      Direction.Axis var6 = var3.getAxis();
      if (var6 == Direction.Axis.Y) {
         BlockState var2 = this.defaultBlockState()
            .setValue(ATTACHMENT, var3 == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR)
            .setValue(FACING, var1.getHorizontalDirection());
         if (var2.canSurvive(var1.getLevel(), var4)) {
            return var2;
         }
      } else {
         boolean var7 = var6 == Direction.Axis.X
               && var5.getBlockState(var4.west()).isFaceSturdy(var5, var4.west(), Direction.EAST)
               && var5.getBlockState(var4.east()).isFaceSturdy(var5, var4.east(), Direction.WEST)
            || var6 == Direction.Axis.Z
               && var5.getBlockState(var4.north()).isFaceSturdy(var5, var4.north(), Direction.SOUTH)
               && var5.getBlockState(var4.south()).isFaceSturdy(var5, var4.south(), Direction.NORTH);
         BlockState var9 = this.defaultBlockState()
            .setValue(FACING, var3.getOpposite())
            .setValue(ATTACHMENT, var7 ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
         if (var9.canSurvive(var1.getLevel(), var1.getClickedPos())) {
            return var9;
         }

         boolean var8 = var5.getBlockState(var4.below()).isFaceSturdy(var5, var4.below(), Direction.UP);
         var9 = var9.setValue(ATTACHMENT, var8 ? BellAttachType.FLOOR : BellAttachType.CEILING);
         if (var9.canSurvive(var1.getLevel(), var1.getClickedPos())) {
            return var9;
         }
      }

      return null;
   }

   @Override
   protected void onExplosionHit(BlockState var1, Level var2, BlockPos var3, Explosion var4, BiConsumer<ItemStack, BlockPos> var5) {
      if (var4.getBlockInteraction() == Explosion.BlockInteraction.TRIGGER_BLOCK && !var2.isClientSide()) {
         this.attemptToRing(var2, var3, null);
      }

      super.onExplosionHit(var1, var2, var3, var4, var5);
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      BellAttachType var7 = var1.getValue(ATTACHMENT);
      Direction var8 = getConnectedDirection(var1).getOpposite();
      if (var8 == var2 && !var1.canSurvive(var4, var5) && var7 != BellAttachType.DOUBLE_WALL) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (var2.getAxis() == var1.getValue(FACING).getAxis()) {
            if (var7 == BellAttachType.DOUBLE_WALL && !var3.isFaceSturdy(var4, var6, var2)) {
               return var1.setValue(ATTACHMENT, BellAttachType.SINGLE_WALL).setValue(FACING, var2.getOpposite());
            }

            if (var7 == BellAttachType.SINGLE_WALL && var8.getOpposite() == var2 && var3.isFaceSturdy(var4, var6, var1.getValue(FACING))) {
               return var1.setValue(ATTACHMENT, BellAttachType.DOUBLE_WALL);
            }
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = getConnectedDirection(var1).getOpposite();
      return var4 == Direction.UP
         ? Block.canSupportCenter(var2, var3.above(), Direction.DOWN)
         : FaceAttachedHorizontalDirectionalBlock.canAttach(var2, var3, var4);
   }

   private static Direction getConnectedDirection(BlockState var0) {
      switch((BellAttachType)var0.getValue(ATTACHMENT)) {
         case FLOOR:
            return Direction.UP;
         case CEILING:
            return Direction.DOWN;
         default:
            return var0.getValue(FACING).getOpposite();
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, ATTACHMENT, POWERED);
   }

   @Nullable
   @Override
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new BellBlockEntity(var1, var2);
   }

   @Nullable
   @Override
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.BELL, var1.isClientSide ? BellBlockEntity::clientTick : BellBlockEntity::serverTick);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }
}
