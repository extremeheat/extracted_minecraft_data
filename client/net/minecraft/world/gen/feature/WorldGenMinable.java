package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMinable extends WorldGenerator {
   private final IBlockState field_175920_a;
   private final int field_76541_b;
   private final Predicate<IBlockState> field_175919_c;

   public WorldGenMinable(IBlockState var1, int var2) {
      this(var1, var2, BlockHelper.func_177642_a(Blocks.field_150348_b));
   }

   public WorldGenMinable(IBlockState var1, int var2, Predicate<IBlockState> var3) {
      super();
      this.field_175920_a = var1;
      this.field_76541_b = var2;
      this.field_175919_c = var3;
   }

   public boolean func_180709_b(World var1, Random var2, BlockPos var3) {
      float var4 = var2.nextFloat() * 3.1415927F;
      double var5 = (double)((float)(var3.func_177958_n() + 8) + MathHelper.func_76126_a(var4) * (float)this.field_76541_b / 8.0F);
      double var7 = (double)((float)(var3.func_177958_n() + 8) - MathHelper.func_76126_a(var4) * (float)this.field_76541_b / 8.0F);
      double var9 = (double)((float)(var3.func_177952_p() + 8) + MathHelper.func_76134_b(var4) * (float)this.field_76541_b / 8.0F);
      double var11 = (double)((float)(var3.func_177952_p() + 8) - MathHelper.func_76134_b(var4) * (float)this.field_76541_b / 8.0F);
      double var13 = (double)(var3.func_177956_o() + var2.nextInt(3) - 2);
      double var15 = (double)(var3.func_177956_o() + var2.nextInt(3) - 2);

      for(int var17 = 0; var17 < this.field_76541_b; ++var17) {
         float var18 = (float)var17 / (float)this.field_76541_b;
         double var19 = var5 + (var7 - var5) * (double)var18;
         double var21 = var13 + (var15 - var13) * (double)var18;
         double var23 = var9 + (var11 - var9) * (double)var18;
         double var25 = var2.nextDouble() * (double)this.field_76541_b / 16.0D;
         double var27 = (double)(MathHelper.func_76126_a(3.1415927F * var18) + 1.0F) * var25 + 1.0D;
         double var29 = (double)(MathHelper.func_76126_a(3.1415927F * var18) + 1.0F) * var25 + 1.0D;
         int var31 = MathHelper.func_76128_c(var19 - var27 / 2.0D);
         int var32 = MathHelper.func_76128_c(var21 - var29 / 2.0D);
         int var33 = MathHelper.func_76128_c(var23 - var27 / 2.0D);
         int var34 = MathHelper.func_76128_c(var19 + var27 / 2.0D);
         int var35 = MathHelper.func_76128_c(var21 + var29 / 2.0D);
         int var36 = MathHelper.func_76128_c(var23 + var27 / 2.0D);

         for(int var37 = var31; var37 <= var34; ++var37) {
            double var38 = ((double)var37 + 0.5D - var19) / (var27 / 2.0D);
            if (var38 * var38 < 1.0D) {
               for(int var40 = var32; var40 <= var35; ++var40) {
                  double var41 = ((double)var40 + 0.5D - var21) / (var29 / 2.0D);
                  if (var38 * var38 + var41 * var41 < 1.0D) {
                     for(int var43 = var33; var43 <= var36; ++var43) {
                        double var44 = ((double)var43 + 0.5D - var23) / (var27 / 2.0D);
                        if (var38 * var38 + var41 * var41 + var44 * var44 < 1.0D) {
                           BlockPos var46 = new BlockPos(var37, var40, var43);
                           if (this.field_175919_c.apply(var1.func_180495_p(var46))) {
                              var1.func_180501_a(var46, this.field_175920_a, 2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
