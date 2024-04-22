package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;

public abstract class CustomRecipe implements CraftingRecipe {
   private final CraftingBookCategory category;

   public CustomRecipe(CraftingBookCategory var1) {
      super();
      this.category = var1;
   }

   @Override
   public boolean isSpecial() {
      return true;
   }

   @Override
   public ItemStack getResultItem(HolderLookup.Provider var1) {
      return ItemStack.EMPTY;
   }

   @Override
   public CraftingBookCategory category() {
      return this.category;
   }
}