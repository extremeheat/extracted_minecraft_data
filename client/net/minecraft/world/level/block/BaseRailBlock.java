package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseRailBlock extends Block implements SimpleWaterloggedBlock {
   protected static final VoxelShape FLAT_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape HALF_BLOCK_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   public static final BooleanProperty WATERLOGGED;
   private final boolean isStraight;

   public static boolean isRail(Level var0, BlockPos var1) {
      return isRail(var0.getBlockState(var1));
   }

   public static boolean isRail(BlockState var0) {
      return var0.is(BlockTags.RAILS) && var0.getBlock() instanceof BaseRailBlock;
   }

   protected BaseRailBlock(boolean var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.isStraight = var1;
   }

   public boolean isStraight() {
      return this.isStraight;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      RailShape var5 = var1.is(this) ? (RailShape)var1.getValue(this.getShapeProperty()) : null;
      return var5 != null && var5.isAscending() ? HALF_BLOCK_AABB : FLAT_AABB;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return canSupportRigidBlock(var2, var3.below());
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.updateState(var1, var2, var3, var5);
      }
   }

   protected BlockState updateState(BlockState var1, Level var2, BlockPos var3, boolean var4) {
      var1 = this.updateDir(var2, var3, var1, true);
      if (this.isStraight) {
         var1.neighborChanged(var2, var3, this, var3, var4);
      }

      return var1;
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide && var2.getBlockState(var3).is(this)) {
         RailShape var7 = (RailShape)var1.getValue(this.getShapeProperty());
         if (shouldBeRemoved(var3, var2, var7)) {
            dropResources(var1, var2, var3);
            var2.removeBlock(var3, var6);
         } else {
            this.updateState(var1, var2, var3, var4);
         }

      }
   }

   private static boolean shouldBeRemoved(BlockPos var0, Level var1, RailShape var2) {
      if (!canSupportRigidBlock(var1, var0.below())) {
         return true;
      } else {
         switch(var2) {
         case ASCENDING_EAST:
            return !canSupportRigidBlock(var1, var0.east());
         case ASCENDING_WEST:
            return !canSupportRigidBlock(var1, var0.west());
         case ASCENDING_NORTH:
            return !canSupportRigidBlock(var1, var0.north());
         case ASCENDING_SOUTH:
            return !canSupportRigidBlock(var1, var0.south());
         default:
            return false;
         }
      }
   }

   protected void updateState(BlockState var1, Level var2, BlockPos var3, Block var4) {
   }

   protected BlockState updateDir(Level var1, BlockPos var2, BlockState var3, boolean var4) {
      if (var1.isClientSide) {
         return var3;
      } else {
         RailShape var5 = (RailShape)var3.getValue(this.getShapeProperty());
         return (new RailState(var1, var2, var3)).place(var1.hasNeighborSignal(var2), var4, var5).getState();
      }
   }

   public PushReaction getPistonPushReaction(BlockState var1) {
      return PushReaction.NORMAL;
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5) {
         super.onRemove(var1, var2, var3, var4, var5);
         if (((RailShape)var1.getValue(this.getShapeProperty())).isAscending()) {
            var2.updateNeighborsAt(var3.above(), this);
         }

         if (this.isStraight) {
            var2.updateNeighborsAt(var3, this);
            var2.updateNeighborsAt(var3.below(), this);
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      boolean var3 = var2.getType() == Fluids.WATER;
      BlockState var4 = super.defaultBlockState();
      Direction var5 = var1.getHorizontalDirection();
      boolean var6 = var5 == Direction.EAST || var5 == Direction.WEST;
      return (BlockState)((BlockState)var4.setValue(this.getShapeProperty(), var6 ? RailShape.EAST_WEST : RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, var3);
   }

   public abstract Property<RailShape> getShapeProperty();

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.getLiquidTicks().scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
   }
}
