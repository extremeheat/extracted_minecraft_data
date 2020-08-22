package net.minecraft.world.item.crafting;

public interface CraftingRecipe extends Recipe {
   default RecipeType getType() {
      return RecipeType.CRAFTING;
   }
}
