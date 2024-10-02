package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public interface CraftingRecipe extends Recipe<CraftingInput> {
   @Override
   default RecipeType<CraftingRecipe> getType() {
      return RecipeType.CRAFTING;
   }

   @Override
   RecipeSerializer<? extends CraftingRecipe> getSerializer();

   CraftingBookCategory category();

   default NonNullList<ItemStack> getRemainingItems(CraftingInput var1) {
      return defaultCraftingReminder(var1);
   }

   static NonNullList<ItemStack> defaultCraftingReminder(CraftingInput var0) {
      NonNullList var1 = NonNullList.withSize(var0.size(), ItemStack.EMPTY);

      for (int var2 = 0; var2 < var1.size(); var2++) {
         Item var3 = var0.getItem(var2).getItem();
         var1.set(var2, var3.getCraftingRemainder());
      }

      return var1;
   }

   @Override
   default BasicRecipeBookCategory recipeBookCategory() {
      return switch (this.category()) {
         case BUILDING -> BasicRecipeBookCategory.CRAFTING_BUILDING_BLOCKS;
         case EQUIPMENT -> BasicRecipeBookCategory.CRAFTING_EQUIPMENT;
         case REDSTONE -> BasicRecipeBookCategory.CRAFTING_REDSTONE;
         case MISC -> BasicRecipeBookCategory.CRAFTING_MISC;
      };
   }
}
