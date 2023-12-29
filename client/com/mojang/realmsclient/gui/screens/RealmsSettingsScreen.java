package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsSettingsScreen extends RealmsScreen {
   private static final int COMPONENT_WIDTH = 212;
   private static final Component NAME_LABEL = Component.translatable("mco.configure.world.name");
   private static final Component DESCRIPTION_LABEL = Component.translatable("mco.configure.world.description");
   private final RealmsConfigureWorldScreen configureWorldScreen;
   private final RealmsServer serverData;
   private EditBox descEdit;
   private EditBox nameEdit;

   public RealmsSettingsScreen(RealmsConfigureWorldScreen var1, RealmsServer var2) {
      super(Component.translatable("mco.configure.world.settings.title"));
      this.configureWorldScreen = var1;
      this.serverData = var2;
   }

   @Override
   public void init() {
      int var1 = this.width / 2 - 106;
      String var2 = this.serverData.state == RealmsServer.State.OPEN ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open";
      Button var3 = Button.builder(Component.translatable(var2), var1x -> {
         if (this.serverData.state == RealmsServer.State.OPEN) {
            MutableComponent var2x = Component.translatable("mco.configure.world.close.question.line1");
            MutableComponent var3x = Component.translatable("mco.configure.world.close.question.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen(var1xx -> {
               if (var1xx) {
                  this.configureWorldScreen.closeTheWorld(this);
               } else {
                  this.minecraft.setScreen(this);
               }
            }, RealmsLongConfirmationScreen.Type.INFO, var2x, var3x, true));
         } else {
            this.configureWorldScreen.openTheWorld(false, this);
         }
      }).bounds(this.width / 2 - 53, row(0), 106, 20).build();
      this.addRenderableWidget(var3);
      this.nameEdit = new EditBox(this.minecraft.font, var1, row(4), 212, 20, Component.translatable("mco.configure.world.name"));
      this.nameEdit.setMaxLength(32);
      this.nameEdit.setValue(this.serverData.getName());
      this.addRenderableWidget(this.nameEdit);
      this.setInitialFocus(this.nameEdit);
      this.descEdit = new EditBox(this.minecraft.font, var1, row(8), 212, 20, Component.translatable("mco.configure.world.description"));
      this.descEdit.setMaxLength(32);
      this.descEdit.setValue(this.serverData.getDescription());
      this.addRenderableWidget(this.descEdit);
      Button var4 = this.addRenderableWidget(
         Button.builder(Component.translatable("mco.configure.world.buttons.done"), var1x -> this.save()).bounds(var1 - 2, row(12), 106, 20).build()
      );
      this.nameEdit.setResponder(var1x -> var4.active = !Util.isBlank(var1x));
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, var1x -> this.onClose()).bounds(this.width / 2 + 2, row(12), 106, 20).build());
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.configureWorldScreen);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 17, -1);
      var1.drawString(this.font, NAME_LABEL, this.width / 2 - 106, row(3), -1, false);
      var1.drawString(this.font, DESCRIPTION_LABEL, this.width / 2 - 106, row(7), -1, false);
   }

   public void save() {
      this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
   }
}
