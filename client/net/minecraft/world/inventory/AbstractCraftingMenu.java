package net.minecraft.world.inventory;

import java.util.List;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;

public abstract class AbstractCraftingMenu extends RecipeBookMenu {
   private final int width;
   private final int height;
   protected final CraftingContainer craftSlots;
   protected final ResultContainer resultSlots = new ResultContainer();

   public AbstractCraftingMenu(MenuType<?> var1, int var2, int var3, int var4) {
      super(var1, var2);
      this.width = var3;
      this.height = var4;
      this.craftSlots = new TransientCraftingContainer(this, var3, var4);
   }

   protected Slot addResultSlot(Player var1, int var2, int var3) {
      return this.addSlot(new ResultSlot(var1, this.craftSlots, this.resultSlots, 0, var2, var3));
   }

   protected void addCraftingGridSlots(int var1, int var2) {
      for(int var3 = 0; var3 < this.width; ++var3) {
         for(int var4 = 0; var4 < this.height; ++var4) {
            this.addSlot(new Slot(this.craftSlots, var4 + var3 * this.width, var1 + var4 * 18, var2 + var3 * 18));
         }
      }

   }

   public RecipeBookMenu.PostPlaceAction handlePlacement(boolean var1, boolean var2, RecipeHolder<?> var3, ServerLevel var4, Inventory var5) {
      RecipeHolder var6 = var3;
      this.beginPlacingRecipe();

      RecipeBookMenu.PostPlaceAction var8;
      try {
         List var7 = this.getInputGridSlots();
         var8 = ServerPlaceRecipe.placeRecipe(new ServerPlaceRecipe.CraftingMenuAccess<CraftingRecipe>() {
            public void fillCraftSlotsStackedContents(StackedItemContents var1) {
               AbstractCraftingMenu.this.fillCraftSlotsStackedContents(var1);
            }

            public void clearCraftingContent() {
               AbstractCraftingMenu.this.resultSlots.clearContent();
               AbstractCraftingMenu.this.craftSlots.clearContent();
            }

            public boolean recipeMatches(RecipeHolder<CraftingRecipe> var1) {
               return ((CraftingRecipe)var1.value()).matches(AbstractCraftingMenu.this.craftSlots.asCraftInput(), AbstractCraftingMenu.this.owner().level());
            }
         }, this.width, this.height, var7, var7, var5, var6, var1, var2);
      } finally {
         this.finishPlacingRecipe(var4, var3);
      }

      return var8;
   }

   protected void beginPlacingRecipe() {
   }

   protected void finishPlacingRecipe(ServerLevel var1, RecipeHolder<CraftingRecipe> var2) {
   }

   public abstract Slot getResultSlot();

   public abstract List<Slot> getInputGridSlots();

   public int getGridWidth() {
      return this.width;
   }

   public int getGridHeight() {
      return this.height;
   }

   protected abstract Player owner();

   public void fillCraftSlotsStackedContents(StackedItemContents var1) {
      this.craftSlots.fillStackedContents(var1);
   }
}
