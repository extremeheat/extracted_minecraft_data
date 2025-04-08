package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;

public abstract class AbstractRecipeBookScreen<T extends RecipeBookMenu> extends AbstractContainerScreen<T> implements RecipeUpdateListener {
   private final RecipeBookComponent<?> recipeBookComponent;
   private boolean widthTooNarrow;

   public AbstractRecipeBookScreen(T var1, RecipeBookComponent<?> var2, Inventory var3, Component var4) {
      super(var1, var3, var4);
      this.recipeBookComponent = var2;
   }

   protected void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
      this.initButton();
   }

   protected abstract ScreenPosition getRecipeBookButtonPosition();

   private void initButton() {
      ScreenPosition var1 = this.getRecipeBookButtonPosition();
      this.addRenderableWidget(new ImageButton(var1.x(), var1.y(), 20, 18, RecipeBookComponent.RECIPE_BUTTON_SPRITES, (var1x) -> {
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
         ScreenPosition var2 = this.getRecipeBookButtonPosition();
         var1x.setPosition(var2.x(), var2.y());
         this.onRecipeBookButtonClick();
      }));
      this.addWidget(this.recipeBookComponent);
   }

   protected void onRecipeBookButtonClick() {
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBackground(var1, var2, var3, var4);
      } else {
         super.render(var1, var2, var3, var4);
      }

      this.recipeBookComponent.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
      this.recipeBookComponent.renderTooltip(var1, var2, var3, this.hoveredSlot);
   }

   protected void renderSlots(GuiGraphics var1) {
      super.renderSlots(var1);
      this.recipeBookComponent.renderGhostRecipe(var1, this.isBiggerResultSlot());
   }

   protected boolean isBiggerResultSlot() {
      return true;
   }

   public boolean charTyped(char var1, int var2) {
      return this.recipeBookComponent.charTyped(var1, var2) ? true : super.charTyped(var1, var2);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.recipeBookComponent.keyPressed(var1, var2, var3) ? true : super.keyPressed(var1, var2, var3);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.recipeBookComponent.mouseClicked(var1, var3, var5)) {
         this.setFocused(this.recipeBookComponent);
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   protected boolean isHovering(int var1, int var2, int var3, int var4, double var5, double var7) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(var1, var2, var3, var4, var5, var7);
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(var1, var3, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, var7) && var8;
   }

   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      super.slotClicked(var1, var2, var3, var4);
      this.recipeBookComponent.slotClicked(var1);
   }

   public void containerTick() {
      super.containerTick();
      this.recipeBookComponent.tick();
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public void fillGhostRecipe(RecipeDisplay var1) {
      this.recipeBookComponent.fillGhostRecipe(var1);
   }
}
