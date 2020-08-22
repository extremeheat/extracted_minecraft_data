package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen extends RealmsScreen {
   private final RealmsScreen nextScreen;
   private String line1;
   private String line2;

   public RealmsGenericErrorScreen(RealmsServiceException var1, RealmsScreen var2) {
      this.nextScreen = var2;
      this.errorMessage(var1);
   }

   public RealmsGenericErrorScreen(String var1, RealmsScreen var2) {
      this.nextScreen = var2;
      this.errorMessage(var1);
   }

   public RealmsGenericErrorScreen(String var1, String var2, RealmsScreen var3) {
      this.nextScreen = var3;
      this.errorMessage(var1, var2);
   }

   private void errorMessage(RealmsServiceException var1) {
      if (var1.errorCode == -1) {
         this.line1 = "An error occurred (" + var1.httpResultCode + "):";
         this.line2 = var1.httpResponseContent;
      } else {
         this.line1 = "Realms (" + var1.errorCode + "):";
         String var2 = "mco.errorMessage." + var1.errorCode;
         String var3 = getLocalizedString(var2);
         this.line2 = var3.equals(var2) ? var1.errorMsg : var3;
      }

   }

   private void errorMessage(String var1) {
      this.line1 = "An error occurred: ";
      this.line2 = var1;
   }

   private void errorMessage(String var1, String var2) {
      this.line1 = var1;
      this.line2 = var2;
   }

   public void init() {
      Realms.narrateNow(this.line1 + ": " + this.line2);
      this.buttonsAdd(new RealmsButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok") {
         public void onPress() {
            Realms.setScreen(RealmsGenericErrorScreen.this.nextScreen);
         }
      });
   }

   public void tick() {
      super.tick();
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.line1, this.width() / 2, 80, 16777215);
      this.drawCenteredString(this.line2, this.width() / 2, 100, 16711680);
      super.render(var1, var2, var3);
   }
}
