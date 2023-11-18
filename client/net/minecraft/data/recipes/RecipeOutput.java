package net.minecraft.data.recipes;

import net.minecraft.advancements.Advancement;

public interface RecipeOutput {
   void accept(FinishedRecipe var1);

   Advancement.Builder advancement();
}
