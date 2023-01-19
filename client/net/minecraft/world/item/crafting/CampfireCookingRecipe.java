package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class CampfireCookingRecipe extends AbstractCookingRecipe {
   public CampfireCookingRecipe(ResourceLocation var1, String var2, CookingBookCategory var3, Ingredient var4, ItemStack var5, float var6, int var7) {
      super(RecipeType.CAMPFIRE_COOKING, var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.CAMPFIRE);
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
   }
}
