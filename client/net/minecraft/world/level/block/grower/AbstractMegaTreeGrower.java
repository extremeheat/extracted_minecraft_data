package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public abstract class AbstractMegaTreeGrower extends AbstractTreeGrower {
   public AbstractMegaTreeGrower() {
      super();
   }

   public boolean growTree(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      for(int var5 = 0; var5 >= -1; --var5) {
         for(int var6 = 0; var6 >= -1; --var6) {
            if (isTwoByTwoSapling(var3, var1, var2, var5, var6)) {
               return this.placeMega(var1, var2, var3, var4, var5, var6);
            }
         }
      }

      return super.growTree(var1, var2, var3, var4);
   }

   @Nullable
   protected abstract AbstractTreeFeature<NoneFeatureConfiguration> getMegaFeature(Random var1);

   public boolean placeMega(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4, int var5, int var6) {
      AbstractTreeFeature var7 = this.getMegaFeature(var4);
      if (var7 == null) {
         return false;
      } else {
         BlockState var8 = Blocks.AIR.defaultBlockState();
         var1.setBlock(var2.offset(var5, 0, var6), var8, 4);
         var1.setBlock(var2.offset(var5 + 1, 0, var6), var8, 4);
         var1.setBlock(var2.offset(var5, 0, var6 + 1), var8, 4);
         var1.setBlock(var2.offset(var5 + 1, 0, var6 + 1), var8, 4);
         if (var7.place(var1, var1.getChunkSource().getGenerator(), var4, var2.offset(var5, 0, var6), FeatureConfiguration.NONE)) {
            return true;
         } else {
            var1.setBlock(var2.offset(var5, 0, var6), var3, 4);
            var1.setBlock(var2.offset(var5 + 1, 0, var6), var3, 4);
            var1.setBlock(var2.offset(var5, 0, var6 + 1), var3, 4);
            var1.setBlock(var2.offset(var5 + 1, 0, var6 + 1), var3, 4);
            return false;
         }
      }
   }

   public static boolean isTwoByTwoSapling(BlockState var0, BlockGetter var1, BlockPos var2, int var3, int var4) {
      Block var5 = var0.getBlock();
      return var5 == var1.getBlockState(var2.offset(var3, 0, var4)).getBlock() && var5 == var1.getBlockState(var2.offset(var3 + 1, 0, var4)).getBlock() && var5 == var1.getBlockState(var2.offset(var3, 0, var4 + 1)).getBlock() && var5 == var1.getBlockState(var2.offset(var3 + 1, 0, var4 + 1)).getBlock();
   }
}
