package net.minecraft.world.item.crafting;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MapCloningRecipe extends CustomRecipe {
   public MapCloningRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingInput var1, Level var2) {
      if (var1.ingredientCount() < 2) {
         return false;
      } else {
         boolean var3 = false;
         boolean var4 = false;

         for (int var5 = 0; var5 < var1.size(); var5++) {
            ItemStack var6 = var1.getItem(var5);
            if (!var6.isEmpty()) {
               if (var6.has(DataComponents.MAP_ID)) {
                  if (var4) {
                     return false;
                  }

                  var4 = true;
               } else {
                  if (!var6.is(Items.MAP)) {
                     return false;
                  }

                  var3 = true;
               }
            }
         }

         return var4 && var3;
      }
   }

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for (int var5 = 0; var5 < var1.size(); var5++) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.has(DataComponents.MAP_ID)) {
               if (!var4.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var4 = var6;
            } else {
               if (!var6.is(Items.MAP)) {
                  return ItemStack.EMPTY;
               }

               var3++;
            }
         }
      }

      return !var4.isEmpty() && var3 >= 1 ? var4.copyWithCount(var3 + 1) : ItemStack.EMPTY;
   }

   @Override
   public RecipeSerializer<MapCloningRecipe> getSerializer() {
      return RecipeSerializer.MAP_CLONING;
   }
}
