package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralTreeFeature extends CoralFeature {
   public CoralTreeFeature() {
      super();
   }

   protected boolean func_204623_a(IWorld var1, Random var2, BlockPos var3, IBlockState var4) {
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos(var3);
      int var6 = var2.nextInt(3) + 1;

      for(int var7 = 0; var7 < var6; ++var7) {
         if (!this.func_204624_b(var1, var2, var5, var4)) {
            return true;
         }

         var5.func_189536_c(EnumFacing.UP);
      }

      BlockPos var16 = var5.func_185334_h();
      int var8 = var2.nextInt(3) + 2;
      ArrayList var9 = Lists.newArrayList(EnumFacing.Plane.HORIZONTAL);
      Collections.shuffle(var9, var2);
      List var10 = var9.subList(0, var8);
      Iterator var11 = var10.iterator();

      while(var11.hasNext()) {
         EnumFacing var12 = (EnumFacing)var11.next();
         var5.func_189533_g(var16);
         var5.func_189536_c(var12);
         int var13 = var2.nextInt(5) + 2;
         int var14 = 0;

         for(int var15 = 0; var15 < var13 && this.func_204624_b(var1, var2, var5, var4); ++var15) {
            ++var14;
            var5.func_189536_c(EnumFacing.UP);
            if (var15 == 0 || var14 >= 2 && var2.nextFloat() < 0.25F) {
               var5.func_189536_c(var12);
               var14 = 0;
            }
         }
      }

      return true;
   }
}
