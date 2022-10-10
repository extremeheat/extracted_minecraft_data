package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipesMapCloning extends IRecipeHidden {
   public RecipesMapCloning(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (!(var1 instanceof InventoryCrafting)) {
         return false;
      } else {
         int var3 = 0;
         ItemStack var4 = ItemStack.field_190927_a;

         for(int var5 = 0; var5 < var1.func_70302_i_(); ++var5) {
            ItemStack var6 = var1.func_70301_a(var5);
            if (!var6.func_190926_b()) {
               if (var6.func_77973_b() == Items.field_151098_aY) {
                  if (!var4.func_190926_b()) {
                     return false;
                  }

                  var4 = var6;
               } else {
                  if (var6.func_77973_b() != Items.field_151148_bJ) {
                     return false;
                  }

                  ++var3;
               }
            }
         }

         return !var4.func_190926_b() && var3 > 0;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      int var2 = 0;
      ItemStack var3 = ItemStack.field_190927_a;

      for(int var4 = 0; var4 < var1.func_70302_i_(); ++var4) {
         ItemStack var5 = var1.func_70301_a(var4);
         if (!var5.func_190926_b()) {
            if (var5.func_77973_b() == Items.field_151098_aY) {
               if (!var3.func_190926_b()) {
                  return ItemStack.field_190927_a;
               }

               var3 = var5;
            } else {
               if (var5.func_77973_b() != Items.field_151148_bJ) {
                  return ItemStack.field_190927_a;
               }

               ++var2;
            }
         }
      }

      if (!var3.func_190926_b() && var2 >= 1) {
         ItemStack var6 = var3.func_77946_l();
         var6.func_190920_e(var2 + 1);
         return var6;
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 >= 3 && var2 >= 3;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199579_e;
   }
}
