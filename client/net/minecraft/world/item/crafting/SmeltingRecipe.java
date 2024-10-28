package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SmeltingRecipe extends AbstractCookingRecipe {
   public SmeltingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected Item furnaceIcon() {
      return Items.FURNACE;
   }

   public RecipeSerializer<SmeltingRecipe> getSerializer() {
      return RecipeSerializer.SMELTING_RECIPE;
   }

   public RecipeType<SmeltingRecipe> getType() {
      return RecipeType.SMELTING;
   }

   public RecipeBookCategory recipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.category()) {
         case BLOCKS -> var10000 = RecipeBookCategories.FURNACE_BLOCKS;
         case FOOD -> var10000 = RecipeBookCategories.FURNACE_FOOD;
         case MISC -> var10000 = RecipeBookCategories.FURNACE_MISC;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
