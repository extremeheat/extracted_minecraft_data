package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeBookCloning implements IRecipe {
   public RecipeBookCloning() {
      super();
   }

   public boolean func_77569_a(InventoryCrafting var1, World var2) {
      int var3 = 0;
      ItemStack var4 = null;

      for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
         ItemStack var6 = var1.func_70301_a(var5);
         if (var6 != null) {
            if (var6.func_77973_b() == Items.field_151164_bB) {
               if (var4 != null) {
                  return false;
               }

               var4 = var6;
            } else {
               if (var6.func_77973_b() != Items.field_151099_bA) {
                  return false;
               }

               ++var3;
            }
         }
      }

      return var4 != null && var3 > 0;
   }

   public ItemStack func_77572_b(InventoryCrafting var1) {
      int var2 = 0;
      ItemStack var3 = null;

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         ItemStack var5 = var1.func_70301_a(var4);
         if (var5 != null) {
            if (var5.func_77973_b() == Items.field_151164_bB) {
               if (var3 != null) {
                  return null;
               }

               var3 = var5;
            } else {
               if (var5.func_77973_b() != Items.field_151099_bA) {
                  return null;
               }

               ++var2;
            }
         }
      }

      if (var3 != null && var2 >= 1 && ItemEditableBook.func_179230_h(var3) < 2) {
         ItemStack var6 = new ItemStack(Items.field_151164_bB, var2);
         var6.func_77982_d((NBTTagCompound)var3.func_77978_p().func_74737_b());
         var6.func_77978_p().func_74768_a("generation", ItemEditableBook.func_179230_h(var3) + 1);
         if (var3.func_82837_s()) {
            var6.func_151001_c(var3.func_82833_r());
         }

         return var6;
      } else {
         return null;
      }
   }

   public int func_77570_a() {
      return 9;
   }

   public ItemStack func_77571_b() {
      return null;
   }

   public ItemStack[] func_179532_b(InventoryCrafting var1) {
      ItemStack[] var2 = new ItemStack[var1.func_70302_i_()];

      for(int var3 = 0; var3 < var2.length; ++var3) {
         ItemStack var4 = var1.func_70301_a(var3);
         if (var4 != null && var4.func_77973_b() instanceof ItemEditableBook) {
            var2[var3] = var4;
            break;
         }
      }

      return var2;
   }
}
