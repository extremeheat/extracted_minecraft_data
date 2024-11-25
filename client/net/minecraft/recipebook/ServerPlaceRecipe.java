package net.minecraft.recipebook;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

public class ServerPlaceRecipe<R extends Recipe<?>> {
   private static final int ITEM_NOT_FOUND = -1;
   private final Inventory inventory;
   private final CraftingMenuAccess<R> menu;
   private final boolean useMaxItems;
   private final int gridWidth;
   private final int gridHeight;
   private final List<Slot> inputGridSlots;
   private final List<Slot> slotsToClear;

   public static <I extends RecipeInput, R extends Recipe<I>> RecipeBookMenu.PostPlaceAction placeRecipe(CraftingMenuAccess<R> var0, int var1, int var2, List<Slot> var3, List<Slot> var4, Inventory var5, RecipeHolder<R> var6, boolean var7, boolean var8) {
      ServerPlaceRecipe var9 = new ServerPlaceRecipe(var0, var5, var7, var1, var2, var3, var4);
      if (!var8 && !var9.testClearGrid()) {
         return RecipeBookMenu.PostPlaceAction.NOTHING;
      } else {
         StackedItemContents var10 = new StackedItemContents();
         var5.fillStackedContents(var10);
         var0.fillCraftSlotsStackedContents(var10);
         return var9.tryPlaceRecipe(var6, var10);
      }
   }

   private ServerPlaceRecipe(CraftingMenuAccess<R> var1, Inventory var2, boolean var3, int var4, int var5, List<Slot> var6, List<Slot> var7) {
      super();
      this.menu = var1;
      this.inventory = var2;
      this.useMaxItems = var3;
      this.gridWidth = var4;
      this.gridHeight = var5;
      this.inputGridSlots = var6;
      this.slotsToClear = var7;
   }

   private RecipeBookMenu.PostPlaceAction tryPlaceRecipe(RecipeHolder<R> var1, StackedItemContents var2) {
      if (var2.canCraft(var1.value(), (StackedContents.Output)null)) {
         this.placeRecipe(var1, var2);
         this.inventory.setChanged();
         return RecipeBookMenu.PostPlaceAction.NOTHING;
      } else {
         this.clearGrid();
         this.inventory.setChanged();
         return RecipeBookMenu.PostPlaceAction.PLACE_GHOST_RECIPE;
      }
   }

   private void clearGrid() {
      for(Slot var2 : this.slotsToClear) {
         ItemStack var3 = var2.getItem().copy();
         this.inventory.placeItemBackInInventory(var3, false);
         var2.set(var3);
      }

      this.menu.clearCraftingContent();
   }

   private void placeRecipe(RecipeHolder<R> var1, StackedItemContents var2) {
      boolean var3 = this.menu.recipeMatches(var1);
      int var4 = var2.getBiggestCraftableStack(var1.value(), (StackedContents.Output)null);
      if (var3) {
         for(Slot var6 : this.inputGridSlots) {
            ItemStack var7 = var6.getItem();
            if (!var7.isEmpty() && Math.min(var4, var7.getMaxStackSize()) < var7.getCount() + 1) {
               return;
            }
         }
      }

      int var8 = this.calculateAmountToCraft(var4, var3);
      ArrayList var9 = new ArrayList();
      Recipe var10001 = var1.value();
      Objects.requireNonNull(var9);
      if (var2.canCraft(var10001, var8, var9::add)) {
         int var10 = clampToMaxStackSize(var8, var9);
         if (var10 != var8) {
            var9.clear();
            var10001 = var1.value();
            Objects.requireNonNull(var9);
            if (!var2.canCraft(var10001, var10, var9::add)) {
               return;
            }
         }

         this.clearGrid();
         PlaceRecipeHelper.placeRecipe(this.gridWidth, this.gridHeight, var1.value(), var1.value().placementInfo().slotsToIngredientIndex(), (var3x, var4x, var5, var6x) -> {
            if (var3x != -1) {
               Slot var7 = (Slot)this.inputGridSlots.get(var4x);
               Holder var8 = (Holder)var9.get(var3x);
               int var9x = var10;

               while(var9x > 0) {
                  var9x = this.moveItemToGrid(var7, var8, var9x);
                  if (var9x == -1) {
                     return;
                  }
               }

            }
         });
      }
   }

   private static int clampToMaxStackSize(int var0, List<Holder<Item>> var1) {
      for(Holder var3 : var1) {
         var0 = Math.min(var0, ((Item)var3.value()).getDefaultMaxStackSize());
      }

      return var0;
   }

   private int calculateAmountToCraft(int var1, boolean var2) {
      if (this.useMaxItems) {
         return var1;
      } else if (var2) {
         int var3 = 2147483647;

         for(Slot var5 : this.inputGridSlots) {
            ItemStack var6 = var5.getItem();
            if (!var6.isEmpty() && var3 > var6.getCount()) {
               var3 = var6.getCount();
            }
         }

         if (var3 != 2147483647) {
            ++var3;
         }

         return var3;
      } else {
         return 1;
      }
   }

   private int moveItemToGrid(Slot var1, Holder<Item> var2, int var3) {
      ItemStack var4 = var1.getItem();
      int var5 = this.inventory.findSlotMatchingCraftingIngredient(var2, var4);
      if (var5 == -1) {
         return -1;
      } else {
         ItemStack var6 = this.inventory.getItem(var5);
         ItemStack var7;
         if (var3 < var6.getCount()) {
            var7 = this.inventory.removeItem(var5, var3);
         } else {
            var7 = this.inventory.removeItemNoUpdate(var5);
         }

         int var8 = var7.getCount();
         if (var4.isEmpty()) {
            var1.set(var7);
         } else {
            var4.grow(var8);
         }

         return var3 - var8;
      }
   }

   private boolean testClearGrid() {
      ArrayList var1 = Lists.newArrayList();
      int var2 = this.getAmountOfFreeSlotsInInventory();

      for(Slot var4 : this.inputGridSlots) {
         ItemStack var5 = var4.getItem().copy();
         if (!var5.isEmpty()) {
            int var6 = this.inventory.getSlotWithRemainingSpace(var5);
            if (var6 == -1 && var1.size() <= var2) {
               for(ItemStack var8 : var1) {
                  if (ItemStack.isSameItem(var8, var5) && var8.getCount() != var8.getMaxStackSize() && var8.getCount() + var5.getCount() <= var8.getMaxStackSize()) {
                     var8.grow(var5.getCount());
                     var5.setCount(0);
                     break;
                  }
               }

               if (!var5.isEmpty()) {
                  if (var1.size() >= var2) {
                     return false;
                  }

                  var1.add(var5);
               }
            } else if (var6 == -1) {
               return false;
            }
         }
      }

      return true;
   }

   private int getAmountOfFreeSlotsInInventory() {
      int var1 = 0;

      for(ItemStack var3 : this.inventory.items) {
         if (var3.isEmpty()) {
            ++var1;
         }
      }

      return var1;
   }

   public interface CraftingMenuAccess<T extends Recipe<?>> {
      void fillCraftSlotsStackedContents(StackedItemContents var1);

      void clearCraftingContent();

      boolean recipeMatches(RecipeHolder<T> var1);
   }
}
