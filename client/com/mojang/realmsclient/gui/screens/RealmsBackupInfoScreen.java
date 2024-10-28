package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameType;

public class RealmsBackupInfoScreen extends RealmsScreen {
   private static final Component TITLE = Component.translatable("mco.backup.info.title");
   private static final Component UNKNOWN = Component.translatable("mco.backup.unknown");
   private final Screen lastScreen;
   final Backup backup;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private BackupInfoList backupInfoList;

   public RealmsBackupInfoScreen(Screen var1, Backup var2) {
      super(TITLE);
      this.lastScreen = var1;
      this.backup = var2;
   }

   public void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.backupInfoList = (BackupInfoList)this.layout.addToContents(new BackupInfoList(this.minecraft));
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1) -> {
         this.onClose();
      }).build());
      this.repositionElements();
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
   }

   protected void repositionElements() {
      this.backupInfoList.setSize(this.width, this.layout.getContentHeight());
      this.layout.arrangeElements();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   Component checkForSpecificMetadata(String var1, String var2) {
      String var3 = var1.toLowerCase(Locale.ROOT);
      if (var3.contains("game") && var3.contains("mode")) {
         return this.gameModeMetadata(var2);
      } else {
         return (Component)(var3.contains("game") && var3.contains("difficulty") ? this.gameDifficultyMetadata(var2) : Component.literal(var2));
      }
   }

   private Component gameDifficultyMetadata(String var1) {
      try {
         return ((Difficulty)RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(var1))).getDisplayName();
      } catch (Exception var3) {
         return UNKNOWN;
      }
   }

   private Component gameModeMetadata(String var1) {
      try {
         return ((GameType)RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(var1))).getShortDisplayName();
      } catch (Exception var3) {
         return UNKNOWN;
      }
   }

   private class BackupInfoList extends ObjectSelectionList<BackupInfoListEntry> {
      public BackupInfoList(final Minecraft var2) {
         super(var2, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.layout.getContentHeight(), RealmsBackupInfoScreen.this.layout.getHeaderHeight(), 36);
         if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup.changeList.forEach((var1x, var2x) -> {
               this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(var1x, var2x));
            });
         }

      }
   }

   private class BackupInfoListEntry extends ObjectSelectionList.Entry<BackupInfoListEntry> {
      private static final Component TEMPLATE_NAME = Component.translatable("mco.backup.entry.templateName");
      private static final Component GAME_DIFFICULTY = Component.translatable("mco.backup.entry.gameDifficulty");
      private static final Component NAME = Component.translatable("mco.backup.entry.name");
      private static final Component GAME_SERVER_VERSION = Component.translatable("mco.backup.entry.gameServerVersion");
      private static final Component UPLOADED = Component.translatable("mco.backup.entry.uploaded");
      private static final Component ENABLED_PACK = Component.translatable("mco.backup.entry.enabledPack");
      private static final Component DESCRIPTION = Component.translatable("mco.backup.entry.description");
      private static final Component GAME_MODE = Component.translatable("mco.backup.entry.gameMode");
      private static final Component SEED = Component.translatable("mco.backup.entry.seed");
      private static final Component WORLD_TYPE = Component.translatable("mco.backup.entry.worldType");
      private static final Component UNDEFINED = Component.translatable("mco.backup.entry.undefined");
      private final String key;
      private final String value;

      public BackupInfoListEntry(final String var2, final String var3) {
         super();
         this.key = var2;
         this.value = var3;
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.drawString(RealmsBackupInfoScreen.this.font, this.translateKey(this.key), var4, var3, -6250336);
         var1.drawString(RealmsBackupInfoScreen.this.font, (Component)RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), var4, var3 + 12, -1);
      }

      private Component translateKey(String var1) {
         Component var10000;
         switch (var1) {
            case "template_name" -> var10000 = TEMPLATE_NAME;
            case "game_difficulty" -> var10000 = GAME_DIFFICULTY;
            case "name" -> var10000 = NAME;
            case "game_server_version" -> var10000 = GAME_SERVER_VERSION;
            case "uploaded" -> var10000 = UPLOADED;
            case "enabled_packs" -> var10000 = ENABLED_PACK;
            case "description" -> var10000 = DESCRIPTION;
            case "game_mode" -> var10000 = GAME_MODE;
            case "seed" -> var10000 = SEED;
            case "world_type" -> var10000 = WORLD_TYPE;
            default -> var10000 = UNDEFINED;
         }

         return var10000;
      }

      public Component getNarration() {
         return Component.translatable("narrator.select", this.key + " " + this.value);
      }
   }
}
