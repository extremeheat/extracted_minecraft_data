package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
   private final ItemStack field_77580_a;
   private final List<ItemStack> field_77579_b;

   public ShapelessRecipes(ItemStack var1, List<ItemStack> var2) {
      super();
      this.field_77580_a = var1;
      this.field_77579_b = var2;
   }

   public ItemStack func_77571_b() {
      return this.field_77580_a;
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

   public boolean func_77569_a(InventoryCrafting var1, World var2) {
      ArrayList var3 = Lists.newArrayList(this.field_77579_b);

      for(int var4 = 0; var4 < var1.func_174923_h(); ++var4) {
         for(int var5 = 0; var5 < var1.func_174922_i(); ++var5) {
            ItemStack var6 = var1.func_70463_b(var5, var4);
            if (var6 != null) {
               boolean var7 = false;
               Iterator var8 = var3.iterator();

               while(var8.hasNext()) {
                  ItemStack var9 = (ItemStack)var8.next();
                  if (var6.func_77973_b() == var9.func_77973_b() && (var9.func_77960_j() == 32767 || var6.func_77960_j() == var9.func_77960_j())) {
                     var7 = true;
                     var3.remove(var9);
                     break;
                  }
               }

               if (!var7) {
                  return false;
               }
            }
         }
      }

      return var3.isEmpty();
   }

   public ItemStack func_77572_b(InventoryCrafting var1) {
      return this.field_77580_a.func_77946_l();
   }

   public int func_77570_a() {
      return this.field_77579_b.size();
   }
}
