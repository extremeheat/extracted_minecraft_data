package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.social.EntityPortraitWidget;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;

public class InventoryScreen extends AbstractRecipeBookScreen<InventoryMenu> {
   private float xMouse;
   private float yMouse;
   private boolean buttonClicked;
   private final EffectsInInventory effects;

   public InventoryScreen(final Player player) {
      super(player.inventoryMenu, new CraftingRecipeBookComponent(player.inventoryMenu), player.getInventory(), Component.translatable("container.crafting"));
      this.titleLabelX = 97;
      this.effects = new EffectsInInventory(this);
   }

   public void containerTick() {
      super.containerTick();
      if (this.minecraft.player.hasInfiniteMaterials()) {
         this.minecraft.gui.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()));
      }

   }

   protected void init() {
      if (this.minecraft.player.hasInfiniteMaterials()) {
         this.minecraft.gui.setScreen(new CreativeModeInventoryScreen(this.minecraft.player, this.minecraft.player.connection.enabledFeatures(), (Boolean)this.minecraft.options.operatorItemsTab().get()));
      } else {
         super.init();
      }
   }

   protected ScreenPosition getRecipeBookButtonPosition() {
      return new ScreenPosition(this.leftPos + 104, this.height / 2 - 22);
   }

   protected void onRecipeBookButtonClick() {
      this.buttonClicked = true;
   }

   protected void extractLabels(final GuiGraphicsExtractor graphics, final int xm, final int ym) {
      graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      this.effects.extractRenderState(graphics, mouseX, mouseY);
      super.extractRenderState(graphics, mouseX, mouseY, a);
      this.xMouse = (float)mouseX;
      this.yMouse = (float)mouseY;
   }

   public boolean showsActiveEffects() {
      return this.effects.canSeeEffects();
   }

   protected boolean isBiggerResultSlot() {
      return false;
   }

   public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractBackground(graphics, mouseX, mouseY, a);
      int xo = this.leftPos;
      int yo = this.topPos;
      graphics.blit(RenderPipelines.GUI_TEXTURED, INVENTORY_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      EntityPortraitWidget.extractEntityInInventoryFollowsMouse(graphics, xo + 26, yo + 8, xo + 75, yo + 78, 30.0F, 0.0625F, this.xMouse, this.yMouse, this.minecraft.player);
   }

   public boolean mouseReleased(final MouseButtonEvent event) {
      if (this.buttonClicked) {
         this.buttonClicked = false;
         return true;
      } else {
         return super.mouseReleased(event);
      }
   }
}
