package net.minecraft.item.crafting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ShapelessRecipes implements IRecipe {
   private final ItemStack field_77580_a;
   private final List field_77579_b;

   public ShapelessRecipes(ItemStack var1, List var2) {
      super();
      this.field_77580_a = var1;
      this.field_77579_b = var2;
   }

   @Override
   public ItemStack func_77571_b() {
      return this.field_77580_a;
   }

   @Override
   public boolean func_77569_a(InventoryCrafting var1, World var2) {
      ArrayList var3 = new ArrayList(this.field_77579_b);

      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 3; ++var5) {
            ItemStack var6 = var1.func_70463_b(var5, var4);
            if (var6 != null) {
               boolean var7 = false;

               for(ItemStack var9 : var3) {
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

   @Override
   public ItemStack func_77572_b(InventoryCrafting var1) {
      return this.field_77580_a.func_77946_l();
   }

   @Override
   public int func_77570_a() {
      return this.field_77579_b.size();
   }
}
