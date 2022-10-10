package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class AbstractTree {
   public AbstractTree() {
      super();
   }

   @Nullable
   protected abstract AbstractTreeFeature<NoFeatureConfig> func_196936_b(Random var1);

   public boolean func_196935_a(IWorld var1, BlockPos var2, IBlockState var3, Random var4) {
      AbstractTreeFeature var5 = this.func_196936_b(var4);
      if (var5 == null) {
         return false;
      } else {
         var1.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 4);
         if (var5.func_212245_a(var1, var1.func_72863_F().func_201711_g(), var4, var2, IFeatureConfig.field_202429_e)) {
            return true;
         } else {
            var1.func_180501_a(var2, var3, 4);
            return false;
         }
      }
   }
}
