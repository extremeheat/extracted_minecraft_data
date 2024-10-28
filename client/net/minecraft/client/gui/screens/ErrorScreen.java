package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ErrorScreen extends Screen {
   private final Component message;

   public ErrorScreen(Component var1, Component var2) {
      super(var1);
      this.message = var2;
   }

   protected void init() {
      super.init();
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1) -> {
         this.minecraft.setScreen((Screen)null);
      }).bounds(this.width / 2 - 100, 140, 200, 20).build());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 90, 16777215);
      var1.drawCenteredString(this.font, (Component)this.message, this.width / 2, 110, 16777215);
   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      var1.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
