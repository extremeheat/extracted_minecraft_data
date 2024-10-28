package com.mojang.realmsclient.gui.screens;

import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsClientOutdatedScreen extends RealmsScreen {
   private static final Component INCOMPATIBLE_TITLE = Component.translatable("mco.client.incompatible.title").withColor(-65536);
   private static final Component INCOMPATIBLE_CLIENT_VERSION = Component.literal(SharedConstants.getCurrentVersion().getName()).withColor(-65536);
   private static final Component UNSUPPORTED_SNAPSHOT_VERSION;
   private static final Component OUTDATED_STABLE_VERSION;
   private final Screen lastScreen;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public RealmsClientOutdatedScreen(Screen var1) {
      super(INCOMPATIBLE_TITLE);
      this.lastScreen = var1;
   }

   public void init() {
      this.layout.addTitleHeader(INCOMPATIBLE_TITLE, this.font);
      this.layout.addToContents((new MultiLineTextWidget(this.getErrorMessage(), this.font)).setCentered(true));
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1) -> {
         this.onClose();
      }).width(200).build());
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private Component getErrorMessage() {
      return SharedConstants.getCurrentVersion().isStable() ? OUTDATED_STABLE_VERSION : UNSUPPORTED_SNAPSHOT_VERSION;
   }

   static {
      UNSUPPORTED_SNAPSHOT_VERSION = Component.translatable("mco.client.unsupported.snapshot.version", INCOMPATIBLE_CLIENT_VERSION);
      OUTDATED_STABLE_VERSION = Component.translatable("mco.client.outdated.stable.version", INCOMPATIBLE_CLIENT_VERSION);
   }
}
