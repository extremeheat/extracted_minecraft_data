package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
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
   protected final RealmsConfigureWorldScreen parentScreen;
   private int column1X;
   private int columnWidth;
   private final RealmsWorldOptions options;
   private final RealmsServer.WorldType worldType;
   private Difficulty difficulty;
   private GameType gameMode;
   private final String defaultSlotName;
   private String worldName;
   private boolean pvp;
   private boolean spawnMonsters;
   int spawnProtection;
   private boolean commandBlocks;
   private boolean forceGameMode;
   SettingsSlider spawnProtectionButton;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen var1, RealmsWorldOptions var2, RealmsServer.WorldType var3, int var4) {
      super(Component.translatable("mco.configure.world.buttons.options"));
      this.parentScreen = var1;
      this.options = var2;
      this.worldType = var3;
      this.difficulty = (Difficulty)findByIndex(DIFFICULTIES, var2.difficulty, 2);
      this.gameMode = (GameType)findByIndex(GAME_MODES, var2.gameMode, 0);
      this.defaultSlotName = var2.getDefaultSlotName(var4);
      this.setWorldName(var2.getSlotName(var4));
      if (var3 == RealmsServer.WorldType.NORMAL) {
         this.pvp = var2.pvp;
         this.spawnProtection = var2.spawnProtection;
         this.forceGameMode = var2.forceGameMode;
         this.spawnMonsters = var2.spawnMonsters;
         this.commandBlocks = var2.commandBlocks;
      } else {
         this.pvp = true;
         this.spawnProtection = 0;
         this.forceGameMode = false;
         this.spawnMonsters = true;
         this.commandBlocks = true;
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.parentScreen);
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
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         MutableComponent var2;
         if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
            var2 = Component.translatable("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
            var2 = Component.translatable("mco.configure.world.edit.subscreen.inspiration");
         } else {
            var2 = Component.translatable("mco.configure.world.edit.subscreen.experience");
         }

         this.addLabel(new RealmsLabel(var2, this.width / 2, 26, 16711680));
      }

      this.nameEdit = (EditBox)this.addWidget(new EditBox(this.minecraft.font, this.column1X, row(1), this.columnWidth, 20, (EditBox)null, Component.translatable("mco.configure.world.edit.slot.name")));
      this.nameEdit.setMaxLength(10);
      this.nameEdit.setValue(this.worldName);
      this.nameEdit.setResponder(this::setWorldName);
      CycleButton var7 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.pvp).create(var1, row(1), this.columnWidth, 20, Component.translatable("mco.configure.world.pvp"), (var1x, var2x) -> {
         this.pvp = var2x;
      }));
      this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).withValues((Collection)GAME_MODES).withInitialValue(this.gameMode).create(this.column1X, row(3), this.columnWidth, 20, Component.translatable("selectWorld.gameMode"), (var1x, var2x) -> {
         this.gameMode = var2x;
      }));
      this.spawnProtectionButton = (SettingsSlider)this.addRenderableWidget(new SettingsSlider(var1, row(3), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      MutableComponent var3 = Component.translatable("mco.configure.world.spawn_toggle.message");
      CycleButton var4 = CycleButton.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).create(var1, row(5), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnMonsters"), this.confirmDangerousOption(var3, (var1x) -> {
         this.spawnMonsters = var1x;
      }));
      this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName).withValues((Collection)DIFFICULTIES).withInitialValue(this.difficulty).create(this.column1X, row(5), this.columnWidth, 20, Component.translatable("options.difficulty"), (var2x, var3x) -> {
         this.difficulty = var3x;
         if (this.worldType == RealmsServer.WorldType.NORMAL) {
            boolean var4x = this.difficulty != Difficulty.PEACEFUL;
            var4.active = var4x;
            var4.setValue(var4x && this.spawnMonsters);
         }

      }));
      this.addRenderableWidget(var4);
      CycleButton var5 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.forceGameMode).create(this.column1X, row(7), this.columnWidth, 20, Component.translatable("mco.configure.world.forceGameMode"), (var1x, var2x) -> {
         this.forceGameMode = var2x;
      }));
      CycleButton var6 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.commandBlocks).create(var1, row(7), this.columnWidth, 20, Component.translatable("mco.configure.world.commandBlocks"), (var1x, var2x) -> {
         this.commandBlocks = var2x;
      }));
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         var7.active = false;
         var4.active = false;
         this.spawnProtectionButton.active = false;
         var6.active = false;
         var5.active = false;
      }

      if (this.difficulty == Difficulty.PEACEFUL) {
         var4.active = false;
      }

      this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.done"), (var1x) -> {
         this.saveSettings();
      }).bounds(this.column1X, row(13), this.columnWidth, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> {
         this.onClose();
      }).bounds(var1, row(13), this.columnWidth, 20).build());
   }

   private CycleButton.OnValueChange<Boolean> confirmDangerousOption(Component var1, Consumer<Boolean> var2) {
      return (var3, var4) -> {
         if (var4) {
            var2.accept(true);
         } else {
            this.minecraft.setScreen(RealmsPopups.warningPopupScreen(this, var1, (var1x) -> {
               var2.accept(false);
               var1x.onClose();
            }));
         }

      };
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title, this.width / 2, 17, -1);
      var1.drawString(this.font, (Component)NAME_LABEL, this.column1X + this.columnWidth / 2 - this.font.width((FormattedText)NAME_LABEL) / 2, row(0) - 5, -1, false);
      this.nameEdit.render(var1, var2, var3, var4);
   }

   private void setWorldName(String var1) {
      if (var1.equals(this.defaultSlotName)) {
         this.worldName = "";
      } else {
         this.worldName = var1;
      }

   }

   private void saveSettings() {
      int var1 = findIndex(DIFFICULTIES, this.difficulty, 2);
      int var2 = findIndex(GAME_MODES, this.gameMode, 0);
      if (this.worldType != RealmsServer.WorldType.ADVENTUREMAP && this.worldType != RealmsServer.WorldType.EXPERIENCE && this.worldType != RealmsServer.WorldType.INSPIRATION) {
         boolean var3 = this.worldType == RealmsServer.WorldType.NORMAL && this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters;
         this.parentScreen.saveSlotSettings(new RealmsWorldOptions(this.pvp, var3, this.spawnProtection, this.commandBlocks, var1, var2, this.options.hardcore, this.forceGameMode, this.worldName, this.options.version, this.options.compatibility));
      } else {
         this.parentScreen.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnMonsters, this.options.spawnProtection, this.options.commandBlocks, var1, var2, this.options.hardcore, this.options.forceGameMode, this.worldName, this.options.version, this.options.compatibility));
      }

   }

   static {
      DIFFICULTIES = ImmutableList.of(Difficulty.PEACEFUL, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD);
      GAME_MODES = ImmutableList.of(GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE);
      NAME_LABEL = Component.translatable("mco.configure.world.edit.slot.name");
      SPAWN_PROTECTION_TEXT = Component.translatable("mco.configure.world.spawnProtection");
   }

   private class SettingsSlider extends AbstractSliderButton {
      private final double minValue;
      private final double maxValue;

      public SettingsSlider(final int var2, final int var3, final int var4, final int var5, final float var6, final float var7) {
         super(var2, var3, var4, 20, CommonComponents.EMPTY, 0.0);
         this.minValue = (double)var6;
         this.maxValue = (double)var7;
         this.value = (double)((Mth.clamp((float)var5, var6, var7) - var6) / (var7 - var6));
         this.updateMessage();
      }

      public void applyValue() {
         if (RealmsSlotOptionsScreen.this.spawnProtectionButton.active) {
            RealmsSlotOptionsScreen.this.spawnProtection = (int)Mth.lerp(Mth.clamp(this.value, 0.0, 1.0), this.minValue, this.maxValue);
         }
      }

      protected void updateMessage() {
         this.setMessage(CommonComponents.optionNameValue(RealmsSlotOptionsScreen.SPAWN_PROTECTION_TEXT, (Component)(RealmsSlotOptionsScreen.this.spawnProtection == 0 ? CommonComponents.OPTION_OFF : Component.literal(String.valueOf(RealmsSlotOptionsScreen.this.spawnProtection)))));
      }
   }
}
