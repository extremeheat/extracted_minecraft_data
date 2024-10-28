package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.configurations.CountConfiguration;

public class SeaPickleFeature extends Feature<CountConfiguration> {
   public SeaPickleFeature(Codec<CountConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<CountConfiguration> var1) {
      int var2 = 0;
      RandomSource var3 = var1.random();
      WorldGenLevel var4 = var1.level();
      BlockPos var5 = var1.origin();
      int var6 = ((CountConfiguration)var1.config()).count().sample(var3);

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var3.nextInt(8) - var3.nextInt(8);
         int var9 = var3.nextInt(8) - var3.nextInt(8);
         int var10 = var4.getHeight(Heightmap.Types.OCEAN_FLOOR, var5.getX() + var8, var5.getZ() + var9);
         BlockPos var11 = new BlockPos(var5.getX() + var8, var10, var5.getZ() + var9);
         BlockState var12 = (BlockState)Blocks.SEA_PICKLE.defaultBlockState().setValue(SeaPickleBlock.PICKLES, var3.nextInt(4) + 1);
         if (var4.getBlockState(var11).is(Blocks.WATER) && var12.canSurvive(var4, var11)) {
            var4.setBlock(var11, var12, 2);
            ++var2;
         }
      }

      return var2 > 0;
   }
}
