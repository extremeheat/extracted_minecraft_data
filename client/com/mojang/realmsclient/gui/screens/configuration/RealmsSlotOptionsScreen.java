package com.mojang.realmsclient.gui.screens.configuration;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
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
   private static final Component SPAWN_PROTECTION_TEXT;
   private EditBox nameEdit;
   protected final RealmsConfigureWorldScreen parentScreen;
   private int column1X;
   private int columnWidth;
   private final RealmsSlot slot;
   private final RealmsServer.WorldType worldType;
   private Difficulty difficulty;
   private GameType gameMode;
   private final String defaultSlotName;
   private String worldName;
   private int spawnProtection;
   private boolean forceGameMode;
   private SettingsSlider spawnProtectionButton;

   public RealmsSlotOptionsScreen(final RealmsConfigureWorldScreen configureWorldScreen, final RealmsSlot slot, final RealmsServer.WorldType worldType, final int activeSlot) {
      super(Component.translatable("mco.configure.world.buttons.options"));
      this.parentScreen = configureWorldScreen;
      this.slot = slot;
      this.worldType = worldType;
      this.difficulty = (Difficulty)findByIndex(DIFFICULTIES, slot.options.difficulty, 2);
      this.gameMode = (GameType)findByIndex(GAME_MODES, slot.options.gameMode, 0);
      this.defaultSlotName = slot.options.getDefaultSlotName(activeSlot);
      this.setWorldName(slot.options.getSlotName(activeSlot));
      if (worldType == RealmsServer.WorldType.NORMAL) {
         this.spawnProtection = slot.options.spawnProtection;
         this.forceGameMode = slot.options.forceGameMode;
      } else {
         this.spawnProtection = 0;
         this.forceGameMode = false;
      }

   }

   public void onClose() {
      this.minecraft.setScreen(this.parentScreen);
   }

   private static <T> T findByIndex(final List<T> values, final int index, final int defaultIndex) {
      try {
         return (T)values.get(index);
      } catch (IndexOutOfBoundsException var4) {
         return (T)values.get(defaultIndex);
      }
   }

   private static <T> int findIndex(final List<T> values, final T value, final int defaultIndex) {
      int result = values.indexOf(value);
      return result == -1 ? defaultIndex : result;
   }

   public void init() {
      this.columnWidth = 170;
      this.column1X = this.width / 2 - this.columnWidth;
      int column2X = this.width / 2 + 10;
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         Component warning;
         if (this.worldType == RealmsServer.WorldType.ADVENTUREMAP) {
            warning = Component.translatable("mco.configure.world.edit.subscreen.adventuremap");
         } else if (this.worldType == RealmsServer.WorldType.INSPIRATION) {
            warning = Component.translatable("mco.configure.world.edit.subscreen.inspiration");
         } else {
            warning = Component.translatable("mco.configure.world.edit.subscreen.experience");
         }

         this.addLabel(new RealmsLabel(warning, this.width / 2, 26, -65536));
      }

      this.nameEdit = (EditBox)this.addWidget(new EditBox(this.minecraft.font, this.column1X, row(1), this.columnWidth, 20, (EditBox)null, Component.translatable("mco.configure.world.edit.slot.name")));
      this.nameEdit.setValue(this.worldName);
      this.nameEdit.setResponder(this::setWorldName);
      CycleButton<Difficulty> difficultyCycleButton = (CycleButton)this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName, this.difficulty).withValues(DIFFICULTIES).create(column2X, row(1), this.columnWidth, 20, Component.translatable("options.difficulty"), (button, value) -> this.difficulty = value));
      CycleButton<GameType> gameTypeCycleButton = (CycleButton)this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName, this.gameMode).withValues(GAME_MODES).create(this.column1X, row(3), this.columnWidth, 20, Component.translatable("selectWorld.gameMode"), (button, value) -> this.gameMode = value));
      CycleButton<Boolean> forceGameModeButton = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.forceGameMode).create(column2X, row(3), this.columnWidth, 20, Component.translatable("mco.configure.world.forceGameMode"), (button, value) -> this.forceGameMode = value));
      this.spawnProtectionButton = (SettingsSlider)this.addRenderableWidget(new SettingsSlider(this.column1X, row(5), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         this.spawnProtectionButton.active = false;
         forceGameModeButton.active = false;
      }

      if (this.slot.isHardcore()) {
         difficultyCycleButton.active = false;
         gameTypeCycleButton.active = false;
         forceGameModeButton.active = false;
      }

      this.addRenderableWidget(Button.builder(Component.translatable("mco.configure.world.buttons.done"), (button) -> this.saveSettings()).bounds(this.column1X, row(13), this.columnWidth, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> this.onClose()).bounds(column2X, row(13), this.columnWidth, 20).build());
   }

   private CycleButton.OnValueChange<Boolean> confirmDangerousOption(final Component message, final Consumer<Boolean> setter) {
      return (button, value) -> {
         if (value) {
            setter.accept(true);
         } else {
            this.minecraft.setScreen(RealmsPopups.warningPopupScreen(this, message, (popupScreen) -> {
               setter.accept(false);
               popupScreen.onClose();
            }));
         }

      };
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(this.getTitle(), this.createLabelNarration());
   }

   public void render(final GuiGraphics graphics, final int xm, final int ym, final float a) {
      super.render(graphics, xm, ym, a);
      graphics.drawCenteredString(this.font, (Component)this.title, this.width / 2, 17, -1);
      graphics.drawString(this.font, (Component)NAME_LABEL, this.column1X + this.columnWidth / 2 - this.font.width((FormattedText)NAME_LABEL) / 2, row(0) - 5, -1);
      this.nameEdit.render(graphics, xm, ym, a);
   }

   private void setWorldName(final String value) {
      if (value.equals(this.defaultSlotName)) {
         this.worldName = "";
      } else {
         this.worldName = value;
      }

   }

   private void saveSettings() {
      int difficultyId = findIndex(DIFFICULTIES, this.difficulty, 2);
      int gameModeId = findIndex(GAME_MODES, this.gameMode, 0);
      if (this.worldType != RealmsServer.WorldType.ADVENTUREMAP && this.worldType != RealmsServer.WorldType.EXPERIENCE && this.worldType != RealmsServer.WorldType.INSPIRATION) {
         this.parentScreen.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.spawnProtection, difficultyId, gameModeId, this.forceGameMode, this.worldName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
      } else {
         this.parentScreen.saveSlotSettings(new RealmsSlot(this.slot.slotId, new RealmsWorldOptions(this.slot.options.spawnProtection, difficultyId, gameModeId, this.slot.options.forceGameMode, this.worldName, this.slot.options.version, this.slot.options.compatibility), this.slot.settings));
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

      public SettingsSlider(final int x, final int y, final int width, final int currentValue, final float minValue, final float maxValue) {
         Objects.requireNonNull(RealmsSlotOptionsScreen.this);
         super(x, y, width, 20, CommonComponents.EMPTY, 0.0);
         this.minValue = (double)minValue;
         this.maxValue = (double)maxValue;
         this.value = (double)((Mth.clamp((float)currentValue, minValue, maxValue) - minValue) / (maxValue - minValue));
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
