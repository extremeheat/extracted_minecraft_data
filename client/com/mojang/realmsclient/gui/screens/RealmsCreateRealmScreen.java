package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.task.WorldCreationTask;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsCreateRealmScreen extends RealmsScreen {
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
   private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
   private static final int BUTTON_SPACING = 10;
   private static final int CONTENT_WIDTH = 210;
   private final RealmsServer server;
   private final RealmsMainScreen lastScreen;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private EditBox nameBox;
   private EditBox descriptionBox;

   public RealmsCreateRealmScreen(RealmsServer var1, RealmsMainScreen var2) {
      super(Component.translatable("mco.selectServer.create"));
      this.server = var1;
      this.lastScreen = var2;
   }

   @Override
   public void init() {
      this.layout.addToHeader(new StringWidget(this.title, this.font));
      LinearLayout var1 = this.layout.addToContents(LinearLayout.vertical()).spacing(10);
      Button var2 = Button.builder(Component.translatable("mco.create.world"), var1x -> this.createWorld()).build();
      var2.active = false;
      this.nameBox = new EditBox(this.font, 210, 20, Component.translatable("mco.configure.world.name"));
      this.nameBox.setResponder(var1x -> var2.active = !Util.isBlank(var1x));
      this.descriptionBox = new EditBox(this.font, 210, 20, Component.translatable("mco.configure.world.description"));
      var1.addChild(CommonLayouts.labeledElement(this.font, this.nameBox, NAME_LABEL));
      var1.addChild(CommonLayouts.labeledElement(this.font, this.descriptionBox, DESCRIPTION_LABEL));
      LinearLayout var3 = this.layout.addToFooter(LinearLayout.horizontal().spacing(10));
      var3.addChild(var2);
      var3.addChild(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.onClose()).build());
      this.layout.visitWidgets(var1x -> {
      });
      this.repositionElements();
      this.setInitialFocus(this.nameBox);
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   private void createWorld() {
      RealmsResetWorldScreen var1 = RealmsResetWorldScreen.forNewRealm(this.lastScreen, this.server, () -> this.minecraft.execute(() -> {
            this.lastScreen.refreshServerList();
            this.minecraft.setScreen(this.lastScreen);
         }));
      this.minecraft
         .setScreen(
            new RealmsLongRunningMcoTaskScreen(
               this.lastScreen, new WorldCreationTask(this.server.id, this.nameBox.getValue(), this.descriptionBox.getValue(), var1)
            )
         );
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}
