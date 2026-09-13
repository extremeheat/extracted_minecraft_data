package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoublePlant extends BlockBush implements IGrowable {
   public static final String[] field_149892_a = new String[]{"sunflower", "syringa", "grass", "fern", "rose", "paeonia"};
   private IIcon[] field_149893_M;
   private IIcon[] field_149894_N;
   public IIcon[] field_149891_b;

   public BlockDoublePlant() {
      super(Material.field_151585_k);
      this.func_149711_c(0.0F);
      this.func_149672_a(field_149779_h);
      this.func_149663_c("doublePlant");
   }

   @Override
   public int func_149645_b() {
      return 40;
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public int func_149885_e(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      return !func_149887_c(var5) ? var5 & 7 : var1.func_72805_g(var2, var3 - 1, var4) & 7;
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      return super.func_149742_c(var1, var2, var3, var4) && var1.func_147437_c(var2, var3 + 1, var4);
   }

   @Override
   protected void func_149855_e(World var1, int var2, int var3, int var4) {
      if (!this.func_149718_j(var1, var2, var3, var4)) {
         int var5 = var1.func_72805_g(var2, var3, var4);
         if (!func_149887_c(var5)) {
            this.func_149697_b(var1, var2, var3, var4, var5, 0);
            if (var1.func_147439_a(var2, var3 + 1, var4) == this) {
               var1.func_147465_d(var2, var3 + 1, var4, Blocks.field_150350_a, 0, 2);
            }
         }

         var1.func_147465_d(var2, var3, var4, Blocks.field_150350_a, 0, 2);
      }
   }

   @Override
   public boolean func_149718_j(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      if (func_149887_c(var5)) {
         return var1.func_147439_a(var2, var3 - 1, var4) == this;
      } else {
         return var1.func_147439_a(var2, var3 + 1, var4) == this && super.func_149718_j(var1, var2, var3, var4);
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      if (func_149887_c(var1)) {
         return null;
      } else {
         int var4 = func_149890_d(var1);
         return var4 != 3 && var4 != 2 ? Item.func_150898_a(this) : null;
      }
   }

   @Override
   public int func_149692_a(int var1) {
      return func_149887_c(var1) ? 0 : var1 & 7;
   }

   public static boolean func_149887_c(int var0) {
      return (var0 & 8) != 0;
   }

   public static int func_149890_d(int var0) {
      return var0 & 7;
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return func_149887_c(var2) ? this.field_149893_M[0] : this.field_149893_M[var2 & 7];
   }

   public IIcon func_149888_a(boolean var1, int var2) {
      return var1 ? this.field_149894_N[var2] : this.field_149893_M[var2];
   }

   @Override
   public int func_149720_d(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = this.func_149885_e(var1, var2, var3, var4);
      return var5 != 2 && var5 != 3 ? 16777215 : var1.func_72807_a(var2, var4).func_150558_b(var2, var3, var4);
   }

   public void func_149889_c(World var1, int var2, int var3, int var4, int var5, int var6) {
      var1.func_147465_d(var2, var3, var4, this, var5, var6);
      var1.func_147465_d(var2, var3 + 1, var4, this, 8, var6);
   }

   @Override
   public void func_149689_a(World var1, int var2, int var3, int var4, EntityLivingBase var5, ItemStack var6) {
      int var7 = ((MathHelper.func_76128_c((double)(var5.field_70177_z * 4.0F / 360.0F) + 0.5) & 3) + 2) % 4;
      var1.func_147465_d(var2, var3 + 1, var4, this, 8 | var7, 2);
   }

   @Override
   public void func_149636_a(World var1, EntityPlayer var2, int var3, int var4, int var5, int var6) {
      if (var1.field_72995_K
         || var2.func_71045_bC() == null
         || var2.func_71045_bC().func_77973_b() != Items.field_151097_aZ
         || func_149887_c(var6)
         || !this.func_149886_b(var1, var3, var4, var5, var6, var2)) {
         super.func_149636_a(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      if (func_149887_c(var5)) {
         if (var1.func_147439_a(var2, var3 - 1, var4) == this) {
            if (!var6.field_71075_bZ.field_75098_d) {
               int var7 = var1.func_72805_g(var2, var3 - 1, var4);
               int var8 = func_149890_d(var7);
               if (var8 != 3 && var8 != 2) {
                  var1.func_147480_a(var2, var3 - 1, var4, true);
               } else {
                  if (!var1.field_72995_K && var6.func_71045_bC() != null && var6.func_71045_bC().func_77973_b() == Items.field_151097_aZ) {
                     this.func_149886_b(var1, var2, var3, var4, var7, var6);
                  }

                  var1.func_147468_f(var2, var3 - 1, var4);
               }
            } else {
               var1.func_147468_f(var2, var3 - 1, var4);
            }
         }
      } else if (var6.field_71075_bZ.field_75098_d && var1.func_147439_a(var2, var3 + 1, var4) == this) {
         var1.func_147465_d(var2, var3 + 1, var4, Blocks.field_150350_a, 0, 2);
      }

      super.func_149681_a(var1, var2, var3, var4, var5, var6);
   }

   private boolean func_149886_b(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      int var7 = func_149890_d(var5);
      if (var7 != 3 && var7 != 2) {
         return false;
      } else {
         var6.func_71064_a(StatList.field_75934_C[Block.func_149682_b(this)], 1);
         byte var8 = 1;
         if (var7 == 3) {
            var8 = 2;
         }

         this.func_149642_a(var1, var2, var3, var4, new ItemStack(Blocks.field_150329_H, 2, var8));
         return true;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_149893_M = new IIcon[field_149892_a.length];
      this.field_149894_N = new IIcon[field_149892_a.length];

      for(int var2 = 0; var2 < this.field_149893_M.length; ++var2) {
         this.field_149893_M[var2] = var1.func_94245_a("double_plant_" + field_149892_a[var2] + "_bottom");
         this.field_149894_N[var2] = var1.func_94245_a("double_plant_" + field_149892_a[var2] + "_top");
      }

      this.field_149891_b = new IIcon[2];
      this.field_149891_b[0] = var1.func_94245_a("double_plant_sunflower_front");
      this.field_149891_b[1] = var1.func_94245_a("double_plant_sunflower_back");
   }

   @Override
   public void func_149666_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < this.field_149893_M.length; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public int func_149643_k(World var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      return func_149887_c(var5) ? func_149890_d(var1.func_72805_g(var2, var3 - 1, var4)) : func_149890_d(var5);
   }

   @Override
   public boolean func_149851_a(World var1, int var2, int var3, int var4, boolean var5) {
      int var6 = this.func_149885_e(var1, var2, var3, var4);
      return var6 != 2 && var6 != 3;
   }

   @Override
   public boolean func_149852_a(World var1, Random var2, int var3, int var4, int var5) {
      return true;
   }

   @Override
   public void func_149853_b(World var1, Random var2, int var3, int var4, int var5) {
      int var6 = this.func_149885_e(var1, var3, var4, var5);
      this.func_149642_a(var1, var3, var4, var5, new ItemStack(this, 1, var6));
   }
}
