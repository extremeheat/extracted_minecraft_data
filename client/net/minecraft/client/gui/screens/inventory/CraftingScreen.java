package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CraftingMenu;

public class CraftingScreen extends AbstractRecipeBookScreen<CraftingMenu> {
   private static final Identifier CRAFTING_TABLE_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");

   public CraftingScreen(final CraftingMenu menu, final Inventory inventory, final Component title) {
      super(menu, new CraftingRecipeBookComponent(menu), inventory, title);
   }

   protected void init() {
      super.init();
      this.titleLabelX = 29;
   }

   protected ScreenPosition getRecipeBookButtonPosition() {
      return new ScreenPosition(this.leftPos + 5, this.height / 2 - 49);
   }

   protected void renderBg(final GuiGraphics graphics, final float a, final int xm, final int ym) {
      int xo = this.leftPos;
      int yo = (this.height - this.imageHeight) / 2;
      graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }
}
