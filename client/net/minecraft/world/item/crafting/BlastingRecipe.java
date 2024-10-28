package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class BlastingRecipe extends AbstractCookingRecipe {
   public BlastingRecipe(String var1, CookingBookCategory var2, Ingredient var3, ItemStack var4, float var5, int var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   protected Item furnaceIcon() {
      return Items.BLAST_FURNACE;
   }

   public RecipeSerializer<BlastingRecipe> getSerializer() {
      return RecipeSerializer.BLASTING_RECIPE;
   }

   public RecipeType<BlastingRecipe> getType() {
      return RecipeType.BLASTING;
   }

   public RecipeBookCategory recipeBookCategory() {
      RecipeBookCategory var10000;
      switch (this.category()) {
         case BLOCKS:
            var10000 = RecipeBookCategories.BLAST_FURNACE_BLOCKS;
            break;
         case FOOD:
         case MISC:
            var10000 = RecipeBookCategories.BLAST_FURNACE_MISC;
            break;
         default:
            throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }
}
