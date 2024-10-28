package net.minecraft.world.item.crafting;

import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.component.MapPostProcessing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(CraftingBookCategory var1) {
      super("", var1, ShapedRecipePattern.of(Map.of('#', Ingredient.of(Items.PAPER), 'x', Ingredient.of(Items.FILLED_MAP)), "###", "#x#", "###"), new ItemStack(Items.MAP));
   }

   public boolean matches(CraftingInput var1, Level var2) {
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

   public ItemStack assemble(CraftingInput var1, HolderLookup.Provider var2) {
      ItemStack var3 = findFilledMap(var1).copyWithCount(1);
      var3.set(DataComponents.MAP_POST_PROCESSING, MapPostProcessing.SCALE);
      return var3;
   }

   private static ItemStack findFilledMap(CraftingInput var0) {
      for(int var1 = 0; var1 < var0.size(); ++var1) {
         ItemStack var2 = var0.getItem(var1);
         if (var2.is(Items.FILLED_MAP)) {
            return var2;
         }
      }

      return ItemStack.EMPTY;
   }

   public boolean isSpecial() {
      return true;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.MAP_EXTENDING;
   }
}
