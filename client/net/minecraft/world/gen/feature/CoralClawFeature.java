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

public class CoralClawFeature extends CoralFeature {
   public CoralClawFeature() {
      super();
   }

   protected boolean func_204623_a(IWorld var1, Random var2, BlockPos var3, IBlockState var4) {
      if (!this.func_204624_b(var1, var2, var3, var4)) {
         return false;
      } else {
         EnumFacing var5 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
         int var6 = var2.nextInt(2) + 2;
         ArrayList var7 = Lists.newArrayList(new EnumFacing[]{var5, var5.func_176746_e(), var5.func_176735_f()});
         Collections.shuffle(var7, var2);
         List var8 = var7.subList(0, var6);
         Iterator var9 = var8.iterator();

         while(var9.hasNext()) {
            EnumFacing var10 = (EnumFacing)var9.next();
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos(var3);
            int var12 = var2.nextInt(2) + 1;
            var11.func_189536_c(var10);
            int var13;
            EnumFacing var14;
            if (var10 == var5) {
               var14 = var5;
               var13 = var2.nextInt(3) + 2;
            } else {
               var11.func_189536_c(EnumFacing.UP);
               EnumFacing[] var15 = new EnumFacing[]{var10, EnumFacing.UP};
               var14 = var15[var2.nextInt(var15.length)];
               var13 = var2.nextInt(3) + 3;
            }

            int var16;
            for(var16 = 0; var16 < var12 && this.func_204624_b(var1, var2, var11, var4); ++var16) {
               var11.func_189536_c(var14);
            }

            var11.func_189536_c(var14.func_176734_d());
            var11.func_189536_c(EnumFacing.UP);

            for(var16 = 0; var16 < var13; ++var16) {
               var11.func_189536_c(var5);
               if (!this.func_204624_b(var1, var2, var11, var4)) {
                  break;
               }

               if (var2.nextFloat() < 0.25F) {
                  var11.func_189536_c(EnumFacing.UP);
               }
            }
         }

         return true;
      }
   }
}
