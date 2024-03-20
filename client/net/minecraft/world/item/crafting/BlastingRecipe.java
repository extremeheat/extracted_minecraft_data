package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class BlastingRecipe extends AbstractCookingRecipe {
   public BlastingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(RecipeType.BLASTING, var1, var2, var3, var4, var5, var6);
   }

   @Override
   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.BLAST_FURNACE);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.BLASTING_RECIPE;
   }
}