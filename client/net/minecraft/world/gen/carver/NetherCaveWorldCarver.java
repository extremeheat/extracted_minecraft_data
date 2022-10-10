package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class NetherCaveWorldCarver extends CaveWorldCarver {
   public NetherCaveWorldCarver() {
      super();
      this.field_202531_g = ImmutableSet.of(Blocks.field_150348_b, Blocks.field_196650_c, Blocks.field_196654_e, Blocks.field_196656_g, Blocks.field_150346_d, Blocks.field_196660_k, new Block[]{Blocks.field_196661_l, Blocks.field_196658_i, Blocks.field_150424_aL});
      this.field_204634_f = ImmutableSet.of(Fluids.field_204547_b, Fluids.field_204546_a);
   }

   public boolean func_212246_a(IBlockReader var1, Random var2, int var3, int var4, ProbabilityConfig var5) {
      return var2.nextFloat() <= var5.field_203622_a;
   }

   public boolean func_202522_a(IWorld var1, Random var2, int var3, int var4, int var5, int var6, BitSet var7, ProbabilityConfig var8) {
      int var9 = (this.func_202520_b() * 2 - 1) * 16;
      int var10 = var2.nextInt(var2.nextInt(var2.nextInt(10) + 1) + 1);

      for(int var11 = 0; var11 < var10; ++var11) {
         double var12 = (double)(var3 * 16 + var2.nextInt(16));
         double var14 = (double)var2.nextInt(128);
         double var16 = (double)(var4 * 16 + var2.nextInt(16));
         int var18 = 1;
         float var21;
         if (var2.nextInt(4) == 0) {
            double var19 = 0.5D;
            var21 = 1.0F + var2.nextFloat() * 6.0F;
            this.func_203627_a(var1, var2.nextLong(), var5, var6, var12, var14, var16, var21, 0.5D, var7);
            var18 += var2.nextInt(4);
         }

         for(int var27 = 0; var27 < var18; ++var27) {
            float var20 = var2.nextFloat() * 6.2831855F;
            var21 = (var2.nextFloat() - 0.5F) * 2.0F / 8.0F;
            double var22 = 5.0D;
            float var24 = (var2.nextFloat() * 2.0F + var2.nextFloat()) * 2.0F;
            int var25 = var9 - var2.nextInt(var9 / 4);
            boolean var26 = false;
            this.func_202533_a(var1, var2.nextLong(), var5, var6, var12, var14, var16, var24, var20, var21, 0, var25, 5.0D, var7);
         }
      }

      return true;
   }

   protected boolean func_202516_a(IWorld var1, long var2, int var4, int var5, double var6, double var8, double var10, double var12, double var14, BitSet var16) {
      double var17 = (double)(var4 * 16 + 8);
      double var19 = (double)(var5 * 16 + 8);
      if (var6 >= var17 - 16.0D - var12 * 2.0D && var10 >= var19 - 16.0D - var12 * 2.0D && var6 <= var17 + 16.0D + var12 * 2.0D && var10 <= var19 + 16.0D + var12 * 2.0D) {
         int var21 = Math.max(MathHelper.func_76128_c(var6 - var12) - var4 * 16 - 1, 0);
         int var22 = Math.min(MathHelper.func_76128_c(var6 + var12) - var4 * 16 + 1, 16);
         int var23 = Math.max(MathHelper.func_76128_c(var8 - var14) - 1, 1);
         int var24 = Math.min(MathHelper.func_76128_c(var8 + var14) + 1, 120);
         int var25 = Math.max(MathHelper.func_76128_c(var10 - var12) - var5 * 16 - 1, 0);
         int var26 = Math.min(MathHelper.func_76128_c(var10 + var12) - var5 * 16 + 1, 16);
         if (this.func_202524_a(var1, var4, var5, var21, var22, var23, var24, var25, var26)) {
            return false;
         } else if (var21 <= var22 && var23 <= var24 && var25 <= var26) {
            boolean var27 = false;

            for(int var28 = var21; var28 < var22; ++var28) {
               int var29 = var28 + var4 * 16;
               double var30 = ((double)var29 + 0.5D - var6) / var12;

               for(int var32 = var25; var32 < var26; ++var32) {
                  int var33 = var32 + var5 * 16;
                  double var34 = ((double)var33 + 0.5D - var10) / var12;

                  for(int var36 = var24; var36 > var23; --var36) {
                     double var37 = ((double)(var36 - 1) + 0.5D - var8) / var14;
                     if (var37 > -0.7D && var30 * var30 + var37 * var37 + var34 * var34 < 1.0D) {
                        int var39 = var28 | var32 << 4 | var36 << 8;
                        if (!var16.get(var39)) {
                           var16.set(var39);
                           if (this.func_202519_b(var1.func_180495_p(new BlockPos(var29, var36, var33)))) {
                              if (var36 <= 31) {
                                 var1.func_180501_a(new BlockPos(var29, var36, var33), field_202529_e.func_206883_i(), 2);
                              } else {
                                 var1.func_180501_a(new BlockPos(var29, var36, var33), field_202526_b, 2);
                              }

                              var27 = true;
                           }
                        }
                     }
                  }
               }
            }

            return var27;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
