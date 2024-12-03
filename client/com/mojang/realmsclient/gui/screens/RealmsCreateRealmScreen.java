package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.Util;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.CommonLayouts;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.AlertScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.StringUtil;

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

   public RealmsCreateRealmScreen(RealmsMainScreen var1, RealmsServer var2, boolean var3) {
      super(CREATE_REALM_TEXT);
      this.lastScreen = var1;
      this.createWorldRunnable = () -> this.createWorld(var2, var3);
   }

   public void init() {
      this.layout.addTitleHeader(this.title, this.font);
      LinearLayout var1 = ((LinearLayout)this.layout.addToContents(LinearLayout.vertical())).spacing(10);
      Button var2 = Button.builder(CommonComponents.GUI_CONTINUE, (var1x) -> this.createWorldRunnable.run()).build();
      var2.active = false;
      this.nameBox = new EditBox(this.font, 210, 20, NAME_LABEL);
      this.nameBox.setResponder((var1x) -> var2.active = !StringUtil.isBlank(var1x));
      this.descriptionBox = new EditBox(this.font, 210, 20, DESCRIPTION_LABEL);
      var1.addChild(CommonLayouts.labeledElement(this.font, this.nameBox, NAME_LABEL));
      var1.addChild(CommonLayouts.labeledElement(this.font, this.descriptionBox, DESCRIPTION_LABEL));
      LinearLayout var3 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(10));
      var3.addChild(var2);
      var3.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onClose()).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void setInitialFocus() {
      this.setInitialFocus(this.nameBox);
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   private void createWorld(RealmsServer var1, boolean var2) {
      if (!var1.isSnapshotRealm() && var2) {
         AtomicBoolean var3 = new AtomicBoolean();
         this.minecraft.setScreen(new AlertScreen(() -> {
            var3.set(true);
            this.lastScreen.resetScreen();
            this.minecraft.setScreen(this.lastScreen);
         }, Component.translatable("mco.upload.preparing"), Component.empty()));
         CompletableFuture.supplyAsync(() -> createSnapshotRealm(var1), Util.backgroundExecutor()).thenAcceptAsync((var2x) -> {
            if (!var3.get()) {
               this.showResetWorldScreen(var2x);
            }

         }, this.minecraft).exceptionallyAsync((var1x) -> {
            this.lastScreen.resetScreen();
            Throwable var4 = var1x.getCause();
            Object var2;
            if (var4 instanceof RealmsServiceException var3) {
               var2 = var3.realmsError.errorMessage();
            } else {
               var2 = Component.translatable("mco.errorMessage.initialize.failed");
            }

            this.minecraft.setScreen(new RealmsGenericErrorScreen((Component)var2, this.lastScreen));
            return null;
         }, this.minecraft);
      } else {
         this.showResetWorldScreen(var1);
      }

   }

   private static RealmsServer createSnapshotRealm(RealmsServer var0) {
      RealmsClient var1 = RealmsClient.create();

      try {
         return var1.createSnapshotRealm(var0.id);
      } catch (RealmsServiceException var3) {
         throw new RuntimeException(var3);
      }
   }

   private void showResetWorldScreen(RealmsServer var1) {
      RealmCreationTask var2 = new RealmCreationTask(var1.id, this.nameBox.getValue(), this.descriptionBox.getValue());
      RealmsResetWorldScreen var3 = RealmsResetWorldScreen.forNewRealm(this, var1, var2, () -> this.minecraft.execute(() -> {
            RealmsMainScreen.refreshServerList();
            this.minecraft.setScreen(this.lastScreen);
         }));
      this.minecraft.setScreen(var3);
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }
}
