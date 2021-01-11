package net.minecraft.item;

import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;

public class ItemEnchantedBook extends Item {
   public ItemEnchantedBook() {
      super();
   }

   public boolean func_77636_d(ItemStack var1) {
      return true;
   }

   public boolean func_77616_k(ItemStack var1) {
      return false;
   }

   public EnumRarity func_77613_e(ItemStack var1) {
      return this.func_92110_g(var1).func_74745_c() > 0 ? EnumRarity.UNCOMMON : super.func_77613_e(var1);
   }

   public NBTTagList func_92110_g(ItemStack var1) {
      NBTTagCompound var2 = var1.func_77978_p();
      return var2 != null && var2.func_150297_b("StoredEnchantments", 9) ? (NBTTagList)var2.func_74781_a("StoredEnchantments") : new NBTTagList();
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      super.func_77624_a(var1, var2, var3, var4);
      NBTTagList var5 = this.func_92110_g(var1);
      if (var5 != null) {
         for(int var6 = 0; var6 < var5.func_74745_c(); ++var6) {
            short var7 = var5.func_150305_b(var6).func_74765_d("id");
            short var8 = var5.func_150305_b(var6).func_74765_d("lvl");
            if (Enchantment.func_180306_c(var7) != null) {
               var3.add(Enchantment.func_180306_c(var7).func_77316_c(var8));
            }
         }
      }

   }

   public void func_92115_a(ItemStack var1, EnchantmentData var2) {
      NBTTagList var3 = this.func_92110_g(var1);
      boolean var4 = true;

      for(int var5 = 0; var5 < var3.func_74745_c(); ++var5) {
         NBTTagCompound var6 = var3.func_150305_b(var5);
         if (var6.func_74765_d("id") == var2.field_76302_b.field_77352_x) {
            if (var6.func_74765_d("lvl") < var2.field_76303_c) {
               var6.func_74777_a("lvl", (short)var2.field_76303_c);
            }

            var4 = false;
            break;
         }
      }

      if (var4) {
         NBTTagCompound var7 = new NBTTagCompound();
         var7.func_74777_a("id", (short)var2.field_76302_b.field_77352_x);
         var7.func_74777_a("lvl", (short)var2.field_76303_c);
         var3.func_74742_a(var7);
      }

      if (!var1.func_77942_o()) {
         var1.func_77982_d(new NBTTagCompound());
      }

      var1.func_77978_p().func_74782_a("StoredEnchantments", var3);
   }

   public ItemStack func_92111_a(EnchantmentData var1) {
      ItemStack var2 = new ItemStack(this);
      this.func_92115_a(var2, var1);
      return var2;
   }

   public void func_92113_a(Enchantment var1, List<ItemStack> var2) {
      for(int var3 = var1.func_77319_d(); var3 <= var1.func_77325_b(); ++var3) {
         var2.add(this.func_92111_a(new EnchantmentData(var1, var3)));
      }

   }

   public WeightedRandomChestContent func_92114_b(Random var1) {
      return this.func_92112_a(var1, 1, 1, 1);
   }

   public WeightedRandomChestContent func_92112_a(Random var1, int var2, int var3, int var4) {
      ItemStack var5 = new ItemStack(Items.field_151122_aG, 1, 0);
      EnchantmentHelper.func_77504_a(var1, var5, 30);
      return new WeightedRandomChestContent(var5, var2, var3, var4);
   }
}
