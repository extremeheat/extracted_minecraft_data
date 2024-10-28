package net.minecraft.world.item.crafting;

import net.minecraft.world.item.ItemStack;

public interface RecipeInput {
   ItemStack getItem(int var1);

   int size();

   default boolean isEmpty() {
      for(int var1 = 0; var1 < this.size(); ++var1) {
         if (!this.getItem(var1).isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
