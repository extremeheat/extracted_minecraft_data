package net.minecraft.recipebook;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;

public class ServerPlaceSmeltingRecipe extends ServerPlaceRecipe {
   private boolean recipeMatchesPlaced;

   public ServerPlaceSmeltingRecipe(RecipeBookMenu var1) {
      super(var1);
   }

   protected void handleRecipeClicked(Recipe var1, boolean var2) {
      this.recipeMatchesPlaced = this.menu.recipeMatches(var1);
      int var3 = this.stackedContents.getBiggestCraftableStack(var1, (IntList)null);
      if (this.recipeMatchesPlaced) {
         ItemStack var4 = this.menu.getSlot(0).getItem();
         if (var4.isEmpty() || var3 <= var4.getCount()) {
            return;
         }
      }

      int var6 = this.getStackSize(var2, var3, this.recipeMatchesPlaced);
      IntArrayList var5 = new IntArrayList();
      if (this.stackedContents.canCraft(var1, var5, var6)) {
         if (!this.recipeMatchesPlaced) {
            this.moveItemToInventory(this.menu.getResultSlotIndex());
            this.moveItemToInventory(0);
         }

         this.placeRecipe(var6, var5);
      }
   }

   protected void clearGrid() {
      this.moveItemToInventory(this.menu.getResultSlotIndex());
      super.clearGrid();
   }

   protected void placeRecipe(int var1, IntList var2) {
      IntListIterator var3 = var2.iterator();
      Slot var4 = this.menu.getSlot(0);
      ItemStack var5 = StackedContents.fromStackingIndex((Integer)var3.next());
      if (!var5.isEmpty()) {
         int var6 = Math.min(var5.getMaxStackSize(), var1);
         if (this.recipeMatchesPlaced) {
            var6 -= var4.getItem().getCount();
         }

         for(int var7 = 0; var7 < var6; ++var7) {
            this.moveItemToGrid(var4, var5);
         }

      }
   }
}
