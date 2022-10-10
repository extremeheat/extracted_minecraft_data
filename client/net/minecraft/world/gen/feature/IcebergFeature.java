package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class IcebergFeature extends Feature<IcebergConfig> {
   public IcebergFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, IcebergConfig var5) {
      var4 = new BlockPos(var4.func_177958_n(), var1.func_181545_F(), var4.func_177952_p());
      boolean var6 = var3.nextDouble() > 0.7D;
      IBlockState var7 = var5.field_205191_a;
      double var8 = var3.nextDouble() * 2.0D * 3.141592653589793D;
      int var10 = 11 - var3.nextInt(5);
      int var11 = 3 + var3.nextInt(3);
      boolean var12 = var3.nextDouble() > 0.7D;
      boolean var13 = true;
      int var14 = var12 ? var3.nextInt(6) + 6 : var3.nextInt(15) + 3;
      if (!var12 && var3.nextDouble() > 0.9D) {
         var14 += var3.nextInt(19) + 7;
      }

      int var15 = Math.min(var14 + var3.nextInt(11), 18);
      int var16 = Math.min(var14 + var3.nextInt(7) - var3.nextInt(5), 11);
      int var17 = var12 ? var10 : 11;

      int var18;
      int var19;
      int var20;
      int var21;
      for(var18 = -var17; var18 < var17; ++var18) {
         for(var19 = -var17; var19 < var17; ++var19) {
            for(var20 = 0; var20 < var14; ++var20) {
               var21 = var12 ? this.func_205178_b(var20, var14, var16) : this.func_205183_a(var3, var20, var14, var16);
               if (var12 || var18 < var21) {
                  this.func_205181_a(var1, var3, var4, var14, var18, var20, var19, var21, var17, var12, var11, var8, var6, var7);
               }
            }
         }
      }

      this.func_205186_a(var1, var4, var16, var14, var12, var10);

      for(var18 = -var17; var18 < var17; ++var18) {
         for(var19 = -var17; var19 < var17; ++var19) {
            for(var20 = -1; var20 > -var15; --var20) {
               var21 = var12 ? MathHelper.func_76123_f((float)var17 * (1.0F - (float)Math.pow((double)var20, 2.0D) / ((float)var15 * 8.0F))) : var17;
               int var22 = this.func_205187_b(var3, -var20, var15, var16);
               if (var18 < var22) {
                  this.func_205181_a(var1, var3, var4, var15, var18, var20, var19, var22, var21, var12, var11, var8, var6, var7);
               }
            }
         }
      }

      boolean var23 = var12 ? var3.nextDouble() > 0.1D : var3.nextDouble() > 0.7D;
      if (var23) {
         this.func_205184_a(var3, var1, var16, var14, var4, var12, var10, var8, var11);
      }

      return true;
   }

   private void func_205184_a(Random var1, IWorld var2, int var3, int var4, BlockPos var5, boolean var6, int var7, double var8, int var10) {
      int var11 = var1.nextBoolean() ? -1 : 1;
      int var12 = var1.nextBoolean() ? -1 : 1;
      int var13 = var1.nextInt(Math.max(var3 / 2 - 2, 1));
      if (var1.nextBoolean()) {
         var13 = var3 / 2 + 1 - var1.nextInt(Math.max(var3 - var3 / 2 - 1, 1));
      }

      int var14 = var1.nextInt(Math.max(var3 / 2 - 2, 1));
      if (var1.nextBoolean()) {
         var14 = var3 / 2 + 1 - var1.nextInt(Math.max(var3 - var3 / 2 - 1, 1));
      }

      if (var6) {
         var13 = var14 = var1.nextInt(Math.max(var7 - 5, 1));
      }

      BlockPos var15 = (new BlockPos(0, 0, 0)).func_177982_a(var11 * var13, 0, var12 * var14);
      double var16 = var6 ? var8 + 1.5707963267948966D : var1.nextDouble() * 2.0D * 3.141592653589793D;

      int var18;
      int var19;
      for(var18 = 0; var18 < var4 - 3; ++var18) {
         var19 = this.func_205183_a(var1, var18, var4, var3);
         this.func_205174_a(var19, var18, var5, var2, false, var16, var15, var7, var10);
      }

      for(var18 = -1; var18 > -var4 + var1.nextInt(5); --var18) {
         var19 = this.func_205187_b(var1, -var18, var4, var3);
         this.func_205174_a(var19, var18, var5, var2, true, var16, var15, var7, var10);
      }

   }

   private void func_205174_a(int var1, int var2, BlockPos var3, IWorld var4, boolean var5, double var6, BlockPos var8, int var9, int var10) {
      int var11 = var1 + 1 + var9 / 3;
      int var12 = Math.min(var1 - 3, 3) + var10 / 2 - 1;

      for(int var13 = -var11; var13 < var11; ++var13) {
         for(int var14 = -var11; var14 < var11; ++var14) {
            double var15 = this.func_205180_a(var13, var14, var8, var11, var12, var6);
            if (var15 < 0.0D) {
               BlockPos var17 = var3.func_177982_a(var13, var2, var14);
               Block var18 = var4.func_180495_p(var17).func_177230_c();
               if (this.func_205179_a(var18) || var18 == Blocks.field_196604_cC) {
                  if (var5) {
                     this.func_202278_a(var4, var17, Blocks.field_150355_j.func_176223_P());
                  } else {
                     this.func_202278_a(var4, var17, Blocks.field_150350_a.func_176223_P());
                     this.func_205185_a(var4, var17);
                  }
               }
            }
         }
      }

   }

   private void func_205185_a(IWorld var1, BlockPos var2) {
      if (var1.func_180495_p(var2.func_177984_a()).func_177230_c() == Blocks.field_150433_aE) {
         this.func_202278_a(var1, var2.func_177984_a(), Blocks.field_150350_a.func_176223_P());
      }

   }

   private void func_205181_a(IWorld var1, Random var2, BlockPos var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10, int var11, double var12, boolean var14, IBlockState var15) {
      BlockPos var16 = new BlockPos(0, 0, 0);
      double var17 = var10 ? this.func_205180_a(var5, var7, var16, var9, this.func_205176_a(var6, var4, var11), var12) : this.func_205177_a(var5, var7, var16, var8, var2);
      if (var17 < 0.0D) {
         BlockPos var19 = var3.func_177982_a(var5, var6, var7);
         double var20 = var10 ? -0.5D : (double)(-6 - var2.nextInt(3));
         if (var17 > var20 && var2.nextDouble() > 0.9D) {
            return;
         }

         this.func_205175_a(var19, var1, var2, var4 - var6, var4, var10, var14, var15);
      }

   }

   private void func_205175_a(BlockPos var1, IWorld var2, Random var3, int var4, int var5, boolean var6, boolean var7, IBlockState var8) {
      IBlockState var9 = var2.func_180495_p(var1);
      Block var10 = var9.func_177230_c();
      if (var9.func_185904_a() == Material.field_151579_a || var10 == Blocks.field_196604_cC || var10 == Blocks.field_150432_aD || var10 == Blocks.field_150355_j) {
         boolean var11 = !var6 || var3.nextDouble() > 0.05D;
         int var12 = var6 ? 3 : 2;
         if (var7 && var10 != Blocks.field_150355_j && (double)var4 <= (double)var3.nextInt(Math.max(1, var5 / var12)) + (double)var5 * 0.6D && var11) {
            this.func_202278_a(var2, var1, Blocks.field_196604_cC.func_176223_P());
         } else {
            this.func_202278_a(var2, var1, var8);
         }
      }

   }

   private int func_205176_a(int var1, int var2, int var3) {
      int var4 = var3;
      if (var1 > 0 && var2 - var1 <= 3) {
         var4 = var3 - (4 - (var2 - var1));
      }

      return var4;
   }

   private double func_205177_a(int var1, int var2, BlockPos var3, int var4, Random var5) {
      float var6 = 10.0F * MathHelper.func_76131_a(var5.nextFloat(), 0.2F, 0.8F) / (float)var4;
      return (double)var6 + Math.pow((double)(var1 - var3.func_177958_n()), 2.0D) + Math.pow((double)(var2 - var3.func_177952_p()), 2.0D) - Math.pow((double)var4, 2.0D);
   }

   private double func_205180_a(int var1, int var2, BlockPos var3, int var4, int var5, double var6) {
      return Math.pow(((double)(var1 - var3.func_177958_n()) * Math.cos(var6) - (double)(var2 - var3.func_177952_p()) * Math.sin(var6)) / (double)var4, 2.0D) + Math.pow(((double)(var1 - var3.func_177958_n()) * Math.sin(var6) + (double)(var2 - var3.func_177952_p()) * Math.cos(var6)) / (double)var5, 2.0D) - 1.0D;
   }

   private int func_205183_a(Random var1, int var2, int var3, int var4) {
      float var5 = 3.5F - var1.nextFloat();
      float var6 = (1.0F - (float)Math.pow((double)var2, 2.0D) / ((float)var3 * var5)) * (float)var4;
      if (var3 > 15 + var1.nextInt(5)) {
         int var7 = var2 < 3 + var1.nextInt(6) ? var2 / 2 : var2;
         var6 = (1.0F - (float)var7 / ((float)var3 * var5 * 0.4F)) * (float)var4;
      }

      return MathHelper.func_76123_f(var6 / 2.0F);
   }

   private int func_205178_b(int var1, int var2, int var3) {
      float var4 = 1.0F;
      float var5 = (1.0F - (float)Math.pow((double)var1, 2.0D) / ((float)var2 * 1.0F)) * (float)var3;
      return MathHelper.func_76123_f(var5 / 2.0F);
   }

   private int func_205187_b(Random var1, int var2, int var3, int var4) {
      float var5 = 1.0F + var1.nextFloat() / 2.0F;
      float var6 = (1.0F - (float)var2 / ((float)var3 * var5)) * (float)var4;
      return MathHelper.func_76123_f(var6 / 2.0F);
   }

   private boolean func_205179_a(Block var1) {
      return var1 == Blocks.field_150403_cj || var1 == Blocks.field_196604_cC || var1 == Blocks.field_205164_gk;
   }

   private boolean func_205182_b(IBlockReader var1, BlockPos var2) {
      return var1.func_180495_p(var2.func_177977_b()).func_185904_a() == Material.field_151579_a;
   }

   private void func_205186_a(IWorld var1, BlockPos var2, int var3, int var4, boolean var5, int var6) {
      int var7 = var5 ? var6 : var3 / 2;

      for(int var8 = -var7; var8 <= var7; ++var8) {
         for(int var9 = -var7; var9 <= var7; ++var9) {
            for(int var10 = 0; var10 <= var4; ++var10) {
               BlockPos var11 = var2.func_177982_a(var8, var10, var9);
               Block var12 = var1.func_180495_p(var11).func_177230_c();
               if (this.func_205179_a(var12) || var12 == Blocks.field_150433_aE) {
                  if (this.func_205182_b(var1, var11)) {
                     this.func_202278_a(var1, var11, Blocks.field_150350_a.func_176223_P());
                     this.func_202278_a(var1, var11.func_177984_a(), Blocks.field_150350_a.func_176223_P());
                  } else if (this.func_205179_a(var12)) {
                     Block[] var13 = new Block[]{var1.func_180495_p(var11.func_177976_e()).func_177230_c(), var1.func_180495_p(var11.func_177974_f()).func_177230_c(), var1.func_180495_p(var11.func_177978_c()).func_177230_c(), var1.func_180495_p(var11.func_177968_d()).func_177230_c()};
                     int var14 = 0;
                     Block[] var15 = var13;
                     int var16 = var13.length;

                     for(int var17 = 0; var17 < var16; ++var17) {
                        Block var18 = var15[var17];
                        if (!this.func_205179_a(var18)) {
                           ++var14;
                        }
                     }

                     if (var14 >= 3) {
                        this.func_202278_a(var1, var11, Blocks.field_150350_a.func_176223_P());
                     }
                  }
               }
            }
         }
      }

   }
}
