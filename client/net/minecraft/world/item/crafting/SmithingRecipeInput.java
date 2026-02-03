package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public record SmithingRecipeInput(ItemStack template, ItemStack base, ItemStack addition) implements RecipeInput {
   public SmithingRecipeInput {
      super();
   }

   public ItemStack getItem(final int index) {
      ItemStack var10000;
      switch (index) {
         case 0 -> var10000 = this.template;
         case 1 -> var10000 = this.base;
         case 2 -> var10000 = this.addition;
         default -> throw new IllegalArgumentException("Recipe does not contain slot " + index);
      }

      return var10000;
   }

   public int size() {
      return 3;
   }

   public boolean isEmpty() {
      return this.template.isEmpty() && this.base.isEmpty() && this.addition.isEmpty();
   }
}
