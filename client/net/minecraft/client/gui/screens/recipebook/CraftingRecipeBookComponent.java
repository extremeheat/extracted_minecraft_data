package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.RecipeBook;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class CraftingRecipeBookComponent extends RecipeBookComponent<AbstractCraftingMenu> {
   private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(
      ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"),
      ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"),
      ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted")
   );
   private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");

   public CraftingRecipeBookComponent(AbstractCraftingMenu var1) {
      super(var1);
   }

   @Override
   protected boolean isCraftingSlot(Slot var1) {
      return this.menu.getResultSlot() == var1 || this.menu.getInputGridSlots().contains(var1);
   }

   @Override
   protected void setupGhostRecipeSlots(GhostSlots var1, RecipeHolder<?> var2) {
      ItemStack var3 = var2.value().getResultItem(this.minecraft.level.registryAccess());
      Slot var4 = this.menu.getResultSlot();
      var1.addResult(var3, var4);
      List var5 = this.menu.getInputGridSlots();
      PlaceRecipeHelper.placeRecipe(
         this.menu.getGridWidth(),
         this.menu.getGridHeight(),
         var2,
         var2.value().placementInfo().slotInfo(),
         (var2x, var3x, var4x, var5x) -> var2x.ifPresent(var3xx -> {
               Slot var4xx = (Slot)var5.get(var3x);
               var1.addIngredient(var3xx.possibleItems(), var4xx);
            })
      );
   }

   @Override
   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
   }

   @Override
   protected Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   @Override
   protected void selectMatchingRecipes(RecipeCollection var1, StackedItemContents var2, RecipeBook var3) {
      var1.selectMatchingRecipes(var2, this.menu.getGridWidth(), this.menu.getGridHeight(), var3);
   }
}
