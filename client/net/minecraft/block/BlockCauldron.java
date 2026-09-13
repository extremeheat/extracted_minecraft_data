package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor$ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
   private IIcon field_150029_a;
   private IIcon field_150028_b;
   private IIcon field_150030_M;

   public BlockCauldron() {
      super(Material.field_151573_f);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      if (var1 == 1) {
         return this.field_150028_b;
      } else {
         return var1 == 0 ? this.field_150030_M : this.field_149761_L;
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150029_a = var1.func_94245_a(this.func_149641_N() + "_" + "inner");
      this.field_150028_b = var1.func_94245_a(this.func_149641_N() + "_top");
      this.field_150030_M = var1.func_94245_a(this.func_149641_N() + "_" + "bottom");
      this.field_149761_L = var1.func_94245_a(this.func_149641_N() + "_side");
   }

   public static IIcon func_150026_e(String var0) {
      if (var0.equals("inner")) {
         return Blocks.field_150383_bp.field_150029_a;
      } else {
         return var0.equals("bottom") ? Blocks.field_150383_bp.field_150030_M : null;
      }
   }

   @Override
   public void func_149743_a(World var1, int var2, int var3, int var4, AxisAlignedBB var5, List var6, Entity var7) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      float var8 = 0.125F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, var8, 1.0F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var8);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149676_a(1.0F - var8, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149676_a(0.0F, 0.0F, 1.0F - var8, 1.0F, 1.0F, 1.0F);
      super.func_149743_a(var1, var2, var3, var4, var5, var6, var7);
      this.func_149683_g();
   }

   @Override
   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 24;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public void func_149670_a(World var1, int var2, int var3, int var4, Entity var5) {
      int var6 = func_150027_b(var1.func_72805_g(var2, var3, var4));
      float var7 = (float)var3 + (6.0F + (float)(3 * var6)) / 16.0F;
      if (!var1.field_72995_K && var5.func_70027_ad() && var6 > 0 && var5.field_70121_D.field_72338_b <= (double)var7) {
         var5.func_70066_B();
         this.func_150024_a(var1, var2, var3, var4, var6 - 1);
      }
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (var1.field_72995_K) {
         return true;
      } else {
         ItemStack var10 = var5.field_71071_by.func_70448_g();
         if (var10 == null) {
            return true;
         } else {
            int var11 = var1.func_72805_g(var2, var3, var4);
            int var12 = func_150027_b(var11);
            if (var10.func_77973_b() == Items.field_151131_as) {
               if (var12 < 3) {
                  if (!var5.field_71075_bZ.field_75098_d) {
                     var5.field_71071_by.func_70299_a(var5.field_71071_by.field_70461_c, new ItemStack(Items.field_151133_ar));
                  }

                  this.func_150024_a(var1, var2, var3, var4, 3);
               }

               return true;
            } else {
               if (var10.func_77973_b() == Items.field_151069_bo) {
                  if (var12 > 0) {
                     if (!var5.field_71075_bZ.field_75098_d) {
                        ItemStack var13 = new ItemStack(Items.field_151068_bn, 1, 0);
                        if (!var5.field_71071_by.func_70441_a(var13)) {
                           var1.func_72838_d(new EntityItem(var1, (double)var2 + 0.5, (double)var3 + 1.5, (double)var4 + 0.5, var13));
                        } else if (var5 instanceof EntityPlayerMP) {
                           ((EntityPlayerMP)var5).func_71120_a(var5.field_71069_bz);
                        }

                        --var10.field_77994_a;
                        if (var10.field_77994_a <= 0) {
                           var5.field_71071_by.func_70299_a(var5.field_71071_by.field_70461_c, null);
                        }
                     }

                     this.func_150024_a(var1, var2, var3, var4, var12 - 1);
                  }
               } else if (var12 > 0
                  && var10.func_77973_b() instanceof ItemArmor
                  && ((ItemArmor)var10.func_77973_b()).func_82812_d() == ItemArmor$ArmorMaterial.CLOTH) {
                  ItemArmor var14 = (ItemArmor)var10.func_77973_b();
                  var14.func_82815_c(var10);
                  this.func_150024_a(var1, var2, var3, var4, var12 - 1);
                  return true;
               }

               return false;
            }
         }
      }
   }

   public void func_150024_a(World var1, int var2, int var3, int var4, int var5) {
      var1.func_72921_c(var2, var3, var4, MathHelper.func_76125_a(var5, 0, 3), 2);
      var1.func_147453_f(var2, var3, var4, this);
   }

   @Override
   public void func_149639_l(World var1, int var2, int var3, int var4) {
      if (var1.field_73012_v.nextInt(20) == 1) {
         int var5 = var1.func_72805_g(var2, var3, var4);
         if (var5 < 3) {
            var1.func_72921_c(var2, var3, var4, var5 + 1, 2);
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      return Items.field_151066_bu;
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return Items.field_151066_bu;
   }

   @Override
   public boolean func_149740_M() {
      return true;
   }

   @Override
   public int func_149736_g(World var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      return func_150027_b(var6);
   }

   public static int func_150027_b(int var0) {
      return var0;
   }

   public static float func_150025_c(int var0) {
      int var1 = MathHelper.func_76125_a(var0, 0, 3);
      return (float)(6 + 3 * var1) / 16.0F;
   }
}
