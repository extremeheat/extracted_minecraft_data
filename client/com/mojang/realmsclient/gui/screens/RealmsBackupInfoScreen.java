package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

public class RealmsBackupInfoScreen extends RealmsScreen {
   private final RealmsScreen lastScreen;
   private final int BUTTON_BACK_ID = 0;
   private final Backup backup;
   private final List<String> keys = new ArrayList();
   private RealmsBackupInfoScreen.BackupInfoList backupInfoList;
   String[] difficulties = new String[]{getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard")};
   String[] gameModes = new String[]{getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure")};

   public RealmsBackupInfoScreen(RealmsScreen var1, Backup var2) {
      super();
      this.lastScreen = var1;
      this.backup = var2;
      if (var2.changeList != null) {
         Iterator var3 = var2.changeList.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            this.keys.add(var4.getKey());
         }
      }

   }

   public void tick() {
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 24, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsBackupInfoScreen.this.lastScreen);
         }
      });
      this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList();
      this.addWidget(this.backupInfoList);
      this.focusOn(this.backupInfoList);
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

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString("Changes from last backup", this.width() / 2, 10, 16777215);
      this.backupInfoList.render(var1, var2, var3);
      super.render(var1, var2, var3);
   }

   private String checkForSpecificMetadata(String var1, String var2) {
      String var3 = var1.toLowerCase(Locale.ROOT);
      if (var3.contains("game") && var3.contains("mode")) {
         return this.gameModeMetadata(var2);
      } else {
         return var3.contains("game") && var3.contains("difficulty") ? this.gameDifficultyMetadata(var2) : var2;
      }
   }

   private String gameDifficultyMetadata(String var1) {
      try {
         return this.difficulties[Integer.parseInt(var1)];
      } catch (Exception var3) {
         return "UNKNOWN";
      }
   }

   private String gameModeMetadata(String var1) {
      try {
         return this.gameModes[Integer.parseInt(var1)];
      } catch (Exception var3) {
         return "UNKNOWN";
      }
   }

   class BackupInfoList extends RealmsSimpleScrolledSelectionList {
      public BackupInfoList() {
         super(RealmsBackupInfoScreen.this.width(), RealmsBackupInfoScreen.this.height(), 32, RealmsBackupInfoScreen.this.height() - 64, 36);
      }

      public int getItemCount() {
         return RealmsBackupInfoScreen.this.backup.changeList.size();
      }

      public boolean isSelectedItem(int var1) {
         return false;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground() {
      }

      public void renderItem(int var1, int var2, int var3, int var4, Tezzelator var5, int var6, int var7) {
         String var8 = (String)RealmsBackupInfoScreen.this.keys.get(var1);
         RealmsBackupInfoScreen.this.drawString(var8, this.width() / 2 - 40, var3, 10526880);
         String var9 = (String)RealmsBackupInfoScreen.this.backup.changeList.get(var8);
         RealmsBackupInfoScreen.this.drawString(RealmsBackupInfoScreen.this.checkForSpecificMetadata(var8, var9), this.width() / 2 - 40, var3 + 12, 16777215);
      }
   }
}
