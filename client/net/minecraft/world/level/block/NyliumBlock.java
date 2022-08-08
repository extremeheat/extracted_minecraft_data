package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.features.NetherFeatures;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.lighting.LayerLightEngine;

public class NyliumBlock extends Block implements BonemealableBlock {
   protected NyliumBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   private static boolean canBeNylium(BlockState var0, LevelReader var1, BlockPos var2) {
      BlockPos var3 = var2.above();
      BlockState var4 = var1.getBlockState(var3);
      int var5 = LayerLightEngine.getLightBlockInto(var1, var0, var2, var4, var3, Direction.UP, var4.getLightBlock(var1, var3));
      return var5 < var1.getMaxLightLevel();
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!canBeNylium(var1, var2, var3)) {
         var2.setBlockAndUpdate(var3, Blocks.NETHERRACK.defaultBlockState());
      }

   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockState var5 = var1.getBlockState(var3);
      BlockPos var6 = var3.above();
      ChunkGenerator var7 = var1.getChunkSource().getGenerator();
      if (var5.is(Blocks.CRIMSON_NYLIUM)) {
         ((ConfiguredFeature)NetherFeatures.CRIMSON_FOREST_VEGETATION_BONEMEAL.value()).place(var1, var7, var2, var6);
      } else if (var5.is(Blocks.WARPED_NYLIUM)) {
         ((ConfiguredFeature)NetherFeatures.WARPED_FOREST_VEGETATION_BONEMEAL.value()).place(var1, var7, var2, var6);
         ((ConfiguredFeature)NetherFeatures.NETHER_SPROUTS_BONEMEAL.value()).place(var1, var7, var2, var6);
         if (var2.nextInt(8) == 0) {
            ((ConfiguredFeature)NetherFeatures.TWISTING_VINES_BONEMEAL.value()).place(var1, var7, var2, var6);
         }
      }

   }
}
