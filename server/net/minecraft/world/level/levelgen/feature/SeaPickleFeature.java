package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;

public class SeaPickleFeature extends Feature<CountConfiguration> {
   public SeaPickleFeature(Codec<CountConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, CountConfiguration var5) {
      int var6 = 0;
      int var7 = var5.count().sample(var3);

      for(int var8 = 0; var8 < var7; ++var8) {
         int var9 = var3.nextInt(8) - var3.nextInt(8);
         int var10 = var3.nextInt(8) - var3.nextInt(8);
         int var11 = var1.getHeight(Heightmap.Types.OCEAN_FLOOR, var4.getX() + var9, var4.getZ() + var10);
         BlockPos var12 = new BlockPos(var4.getX() + var9, var11, var4.getZ() + var10);
         BlockState var13 = (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, var3.nextInt(4) + 1);
         if (var1.getBlockState(var12).is(Blocks.WATER) && var13.canSurvive(var1, var12)) {
            var1.setBlock(var12, var13, 2);
            ++var6;
         }
      }

      return var6 > 0;
   }
}
