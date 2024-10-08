package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class SmeltingRecipe extends AbstractCookingRecipe {
   public SmeltingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected Item furnaceIcon() {
      return Items.FURNACE;
   }

   @Override
   public RecipeSerializer<SmeltingRecipe> getSerializer() {
      return RecipeSerializer.SMELTING_RECIPE;
   }

   @Override
   public RecipeType<SmeltingRecipe> getType() {
      return RecipeType.SMELTING;
   }

   @Override
   public RecipeBookCategory recipeBookCategory() {
      return switch (this.category()) {
         case BLOCKS -> RecipeBookCategories.FURNACE_BLOCKS;
         case FOOD -> RecipeBookCategories.FURNACE_FOOD;
         case MISC -> RecipeBookCategories.FURNACE_MISC;
      };
   }
}
