package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ConfirmScreen;
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
   private static final Component SPAWN_WARNING_TITLE;
   private EditBox nameEdit;
   protected final RealmsConfigureWorldScreen parent;
   private int column1X;
   private int columnWidth;
   private final RealmsWorldOptions options;
   private final RealmsServer.WorldType worldType;
   private Difficulty difficulty;
   private GameType gameMode;
   private final String defaultSlotName;
   private String worldName;
   private boolean pvp;
   private boolean spawnNPCs;
   private boolean spawnAnimals;
   private boolean spawnMonsters;
   int spawnProtection;
   private boolean commandBlocks;
   private boolean forceGameMode;
   SettingsSlider spawnProtectionButton;

   public RealmsSlotOptionsScreen(RealmsConfigureWorldScreen var1, RealmsWorldOptions var2, RealmsServer.WorldType var3, int var4) {
      super(Component.translatable("mco.configure.world.buttons.options"));
      this.parent = var1;
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
         this.spawnAnimals = var2.spawnAnimals;
         this.spawnMonsters = var2.spawnMonsters;
         this.spawnNPCs = var2.spawnNPCs;
         this.commandBlocks = var2.commandBlocks;
      } else {
         this.pvp = true;
         this.spawnProtection = 0;
         this.forceGameMode = false;
         this.spawnAnimals = true;
         this.spawnMonsters = true;
         this.spawnNPCs = true;
         this.commandBlocks = true;
      }

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

      this.nameEdit = new EditBox(this.minecraft.font, this.column1X + 2, row(1), this.columnWidth - 4, 20, (EditBox)null, Component.translatable("mco.configure.world.edit.slot.name"));
      this.nameEdit.setMaxLength(10);
      this.nameEdit.setValue(this.worldName);
      this.nameEdit.setResponder(this::setWorldName);
      this.magicalSpecialHackyFocus(this.nameEdit);
      CycleButton var9 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.pvp).create(var1, row(1), this.columnWidth, 20, Component.translatable("mco.configure.world.pvp"), (var1x, var2x) -> {
         this.pvp = var2x;
      }));
      this.addRenderableWidget(CycleButton.builder(GameType::getShortDisplayName).withValues((Collection)GAME_MODES).withInitialValue(this.gameMode).create(this.column1X, row(3), this.columnWidth, 20, Component.translatable("selectWorld.gameMode"), (var1x, var2x) -> {
         this.gameMode = var2x;
      }));
      MutableComponent var3 = Component.translatable("mco.configure.world.spawn_toggle.message");
      CycleButton var4 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnAnimals).create(var1, row(3), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnAnimals"), this.confirmDangerousOption(var3, (var1x) -> {
         this.spawnAnimals = var1x;
      })));
      CycleButton var5 = CycleButton.onOffBuilder(this.difficulty != Difficulty.PEACEFUL && this.spawnMonsters).create(var1, row(5), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnMonsters"), this.confirmDangerousOption(var3, (var1x) -> {
         this.spawnMonsters = var1x;
      }));
      this.addRenderableWidget(CycleButton.builder(Difficulty::getDisplayName).withValues((Collection)DIFFICULTIES).withInitialValue(this.difficulty).create(this.column1X, row(5), this.columnWidth, 20, Component.translatable("options.difficulty"), (var2x, var3x) -> {
         this.difficulty = var3x;
         if (this.worldType == RealmsServer.WorldType.NORMAL) {
            boolean var4 = this.difficulty != Difficulty.PEACEFUL;
            var5.active = var4;
            var5.setValue(var4 && this.spawnMonsters);
         }

      }));
      this.addRenderableWidget(var5);
      this.spawnProtectionButton = (SettingsSlider)this.addRenderableWidget(new SettingsSlider(this.column1X, row(7), this.columnWidth, this.spawnProtection, 0.0F, 16.0F));
      CycleButton var6 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.spawnNPCs).create(var1, row(7), this.columnWidth, 20, Component.translatable("mco.configure.world.spawnNPCs"), this.confirmDangerousOption(Component.translatable("mco.configure.world.spawn_toggle.message.npc"), (var1x) -> {
         this.spawnNPCs = var1x;
      })));
      CycleButton var7 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.forceGameMode).create(this.column1X, row(9), this.columnWidth, 20, Component.translatable("mco.configure.world.forceGameMode"), (var1x, var2x) -> {
         this.forceGameMode = var2x;
      }));
      CycleButton var8 = (CycleButton)this.addRenderableWidget(CycleButton.onOffBuilder(this.commandBlocks).create(var1, row(9), this.columnWidth, 20, Component.translatable("mco.configure.world.commandBlocks"), (var1x, var2x) -> {
         this.commandBlocks = var2x;
      }));
      if (this.worldType != RealmsServer.WorldType.NORMAL) {
         var9.active = false;
         var4.active = false;
         var6.active = false;
         var5.active = false;
         this.spawnProtectionButton.active = false;
         var8.active = false;
         var7.active = false;
      }

      if (this.difficulty == Difficulty.PEACEFUL) {
         var5.active = false;
      }

      this.addRenderableWidget(new Button(this.column1X, row(13), this.columnWidth, 20, Component.translatable("mco.configure.world.buttons.done"), (var1x) -> {
         this.saveSettings();
      }));
      this.addRenderableWidget(new Button(var1, row(13), this.columnWidth, 20, CommonComponents.GUI_CANCEL, (var1x) -> {
         this.minecraft.setScreen(this.parent);
      }));
      this.addWidget(this.nameEdit);
   }

   private CycleButton.OnValueChange<Boolean> confirmDangerousOption(Component var1, Consumer<Boolean> var2) {
      return (var3, var4) -> {
         if (var4) {
            var2.accept(true);
         } else {
            this.minecraft.setScreen(new ConfirmScreen((var2x) -> {
               if (var2x) {
                  var2.accept(false);
               }

               this.minecraft.setScreen(this);
            }, SPAWN_WARNING_TITLE, var1, CommonComponents.GUI_PROCEED, CommonComponents.GUI_CANCEL));
         }

      };
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
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.pvp, this.spawnAnimals, var3, this.spawnNPCs, this.spawnProtection, this.commandBlocks, var1, var2, this.forceGameMode, this.worldName));
      } else {
         this.parent.saveSlotSettings(new RealmsWorldOptions(this.options.pvp, this.options.spawnAnimals, this.options.spawnMonsters, this.options.spawnNPCs, this.options.spawnProtection, this.options.commandBlocks, var1, var2, this.options.forceGameMode, this.worldName));
      }

   }

   static {
      DIFFICULTIES = ImmutableList.of(Difficulty.PEACEFUL, Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD);
      GAME_MODES = ImmutableList.of(GameType.SURVIVAL, GameType.CREATIVE, GameType.ADVENTURE);
      NAME_LABEL = Component.translatable("mco.configure.world.edit.slot.name");
      SPAWN_PROTECTION_TEXT = Component.translatable("mco.configure.world.spawnProtection");
      SPAWN_WARNING_TITLE = Component.translatable("mco.configure.world.spawn_toggle.title").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);
   }

   private class SettingsSlider extends AbstractSliderButton {
      private final double minValue;
      private final double maxValue;

      public SettingsSlider(int var2, int var3, int var4, int var5, float var6, float var7) {
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

      public void onClick(double var1, double var3) {
      }

      public void onRelease(double var1, double var3) {
      }
   }
}
