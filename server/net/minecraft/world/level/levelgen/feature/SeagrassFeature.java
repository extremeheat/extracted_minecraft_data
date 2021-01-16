package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrass;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;

public class SeagrassFeature extends Feature<ProbabilityFeatureConfiguration> {
   public SeagrassFeature(Codec<ProbabilityFeatureConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, ProbabilityFeatureConfiguration var5) {
      boolean var6 = false;
      int var7 = var3.nextInt(8) - var3.nextInt(8);
      int var8 = var3.nextInt(8) - var3.nextInt(8);
      int var9 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR, var4.getX() + var7, var4.getZ() + var8);
      BlockPos var10 = new BlockPos(var4.getX() + var7, var9, var4.getZ() + var8);
      if (var1.getBlockState(var10).is(Blocks.WATER)) {
         boolean var11 = var3.nextDouble() < (double)var5.probability;
         BlockState var12 = var11 ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
         if (var12.canSurvive(var1, var10)) {
            if (var11) {
               BlockState var13 = (BlockState)var12.setValue(TallSeagrass.HALF, DoubleBlockHalf.UPPER);
               BlockPos var14 = var10.above();
               if (var1.getBlockState(var14).is(Blocks.WATER)) {
                  var1.setBlock(var10, var12, 2);
                  var1.setBlock(var14, var13, 2);
               }
            } else {
               var1.setBlock(var10, var12, 2);
            }

            var6 = true;
         }
      }

      return var6;
   }
}
