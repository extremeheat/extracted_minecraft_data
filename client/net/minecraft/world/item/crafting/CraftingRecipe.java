package net.minecraft.world.item.crafting;

public interface CraftingRecipe extends Recipe<CraftingInput> {
   default RecipeType<?> getType() {
      return RecipeType.CRAFTING;
   }

   CraftingBookCategory category();
}
