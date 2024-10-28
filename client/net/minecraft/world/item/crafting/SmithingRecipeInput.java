package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public record SmithingRecipeInput(ItemStack template, ItemStack base, ItemStack addition) implements RecipeInput {
   public SmithingRecipeInput(ItemStack template, ItemStack base, ItemStack addition) {
      super();
      this.template = template;
      this.base = base;
      this.addition = addition;
   }

   public ItemStack getItem(int var1) {
      ItemStack var10000;
      switch (var1) {
         case 0 -> var10000 = this.template;
         case 1 -> var10000 = this.base;
         case 2 -> var10000 = this.addition;
         default -> throw new IllegalArgumentException("Recipe does not contain slot " + var1);
      }

      return var10000;
   }

   public int size() {
      return 3;
   }

   public boolean isEmpty() {
      return this.template.isEmpty() && this.base.isEmpty() && this.addition.isEmpty();
   }

   public ItemStack template() {
      return this.template;
   }

   public ItemStack base() {
      return this.base;
   }

   public ItemStack addition() {
      return this.addition;
   }
}
