package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public class KelpBlock extends GrowingPlantHeadBlock implements LiquidBlockContainer {
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0);
   private static final double GROW_PER_TICK_PROBABILITY = 0.14;

   protected KelpBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.UP, SHAPE, true, 0.14);
   }

   @Override
   protected boolean canGrowInto(BlockState var1) {
      return var1.is(Blocks.WATER);
   }

   @Override
   protected Block getBodyBlock() {
      return Blocks.KELP_PLANT;
   }

   @Override
   protected boolean canAttachTo(BlockState var1) {
      return !var1.is(Blocks.MAGMA_BLOCK);
   }

   @Override
   public boolean canPlaceLiquid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4) {
      return false;
   }

   @Override
   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      return false;
   }

   @Override
   protected int getBlocksToGrowWhenBonemealed(RandomSource var1) {
      return 1;
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return var2.is(FluidTags.WATER) && var2.getAmount() == 8 ? super.getStateForPlacement(var1) : null;
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }
}
