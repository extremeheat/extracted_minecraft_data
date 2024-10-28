package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.GetServerDetailsTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

public class RealmsTermsScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.terms.title");
   private static final Component TERMS_STATIC_TEXT = Component.translatable("mco.terms.sentence.1");
   private static final Component TERMS_LINK_TEXT;
   private final Screen lastScreen;
   private final RealmsServer realmsServer;
   private boolean onLink;

   public RealmsTermsScreen(Screen var1, RealmsServer var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.realmsServer = var2;
   }

   public void init() {
      int var1 = this.width / 4 - 2;
      this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.agree"), (var1x) -> {
         this.agreedToTos();
      }).bounds(this.width / 4, row(12), var1, 20).build());
      this.addRenderableWidget(Button.builder(Component.translatable("mco.terms.buttons.disagree"), (var1x) -> {
         this.minecraft.setScreen(this.lastScreen);
      }).bounds(this.width / 2 + 4, row(12), var1, 20).build());
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
         this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new LongRunningTask[]{new GetServerDetailsTask(this.lastScreen, this.realmsServer)}));
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn't agree to TOS", var3);
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (this.onLink) {
         this.minecraft.keyboardHandler.setClipboard(CommonLinks.REALMS_TERMS.toString());
         Util.getPlatform().openUri(CommonLinks.REALMS_TERMS);
         return true;
      } else {
         return super.mouseClicked(var1, var3, var5);
      }
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), TERMS_STATIC_TEXT).append(CommonComponents.SPACE).append(TERMS_LINK_TEXT);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 17, -1);
      var1.drawString(this.font, (Component)TERMS_STATIC_TEXT, this.width / 2 - 120, row(5), -1, false);
      int var5 = this.font.width((FormattedText)TERMS_STATIC_TEXT);
      int var6 = this.width / 2 - 121 + var5;
      int var7 = row(5);
      int var8 = var6 + this.font.width((FormattedText)TERMS_LINK_TEXT) + 1;
      int var10000 = var7 + 1;
      Objects.requireNonNull(this.font);
      int var9 = var10000 + 9;
      this.onLink = var6 <= var2 && var2 <= var8 && var7 <= var3 && var3 <= var9;
      var1.drawString(this.font, TERMS_LINK_TEXT, this.width / 2 - 120 + var5, row(5), this.onLink ? 7107012 : 3368635, false);
   }

   static {
      TERMS_LINK_TEXT = CommonComponents.space().append((Component)Component.translatable("mco.terms.sentence.2").withStyle(Style.EMPTY.withUnderlined(true)));
   }
}
