package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.worldgen.Features;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.NetherForestVegetationFeature;
import net.minecraft.world.level.levelgen.feature.TwistingVinesFeature;
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

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (!canBeNylium(var1, var2, var3)) {
         var2.setBlockAndUpdate(var3, Blocks.NETHERRACK.defaultBlockState());
      }

   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      BlockState var5 = var1.getBlockState(var3);
      BlockPos var6 = var3.above();
      if (var5.is(Blocks.CRIMSON_NYLIUM)) {
         NetherForestVegetationFeature.place(var1, var2, var6, Features.Configs.CRIMSON_FOREST_CONFIG, 3, 1);
      } else if (var5.is(Blocks.WARPED_NYLIUM)) {
         NetherForestVegetationFeature.place(var1, var2, var6, Features.Configs.WARPED_FOREST_CONFIG, 3, 1);
         NetherForestVegetationFeature.place(var1, var2, var6, Features.Configs.NETHER_SPROUTS_CONFIG, 3, 1);
         if (var2.nextInt(8) == 0) {
            TwistingVinesFeature.place(var1, var2, var6, 3, 1, 2);
         }
      }

   }
}
