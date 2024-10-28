package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class DoublePlantBlock extends BushBlock {
   public static final MapCodec<DoublePlantBlock> CODEC = simpleCodec(DoublePlantBlock::new);
   public static final EnumProperty<DoubleBlockHalf> HALF;

   public MapCodec<? extends DoublePlantBlock> codec() {
      return CODEC;
   }

   public DoublePlantBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      DoubleBlockHalf var7 = (DoubleBlockHalf)var1.getValue(HALF);
      if (var2.getAxis() == Direction.Axis.Y && var7 == DoubleBlockHalf.LOWER == (var2 == Direction.UP) && (!var3.is(this) || var3.getValue(HALF) == var7)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         return var7 == DoubleBlockHalf.LOWER && var2 == Direction.DOWN && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Level var3 = var1.getLevel();
      return var2.getY() < var3.getMaxBuildHeight() - 1 && var3.getBlockState(var2.above()).canBeReplaced(var1) ? super.getStateForPlacement(var1) : null;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      BlockPos var6 = var2.above();
      var1.setBlock(var6, copyWaterloggedFrom(var1, var6, (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.UPPER)), 3);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      if (var1.getValue(HALF) != DoubleBlockHalf.UPPER) {
         return super.canSurvive(var1, var2, var3);
      } else {
         BlockState var4 = var2.getBlockState(var3.below());
         return var4.is(this) && var4.getValue(HALF) == DoubleBlockHalf.LOWER;
      }
   }

   public static void placeAt(LevelAccessor var0, BlockState var1, BlockPos var2, int var3) {
      BlockPos var4 = var2.above();
      var0.setBlock(var2, copyWaterloggedFrom(var0, var2, (BlockState)var1.setValue(HALF, DoubleBlockHalf.LOWER)), var3);
      var0.setBlock(var4, copyWaterloggedFrom(var0, var4, (BlockState)var1.setValue(HALF, DoubleBlockHalf.UPPER)), var3);
   }

   public static BlockState copyWaterloggedFrom(LevelReader var0, BlockPos var1, BlockState var2) {
      return var2.hasProperty(BlockStateProperties.WATERLOGGED) ? (BlockState)var2.setValue(BlockStateProperties.WATERLOGGED, var0.isWaterAt(var1)) : var2;
   }

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide) {
         if (var4.isCreative()) {
            preventDropFromBottomPart(var1, var2, var3, var4);
         } else {
            dropResources(var3, var1, var2, (BlockEntity)null, var4, var4.getMainHandItem());
         }
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   public void playerDestroy(Level var1, Player var2, BlockPos var3, BlockState var4, @Nullable BlockEntity var5, ItemStack var6) {
      super.playerDestroy(var1, var2, var3, Blocks.AIR.defaultBlockState(), var5, var6);
   }

   protected static void preventDropFromBottomPart(Level var0, BlockPos var1, BlockState var2, Player var3) {
      DoubleBlockHalf var4 = (DoubleBlockHalf)var2.getValue(HALF);
      if (var4 == DoubleBlockHalf.UPPER) {
         BlockPos var5 = var1.below();
         BlockState var6 = var0.getBlockState(var5);
         if (var6.is(var2.getBlock()) && var6.getValue(HALF) == DoubleBlockHalf.LOWER) {
            BlockState var7 = var6.getFluidState().is((Fluid)Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
            var0.setBlock(var5, var7, 35);
            var0.levelEvent(var3, 2001, var5, Block.getId(var6));
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(HALF);
   }

   protected long getSeed(BlockState var1, BlockPos var2) {
      return Mth.getSeed(var2.getX(), var2.below(var1.getValue(HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), var2.getZ());
   }

   static {
      HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   }
}
