package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsInviteScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.invite.profile.name");
   private static final Component NO_SUCH_PLAYER_ERROR_TEXT = Component.translatable("mco.configure.world.players.error");
   private EditBox profileName;
   private final RealmsServer serverData;
   private final RealmsConfigureWorldScreen configureScreen;
   private final Screen lastScreen;
   @Nullable
   private Component errorMsg;

   public RealmsInviteScreen(RealmsConfigureWorldScreen var1, Screen var2, RealmsServer var3) {
      super(GameNarrator.NO_TITLE);
      this.configureScreen = var1;
      this.lastScreen = var2;
      this.serverData = var3;
   }

   @Override
   public void tick() {
      this.profileName.tick();
   }

   @Override
   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.profileName = new EditBox(
         this.minecraft.font, this.width / 2 - 100, row(2), 200, 20, null, Component.translatable("mco.configure.world.invite.profile.name")
      );
      this.addWidget(this.profileName);
      this.setInitialFocus(this.profileName);
      this.addRenderableWidget(
         new Button(this.width / 2 - 100, row(10), 200, 20, Component.translatable("mco.configure.world.buttons.invite"), var1 -> this.onInvite())
      );
      this.addRenderableWidget(
         new Button(this.width / 2 - 100, row(12), 200, 20, CommonComponents.GUI_CANCEL, var1 -> this.minecraft.setScreen(this.lastScreen))
      );
   }

   @Override
   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onInvite() {
      RealmsClient var1 = RealmsClient.create();
      if (this.profileName.getValue() != null && !this.profileName.getValue().isEmpty()) {
         try {
            RealmsServer var2 = var1.invite(this.serverData.id, this.profileName.getValue().trim());
            if (var2 != null) {
               this.serverData.players = var2.players;
               this.minecraft.setScreen(new RealmsPlayerScreen(this.configureScreen, this.serverData));
            } else {
               this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
            }
         } catch (Exception var3) {
            LOGGER.error("Couldn't invite user");
            this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
         }
      } else {
         this.showError(NO_SUCH_PLAYER_ERROR_TEXT);
      }
   }

   private void showError(Component var1) {
      this.errorMsg = var1;
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
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.font.draw(var1, NAME_LABEL, (float)(this.width / 2 - 100), (float)row(1), 10526880);
      if (this.errorMsg != null) {
         drawCenteredString(var1, this.font, this.errorMsg, this.width / 2, row(5), 16711680);
      }

      this.profileName.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }
}
