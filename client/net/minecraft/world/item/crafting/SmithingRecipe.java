package net.minecraft.world.item.crafting;

import java.util.Optional;
import net.minecraft.world.level.Level;

public interface SmithingRecipe extends Recipe<SmithingRecipeInput> {
   default RecipeType<SmithingRecipe> getType() {
      return RecipeType.SMITHING;
   }

   RecipeSerializer<? extends SmithingRecipe> getSerializer();

   default boolean matches(SmithingRecipeInput var1, Level var2) {
      return Ingredient.testOptionalIngredient(this.templateIngredient(), var1.template()) && this.baseIngredient().test(var1.base()) && Ingredient.testOptionalIngredient(this.additionIngredient(), var1.addition());
   }

   Optional<Ingredient> templateIngredient();

   Ingredient baseIngredient();

   Optional<Ingredient> additionIngredient();

   default RecipeBookCategory recipeBookCategory() {
      return RecipeBookCategories.SMITHING;
   }
}
