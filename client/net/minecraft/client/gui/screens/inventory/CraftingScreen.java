package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.Slot;

public class CraftingScreen extends AbstractContainerScreen<CraftingMenu> implements RecipeUpdateListener {
   private static final ResourceLocation CRAFTING_TABLE_LOCATION = new ResourceLocation("textures/gui/container/crafting_table.png");
   private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
   private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
   private boolean widthTooNarrow;

   public CraftingScreen(CraftingMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   protected void init() {
      super.init();
      this.widthTooNarrow = this.width < 379;
      this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, (RecipeBookMenu)this.menu);
      this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
      this.children.add(this.recipeBookComponent);
      this.setInitialFocus(this.recipeBookComponent);
      this.addButton(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (var1) -> {
         this.recipeBookComponent.initVisuals(this.widthTooNarrow);
         this.recipeBookComponent.toggleVisibility();
         this.leftPos = this.recipeBookComponent.updateScreenPosition(this.widthTooNarrow, this.width, this.imageWidth);
         ((ImageButton)var1).setPosition(this.leftPos + 5, this.height / 2 - 49);
      }));
   }

   public void tick() {
      super.tick();
      this.recipeBookComponent.tick();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
         this.renderBg(var3, var1, var2);
         this.recipeBookComponent.render(var1, var2, var3);
      } else {
         this.recipeBookComponent.render(var1, var2, var3);
         super.render(var1, var2, var3);
         this.recipeBookComponent.renderGhostRecipe(this.leftPos, this.topPos, true, var3);
      }

      this.renderTooltip(var1, var2);
      this.recipeBookComponent.renderTooltip(this.leftPos, this.topPos, var1, var2);
      this.magicalSpecialHackyFocus(this.recipeBookComponent);
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 28.0F, 6.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(CRAFTING_TABLE_LOCATION);
      int var4 = this.leftPos;
      int var5 = (this.height - this.imageHeight) / 2;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
   }

   protected boolean isHovering(int var1, int var2, int var3, int var4, double var5, double var7) {
      return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(var1, var2, var3, var4, var5, var7);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.recipeBookComponent.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         return this.widthTooNarrow && this.recipeBookComponent.isVisible() ? true : super.mouseClicked(var1, var3, var5);
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
      return this.recipeBookComponent.hasClickedOutside(var1, var3, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, var7) && var8;
   }

   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      super.slotClicked(var1, var2, var3, var4);
      this.recipeBookComponent.slotClicked(var1);
   }

   public void recipesUpdated() {
      this.recipeBookComponent.recipesUpdated();
   }

   public void removed() {
      this.recipeBookComponent.removed();
      super.removed();
   }

   public RecipeBookComponent getRecipeBookComponent() {
      return this.recipeBookComponent;
   }
}
