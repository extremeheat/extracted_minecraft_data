package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HopperBlock extends BaseEntityBlock {
   public static final MapCodec<HopperBlock> CODEC = simpleCodec(HopperBlock::new);
   public static final EnumProperty<Direction> FACING;
   public static final BooleanProperty ENABLED;
   private static final VoxelShape TOP;
   private static final VoxelShape FUNNEL;
   private static final VoxelShape CONVEX_BASE;
   private static final VoxelShape INSIDE;
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

   public MapCodec<HopperBlock> codec() {
      return CODEC;
   }

   public HopperBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.DOWN)).setValue(ENABLED, true));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch ((Direction)var1.getValue(FACING)) {
         case DOWN -> {
            return DOWN_SHAPE;
         }
         case NORTH -> {
            return NORTH_SHAPE;
         }
         case SOUTH -> {
            return SOUTH_SHAPE;
         }
         case WEST -> {
            return WEST_SHAPE;
         }
         case EAST -> {
            return EAST_SHAPE;
         }
         default -> {
            return BASE;
         }
      }
   }

   protected VoxelShape getInteractionShape(BlockState var1, BlockGetter var2, BlockPos var3) {
      switch ((Direction)var1.getValue(FACING)) {
         case DOWN -> {
            return DOWN_INTERACTION_SHAPE;
         }
         case NORTH -> {
            return NORTH_INTERACTION_SHAPE;
         }
         case SOUTH -> {
            return SOUTH_INTERACTION_SHAPE;
         }
         case WEST -> {
            return WEST_INTERACTION_SHAPE;
         }
         case EAST -> {
            return EAST_INTERACTION_SHAPE;
         }
         default -> {
            return INSIDE;
         }
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

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.checkPoweredState(var2, var3, var1);
      }
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var2.isClientSide) {
         BlockEntity var7 = var2.getBlockEntity(var3);
         if (var7 instanceof HopperBlockEntity) {
            HopperBlockEntity var6 = (HopperBlockEntity)var7;
            var4.openMenu(var6);
            var4.awardStat(Stats.INSPECT_HOPPER);
         }
      }

      return InteractionResult.SUCCESS;
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      this.checkPoweredState(var2, var3, var1);
   }

   private void checkPoweredState(Level var1, BlockPos var2, BlockState var3) {
      boolean var4 = !var1.hasNeighborSignal(var2);
      if (var4 != (Boolean)var3.getValue(ENABLED)) {
         var1.setBlock(var2, (BlockState)var3.setValue(ENABLED, var4), 2);
      }

   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      Containers.dropContentsOnDestroy(var1, var4, var2, var3);
      super.onRemove(var1, var2, var3, var4, var5);
   }

   protected boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   protected int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(var2.getBlockEntity(var3));
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, ENABLED);
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      BlockEntity var5 = var2.getBlockEntity(var3);
      if (var5 instanceof HopperBlockEntity) {
         HopperBlockEntity.entityInside(var2, var3, var1, var4, (HopperBlockEntity)var5);
      }

   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   static {
      FACING = BlockStateProperties.FACING_HOPPER;
      ENABLED = BlockStateProperties.ENABLED;
      TOP = Block.box(0.0, 10.0, 0.0, 16.0, 16.0, 16.0);
      FUNNEL = Block.box(4.0, 4.0, 4.0, 12.0, 10.0, 12.0);
      CONVEX_BASE = Shapes.or(FUNNEL, TOP);
      INSIDE = box(2.0, 11.0, 2.0, 14.0, 16.0, 14.0);
      BASE = Shapes.join(CONVEX_BASE, INSIDE, BooleanOp.ONLY_FIRST);
      DOWN_SHAPE = Shapes.or(BASE, Block.box(6.0, 0.0, 6.0, 10.0, 4.0, 10.0));
      EAST_SHAPE = Shapes.or(BASE, Block.box(12.0, 4.0, 6.0, 16.0, 8.0, 10.0));
      NORTH_SHAPE = Shapes.or(BASE, Block.box(6.0, 4.0, 0.0, 10.0, 8.0, 4.0));
      SOUTH_SHAPE = Shapes.or(BASE, Block.box(6.0, 4.0, 12.0, 10.0, 8.0, 16.0));
      WEST_SHAPE = Shapes.or(BASE, Block.box(0.0, 4.0, 6.0, 4.0, 8.0, 10.0));
      DOWN_INTERACTION_SHAPE = INSIDE;
      EAST_INTERACTION_SHAPE = Shapes.or(INSIDE, Block.box(12.0, 8.0, 6.0, 16.0, 10.0, 10.0));
      NORTH_INTERACTION_SHAPE = Shapes.or(INSIDE, Block.box(6.0, 8.0, 0.0, 10.0, 10.0, 4.0));
      SOUTH_INTERACTION_SHAPE = Shapes.or(INSIDE, Block.box(6.0, 8.0, 12.0, 10.0, 10.0, 16.0));
      WEST_INTERACTION_SHAPE = Shapes.or(INSIDE, Block.box(0.0, 8.0, 6.0, 4.0, 10.0, 10.0));
   }
}
