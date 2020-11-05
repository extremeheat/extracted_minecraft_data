package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HopperBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING;
   public static final BooleanProperty ENABLED;
   private static final VoxelShape TOP;
   private static final VoxelShape FUNNEL;
   private static final VoxelShape CONVEX_BASE;
   private static final VoxelShape BASE;
   private static final VoxelShape DOWN_SHAPE;
   private static final VoxelShape EAST_SHAPE;
   private static final VoxelShape NORTH_SHAPE;
   private static final VoxelShape SOUTH_SHAPE;
   private static final VoxelShape WEST_SHAPE;
   private static final VoxelShape DOWN_INTERACTION_SHAPE;
   private static final VoxelShape EAST_INTERACTION_SHAPE;
   private static final VoxelShape NORTH_INTERACTION_SHAPE;
   private static final VoxelShape SOUTH_INTERACTION_SHAPE;
   private static final VoxelShape WEST_INTERACTION_SHAPE;

   public HopperBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.DOWN)).setValue(ENABLED, true));
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch((Direction)var1.getValue(FACING)) {
      case DOWN:
         return DOWN_SHAPE;
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      case EAST:
         return EAST_SHAPE;
      default:
         return BASE;
      }
   }

   public VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      switch((Direction)var1.getValue(FACING)) {
      case DOWN:
         return DOWN_INTERACTION_SHAPE;
      case NORTH:
         return NORTH_INTERACTION_SHAPE;
      case SOUTH:
         return SOUTH_INTERACTION_SHAPE;
      case WEST:
         return WEST_INTERACTION_SHAPE;
      case EAST:
         return EAST_INTERACTION_SHAPE;
      default:
         return Hopper.INSIDE;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getClickedFace().getOpposite();
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var2.getAxis() == Direction.Axis.Y ? Direction.DOWN : var2)).setValue(ENABLED, true);
   }

   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return new HopperBlockEntity(var1, var2);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return var1.isClientSide ? null : createTickerHelper(var3, BlockEntityType.HOPPER, HopperBlockEntity::pushItemsTick);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (var5.hasCustomHoverName()) {
         BlockEntity var6 = var1.getBlockEntity(var2);
         if (var6 instanceof HopperBlockEntity) {
            ((HopperBlockEntity)var6).setCustomName(var5.getHoverName());
         }
      }

   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.checkPoweredState(var2, var3, var1);
      }
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (var2.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof HopperBlockEntity) {
            var4.openMenu((HopperBlockEntity)var7);
            var4.awardStat(Stats.INSPECT_HOPPER);
         }

         return InteractionResult.CONSUME;
      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      this.checkPoweredState(var2, var3, var1);
   }

   private void checkPoweredState(Level var1, BlockPos var2, BlockState var3) {
      boolean var4 = !var1.hasNeighborSignal(var2);
      if (var4 != (Boolean)var3.getValue(ENABLED)) {
         var1.setBlock(var2, (BlockState)var3.setValue(ENABLED, var4), 4);
      }

   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof HopperBlockEntity) {
            Containers.dropContents(var2, (BlockPos)var3, (Container)((HopperBlockEntity)var6));
            var2.updateNeighbourForOutputSignal(var3, this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.MODEL;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, ENABLED);
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof HopperBlockEntity) {
         HopperBlockEntity.entityInside(var2, var3, var1, var4, (HopperBlockEntity)var5);
      }

   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = BlockStateProperties.FACING_HOPPER;
      ENABLED = BlockStateProperties.ENABLED;
      TOP = Block.box(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      FUNNEL = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
      CONVEX_BASE = Shapes.or(FUNNEL, TOP);
      BASE = Shapes.join(CONVEX_BASE, Hopper.INSIDE, BooleanOp.ONLY_FIRST);
      DOWN_SHAPE = Shapes.or(BASE, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
      EAST_SHAPE = Shapes.or(BASE, Block.box(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
      NORTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
      SOUTH_SHAPE = Shapes.or(BASE, Block.box(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
      WEST_SHAPE = Shapes.or(BASE, Block.box(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
      DOWN_INTERACTION_SHAPE = Hopper.INSIDE;
      EAST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
      NORTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
      SOUTH_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
      WEST_INTERACTION_SHAPE = Shapes.or(Hopper.INSIDE, Block.box(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));
   }
}
