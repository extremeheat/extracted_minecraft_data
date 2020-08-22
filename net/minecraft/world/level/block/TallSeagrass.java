package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TallSeagrass extends ShearableDoublePlantBlock implements LiquidBlockContainer {
   public static final EnumProperty HALF;
   protected static final VoxelShape SHAPE;

   public TallSeagrass(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isFaceSturdy(var2, var3, Direction.UP) && var1.getBlock() != Blocks.MAGMA_BLOCK;
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(Blocks.SEAGRASS);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = super.getStateForPlacement(var1);
      if (var2 != null) {
         FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos().above());
         if (var3.is(FluidTags.WATER) && var3.getAmount() == 8) {
            return var2;
         }
      }

      return null;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      if (var1.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockState var5 = var2.getBlockState(var3.below());
         return var5.getBlock() == this && var5.getValue(HALF) == DoubleBlockHalf.LOWER;
      } else {
         FluidState var4 = var2.getFluidState(var3);
         return super.canSurvive(var1, var2, var3) && var4.is(FluidTags.WATER) && var4.getAmount() == 8;
      }
   }

   public FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }

   public boolean canPlaceLiquid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      return false;
   }

   static {
      HALF = ShearableDoublePlantBlock.HALF;
      SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   }
}
