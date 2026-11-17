package net.minecraft.client.gui.screens.recipebook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.recipebook.PlaceRecipeHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.FurnaceRecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.item.crafting.display.ShapedCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import org.jspecify.annotations.Nullable;

public class OverlayRecipeComponent extends AbstractContainerWidget {
   private static final int BORDER_PADDING = 4;
   private static final Identifier OVERLAY_RECIPE_SPRITE = Identifier.withDefaultNamespace("recipe_book/overlay_recipe");
   private static final int MAX_ROW = 4;
   private static final int MAX_ROW_LARGE = 5;
   private static final float ITEM_RENDER_SCALE = 0.375F;
   public static final int BUTTON_SIZE = 25;
   private static final int CRAFTING_GRID_WIDTH = 3;
   private static final int CRAFTING_GRID_HEIGHT = 3;
   private static final WidgetSprites FURNACE_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/furnace_overlay"), Identifier.withDefaultNamespace("recipe_book/furnace_overlay_disabled"), Identifier.withDefaultNamespace("recipe_book/furnace_overlay_highlighted"), Identifier.withDefaultNamespace("recipe_book/furnace_overlay_disabled_highlighted"));
   private static final WidgetSprites CRAFTING_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("recipe_book/crafting_overlay"), Identifier.withDefaultNamespace("recipe_book/crafting_overlay_disabled"), Identifier.withDefaultNamespace("recipe_book/crafting_overlay_highlighted"), Identifier.withDefaultNamespace("recipe_book/crafting_overlay_disabled_highlighted"));
   private final List<OverlayRecipeButton> recipeButtons = new ArrayList();
   private RecipeCollection collection;
   private @Nullable RecipeButton opener;
   private int columns;
   private final SlotSelectTime slotSelectTime;
   private final boolean isFurnaceMenu;
   private final RecipeBookComponent.RecipePlacer recipePlacer;

   public OverlayRecipeComponent(final SlotSelectTime slotSelectTime, final boolean isFurnaceMenu, final RecipeBookComponent.RecipePlacer recipePlacer) {
      super(0, 0, 0, 0, CommonComponents.EMPTY);
      this.collection = RecipeCollection.EMPTY;
      this.columns = 4;
      this.slotSelectTime = slotSelectTime;
      this.isFurnaceMenu = isFurnaceMenu;
      this.recipePlacer = recipePlacer;
      this.allowTabNavigationInside(false);
      this.setVisible(false);
   }

   public void init(final RecipeButton opener, final ContextMap context, final boolean isFiltering, final int centerX, final int centerY) {
      this.opener = opener;
      this.collection = opener.getCollection();
      float buttonWidth = (float)opener.getWidth();
      List<RecipeDisplayEntry> craftable = this.collection.getSelectedRecipes(RecipeCollection.CraftableStatus.CRAFTABLE);
      List<RecipeDisplayEntry> unCraftable = isFiltering ? Collections.emptyList() : this.collection.getSelectedRecipes(RecipeCollection.CraftableStatus.NOT_CRAFTABLE);
      int craftables = craftable.size();
      int total = craftables + unCraftable.size();
      int maxRow = total <= 16 ? 4 : 5;
      this.columns = maxRow;
      int rows = (int)Math.ceil((double)((float)total / (float)maxRow));
      int x = opener.getX();
      int y = opener.getY();
      float rightPos = (float)(x + Math.min(total, maxRow) * 25);
      float maxLeftPos = (float)(centerX + 50);
      if (rightPos > maxLeftPos) {
         x = (int)((float)x - buttonWidth * (float)((int)((rightPos - maxLeftPos) / buttonWidth)));
      }

      float bottomPos = (float)(y + rows * 25);
      float maxBottomPos = (float)(centerY + 50);
      if (bottomPos > maxBottomPos) {
         y = (int)((float)y - buttonWidth * (float)Mth.ceil((bottomPos - maxBottomPos) / buttonWidth));
      }

      float topPos = (float)y;
      float maxTopPos = (float)(centerY - 100);
      if (topPos < maxTopPos) {
         y = (int)((float)y - buttonWidth * (float)Mth.ceil((topPos - maxTopPos) / buttonWidth));
      }

      this.setVisible(true);
      this.setRectangle(Math.min(total, maxRow) * 25 + 8, rows * 25 + 8, x, y);
      this.recipeButtons.clear();

      for(int i = 0; i < total; ++i) {
         boolean canCraft = i < craftables;
         RecipeDisplayEntry recipe = canCraft ? (RecipeDisplayEntry)craftable.get(i) : (RecipeDisplayEntry)unCraftable.get(i - craftables);
         int overlayButtonX = this.getX() + 4 + 25 * (i % maxRow);
         int overlayButtonY = this.getY() + 4 + 1 + 25 * (i / maxRow);
         ItemStack result = recipe.display().result().resolveForFirstStack(context);
         if (this.isFurnaceMenu) {
            this.recipeButtons.add(new OverlayRecipeButton(overlayButtonX, overlayButtonY, recipe.id(), result, canCraft, FURNACE_SPRITES, calculateSmeltingIngredientPositions(recipe.display(), context), this.slotSelectTime));
         } else {
            this.recipeButtons.add(new OverlayRecipeButton(overlayButtonX, overlayButtonY, recipe.id(), result, canCraft, CRAFTING_SPRITES, calculateCraftingIngredientPositions(recipe.display(), context), this.slotSelectTime));
         }
      }

   }

   public void focusFirstButton() {
      this.setFocused(this.recipeButtons.isEmpty() ? null : (GuiEventListener)this.recipeButtons.getFirst());
   }

   public @Nullable RecipeButton getOpener() {
      return this.opener;
   }

   public List<? extends GuiEventListener> children() {
      return this.recipeButtons;
   }

   public @Nullable ComponentPath nextFocusPath(final FocusNavigationEvent navigationEvent) {
      ComponentPath focusPath = super.nextFocusPath(navigationEvent);
      return focusPath == null && navigationEvent instanceof FocusNavigationEvent.ArrowNavigation && this.isFocused() ? this.getCurrentFocusPath() : focusPath;
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      return !event.isLeftMouse() ? false : super.mouseClicked(event, doubleClick);
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      graphics.blitSprite(GuiPipeline.TEXTURED, OVERLAY_RECIPE_SPRITE, this.getX(), this.getY(), this.getWidth(), this.getHeight());

      for(OverlayRecipeButton component : this.recipeButtons) {
         component.extractRenderState(graphics);
      }

   }

   public NarratableEntry.NarrationPriority narrationPriority() {
      return NarratableEntry.NarrationPriority.of(this.isFocused(), this.getHoveredButton() != null);
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
      OverlayRecipeButton narrated = (OverlayRecipeButton)getNarratedChild(output.narrationTrigger(), this.getHoveredButton(), this.getFocusedButton());
      GroupNarration groupNarration = GroupNarration.grid(this.columns, "narration.group_navigation.usage.recipe");
      if (narrated != null) {
         narrated.updateNarration(output.nest());
         groupNarration.narratePosition(output, this.recipeButtons, narrated);
      }

      if (this.isFocused()) {
         Component close = Component.translatable("narration.recipe.usage.close");
         Component usage = groupNarration.usage(this.recipeButtons.size());
         if (usage != null) {
            output.add(NarratedElementType.USAGE, usage, close);
         } else {
            output.add(NarratedElementType.USAGE, close);
         }
      }

   }

   private OverlayRecipeButton getHoveredButton() {
      for(OverlayRecipeButton button : this.recipeButtons) {
         if (button.isHovered()) {
            return button;
         }
      }

      return null;
   }

   private OverlayRecipeButton getFocusedButton() {
      GuiEventListener var2 = this.getFocused();
      OverlayRecipeButton var10000;
      if (var2 instanceof OverlayRecipeButton button) {
         var10000 = button;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   protected int contentHeight() {
      return this.height;
   }

   private static List<Pos> calculateSmeltingIngredientPositions(final RecipeDisplay recipe, final ContextMap context) {
      if (recipe instanceof FurnaceRecipeDisplay furnaceRecipe) {
         List<ItemStack> items = furnaceRecipe.ingredient().resolveForStacks(context);
         if (!items.isEmpty()) {
            return List.of(createGridPos(1, 1, items));
         }
      }

      return List.of();
   }

   private static List<Pos> calculateCraftingIngredientPositions(final RecipeDisplay recipe, final ContextMap context) {
      List<Pos> result = new ArrayList();
      Objects.requireNonNull(recipe);
      byte var4 = 0;
      //$FF: var4->value
      //0->net/minecraft/world/item/crafting/display/ShapedCraftingRecipeDisplay
      //1->net/minecraft/world/item/crafting/display/ShapelessCraftingRecipeDisplay
      switch (recipe.typeSwitch<invokedynamic>(recipe, var4)) {
         case 0:
            ShapedCraftingRecipeDisplay shaped = (ShapedCraftingRecipeDisplay)recipe;
            PlaceRecipeHelper.placeRecipe(3, 3, shaped.width(), shaped.height(), shaped.ingredients(), (ingredient, gridIndex, gridXPos, gridYPos) -> {
               List<ItemStack> items = ingredient.resolveForStacks(context);
               if (!items.isEmpty()) {
                  result.add(createGridPos(gridXPos, gridYPos, items));
               }

            });
            break;
         case 1:
            ShapelessCraftingRecipeDisplay shapeless = (ShapelessCraftingRecipeDisplay)recipe;
            List<SlotDisplay> ingredients = shapeless.ingredients();

            for(int i = 0; i < ingredients.size(); ++i) {
               List<ItemStack> items = ((SlotDisplay)ingredients.get(i)).resolveForStacks(context);
               if (!items.isEmpty()) {
                  result.add(createGridPos(i % 3, i / 3, items));
               }
            }
      }

      return result;
   }

   private static Pos createGridPos(final int gridXPos, final int gridYPos, final List<ItemStack> itemStacks) {
      return new Pos(3 + gridXPos * 7, 3 + gridYPos * 7, itemStacks);
   }

   private class OverlayRecipeButton extends AbstractButton {
      private final RecipeDisplayId recipe;
      private final ItemStack result;
      private final boolean isCraftable;
      private final WidgetSprites sprites;
      private final List<Pos> slots;
      private final SlotSelectTime slotSelectTime;

      public OverlayRecipeButton(final int x, final int y, final RecipeDisplayId recipe, final ItemStack result, final boolean isCraftable, final WidgetSprites sprites, final List<Pos> slots, final SlotSelectTime slotSelectTime) {
         Objects.requireNonNull(OverlayRecipeComponent.this);
         super(x, y, 24, 24, CommonComponents.EMPTY);
         this.slots = slots;
         this.recipe = recipe;
         this.result = result;
         this.isCraftable = isCraftable;
         this.sprites = sprites;
         this.slotSelectTime = slotSelectTime;
      }

      public void onPress(final InputWithModifiers input) {
         OverlayRecipeComponent.this.recipePlacer.placeRecipe(OverlayRecipeComponent.this.collection, this.recipe, input.hasShiftDown());
      }

      protected MutableComponent createNarrationMessage() {
         return Component.translatable("narration.recipe", this.result.getHoverName());
      }

      protected void updateWidgetNarration(final NarrationElementOutput output) {
         this.defaultButtonNarrationText(output);
      }

      protected void extractContents(final GuiGraphicsExtractor graphics) {
         graphics.blitSprite(GuiPipeline.TEXTURED, this.sprites.get(this.isCraftable, this.isHighlighted()), this.getX(), this.getY(), this.width, this.height);
         float gridPosX = (float)(this.getX() + 2);
         float gridPosY = (float)(this.getY() + 2);

         for(Pos pos : this.slots) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(gridPosX + (float)pos.x, gridPosY + (float)pos.y);
            graphics.pose().scale(0.375F, 0.375F);
            graphics.pose().translate(-8.0F, -8.0F);
            graphics.item(pos.selectIngredient(this.slotSelectTime.currentIndex()), 0, 0);
            graphics.pose().popMatrix();
         }

      }
   }

   private static record Pos(int x, int y, List<ItemStack> ingredients) {
      public Pos {
         super();
         if (ingredients.isEmpty()) {
            throw new IllegalArgumentException("Ingredient list must be non-empty");
         }
      }

      public ItemStack selectIngredient(final int currentIndex) {
         return (ItemStack)this.ingredients.get(currentIndex % this.ingredients.size());
      }
   }
}
