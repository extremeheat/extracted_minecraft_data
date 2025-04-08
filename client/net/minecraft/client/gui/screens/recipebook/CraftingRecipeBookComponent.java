package net.minecraft.client.gui.screens.recipebook;

import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.ResourceLocation;
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
   private static final WidgetSprites FILTER_BUTTON_SPRITES = new WidgetSprites(ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled"), ResourceLocation.withDefaultNamespace("recipe_book/filter_enabled_highlighted"), ResourceLocation.withDefaultNamespace("recipe_book/filter_disabled_highlighted"));
   private static final Component ONLY_CRAFTABLES_TOOLTIP = Component.translatable("gui.recipebook.toggleRecipes.craftable");
   private static final List<RecipeBookComponent.TabInfo> TABS;

   public CraftingRecipeBookComponent(AbstractCraftingMenu var1) {
      super(var1, TABS);
   }

   protected boolean isCraftingSlot(Slot var1) {
      return ((AbstractCraftingMenu)this.menu).getResultSlot() == var1 || ((AbstractCraftingMenu)this.menu).getInputGridSlots().contains(var1);
   }

   private boolean canDisplay(RecipeDisplay var1) {
      int var2 = ((AbstractCraftingMenu)this.menu).getGridWidth();
      int var3 = ((AbstractCraftingMenu)this.menu).getGridHeight();
      Objects.requireNonNull(var1);
      byte var5 = 0;
      boolean var10000;
      //$FF: var5->value
      //0->net/minecraft/world/item/crafting/display/ShapedCraftingRecipeDisplay
      //1->net/minecraft/world/item/crafting/display/ShapelessCraftingRecipeDisplay
      switch (var1.typeSwitch<invokedynamic>(var1, var5)) {
         case 0:
            ShapedCraftingRecipeDisplay var6 = (ShapedCraftingRecipeDisplay)var1;
            var10000 = var2 >= var6.width() && var3 >= var6.height();
            break;
         case 1:
            ShapelessCraftingRecipeDisplay var7 = (ShapelessCraftingRecipeDisplay)var1;
            var10000 = var2 * var3 >= var7.ingredients().size();
            break;
         default:
            var10000 = false;
      }

      return var10000;
   }

   protected void fillGhostRecipe(GhostSlots var1, RecipeDisplay var2, ContextMap var3) {
      var1.setResult(((AbstractCraftingMenu)this.menu).getResultSlot(), var3, var2.result());
      Objects.requireNonNull(var2);
      byte var5 = 0;
      //$FF: var5->value
      //0->net/minecraft/world/item/crafting/display/ShapedCraftingRecipeDisplay
      //1->net/minecraft/world/item/crafting/display/ShapelessCraftingRecipeDisplay
      switch (var2.typeSwitch<invokedynamic>(var2, var5)) {
         case 0:
            ShapedCraftingRecipeDisplay var6 = (ShapedCraftingRecipeDisplay)var2;
            List var11 = (this.menu).getInputGridSlots();
            PlaceRecipeHelper.placeRecipe(((AbstractCraftingMenu)this.menu).getGridWidth(), ((AbstractCraftingMenu)this.menu).getGridHeight(), var6.width(), var6.height(), var6.ingredients(), (var3x, var4, var5x, var6x) -> {
               Slot var7 = (Slot)var11.get(var4);
               var1.setInput(var7, var3, var3x);
            });
            break;
         case 1:
            ShapelessCraftingRecipeDisplay var7 = (ShapelessCraftingRecipeDisplay)var2;
            List var8 = (this.menu).getInputGridSlots();
            int var9 = Math.min(var7.ingredients().size(), var8.size());

            for(int var10 = 0; var10 < var9; ++var10) {
               var1.setInput((Slot)var8.get(var10), var3, (SlotDisplay)var7.ingredients().get(var10));
            }
      }

   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(FILTER_BUTTON_SPRITES);
   }

   protected Component getRecipeFilterName() {
      return ONLY_CRAFTABLES_TOOLTIP;
   }

   protected void selectMatchingRecipes(RecipeCollection var1, StackedItemContents var2) {
      var1.selectRecipes(var2, this::canDisplay);
   }

   static {
      TABS = List.of(new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.CRAFTING), new RecipeBookComponent.TabInfo(Items.IRON_AXE, Items.GOLDEN_SWORD, RecipeBookCategories.CRAFTING_EQUIPMENT), new RecipeBookComponent.TabInfo(Items.BRICKS, RecipeBookCategories.CRAFTING_BUILDING_BLOCKS), new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.APPLE, RecipeBookCategories.CRAFTING_MISC), new RecipeBookComponent.TabInfo(Items.REDSTONE, RecipeBookCategories.CRAFTING_REDSTONE));
   }
}
