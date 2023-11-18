package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.CaveFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class MossBlock extends Block implements BonemealableBlock {
   public MossBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return var1.getBlockState(var2.above()).isAir();
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      var1.registryAccess()
         .registry(Registries.CONFIGURED_FEATURE)
         .flatMap(var0 -> var0.getHolder(CaveFeatures.MOSS_PATCH_BONEMEAL))
         .ifPresent(var3x -> var3x.value().place(var1, var1.getChunkSource().getGenerator(), var2, var3.above()));
   }
}
