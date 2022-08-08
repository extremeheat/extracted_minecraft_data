package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class SeagrassFeature extends Feature<ProbabilityFeatureConfiguration> {
   public SeagrassFeature(Codec<ProbabilityFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<ProbabilityFeatureConfiguration> var1) {
      boolean var2 = false;
      RandomSource var3 = var1.random();
      WorldGenLevel var4 = var1.level();
      BlockPos var5 = var1.origin();
      ProbabilityFeatureConfiguration var6 = (ProbabilityFeatureConfiguration)var1.config();
      int var7 = var3.nextInt(8) - var3.nextInt(8);
      int var8 = var3.nextInt(8) - var3.nextInt(8);
      int var9 = var4.getHeight(Heightmap.Types.OCEAN_FLOOR, var5.getX() + var7, var5.getZ() + var8);
      BlockPos var10 = new BlockPos(var5.getX() + var7, var9, var5.getZ() + var8);
      if (var4.getBlockState(var10).is(Blocks.WATER)) {
         boolean var11 = var3.nextDouble() < (double)var6.probability;
         BlockState var12 = var11 ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
         if (var12.canSurvive(var4, var10)) {
            if (var11) {
               BlockState var13 = (BlockState)var12.setValue(TallSeagrassBlock.HALF, DoubleBlockHalf.UPPER);
               BlockPos var14 = var10.above();
               if (var4.getBlockState(var14).is(Blocks.WATER)) {
                  var4.setBlock(var10, var12, 2);
                  var4.setBlock(var14, var13, 2);
               }
            } else {
               var4.setBlock(var10, var12, 2);
            }

            var2 = true;
         }
      }

      return var2;
   }
}
