package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class EndRodBlock extends RodBlock {
   public static final MapCodec<EndRodBlock> CODEC = simpleCodec(EndRodBlock::new);

   @Override
   public MapCodec<EndRodBlock> codec() {
      return CODEC;
   }

   protected EndRodBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Direction var2 = var1.getClickedFace();
      BlockState var3 = var1.getLevel().getBlockState(var1.getClickedPos().relative(var2.getOpposite()));
      return var3.is(this) && var3.getValue(FACING) == var2
         ? this.defaultBlockState().setValue(FACING, var2.getOpposite())
         : this.defaultBlockState().setValue(FACING, var2);
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      Direction var5 = var1.getValue(FACING);
      double var6 = (double)var3.getX() + 0.55 - (double)(var4.nextFloat() * 0.1F);
      double var8 = (double)var3.getY() + 0.55 - (double)(var4.nextFloat() * 0.1F);
      double var10 = (double)var3.getZ() + 0.55 - (double)(var4.nextFloat() * 0.1F);
      double var12 = (double)(0.4F - (var4.nextFloat() + var4.nextFloat()) * 0.4F);
      if (var4.nextInt(5) == 0) {
         var2.addParticle(
            ParticleTypes.END_ROD,
            var6 + (double)var5.getStepX() * var12,
            var8 + (double)var5.getStepY() * var12,
            var10 + (double)var5.getStepZ() * var12,
            var4.nextGaussian() * 0.005,
            var4.nextGaussian() * 0.005,
            var4.nextGaussian() * 0.005
         );
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }
}
