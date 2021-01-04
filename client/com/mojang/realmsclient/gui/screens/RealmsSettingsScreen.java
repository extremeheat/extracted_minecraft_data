package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

public class RealmsSettingsScreen extends RealmsScreen {
   private final RealmsConfigureWorldScreen configureWorldScreen;
   private final RealmsServer serverData;
   private final int COMPONENT_WIDTH = 212;
   private RealmsButton doneButton;
   private RealmsEditBox descEdit;
   private RealmsEditBox nameEdit;
   private RealmsLabel titleLabel;

   public RealmsSettingsScreen(RealmsConfigureWorldScreen var1, RealmsServer var2) {
      super();
      this.configureWorldScreen = var1;
      this.serverData = var2;
   }

   public void tick() {
      this.nameEdit.tick();
      this.descEdit.tick();
      this.doneButton.active(this.nameEdit.getValue() != null && !this.nameEdit.getValue().trim().isEmpty());
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      int var1 = this.width() / 2 - 106;
      this.buttonsAdd(this.doneButton = new RealmsButton(1, var1 - 2, RealmsConstants.row(12), 106, 20, getLocalizedString("mco.configure.world.buttons.done")) {
         public void onPress() {
            RealmsSettingsScreen.this.save();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 2, RealmsConstants.row(12), 106, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsSettingsScreen.this.configureWorldScreen);
         }
      });
      this.buttonsAdd(new RealmsButton(5, this.width() / 2 - 53, RealmsConstants.row(0), 106, 20, getLocalizedString(this.serverData.state.equals(RealmsServer.State.OPEN) ? "mco.configure.world.buttons.close" : "mco.configure.world.buttons.open")) {
         public void onPress() {
            if (RealmsSettingsScreen.this.serverData.state.equals(RealmsServer.State.OPEN)) {
               String var1 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line1");
               String var2 = RealmsScreen.getLocalizedString("mco.configure.world.close.question.line2");
               Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSettingsScreen.this, RealmsLongConfirmationScreen.Type.Info, var1, var2, true, 5));
            } else {
               RealmsSettingsScreen.this.configureWorldScreen.openTheWorld(false, RealmsSettingsScreen.this);
            }

         }
      });
      this.nameEdit = this.newEditBox(2, var1, RealmsConstants.row(4), 212, 20, getLocalizedString("mco.configure.world.name"));
      this.nameEdit.setMaxLength(32);
      if (this.serverData.getName() != null) {
         this.nameEdit.setValue(this.serverData.getName());
      }

      this.addWidget(this.nameEdit);
      this.focusOn(this.nameEdit);
      this.descEdit = this.newEditBox(3, var1, RealmsConstants.row(8), 212, 20, getLocalizedString("mco.configure.world.description"));
      this.descEdit.setMaxLength(32);
      if (this.serverData.getDescription() != null) {
         this.descEdit.setValue(this.serverData.getDescription());
      }

      this.addWidget(this.descEdit);
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.settings.title"), this.width() / 2, 17, 16777215));
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public void confirmResult(boolean var1, int var2) {
      switch(var2) {
      case 5:
         if (var1) {
            this.configureWorldScreen.closeTheWorld(this);
         } else {
            Realms.setScreen(this);
         }
      default:
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      switch(var1) {
      case 256:
         Realms.setScreen(this.configureWorldScreen);
         return true;
      default:
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.titleLabel.render(this);
      this.drawString(getLocalizedString("mco.configure.world.name"), this.width() / 2 - 106, RealmsConstants.row(3), 10526880);
      this.drawString(getLocalizedString("mco.configure.world.description"), this.width() / 2 - 106, RealmsConstants.row(7), 10526880);
      this.nameEdit.render(var1, var2, var3);
      this.descEdit.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }

   public void save() {
      this.configureWorldScreen.saveSettings(this.nameEdit.getValue(), this.descEdit.getValue());
   }
}
