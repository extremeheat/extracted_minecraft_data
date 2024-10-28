package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class BlockBlobFeature extends Feature<BlockStateConfiguration> {
   public BlockBlobFeature(Codec<BlockStateConfiguration> var1) {
      super(var1);
   }

   public boolean place(FeaturePlaceContext<BlockStateConfiguration> var1) {
      BlockPos var2 = var1.origin();
      WorldGenLevel var3 = var1.level();
      RandomSource var4 = var1.random();

      BlockStateConfiguration var5;
      for(var5 = (BlockStateConfiguration)var1.config(); var2.getY() > var3.getMinBuildHeight() + 3; var2 = var2.below()) {
         if (!var3.isEmptyBlock(var2.below())) {
            BlockState var6 = var3.getBlockState(var2.below());
            if (isDirt(var6) || isStone(var6)) {
               break;
            }
         }
      }

      if (var2.getY() <= var3.getMinBuildHeight() + 3) {
         return false;
      } else {
         for(int var13 = 0; var13 < 3; ++var13) {
            int var7 = var4.nextInt(2);
            int var8 = var4.nextInt(2);
            int var9 = var4.nextInt(2);
            float var10 = (float)(var7 + var8 + var9) * 0.333F + 0.5F;
            Iterator var11 = BlockPos.betweenClosed(var2.offset(-var7, -var8, -var9), var2.offset(var7, var8, var9)).iterator();

            while(var11.hasNext()) {
               BlockPos var12 = (BlockPos)var11.next();
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
