package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class CloudFeature extends Feature<BlockStateConfiguration> {
   public CloudFeature(Codec<BlockStateConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<BlockStateConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      if (!var3.isEmptyBlock(var2)) {
         return false;
      } else {
         RandomSource var4 = var1.random();
         BlockStateConfiguration var5 = (BlockStateConfiguration)var1.config();

         for(int var6 = 0; var6 < 3; ++var6) {
            int var7 = var4.nextInt(2) + 1;
            int var8 = var4.nextInt(2) + 1;
            int var9 = var4.nextInt(2) + 1;
            float var10 = (float)(var7 + var8 + var9) * 0.333F + 0.5F;

            for(BlockPos var12 : BlockPos.betweenClosed(var2.offset(-var7, -var8, -var9), var2.offset(var7, var8, var9))) {
               if (var12.distSqr(var2) <= (double)(var10 * var10)) {
                  var3.setBlock(var12, var5.state, 3);
               }
            }

            var2 = var2.offset(-1 + var4.nextInt(2), -var4.nextInt(2), -1 + var4.nextInt(2));
         }

         return true;
      }
   }
}
