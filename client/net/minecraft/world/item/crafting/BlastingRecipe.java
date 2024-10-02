package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlastingRecipe extends AbstractCookingRecipe {
   public BlastingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected Item furnaceIcon() {
      return Items.BLAST_FURNACE;
   }

   @Override
   public RecipeSerializer<BlastingRecipe> getSerializer() {
      return RecipeSerializer.BLASTING_RECIPE;
   }

   @Override
   public RecipeType<BlastingRecipe> getType() {
      return RecipeType.BLASTING;
   }

   @Override
   public BasicRecipeBookCategory recipeBookCategory() {
      return switch (this.category()) {
         case BLOCKS -> BasicRecipeBookCategory.BLAST_FURNACE_BLOCKS;
         case FOOD, MISC -> BasicRecipeBookCategory.BLAST_FURNACE_MISC;
      };
   }
}
