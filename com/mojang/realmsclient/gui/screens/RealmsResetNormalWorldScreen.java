package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;

public class RealmsResetNormalWorldScreen extends RealmsScreen {
   private final RealmsResetWorldScreen lastScreen;
   private RealmsLabel titleLabel;
   private RealmsEditBox seedEdit;
   private Boolean generateStructures;
   private Integer levelTypeIndex;
   String[] levelTypes;
   private final int BUTTON_CANCEL_ID;
   private final int BUTTON_RESET_ID;
   private final int SEED_EDIT_BOX;
   private RealmsButton resetButton;
   private RealmsButton levelTypeButton;
   private RealmsButton generateStructuresButton;
   private String buttonTitle;

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen var1) {
      this.generateStructures = true;
      this.levelTypeIndex = 0;
      this.BUTTON_CANCEL_ID = 0;
      this.BUTTON_RESET_ID = 1;
      this.SEED_EDIT_BOX = 4;
      this.buttonTitle = getLocalizedString("mco.backup.button.reset");
      this.lastScreen = var1;
   }

   public RealmsResetNormalWorldScreen(RealmsResetWorldScreen var1, String var2) {
      this(var1);
      this.buttonTitle = var2;
   }

   public void tick() {
      this.seedEdit.tick();
      super.tick();
   }

   public void init() {
      this.levelTypes = new String[]{getLocalizedString("generator.default"), getLocalizedString("generator.flat"), getLocalizedString("generator.largeBiomes"), getLocalizedString("generator.amplified")};
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 + 8, RealmsConstants.row(12), 97, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsResetNormalWorldScreen.this.lastScreen);
         }
      });
      this.buttonsAdd(this.resetButton = new RealmsButton(1, this.width() / 2 - 102, RealmsConstants.row(12), 97, 20, this.buttonTitle) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.onReset();
         }
      });
      this.seedEdit = this.newEditBox(4, this.width() / 2 - 100, RealmsConstants.row(2), 200, 20, getLocalizedString("mco.reset.world.seed"));
      this.seedEdit.setMaxLength(32);
      this.seedEdit.setValue("");
      this.addWidget(this.seedEdit);
      this.focusOn(this.seedEdit);
      this.buttonsAdd(this.levelTypeButton = new RealmsButton(2, this.width() / 2 - 102, RealmsConstants.row(4), 205, 20, this.levelTypeTitle()) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.levelTypeIndex = (RealmsResetNormalWorldScreen.this.levelTypeIndex + 1) % RealmsResetNormalWorldScreen.this.levelTypes.length;
            this.setMessage(RealmsResetNormalWorldScreen.this.levelTypeTitle());
         }
      });
      this.buttonsAdd(this.generateStructuresButton = new RealmsButton(3, this.width() / 2 - 102, RealmsConstants.row(6) - 2, 205, 20, this.generateStructuresTitle()) {
         public void onPress() {
            RealmsResetNormalWorldScreen.this.generateStructures = !RealmsResetNormalWorldScreen.this.generateStructures;
            this.setMessage(RealmsResetNormalWorldScreen.this.generateStructuresTitle());
         }
      });
      this.titleLabel = new RealmsLabel(getLocalizedString("mco.reset.world.generate"), this.width() / 2, 17, 16777215);
      this.addWidget(this.titleLabel);
      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void onReset() {
      this.lastScreen.resetWorld(new RealmsResetWorldScreen.ResetWorldInfo(this.seedEdit.getValue(), this.levelTypeIndex, this.generateStructures));
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.titleLabel.render(this);
      this.drawString(getLocalizedString("mco.reset.world.seed"), this.width() / 2 - 100, RealmsConstants.row(1), 10526880);
      this.seedEdit.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }

   private String levelTypeTitle() {
      String var1 = getLocalizedString("selectWorld.mapType");
      return var1 + " " + this.levelTypes[this.levelTypeIndex];
   }

   private String generateStructuresTitle() {
      return getLocalizedString("selectWorld.mapFeatures") + " " + getLocalizedString(this.generateStructures ? "mco.configure.world.on" : "mco.configure.world.off");
   }
}
