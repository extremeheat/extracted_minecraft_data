package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringUtil;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RealmsJoinRealmWithCodeScreen extends AbstractRealmsCodeScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.selectServer.joinCode.title");
   private static final Component JOIN_CODE_LABEL = Component.translatable("mco.selectServer.joinCode").withColor(-6250336);
   private static final Component JOIN_CODE_HINT = Component.translatable("mco.selectServer.joinCode.hint");
   private static final Component JOIN_REALM_TEXT = Component.translatable("mco.selectServer.joinRealm");
   private static final Component VALIDATING_CODE_TEXT = Component.translatable("mco.selectServer.joinCode.validating").withColor(-1);
   private static final Component INVALID_CODE_TEXT = Component.translatable("mco.selectServer.joinCode.invalid").withColor(-65536);
   private final RealmsMainScreen lastScreen;
   private @Nullable EditBox joinCode;
   private @Nullable Button joinButton;
   private @Nullable Button backButton;
   private @Nullable Component message;

   public RealmsJoinRealmWithCodeScreen(final RealmsMainScreen lastScreen) {
      super(TITLE);
      this.lastScreen = lastScreen;
   }

   protected void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      LinearLayout content = (LinearLayout)this.layout.addToContents(LinearLayout.vertical().spacing(8));
      this.joinCode = new EditBox(this.font, 200, 20, JOIN_CODE_LABEL);
      this.joinCode.setHint(JOIN_CODE_HINT);
      content.addChild(CommonLayouts.labeledElement(this.font, this.joinCode, JOIN_CODE_LABEL));
      this.joinButton = (Button)content.addChild(Button.builder(JOIN_REALM_TEXT, (var1) -> this.joinRealm()).width(200).build());
      this.joinButton.active = false;
      this.joinCode.setResponder((value) -> {
         this.joinButton.active = !StringUtil.isBlank(value);
         this.message = null;
      });
      this.backButton = (Button)this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1) -> this.onClose()).width(200).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
   }

   protected void setInitialFocus() {
      if (this.joinCode != null) {
         this.setInitialFocus(this.joinCode);
      }

   }

   private void joinRealm() {
      if (this.joinCode != null && this.joinButton != null && this.backButton != null) {
         String code = this.joinCode.getValue().trim();
         if (!StringUtil.isBlank(code)) {
            this.joinCode.setEditable(false);
            this.joinButton.active = false;
            this.backButton.active = false;
            this.showMessage(VALIDATING_CODE_TEXT);
            RealmsUtil.runAsync((client) -> client.redeemInviteCode(code), this::onJoinFailure).thenRunAsync(() -> {
               this.minecraft.gui.setScreen(this.lastScreen);
               this.lastScreen.resetScreen();
            }, this.screenExecutor);
         }
      }
   }

   private void onJoinFailure(final RealmsServiceException exception) {
      LOGGER.error("Couldn't redeem invite code", exception);
      this.screenExecutor.execute(() -> {
         if (this.joinCode != null && this.joinButton != null && this.backButton != null) {
            this.joinCode.setEditable(true);
            this.joinButton.active = !StringUtil.isBlank(this.joinCode.getValue());
            this.backButton.active = true;
            this.showMessage(INVALID_CODE_TEXT);
         }

      });
   }

   private void showMessage(final Component message) {
      this.message = message;
      this.minecraft.getNarrator().saySystemNow(message);
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      if (this.message != null && this.joinButton != null) {
         graphics.centeredText(this.font, (Component)this.message, this.width / 2, this.joinButton.getY() + this.joinButton.getHeight() + 8, -1);
      }

   }

   public void onClose() {
      if (this.backButton == null || this.backButton.active) {
         this.minecraft.gui.setScreen(this.lastScreen);
      }

   }
}
