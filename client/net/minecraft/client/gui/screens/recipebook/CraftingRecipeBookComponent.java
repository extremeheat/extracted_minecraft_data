package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.world.inventory.AbstractCraftingMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class CraftingRecipeBookComponent extends RecipeBookComponent<AbstractCraftingMenu> {
   private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/filter_enabled"), Identifier.withDefaultNamespace("recipe_book/filter_disabled"), Identifier.withDefaultNamespace("recipe_book/filter_enabled_highlighted"), Identifier.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));
   private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
   private static final List<RecipeBookComponent.TabInfo> TABS;

   public CraftingRecipeBookComponent(final AbstractCraftingMenu menu) {
      super(menu, TABS);
   }

   protected boolean isCraftingSlot(final Slot slot) {
      return ((AbstractCraftingMenu)this.menu).getResultSlot() == slot || ((AbstractCraftingMenu)this.menu).getInputGridSlots().contains(slot);
   }

   private boolean canDisplay(final RecipeDisplay display) {
      int gridWidth = ((AbstractCraftingMenu)this.menu).getGridWidth();
      int gridHeight = ((AbstractCraftingMenu)this.menu).getGridHeight();
      Objects.requireNonNull(display);
      byte var5 = 0;
      boolean var10000;
      //$FF: var5->value
      //0->net/minecraft/world/item/crafting/display/ShapedCraftingRecipeDisplay
      //1->net/minecraft/world/item/crafting/display/ShapelessCraftingRecipeDisplay
      switch (display.typeSwitch<invokedynamic>(display, var5)) {
         case 0:
            ShapedCraftingRecipeDisplay shaped = (ShapedCraftingRecipeDisplay)display;
            var10000 = gridWidth >= shaped.width() && gridHeight >= shaped.height();
            break;
         case 1:
            ShapelessCraftingRecipeDisplay shapeless = (ShapelessCraftingRecipeDisplay)display;
            var10000 = gridWidth * gridHeight >= shapeless.ingredients().size();
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   protected void fillGhostRecipe(final GhostSlots ghostSlots, final RecipeDisplay recipe, final ContextMap context) {
      ghostSlots.setResult(((AbstractCraftingMenu)this.menu).getResultSlot(), context, recipe.result());
      Objects.requireNonNull(recipe);
      byte var5 = 0;
      //$FF: var5->value
      //0->net/minecraft/world/item/crafting/display/ShapedCraftingRecipeDisplay
      //1->net/minecraft/world/item/crafting/display/ShapelessCraftingRecipeDisplay
      switch (recipe.typeSwitch<invokedynamic>(recipe, var5)) {
         case 0:
            ShapedCraftingRecipeDisplay shaped = (ShapedCraftingRecipeDisplay)recipe;
            List<Slot> inputSlots = ((AbstractCraftingMenu)this.menu).getInputGridSlots();
            PlaceRecipeHelper.placeRecipe(((AbstractCraftingMenu)this.menu).getGridWidth(), ((AbstractCraftingMenu)this.menu).getGridHeight(), shaped.width(), shaped.height(), shaped.ingredients(), (ingredient, gridIndex, gridXPos, gridYPos) -> {
               Slot slot = (Slot)inputSlots.get(gridIndex);
               ghostSlots.setInput(slot, context, ingredient);
            });
            break;
         case 1:
            ShapelessCraftingRecipeDisplay shapeless = (ShapelessCraftingRecipeDisplay)recipe;
            List<Slot> inputSlots = ((AbstractCraftingMenu)this.menu).getInputGridSlots();
            int slotCount = Math.min(shapeless.ingredients().size(), inputSlots.size());

            for(int i = 0; i < slotCount; ++i) {
               ghostSlots.setInput((Slot)inputSlots.get(i), context, (SlotDisplay)shapeless.ingredients().get(i));
            }
      }

   }

   protected WidgetSprites getFilterButtonTextures() {
      return FILTER_BUTTON_SPRITES;
   }

   protected Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   protected void selectMatchingRecipes(final RecipeCollection collection, final StackedItemContents stackedContents) {
      collection.selectRecipes(stackedContents, this::canDisplay);
   }

   static {
      TABS = List.of(new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.CRAFTING), new RecipeBookComponent.TabInfo(Items.IRON_AXE, Items.GOLDEN_SWORD, RecipeBookCategories.CRAFTING_EQUIPMENT), new RecipeBookComponent.TabInfo(Items.BRICKS, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS), new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.APPLE, RecipeBookCategories.CRAFTING_MISC), new RecipeBookComponent.TabInfo(Items.REDSTONE, RecipeBookCategories.CRAFTING_REDSTONE));
   }
}
