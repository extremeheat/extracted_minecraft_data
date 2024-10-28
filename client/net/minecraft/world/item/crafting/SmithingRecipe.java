package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public interface SmithingRecipe extends Recipe<SmithingRecipeInput> {
   default RecipeType<?> getType() {
      return RecipeType.SMITHING;
   }

   default boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 3 && var2 >= 1;
   }

   default ItemStack getToastSymbol() {
      return new ItemStack(Blocks.SMITHING_TABLE);
   }

   boolean isTemplateIngredient(ItemStack var1);

   boolean isBaseIngredient(ItemStack var1);

   boolean isAdditionIngredient(ItemStack var1);
}
