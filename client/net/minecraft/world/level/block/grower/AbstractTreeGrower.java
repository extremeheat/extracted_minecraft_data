package net.minecraft.world.level.block.grower;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;

public abstract class AbstractTreeGrower {
   public AbstractTreeGrower() {
      super();
   }

   @Nullable
   protected abstract AbstractTreeFeature<NoneFeatureConfiguration> getFeature(Random var1);

   public boolean growTree(LevelAccessor var1, BlockPos var2, BlockState var3, Random var4) {
      AbstractTreeFeature var5 = this.getFeature(var4);
      if (var5 == null) {
         return false;
      } else {
         var1.setBlock(var2, Blocks.AIR.defaultBlockState(), 4);
         if (var5.place(var1, var1.getChunkSource().getGenerator(), var4, var2, FeatureConfiguration.NONE)) {
            return true;
         } else {
            var1.setBlock(var2, var3, 4);
            return false;
         }
      }
   }
}
