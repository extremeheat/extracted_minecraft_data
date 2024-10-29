package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SeagrassBlock extends BushBlock implements BonemealableBlock, LiquidBlockContainer {
   public static final MapCodec<SeagrassBlock> CODEC = simpleCodec(SeagrassBlock::new);
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0);

   public MapCodec<SeagrassBlock> codec() {
      return CODEC;
   }

   protected SeagrassBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.isFaceSturdy(var2, var3, Direction.UP) && !var1.is(Blocks.MAGMA_BLOCK);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return var2.is(FluidTags.WATER) && var2.getAmount() == 8 ? super.getStateForPlacement(var1) : null;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      BlockState var9 = super.updateShape(var1, var2, var3, var4, var5, var6, var7, var8);
      if (!var9.isAir()) {
         var3.scheduleTick(var4, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var2));
      }

      return var9;
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.above()).is(Blocks.WATER);
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   protected FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockState var5 = Blocks.TALL_SEAGRASS.defaultBlockState();
      BlockState var6 = (BlockState)var5.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
      BlockPos var7 = var3.above();
      var1.setBlock(var3, var5, 2);
      var1.setBlock(var7, var6, 2);
   }

   public boolean canPlaceLiquid(@Nullable Player var1, BlockGetter var2, BlockPos var3, BlockState var4, Fluid var5) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      return false;
   }
}
