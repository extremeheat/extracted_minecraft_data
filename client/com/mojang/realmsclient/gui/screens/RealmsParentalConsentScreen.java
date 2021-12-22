package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsParentalConsentScreen extends RealmsScreen {
   private static final Component MESSAGE = new TranslatableComponent("mco.account.privacyinfo");
   private final Screen nextScreen;
   private MultiLineLabel messageLines;

   public RealmsParentalConsentScreen(Screen var1) {
      super(NarratorChatListener.NO_TITLE);
      this.messageLines = MultiLineLabel.EMPTY;
      this.nextScreen = var1;
   }

   public void init() {
      TranslatableComponent var1 = new TranslatableComponent("mco.account.update");
      Component var2 = CommonComponents.GUI_BACK;
      int var3 = Math.max(this.font.width((FormattedText)var1), this.font.width((FormattedText)var2)) + 30;
      TranslatableComponent var4 = new TranslatableComponent("mco.account.privacy.info");
      int var5 = (int)((double)this.font.width((FormattedText)var4) * 1.2D);
      this.addRenderableWidget(new Button(this.width / 2 - var5 / 2, row(11), var5, 20, var4, (var0) -> {
         Util.getPlatform().openUri("https://aka.ms/MinecraftGDPR");
      }));
      this.addRenderableWidget(new Button(this.width / 2 - (var3 + 5), row(13), var3, 20, var1, (var0) -> {
         Util.getPlatform().openUri("https://aka.ms/UpdateMojangAccount");
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 5, row(13), var3, 20, var2, (var1x) -> {
         this.minecraft.setScreen(this.nextScreen);
      }));
      this.messageLines = MultiLineLabel.create(this.font, MESSAGE, (int)Math.round((double)this.width * 0.9D));
   }

   public Component getNarrationMessage() {
      return MESSAGE;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.messageLines.renderCentered(var1, this.width / 2, 15, 15, 16777215);
      super.render(var1, var2, var3, var4);
   }
}
