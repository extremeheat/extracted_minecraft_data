package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
   public AbstractMegaTreeGrower() {
      super();
   }

   public boolean growTree(ServerLevel var1, ChunkGenerator var2, BlockPos var3, BlockState var4, Random var5) {
      for(int var6 = 0; var6 >= -1; --var6) {
         for(int var7 = 0; var7 >= -1; --var7) {
            if (isTwoByTwoSapling(var4, var1, var3, var6, var7)) {
               return this.placeMega(var1, var2, var3, var4, var5, var6, var7);
            }
         }
      }

      return super.growTree(var1, var2, var3, var4, var5);
   }

   @Nullable
   protected abstract ConfiguredFeature<?, ?> getConfiguredMegaFeature(Random var1);

   public boolean placeMega(ServerLevel var1, ChunkGenerator var2, BlockPos var3, BlockState var4, Random var5, int var6, int var7) {
      ConfiguredFeature var8 = this.getConfiguredMegaFeature(var5);
      if (var8 == null) {
         return false;
      } else {
         BlockState var9 = Blocks.AIR.defaultBlockState();
         var1.setBlock(var3.offset(var6, 0, var7), var9, 4);
         var1.setBlock(var3.offset(var6 + 1, 0, var7), var9, 4);
         var1.setBlock(var3.offset(var6, 0, var7 + 1), var9, 4);
         var1.setBlock(var3.offset(var6 + 1, 0, var7 + 1), var9, 4);
         if (var8.place(var1, var2, var5, var3.offset(var6, 0, var7))) {
            return true;
         } else {
            var1.setBlock(var3.offset(var6, 0, var7), var4, 4);
            var1.setBlock(var3.offset(var6 + 1, 0, var7), var4, 4);
            var1.setBlock(var3.offset(var6, 0, var7 + 1), var4, 4);
            var1.setBlock(var3.offset(var6 + 1, 0, var7 + 1), var4, 4);
            return false;
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState var0, BlockGetter var1, BlockPos var2, int var3, int var4) {
      Block var5 = var0.getBlock();
      return var1.getBlockState(var2.offset(var3, 0, var4)).is(var5) && var1.getBlockState(var2.offset(var3 + 1, 0, var4)).is(var5) && var1.getBlockState(var2.offset(var3, 0, var4 + 1)).is(var5) && var1.getBlockState(var2.offset(var3 + 1, 0, var4 + 1)).is(var5);
   }
}
