package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsParentalConsentScreen extends RealmsScreen {
   private final RealmsScreen nextScreen;

   public RealmsParentalConsentScreen(RealmsScreen var1) {
      this.nextScreen = var1;
   }

   public void init() {
      Realms.narrateNow(getLocalizedString("mco.account.privacyinfo"));
      String var1 = getLocalizedString("mco.account.update");
      String var2 = getLocalizedString("gui.back");
      int var3 = Math.max(this.fontWidth(var1), this.fontWidth(var2)) + 30;
      String var4 = getLocalizedString("mco.account.privacy.info");
      int var5 = (int)((double)this.fontWidth(var4) * 1.2D);
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 - var5 / 2, RealmsConstants.row(11), var5, 20, var4) {
         public void onPress() {
            RealmsUtil.browseTo("https://minecraft.net/privacy/gdpr/");
         }
      });
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 - (var3 + 5), RealmsConstants.row(13), var3, 20, var1) {
         public void onPress() {
            RealmsUtil.browseTo("https://minecraft.net/update-account");
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 5, RealmsConstants.row(13), var3, 20, var2) {
         public void onPress() {
            Realms.setScreen(RealmsParentalConsentScreen.this.nextScreen);
         }
      });
   }

   public void tick() {
      super.tick();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return super.mouseClicked(var1, var3, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      List var4 = this.getLocalizedStringWithLineWidth("mco.account.privacyinfo", (int)Math.round((double)this.width() * 0.9D));
      int var5 = 15;

      for(Iterator var6 = var4.iterator(); var6.hasNext(); var5 += 15) {
         String var7 = (String)var6.next();
         this.drawCenteredString(var7, this.width() / 2, var5, 16777215);
      }

      super.render(var1, var2, var3);
   }
}
