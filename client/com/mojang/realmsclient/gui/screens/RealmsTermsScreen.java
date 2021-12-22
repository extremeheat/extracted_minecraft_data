package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsTermsScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Component TITLE = new TranslatableComponent("mco.terms.title");
   private static final Component TERMS_STATIC_TEXT = new TranslatableComponent("mco.terms.sentence.1");
   private static final Component TERMS_LINK_TEXT;
   private final Screen lastScreen;
   private final RealmsMainScreen mainScreen;
   private final RealmsServer realmsServer;
   private boolean onLink;
   private final String realmsToSUrl = "https://aka.ms/MinecraftRealmsTerms";

   public RealmsTermsScreen(Screen var1, RealmsMainScreen var2, RealmsServer var3) {
      super(TITLE);
      this.lastScreen = var1;
      this.mainScreen = var2;
      this.realmsServer = var3;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int var1 = this.width / 4 - 2;
      this.addRenderableWidget(new Button(this.width / 4, row(12), var1, 20, new TranslatableComponent("mco.terms.buttons.agree"), (var1x) -> {
         this.agreedToTos();
      }));
      this.addRenderableWidget(new Button(this.width / 2 + 4, row(12), var1, 20, new TranslatableComponent("mco.terms.buttons.disagree"), (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void agreedToTos() {
      RealmsClient var1 = RealmsClient.create();

      try {
         var1.agreeToTos();
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new GetServerDetailsTask(this.mainScreen, this.lastScreen, this.realmsServer, new ReentrantLock())));
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn't agree to TOS");
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.onLink) {
         this.minecraft.keyboardHandler.setClipboard("https://aka.ms/MinecraftRealmsTerms");
         Util.getPlatform().openUri("https://aka.ms/MinecraftRealmsTerms");
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), TERMS_STATIC_TEXT).append(" ").append(TERMS_LINK_TEXT);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 17, 16777215);
      this.font.draw(var1, TERMS_STATIC_TEXT, (float)(this.width / 2 - 120), (float)row(5), 16777215);
      int var5 = this.font.width((FormattedText)TERMS_STATIC_TEXT);
      int var6 = this.width / 2 - 121 + var5;
      int var7 = row(5);
      int var8 = var6 + this.font.width((FormattedText)TERMS_LINK_TEXT) + 1;
      int var10000 = var7 + 1;
      Objects.requireNonNull(this.font);
      int var9 = var10000 + 9;
      this.onLink = var6 <= var2 && var2 <= var8 && var7 <= var3 && var3 <= var9;
      this.font.draw(var1, TERMS_LINK_TEXT, (float)(this.width / 2 - 120 + var5), (float)row(5), this.onLink ? 7107012 : 3368635);
      super.render(var1, var2, var3, var4);
   }

   static {
      TERMS_LINK_TEXT = (new TextComponent(" ")).append((new TranslatableComponent("mco.terms.sentence.2")).withStyle(Style.EMPTY.withUnderlined(true)));
   }
}
