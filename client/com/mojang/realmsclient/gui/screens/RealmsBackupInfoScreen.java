package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.dto.Backup;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsBackupInfoScreen extends RealmsScreen {
   private final Screen lastScreen;
   private final Backup backup;
   private RealmsBackupInfoScreen.BackupInfoList backupInfoList;

   public RealmsBackupInfoScreen(Screen var1, Backup var2) {
      super();
      this.lastScreen = var1;
      this.backup = var2;
   }

   public void tick() {
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 120 + 24, 200, 20, CommonComponents.GUI_BACK, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList(this.minecraft);
      this.addWidget(this.backupInfoList);
      this.magicalSpecialHackyFocus(this.backupInfoList);
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, "Changes from last backup", this.width / 2, 10, 16777215);
      this.backupInfoList.render(var1, var2, var3, var4);
      super.render(var1, var2, var3, var4);
   }

   private Component checkForSpecificMetadata(String var1, String var2) {
      String var3 = var1.toLowerCase(Locale.ROOT);
      if (var3.contains("game") && var3.contains("mode")) {
         return this.gameModeMetadata(var2);
      } else {
         return (Component)(var3.contains("game") && var3.contains("difficulty") ? this.gameDifficultyMetadata(var2) : new TextComponent(var2));
      }
   }

   private Component gameDifficultyMetadata(String var1) {
      try {
         return RealmsSlotOptionsScreen.DIFFICULTIES[Integer.parseInt(var1)];
      } catch (Exception var3) {
         return new TextComponent("UNKNOWN");
      }
   }

   private Component gameModeMetadata(String var1) {
      try {
         return RealmsSlotOptionsScreen.GAME_MODES[Integer.parseInt(var1)];
      } catch (Exception var3) {
         return new TextComponent("UNKNOWN");
      }
   }

   class BackupInfoList extends ObjectSelectionList<RealmsBackupInfoScreen.BackupInfoListEntry> {
      public BackupInfoList(Minecraft var2) {
         super(var2, RealmsBackupInfoScreen.this.width, RealmsBackupInfoScreen.this.height, 32, RealmsBackupInfoScreen.this.height - 64, 36);
         this.setRenderSelection(false);
         if (RealmsBackupInfoScreen.this.backup.changeList != null) {
            RealmsBackupInfoScreen.this.backup.changeList.forEach((var1x, var2x) -> {
               this.addEntry(RealmsBackupInfoScreen.this.new BackupInfoListEntry(var1x, var2x));
            });
         }

      }
   }

   class BackupInfoListEntry extends ObjectSelectionList.Entry<RealmsBackupInfoScreen.BackupInfoListEntry> {
      private final String key;
      private final String value;

      public BackupInfoListEntry(String var2, String var3) {
         super();
         this.key = var2;
         this.value = var3;
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         Font var11 = RealmsBackupInfoScreen.this.minecraft.font;
         GuiComponent.drawString(var1, var11, this.key, var4, var3, 10526880);
         GuiComponent.drawString(var1, var11, RealmsBackupInfoScreen.this.checkForSpecificMetadata(this.key, this.value), var4, var3 + 12, 16777215);
      }
   }
}
