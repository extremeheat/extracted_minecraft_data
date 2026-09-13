package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockLeaves extends BlockLeavesBase {
   int[] field_150128_a;
   protected int field_150127_b;
   protected IIcon[][] field_150129_M = new IIcon[2][];

   public BlockLeaves() {
      super(Material.field_151584_j, false);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78031_c);
      this.func_149711_c(0.2F);
      this.func_149713_g(1);
      this.func_149672_a(field_149779_h);
   }

   @Override
   public int func_149635_D() {
      double var1 = 0.5;
      double var3 = 1.0;
      return ColorizerFoliage.func_77470_a(var1, var3);
   }

   @Override
   public int func_149741_i(int var1) {
      return ColorizerFoliage.func_77468_c();
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = 0;
      int var6 = 0;
      int var7 = 0;

      for(int var8 = -1; var8 <= 1; ++var8) {
         for(int var9 = -1; var9 <= 1; ++var9) {
            int var10 = var1.func_72807_a(var2 + var9, var4 + var8).func_150571_c(var2 + var9, var3, var4 + var8);
            var5 += (var10 & 0xFF0000) >> 16;
            var6 += (var10 & 0xFF00) >> 8;
            var7 += var10 & 0xFF;
         }
      }

      return (var5 / 9 & 0xFF) << 16 | (var6 / 9 & 0xFF) << 8 | var7 / 9 & 0xFF;
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      byte var7 = 1;
      int var8 = var7 + 1;
      if (var1.func_72904_c(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8)) {
         for(int var9 = -var7; var9 <= var7; ++var9) {
            for(int var10 = -var7; var10 <= var7; ++var10) {
               for(int var11 = -var7; var11 <= var7; ++var11) {
                  if (var1.func_147439_a(var2 + var9, var3 + var10, var4 + var11).func_149688_o() == Material.field_151584_j) {
                     int var12 = var1.func_72805_g(var2 + var9, var3 + var10, var4 + var11);
                     var1.func_72921_c(var2 + var9, var3 + var10, var4 + var11, var12 | 8, 4);
                  }
               }
            }
         }
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if ((var6 & 8) != 0 && (var6 & 4) == 0) {
            byte var7 = 4;
            int var8 = var7 + 1;
            byte var9 = 32;
            int var10 = var9 * var9;
            int var11 = var9 / 2;
            if (this.field_150128_a == null) {
               this.field_150128_a = new int[var9 * var9 * var9];
            }

            if (var1.func_72904_c(var2 - var8, var3 - var8, var4 - var8, var2 + var8, var3 + var8, var4 + var8)) {
               for(int var12 = -var7; var12 <= var7; ++var12) {
                  for(int var13 = -var7; var13 <= var7; ++var13) {
                     for(int var14 = -var7; var14 <= var7; ++var14) {
                        Block var15 = var1.func_147439_a(var2 + var12, var3 + var13, var4 + var14);
                        if (var15 != Blocks.field_150364_r && var15 != Blocks.field_150363_s) {
                           if (var15.func_149688_o() == Material.field_151584_j) {
                              this.field_150128_a[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -2;
                           } else {
                              this.field_150128_a[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = -1;
                           }
                        } else {
                           this.field_150128_a[(var12 + var11) * var10 + (var13 + var11) * var9 + var14 + var11] = 0;
                        }
                     }
                  }
               }

               for(int var16 = 1; var16 <= 4; ++var16) {
                  for(int var18 = -var7; var18 <= var7; ++var18) {
                     for(int var19 = -var7; var19 <= var7; ++var19) {
                        for(int var20 = -var7; var20 <= var7; ++var20) {
                           if (this.field_150128_a[(var18 + var11) * var10 + (var19 + var11) * var9 + var20 + var11] == var16 - 1) {
                              if (this.field_150128_a[(var18 + var11 - 1) * var10 + (var19 + var11) * var9 + var20 + var11] == -2) {
                                 this.field_150128_a[(var18 + var11 - 1) * var10 + (var19 + var11) * var9 + var20 + var11] = var16;
                              }

                              if (this.field_150128_a[(var18 + var11 + 1) * var10 + (var19 + var11) * var9 + var20 + var11] == -2) {
                                 this.field_150128_a[(var18 + var11 + 1) * var10 + (var19 + var11) * var9 + var20 + var11] = var16;
                              }

                              if (this.field_150128_a[(var18 + var11) * var10 + (var19 + var11 - 1) * var9 + var20 + var11] == -2) {
                                 this.field_150128_a[(var18 + var11) * var10 + (var19 + var11 - 1) * var9 + var20 + var11] = var16;
                              }

                              if (this.field_150128_a[(var18 + var11) * var10 + (var19 + var11 + 1) * var9 + var20 + var11] == -2) {
                                 this.field_150128_a[(var18 + var11) * var10 + (var19 + var11 + 1) * var9 + var20 + var11] = var16;
                              }

                              if (this.field_150128_a[(var18 + var11) * var10 + (var19 + var11) * var9 + (var20 + var11 - 1)] == -2) {
                                 this.field_150128_a[(var18 + var11) * var10 + (var19 + var11) * var9 + (var20 + var11 - 1)] = var16;
                              }

                              if (this.field_150128_a[(var18 + var11) * var10 + (var19 + var11) * var9 + var20 + var11 + 1] == -2) {
                                 this.field_150128_a[(var18 + var11) * var10 + (var19 + var11) * var9 + var20 + var11 + 1] = var16;
                              }
                           }
                        }
                     }
                  }
               }
            }

            int var17 = this.field_150128_a[var11 * var10 + var11 * var9 + var11];
            if (var17 >= 0) {
               var1.func_72921_c(var2, var3, var4, var6 & -9, 4);
            } else {
               this.func_150126_e(var1, var2, var3, var4);
            }
         }
      }
   }

   @Override
   public void func_149734_b(World var1, int var2, int var3, int var4, Random var5) {
      if (var1.func_72951_B(var2, var3 + 1, var4) && !World.func_147466_a(var1, var2, var3 - 1, var4) && var5.nextInt(15) == 1) {
         double var6 = (double)((float)var2 + var5.nextFloat());
         double var8 = (double)var3 - 0.05;
         double var10 = (double)((float)var4 + var5.nextFloat());
         var1.func_72869_a("dripWater", var6, var8, var10, 0.0, 0.0, 0.0);
      }
   }

   private void func_150126_e(World var1, int var2, int var3, int var4) {
      this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
      var1.func_147468_f(var2, var3, var4);
   }

   @Override
   public int func_149745_a(Random var1) {
      return var1.nextInt(20) == 0 ? 1 : 0;
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Item.func_150898_a(Blocks.field_150345_g);
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      if (!var1.field_72995_K) {
         int var8 = this.func_150123_b(var5);
         if (var7 > 0) {
            var8 -= 2 << var7;
            if (var8 < 10) {
               var8 = 10;
            }
         }

         if (var1.field_73012_v.nextInt(var8) == 0) {
            Item var9 = this.func_149650_a(var5, var1.field_73012_v, var7);
            this.func_149642_a(var1, var2, var3, var4, new ItemStack(var9, 1, this.func_149692_a(var5)));
         }

         var8 = 200;
         if (var7 > 0) {
            var8 -= 10 << var7;
            if (var8 < 40) {
               var8 = 40;
            }
         }

         this.func_150124_c(var1, var2, var3, var4, var5, var8);
      }
   }

   protected void func_150124_c(World var1, int var2, int var3, int var4, int var5, int var6) {
   }

   protected int func_150123_b(int var1) {
      return 20;
   }

   @Override
   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      if (!var1.field_72995_K && var2.func_71045_bC() != null && var2.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
         var2.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
         this.func_149642_a(var1, var3, var4, var5, new ItemStack(Item.func_150898_a(this), 1, var6 & 3));
      } else {
         super.func_149636_a(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public int func_149692_a(int var1) {
      return var1 & 3;
   }

   @Override
   public boolean func_149662_c() {
      return !this.field_150121_P;
   }

   @Override
   public abstract IIcon func_149691_a(int var1, int var2);

   public void func_150122_b(boolean var1) {
      this.field_150121_P = var1;
      this.field_150127_b = var1 ? 0 : 1;
   }

   @Override
   protected ItemStack func_149644_j(int var1) {
      return new ItemStack(Item.func_150898_a(this), 1, var1 & 3);
   }

   public abstract String[] func_150125_e();
}
