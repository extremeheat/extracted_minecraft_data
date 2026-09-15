package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public record BrewingInput(ItemStack input, ItemStack reagent) implements RecipeInput {
   public BrewingInput {
      super();
   }

   public ItemStack getItem(final int index) {
      ItemStack var10000;
      switch (index) {
         case 0 -> var10000 = this.input;
         case 1 -> var10000 = this.reagent;
         default -> throw new IllegalArgumentException("No item for index " + index);
      }

      return var10000;
   }

   public int size() {
      return 2;
   }
}
