package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLever extends Block {
   protected BlockLever() {
      super(Material.field_151594_q);
      this.func_149647_a(CreativeTabs.field_78028_d);
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
      return 12;
   }

   @Override
   public boolean func_149707_d(World var1, int var2, int var3, int var4, int var5) {
      if (var5 == 0 && var1.func_147439_a(var2, var3 + 1, var4).func_149721_r()) {
         return true;
      } else if (var5 == 1 && World.func_147466_a(var1, var2, var3 - 1, var4)) {
         return true;
      } else if (var5 == 2 && var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
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
      } else if (var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
         return true;
      } else if (World.func_147466_a(var1, var2, var3 - 1, var4)) {
         return true;
      } else {
         return var1.func_147439_a(var2, var3 + 1, var4).func_149721_r();
      }
   }

   @Override
   public int func_149660_a(World var1, int var2, int var3, int var4, int var5, float var6, float var7, float var8, int var9) {
      int var11 = var9 & 8;
      int var10 = var9 & 7;
      byte var12 = -1;
      if (var5 == 0 && var1.func_147439_a(var2, var3 + 1, var4).func_149721_r()) {
         var12 = 0;
      }

      if (var5 == 1 && World.func_147466_a(var1, var2, var3 - 1, var4)) {
         var12 = 5;
      }

      if (var5 == 2 && var1.func_147439_a(var2, var3, var4 + 1).func_149721_r()) {
         var12 = 4;
      }

      if (var5 == 3 && var1.func_147439_a(var2, var3, var4 - 1).func_149721_r()) {
         var12 = 3;
      }

      if (var5 == 4 && var1.func_147439_a(var2 + 1, var3, var4).func_149721_r()) {
         var12 = 2;
      }

      if (var5 == 5 && var1.func_147439_a(var2 - 1, var3, var4).func_149721_r()) {
         var12 = 1;
      }

      return var12 + var11;
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = var1.func_72805_g(var2, var3, var4);
      int var8 = var7 & 7;
      int var9 = var7 & 8;
      if (var8 == func_149819_b(1)) {
         if ((MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 1) == 0) {
            var1.func_72921_c(var2, var3, var4, 5 | var9, 2);
         } else {
            var1.func_72921_c(var2, var3, var4, 6 | var9, 2);
         }
      } else if (var8 == func_149819_b(0)) {
         if ((MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 1) == 0) {
            var1.func_72921_c(var2, var3, var4, 7 | var9, 2);
         } else {
            var1.func_72921_c(var2, var3, var4, 0 | var9, 2);
         }
      }
   }

   public static int func_149819_b(int var0) {
      switch(var0) {
         case 0:
            return 0;
         case 1:
            return 5;
         case 2:
            return 4;
         case 3:
            return 3;
         case 4:
            return 2;
         case 5:
            return 1;
         default:
            return -1;
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      if (this.func_149820_e(var1, var2, var3, var4)) {
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

         if (!World.func_147466_a(var1, var2, var3 - 1, var4) && var6 == 5) {
            var7 = true;
         }

         if (!World.func_147466_a(var1, var2, var3 - 1, var4) && var6 == 6) {
            var7 = true;
         }

         if (!var1.func_147439_a(var2, var3 + 1, var4).func_149721_r() && var6 == 0) {
            var7 = true;
         }

         if (!var1.func_147439_a(var2, var3 + 1, var4).func_149721_r() && var6 == 7) {
            var7 = true;
         }

         if (var7) {
            this.func_149697_b(var1, var2, var3, var4, var1.func_72805_g(var2, var3, var4), 0);
            var1.func_147468_f(var2, var3, var4);
         }
      }
   }

   private boolean func_149820_e(World var1, int var2, int var3, int var4) {
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
      int var5 = var1.func_72805_g(var2, var3, var4) & 7;
      float var6 = 0.1875F;
      if (var5 == 1) {
         this.func_149676_a(0.0F, 0.2F, 0.5F - var6, var6 * 2.0F, 0.8F, 0.5F + var6);
      } else if (var5 == 2) {
         this.func_149676_a(1.0F - var6 * 2.0F, 0.2F, 0.5F - var6, 1.0F, 0.8F, 0.5F + var6);
      } else if (var5 == 3) {
         this.func_149676_a(0.5F - var6, 0.2F, 0.0F, 0.5F + var6, 0.8F, var6 * 2.0F);
      } else if (var5 == 4) {
         this.func_149676_a(0.5F - var6, 0.2F, 1.0F - var6 * 2.0F, 0.5F + var6, 0.8F, 1.0F);
      } else if (var5 == 5 || var5 == 6) {
         var6 = 0.25F;
         this.func_149676_a(0.5F - var6, 0.0F, 0.5F - var6, 0.5F + var6, 0.6F, 0.5F + var6);
      } else if (var5 == 0 || var5 == 7) {
         var6 = 0.25F;
         this.func_149676_a(0.5F - var6, 0.4F, 0.5F - var6, 0.5F + var6, 1.0F, 0.5F + var6);
      }
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         int var10 = var1.func_72805_g(var2, var3, var4);
         int var11 = var10 & 7;
         int var12 = 8 - (var10 & 8);
         var1.func_72921_c(var2, var3, var4, var11 + var12, 3);
         var1.func_72908_a((double)var2 + 0.5, (double)var3 + 0.5, (double)var4 + 0.5, "random.click", 0.3F, var12 > 0 ? 0.6F : 0.5F);
         var1.func_147459_d(var2, var3, var4, this);
         if (var11 == 1) {
            var1.func_147459_d(var2 - 1, var3, var4, this);
         } else if (var11 == 2) {
            var1.func_147459_d(var2 + 1, var3, var4, this);
         } else if (var11 == 3) {
            var1.func_147459_d(var2, var3, var4 - 1, this);
         } else if (var11 == 4) {
            var1.func_147459_d(var2, var3, var4 + 1, this);
         } else if (var11 == 5 || var11 == 6) {
            var1.func_147459_d(var2, var3 - 1, var4, this);
         } else if (var11 == 0 || var11 == 7) {
            var1.func_147459_d(var2, var3 + 1, var4, this);
         }

         return true;
      }
   }

   @Override
   public void func_149749_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      if ((var6 & 8) > 0) {
         var1.func_147459_d(var2, var3, var4, this);
         int var7 = var6 & 7;
         if (var7 == 1) {
            var1.func_147459_d(var2 - 1, var3, var4, this);
         } else if (var7 == 2) {
            var1.func_147459_d(var2 + 1, var3, var4, this);
         } else if (var7 == 3) {
            var1.func_147459_d(var2, var3, var4 - 1, this);
         } else if (var7 == 4) {
            var1.func_147459_d(var2, var3, var4 + 1, this);
         } else if (var7 == 5 || var7 == 6) {
            var1.func_147459_d(var2, var3 - 1, var4, this);
         } else if (var7 == 0 || var7 == 7) {
            var1.func_147459_d(var2, var3 + 1, var4, this);
         }
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
         if (var7 == 0 && var5 == 0) {
            return 15;
         } else if (var7 == 7 && var5 == 0) {
            return 15;
         } else if (var7 == 6 && var5 == 1) {
            return 15;
         } else if (var7 == 5 && var5 == 1) {
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
}
