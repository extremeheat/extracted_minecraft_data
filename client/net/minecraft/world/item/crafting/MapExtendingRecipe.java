package net.minecraft.world.item.crafting;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(CraftingBookCategory var1) {
      super(
         "",
         var1,
         3,
         3,
         NonNullList.of(
            Ingredient.EMPTY,
            Ingredient.of(Items.PAPER),
            Ingredient.of(Items.PAPER),
            Ingredient.of(Items.PAPER),
            Ingredient.of(Items.PAPER),
            Ingredient.of(Items.FILLED_MAP),
            Ingredient.of(Items.PAPER),
            Ingredient.of(Items.PAPER),
            Ingredient.of(Items.PAPER),
            Ingredient.of(Items.PAPER)
         ),
         new ItemStack(Items.MAP)
      );
   }

   @Override
   public boolean matches(CraftingContainer var1, Level var2) {
      if (!super.matches(var1, var2)) {
         return false;
      } else {
         ItemStack var3 = findFilledMap(var1);
         if (var3.isEmpty()) {
            return false;
         } else {
            MapItemSavedData var4 = MapItem.getSavedData(var3, var2);
            if (var4 == null) {
               return false;
            } else if (var4.isExplorationMap()) {
               return false;
            } else {
               return var4.scale < 4;
            }
         }
      }
   }

   @Override
   public ItemStack assemble(CraftingContainer var1, RegistryAccess var2) {
      ItemStack var3 = findFilledMap(var1).copyWithCount(1);
      var3.getOrCreateTag().putInt("map_scale_direction", 1);
      return var3;
   }

   private static ItemStack findFilledMap(CraftingContainer var0) {
      for(int var1 = 0; var1 < var0.getContainerSize(); ++var1) {
         ItemStack var2 = var0.getItem(var1);
         if (var2.is(Items.FILLED_MAP)) {
            return var2;
         }
      }

      return ItemStack.EMPTY;
   }

   @Override
   public boolean isSpecial() {
      return true;
   }

   @Override
   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.MAP_EXTENDING;
   }
}
