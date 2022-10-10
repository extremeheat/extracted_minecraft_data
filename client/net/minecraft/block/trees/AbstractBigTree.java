package net.minecraft.block.trees;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.AbstractTreeFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public abstract class AbstractBigTree extends AbstractTree {
   public AbstractBigTree() {
      super();
   }

   public boolean func_196935_a(IWorld var1, BlockPos var2, IBlockState var3, Random var4) {
      for(int var5 = 0; var5 >= -1; --var5) {
         for(int var6 = 0; var6 >= -1; --var6) {
            if (func_196937_a(var3, var1, var2, var5, var6)) {
               return this.func_196939_a(var1, var2, var3, var4, var5, var6);
            }
         }
      }

      return super.func_196935_a(var1, var2, var3, var4);
   }

   @Nullable
   protected abstract AbstractTreeFeature<NoFeatureConfig> func_196938_a(Random var1);

   public boolean func_196939_a(IWorld var1, BlockPos var2, IBlockState var3, Random var4, int var5, int var6) {
      AbstractTreeFeature var7 = this.func_196938_a(var4);
      if (var7 == null) {
         return false;
      } else {
         IBlockState var8 = Blocks.field_150350_a.func_176223_P();
         var1.func_180501_a(var2.func_177982_a(var5, 0, var6), var8, 4);
         var1.func_180501_a(var2.func_177982_a(var5 + 1, 0, var6), var8, 4);
         var1.func_180501_a(var2.func_177982_a(var5, 0, var6 + 1), var8, 4);
         var1.func_180501_a(var2.func_177982_a(var5 + 1, 0, var6 + 1), var8, 4);
         if (var7.func_212245_a(var1, var1.func_72863_F().func_201711_g(), var4, var2.func_177982_a(var5, 0, var6), IFeatureConfig.field_202429_e)) {
            return true;
         } else {
            var1.func_180501_a(var2.func_177982_a(var5, 0, var6), var3, 4);
            var1.func_180501_a(var2.func_177982_a(var5 + 1, 0, var6), var3, 4);
            var1.func_180501_a(var2.func_177982_a(var5, 0, var6 + 1), var3, 4);
            var1.func_180501_a(var2.func_177982_a(var5 + 1, 0, var6 + 1), var3, 4);
            return false;
         }
      }
   }

   public static boolean func_196937_a(IBlockState var0, IBlockReader var1, BlockPos var2, int var3, int var4) {
      Block var5 = var0.func_177230_c();
      return var5 == var1.func_180495_p(var2.func_177982_a(var3, 0, var4)).func_177230_c() && var5 == var1.func_180495_p(var2.func_177982_a(var3 + 1, 0, var4)).func_177230_c() && var5 == var1.func_180495_p(var2.func_177982_a(var3, 0, var4 + 1)).func_177230_c() && var5 == var1.func_180495_p(var2.func_177982_a(var3 + 1, 0, var4 + 1)).func_177230_c();
   }
}
