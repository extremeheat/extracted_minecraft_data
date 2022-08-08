package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.util.Objects;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen extends RealmsScreen {
   private final Screen nextScreen;
   private final Pair<Component, Component> lines;
   private MultiLineLabel line2Split;

   public RealmsGenericErrorScreen(RealmsServiceException var1, Screen var2) {
      super(GameNarrator.NO_TITLE);
      this.line2Split = MultiLineLabel.EMPTY;
      this.nextScreen = var2;
      this.lines = errorMessage(var1);
   }

   public RealmsGenericErrorScreen(Component var1, Screen var2) {
      super(GameNarrator.NO_TITLE);
      this.line2Split = MultiLineLabel.EMPTY;
      this.nextScreen = var2;
      this.lines = errorMessage(var1);
   }

   public RealmsGenericErrorScreen(Component var1, Component var2, Screen var3) {
      super(GameNarrator.NO_TITLE);
      this.line2Split = MultiLineLabel.EMPTY;
      this.nextScreen = var3;
      this.lines = errorMessage(var1, var2);
   }

   private static Pair<Component, Component> errorMessage(RealmsServiceException var0) {
      if (var0.realmsError == null) {
         return Pair.of(Component.literal("An error occurred (" + var0.httpResultCode + "):"), Component.literal(var0.rawResponse));
      } else {
         String var1 = "mco.errorMessage." + var0.realmsError.getErrorCode();
         return Pair.of(Component.literal("Realms (" + var0.realmsError + "):"), I18n.exists(var1) ? Component.translatable(var1) : Component.nullToEmpty(var0.realmsError.getErrorMessage()));
      }
   }

   private static Pair<Component, Component> errorMessage(Component var0) {
      return Pair.of(Component.literal("An error occurred: "), var0);
   }

   private static Pair<Component, Component> errorMessage(Component var0, Component var1) {
      return Pair.of(var0, var1);
   }

   public void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 52, 200, 20, Component.literal("Ok"), (var1) -> {
         this.minecraft.setScreen(this.nextScreen);
      }));
      this.line2Split = MultiLineLabel.create(this.font, (FormattedText)this.lines.getSecond(), this.width * 3 / 4);
   }

   public Component getNarrationMessage() {
      return Component.empty().append((Component)this.lines.getFirst()).append(": ").append((Component)this.lines.getSecond());
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, (Component)this.lines.getFirst(), this.width / 2, 80, 16777215);
      MultiLineLabel var10000 = this.line2Split;
      int var10002 = this.width / 2;
      Objects.requireNonNull(this.minecraft.font);
      var10000.renderCentered(var1, var10002, 100, 9, 16711680);
      super.render(var1, var2, var3, var4);
   }
}
