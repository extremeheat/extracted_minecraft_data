package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsInviteScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.invite.profile.name").withStyle(var0 -> var0.withColor(-6250336));
   private static final Component INVITING_PLAYER_TEXT = Component.translatable("mco.configure.world.players.inviting")
      .withStyle(var0 -> var0.withColor(-6250336));
   private static final Component NO_SUCH_PLAYER_ERROR_TEXT = Component.translatable("mco.configure.world.players.error")
      .withStyle(var0 -> var0.withColor(-65536));
   private EditBox profileName;
   private Button inviteButton;
   private final RealmsServer serverData;
   private final RealmsConfigureWorldScreen configureScreen;
   private final Screen lastScreen;
   @Nullable
   private Component message;

   public RealmsInviteScreen(RealmsConfigureWorldScreen var1, Screen var2, RealmsServer var3) {
      super(GameNarrator.NO_TITLE);
      this.configureScreen = var1;
      this.lastScreen = var2;
      this.serverData = var3;
   }

   @Override
   public void init() {
      this.profileName = new EditBox(
         this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, null, Component.translatable("mco.configure.world.invite.profile.name")
      );
      this.addWidget(this.profileName);
      this.setInitialFocus(this.profileName);
      this.inviteButton = this.addRenderableWidget(
         Button.builder(Component.translatable("mco.configure.world.buttons.invite"), var1 -> this.onInvite())
            .bounds(this.width / 2 - 100, row(10), 200, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, row(12), 200, 20).build()
      );
   }

   private void onInvite() {
      if (Util.isBlank(this.profileName.getValue())) {
         this.showMessage(NO_SUCH_PLAYER_ERROR_TEXT);
      } else {
         long var1 = this.serverData.id;
         String var3 = this.profileName.getValue().trim();
         this.inviteButton.active = false;
         this.profileName.setEditable(false);
         this.showMessage(INVITING_PLAYER_TEXT);
         CompletableFuture.<RealmsServer>supplyAsync(() -> {
            try {
               return RealmsClient.create().invite(var1, var3);
            } catch (Exception var4) {
               LOGGER.error("Couldn't invite user");
               return null;
            }
         }, Util.ioPool()).thenAcceptAsync(var1x -> {
            if (var1x != null) {
               this.serverData.players = var1x.players;
               this.minecraft.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            } else {
               this.showMessage(NO_SUCH_PLAYER_ERROR_TEXT);
            }

            this.profileName.setEditable(true);
            this.inviteButton.active = true;
         }, this.screenExecutor);
      }
   }

   private void showMessage(Component var1) {
      this.message = var1;
      this.minecraft.getNarrator().sayNow(var1);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawString(this.font, NAME_LABEL, this.width / 2 - 100, row(1), -1, false);
      if (this.message != null) {
         var1.drawCenteredString(this.font, this.message, this.width / 2, row(5), -1);
      }

      this.profileName.render(var1, var2, var3, var4);
   }
}
