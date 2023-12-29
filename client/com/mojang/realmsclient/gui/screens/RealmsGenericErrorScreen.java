package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen extends RealmsScreen {
   private final Screen nextScreen;
   private final RealmsGenericErrorScreen.ErrorMessage lines;
   private MultiLineLabel line2Split = MultiLineLabel.EMPTY;

   public RealmsGenericErrorScreen(RealmsServiceException var1, Screen var2) {
      super(GameNarrator.NO_TITLE);
      this.nextScreen = var2;
      this.lines = errorMessage(var1);
   }

   public RealmsGenericErrorScreen(Component var1, Screen var2) {
      super(GameNarrator.NO_TITLE);
      this.nextScreen = var2;
      this.lines = errorMessage(var1);
   }

   public RealmsGenericErrorScreen(Component var1, Component var2, Screen var3) {
      super(GameNarrator.NO_TITLE);
      this.nextScreen = var3;
      this.lines = errorMessage(var1, var2);
   }

   private static RealmsGenericErrorScreen.ErrorMessage errorMessage(RealmsServiceException var0) {
      RealmsError var1 = var0.realmsError;
      return errorMessage(Component.translatable("mco.errorMessage.realmsService.realmsError", var1.errorCode()), var1.errorMessage());
   }

   private static RealmsGenericErrorScreen.ErrorMessage errorMessage(Component var0) {
      return errorMessage(Component.translatable("mco.errorMessage.generic"), var0);
   }

   private static RealmsGenericErrorScreen.ErrorMessage errorMessage(Component var0, Component var1) {
      return new RealmsGenericErrorScreen.ErrorMessage(var0, var1);
   }

   @Override
   public void init() {
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_OK, var1 -> this.minecraft.setScreen(this.nextScreen))
            .bounds(this.width / 2 - 100, this.height - 52, 200, 20)
            .build()
      );
      this.line2Split = MultiLineLabel.create(this.font, this.lines.detail, this.width * 3 / 4);
   }

   @Override
   public Component getNarrationMessage() {
      return Component.empty().append(this.lines.title).append(": ").append(this.lines.detail);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.lines.title, this.width / 2, 80, -1);
      this.line2Split.renderCentered(var1, this.width / 2, 100, 9, -2142128);
   }

   static record ErrorMessage(Component a, Component b) {
      final Component title;
      final Component detail;

      ErrorMessage(Component var1, Component var2) {
         super();
         this.title = var1;
         this.detail = var2;
      }
   }
}
