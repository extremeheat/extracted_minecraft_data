package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;

public class MapGenRavine extends MapGenBase {
   private float[] field_75046_d = new float[1024];

   public MapGenRavine() {
      super();
   }

   protected void func_180707_a(long var1, int var3, int var4, ChunkPrimer var5, double var6, double var8, double var10, float var12, float var13, float var14, int var15, int var16, double var17) {
      Random var19 = new Random(var1);
      double var20 = (double)(var3 * 16 + 8);
      double var22 = (double)(var4 * 16 + 8);
      float var24 = 0.0F;
      float var25 = 0.0F;
      if (var16 <= 0) {
         int var26 = this.field_75040_a * 16 - 16;
         var16 = var26 - var19.nextInt(var26 / 4);
      }

      boolean var53 = false;
      if (var15 == -1) {
         var15 = var16 / 2;
         var53 = true;
      }

      float var27 = 1.0F;

      for(int var28 = 0; var28 < 256; ++var28) {
         if (var28 == 0 || var19.nextInt(3) == 0) {
            var27 = 1.0F + var19.nextFloat() * var19.nextFloat() * 1.0F;
         }

         this.field_75046_d[var28] = var27 * var27;
      }

      for(; var15 < var16; ++var15) {
         double var54 = 1.5D + (double)(MathHelper.func_76126_a((float)var15 * 3.1415927F / (float)var16) * var12 * 1.0F);
         double var30 = var54 * var17;
         var54 *= (double)var19.nextFloat() * 0.25D + 0.75D;
         var30 *= (double)var19.nextFloat() * 0.25D + 0.75D;
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
         if (var53 || var19.nextInt(4) != 0) {
            double var34 = var6 - var20;
            double var36 = var10 - var22;
            double var38 = (double)(var16 - var15);
            double var40 = (double)(var12 + 2.0F + 16.0F);
            if (var34 * var34 + var36 * var36 - var38 * var38 > var40 * var40) {
               return;
            }

            if (var6 >= var20 - 16.0D - var54 * 2.0D && var10 >= var22 - 16.0D - var54 * 2.0D && var6 <= var20 + 16.0D + var54 * 2.0D && var10 <= var22 + 16.0D + var54 * 2.0D) {
               int var55 = MathHelper.func_76128_c(var6 - var54) - var3 * 16 - 1;
               int var35 = MathHelper.func_76128_c(var6 + var54) - var3 * 16 + 1;
               int var56 = MathHelper.func_76128_c(var8 - var30) - 1;
               int var37 = MathHelper.func_76128_c(var8 + var30) + 1;
               int var57 = MathHelper.func_76128_c(var10 - var54) - var4 * 16 - 1;
               int var39 = MathHelper.func_76128_c(var10 + var54) - var4 * 16 + 1;
               if (var55 < 0) {
                  var55 = 0;
               }

               if (var35 > 16) {
                  var35 = 16;
               }

               if (var56 < 1) {
                  var56 = 1;
               }

               if (var37 > 248) {
                  var37 = 248;
               }

               if (var57 < 0) {
                  var57 = 0;
               }

               if (var39 > 16) {
                  var39 = 16;
               }

               boolean var58 = false;

               int var42;
               for(int var41 = var55; !var58 && var41 < var35; ++var41) {
                  for(var42 = var57; !var58 && var42 < var39; ++var42) {
                     for(int var43 = var37 + 1; !var58 && var43 >= var56 - 1; --var43) {
                        if (var43 >= 0 && var43 < 256) {
                           IBlockState var44 = var5.func_177856_a(var41, var43, var42);
                           if (var44.func_177230_c() == Blocks.field_150358_i || var44.func_177230_c() == Blocks.field_150355_j) {
                              var58 = true;
                           }

                           if (var43 != var56 - 1 && var41 != var55 && var41 != var35 - 1 && var42 != var57 && var42 != var39 - 1) {
                              var43 = var56;
                           }
                        }
                     }
                  }
               }

               if (!var58) {
                  BlockPos.MutableBlockPos var59 = new BlockPos.MutableBlockPos();

                  for(var42 = var55; var42 < var35; ++var42) {
                     double var60 = ((double)(var42 + var3 * 16) + 0.5D - var6) / var54;

                     for(int var45 = var57; var45 < var39; ++var45) {
                        double var46 = ((double)(var45 + var4 * 16) + 0.5D - var10) / var54;
                        boolean var48 = false;
                        if (var60 * var60 + var46 * var46 < 1.0D) {
                           for(int var49 = var37; var49 > var56; --var49) {
                              double var50 = ((double)(var49 - 1) + 0.5D - var8) / var30;
                              if ((var60 * var60 + var46 * var46) * (double)this.field_75046_d[var49 - 1] + var50 * var50 / 6.0D < 1.0D) {
                                 IBlockState var52 = var5.func_177856_a(var42, var49, var45);
                                 if (var52.func_177230_c() == Blocks.field_150349_c) {
                                    var48 = true;
                                 }

                                 if (var52.func_177230_c() == Blocks.field_150348_b || var52.func_177230_c() == Blocks.field_150346_d || var52.func_177230_c() == Blocks.field_150349_c) {
                                    if (var49 - 1 < 10) {
                                       var5.func_177855_a(var42, var49, var45, Blocks.field_150356_k.func_176223_P());
                                    } else {
                                       var5.func_177855_a(var42, var49, var45, Blocks.field_150350_a.func_176223_P());
                                       if (var48 && var5.func_177856_a(var42, var49 - 1, var45).func_177230_c() == Blocks.field_150346_d) {
                                          var59.func_181079_c(var42 + var3 * 16, 0, var45 + var4 * 16);
                                          var5.func_177855_a(var42, var49 - 1, var45, this.field_75039_c.func_180494_b(var59).field_76752_A);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }

                  if (var53) {
                     break;
                  }
               }
            }
         }
      }

   }

   protected void func_180701_a(World var1, int var2, int var3, int var4, int var5, ChunkPrimer var6) {
      if (this.field_75038_b.nextInt(50) == 0) {
         double var7 = (double)(var2 * 16 + this.field_75038_b.nextInt(16));
         double var9 = (double)(this.field_75038_b.nextInt(this.field_75038_b.nextInt(40) + 8) + 20);
         double var11 = (double)(var3 * 16 + this.field_75038_b.nextInt(16));
         byte var13 = 1;

         for(int var14 = 0; var14 < var13; ++var14) {
            float var15 = this.field_75038_b.nextFloat() * 3.1415927F * 2.0F;
            float var16 = (this.field_75038_b.nextFloat() - 0.5F) * 2.0F / 8.0F;
            float var17 = (this.field_75038_b.nextFloat() * 2.0F + this.field_75038_b.nextFloat()) * 2.0F;
            this.func_180707_a(this.field_75038_b.nextLong(), var4, var5, var6, var7, var9, var11, var17, var15, var16, 0, 0, 3.0D);
         }

      }
   }
}
