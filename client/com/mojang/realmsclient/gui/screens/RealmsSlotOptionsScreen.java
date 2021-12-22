package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.List;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class RealmsSlotOptionsScreen extends RealmsScreen {
   private static final int DEFAULT_DIFFICULTY = 2;
   public static final List<Difficulty> DIFFICULTIES;
   private static final int DEFAULT_GAME_MODE = 0;
   public static final List<GameType> GAME_MODES;
   private static final Component NAME_LABEL;
   static final Component SPAWN_PROTECTION_TEXT;
   private EditBox nameEdit;
   protected final RealmsConfigureWorldScreen parent;
   private int column1X;
   private int columnWidth;
   private final RealmsWorldOptions options;
   private final RealmsServer.WorldType worldType;
   private final int activeSlot;
   private Difficulty difficulty;
   private GameType gameMode;
   private boolean pvp;
   private boolean spawnNPCs;
   private boolean spawnAnimals;
   private boolean spawnMonsters;
   int spawnProtection;
   private boolean commandBlocks;
   private boolean forceGameMode;
   RealmsSlotOptionsScreen.SettingsSlider spawnProtectionButton;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen var1, RealmsWorldOptions var2, RealmsServer.WorldType var3, int var4) {
      super(new TranslatableComponent("mco.configure.world.buttons.options"));
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

   private static <T> T findByIndex(List<T> var0, int var1, int var2) {
      try {
         return var0.get(var1);
      } catch (IndexOutOfBoundsException var4) {
         return var0.get(var2);
      }
   }

   private static <T> int findIndex(List<T> var0, T var1, int var2) {
      int var3 = var0.indexOf(var1);
      return var3 == -1 ? var2 : var3;
   }

   public void init() {
      this.columnWidth = 170;
      this.column1X = this.width / 2 - this.columnWidth;
      int var1 = this.width / 2 + 10;
      this.difficulty = (Difficulty)findByIndex(DIFFICULTIES, this.options.difficulty, 2);
      this.gameMode = (GameType)findByIndex(GAME_MODES, this.options.gameMode, 0);
      if (this.worldType == RealmsServer.WorldType.NORMAL) {
         this.pvp = this.options.pvp;
         this.spawnProtection = this.options.spawnProtection;
         this.forceGameMode = this.options.forceGameMode;
         this.spawnAnimals = this.options.spawnAnimals;
         this.spawnMonsters = this.options.spawnMonsters;
         this.spawnNPCs = this.options.spawnNPCs;
         this.commandBlocks = this.options.commandBlocks;
      } else {
         TranslatableComponent var2;
         if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
            var2 = new TranslatableComponent("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
            var2 = new TranslatableComponent("mco.configure.world.edit.subscreen.inspiration");
         } else {
            var2 = new TranslatableComponent("mco.configure.world.edit.subscreen.experience");
         }

         this.addLabel(new RealmsLabel(var2, this.width / 2, 26, 16711680));
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
      CycleButton var8 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.pvp).create(var1, row(1), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.pvp"), (var1x, var2x) -> {
         this.pvp = var2x;
      }));
      this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).withValues(GAME_MODES).withInitialValue(this.gameMode).create(this.column1X, row(3), this.columnWidth, 20, new TranslatableComponent("selectWorld.gameMode"), (var1x, var2x) -> {
         this.gameMode = var2x;
      }));
      CycleButton var3 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnAnimals).create(var1, row(3), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.spawnAnimals"), (var1x, var2x) -> {
         this.spawnAnimals = var2x;
      }));
      CycleButton var4 = CycleButton.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).create(var1, row(5), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.spawnMonsters"), (var1x, var2x) -> {
         this.spawnMonsters = var2x;
      });
      this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName).withValues(DIFFICULTIES).withInitialValue(this.difficulty).create(this.column1X, row(5), this.columnWidth, 20, new TranslatableComponent("options.difficulty"), (var2x, var3x) -> {
         this.difficulty = var3x;
         if (this.worldType == RealmsServer.WorldType.NORMAL) {
            boolean var4x = this.difficulty != Difficulty.PEACEFUL;
            var4.active = var4x;
            var4.setValue(var4x && this.spawnMonsters);
         }

      }));
      this.addRenderableWidget(var4);
      this.spawnProtectionButton = (RealmsSlotOptionsScreen.SettingsSlider)this.addRenderableWidget(new RealmsSlotOptionsScreen.SettingsSlider(this.column1X, row(7), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      CycleButton var5 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnNPCs).create(var1, row(7), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.spawnNPCs"), (var1x, var2x) -> {
         this.spawnNPCs = var2x;
      }));
      CycleButton var6 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.forceGameMode).create(this.column1X, row(9), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.forceGameMode"), (var1x, var2x) -> {
         this.forceGameMode = var2x;
      }));
      CycleButton var7 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.commandBlocks).create(var1, row(9), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.commandBlocks"), (var1x, var2x) -> {
         this.commandBlocks = var2x;
      }));
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         var8.active = false;
         var3.active = false;
         var5.active = false;
         var4.active = false;
         this.spawnProtectionButton.active = false;
         var7.active = false;
         var6.active = false;
      }

      if (this.difficulty == Difficulty.PEACEFUL) {
         var4.active = false;
      }

      this.addRenderableWidget(new Button(this.column1X, row(13), this.columnWidth, 20, new TranslatableComponent("mco.configure.world.buttons.done"), (var1x) -> {
         this.saveSettings();
      }));
      this.addRenderableWidget(new Button(var1, row(13), this.columnWidth, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.addWidget(this.nameEdit);
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 17, 16777215);
      this.font.draw(var1, NAME_LABEL, (float)(this.column1X + this.columnWidth / 2 - this.font.width((FormattedText)NAME_LABEL) / 2), (float)(row(0) - 5), 16777215);
      this.nameEdit.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }

   private String getSlotName() {
      return this.nameEdit.getValue().equals(this.options.getDefaultSlotName(this.activeSlot)) ? "" : this.nameEdit.getValue();
   }

   private void saveSettings() {
      int var1 = findIndex(DIFFICULTIES, this.difficulty, 2);
      int var2 = findIndex(GAME_MODES, this.gameMode, 0);
      if (this.worldType != RealmsServer.WorldType.ADVENTUREMAP && this.worldType != RealmsServer.WorldType.EXPERIENCE && this.worldType != RealmsServer.WorldType.INSPIRATION) {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, this.spawnMonsters, this.spawnNPCs, this.spawnProtection, this.commandBlocks, var1, var2, this.forceGameMode, this.getSlotName()));
      } else {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, var1, var2, this.options.forceGameMode, this.getSlotName()));
      }

   }

   static {
      DIFFICULTIES = ImmutableList.of(Difficulty.PEACEFUL, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD);
      GAME_MODES = ImmutableList.of(GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE);
      NAME_LABEL = new TranslatableComponent("mco.configure.world.edit.slot.name");
      SPAWN_PROTECTION_TEXT = new TranslatableComponent("mco.configure.world.spawnProtection");
   }

   private class SettingsSlider extends AbstractSliderButton {
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
         this.setMessage(CommonComponents.optionNameValue(RealmsSlotOptionsScreen.SPAWN_PROTECTION_TEXT, (Component)(RealmsSlotOptionsScreen.this.spawnProtection == 0 ? CommonComponents.OPTION_OFF : new TextComponent(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection)))));
      }

      public void onClick(double var1, double var3) {
      }

      public void onRelease(double var1, double var3) {
      }
   }
}
