package net.minecraft.world.inventory;

import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;

public abstract class RecipeBookMenu<I extends RecipeInput, R extends Recipe<I>> extends AbstractContainerMenu {
   public RecipeBookMenu(MenuType<?> var1, int var2) {
      super(var1, var2);
   }

   public void handlePlacement(boolean var1, RecipeHolder<?> var2, ServerPlayer var3) {
      RecipeHolder var4 = var2;
      this.beginPlacingRecipe();

      try {
         (new ServerPlaceRecipe(this)).recipeClicked(var3, var4, var1);
      } finally {
         this.finishPlacingRecipe(var2);
      }

   }

   protected void beginPlacingRecipe() {
   }

   protected void finishPlacingRecipe(RecipeHolder<R> var1) {
   }

   public abstract void fillCraftSlotsStackedContents(StackedContents var1);

   public abstract void clearCraftingContent();

   public abstract boolean recipeMatches(RecipeHolder<R> var1);

   public abstract int getResultSlotIndex();

   public abstract int getGridWidth();

   public abstract int getGridHeight();

   public abstract int getSize();

   public abstract RecipeBookType getRecipeBookType();

   public abstract boolean shouldMoveToInventory(int var1);
}
