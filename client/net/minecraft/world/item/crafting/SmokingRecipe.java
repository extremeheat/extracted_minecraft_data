package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SmokingRecipe extends AbstractCookingRecipe {
   public SmokingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected Item furnaceIcon() {
      return Items.SMOKER;
   }

   public RecipeType<SmokingRecipe> getType() {
      return RecipeType.SMOKING;
   }

   public RecipeSerializer<SmokingRecipe> getSerializer() {
      return RecipeSerializer.SMOKING_RECIPE;
   }

   public RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.SMOKER_FOOD;
   }
}
