package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public record SingleRecipeInput(ItemStack item) implements RecipeInput {
   public SingleRecipeInput(ItemStack item) {
      super();
      this.item = item;
   }

   @Override
   public ItemStack getItem(int var1) {
      if (var1 != 0) {
         throw new IllegalArgumentException("No item for index " + var1);
      } else {
         return this.item;
      }
   }

   @Override
   public int size() {
      return 1;
   }
}
