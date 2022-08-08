package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.NetherForestVegetationConfig;

public class NetherForestVegetationFeature extends Feature<NetherForestVegetationConfig> {
   public NetherForestVegetationFeature(Codec<NetherForestVegetationConfig> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<NetherForestVegetationConfig> var1) {
      WorldGenLevel var2 = var1.level();
      BlockPos var3 = var1.origin();
      BlockState var4 = var2.getBlockState(var3.below());
      NetherForestVegetationConfig var5 = (NetherForestVegetationConfig)var1.config();
      RandomSource var6 = var1.random();
      if (!var4.is(BlockTags.NYLIUM)) {
         return false;
      } else {
         int var7 = var3.getY();
         if (var7 >= var2.getMinBuildHeight() + 1 && var7 + 1 < var2.getMaxBuildHeight()) {
            int var8 = 0;

            for(int var9 = 0; var9 < var5.spreadWidth * var5.spreadWidth; ++var9) {
               BlockPos var10 = var3.offset(var6.nextInt(var5.spreadWidth) - var6.nextInt(var5.spreadWidth), var6.nextInt(var5.spreadHeight) - var6.nextInt(var5.spreadHeight), var6.nextInt(var5.spreadWidth) - var6.nextInt(var5.spreadWidth));
               BlockState var11 = var5.stateProvider.getState(var6, var10);
               if (var2.isEmptyBlock(var10) && var10.getY() > var2.getMinBuildHeight() && var11.canSurvive(var2, var10)) {
                  var2.setBlock(var10, var11, 2);
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
