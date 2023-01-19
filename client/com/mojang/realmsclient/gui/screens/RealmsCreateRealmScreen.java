package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.util.task.WorldCreationTask;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsCreateRealmScreen extends RealmsScreen {
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
   private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
   private final RealmsServer server;
   private final RealmsMainScreen lastScreen;
   private EditBox nameBox;
   private EditBox descriptionBox;
   private Button createButton;

   public RealmsCreateRealmScreen(RealmsServer var1, RealmsMainScreen var2) {
      super(Component.translatable("mco.selectServer.create"));
      this.server = var1;
      this.lastScreen = var2;
   }

   @Override
   public void tick() {
      if (this.nameBox != null) {
         this.nameBox.tick();
      }

      if (this.descriptionBox != null) {
         this.descriptionBox.tick();
      }
   }

   @Override
   public void init() {
      this.createButton = this.addRenderableWidget(
         Button.builder(Component.translatable("mco.create.world"), var1 -> this.createWorld())
            .bounds(this.width / 2 - 100, this.height / 4 + 120 + 17, 97, 20)
            .build()
      );
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_CANCEL, var1 -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 + 5, this.height / 4 + 120 + 17, 95, 20)
            .build()
      );
      this.createButton.active = false;
      this.nameBox = new EditBox(this.minecraft.font, this.width / 2 - 100, 65, 200, 20, null, Component.translatable("mco.configure.world.name"));
      this.addWidget(this.nameBox);
      this.setInitialFocus(this.nameBox);
      this.descriptionBox = new EditBox(
         this.minecraft.font, this.width / 2 - 100, 115, 200, 20, null, Component.translatable("mco.configure.world.description")
      );
      this.addWidget(this.descriptionBox);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      boolean var3 = super.charTyped(var1, var2);
      this.createButton.active = this.valid();
      return var3;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         boolean var4 = super.keyPressed(var1, var2, var3);
         this.createButton.active = this.valid();
         return var4;
      }
   }

   private void createWorld() {
      if (this.valid()) {
         RealmsResetWorldScreen var1 = new RealmsResetWorldScreen(
            this.lastScreen,
            this.server,
            Component.translatable("mco.selectServer.create"),
            Component.translatable("mco.create.world.subtitle"),
            10526880,
            Component.translatable("mco.create.world.skip"),
            () -> this.minecraft.execute(() -> this.minecraft.setScreen(this.lastScreen.newScreen())),
            () -> this.minecraft.setScreen(this.lastScreen.newScreen())
         );
         var1.setResetTitle(Component.translatable("mco.create.world.reset.title"));
         this.minecraft
            .setScreen(
               new RealmsLongRunningMcoTaskScreen(
                  this.lastScreen, new WorldCreationTask(this.server.id, this.nameBox.getValue(), this.descriptionBox.getValue(), var1)
               )
            );
      }
   }

   private boolean valid() {
      return !this.nameBox.getValue().trim().isEmpty();
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 11, 16777215);
      this.font.draw(var1, NAME_LABEL, (float)(this.width / 2 - 100), 52.0F, 10526880);
      this.font.draw(var1, DESCRIPTION_LABEL, (float)(this.width / 2 - 100), 102.0F, 10526880);
      if (this.nameBox != null) {
         this.nameBox.render(var1, var2, var3, var4);
      }

      if (this.descriptionBox != null) {
         this.descriptionBox.render(var1, var2, var3, var4);
      }

      super.render(var1, var2, var3, var4);
   }
}
