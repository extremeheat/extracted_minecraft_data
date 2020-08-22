package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class ErrorScreen extends Screen {
   private final String message;

   public ErrorScreen(Component var1, String var2) {
      super(var1);
      this.message = var2;
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, 140, 200, 20, I18n.get("gui.cancel"), (var1) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   public void render(int var1, int var2, float var3) {
      this.fillGradient(0, 0, this.width, this.height, -12574688, -11530224);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 90, 16777215);
      this.drawCenteredString(this.font, this.message, this.width / 2, 110, 16777215);
      super.render(var1, var2, var3);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }
}
