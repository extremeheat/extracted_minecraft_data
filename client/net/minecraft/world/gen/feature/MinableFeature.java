package net.minecraft.world.gen.feature;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class MinableFeature extends Feature<MinableConfig> {
   public MinableFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, MinableConfig var5) {
      float var6 = var3.nextFloat() * 3.1415927F;
      float var7 = (float)var5.field_202443_c / 8.0F;
      int var8 = MathHelper.func_76123_f(((float)var5.field_202443_c / 16.0F * 2.0F + 1.0F) / 2.0F);
      double var9 = (double)((float)var4.func_177958_n() + MathHelper.func_76126_a(var6) * var7);
      double var11 = (double)((float)var4.func_177958_n() - MathHelper.func_76126_a(var6) * var7);
      double var13 = (double)((float)var4.func_177952_p() + MathHelper.func_76134_b(var6) * var7);
      double var15 = (double)((float)var4.func_177952_p() - MathHelper.func_76134_b(var6) * var7);
      boolean var17 = true;
      double var18 = (double)(var4.func_177956_o() + var3.nextInt(3) - 2);
      double var20 = (double)(var4.func_177956_o() + var3.nextInt(3) - 2);
      int var22 = var4.func_177958_n() - MathHelper.func_76123_f(var7) - var8;
      int var23 = var4.func_177956_o() - 2 - var8;
      int var24 = var4.func_177952_p() - MathHelper.func_76123_f(var7) - var8;
      int var25 = 2 * (MathHelper.func_76123_f(var7) + var8);
      int var26 = 2 * (2 + var8);

      for(int var27 = var22; var27 <= var22 + var25; ++var27) {
         for(int var28 = var24; var28 <= var24 + var25; ++var28) {
            if (var23 <= var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, var27, var28)) {
               return this.func_207803_a(var1, var3, var5, var9, var11, var13, var15, var18, var20, var22, var23, var24, var25, var26);
            }
         }
      }

      return false;
   }

   protected boolean func_207803_a(IWorld var1, Random var2, MinableConfig var3, double var4, double var6, double var8, double var10, double var12, double var14, int var16, int var17, int var18, int var19, int var20) {
      int var21 = 0;
      BitSet var22 = new BitSet(var19 * var20 * var19);
      BlockPos.MutableBlockPos var23 = new BlockPos.MutableBlockPos();
      double[] var24 = new double[var3.field_202443_c * 4];

      int var25;
      double var27;
      double var29;
      double var31;
      double var33;
      for(var25 = 0; var25 < var3.field_202443_c; ++var25) {
         float var26 = (float)var25 / (float)var3.field_202443_c;
         var27 = var4 + (var6 - var4) * (double)var26;
         var29 = var12 + (var14 - var12) * (double)var26;
         var31 = var8 + (var10 - var8) * (double)var26;
         var33 = var2.nextDouble() * (double)var3.field_202443_c / 16.0D;
         double var35 = ((double)(MathHelper.func_76126_a(3.1415927F * var26) + 1.0F) * var33 + 1.0D) / 2.0D;
         var24[var25 * 4 + 0] = var27;
         var24[var25 * 4 + 1] = var29;
         var24[var25 * 4 + 2] = var31;
         var24[var25 * 4 + 3] = var35;
      }

      for(var25 = 0; var25 < var3.field_202443_c - 1; ++var25) {
         if (var24[var25 * 4 + 3] > 0.0D) {
            for(int var50 = var25 + 1; var50 < var3.field_202443_c; ++var50) {
               if (var24[var50 * 4 + 3] > 0.0D) {
                  var27 = var24[var25 * 4 + 0] - var24[var50 * 4 + 0];
                  var29 = var24[var25 * 4 + 1] - var24[var50 * 4 + 1];
                  var31 = var24[var25 * 4 + 2] - var24[var50 * 4 + 2];
                  var33 = var24[var25 * 4 + 3] - var24[var50 * 4 + 3];
                  if (var33 * var33 > var27 * var27 + var29 * var29 + var31 * var31) {
                     if (var33 > 0.0D) {
                        var24[var50 * 4 + 3] = -1.0D;
                     } else {
                        var24[var25 * 4 + 3] = -1.0D;
                     }
                  }
               }
            }
         }
      }

      for(var25 = 0; var25 < var3.field_202443_c; ++var25) {
         double var51 = var24[var25 * 4 + 3];
         if (var51 >= 0.0D) {
            double var28 = var24[var25 * 4 + 0];
            double var30 = var24[var25 * 4 + 1];
            double var32 = var24[var25 * 4 + 2];
            int var34 = Math.max(MathHelper.func_76128_c(var28 - var51), var16);
            int var52 = Math.max(MathHelper.func_76128_c(var30 - var51), var17);
            int var36 = Math.max(MathHelper.func_76128_c(var32 - var51), var18);
            int var37 = Math.max(MathHelper.func_76128_c(var28 + var51), var34);
            int var38 = Math.max(MathHelper.func_76128_c(var30 + var51), var52);
            int var39 = Math.max(MathHelper.func_76128_c(var32 + var51), var36);

            for(int var40 = var34; var40 <= var37; ++var40) {
               double var41 = ((double)var40 + 0.5D - var28) / var51;
               if (var41 * var41 < 1.0D) {
                  for(int var43 = var52; var43 <= var38; ++var43) {
                     double var44 = ((double)var43 + 0.5D - var30) / var51;
                     if (var41 * var41 + var44 * var44 < 1.0D) {
                        for(int var46 = var36; var46 <= var39; ++var46) {
                           double var47 = ((double)var46 + 0.5D - var32) / var51;
                           if (var41 * var41 + var44 * var44 + var47 * var47 < 1.0D) {
                              int var49 = var40 - var16 + (var43 - var17) * var19 + (var46 - var18) * var19 * var20;
                              if (!var22.get(var49)) {
                                 var22.set(var49);
                                 var23.func_181079_c(var40, var43, var46);
                                 if (var3.field_202442_b.test(var1.func_180495_p(var23))) {
                                    var1.func_180501_a(var23, var3.field_202444_d, 2);
                                    ++var21;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var21 > 0;
   }
}
