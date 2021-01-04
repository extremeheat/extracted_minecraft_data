package net.minecraft.world.item.crafting;

import java.util.Iterator;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class MapExtendingRecipe extends ShapedRecipe {
   public MapExtendingRecipe(ResourceLocation var1) {
      super(var1, "", 3, 3, NonNullList.of(Ingredient.EMPTY, Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.FILLED_MAP), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER), Ingredient.of(Items.PAPER)), new ItemStack(Items.MAP));
   }

   public boolean matches(CraftingContainer var1, Level var2) {
      if (!super.matches(var1, var2)) {
         return false;
      } else {
         ItemStack var3 = ItemStack.EMPTY;

         for(int var4 = 0; var4 < var1.getContainerSize() && var3.isEmpty(); ++var4) {
            ItemStack var5 = var1.getItem(var4);
            if (var5.getItem() == Items.FILLED_MAP) {
               var3 = var5;
            }
         }

         if (var3.isEmpty()) {
            return false;
         } else {
            MapItemSavedData var6 = MapItem.getOrCreateSavedData(var3, var2);
            if (var6 == null) {
               return false;
            } else if (this.isExplorationMap(var6)) {
               return false;
            } else {
               return var6.scale < 4;
            }
         }
      }
   }

   private boolean isExplorationMap(MapItemSavedData var1) {
      if (var1.decorations != null) {
         Iterator var2 = var1.decorations.values().iterator();

         while(var2.hasNext()) {
            MapDecoration var3 = (MapDecoration)var2.next();
            if (var3.getType() == MapDecoration.Type.MANSION || var3.getType() == MapDecoration.Type.MONUMENT) {
               return true;
            }
         }
      }

      return false;
   }

   public ItemStack assemble(CraftingContainer var1) {
      ItemStack var2 = ItemStack.EMPTY;

      for(int var3 = 0; var3 < var1.getContainerSize() && var2.isEmpty(); ++var3) {
         ItemStack var4 = var1.getItem(var3);
         if (var4.getItem() == Items.FILLED_MAP) {
            var2 = var4;
         }
      }

      var2 = var2.copy();
      var2.setCount(1);
      var2.getOrCreateTag().putInt("map_scale_direction", 1);
      return var2;
   }

   public boolean isSpecial() {
      return true;
   }

   public RecipeSerializer<?> getSerializer() {
      return RecipeSerializer.MAP_EXTENDING;
   }
}
