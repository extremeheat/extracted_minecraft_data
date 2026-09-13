package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStem extends BlockBush implements IGrowable {
   private final Block field_149877_a;
   private IIcon field_149876_b;

   protected BlockStem(Block var1) {
      super();
      this.field_149877_a = var1;
      this.func_149675_a(true);
      float var2 = 0.125F;
      this.func_149676_a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, 0.25F, 0.5F + var2);
      this.func_149647_a(null);
   }

   @Override
   protected boolean func_149854_a(Block var1) {
      return var1 == Blocks.field_150458_ak;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      super.func_149674_a(var1, var2, var3, var4, var5);
      if (var1.func_72957_l(var2, var3 + 1, var4) >= 9) {
         float var6 = this.func_149875_n(var1, var2, var3, var4);
         if (var5.nextInt((int)(25.0F / var6) + 1) == 0) {
            int var7 = var1.func_72805_g(var2, var3, var4);
            if (var7 < 7) {
               var1.func_72921_c(var2, var3, var4, ++var7, 2);
            } else {
               if (var1.func_147439_a(var2 - 1, var3, var4) == this.field_149877_a) {
                  return;
               }

               if (var1.func_147439_a(var2 + 1, var3, var4) == this.field_149877_a) {
                  return;
               }

               if (var1.func_147439_a(var2, var3, var4 - 1) == this.field_149877_a) {
                  return;
               }

               if (var1.func_147439_a(var2, var3, var4 + 1) == this.field_149877_a) {
                  return;
               }

               int var8 = var5.nextInt(4);
               int var9 = var2;
               int var10 = var4;
               if (var8 == 0) {
                  var9 = var2 - 1;
               }

               if (var8 == 1) {
                  ++var9;
               }

               if (var8 == 2) {
                  var10 = var4 - 1;
               }

               if (var8 == 3) {
                  ++var10;
               }

               Block var11 = var1.func_147439_a(var9, var3 - 1, var10);
               if (var1.func_147439_a(var9, var3, var10).field_149764_J == Material.field_151579_a
                  && (var11 == Blocks.field_150458_ak || var11 == Blocks.field_150346_d || var11 == Blocks.field_150349_c)) {
                  var1.func_147449_b(var9, var3, var10, this.field_149877_a);
               }
            }
         }
      }
   }

   public void func_149874_m(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4) + MathHelper.func_76136_a(var1.field_73012_v, 2, 5);
      if (var5 > 7) {
         var5 = 7;
      }

      var1.func_72921_c(var2, var3, var4, var5, 2);
   }

   private float func_149875_n(World var1, int var2, int var3, int var4) {
      float var5 = 1.0F;
      Block var6 = var1.func_147439_a(var2, var3, var4 - 1);
      Block var7 = var1.func_147439_a(var2, var3, var4 + 1);
      Block var8 = var1.func_147439_a(var2 - 1, var3, var4);
      Block var9 = var1.func_147439_a(var2 + 1, var3, var4);
      Block var10 = var1.func_147439_a(var2 - 1, var3, var4 - 1);
      Block var11 = var1.func_147439_a(var2 + 1, var3, var4 - 1);
      Block var12 = var1.func_147439_a(var2 + 1, var3, var4 + 1);
      Block var13 = var1.func_147439_a(var2 - 1, var3, var4 + 1);
      boolean var14 = var8 == this || var9 == this;
      boolean var15 = var6 == this || var7 == this;
      boolean var16 = var10 == this || var11 == this || var12 == this || var13 == this;

      for(int var17 = var2 - 1; var17 <= var2 + 1; ++var17) {
         for(int var18 = var4 - 1; var18 <= var4 + 1; ++var18) {
            Block var19 = var1.func_147439_a(var17, var3 - 1, var18);
            float var20 = 0.0F;
            if (var19 == Blocks.field_150458_ak) {
               var20 = 1.0F;
               if (var1.func_72805_g(var17, var3 - 1, var18) > 0) {
                  var20 = 3.0F;
               }
            }

            if (var17 != var2 || var18 != var4) {
               var20 /= 4.0F;
            }

            var5 += var20;
         }
      }

      if (var16 || var14 && var15) {
         var5 /= 2.0F;
      }

      return var5;
   }

   @Override
   public int func_149741_i(int var1) {
      int var2 = var1 * 32;
      int var3 = 255 - var1 * 8;
      int var4 = var1 * 4;
      return var2 << 16 | var3 << 8 | var4;
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      return this.func_149741_i(var1.func_72805_g(var2, var3, var4));
   }

   @Override
   public void func_149683_g() {
      float var1 = 0.125F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, 0.25F, 0.5F + var1);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.field_149756_F = (double)((float)(var1.func_72805_g(var2, var3, var4) * 2 + 2) / 16.0F);
      float var5 = 0.125F;
      this.func_149676_a(0.5F - var5, 0.0F, 0.5F - var5, 0.5F + var5, (float)this.field_149756_F, 0.5F + var5);
   }

   @Override
   public int func_149645_b() {
      return 19;
   }

   public int func_149873_e(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if (var5 < 7) {
         return -1;
      } else if (var1.func_147439_a(var2 - 1, var3, var4) == this.field_149877_a) {
         return 0;
      } else if (var1.func_147439_a(var2 + 1, var3, var4) == this.field_149877_a) {
         return 1;
      } else if (var1.func_147439_a(var2, var3, var4 - 1) == this.field_149877_a) {
         return 2;
      } else {
         return var1.func_147439_a(var2, var3, var4 + 1) == this.field_149877_a ? 3 : -1;
      }
   }

   @Override
   public void func_149690_a(World var1, int var2, int var3, int var4, int var5, float var6, int var7) {
      super.func_149690_a(var1, var2, var3, var4, var5, var6, var7);
      if (!var1.field_72995_K) {
         Item var8 = null;
         if (this.field_149877_a == Blocks.field_150423_aK) {
            var8 = Items.field_151080_bb;
         }

         if (this.field_149877_a == Blocks.field_150440_ba) {
            var8 = Items.field_151081_bc;
         }

         for(int var9 = 0; var9 < 3; ++var9) {
            if (var1.field_73012_v.nextInt(15) <= var5) {
               this.func_149642_a(var1, var2, var3, var4, new ItemStack(var8));
            }
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return null;
   }

   @Override
   public int func_149745_a(Random var1) {
      return 1;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      if (this.field_149877_a == Blocks.field_150423_aK) {
         return Items.field_151080_bb;
      } else {
         return this.field_149877_a == Blocks.field_150440_ba ? Items.field_151081_bc : Item.func_150899_d(0);
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_disconnected");
      this.field_149876_b = var1.func_94245_a(this.func_149641_N() + "_connected");
   }

   public IIcon func_149872_i() {
      return this.field_149876_b;
   }

   @Override
   public boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5) {
      return var1.func_72805_g(var2, var3, var4) != 7;
   }

   @Override
   public boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149853_b(World var1, Random var2, int var3, int var4, int var5) {
      this.func_149874_m(var1, var3, var4, var5);
   }
}
