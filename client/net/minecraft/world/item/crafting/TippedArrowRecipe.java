package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class TippedArrowRecipe extends CustomRecipe {
   public TippedArrowRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      if (var1.width() == 3 && var1.height() == 3 && var1.ingredientCount() == 9) {
         for (int var3 = 0; var3 < var1.height(); var3++) {
            for (int var4 = 0; var4 < var1.width(); var4++) {
               ItemStack var5 = var1.getItem(var4, var3);
               if (var5.isEmpty()) {
                  return false;
               }

               if (var4 == 1 && var3 == 1) {
                  if (!var5.is(Items.LINGERING_POTION)) {
                     return false;
                  }
               } else if (!var5.is(Items.ARROW)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = var1.getItem(1, 1);
      if (!var3.is(Items.LINGERING_POTION)) {
         return ItemStack.EMPTY;
      } else {
         ItemStack var4 = new ItemStack(Items.TIPPED_ARROW, 8);
         var4.set(DataComponents.POTION_CONTENTS, var3.get(DataComponents.POTION_CONTENTS));
         return var4;
      }
   }

   @Override
   public RecipeSerializer<TippedArrowRecipe> getSerializer() {
      return RecipeSerializer.TIPPED_ARROW;
   }
}
