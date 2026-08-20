package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RootedDirtBlock extends Block implements BonemealableBlock {
   public RootedDirtBlock(final BlockBehaviour.Properties properties) {
      super(properties);
   }

   public boolean isValidBonemealTarget(final LevelReader level, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return level.getBlockState(pos.below()).isAir() && level.isInsideBuildHeight(pos.below());
   }

   public boolean isBonemealSuccess(final Level level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      return true;
   }

   public void performBonemeal(final ServerLevel level, final RandomSource random, final BlockPos pos, final BlockState state, final BonemealSource source) {
      level.setBlockAndUpdate(pos.below(), Blocks.HANGING_ROOTS.defaultBlockState());
   }

   public BlockPos getParticlePos(final BlockPos blockPos) {
      return blockPos.below();
   }
}
