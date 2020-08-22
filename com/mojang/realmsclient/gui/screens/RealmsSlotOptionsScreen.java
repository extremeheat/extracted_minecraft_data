package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSliderButton;

public class RealmsSlotOptionsScreen extends RealmsScreen {
   private RealmsEditBox nameEdit;
   protected final RealmsConfigureWorldScreen parent;
   private int column1_x;
   private int column_width;
   private int column2_x;
   private final RealmsWorldOptions options;
   private final RealmsServer.WorldType worldType;
   private final int activeSlot;
   private int difficultyIndex;
   private int gameModeIndex;
   private Boolean pvp;
   private Boolean spawnNPCs;
   private Boolean spawnAnimals;
   private Boolean spawnMonsters;
   private Integer spawnProtection;
   private Boolean commandBlocks;
   private Boolean forceGameMode;
   private RealmsButton pvpButton;
   private RealmsButton spawnAnimalsButton;
   private RealmsButton spawnMonstersButton;
   private RealmsButton spawnNPCsButton;
   private RealmsSliderButton spawnProtectionButton;
   private RealmsButton commandBlocksButton;
   private RealmsButton forceGameModeButton;
   String[] difficulties;
   String[] gameModes;
   String[][] gameModeHints;
   private RealmsLabel titleLabel;
   private RealmsLabel warningLabel;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen var1, RealmsWorldOptions var2, RealmsServer.WorldType var3, int var4) {
      this.parent = var1;
      this.options = var2;
      this.worldType = var3;
      this.activeSlot = var4;
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public void tick() {
      this.nameEdit.tick();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      switch(var1) {
      case 256:
         Realms.setScreen(this.parent);
         return true;
      default:
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void init() {
      this.column_width = 170;
      this.column1_x = this.width() / 2 - this.column_width * 2 / 2;
      this.column2_x = this.width() / 2 + 10;
      this.createDifficultyAndGameMode();
      this.difficultyIndex = this.options.difficulty;
      this.gameModeIndex = this.options.gameMode;
      if (this.worldType.equals(RealmsServer.WorldType.NORMAL)) {
         this.pvp = this.options.pvp;
         this.spawnProtection = this.options.spawnProtection;
         this.forceGameMode = this.options.forceGameMode;
         this.spawnAnimals = this.options.spawnAnimals;
         this.spawnMonsters = this.options.spawnMonsters;
         this.spawnNPCs = this.options.spawnNPCs;
         this.commandBlocks = this.options.commandBlocks;
      } else {
         String var1;
         if (this.worldType.equals(RealmsServer.WorldType.ADVENTUREMAP)) {
            var1 = getLocalizedString("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType.equals(RealmsServer.WorldType.INSPIRATION)) {
            var1 = getLocalizedString("mco.configure.world.edit.subscreen.inspiration");
         } else {
            var1 = getLocalizedString("mco.configure.world.edit.subscreen.experience");
         }

         this.warningLabel = new RealmsLabel(var1, this.width() / 2, 26, 16711680);
         this.pvp = true;
         this.spawnProtection = 0;
         this.forceGameMode = false;
         this.spawnAnimals = true;
         this.spawnMonsters = true;
         this.spawnNPCs = true;
         this.commandBlocks = true;
      }

      this.nameEdit = this.newEditBox(11, this.column1_x + 2, RealmsConstants.row(1), this.column_width - 4, 20, getLocalizedString("mco.configure.world.edit.slot.name"));
      this.nameEdit.setMaxLength(10);
      this.nameEdit.setValue(this.options.getSlotName(this.activeSlot));
      this.focusOn(this.nameEdit);
      this.buttonsAdd(this.pvpButton = new RealmsButton(4, this.column2_x, RealmsConstants.row(1), this.column_width, 20, this.pvpTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.pvp = !RealmsSlotOptionsScreen.this.pvp;
            this.setMessage(RealmsSlotOptionsScreen.this.pvpTitle());
         }
      });
      this.buttonsAdd(new RealmsButton(3, this.column1_x, RealmsConstants.row(3), this.column_width, 20, this.gameModeTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.gameModeIndex = (RealmsSlotOptionsScreen.this.gameModeIndex + 1) % RealmsSlotOptionsScreen.this.gameModes.length;
            this.setMessage(RealmsSlotOptionsScreen.this.gameModeTitle());
         }
      });
      this.buttonsAdd(this.spawnAnimalsButton = new RealmsButton(5, this.column2_x, RealmsConstants.row(3), this.column_width, 20, this.spawnAnimalsTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.spawnAnimals = !RealmsSlotOptionsScreen.this.spawnAnimals;
            this.setMessage(RealmsSlotOptionsScreen.this.spawnAnimalsTitle());
         }
      });
      this.buttonsAdd(new RealmsButton(2, this.column1_x, RealmsConstants.row(5), this.column_width, 20, this.difficultyTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.difficultyIndex = (RealmsSlotOptionsScreen.this.difficultyIndex + 1) % RealmsSlotOptionsScreen.this.difficulties.length;
            this.setMessage(RealmsSlotOptionsScreen.this.difficultyTitle());
            if (RealmsSlotOptionsScreen.this.worldType.equals(RealmsServer.WorldType.NORMAL)) {
               RealmsSlotOptionsScreen.this.spawnMonstersButton.active(RealmsSlotOptionsScreen.this.difficultyIndex != 0);
               RealmsSlotOptionsScreen.this.spawnMonstersButton.setMessage(RealmsSlotOptionsScreen.this.spawnMonstersTitle());
            }

         }
      });
      this.buttonsAdd(this.spawnMonstersButton = new RealmsButton(6, this.column2_x, RealmsConstants.row(5), this.column_width, 20, this.spawnMonstersTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.spawnMonsters = !RealmsSlotOptionsScreen.this.spawnMonsters;
            this.setMessage(RealmsSlotOptionsScreen.this.spawnMonstersTitle());
         }
      });
      this.buttonsAdd(this.spawnProtectionButton = new RealmsSlotOptionsScreen.SettingsSlider(8, this.column1_x, RealmsConstants.row(7), this.column_width, this.spawnProtection, 0.0F, 16.0F));
      this.buttonsAdd(this.spawnNPCsButton = new RealmsButton(7, this.column2_x, RealmsConstants.row(7), this.column_width, 20, this.spawnNPCsTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.spawnNPCs = !RealmsSlotOptionsScreen.this.spawnNPCs;
            this.setMessage(RealmsSlotOptionsScreen.this.spawnNPCsTitle());
         }
      });
      this.buttonsAdd(this.forceGameModeButton = new RealmsButton(10, this.column1_x, RealmsConstants.row(9), this.column_width, 20, this.forceGameModeTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.forceGameMode = !RealmsSlotOptionsScreen.this.forceGameMode;
            this.setMessage(RealmsSlotOptionsScreen.this.forceGameModeTitle());
         }
      });
      this.buttonsAdd(this.commandBlocksButton = new RealmsButton(9, this.column2_x, RealmsConstants.row(9), this.column_width, 20, this.commandBlocksTitle()) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.commandBlocks = !RealmsSlotOptionsScreen.this.commandBlocks;
            this.setMessage(RealmsSlotOptionsScreen.this.commandBlocksTitle());
         }
      });
      if (!this.worldType.equals(RealmsServer.WorldType.NORMAL)) {
         this.pvpButton.active(false);
         this.spawnAnimalsButton.active(false);
         this.spawnNPCsButton.active(false);
         this.spawnMonstersButton.active(false);
         this.spawnProtectionButton.active(false);
         this.commandBlocksButton.active(false);
         this.spawnProtectionButton.active(false);
         this.forceGameModeButton.active(false);
      }

      if (this.difficultyIndex == 0) {
         this.spawnMonstersButton.active(false);
      }

      this.buttonsAdd(new RealmsButton(1, this.column1_x, RealmsConstants.row(13), this.column_width, 20, getLocalizedString("mco.configure.world.buttons.done")) {
         public void onPress() {
            RealmsSlotOptionsScreen.this.saveSettings();
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.column2_x, RealmsConstants.row(13), this.column_width, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            Realms.setScreen(RealmsSlotOptionsScreen.this.parent);
         }
      });
      this.addWidget(this.nameEdit);
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.buttons.options"), this.width() / 2, 17, 16777215));
      if (this.warningLabel != null) {
         this.addWidget(this.warningLabel);
      }

      this.narrateLabels();
   }

   private void createDifficultyAndGameMode() {
      this.difficulties = new String[]{getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard")};
      this.gameModes = new String[]{getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure")};
      this.gameModeHints = new String[][]{{getLocalizedString("selectWorld.gameMode.survival.line1"), getLocalizedString("selectWorld.gameMode.survival.line2")}, {getLocalizedString("selectWorld.gameMode.creative.line1"), getLocalizedString("selectWorld.gameMode.creative.line2")}, {getLocalizedString("selectWorld.gameMode.adventure.line1"), getLocalizedString("selectWorld.gameMode.adventure.line2")}};
   }

   private String difficultyTitle() {
      String var1 = getLocalizedString("options.difficulty");
      return var1 + ": " + this.difficulties[this.difficultyIndex];
   }

   private String gameModeTitle() {
      String var1 = getLocalizedString("selectWorld.gameMode");
      return var1 + ": " + this.gameModes[this.gameModeIndex];
   }

   private String pvpTitle() {
      return getLocalizedString("mco.configure.world.pvp") + ": " + getLocalizedString(this.pvp ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String spawnAnimalsTitle() {
      return getLocalizedString("mco.configure.world.spawnAnimals") + ": " + getLocalizedString(this.spawnAnimals ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String spawnMonstersTitle() {
      return this.difficultyIndex == 0 ? getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString("mco.configure.world.off") : getLocalizedString("mco.configure.world.spawnMonsters") + ": " + getLocalizedString(this.spawnMonsters ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String spawnNPCsTitle() {
      return getLocalizedString("mco.configure.world.spawnNPCs") + ": " + getLocalizedString(this.spawnNPCs ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String commandBlocksTitle() {
      return getLocalizedString("mco.configure.world.commandBlocks") + ": " + getLocalizedString(this.commandBlocks ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   private String forceGameModeTitle() {
      return getLocalizedString("mco.configure.world.forceGameMode") + ": " + getLocalizedString(this.forceGameMode ? "mco.configure.world.on" : "mco.configure.world.off");
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      String var4 = getLocalizedString("mco.configure.world.edit.slot.name");
      this.drawString(var4, this.column1_x + this.column_width / 2 - this.fontWidth(var4) / 2, RealmsConstants.row(0) - 5, 16777215);
      this.titleLabel.render(this);
      if (this.warningLabel != null) {
         this.warningLabel.render(this);
      }

      this.nameEdit.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }

   private String getSlotName() {
      return this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot)) ? "" : this.nameEdit.getValue();
   }

   private void saveSettings() {
      if (!this.worldType.equals(RealmsServer.WorldType.ADVENTUREMAP) && !this.worldType.equals(RealmsServer.WorldType.EXPERIENCE) && !this.worldType.equals(RealmsServer.WorldType.INSPIRATION)) {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.forceGameMode, this.getSlotName()));
      } else {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficultyIndex, this.gameModeIndex, this.options.forceGameMode, this.getSlotName()));
      }

   }

   class SettingsSlider extends RealmsSliderButton {
      public SettingsSlider(int var2, int var3, int var4, int var5, int var6, float var7, float var8) {
         super(var2, var3, var4, var5, var6, (double)var7, (double)var8);
      }

      public void applyValue() {
         if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active()) {
            RealmsSlotOptionsScreen.this.spawnProtection = (int)this.toValue(this.getValue());
         }
      }

      public String getMessage() {
         return RealmsScreen.getLocalizedString("mco.configure.world.spawnProtection") + ": " + (RealmsSlotOptionsScreen.this.spawnProtection == 0 ? RealmsScreen.getLocalizedString("mco.configure.world.off") : RealmsSlotOptionsScreen.this.spawnProtection);
      }
   }
}
