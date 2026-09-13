package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockButton extends Block {
   private final boolean field_150047_a;

   protected BlockButton(boolean var1) {
      super(Material.field_151594_q);
      this.func_149675_a(true);
      this.func_149647_a(CreativeTabs.field_78028_d);
      this.field_150047_a = var1;
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      return null;
   }

   @Override
   public int func_149738_a(World var1) {
      return this.field_150047_a ? 30 : 20;
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
      int var10 = var1.func_72805_g(var2, var3, var4);
      int var11 = var10 & 8;
      var10 &= 7;
      if (var5 == 2 && var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
         var10 = 4;
      } else if (var5 == 3 && var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         var10 = 3;
      } else if (var5 == 4 && var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         var10 = 2;
      } else if (var5 == 5 && var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
         var10 = 1;
      } else {
         var10 = this.func_150045_e(var1, var2, var3, var4);
      }

      return var10 + var11;
   }

   private int func_150045_e(World var1, int var2, int var3, int var4) {
      if (var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
         return 1;
      } else if (var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         return 2;
      } else if (var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         return 3;
      } else {
         return var1.func_147439_a(var2, var3, var4 + 1).func_149721_r() ? 4 : 1;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (this.func_150044_m(var1, var2, var3, var4)) {
         int var6 = var1.func_72805_g(var2, var3, var4) & 7;
         boolean var7 = false;
         if (!var1.func_147439_a(var2 - 1, var3, var4).func_149721_r() && var6 == 1) {
            var7 = true;
         }

         if (!var1.func_147439_a(var2 + 1, var3, var4).func_149721_r() && var6 == 2) {
            var7 = true;
         }

         if (!var1.func_147439_a(var2, var3, var4 - 1).func_149721_r() && var6 == 3) {
            var7 = true;
         }

         if (!var1.func_147439_a(var2, var3, var4 + 1).func_149721_r() && var6 == 4) {
            var7 = true;
         }

         if (var7) {
            this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
            var1.func_147468_f(var2, var3, var4);
         }
      }
   }

   private boolean func_150044_m(World var1, int var2, int var3, int var4) {
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
      int var5 = var1.func_72805_g(var2, var3, var4);
      this.func_150043_b(var5);
   }

   private void func_150043_b(int var1) {
      int var2 = var1 & 7;
      boolean var3 = (var1 & 8) > 0;
      float var4 = 0.375F;
      float var5 = 0.625F;
      float var6 = 0.1875F;
      float var7 = 0.125F;
      if (var3) {
         var7 = 0.0625F;
      }

      if (var2 == 1) {
         this.func_149676_a(0.0F, var4, 0.5F - var6, var7, var5, 0.5F + var6);
      } else if (var2 == 2) {
         this.func_149676_a(1.0F - var7, var4, 0.5F - var6, 1.0F, var5, 0.5F + var6);
      } else if (var2 == 3) {
         this.func_149676_a(0.5F - var6, var4, 0.0F, 0.5F + var6, var5, var7);
      } else if (var2 == 4) {
         this.func_149676_a(0.5F - var6, var4, 1.0F - var7, 0.5F + var6, var5, 1.0F);
      }
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      int var10 = var1.func_72805_g(var2, var3, var4);
      int var11 = var10 & 7;
      int var12 = 8 - (var10 & 8);
      if (var12 == 0) {
         return true;
      } else {
         var1.func_72921_c(var2, var3, var4, var11 + var12, 3);
         var1.func_147458_c(var2, var3, var4, var2, var3, var4);
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "random.click", 0.3F, 0.6F);
         this.func_150042_a(var1, var2, var3, var4, var11);
         var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
         return true;
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      if ((var6 & 8) > 0) {
         int var7 = var6 & 7;
         this.func_150042_a(var1, var2, var3, var4, var7);
      }

      super.func_149749_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public int func_149709_b(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      return (var1.func_72805_g(var2, var3, var4) & 8) > 0 ? 15 : 0;
   }

   @Override
   public int func_149748_c(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if ((var6 & 8) == 0) {
         return 0;
      } else {
         int var7 = var6 & 7;
         if (var7 == 5 && var5 == 1) {
            return 15;
         } else if (var7 == 4 && var5 == 2) {
            return 15;
         } else if (var7 == 3 && var5 == 3) {
            return 15;
         } else if (var7 == 2 && var5 == 4) {
            return 15;
         } else {
            return var7 == 1 && var5 == 5 ? 15 : 0;
         }
      }
   }

   @Override
   public boolean func_149744_f() {
      return true;
   }

   @Override
   public void func_149674_a(World var1, int var2, int var3, int var4, Random var5) {
      if (!var1.field_72995_K) {
         int var6 = var1.func_72805_g(var2, var3, var4);
         if ((var6 & 8) != 0) {
            if (this.field_150047_a) {
               this.func_150046_n(var1, var2, var3, var4);
            } else {
               var1.func_72921_c(var2, var3, var4, var6 & 7, 3);
               int var7 = var6 & 7;
               this.func_150042_a(var1, var2, var3, var4, var7);
               var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "random.click", 0.3F, 0.5F);
               var1.func_147458_c(var2, var3, var4, var2, var3, var4);
            }
         }
      }
   }

   @Override
   public void func_149683_g() {
      float var1 = 0.1875F;
      float var2 = 0.125F;
      float var3 = 0.125F;
      this.func_149676_a(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      if (!var1.field_72995_K) {
         if (this.field_150047_a) {
            if ((var1.func_72805_g(var2, var3, var4) & 8) == 0) {
               this.func_150046_n(var1, var2, var3, var4);
            }
         }
      }
   }

   private void func_150046_n(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      int var6 = var5 & 7;
      boolean var7 = (var5 & 8) != 0;
      this.func_150043_b(var5);
      List var9 = var1.func_72872_a(
         EntityArrow.class,
         AxisAlignedBB.func_72330_a(
            (double)var2 + this.field_149759_B,
            (double)var3 + this.field_149760_C,
            (double)var4 + this.field_149754_D,
            (double)var2 + this.field_149755_E,
            (double)var3 + this.field_149756_F,
            (double)var4 + this.field_149757_G
         )
      );
      boolean var8 = !var9.isEmpty();
      if (var8 && !var7) {
         var1.func_72921_c(var2, var3, var4, var6 | 8, 3);
         this.func_150042_a(var1, var2, var3, var4, var6);
         var1.func_147458_c(var2, var3, var4, var2, var3, var4);
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "random.click", 0.3F, 0.6F);
      }

      if (!var8 && var7) {
         var1.func_72921_c(var2, var3, var4, var6, 3);
         this.func_150042_a(var1, var2, var3, var4, var6);
         var1.func_147458_c(var2, var3, var4, var2, var3, var4);
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "random.click", 0.3F, 0.5F);
      }

      if (var8) {
         var1.func_147464_a(var2, var3, var4, this, this.func_149738_a(var1));
      }
   }

   private void func_150042_a(World var1, int var2, int var3, int var4, int var5) {
      var1.func_147459_d(var2, var3, var4, this);
      if (var5 == 1) {
         var1.func_147459_d(var2 - 1, var3, var4, this);
      } else if (var5 == 2) {
         var1.func_147459_d(var2 + 1, var3, var4, this);
      } else if (var5 == 3) {
         var1.func_147459_d(var2, var3, var4 - 1, this);
      } else if (var5 == 4) {
         var1.func_147459_d(var2, var3, var4 + 1, this);
      } else {
         var1.func_147459_d(var2, var3 - 1, var4, this);
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
   }
}
