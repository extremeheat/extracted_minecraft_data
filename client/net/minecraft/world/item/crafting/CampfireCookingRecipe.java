package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
   public CampfireCookingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected Item furnaceIcon() {
      return Items.CAMPFIRE;
   }

   public RecipeSerializer<CampfireCookingRecipe> getSerializer() {
      return RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
   }

   public RecipeType<CampfireCookingRecipe> getType() {
      return RecipeType.CAMPFIRE_COOKING;
   }

   public RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.CAMPFIRE;
   }
}
