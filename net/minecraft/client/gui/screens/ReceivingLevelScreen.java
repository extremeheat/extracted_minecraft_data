package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.resources.language.I18n;

public class ReceivingLevelScreen extends Screen {
   public ReceivingLevelScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public void render(int var1, int var2, float var3) {
      this.renderDirtBackground(0);
      this.drawCenteredString(this.font, I18n.get("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(var1, var2, var3);
   }

   public boolean isPauseScreen() {
      return false;
   }
}
