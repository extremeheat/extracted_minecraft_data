package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen extends RealmsScreen {
   private final Screen nextScreen;
   private Component line1;
   private Component line2;

   public RealmsGenericErrorScreen(RealmsServiceException var1, Screen var2) {
      super();
      this.nextScreen = var2;
      this.errorMessage(var1);
   }

   public RealmsGenericErrorScreen(Component var1, Screen var2) {
      super();
      this.nextScreen = var2;
      this.errorMessage(var1);
   }

   public RealmsGenericErrorScreen(Component var1, Component var2, Screen var3) {
      super();
      this.nextScreen = var3;
      this.errorMessage(var1, var2);
   }

   private void errorMessage(RealmsServiceException var1) {
      if (var1.errorCode == -1) {
         this.line1 = new TextComponent("An error occurred (" + var1.httpResultCode + "):");
         this.line2 = new TextComponent(var1.httpResponseContent);
      } else {
         this.line1 = new TextComponent("Realms (" + var1.errorCode + "):");
         String var2 = "mco.errorMessage." + var1.errorCode;
         this.line2 = (Component)(I18n.exists(var2) ? new TranslatableComponent(var2) : Component.nullToEmpty(var1.errorMsg));
      }

   }

   private void errorMessage(Component var1) {
      this.line1 = new TextComponent("An error occurred: ");
      this.line2 = var1;
   }

   private void errorMessage(Component var1, Component var2) {
      this.line1 = var1;
      this.line2 = var2;
   }

   public void init() {
      NarrationHelper.now(this.line1.getString() + ": " + this.line2.getString());
      this.addButton(new Button(this.width / 2 - 100, this.height - 52, 200, 20, new TextComponent("Ok"), (var1) -> {
         this.minecraft.setScreen(this.nextScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.line1, this.width / 2, 80, 16777215);
      drawCenteredString(var1, this.font, this.line2, this.width / 2, 100, 16711680);
      super.render(var1, var2, var3, var4);
   }
}
