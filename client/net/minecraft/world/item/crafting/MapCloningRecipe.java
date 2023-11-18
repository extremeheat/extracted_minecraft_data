package net.minecraft.world.item.crafting;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class MapCloningRecipe extends CustomRecipe {
   public MapCloningRecipe(CraftingBookCategory var1) {
      super(var1);
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.is(Items.FILLED_MAP)) {
               if (!var4.isEmpty()) {
                  return false;
               }

               var4 = var6;
            } else {
               if (!var6.is(Items.MAP)) {
                  return false;
               }

               ++var3;
            }
         }
      }

      return !var4.isEmpty() && var3 > 0;
   }

   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      int var3 = 0;
      ItemStack var4 = ItemStack.EMPTY;

      for(int var5 = 0; var5 < var1.getContainerSize(); ++var5) {
         ItemStack var6 = var1.getItem(var5);
         if (!var6.isEmpty()) {
            if (var6.is(Items.FILLED_MAP)) {
               if (!var4.isEmpty()) {
                  return ItemStack.EMPTY;
               }

               var4 = var6;
            } else {
               if (!var6.is(Items.MAP)) {
                  return ItemStack.EMPTY;
               }

               ++var3;
            }
         }
      }

      return !var4.isEmpty() && var3 >= 1 ? var4.copyWithCount(var3 + 1) : ItemStack.EMPTY;
   }

   @Override
   public boolean canCraftInDimensions(int var1, int var2) {
      return var1 >= 3 && var2 >= 3;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.MAP_CLONING;
   }
}
