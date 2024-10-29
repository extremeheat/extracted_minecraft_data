package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.world.item.crafting.ExtendedRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public enum SearchRecipeBookCategory implements ExtendedRecipeBookCategory {
   CRAFTING(new RecipeBookCategory[]{RecipeBookCategories.CRAFTING_EQUIPMENT, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS, RecipeBookCategories.CRAFTING_MISC, RecipeBookCategories.CRAFTING_REDSTONE}),
   FURNACE(new RecipeBookCategory[]{RecipeBookCategories.FURNACE_FOOD, RecipeBookCategories.FURNACE_BLOCKS, RecipeBookCategories.FURNACE_MISC}),
   BLAST_FURNACE(new RecipeBookCategory[]{RecipeBookCategories.BLAST_FURNACE_BLOCKS, RecipeBookCategories.BLAST_FURNACE_MISC}),
   SMOKER(new RecipeBookCategory[]{RecipeBookCategories.SMOKER_FOOD});

   private final List<RecipeBookCategory> includedCategories;

   private SearchRecipeBookCategory(final RecipeBookCategory... var3) {
      this.includedCategories = List.of(var3);
   }

   public List<RecipeBookCategory> includedCategories() {
      return this.includedCategories;
   }

   // $FF: synthetic method
   private static SearchRecipeBookCategory[] $values() {
      return new SearchRecipeBookCategory[]{CRAFTING, FURNACE, BLAST_FURNACE, SMOKER};
   }
}
