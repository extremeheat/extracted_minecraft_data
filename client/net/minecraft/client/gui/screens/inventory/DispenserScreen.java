package net.minecraft.client.gui.screens.inventory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DispenserMenu;

public class DispenserScreen extends AbstractContainerScreen<DispenserMenu> {
   private static final Identifier CONTAINER_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/dispenser.png");

   public DispenserScreen(final DispenserMenu menu, final Inventory inventory, final Component title) {
      super(menu, inventory, title);
   }

   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width((FormattedText)this.title)) / 2;
   }

   protected void renderBg(final GuiGraphics graphics, final float a, final int xm, final int ym) {
      int xo = (this.width - this.imageWidth) / 2;
      int yo = (this.height - this.imageHeight) / 2;
      graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_LOCATION, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
   }
}
