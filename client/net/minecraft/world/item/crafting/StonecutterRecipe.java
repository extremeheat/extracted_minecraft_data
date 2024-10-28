package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class StonecutterRecipe extends SingleItemRecipe {
   public StonecutterRecipe(String var1, Ingredient var2, ItemStack var3) {
      super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTER, var1, var2, var3);
   }

   public boolean matches(SingleRecipeInput var1, Level var2) {
      return this.ingredient.test(var1.item());
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.STONECUTTER);
   }
}
