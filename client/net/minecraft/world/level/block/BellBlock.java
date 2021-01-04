package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BellAttachType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BellBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING;
   private static final EnumProperty<BellAttachType> ATTACHMENT;
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

   public BellBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(ATTACHMENT, BellAttachType.FLOOR));
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Entity var4) {
      if (var4 instanceof AbstractArrow) {
         Entity var5 = ((AbstractArrow)var4).getOwner();
         Player var6 = var5 instanceof Player ? (Player)var5 : null;
         this.onHit(var1, var2, var1.getBlockEntity(var3.getBlockPos()), var3, var6, true);
      }

   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      return this.onHit(var2, var1, var2.getBlockEntity(var3), var6, var4, true);
   }

   public boolean onHit(Level var1, BlockState var2, @Nullable BlockEntity var3, BlockHitResult var4, @Nullable Player var5, boolean var6) {
      Direction var7 = var4.getDirection();
      BlockPos var8 = var4.getBlockPos();
      boolean var9 = !var6 || this.isProperHit(var2, var7, var4.getLocation().y - (double)var8.getY());
      if (!var1.isClientSide && var3 instanceof BellBlockEntity && var9) {
         ((BellBlockEntity)var3).onHit(var7);
         this.ring(var1, var8);
         if (var5 != null) {
            var5.awardStat(Stats.BELL_RING);
         }

         return true;
      } else {
         return true;
      }
   }

   private boolean isProperHit(BlockState var1, Direction var2, double var3) {
      if (var2.getAxis() != Direction.Axis.Y && var3 <= 0.8123999834060669D) {
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

   private void ring(Level var1, BlockPos var2) {
      var1.playSound((Player)null, (BlockPos)var2, SoundEvents.BELL_BLOCK, SoundSource.BLOCKS, 2.0F, 1.0F);
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
      if (var6 == Direction.Axis.Y) {
         var2 = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHMENT, var3 == Direction.DOWN ? BellAttachType.CEILING : BellAttachType.FLOOR)).setValue(FACING, var1.getHorizontalDirection());
         if (var2.canSurvive(var1.getLevel(), var4)) {
            return var2;
         }
      } else {
         boolean var7 = var6 == Direction.Axis.X && var5.getBlockState(var4.west()).isFaceSturdy(var5, var4.west(), Direction.EAST) && var5.getBlockState(var4.east()).isFaceSturdy(var5, var4.east(), Direction.WEST) || var6 == Direction.Axis.Z && var5.getBlockState(var4.north()).isFaceSturdy(var5, var4.north(), Direction.SOUTH) && var5.getBlockState(var4.south()).isFaceSturdy(var5, var4.south(), Direction.NORTH);
         var2 = (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var3.getOpposite())).setValue(ATTACHMENT, var7 ? BellAttachType.DOUBLE_WALL : BellAttachType.SINGLE_WALL);
         if (var2.canSurvive(var1.getLevel(), var1.getClickedPos())) {
            return var2;
         }

         boolean var8 = var5.getBlockState(var4.below()).isFaceSturdy(var5, var4.below(), Direction.UP);
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
      return FaceAttachedHorizontalDirectionalBlock.canAttach(var2, var3, getConnectedDirection(var1).getOpposite());
   }

   private static Direction getConnectedDirection(BlockState var0) {
      switch((BellAttachType)var0.getValue(ATTACHMENT)) {
      case FLOOR:
         return Direction.UP;
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
      var1.add(FACING, ATTACHMENT);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new BellBlockEntity();
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      ATTACHMENT = BlockStateProperties.BELL_ATTACHMENT;
      NORTH_SOUTH_FLOOR_SHAPE = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 12.0D);
      EAST_WEST_FLOOR_SHAPE = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
      BELL_TOP_SHAPE = Block.box(5.0D, 6.0D, 5.0D, 11.0D, 13.0D, 11.0D);
      BELL_BOTTOM_SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 6.0D, 12.0D);
      BELL_SHAPE = Shapes.or(BELL_BOTTOM_SHAPE, BELL_TOP_SHAPE);
      NORTH_SOUTH_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 16.0D));
      EAST_WEST_BETWEEN = Shapes.or(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
      TO_WEST = Shapes.or(BELL_SHAPE, Block.box(0.0D, 13.0D, 7.0D, 13.0D, 15.0D, 9.0D));
      TO_EAST = Shapes.or(BELL_SHAPE, Block.box(3.0D, 13.0D, 7.0D, 16.0D, 15.0D, 9.0D));
      TO_NORTH = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 0.0D, 9.0D, 15.0D, 13.0D));
      TO_SOUTH = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 3.0D, 9.0D, 15.0D, 16.0D));
      CEILING_SHAPE = Shapes.or(BELL_SHAPE, Block.box(7.0D, 13.0D, 7.0D, 9.0D, 16.0D, 9.0D));
   }
}
