package net.minecraft.world.item.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class StonecutterRecipe extends SingleItemRecipe {
   public StonecutterRecipe(ResourceLocation var1, String var2, Ingredient var3, ItemStack var4) {
      super(RecipeType.STONECUTTING, RecipeSerializer.STONECUTTER, var1, var2, var3, var4);
   }

   public boolean matches(Container var1, Level var2) {
      return this.ingredient.test(var1.getItem(0));
   }

   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.STONECUTTER);
   }
}
