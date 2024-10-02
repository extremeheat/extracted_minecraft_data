package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class FurnaceRecipeBookComponent extends RecipeBookComponent<AbstractFurnaceMenu> {
   private static final WidgetSprites FILTER_SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_enabled_highlighted"),
      ResourceLocation.withDefaultNamespace("recipe_book/furnace_filter_disabled_highlighted")
   );
   private final Component recipeFilterName;

   public FurnaceRecipeBookComponent(AbstractFurnaceMenu var1, Component var2, List<RecipeBookComponent.TabInfo> var3) {
      super(var1, var3);
      this.recipeFilterName = var2;
   }

   @Override
   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_SPRITES);
   }

   @Override
   protected boolean isCraftingSlot(Slot var1) {
      return switch (var1.index) {
         case 0, 1, 2 -> true;
         default -> false;
      };
   }

   @Override
   protected void fillGhostRecipe(GhostSlots var1, RecipeDisplay var2, SlotDisplay.ResolutionContext var3) {
      var1.setResult(this.menu.getResultSlot(), var3, var2.result());
      if (var2 instanceof FurnaceRecipeDisplay var4) {
         var1.setInput(this.menu.slots.get(0), var3, var4.ingredient());
         Slot var5 = this.menu.slots.get(1);
         if (var5.getItem().isEmpty()) {
            var1.setInput(var5, var3, var4.fuel());
         }
      }
   }

   @Override
   protected Component getRecipeFilterName() {
      return this.recipeFilterName;
   }

   @Override
   protected void selectMatchingRecipes(RecipeCollection var1, StackedItemContents var2) {
      var1.selectRecipes(var2, var0 -> var0 instanceof FurnaceRecipeDisplay);
   }
}
