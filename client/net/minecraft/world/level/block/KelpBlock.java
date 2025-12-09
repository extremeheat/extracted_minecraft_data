package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class KelpBlock extends GrowingPlantHeadBlock implements LiquidBlockContainer {
   public static final MapCodec<KelpBlock> CODEC = simpleCodec(KelpBlock::new);
   private static final double GROW_PER_TICK_PROBABILITY = 0.14;
   private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 9.0);

   public MapCodec<KelpBlock> codec() {
      return CODEC;
   }

   protected KelpBlock(BlockBehaviour.Properties var1) {
      super(var1, Direction.UP, SHAPE, true, 0.14);
   }

   protected boolean canGrowInto(BlockState var1) {
      return var1.is(Blocks.WATER);
   }

   protected Block getBodyBlock() {
      return Blocks.KELP_PLANT;
   }

   protected boolean canAttachTo(BlockState var1) {
      return !var1.is(Blocks.MAGMA_BLOCK);
   }

   public boolean canPlaceLiquid(@Nullable LivingEntity var1, BlockGetter var2, BlockPos var3, BlockState var4, Fluid var5) {
      return false;
   }

   public boolean placeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3, FluidState var4) {
      return false;
   }

   protected int getBlocksToGrowWhenBonemealed(RandomSource var1) {
      return 1;
   }

   public @Nullable BlockState getStateForPlacement(BlockPlaceContext var1) {
      FluidState var2 = var1.getLevel().getFluidState(var1.getClickedPos());
      return var2.is(FluidTags.WATER) && var2.getAmount() == 8 ? super.getStateForPlacement(var1) : null;
   }

   protected FluidState getFluidState(BlockState var1) {
      return Fluids.WATER.getSource(false);
   }
}
