package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.CountFeatureConfiguration;

public class SeaPickleFeature extends Feature {
   public SeaPickleFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, CountFeatureConfiguration var5) {
      int var6 = 0;

      for(int var7 = 0; var7 < var5.count; ++var7) {
         int var8 = var3.nextInt(8) - var3.nextInt(8);
         int var9 = var3.nextInt(8) - var3.nextInt(8);
         int var10 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR, var4.getX() + var8, var4.getZ() + var9);
         BlockPos var11 = new BlockPos(var4.getX() + var8, var10, var4.getZ() + var9);
         BlockState var12 = (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, var3.nextInt(4) + 1);
         if (var1.getBlockState(var11).getBlock() == Blocks.WATER && var12.canSurvive(var1, var11)) {
            var1.setBlock(var11, var12, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
