package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SmokingRecipe extends AbstractCookingRecipe {
   public SmokingRecipe(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(RecipeType.SMOKING, var1, var2, var3, var4, var5, var6);
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.SMOKER);
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.SMOKING_RECIPE;
   }
}
