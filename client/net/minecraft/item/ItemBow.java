package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class ItemBow extends Item {
   public static final String[] field_94601_a = new String[]{"pulling_0", "pulling_1", "pulling_2"};

   public ItemBow() {
      super();
      this.field_77777_bU = 1;
      this.func_77656_e(384);
      this.func_77637_a(CreativeTabs.field_78037_j);
   }

   public void func_77615_a(ItemStack var1, World var2, EntityPlayer var3, int var4) {
      boolean var5 = var3.field_71075_bZ.field_75098_d || EnchantmentHelper.func_77506_a(Enchantment.field_77342_w.field_77352_x, var1) > 0;
      if (var5 || var3.field_71071_by.func_146028_b(Items.field_151032_g)) {
         int var6 = this.func_77626_a(var1) - var4;
         float var7 = (float)var6 / 20.0F;
         var7 = (var7 * var7 + var7 * 2.0F) / 3.0F;
         if ((double)var7 < 0.1D) {
            return;
         }

         if (var7 > 1.0F) {
            var7 = 1.0F;
         }

         EntityArrow var8 = new EntityArrow(var2, var3, var7 * 2.0F);
         if (var7 == 1.0F) {
            var8.func_70243_d(true);
         }

         int var9 = EnchantmentHelper.func_77506_a(Enchantment.field_77345_t.field_77352_x, var1);
         if (var9 > 0) {
            var8.func_70239_b(var8.func_70242_d() + (double)var9 * 0.5D + 0.5D);
         }

         int var10 = EnchantmentHelper.func_77506_a(Enchantment.field_77344_u.field_77352_x, var1);
         if (var10 > 0) {
            var8.func_70240_a(var10);
         }

         if (EnchantmentHelper.func_77506_a(Enchantment.field_77343_v.field_77352_x, var1) > 0) {
            var8.func_70015_d(100);
         }

         var1.func_77972_a(1, var3);
         var2.func_72956_a(var3, "random.bow", 1.0F, 1.0F / (field_77697_d.nextFloat() * 0.4F + 1.2F) + var7 * 0.5F);
         if (var5) {
            var8.field_70251_a = 2;
         } else {
            var3.field_71071_by.func_146026_a(Items.field_151032_g);
         }

         var3.func_71029_a(StatList.field_75929_E[Item.func_150891_b(this)]);
         if (!var2.field_72995_K) {
            var2.func_72838_d(var8);
         }
      }

   }

   public ItemStack func_77654_b(ItemStack var1, World var2, EntityPlayer var3) {
      return var1;
   }

   public int func_77626_a(ItemStack var1) {
      return 72000;
   }

   public EnumAction func_77661_b(ItemStack var1) {
      return EnumAction.BOW;
   }

   public ItemStack func_77659_a(ItemStack var1, World var2, EntityPlayer var3) {
      if (var3.field_71075_bZ.field_75098_d || var3.field_71071_by.func_146028_b(Items.field_151032_g)) {
         var3.func_71008_a(var1, this.func_77626_a(var1));
      }

      return var1;
   }

   public int func_77619_b() {
      return 1;
   }
}
