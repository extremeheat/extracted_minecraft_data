package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.HopperMenu;

public class HopperScreen extends AbstractContainerScreen<HopperMenu> {
   private static final Identifier HOPPER_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/hopper.png");

   public HopperScreen(final HopperMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title, 176, 133);
      this.inventoryLabelY = this.imageHeight - 94;
   }

   protected void renderBg(final GuiGraphics graphics, final float a, final int xm, final int ym) {
      int xo = (this.width - this.imageWidth) / 2;
      int yo = (this.height - this.imageHeight) / 2;
      graphics.blit(RenderPipelines.GUI_TEXTURED, HOPPER_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }
}
