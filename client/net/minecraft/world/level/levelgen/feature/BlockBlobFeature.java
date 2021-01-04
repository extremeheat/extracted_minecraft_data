package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;

public class BlockBlobFeature extends Feature<BlockBlobConfiguration> {
   public BlockBlobFeature(Function<Dynamic<?>, ? extends BlockBlobConfiguration> var1) {
      super(var1);
   }

   public boolean place(LevelAccessor var1, ChunkGenerator<? extends ChunkGeneratorSettings> var2, Random var3, BlockPos var4, BlockBlobConfiguration var5) {
      while(true) {
         label50: {
            if (var4.getY() > 3) {
               if (var1.isEmptyBlock(var4.below())) {
                  break label50;
               }

               Block var6 = var1.getBlockState(var4.below()).getBlock();
               if (var6 != Blocks.GRASS_BLOCK && !Block.equalsDirt(var6) && !Block.equalsStone(var6)) {
                  break label50;
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
