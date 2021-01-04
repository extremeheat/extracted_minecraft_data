package net.minecraft.client.gui.screens;

import net.minecraft.network.chat.Component;

public class GenericDirtMessageScreen extends Screen {
   public GenericDirtMessageScreen(Component var1) {
      super(var1);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(int var1, int var2, float var3) {
      this.renderDirtBackground(0);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 70, 16777215);
      super.render(var1, var2, var3);
   }
}
