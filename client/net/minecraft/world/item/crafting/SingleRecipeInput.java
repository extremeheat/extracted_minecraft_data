package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public record SingleRecipeInput(ItemStack item) implements RecipeInput {
   public SingleRecipeInput {
      super();
   }

   public ItemStack getItem(final int index) {
      if (index != 0) {
         throw new IllegalArgumentException("No item for index " + index);
      } else {
         return this.item;
      }
   }

   public int size() {
      return 1;
   }
}
