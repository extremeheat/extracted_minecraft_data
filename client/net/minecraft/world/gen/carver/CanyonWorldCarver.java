package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class CanyonWorldCarver extends WorldCarver<ProbabilityConfig> {
   private final float[] field_202536_i = new float[1024];

   public CanyonWorldCarver() {
      super();
   }

   public boolean func_212246_a(IBlockReader var1, Random var2, int var3, int var4, ProbabilityConfig var5) {
      return var2.nextFloat() <= var5.field_203622_a;
   }

   public boolean func_202522_a(IWorld var1, Random var2, int var3, int var4, int var5, int var6, BitSet var7, ProbabilityConfig var8) {
      int var9 = (this.func_202520_b() * 2 - 1) * 16;
      double var10 = (double)(var3 * 16 + var2.nextInt(16));
      double var12 = (double)(var2.nextInt(var2.nextInt(40) + 8) + 20);
      double var14 = (double)(var4 * 16 + var2.nextInt(16));
      float var16 = var2.nextFloat() * 6.2831855F;
      float var17 = (var2.nextFloat() - 0.5F) * 2.0F / 8.0F;
      double var18 = 3.0D;
      float var20 = (var2.nextFloat() * 2.0F + var2.nextFloat()) * 2.0F;
      int var21 = var9 - var2.nextInt(var9 / 4);
      boolean var22 = false;
      this.func_202535_a(var1, var2.nextLong(), var5, var6, var10, var12, var14, var20, var16, var17, 0, var21, 3.0D, var7);
      return true;
   }

   private void func_202535_a(IWorld var1, long var2, int var4, int var5, double var6, double var8, double var10, float var12, float var13, float var14, int var15, int var16, double var17, BitSet var19) {
      Random var20 = new Random(var2);
      float var21 = 1.0F;

      for(int var22 = 0; var22 < 256; ++var22) {
         if (var22 == 0 || var20.nextInt(3) == 0) {
            var21 = 1.0F + var20.nextFloat() * var20.nextFloat();
         }

         this.field_202536_i[var22] = var21 * var21;
      }

      float var31 = 0.0F;
      float var23 = 0.0F;

      for(int var24 = var15; var24 < var16; ++var24) {
         double var25 = 1.5D + (double)(MathHelper.func_76126_a((float)var24 * 3.1415927F / (float)var16) * var12);
         double var27 = var25 * var17;
         var25 *= (double)var20.nextFloat() * 0.25D + 0.75D;
         var27 *= (double)var20.nextFloat() * 0.25D + 0.75D;
         float var29 = MathHelper.func_76134_b(var14);
         float var30 = MathHelper.func_76126_a(var14);
         var6 += (double)(MathHelper.func_76134_b(var13) * var29);
         var8 += (double)var30;
         var10 += (double)(MathHelper.func_76126_a(var13) * var29);
         var14 *= 0.7F;
         var14 += var23 * 0.05F;
         var13 += var31 * 0.05F;
         var23 *= 0.8F;
         var31 *= 0.5F;
         var23 += (var20.nextFloat() - var20.nextFloat()) * var20.nextFloat() * 2.0F;
         var31 += (var20.nextFloat() - var20.nextFloat()) * var20.nextFloat() * 4.0F;
         if (var20.nextInt(4) != 0) {
            if (!this.func_202515_a(var4, var5, var6, var10, var24, var16, var12)) {
               return;
            }

            this.func_202516_a(var1, var2, var4, var5, var6, var8, var10, var25, var27, var19);
         }
      }

   }

   protected boolean func_202516_a(IWorld var1, long var2, int var4, int var5, double var6, double var8, double var10, double var12, double var14, BitSet var16) {
      double var17 = (double)(var4 * 16 + 8);
      double var19 = (double)(var5 * 16 + 8);
      if (var6 >= var17 - 16.0D - var12 * 2.0D && var10 >= var19 - 16.0D - var12 * 2.0D && var6 <= var17 + 16.0D + var12 * 2.0D && var10 <= var19 + 16.0D + var12 * 2.0D) {
         int var21 = Math.max(MathHelper.func_76128_c(var6 - var12) - var4 * 16 - 1, 0);
         int var22 = Math.min(MathHelper.func_76128_c(var6 + var12) - var4 * 16 + 1, 16);
         int var23 = Math.max(MathHelper.func_76128_c(var8 - var14) - 1, 1);
         int var24 = Math.min(MathHelper.func_76128_c(var8 + var14) + 1, 248);
         int var25 = Math.max(MathHelper.func_76128_c(var10 - var12) - var5 * 16 - 1, 0);
         int var26 = Math.min(MathHelper.func_76128_c(var10 + var12) - var5 * 16 + 1, 16);
         if (this.func_202524_a(var1, var4, var5, var21, var22, var23, var24, var25, var26)) {
            return false;
         } else if (var21 <= var22 && var23 <= var24 && var25 <= var26) {
            boolean var27 = false;
            BlockPos.MutableBlockPos var28 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos var29 = new BlockPos.MutableBlockPos();
            BlockPos.MutableBlockPos var30 = new BlockPos.MutableBlockPos();

            for(int var31 = var21; var31 < var22; ++var31) {
               int var32 = var31 + var4 * 16;
               double var33 = ((double)var32 + 0.5D - var6) / var12;

               for(int var35 = var25; var35 < var26; ++var35) {
                  int var36 = var35 + var5 * 16;
                  double var37 = ((double)var36 + 0.5D - var10) / var12;
                  if (var33 * var33 + var37 * var37 < 1.0D) {
                     boolean var39 = false;

                     for(int var40 = var24; var40 > var23; --var40) {
                        double var41 = ((double)(var40 - 1) + 0.5D - var8) / var14;
                        if ((var33 * var33 + var37 * var37) * (double)this.field_202536_i[var40 - 1] + var41 * var41 / 6.0D < 1.0D) {
                           int var43 = var31 | var35 << 4 | var40 << 8;
                           if (!var16.get(var43)) {
                              var16.set(var43);
                              var28.func_181079_c(var32, var40, var36);
                              IBlockState var44 = var1.func_180495_p(var28);
                              var29.func_189533_g(var28).func_189536_c(EnumFacing.UP);
                              var30.func_189533_g(var28).func_189536_c(EnumFacing.DOWN);
                              IBlockState var45 = var1.func_180495_p(var29);
                              if (var44.func_177230_c() == Blocks.field_196658_i || var44.func_177230_c() == Blocks.field_150391_bh) {
                                 var39 = true;
                              }

                              if (this.func_202517_a(var44, var45)) {
                                 if (var40 - 1 < 10) {
                                    var1.func_180501_a(var28, field_202529_e.func_206883_i(), 2);
                                 } else {
                                    var1.func_180501_a(var28, field_202526_b, 2);
                                    if (var39 && var1.func_180495_p(var30).func_177230_c() == Blocks.field_150346_d) {
                                       var1.func_180501_a(var30, var1.func_180494_b(var28).func_203944_q().func_204108_a(), 2);
                                    }
                                 }

                                 var27 = true;
                              }
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
