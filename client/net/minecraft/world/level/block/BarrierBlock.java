package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class BarrierBlock extends Block implements SimpleWaterloggedBlock {
   public static final MapCodec<BarrierBlock> CODEC = simpleCodec(BarrierBlock::new);
   public static final BooleanProperty WATERLOGGED;

   public MapCodec<BarrierBlock> codec() {
      return CODEC;
   }

   protected BarrierBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(WATERLOGGED, false));
   }

   protected boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return var1.getFluidState().isEmpty();
   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   protected float getShadeBrightness(BlockState var1, BlockGetter var2, BlockPos var3) {
      return 1.0F;
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if ((Boolean)var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, (Fluid)Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected FluidState getFluidState(BlockState var1) {
      return (Boolean)var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(WATERLOGGED, var1.getLevel().getFluidState(var1.getClickedPos()).getType() == Fluids.WATER);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(WATERLOGGED);
   }

   public ItemStack pickupBlock(@Nullable Player var1, LevelAccessor var2, BlockPos var3, BlockState var4) {
      return var1 != null && var1.isCreative() ? SimpleWaterloggedBlock.super.pickupBlock(var1, var2, var3, var4) : ItemStack.EMPTY;
   }

   public boolean canPlaceLiquid(@Nullable Player var1, BlockGetter var2, BlockPos var3, BlockState var4, Fluid var5) {
      return var1 != null && var1.isCreative() ? SimpleWaterloggedBlock.super.canPlaceLiquid(var1, var2, var3, var4, var5) : false;
   }

   static {
      WATERLOGGED = BlockStateProperties.WATERLOGGED;
   }
}
