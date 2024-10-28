package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class RootedDirtBlock extends Block implements BonemealableBlock {
   public static final MapCodec<RootedDirtBlock> CODEC = simpleCodec(RootedDirtBlock::new);

   public MapCodec<RootedDirtBlock> codec() {
      return CODEC;
   }

   public RootedDirtBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.below()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.setBlockAndUpdate(var3.below(), Blocks.HANGING_ROOTS.defaultBlockState());
   }

   public BlockPos getParticlePos(BlockPos var1) {
      return var1.below();
   }
}
