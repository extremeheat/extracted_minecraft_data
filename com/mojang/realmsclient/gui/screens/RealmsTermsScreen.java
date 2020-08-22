package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTermsScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsMainScreen mainScreen;
   private final RealmsServer realmsServer;
   private RealmsButton agreeButton;
   private boolean onLink;
   private final String realmsToSUrl = "https://minecraft.net/realms/terms";

   public RealmsTermsScreen(RealmsScreen var1, RealmsMainScreen var2, RealmsServer var3) {
      this.lastScreen = var1;
      this.mainScreen = var2;
      this.realmsServer = var3;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      int var1 = this.width() / 4;
      int var2 = this.width() / 4 - 2;
      int var3 = this.width() / 2 + 4;
      this.buttonsAdd(this.agreeButton = new RealmsButton(1, var1, RealmsConstants.row(12), var2, 20, getLocalizedString("mco.terms.buttons.agree")) {
         public void onPress() {
            RealmsTermsScreen.this.agreedToTos();
         }
      });
      this.buttonsAdd(new RealmsButton(2, var3, RealmsConstants.row(12), var2, 20, getLocalizedString("mco.terms.buttons.disagree")) {
         public void onPress() {
            Realms.setScreen(RealmsTermsScreen.this.lastScreen);
         }
      });
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void agreedToTos() {
      RealmsClient var1 = RealmsClient.createRealmsClient();

      try {
         var1.agreeToTos();
         RealmsLongRunningMcoTaskScreen var2 = new RealmsLongRunningMcoTaskScreen(this.lastScreen, new RealmsTasks.RealmsGetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock()));
         var2.start();
         Realms.setScreen(var2);
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn't agree to TOS");
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.onLink) {
         Realms.setClipboard("https://minecraft.net/realms/terms");
         RealmsUtil.browseTo("https://minecraft.net/realms/terms");
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(getLocalizedString("mco.terms.title"), this.width() / 2, 17, 16777215);
      this.drawString(getLocalizedString("mco.terms.sentence.1"), this.width() / 2 - 120, RealmsConstants.row(5), 16777215);
      int var4 = this.fontWidth(getLocalizedString("mco.terms.sentence.1"));
      int var5 = this.width() / 2 - 121 + var4;
      int var6 = RealmsConstants.row(5);
      int var7 = var5 + this.fontWidth("mco.terms.sentence.2") + 1;
      int var8 = var6 + 1 + this.fontLineHeight();
      if (var5 <= var1 && var1 <= var7 && var6 <= var2 && var2 <= var8) {
         this.onLink = true;
         this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + var4, RealmsConstants.row(5), 7107012);
      } else {
         this.onLink = false;
         this.drawString(" " + getLocalizedString("mco.terms.sentence.2"), this.width() / 2 - 120 + var4, RealmsConstants.row(5), 3368635);
      }

      super.render(var1, var2, var3);
   }
}
