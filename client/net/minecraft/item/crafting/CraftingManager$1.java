package net.minecraft.item.crafting;

import java.util.Comparator;

class CraftingManager$1 implements Comparator {
   CraftingManager$1(CraftingManager var1) {
      super();
      this.field_77582_a = var1;
   }

   public int compare(IRecipe var1, IRecipe var2) {
      if (var1 instanceof ShapelessRecipes && var2 instanceof ShapedRecipes) {
         return 1;
      } else if (var2 instanceof ShapelessRecipes && var1 instanceof ShapedRecipes) {
         return -1;
      } else if (var2.func_77570_a() < var1.func_77570_a()) {
         return -1;
      } else {
         return var2.func_77570_a() > var1.func_77570_a() ? 1 : 0;
      }
   }
}
