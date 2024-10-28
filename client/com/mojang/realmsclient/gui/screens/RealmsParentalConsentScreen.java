package com.mojang.realmsclient.gui.screens;

import java.net.URI;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.CommonLinks;

public class RealmsParentalConsentScreen extends RealmsScreen {
   private static final Component MESSAGE = Component.translatable("mco.account.privacy.information");
   private static final int SPACING = 15;
   private final LinearLayout layout = LinearLayout.vertical();
   private final Screen lastScreen;
   @Nullable
   private MultiLineTextWidget textWidget;

   public RealmsParentalConsentScreen(Screen var1) {
      super(GameNarrator.NO_TITLE);
      this.lastScreen = var1;
   }

   public void init() {
      this.layout.spacing(15).defaultCellSetting().alignHorizontallyCenter();
      this.textWidget = (new MultiLineTextWidget(MESSAGE, this.font)).setCentered(true);
      this.layout.addChild(this.textWidget);
      LinearLayout var1 = (LinearLayout)this.layout.addChild(LinearLayout.horizontal().spacing(8));
      MutableComponent var2 = Component.translatable("mco.account.privacy.info.button");
      var1.addChild(Button.builder(var2, ConfirmLinkScreen.confirmLink(this, (URI)CommonLinks.GDPR)).build());
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   protected void repositionElements() {
      if (this.textWidget != null) {
         this.textWidget.setMaxWidth(this.width - 15);
      }

      this.layout.arrangeElements();
      FrameLayout.centerInRectangle(this.layout, this.getRectangle());
   }

   public Component getNarrationMessage() {
      return MESSAGE;
   }
}
