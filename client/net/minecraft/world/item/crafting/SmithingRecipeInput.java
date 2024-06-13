package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public record SmithingRecipeInput(ItemStack template, ItemStack base, ItemStack addition) implements RecipeInput {
   public SmithingRecipeInput(ItemStack template, ItemStack base, ItemStack addition) {
      super();
      this.template = template;
      this.base = base;
      this.addition = addition;
   }

   @Override
   public ItemStack getItem(int var1) {
      return switch (var1) {
         case 0 -> this.template;
         case 1 -> this.base;
         case 2 -> this.addition;
         default -> throw new IllegalArgumentException("Recipe does not contain slot " + var1);
      };
   }

   @Override
   public int size() {
      return 3;
   }

   @Override
   public boolean isEmpty() {
      return this.template.isEmpty() && this.base.isEmpty() && this.addition.isEmpty();
   }
}
