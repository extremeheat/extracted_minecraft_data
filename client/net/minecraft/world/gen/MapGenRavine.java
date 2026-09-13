package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class MapGenRavine extends MapGenBase {
   private float[] field_75046_d = new float[1024];

   public MapGenRavine() {
      super();
   }

   protected void func_151540_a(
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
      Random var19 = new Random(var1);
      double var20 = (double)(var3 * 16 + 8);
      double var22 = (double)(var4 * 16 + 8);
      float var24 = 0.0F;
      float var25 = 0.0F;
      if (var16 <= 0) {
         int var26 = this.field_75040_a * 16 - 16;
         var16 = var26 - var19.nextInt(var26 / 4);
      }

      boolean var56 = false;
      if (var15 == -1) {
         var15 = var16 / 2;
         var56 = true;
      }

      float var27 = 1.0F;

      for(int var28 = 0; var28 < 256; ++var28) {
         if (var28 == 0 || var19.nextInt(3) == 0) {
            var27 = 1.0F + var19.nextFloat() * var19.nextFloat() * 1.0F;
         }

         this.field_75046_d[var28] = var27 * var27;
      }

      for(; var15 < var16; ++var15) {
         double var57 = 1.5 + (double)(MathHelper.func_76126_a((float)var15 * 3.1415927F / (float)var16) * var12 * 1.0F);
         double var30 = var57 * var17;
         var57 *= (double)var19.nextFloat() * 0.25 + 0.75;
         var30 *= (double)var19.nextFloat() * 0.25 + 0.75;
         float var32 = MathHelper.func_76134_b(var14);
         float var33 = MathHelper.func_76126_a(var14);
         var6 += (double)(MathHelper.func_76134_b(var13) * var32);
         var8 += (double)var33;
         var10 += (double)(MathHelper.func_76126_a(var13) * var32);
         var14 *= 0.7F;
         var14 += var25 * 0.05F;
         var13 += var24 * 0.05F;
         var25 *= 0.8F;
         var24 *= 0.5F;
         var25 += (var19.nextFloat() - var19.nextFloat()) * var19.nextFloat() * 2.0F;
         var24 += (var19.nextFloat() - var19.nextFloat()) * var19.nextFloat() * 4.0F;
         if (var56 || var19.nextInt(4) != 0) {
            double var34 = var6 - var20;
            double var36 = var10 - var22;
            double var38 = (double)(var16 - var15);
            double var40 = (double)(var12 + 2.0F + 16.0F);
            if (var34 * var34 + var36 * var36 - var38 * var38 > var40 * var40) {
               return;
            }

            if (!(var6 < var20 - 16.0 - var57 * 2.0)
               && !(var10 < var22 - 16.0 - var57 * 2.0)
               && !(var6 > var20 + 16.0 + var57 * 2.0)
               && !(var10 > var22 + 16.0 + var57 * 2.0)) {
               int var60 = MathHelper.func_76128_c(var6 - var57) - var3 * 16 - 1;
               int var35 = MathHelper.func_76128_c(var6 + var57) - var3 * 16 + 1;
               int var61 = MathHelper.func_76128_c(var8 - var30) - 1;
               int var37 = MathHelper.func_76128_c(var8 + var30) + 1;
               int var62 = MathHelper.func_76128_c(var10 - var57) - var4 * 16 - 1;
               int var39 = MathHelper.func_76128_c(var10 + var57) - var4 * 16 + 1;
               if (var60 < 0) {
                  var60 = 0;
               }

               if (var35 > 16) {
                  var35 = 16;
               }

               if (var61 < 1) {
                  var61 = 1;
               }

               if (var37 > 248) {
                  var37 = 248;
               }

               if (var62 < 0) {
                  var62 = 0;
               }

               if (var39 > 16) {
                  var39 = 16;
               }

               boolean var63 = false;

               for(int var41 = var60; !var63 && var41 < var35; ++var41) {
                  for(int var42 = var62; !var63 && var42 < var39; ++var42) {
                     for(int var43 = var37 + 1; !var63 && var43 >= var61 - 1; --var43) {
                        int var44 = (var41 * 16 + var42) * 256 + var43;
                        if (var43 >= 0 && var43 < 256) {
                           Block var45 = var5[var44];
                           if (var45 == Blocks.field_150358_i || var45 == Blocks.field_150355_j) {
                              var63 = true;
                           }

                           if (var43 != var61 - 1 && var41 != var60 && var41 != var35 - 1 && var42 != var62 && var42 != var39 - 1) {
                              var43 = var61;
                           }
                        }
                     }
                  }
               }

               if (!var63) {
                  for(int var64 = var60; var64 < var35; ++var64) {
                     double var65 = ((double)(var64 + var3 * 16) + 0.5 - var6) / var57;

                     for(int var66 = var62; var66 < var39; ++var66) {
                        double var67 = ((double)(var66 + var4 * 16) + 0.5 - var10) / var57;
                        int var47 = (var64 * 16 + var66) * 256 + var37;
                        boolean var48 = false;
                        if (var65 * var65 + var67 * var67 < 1.0) {
                           for(int var49 = var37 - 1; var49 >= var61; --var49) {
                              double var50 = ((double)var49 + 0.5 - var8) / var30;
                              if ((var65 * var65 + var67 * var67) * (double)this.field_75046_d[var49] + var50 * var50 / 6.0 < 1.0) {
                                 Block var52 = var5[var47];
                                 if (var52 == Blocks.field_150349_c) {
                                    var48 = true;
                                 }

                                 if (var52 == Blocks.field_150348_b || var52 == Blocks.field_150346_d || var52 == Blocks.field_150349_c) {
                                    if (var49 < 10) {
                                       var5[var47] = Blocks.field_150356_k;
                                    } else {
                                       var5[var47] = null;
                                       if (var48 && var5[var47 - 1] == Blocks.field_150346_d) {
                                          var5[var47 - 1] = this.field_75039_c.func_72807_a(var64 + var3 * 16, var66 + var4 * 16).field_76752_A;
                                       }
                                    }
                                 }
                              }

                              --var47;
                           }
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
      if (this.field_75038_b.nextInt(50) == 0) {
         double var7 = (double)(var2 * 16 + this.field_75038_b.nextInt(16));
         double var9 = (double)(this.field_75038_b.nextInt(this.field_75038_b.nextInt(40) + 8) + 20);
         double var11 = (double)(var3 * 16 + this.field_75038_b.nextInt(16));
         byte var13 = 1;

         for(int var14 = 0; var14 < var13; ++var14) {
            float var15 = this.field_75038_b.nextFloat() * 3.1415927F * 2.0F;
            float var16 = (this.field_75038_b.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var17 = (this.field_75038_b.nextFloat() * 2.0F + this.field_75038_b.nextFloat()) * 2.0F;
            this.func_151540_a(this.field_75038_b.nextLong(), var4, var5, var6, var7, var9, var11, var17, var15, var16, 0, 0, 3.0);
         }
      }
   }
}
