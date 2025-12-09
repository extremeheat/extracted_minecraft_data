package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsError;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.Objects;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen extends RealmsScreen {
   private static final Component GENERIC_TITLE = Component.translatable("mco.errorMessage.generic");
   private final Screen nextScreen;
   private final Component detail;
   private MultiLineLabel splitDetail;

   public RealmsGenericErrorScreen(RealmsServiceException var1, Screen var2) {
      this(RealmsGenericErrorScreen.ErrorMessage.forServiceError(var1), var2);
   }

   public RealmsGenericErrorScreen(Component var1, Screen var2) {
      this(new ErrorMessage(GENERIC_TITLE, var1), var2);
   }

   public RealmsGenericErrorScreen(Component var1, Component var2, Screen var3) {
      this(new ErrorMessage(var1, var2), var3);
   }

   private RealmsGenericErrorScreen(ErrorMessage var1, Screen var2) {
      super(var1.title);
      this.splitDetail = MultiLineLabel.EMPTY;
      this.nextScreen = var2;
      this.detail = ComponentUtils.mergeStyles(var1.detail, Style.EMPTY.withColor(-2142128));
   }

   public void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (var1) -> this.onClose()).bounds(this.width / 2 - 100, this.height - 52, 200, 20).build());
      this.splitDetail = MultiLineLabel.create(this.font, this.detail, this.width * 3 / 4);
   }

   public void onClose() {
      this.minecraft.setScreen(this.nextScreen);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.detail);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 80, -1);
      ActiveTextCollector var5 = var1.textRenderer();
      MultiLineLabel var10000 = this.splitDetail;
      TextAlignment var10001 = TextAlignment.CENTER;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.minecraft.font);
      var10000.visitLines(var10001, var10002, 100, 9, var5);
   }

   static record ErrorMessage(Component title, Component detail) {
      final Component title;
      final Component detail;

      ErrorMessage(Component var1, Component var2) {
         super();
         this.title = var1;
         this.detail = var2;
      }

      static ErrorMessage forServiceError(RealmsServiceException var0) {
         RealmsError var1 = var0.realmsError;
         return new ErrorMessage(Component.translatable("mco.errorMessage.realmsService.realmsError", var1.errorCode()), var1.errorMessage());
      }
   }
}
