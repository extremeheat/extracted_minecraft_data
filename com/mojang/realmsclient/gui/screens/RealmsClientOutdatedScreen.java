package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsClientOutdatedScreen extends RealmsScreen {
   private final RealmsScreen lastScreen;
   private final boolean outdated;

   public RealmsClientOutdatedScreen(RealmsScreen var1, boolean var2) {
      this.lastScreen = var1;
      this.outdated = var2;
   }

   public void init() {
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.row(12), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsClientOutdatedScreen.this.lastScreen);
         }
      });
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      String var4 = getLocalizedString(this.outdated ? "mco.client.outdated.title" : "mco.client.incompatible.title");
      this.drawCenteredString(var4, this.width() / 2, RealmsConstants.row(3), 16711680);
      int var5 = this.outdated ? 2 : 3;

      for(int var6 = 0; var6 < var5; ++var6) {
         String var7 = getLocalizedString((this.outdated ? "mco.client.outdated.msg.line" : "mco.client.incompatible.msg.line") + (var6 + 1));
         this.drawCenteredString(var7, this.width() / 2, RealmsConstants.row(5) + var6 * 12, 16777215);
      }

      super.render(var1, var2, var3);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 != 257 && var1 != 335 && var1 != 256) {
         return super.keyPressed(var1, var2, var3);
      } else {
         Realms.setScreen(this.lastScreen);
         return true;
      }
   }
}
