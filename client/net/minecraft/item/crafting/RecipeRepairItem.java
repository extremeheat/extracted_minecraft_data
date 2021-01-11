package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class RecipeRepairItem implements IRecipe {
   public RecipeRepairItem() {
      super();
   }

   public boolean func_77569_a(InventoryCrafting var1, World var2) {
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         ItemStack var5 = var1.func_70301_a(var4);
         if (var5 != null) {
            var3.add(var5);
            if (var3.size() > 1) {
               ItemStack var6 = (ItemStack)var3.get(0);
               if (var5.func_77973_b() != var6.func_77973_b() || var6.field_77994_a != 1 || var5.field_77994_a != 1 || !var6.func_77973_b().func_77645_m()) {
                  return false;
               }
            }
         }
      }

      return var3.size() == 2;
   }

   public ItemStack func_77572_b(InventoryCrafting var1) {
      ArrayList var2 = Lists.newArrayList();

      ItemStack var4;
      for(int var3 = 0; var3 < var1.func_70302_i_(); ++var3) {
         var4 = var1.func_70301_a(var3);
         if (var4 != null) {
            var2.add(var4);
            if (var2.size() > 1) {
               ItemStack var5 = (ItemStack)var2.get(0);
               if (var4.func_77973_b() != var5.func_77973_b() || var5.field_77994_a != 1 || var4.field_77994_a != 1 || !var5.func_77973_b().func_77645_m()) {
                  return null;
               }
            }
         }
      }

      if (var2.size() == 2) {
         ItemStack var10 = (ItemStack)var2.get(0);
         var4 = (ItemStack)var2.get(1);
         if (var10.func_77973_b() == var4.func_77973_b() && var10.field_77994_a == 1 && var4.field_77994_a == 1 && var10.func_77973_b().func_77645_m()) {
            Item var11 = var10.func_77973_b();
            int var6 = var11.func_77612_l() - var10.func_77952_i();
            int var7 = var11.func_77612_l() - var4.func_77952_i();
            int var8 = var6 + var7 + var11.func_77612_l() * 5 / 100;
            int var9 = var11.func_77612_l() - var8;
            if (var9 < 0) {
               var9 = 0;
            }

            return new ItemStack(var10.func_77973_b(), 1, var9);
         }
      }

      return null;
   }

   public int func_77570_a() {
      return 4;
   }

   public ItemStack func_77571_b() {
      return null;
   }

   public ItemStack[] func_179532_b(InventoryCrafting var1) {
      ItemStack[] var2 = new ItemStack[var1.func_70302_i_()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ItemStack var4 = var1.func_70301_a(var3);
         if (var4 != null && var4.func_77973_b().func_77634_r()) {
            var2[var3] = new ItemStack(var4.func_77973_b().func_77668_q());
         }
      }

      return var2;
   }
}
