package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SlabBlock extends Block implements SimpleWaterloggedBlock {
   public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape BOTTOM_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   protected static final VoxelShape TOP_AABB = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

   public SlabBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   public boolean useShapeForLightOcclusion(BlockState var1) {
      return var1.getValue(TYPE) != SlabType.DOUBLE;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(TYPE, WATERLOGGED);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      SlabType var5 = var1.getValue(TYPE);
      switch(var5) {
         case DOUBLE:
            return Shapes.block();
         case TOP:
            return TOP_AABB;
         default:
            return BOTTOM_AABB;
      }
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      BlockState var3 = var1.getLevel().getBlockState(var2);
      if (var3.is(this)) {
         return var3.setValue(TYPE, SlabType.DOUBLE).setValue(WATERLOGGED, Boolean.valueOf(false));
      } else {
         FluidState var4 = var1.getLevel().getFluidState(var2);
         BlockState var5 = this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM).setValue(WATERLOGGED, Boolean.valueOf(var4.getType() == Fluids.WATER));
         Direction var6 = var1.getClickedFace();
         return var6 != Direction.DOWN && (var6 == Direction.UP || !(var1.getClickLocation().y - (double)var2.getY() > 0.5))
            ? var5
            : var5.setValue(TYPE, SlabType.TOP);
      }
   }

   @Override
   public boolean canBeReplaced(BlockState var1, BlockPlaceContext var2) {
      ItemStack var3 = var2.getItemInHand();
      SlabType var4 = var1.getValue(TYPE);
      if (var4 == SlabType.DOUBLE || !var3.is(this.asItem())) {
         return false;
      } else if (var2.replacingClickedOnBlock()) {
         boolean var5 = var2.getClickLocation().y - (double)var2.getClickedPos().getY() > 0.5;
         Direction var6 = var2.getClickedFace();
         if (var4 == SlabType.BOTTOM) {
            return var6 == Direction.UP || var5 && var6.getAxis().isHorizontal();
         } else {
            return var6 == Direction.DOWN || !var5 && var6.getAxis().isHorizontal();
         }
      } else {
         return true;
      }
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Override
   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      return var3.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.placeLiquid(var1, var2, var3, var4) : false;
   }

   @Override
   public boolean canPlaceLiquid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4) {
      return var3.getValue(TYPE) != SlabType.DOUBLE ? SimpleWaterloggedBlock.super.canPlaceLiquid(var1, var2, var3, var4) : false;
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      switch(var4) {
         case LAND:
            return false;
         case WATER:
            return var2.getFluidState(var3).is(FluidTags.WATER);
         case AIR:
            return false;
         default:
            return false;
      }
   }
}