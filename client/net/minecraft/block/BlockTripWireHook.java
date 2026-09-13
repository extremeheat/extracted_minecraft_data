package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public BlockTripWireHook() {
      super(Material.field_151594_q);
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.func_149675_a(true);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 29;
   }

   @Override
   public int func_149738_a(World var1) {
      return 10;
   }

   @Override
   public boolean func_149707_d(World var1, int var2, int var3, int var4, int var5) {
      if (var5 == 2 && var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
         return true;
      } else if (var5 == 3 && var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         return true;
      } else if (var5 == 4 && var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         return true;
      } else {
         return var5 == 5 && var1.func_147439_a(var2 - 1, var3, var4).func_149721_r();
      }
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
         return true;
      } else if (var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         return true;
      } else if (var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         return true;
      } else {
         return var1.func_147439_a(var2, var3, var4 + 1).func_149721_r();
      }
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      byte var10 = 0;
      if (var5 == 2 && var1.func_147445_c(var2, var3, var4 + 1, true)) {
         var10 = 2;
      }

      if (var5 == 3 && var1.func_147445_c(var2, var3, var4 - 1, true)) {
         var10 = 0;
      }

      if (var5 == 4 && var1.func_147445_c(var2 + 1, var3, var4, true)) {
         var10 = 1;
      }

      if (var5 == 5 && var1.func_147445_c(var2 - 1, var3, var4, true)) {
         var10 = 3;
      }

      return var10;
   }

   @Override
   public void func_149714_e(World var1, int var2, int var3, int var4, int var5) {
      this.func_150136_a(var1, var2, var3, var4, false, var5, false, -1, 0);
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (var5 != this) {
         if (this.func_150137_e(var1, var2, var3, var4)) {
            int var6 = var1.func_72805_g(var2, var3, var4);
            int var7 = var6 & 3;
            boolean var8 = false;
            if (!var1.func_147439_a(var2 - 1, var3, var4).func_149721_r() && var7 == 3) {
               var8 = true;
            }

            if (!var1.func_147439_a(var2 + 1, var3, var4).func_149721_r() && var7 == 1) {
               var8 = true;
            }

            if (!var1.func_147439_a(var2, var3, var4 - 1).func_149721_r() && var7 == 0) {
               var8 = true;
            }

            if (!var1.func_147439_a(var2, var3, var4 + 1).func_149721_r() && var7 == 2) {
               var8 = true;
            }

            if (var8) {
               this.func_149697_b(var1, var2, var3, var4, var6, 0);
               var1.func_147468_f(var2, var3, var4);
            }
         }
      }
   }

   public void func_150136_a(World var1, int var2, int var3, int var4, boolean var5, int var6, boolean var7, int var8, int var9) {
      int var10 = var6 & 3;
      boolean var11 = (var6 & 4) == 4;
      boolean var12 = (var6 & 8) == 8;
      boolean var13 = !var5;
      boolean var14 = false;
      boolean var15 = !World.func_147466_a(var1, var2, var3 - 1, var4);
      int var16 = Direction.field_71583_a[var10];
      int var17 = Direction.field_71581_b[var10];
      int var18 = 0;
      int[] var19 = new int[42];

      for(int var20 = 1; var20 < 42; ++var20) {
         int var21 = var2 + var16 * var20;
         int var22 = var4 + var17 * var20;
         Block var23 = var1.func_147439_a(var21, var3, var22);
         if (var23 == Blocks.field_150479_bC) {
            int var38 = var1.func_72805_g(var21, var3, var22);
            if ((var38 & 3) == Direction.field_71580_e[var10]) {
               var18 = var20;
            }
            break;
         }

         if (var23 != Blocks.field_150473_bD && var20 != var8) {
            var19[var20] = -1;
            var13 = false;
         } else {
            int var24 = var20 == var8 ? var9 : var1.func_72805_g(var21, var3, var22);
            boolean var25 = (var24 & 8) != 8;
            boolean var26 = (var24 & 1) == 1;
            boolean var27 = (var24 & 2) == 2;
            var13 &= var27 == var15;
            var14 |= var25 && var26;
            var19[var20] = var24;
            if (var20 == var8) {
               var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
               var13 &= var25;
            }
         }
      }

      var13 &= var18 > 1;
      var14 &= var13;
      int var31 = (var13 ? 4 : 0) | (var14 ? 8 : 0);
      var6 = var10 | var31;
      if (var18 > 0) {
         int var32 = var2 + var16 * var18;
         int var34 = var4 + var17 * var18;
         int var36 = Direction.field_71580_e[var10];
         var1.func_72921_c(var32, var3, var34, var36 | var31, 3);
         this.func_150134_a(var1, var32, var3, var34, var36);
         this.func_150135_a(var1, var32, var3, var34, var13, var14, var11, var12);
      }

      this.func_150135_a(var1, var2, var3, var4, var13, var14, var11, var12);
      if (!var5) {
         var1.func_72921_c(var2, var3, var4, var6, 3);
         if (var7) {
            this.func_150134_a(var1, var2, var3, var4, var10);
         }
      }

      if (var11 != var13) {
         for(int var33 = 1; var33 < var18; ++var33) {
            int var35 = var2 + var16 * var33;
            int var37 = var4 + var17 * var33;
            int var39 = var19[var33];
            if (var39 >= 0) {
               if (var13) {
                  var39 |= 4;
               } else {
                  var39 &= -5;
               }

               var1.func_72921_c(var35, var3, var37, var39, 3);
            }
         }
      }
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      this.func_150136_a(var1, var2, var3, var4, false, var1.func_72805_g(var2, var3, var4), true, -1, 0);
   }

   private void func_150135_a(World var1, int var2, int var3, int var4, boolean var5, boolean var6, boolean var7, boolean var8) {
      if (var6 && !var8) {
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.1, (double)var4 + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!var6 && var8) {
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.1, (double)var4 + 0.5, "random.click", 0.4F, 0.5F);
      } else if (var5 && !var7) {
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.1, (double)var4 + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!var5 && var7) {
         var1.func_72908_a(
            (double)var2 + 0.5, (double)var3 + 0.1, (double)var4 + 0.5, "random.bowhit", 0.4F, 1.2F / (var1.field_73012_v.nextFloat() * 0.2F + 0.9F)
         );
      }
   }

   private void func_150134_a(World var1, int var2, int var3, int var4, int var5) {
      var1.func_147459_d(var2, var3, var4, this);
      if (var5 == 3) {
         var1.func_147459_d(var2 - 1, var3, var4, this);
      } else if (var5 == 1) {
         var1.func_147459_d(var2 + 1, var3, var4, this);
      } else if (var5 == 0) {
         var1.func_147459_d(var2, var3, var4 - 1, this);
      } else if (var5 == 2) {
         var1.func_147459_d(var2, var3, var4 + 1, this);
      }
   }

   private boolean func_150137_e(World var1, int var2, int var3, int var4) {
      if (!this.func_149742_c(var1, var2, var3, var4)) {
         this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
         var1.func_147468_f(var2, var3, var4);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4) & 3;
      float var6 = 0.1875F;
      if (var5 == 3) {
         this.func_149676_a(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
      } else if (var5 == 1) {
         this.func_149676_a(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
      } else if (var5 == 0) {
         this.func_149676_a(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
      } else if (var5 == 2) {
         this.func_149676_a(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      boolean var7 = (var6 & 4) == 4;
      boolean var8 = (var6 & 8) == 8;
      if (var7 || var8) {
         this.func_150136_a(var1, var2, var3, var4, true, var6, false, -1, 0);
      }

      if (var8) {
         var1.func_147459_d(var2, var3, var4, this);
         int var9 = var6 & 3;
         if (var9 == 3) {
            var1.func_147459_d(var2 - 1, var3, var4, this);
         } else if (var9 == 1) {
            var1.func_147459_d(var2 + 1, var3, var4, this);
         } else if (var9 == 0) {
            var1.func_147459_d(var2, var3, var4 - 1, this);
         } else if (var9 == 2) {
            var1.func_147459_d(var2, var3, var4 + 1, this);
         }
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return (var1.func_72805_g(var2, var3, var4) & 8) == 8 ? 15 : 0;
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if ((var6 & 8) != 8) {
         return 0;
      } else {
         int var7 = var6 & 3;
         if (var7 == 2 && var5 == 2) {
            return 15;
         } else if (var7 == 0 && var5 == 3) {
            return 15;
         } else if (var7 == 1 && var5 == 4) {
            return 15;
         } else {
            return var7 == 3 && var5 == 5 ? 15 : 0;
         }
      }
   }

   @Override
   public boolean func_149744_f() {
      return true;
   }
}
