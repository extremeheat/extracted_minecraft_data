package net.minecraft.world.gen.carver;

import com.google.common.collect.ImmutableSet;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.ProbabilityConfig;

public class UnderwaterCanyonWorldCarver extends CanyonWorldCarver {
   private final float[] field_203628_i = new float[1024];

   public UnderwaterCanyonWorldCarver() {
      super();
      this.field_202531_g = ImmutableSet.of(Blocks.field_150348_b, Blocks.field_196650_c, Blocks.field_196654_e, Blocks.field_196656_g, Blocks.field_150346_d, Blocks.field_196660_k, new Block[]{Blocks.field_196661_l, Blocks.field_196658_i, Blocks.field_150405_ch, Blocks.field_196777_fo, Blocks.field_196778_fp, Blocks.field_196780_fq, Blocks.field_196782_fr, Blocks.field_196783_fs, Blocks.field_196785_ft, Blocks.field_196787_fu, Blocks.field_196789_fv, Blocks.field_196791_fw, Blocks.field_196793_fx, Blocks.field_196795_fy, Blocks.field_196797_fz, Blocks.field_196719_fA, Blocks.field_196720_fB, Blocks.field_196721_fC, Blocks.field_196722_fD, Blocks.field_150322_A, Blocks.field_180395_cM, Blocks.field_150391_bh, Blocks.field_150433_aE, Blocks.field_150354_m, Blocks.field_150351_n, Blocks.field_150355_j, Blocks.field_150353_l, Blocks.field_150343_Z, Blocks.field_150350_a, Blocks.field_201941_jj});
   }

   public boolean func_212246_a(IBlockReader var1, Random var2, int var3, int var4, ProbabilityConfig var5) {
      return var2.nextFloat() <= var5.field_203622_a;
   }

   protected boolean func_202516_a(IWorld var1, long var2, int var4, int var5, double var6, double var8, double var10, double var12, double var14, BitSet var16) {
      Random var17 = new Random(var2 + (long)var4 + (long)var5);
      double var18 = (double)(var4 * 16 + 8);
      double var20 = (double)(var5 * 16 + 8);
      if (var6 >= var18 - 16.0D - var12 * 2.0D && var10 >= var20 - 16.0D - var12 * 2.0D && var6 <= var18 + 16.0D + var12 * 2.0D && var10 <= var20 + 16.0D + var12 * 2.0D) {
         int var22 = Math.max(MathHelper.func_76128_c(var6 - var12) - var4 * 16 - 1, 0);
         int var23 = Math.min(MathHelper.func_76128_c(var6 + var12) - var4 * 16 + 1, 16);
         int var24 = Math.max(MathHelper.func_76128_c(var8 - var14) - 1, 1);
         int var25 = Math.min(MathHelper.func_76128_c(var8 + var14) + 1, 248);
         int var26 = Math.max(MathHelper.func_76128_c(var10 - var12) - var5 * 16 - 1, 0);
         int var27 = Math.min(MathHelper.func_76128_c(var10 + var12) - var5 * 16 + 1, 16);
         if (var22 <= var23 && var24 <= var25 && var26 <= var27) {
            boolean var28 = false;
            BlockPos.MutableBlockPos var29 = new BlockPos.MutableBlockPos();

            for(int var30 = var22; var30 < var23; ++var30) {
               int var31 = var30 + var4 * 16;
               double var32 = ((double)var31 + 0.5D - var6) / var12;

               for(int var34 = var26; var34 < var27; ++var34) {
                  int var35 = var34 + var5 * 16;
                  double var36 = ((double)var35 + 0.5D - var10) / var12;
                  if (var32 * var32 + var36 * var36 < 1.0D) {
                     for(int var38 = var25; var38 > var24; --var38) {
                        double var39 = ((double)(var38 - 1) + 0.5D - var8) / var14;
                        if ((var32 * var32 + var36 * var36) * (double)this.field_203628_i[var38 - 1] + var39 * var39 / 6.0D < 1.0D && var38 < var1.func_181545_F()) {
                           int var41 = var30 | var34 << 4 | var38 << 8;
                           if (!var16.get(var41)) {
                              var16.set(var41);
                              var29.func_181079_c(var31, var38, var35);
                              IBlockState var42 = var1.func_180495_p(var29);
                              if (this.func_202519_b(var42)) {
                                 if (var38 == 10) {
                                    float var47 = var17.nextFloat();
                                    if ((double)var47 < 0.25D) {
                                       var1.func_180501_a(var29, Blocks.field_196814_hQ.func_176223_P(), 2);
                                       var1.func_205220_G_().func_205360_a(var29, Blocks.field_196814_hQ, 0);
                                       var28 = true;
                                    } else {
                                       var1.func_180501_a(var29, Blocks.field_150343_Z.func_176223_P(), 2);
                                       var28 = true;
                                    }
                                 } else if (var38 < 10) {
                                    var1.func_180501_a(var29, Blocks.field_150353_l.func_176223_P(), 2);
                                 } else {
                                    boolean var43 = false;
                                    Iterator var44 = EnumFacing.Plane.HORIZONTAL.iterator();

                                    while(var44.hasNext()) {
                                       EnumFacing var45 = (EnumFacing)var44.next();
                                       IBlockState var46 = var1.func_180495_p(var29.func_181079_c(var31 + var45.func_82601_c(), var38, var35 + var45.func_82599_e()));
                                       if (var46.func_196958_f()) {
                                          var1.func_180501_a(var29, field_202527_c.func_206883_i(), 2);
                                          var1.func_205219_F_().func_205360_a(var29, field_202527_c.func_206886_c(), 0);
                                          var28 = true;
                                          var43 = true;
                                          break;
                                       }
                                    }

                                    var29.func_181079_c(var31, var38, var35);
                                    if (!var43) {
                                       var1.func_180501_a(var29, field_202527_c.func_206883_i(), 2);
                                       var28 = true;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return var28;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
