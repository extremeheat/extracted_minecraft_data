package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockPileConfiguration;

public class NetherForestVegetationFeature extends Feature<BlockPileConfiguration> {
   public NetherForestVegetationFeature(Codec<BlockPileConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, BlockPileConfiguration var5) {
      return place(var1, var3, var4, var5, 8, 4);
   }

   public static boolean place(LevelAccessor var0, Random var1, BlockPos var2, BlockPileConfiguration var3, int var4, int var5) {
      BlockState var6 = var0.getBlockState(var2.below());
      if (!var6.is(BlockTags.NYLIUM)) {
         return false;
      } else {
         int var7 = var2.getY();
         if (var7 >= var0.getMinBuildHeight() + 1 && var7 + 1 < var0.getMaxBuildHeight()) {
            int var8 = 0;

            for(int var9 = 0; var9 < var4 * var4; ++var9) {
               BlockPos var10 = var2.offset(var1.nextInt(var4) - var1.nextInt(var4), var1.nextInt(var5) - var1.nextInt(var5), var1.nextInt(var4) - var1.nextInt(var4));
               BlockState var11 = var3.stateProvider.getState(var1, var10);
               if (var0.isEmptyBlock(var10) && var10.getY() > var0.getMinBuildHeight() && var11.canSurvive(var0, var10)) {
                  var0.setBlock(var10, var11, 2);
                  ++var8;
               }
            }

            return var8 > 0;
         } else {
            return false;
         }
      }
   }
}
