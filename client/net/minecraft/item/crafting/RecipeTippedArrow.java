package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class RecipeTippedArrow extends IRecipeHidden {
   public RecipeTippedArrow(ResourceLocation var1) {
      super(var1);
   }

   public boolean func_77569_a(IInventory var1, World var2) {
      if (var1.func_174922_i() == 3 && var1.func_174923_h() == 3) {
         for(int var3 = 0; var3 < var1.func_174922_i(); ++var3) {
            for(int var4 = 0; var4 < var1.func_174923_h(); ++var4) {
               ItemStack var5 = var1.func_70301_a(var3 + var4 * var1.func_174922_i());
               if (var5.func_190926_b()) {
                  return false;
               }

               Item var6 = var5.func_77973_b();
               if (var3 == 1 && var4 == 1) {
                  if (var6 != Items.field_185156_bI) {
                     return false;
                  }
               } else if (var6 != Items.field_151032_g) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack func_77572_b(IInventory var1) {
      ItemStack var2 = var1.func_70301_a(1 + var1.func_174922_i());
      if (var2.func_77973_b() != Items.field_185156_bI) {
         return ItemStack.field_190927_a;
      } else {
         ItemStack var3 = new ItemStack(Items.field_185167_i, 8);
         PotionUtils.func_185188_a(var3, PotionUtils.func_185191_c(var2));
         PotionUtils.func_185184_a(var3, PotionUtils.func_185190_b(var2));
         return var3;
      }
   }

   public boolean func_194133_a(int var1, int var2) {
      return var1 >= 2 && var2 >= 2;
   }

   public IRecipeSerializer<?> func_199559_b() {
      return RecipeSerializers.field_199585_k;
   }
}
