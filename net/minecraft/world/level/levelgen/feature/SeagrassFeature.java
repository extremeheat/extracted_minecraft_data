package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrass;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.SeagrassFeatureConfiguration;

public class SeagrassFeature extends Feature {
   public SeagrassFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, SeagrassFeatureConfiguration var5) {
      int var6 = 0;

      for(int var7 = 0; var7 < var5.count; ++var7) {
         int var8 = var3.nextInt(8) - var3.nextInt(8);
         int var9 = var3.nextInt(8) - var3.nextInt(8);
         int var10 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR, var4.getX() + var8, var4.getZ() + var9);
         BlockPos var11 = new BlockPos(var4.getX() + var8, var10, var4.getZ() + var9);
         if (var1.getBlockState(var11).getBlock() == Blocks.WATER) {
            boolean var12 = var3.nextDouble() < var5.tallSeagrassProbability;
            BlockState var13 = var12 ? Blocks.TALL_SEAGRASS.defaultBlockState() : Blocks.SEAGRASS.defaultBlockState();
            if (var13.canSurvive(var1, var11)) {
               if (var12) {
                  BlockState var14 = (BlockState)var13.setValue(TallSeagrass.HALF, DoubleBlockHalf.UPPER);
                  BlockPos var15 = var11.above();
                  if (var1.getBlockState(var15).getBlock() == Blocks.WATER) {
                     var1.setBlock(var11, var13, 2);
                     var1.setBlock(var15, var14, 2);
                  }
               } else {
                  var1.setBlock(var11, var13, 2);
               }

               ++var6;
            }
         }
      }

      return var6 > 0;
   }
}
