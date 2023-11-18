package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsBackupInfoScreen extends RealmsScreen {
   private static final Component UNKNOWN = Component.translatable("mco.backup.unknown");
   private final Screen lastScreen;
   final Backup backup;
   private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

   public RealmsBackupInfoScreen(Screen var1, Backup var2) {
      super(Component.translatable("mco.backup.info.title"));
      this.lastScreen = var1;
      this.backup = var2;
   }

   @Override
   public void tick() {
   }

   @Override
   public void init() {
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1 -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20)
            .build()
      );
      this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.minecraft);
      this.addWidget(this.backupInfoList);
      this.magicalSpecialHackyFocus(this.backupInfoList);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      this.backupInfoList.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.title, this.width / 2, 10, 16777215);
      super.render(var1, var2, var3, var4);
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
         return RealmsSlotOptionsScreen.DIFFICULTIES.get(Integer.parseInt(var1)).getDisplayName();
      } catch (Exception var3) {
         return UNKNOWN;
      }
   }

   private Component gameModeMetadata(String var1) {
      try {
         return RealmsSlotOptionsScreen.GAME_MODES.get(Integer.parseInt(var1)).getShortDisplayName();
      } catch (Exception var3) {
         return UNKNOWN;
      }
   }

   class BackupInfoList extends ObjectSelectionList<RealmsBackupInfoScreen.BackupInfoListEntry> {
      public BackupInfoList(Minecraft var2) {
         super(var2, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
         this.setRenderSelection(false);
         if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup
               .changeList
               .forEach((var1x, var2x) -> this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(var1x, var2x)));
         }
      }
   }

   class BackupInfoListEntry extends ObjectSelectionList.Entry<RealmsBackupInfoScreen.BackupInfoListEntry> {
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

      public BackupInfoListEntry(String var2, String var3) {
         super();
         this.key = var2;
         this.value = var3;
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         var1.drawString(RealmsBackupInfoScreen.this.font, this.translateKey(this.key), var4, var3, 10526880);
         var1.drawString(
            RealmsBackupInfoScreen.this.font, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), var4, var3 + 12, 16777215
         );
      }

      private Component translateKey(String var1) {
         return switch(var1) {
            case "template_name" -> TEMPLATE_NAME;
            case "game_difficulty" -> GAME_DIFFICULTY;
            case "name" -> NAME;
            case "game_server_version" -> GAME_SERVER_VERSION;
            case "uploaded" -> UPLOADED;
            case "enabled_pack" -> ENABLED_PACK;
            case "description" -> DESCRIPTION;
            case "game_mode" -> GAME_MODE;
            case "seed" -> SEED;
            case "world_type" -> WORLD_TYPE;
            default -> UNDEFINED;
         };
      }

      @Override
      public Component getNarration() {
         return Component.translatable("narrator.select", this.key + " " + this.value);
      }
   }
}
