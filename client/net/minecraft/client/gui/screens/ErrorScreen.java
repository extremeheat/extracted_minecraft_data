package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ErrorScreen extends Screen {
   private final Component message;

   public ErrorScreen(final Component title, final Component message) {
      super(title);
      this.message = message;
   }

   protected void init() {
      super.init();
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> this.minecraft.setScreen((Screen)null)).bounds(this.width / 2 - 100, 140, 200, 20).build());
   }

   public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      super.render(graphics, mouseX, mouseY, a);
      graphics.drawCenteredString(this.font, (Component)this.title, this.width / 2, 90, -1);
      graphics.drawCenteredString(this.font, (Component)this.message, this.width / 2, 110, -1);
   }

   public void renderBackground(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      graphics.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
