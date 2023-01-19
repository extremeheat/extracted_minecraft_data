package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeagrassBlock extends BushBlock implements BonemealableBlock, LiquidBlockContainer {
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

   protected SeagrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isFaceSturdy(var2, var3, Direction.UP) && !var1.is(Blocks.MAGMA_BLOCK);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return var2.is(FluidTags.WATER) && var2.getAmount() == 8 ? super.getStateForPlacement(var1) : null;
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      BlockState var7 = super.updateShape(var1, var2, var3, var4, var5, var6);
      if (!var7.isAir()) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return var7;
   }

   @Override
   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return true;
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockState var5 = Blocks.TALL_SEAGRASS.defaultBlockState();
      BlockState var6 = var5.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
      BlockPos var7 = var3.above();
      if (var1.getBlockState(var7).is(Blocks.WATER)) {
         var1.setBlock(var3, var5, 2);
         var1.setBlock(var7, var6, 2);
      }
   }

   @Override
   public boolean canPlaceLiquid(BlockGetter var1, BlockPos var2, BlockState var3, Fluid var4) {
      return false;
   }

   @Override
   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      return false;
   }
}
