package net.minecraft.client.gui.screens.inventory;

import java.util.Objects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jspecify.annotations.Nullable;

public abstract class AbstractRecipeBookScreen<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
   private final RecipeBookComponent<?> recipeBookComponent;
   private boolean widthTooNarrow;

   public AbstractRecipeBookScreen(final T menu, final RecipeBookComponent<?> recipeBookComponent, final Inventory inventory, final Component title) {
      super(menu, inventory, title);
      this.recipeBookComponent = recipeBookComponent;
   }

   protected void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
      this.updateScreenPositions();
      this.initButton();
   }

   protected abstract ScreenPosition getRecipeBookButtonPosition();

   private void initButton() {
      ScreenPosition buttonPos = this.getRecipeBookButtonPosition();
      this.addRenderableWidget(new RecipeBookToggleButton(buttonPos.x(), buttonPos.y(), (button) -> {
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         this.updateScreenPositions();
         ScreenPosition updatedButtonPos = this.getRecipeBookButtonPosition();
         button.setPosition(updatedButtonPos.x(), updatedButtonPos.y());
         this.onRecipeBookButtonClick();
      }));
      this.addWidget(this.recipeBookComponent);
   }

   protected void onRecipeBookButtonClick() {
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics) {
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.extractBackground(graphics);
      } else {
         super.extractContents(graphics);
      }

      graphics.nextStratum();
      this.recipeBookComponent.extractRenderState(graphics);
      graphics.nextStratum();
      this.extractCarriedItem(graphics);
      SlotWidget hoveredSlotWidget = this.getHoveredSlot();
      this.recipeBookComponent.extractTooltip(graphics, hoveredSlotWidget == null ? null : hoveredSlotWidget.slot());
   }

   protected void extractSlots(final GuiGraphicsExtractor graphics) {
      super.extractSlots(graphics);
      this.recipeBookComponent.extractGhostRecipe(graphics, this.leftPos, this.topPos, this.isBiggerResultSlot());
   }

   protected boolean isBiggerResultSlot() {
      return true;
   }

   public boolean charTyped(final CharacterEvent event) {
      return this.recipeBookComponent.charTyped(event) ? true : super.charTyped(event);
   }

   public boolean keyPressed(final KeyEvent event) {
      return this.recipeBookComponent.keyPressed(event) ? true : super.keyPressed(event);
   }

   public boolean isInputCaptured() {
      return super.isInputCaptured() || this.recipeBookComponent.capturesInput();
   }

   public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
      if (this.recipeBookComponent.mouseClicked(event, doubleClick)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(event, doubleClick);
      }
   }

   public boolean mouseDragged(final MouseButtonEvent event, final double dx, final double dy) {
      return this.recipeBookComponent.mouseDragged(event, dx, dy) ? true : super.mouseDragged(event, dx, dy);
   }

   protected @Nullable SlotWidget getHoveredSlot() {
      return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? null : super.getHoveredSlot();
   }

   protected boolean hasClickedOutside(final double mx, final double my) {
      boolean clickedOutside = super.hasClickedOutside(mx, my);
      return this.recipeBookComponent.hasClickedOutside(mx, my, this.leftPos, this.topPos, this.imageWidth, this.imageHeight) && clickedOutside;
   }

   protected void slotClicked(final @Nullable SlotWidget slotWidget, final int slotId, final int buttonNum, final ContainerInput containerInput) {
      super.slotClicked(slotWidget, slotId, buttonNum, containerInput);
      this.recipeBookComponent.slotClicked(slotWidget);
   }

   public void containerTick() {
      super.containerTick();
      this.recipeBookComponent.tick();
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public void fillGhostRecipe(final RecipeDisplay display) {
      this.recipeBookComponent.fillGhostRecipe(display);
   }

   private class RecipeBookToggleButton extends ImageButton {
      private static final Tooltip TOOLTIP = Tooltip.create(Component.translatable("gui.recipebook.tooltip"));
      private static final Component NARRATION = Component.translatable("narrator.button.recipe_book");
      private static final Component NARRATION_STATE_OPEN = Component.translatable("narration.button.state.open");
      private static final Component NARRATION_STATE_CLOSED = Component.translatable("narration.button.state.closed");
      private static final Component NARRATION_USAGE_FOCUSED_OPEN = Component.translatable("narration.button.usage.focused.open");
      private static final Component NARRATION_USAGE_FOCUSED_CLOSE = Component.translatable("narration.button.usage.focused.close");
      private static final Component NARRATION_USAGE_HOVERED_OPEN = Component.translatable("narration.button.usage.hovered.open");
      private static final Component NARRATION_USAGE_HOVERED_CLOSE = Component.translatable("narration.button.usage.hovered.close");

      public RecipeBookToggleButton(final int x, final int y, final Button.OnPress onPress) {
         Objects.requireNonNull(AbstractRecipeBookScreen.this);
         super(x, y, 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, onPress, NARRATION);
         this.setTooltip(TOOLTIP);
      }

      protected MutableComponent createNarrationMessage() {
         return CommonComponents.joinForNarration(super.createNarrationMessage(), AbstractRecipeBookScreen.this.recipeBookComponent.isVisible() ? NARRATION_STATE_OPEN : NARRATION_STATE_CLOSED);
      }

      public void updateWidgetNarration(final NarrationElementOutput output) {
         output.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
         if (this.active) {
            boolean visible = AbstractRecipeBookScreen.this.recipeBookComponent.isVisible();
            if (this.isFocused()) {
               output.add(NarratedElementType.USAGE, visible ? NARRATION_USAGE_FOCUSED_CLOSE : NARRATION_USAGE_FOCUSED_OPEN);
            } else {
               output.add(NarratedElementType.USAGE, visible ? NARRATION_USAGE_HOVERED_CLOSE : NARRATION_USAGE_HOVERED_OPEN);
            }
         }

      }
   }
}
