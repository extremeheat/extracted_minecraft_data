package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.components.inventory.IconRadioButton;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.narration.GroupNarration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SelectableRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jspecify.annotations.Nullable;

public class StonecutterScreen extends AbstractContainerScreen<StonecutterMenu> {
   private static final Identifier BG_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/stonecutter.png");
   private static final WidgetSprites RECIPE_BUTTON_SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("container/stonecutter/recipe_selected"), Identifier.withDefaultNamespace("container/stonecutter/recipe"), Identifier.withDefaultNamespace("container/stonecutter/recipe_selected"), Identifier.withDefaultNamespace("container/stonecutter/recipe_highlighted"));
   private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller");
   private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/stonecutter/scroller_disabled");
   private static final int RECIPES_COLUMNS = 4;
   private static final int RECIPES_ROWS = 3;
   private static final int RECIPES_IMAGE_SIZE_WIDTH = 16;
   private static final int RECIPES_IMAGE_SIZE_HEIGHT = 18;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   private static final int SCROLL_AREA_WIDTH = 64;
   private static final int SCROLL_AREA_HEIGHT = 54;
   private static final int RECIPES_START_X = 52;
   private static final int RECIPES_START_Y = 15;
   private ItemStack inputStack;
   private @Nullable ScrollableLayout recipeArea;

   public StonecutterScreen(final StonecutterMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title);
      this.inputStack = ItemStack.EMPTY;
      menu.registerUpdateListener(this::containerChanged);
      --this.titleLabelY;
   }

   protected void init() {
      super.init();
      this.recipeArea = this.createScrollableLayout(new FrameLayout());
   }

   protected void repositionElements() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.updateSlotWidgets();
      if (this.recipeArea != null) {
         this.recipeArea.setPosition(this.leftPos + 52, this.topPos + 15);
         this.recipeArea.arrangeElements();
      }

   }

   private void initButtons() {
      if (this.recipeArea != null) {
         this.recipeArea.visitWidgets((x$0) -> this.removeWidget(x$0));
      }

      GridLayout layout = new GridLayout();
      GridLayout.RowHelper rowHelper = layout.createRowHelper(4);
      ContextMap context = SlotDisplayContext.fromLevel(this.minecraft.level);
      SelectableRecipe.SingleInputSet<StonecutterRecipe> visibleRecipes = ((StonecutterMenu)this.menu).getVisibleRecipes();

      for(int index = 0; index < visibleRecipes.size(); ++index) {
         ItemStack itemStack = ((SelectableRecipe.SingleInputEntry)visibleRecipes.entries().get(index)).recipe().optionDisplay().resolveForFirstStack(context);
         IconRadioButton recipeButton = new IconRadioButton(16, 18, itemStack.getHoverName(), RECIPE_BUTTON_SPRITES, new IconRadioButton.ItemIcon(itemStack), SoundEvents.UI_STONECUTTER_SELECT_RECIPE, (button) -> {
            if (((StonecutterMenu)this.menu).clickMenuButton(this.minecraft.player, index)) {
               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, index);
            }

         }, (defaultNarration) -> Component.translatable("narration.recipe", defaultNarration.get()), () -> Tooltip.createForItem(getTooltipFromItem(this.minecraft, itemStack), itemStack), () -> ((StonecutterMenu)this.menu).getSelectedRecipeIndex() == index);
         rowHelper.addChild(recipeButton);
      }

      this.recipeArea = this.createScrollableLayout(layout);
   }

   public void extractBackground(final GuiGraphicsExtractor graphics) {
      super.extractBackground(graphics);
      graphics.blit(GuiPipeline.TEXTURED, BG_LOCATION, this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }

   private ScrollableLayout createScrollableLayout(final Layout layout) {
      ScrollableLayout scrollableLayout = new ScrollableLayout(this.minecraft, layout, 54, new AbstractScrollArea.ScrollbarSettings(SCROLLER_SPRITE, SCROLLER_DISABLED_SPRITE, (Identifier)null, 12, 15, 18, false), 3, ScrollableLayout.ReserveStrategy.RIGHT, GroupNarration.grid(4, "narration.group_navigation.usage.recipe"));
      scrollableLayout.setPosition(this.leftPos + 52, this.topPos + 15);
      scrollableLayout.setMinWidth(64);
      scrollableLayout.setMinHeight(54);
      scrollableLayout.arrangeElements();
      scrollableLayout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      scrollableLayout.setTabGroup(7);
      scrollableLayout.allowTabNavigationInside(false);
      return scrollableLayout;
   }

   private void containerChanged() {
      ItemStack newInputStack = ((StonecutterMenu)this.menu).getInputItem();
      if (!ItemStack.isSameItemSameComponents(newInputStack, this.inputStack)) {
         this.initButtons();
      }

      this.inputStack = newInputStack.copy();
   }
}
