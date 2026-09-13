package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MapGenCavesHell extends MapGenBase {
   public MapGenCavesHell() {
      super();
   }

   protected void func_151544_a(long var1, int var3, int var4, Block[] var5, double var6, double var8, double var10) {
      this.func_151543_a(var1, var3, var4, var5, var6, var8, var10, 1.0F + this.field_75038_b.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
   }

   protected void func_151543_a(
      long var1,
      int var3,
      int var4,
      Block[] var5,
      double var6,
      double var8,
      double var10,
      float var12,
      float var13,
      float var14,
      int var15,
      int var16,
      double var17
   ) {
      double var19 = (double)(var3 * 16 + 8);
      double var21 = (double)(var4 * 16 + 8);
      float var23 = 0.0F;
      float var24 = 0.0F;
      Random var25 = new Random(var1);
      if (var16 <= 0) {
         int var26 = this.field_75040_a * 16 - 16;
         var16 = var26 - var25.nextInt(var26 / 4);
      }

      boolean var56 = false;
      if (var15 == -1) {
         var15 = var16 / 2;
         var56 = true;
      }

      int var27 = var25.nextInt(var16 / 2) + var16 / 4;

      for(boolean var28 = var25.nextInt(6) == 0; var15 < var16; ++var15) {
         double var29 = 1.5 + (double)(MathHelper.func_76126_a((float)var15 * 3.1415927F / (float)var16) * var12 * 1.0F);
         double var31 = var29 * var17;
         float var33 = MathHelper.func_76134_b(var14);
         float var34 = MathHelper.func_76126_a(var14);
         var6 += (double)(MathHelper.func_76134_b(var13) * var33);
         var8 += (double)var34;
         var10 += (double)(MathHelper.func_76126_a(var13) * var33);
         if (var28) {
            var14 *= 0.92F;
         } else {
            var14 *= 0.7F;
         }

         var14 += var24 * 0.1F;
         var13 += var23 * 0.1F;
         var24 *= 0.9F;
         var23 *= 0.75F;
         var24 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 2.0F;
         var23 += (var25.nextFloat() - var25.nextFloat()) * var25.nextFloat() * 4.0F;
         if (!var56 && var15 == var27 && var12 > 1.0F) {
            this.func_151543_a(
               var25.nextLong(), var3, var4, var5, var6, var8, var10, var25.nextFloat() * 0.5F + 0.5F, var13 - 1.5707964F, var14 / 3.0F, var15, var16, 1.0
            );
            this.func_151543_a(
               var25.nextLong(), var3, var4, var5, var6, var8, var10, var25.nextFloat() * 0.5F + 0.5F, var13 + 1.5707964F, var14 / 3.0F, var15, var16, 1.0
            );
            return;
         }

         if (var56 || var25.nextInt(4) != 0) {
            double var35 = var6 - var19;
            double var37 = var10 - var21;
            double var39 = (double)(var16 - var15);
            double var41 = (double)(var12 + 2.0F + 16.0F);
            if (var35 * var35 + var37 * var37 - var39 * var39 > var41 * var41) {
               return;
            }

            if (!(var6 < var19 - 16.0 - var29 * 2.0)
               && !(var10 < var21 - 16.0 - var29 * 2.0)
               && !(var6 > var19 + 16.0 + var29 * 2.0)
               && !(var10 > var21 + 16.0 + var29 * 2.0)) {
               int var57 = MathHelper.func_76128_c(var6 - var29) - var3 * 16 - 1;
               int var36 = MathHelper.func_76128_c(var6 + var29) - var3 * 16 + 1;
               int var58 = MathHelper.func_76128_c(var8 - var31) - 1;
               int var38 = MathHelper.func_76128_c(var8 + var31) + 1;
               int var59 = MathHelper.func_76128_c(var10 - var29) - var4 * 16 - 1;
               int var40 = MathHelper.func_76128_c(var10 + var29) - var4 * 16 + 1;
               if (var57 < 0) {
                  var57 = 0;
               }

               if (var36 > 16) {
                  var36 = 16;
               }

               if (var58 < 1) {
                  var58 = 1;
               }

               if (var38 > 120) {
                  var38 = 120;
               }

               if (var59 < 0) {
                  var59 = 0;
               }

               if (var40 > 16) {
                  var40 = 16;
               }

               boolean var60 = false;

               for(int var42 = var57; !var60 && var42 < var36; ++var42) {
                  for(int var43 = var59; !var60 && var43 < var40; ++var43) {
                     for(int var44 = var38 + 1; !var60 && var44 >= var58 - 1; --var44) {
                        int var45 = (var42 * 16 + var43) * 128 + var44;
                        if (var44 >= 0 && var44 < 128) {
                           Block var46 = var5[var45];
                           if (var46 == Blocks.field_150356_k || var46 == Blocks.field_150353_l) {
                              var60 = true;
                           }

                           if (var44 != var58 - 1 && var42 != var57 && var42 != var36 - 1 && var43 != var59 && var43 != var40 - 1) {
                              var44 = var58;
                           }
                        }
                     }
                  }
               }

               if (!var60) {
                  for(int var61 = var57; var61 < var36; ++var61) {
                     double var62 = ((double)(var61 + var3 * 16) + 0.5 - var6) / var29;

                     for(int var63 = var59; var63 < var40; ++var63) {
                        double var64 = ((double)(var63 + var4 * 16) + 0.5 - var10) / var29;
                        int var48 = (var61 * 16 + var63) * 128 + var38;

                        for(int var49 = var38 - 1; var49 >= var58; --var49) {
                           double var50 = ((double)var49 + 0.5 - var8) / var31;
                           if (var50 > -0.7 && var62 * var62 + var50 * var50 + var64 * var64 < 1.0) {
                              Block var52 = var5[var48];
                              if (var52 == Blocks.field_150424_aL || var52 == Blocks.field_150346_d || var52 == Blocks.field_150349_c) {
                                 var5[var48] = null;
                              }
                           }

                           --var48;
                        }
                     }
                  }

                  if (var56) {
                     break;
                  }
               }
            }
         }
      }
   }

   @Override
   protected void func_151538_a(World var1, int var2, int var3, int var4, int var5, Block[] var6) {
      int var7 = this.field_75038_b.nextInt(this.field_75038_b.nextInt(this.field_75038_b.nextInt(10) + 1) + 1);
      if (this.field_75038_b.nextInt(5) != 0) {
         var7 = 0;
      }

      for(int var8 = 0; var8 < var7; ++var8) {
         double var9 = (double)(var2 * 16 + this.field_75038_b.nextInt(16));
         double var11 = (double)this.field_75038_b.nextInt(128);
         double var13 = (double)(var3 * 16 + this.field_75038_b.nextInt(16));
         int var15 = 1;
         if (this.field_75038_b.nextInt(4) == 0) {
            this.func_151544_a(this.field_75038_b.nextLong(), var4, var5, var6, var9, var11, var13);
            var15 += this.field_75038_b.nextInt(4);
         }

         for(int var16 = 0; var16 < var15; ++var16) {
            float var17 = this.field_75038_b.nextFloat() * 3.1415927F * 2.0F;
            float var18 = (this.field_75038_b.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var19 = this.field_75038_b.nextFloat() * 2.0F + this.field_75038_b.nextFloat();
            this.func_151543_a(this.field_75038_b.nextLong(), var4, var5, var6, var9, var11, var13, var19 * 2.0F, var17, var18, 0, 0, 0.5);
         }
      }
   }
}
