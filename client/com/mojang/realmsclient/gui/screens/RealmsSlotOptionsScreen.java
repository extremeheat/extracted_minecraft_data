package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;

public class RealmsSlotOptionsScreen extends RealmsScreen {
   public static final Component[] DIFFICULTIES = new Component[]{new TranslatableComponent("options.difficulty.peaceful"), new TranslatableComponent("options.difficulty.easy"), new TranslatableComponent("options.difficulty.normal"), new TranslatableComponent("options.difficulty.hard")};
   public static final Component[] GAME_MODES = new Component[]{new TranslatableComponent("selectWorld.gameMode.survival"), new TranslatableComponent("selectWorld.gameMode.creative"), new TranslatableComponent("selectWorld.gameMode.adventure")};
   private static final Component TEXT_ON = new TranslatableComponent("mco.configure.world.on");
   private static final Component TEXT_OFF = new TranslatableComponent("mco.configure.world.off");
   private static final Component GAME_MODE_LABEL = new TranslatableComponent("selectWorld.gameMode");
   private static final Component NAME_LABEL = new TranslatableComponent("mco.configure.world.edit.slot.name");
   private EditBox nameEdit;
   protected final RealmsConfigureWorldScreen parent;
   private int column1X;
   private int columnWidth;
   private int column2X;
   private final RealmsWorldOptions options;
   private final RealmsServer.WorldType worldType;
   private final int activeSlot;
   private int difficulty;
   private int gameMode;
   private Boolean pvp;
   private Boolean spawnNPCs;
   private Boolean spawnAnimals;
   private Boolean spawnMonsters;
   private Integer spawnProtection;
   private Boolean commandBlocks;
   private Boolean forceGameMode;
   private Button pvpButton;
   private Button spawnAnimalsButton;
   private Button spawnMonstersButton;
   private Button spawnNPCsButton;
   private RealmsSlotOptionsScreen.SettingsSlider spawnProtectionButton;
   private Button commandBlocksButton;
   private Button forceGameModeButton;
   private RealmsLabel titleLabel;
   private RealmsLabel warningLabel;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen var1, RealmsWorldOptions var2, RealmsServer.WorldType var3, int var4) {
      super();
      this.parent = var1;
      this.options = var2;
      this.worldType = var3;
      this.activeSlot = var4;
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public void tick() {
      this.nameEdit.tick();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.parent);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void init() {
      this.columnWidth = 170;
      this.column1X = this.width / 2 - this.columnWidth;
      this.column2X = this.width / 2 + 10;
      this.difficulty = this.options.difficulty;
      this.gameMode = this.options.gameMode;
      if (this.worldType == RealmsServer.WorldType.NORMAL) {
         this.pvp = this.options.pvp;
         this.spawnProtection = this.options.spawnProtection;
         this.forceGameMode = this.options.forceGameMode;
         this.spawnAnimals = this.options.spawnAnimals;
         this.spawnMonsters = this.options.spawnMonsters;
         this.spawnNPCs = this.options.spawnNPCs;
         this.commandBlocks = this.options.commandBlocks;
      } else {
         TranslatableComponent var1;
         if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
            var1 = new TranslatableComponent("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
            var1 = new TranslatableComponent("mco.configure.world.edit.subscreen.inspiration");
         } else {
            var1 = new TranslatableComponent("mco.configure.world.edit.subscreen.experience");
         }

         this.warningLabel = new RealmsLabel(var1, this.width / 2, 26, 16711680);
         this.pvp = true;
         this.spawnProtection = 0;
         this.forceGameMode = false;
         this.spawnAnimals = true;
         this.spawnMonsters = true;
         this.spawnNPCs = true;
         this.commandBlocks = true;
      }

      this.nameEdit = new EditBox(this.minecraft.font, this.column1X + 2, row(1), this.columnWidth - 4, 20, (EditBox)null, new TranslatableComponent("mco.configure.world.edit.slot.name"));
      this.nameEdit.setMaxLength(10);
      this.nameEdit.setValue(this.options.getSlotName(this.activeSlot));
      this.magicalSpecialHackyFocus(this.nameEdit);
      this.pvpButton = (Button)this.addButton(new Button(this.column2X, row(1), this.columnWidth, 20, this.pvpTitle(), (var1x) -> {
         this.pvp = !this.pvp;
         var1x.setMessage(this.pvpTitle());
      }));
      this.addButton(new Button(this.column1X, row(3), this.columnWidth, 20, this.gameModeTitle(), (var1x) -> {
         this.gameMode = (this.gameMode + 1) % GAME_MODES.length;
         var1x.setMessage(this.gameModeTitle());
      }));
      this.spawnAnimalsButton = (Button)this.addButton(new Button(this.column2X, row(3), this.columnWidth, 20, this.spawnAnimalsTitle(), (var1x) -> {
         this.spawnAnimals = !this.spawnAnimals;
         var1x.setMessage(this.spawnAnimalsTitle());
      }));
      this.addButton(new Button(this.column1X, row(5), this.columnWidth, 20, this.difficultyTitle(), (var1x) -> {
         this.difficulty = (this.difficulty + 1) % DIFFICULTIES.length;
         var1x.setMessage(this.difficultyTitle());
         if (this.worldType == RealmsServer.WorldType.NORMAL) {
            this.spawnMonstersButton.active = this.difficulty != 0;
            this.spawnMonstersButton.setMessage(this.spawnMonstersTitle());
         }

      }));
      this.spawnMonstersButton = (Button)this.addButton(new Button(this.column2X, row(5), this.columnWidth, 20, this.spawnMonstersTitle(), (var1x) -> {
         this.spawnMonsters = !this.spawnMonsters;
         var1x.setMessage(this.spawnMonstersTitle());
      }));
      this.spawnProtectionButton = (RealmsSlotOptionsScreen.SettingsSlider)this.addButton(new RealmsSlotOptionsScreen.SettingsSlider(this.column1X, row(7), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      this.spawnNPCsButton = (Button)this.addButton(new Button(this.column2X, row(7), this.columnWidth, 20, this.spawnNPCsTitle(), (var1x) -> {
         this.spawnNPCs = !this.spawnNPCs;
         var1x.setMessage(this.spawnNPCsTitle());
      }));
      this.forceGameModeButton = (Button)this.addButton(new Button(this.column1X, row(9), this.columnWidth, 20, this.forceGameModeTitle(), (var1x) -> {
         this.forceGameMode = !this.forceGameMode;
         var1x.setMessage(this.forceGameModeTitle());
      }));
      this.commandBlocksButton = (Button)this.addButton(new Button(this.column2X, row(9), this.columnWidth, 20, this.commandBlocksTitle(), (var1x) -> {
         this.commandBlocks = !this.commandBlocks;
         var1x.setMessage(this.commandBlocksTitle());
      }));
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         this.pvpButton.active = false;
         this.spawnAnimalsButton.active = false;
         this.spawnNPCsButton.active = false;
         this.spawnMonstersButton.active = false;
         this.spawnProtectionButton.active = false;
         this.commandBlocksButton.active = false;
         this.forceGameModeButton.active = false;
      }

      if (this.difficulty == 0) {
         this.spawnMonstersButton.active = false;
      }

      this.addButton(new Button(this.column1X, row(13), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.buttons.done"), (var1x) -> {
         this.saveSettings();
      }));
      this.addButton(new Button(this.column2X, row(13), this.columnWidth, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.addWidget(this.nameEdit);
      this.titleLabel = (RealmsLabel)this.addWidget(new RealmsLabel(new TranslatableComponent("mco.configure.world.buttons.options"), this.width / 2, 17, 16777215));
      if (this.warningLabel != null) {
         this.addWidget(this.warningLabel);
      }

      this.narrateLabels();
   }

   private Component difficultyTitle() {
      return (new TranslatableComponent("options.difficulty")).append(": ").append(DIFFICULTIES[this.difficulty]);
   }

   private Component gameModeTitle() {
      return new TranslatableComponent("options.generic_value", new Object[]{GAME_MODE_LABEL, GAME_MODES[this.gameMode]});
   }

   private Component pvpTitle() {
      return (new TranslatableComponent("mco.configure.world.pvp")).append(": ").append(getOnOff(this.pvp));
   }

   private Component spawnAnimalsTitle() {
      return (new TranslatableComponent("mco.configure.world.spawnAnimals")).append(": ").append(getOnOff(this.spawnAnimals));
   }

   private Component spawnMonstersTitle() {
      return this.difficulty == 0 ? (new TranslatableComponent("mco.configure.world.spawnMonsters")).append(": ").append((Component)(new TranslatableComponent("mco.configure.world.off"))) : (new TranslatableComponent("mco.configure.world.spawnMonsters")).append(": ").append(getOnOff(this.spawnMonsters));
   }

   private Component spawnNPCsTitle() {
      return (new TranslatableComponent("mco.configure.world.spawnNPCs")).append(": ").append(getOnOff(this.spawnNPCs));
   }

   private Component commandBlocksTitle() {
      return (new TranslatableComponent("mco.configure.world.commandBlocks")).append(": ").append(getOnOff(this.commandBlocks));
   }

   private Component forceGameModeTitle() {
      return (new TranslatableComponent("mco.configure.world.forceGameMode")).append(": ").append(getOnOff(this.forceGameMode));
   }

   private static Component getOnOff(boolean var0) {
      return var0 ? TEXT_ON : TEXT_OFF;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.font.draw(var1, NAME_LABEL, (float)(this.column1X + this.columnWidth / 2 - this.font.width((FormattedText)NAME_LABEL) / 2), (float)(row(0) - 5), 16777215);
      this.titleLabel.render(this, var1);
      if (this.warningLabel != null) {
         this.warningLabel.render(this, var1);
      }

      this.nameEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }

   private String getSlotName() {
      return this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot)) ? "" : this.nameEdit.getValue();
   }

   private void saveSettings() {
      if (this.worldType != RealmsServer.WorldType.ADVENTUREMAP && this.worldType != RealmsServer.WorldType.EXPERIENCE && this.worldType != RealmsServer.WorldType.INSPIRATION) {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, this.difficulty, this.gameMode, this.forceGameMode, this.getSlotName()));
      } else {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, this.difficulty, this.gameMode, this.options.forceGameMode, this.getSlotName()));
      }

   }

   class SettingsSlider extends AbstractSliderButton {
      private final double minValue;
      private final double maxValue;

      public SettingsSlider(int var2, int var3, int var4, int var5, float var6, float var7) {
         super(var2, var3, var4, 20, TextComponent.EMPTY, 0.0D);
         this.minValue = (double)var6;
         this.maxValue = (double)var7;
         this.value = (double)((Mth.clamp((float)var5, var6, var7) - var6) / (var7 - var6));
         this.updateMessage();
      }

      public void applyValue() {
         if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
            RealmsSlotOptionsScreen.this.spawnProtection = (int)Mth.lerp(Mth.clamp(this.value, 0.0D, 1.0D), this.minValue, this.maxValue);
         }
      }

      protected void updateMessage() {
         this.setMessage((new TranslatableComponent("mco.configure.world.spawnProtection")).append(": ").append((Component)(RealmsSlotOptionsScreen.this.spawnProtection == 0 ? new TranslatableComponent("mco.configure.world.off") : new TextComponent(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection)))));
      }

      public void onClick(double var1, double var3) {
      }

      public void onRelease(double var1, double var3) {
      }
   }
}
