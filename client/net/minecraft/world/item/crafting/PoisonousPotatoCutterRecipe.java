package net.minecraft.world.item.crafting;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class PoisonousPotatoCutterRecipe extends SingleItemRecipe {
   public PoisonousPotatoCutterRecipe(String var1, Ingredient var2, ItemStack var3) {
      super(RecipeType.POISONOUS_POTATO_CUTTING, RecipeSerializer.POISONOUS_POTATO_CUTTER_RECIPE, var1, var2, var3);
   }

   @Override
   public boolean matches(Container var1, Level var2) {
      return this.ingredient.test(var1.getItem(0));
   }

   @Override
   public ItemStack getToastSymbol() {
      return new ItemStack(Blocks.POISONOUS_POTATO_CUTTER);
   }
}
