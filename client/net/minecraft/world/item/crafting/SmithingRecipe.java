package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public interface SmithingRecipe extends Recipe<SmithingRecipeInput> {
   @Override
   default RecipeType<?> getType() {
      return RecipeType.SMITHING;
   }

   @Override
   default boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 3 && var2 >= 1;
   }

   @Override
   default ItemStack getCategoryIconItem() {
      return new ItemStack(Blocks.SMITHING_TABLE);
   }

   default boolean matches(SmithingRecipeInput var1, Level var2) {
      return this.isTemplateIngredient(var1.template()) && this.isBaseIngredient(var1.base()) && this.isAdditionIngredient(var1.addition());
   }

   boolean isTemplateIngredient(ItemStack var1);

   boolean isBaseIngredient(ItemStack var1);

   boolean isAdditionIngredient(ItemStack var1);
}
