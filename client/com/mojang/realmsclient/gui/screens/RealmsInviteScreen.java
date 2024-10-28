package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.StringUtil;
import org.slf4j.Logger;

public class RealmsInviteScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.configure.world.buttons.invite");
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.invite.profile.name").withColor(-6250336);
   private static final Component INVITING_PLAYER_TEXT = Component.translatable("mco.configure.world.players.inviting").withColor(-6250336);
   private static final Component NO_SUCH_PLAYER_ERROR_TEXT = Component.translatable("mco.configure.world.players.error").withColor(-65536);
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private EditBox profileName;
   private Button inviteButton;
   private final RealmsServer serverData;
   private final RealmsConfigureWorldScreen configureScreen;
   private final Screen lastScreen;
   @Nullable
   private Component message;

   public RealmsInviteScreen(RealmsConfigureWorldScreen var1, Screen var2, RealmsServer var3) {
      super(TITLE);
      this.configureScreen = var1;
      this.lastScreen = var2;
      this.serverData = var3;
   }

   public void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      LinearLayout var1 = (LinearLayout)this.layout.addToContents(LinearLayout.vertical().spacing(8));
      this.profileName = new EditBox(this.minecraft.font, 200, 20, Component.translatable("mco.configure.world.invite.profile.name"));
      var1.addChild(CommonLayouts.labeledElement(this.font, this.profileName, NAME_LABEL));
      this.inviteButton = (Button)var1.addChild(Button.builder(TITLE, (var1x) -> {
         this.onInvite();
      }).width(200).build());
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).width(200).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.profileName);
   }

   private void onInvite() {
      if (StringUtil.isBlank(this.profileName.getValue())) {
         this.showMessage(NO_SUCH_PLAYER_ERROR_TEXT);
      } else {
         long var1 = this.serverData.id;
         String var3 = this.profileName.getValue().trim();
         this.inviteButton.active = false;
         this.profileName.setEditable(false);
         this.showMessage(INVITING_PLAYER_TEXT);
         CompletableFuture.supplyAsync(() -> {
            try {
               return RealmsClient.create().invite(var1, var3);
            } catch (Exception var4) {
               LOGGER.error("Couldn't invite user");
               return null;
            }
         }, Util.ioPool()).thenAcceptAsync((var1x) -> {
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

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.message != null) {
         var1.drawCenteredString(this.font, (Component)this.message, this.width / 2, this.inviteButton.getY() + this.inviteButton.getHeight() + 8, -1);
      }

   }
}
