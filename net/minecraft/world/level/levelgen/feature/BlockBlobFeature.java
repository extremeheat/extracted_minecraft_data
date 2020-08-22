package net.minecraft.world.level.levelgen.feature;

import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.BlockBlobConfiguration;

public class BlockBlobFeature extends Feature {
   public BlockBlobFeature(Function var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator var2, Random var3, BlockPos var4, BlockBlobConfiguration var5) {
      while(true) {
         label48: {
            if (var4.getY() > 3) {
               if (var1.isEmptyBlock(var4.below())) {
                  break label48;
               }

               Block var6 = var1.getBlockState(var4.below()).getBlock();
               if (!isDirt(var6) && !isStone(var6)) {
                  break label48;
               }
            }

            if (var4.getY() <= 3) {
               return false;
            }

            int var14 = var5.startRadius;

            for(int var7 = 0; var14 >= 0 && var7 < 3; ++var7) {
               int var8 = var14 + var3.nextInt(2);
               int var9 = var14 + var3.nextInt(2);
               int var10 = var14 + var3.nextInt(2);
               float var11 = (float)(var8 + var9 + var10) * 0.333F + 0.5F;
               Iterator var12 = BlockPos.betweenClosed(var4.offset(-var8, -var9, -var10), var4.offset(var8, var9, var10)).iterator();

               while(var12.hasNext()) {
                  BlockPos var13 = (BlockPos)var12.next();
                  if (var13.distSqr(var4) <= (double)(var11 * var11)) {
                     var1.setBlock(var13, var5.state, 4);
                  }
               }

               var4 = var4.offset(-(var14 + 1) + var3.nextInt(2 + var14 * 2), 0 - var3.nextInt(2), -(var14 + 1) + var3.nextInt(2 + var14 * 2));
            }

            return true;
         }

         var4 = var4.below();
      }
   }
}
