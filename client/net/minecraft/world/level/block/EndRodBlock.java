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

   public MapCodec<EndRodBlock> codec() {
      return CODEC;
   }

   protected EndRodBlock(final BlockBehaviour.Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.UP));
   }

   public BlockState getStateForPlacement(final BlockPlaceContext context) {
      Direction clickedFace = context.getClickedFace();
      BlockState blockState = context.getLevel().getBlockState(context.getClickedPos().relative(clickedFace.getOpposite()));
      return blockState.is(this) && blockState.getValue(FACING) == clickedFace ? (BlockState)this.defaultBlockState().setValue(FACING, clickedFace.getOpposite()) : (BlockState)this.defaultBlockState().setValue(FACING, clickedFace);
   }

   public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
      Direction direction = (Direction)state.getValue(FACING);
      double x = (double)pos.getX() + 0.55 - (double)(random.nextFloat() * 0.1F);
      double y = (double)pos.getY() + 0.55 - (double)(random.nextFloat() * 0.1F);
      double z = (double)pos.getZ() + 0.55 - (double)(random.nextFloat() * 0.1F);
      double r = (double)(0.4F - (random.nextFloat() + random.nextFloat()) * 0.4F);
      if (random.nextInt(5) == 0) {
         level.addParticle(ParticleTypes.END_ROD, x + (double)direction.getStepX() * r, y + (double)direction.getStepY() * r, z + (double)direction.getStepZ() * r, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005, random.nextGaussian() * 0.005);
      }

   }

   protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }
}
