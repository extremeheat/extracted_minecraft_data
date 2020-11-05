package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockStateConfiguration;

public class BlockBlobFeature extends Feature<BlockStateConfiguration> {
   public BlockBlobFeature(Codec<BlockStateConfiguration> var1) {
      super(var1);
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4, BlockStateConfiguration var5) {
      for(; var4.getY() > var1.getMinBuildHeight() + 3; var4 = var4.below()) {
         if (!var1.isEmptyBlock(var4.below())) {
            BlockState var6 = var1.getBlockState(var4.below());
            if (isDirt(var6) || isStone(var6)) {
               break;
            }
         }
      }

      if (var4.getY() <= var1.getMinBuildHeight() + 3) {
         return false;
      } else {
         for(int var13 = 0; var13 < 3; ++var13) {
            int var7 = var3.nextInt(2);
            int var8 = var3.nextInt(2);
            int var9 = var3.nextInt(2);
            float var10 = (float)(var7 + var8 + var9) * 0.333F + 0.5F;
            Iterator var11 = BlockPos.betweenClosed(var4.offset(-var7, -var8, -var9), var4.offset(var7, var8, var9)).iterator();

            while(var11.hasNext()) {
               BlockPos var12 = (BlockPos)var11.next();
               if (var12.distSqr(var4) <= (double)(var10 * var10)) {
                  var1.setBlock(var12, var5.state, 4);
               }
            }

            var4 = var4.offset(-1 + var3.nextInt(2), -var3.nextInt(2), -1 + var3.nextInt(2));
         }

         return true;
      }
   }
}
