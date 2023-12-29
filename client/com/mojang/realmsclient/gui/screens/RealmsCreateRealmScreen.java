package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.task.CreateSnapshotRealmTask;
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
   private static final Component CREATE_REALM_TEXT = Component.translatable("mco.selectServer.create");
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
   private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
   private static final int BUTTON_SPACING = 10;
   private static final int CONTENT_WIDTH = 210;
   private final RealmsMainScreen lastScreen;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private EditBox nameBox;
   private EditBox descriptionBox;
   private final Runnable createWorldRunnable;

   public RealmsCreateRealmScreen(RealmsMainScreen var1, RealmsServer var2) {
      super(CREATE_REALM_TEXT);
      this.lastScreen = var1;
      this.createWorldRunnable = () -> this.createWorld(var2);
   }

   public RealmsCreateRealmScreen(RealmsMainScreen var1, long var2) {
      super(CREATE_REALM_TEXT);
      this.lastScreen = var1;
      this.createWorldRunnable = () -> this.createSnapshotWorld(var2);
   }

   @Override
   public void init() {
      this.layout.addToHeader(new StringWidget(this.title, this.font));
      LinearLayout var1 = this.layout.addToContents(LinearLayout.vertical()).spacing(10);
      Button var2 = Button.builder(CommonComponents.GUI_CONTINUE, var1x -> this.createWorldRunnable.run()).build();
      var2.active = false;
      this.nameBox = new EditBox(this.font, 210, 20, NAME_LABEL);
      this.nameBox.setResponder(var1x -> var2.active = !Util.isBlank(var1x));
      this.descriptionBox = new EditBox(this.font, 210, 20, DESCRIPTION_LABEL);
      var1.addChild(CommonLayouts.labeledElement(this.font, this.nameBox, NAME_LABEL));
      var1.addChild(CommonLayouts.labeledElement(this.font, this.descriptionBox, DESCRIPTION_LABEL));
      LinearLayout var3 = this.layout.addToFooter(LinearLayout.horizontal().spacing(10));
      var3.addChild(var2);
      var3.addChild(Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).build());
      this.layout.visitWidgets(var1x -> {
      });
      this.repositionElements();
      this.setInitialFocus(this.nameBox);
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   private void createWorld(RealmsServer var1) {
      WorldCreationTask var2 = new WorldCreationTask(var1.id, this.nameBox.getValue(), this.descriptionBox.getValue());
      RealmsResetWorldScreen var3 = RealmsResetWorldScreen.forNewRealm(this, var1, var2, () -> this.minecraft.execute(() -> {
            RealmsMainScreen.refreshServerList();
            this.minecraft.setScreen(this.lastScreen);
         }));
      this.minecraft.setScreen(var3);
   }

   private void createSnapshotWorld(long var1) {
      RealmsResetNormalWorldScreen var3 = new RealmsResetNormalWorldScreen(
         var3x -> {
            if (var3x == null) {
               this.minecraft.setScreen(this);
            } else {
               this.minecraft
                  .setScreen(
                     new RealmsLongRunningMcoTaskScreen(
                        this, new CreateSnapshotRealmTask(this.lastScreen, var1, var3x, this.nameBox.getValue(), this.descriptionBox.getValue())
                     )
                  );
            }
         },
         CREATE_REALM_TEXT
      );
      this.minecraft.setScreen(var3);
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}
