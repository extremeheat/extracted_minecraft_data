package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSelectFileToUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsResetWorldScreen lastScreen;
   private final long worldId;
   private final int slotId;
   private RealmsButton uploadButton;
   private final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private List levelList = Lists.newArrayList();
   private int selectedWorld = -1;
   private RealmsSelectFileToUploadScreen.WorldSelectionList worldSelectionList;
   private String worldLang;
   private String conversionLang;
   private final String[] gameModesLang = new String[4];
   private RealmsLabel titleLabel;
   private RealmsLabel subtitleLabel;
   private RealmsLabel noWorldsLabel;

   public RealmsSelectFileToUploadScreen(long var1, int var3, RealmsResetWorldScreen var4) {
      this.lastScreen = var4;
      this.worldId = var1;
      this.slotId = var3;
   }

   private void loadLevelList() throws Exception {
      RealmsAnvilLevelStorageSource var1 = this.getLevelStorageSource();
      this.levelList = var1.getLevelList();
      Collections.sort(this.levelList);
      Iterator var2 = this.levelList.iterator();

      while(var2.hasNext()) {
         RealmsLevelSummary var3 = (RealmsLevelSummary)var2.next();
         this.worldSelectionList.addEntry(var3);
      }

   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.worldSelectionList = new RealmsSelectFileToUploadScreen.WorldSelectionList();

      try {
         this.loadLevelList();
      } catch (Exception var2) {
         LOGGER.error("Couldn't load level list", var2);
         Realms.setScreen(new RealmsGenericErrorScreen("Unable to load worlds", var2.getMessage(), this.lastScreen));
         return;
      }

      this.worldLang = getLocalizedString("selectWorld.world");
      this.conversionLang = getLocalizedString("selectWorld.conversion");
      this.gameModesLang[Realms.survivalId()] = getLocalizedString("gameMode.survival");
      this.gameModesLang[Realms.creativeId()] = getLocalizedString("gameMode.creative");
      this.gameModesLang[Realms.adventureId()] = getLocalizedString("gameMode.adventure");
      this.gameModesLang[Realms.spectatorId()] = getLocalizedString("gameMode.spectator");
      this.addWidget(this.worldSelectionList);
      this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 6, this.height() - 32, 153, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsSelectFileToUploadScreen.this.lastScreen);
         }
      });
      this.buttonsAdd(this.uploadButton = new RealmsButton(2, this.width() / 2 - 154, this.height() - 32, 153, 20, getLocalizedString("mco.upload.button.name")) {
         public void onPress() {
            RealmsSelectFileToUploadScreen.this.upload();
         }
      });
      this.uploadButton.active(this.selectedWorld >= 0 && this.selectedWorld < this.levelList.size());
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.title"), this.width() / 2, 13, 16777215));
      this.addWidget(this.subtitleLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.subtitle"), this.width() / 2, RealmsConstants.row(-1), 10526880));
      if (this.levelList.isEmpty()) {
         this.addWidget(this.noWorldsLabel = new RealmsLabel(getLocalizedString("mco.upload.select.world.none"), this.width() / 2, this.height() / 2 - 20, 16777215));
      } else {
         this.noWorldsLabel = null;
      }

      this.narrateLabels();
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void upload() {
      if (this.selectedWorld != -1 && !((RealmsLevelSummary)this.levelList.get(this.selectedWorld)).isHardcore()) {
         RealmsLevelSummary var1 = (RealmsLevelSummary)this.levelList.get(this.selectedWorld);
         Realms.setScreen(new RealmsUploadScreen(this.worldId, this.slotId, this.lastScreen, var1));
      }

   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.worldSelectionList.render(var1, var2, var3);
      this.titleLabel.render(this);
      this.subtitleLabel.render(this);
      if (this.noWorldsLabel != null) {
         this.noWorldsLabel.render(this);
      }

      super.render(var1, var2, var3);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void tick() {
      super.tick();
   }

   private String gameModeName(RealmsLevelSummary var1) {
      return this.gameModesLang[var1.getGameMode()];
   }

   private String formatLastPlayed(RealmsLevelSummary var1) {
      return this.DATE_FORMAT.format(new Date(var1.getLastPlayed()));
   }

   class WorldListEntry extends RealmListEntry {
      final RealmsLevelSummary levelSummary;

      public WorldListEntry(RealmsLevelSummary var2) {
         this.levelSummary = var2;
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.renderItem(this.levelSummary, var1, var3, var2, var5, Tezzelator.instance, var6, var7);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         RealmsSelectFileToUploadScreen.this.worldSelectionList.selectItem(RealmsSelectFileToUploadScreen.this.levelList.indexOf(this.levelSummary));
         return true;
      }

      protected void renderItem(RealmsLevelSummary var1, int var2, int var3, int var4, int var5, Tezzelator var6, int var7, int var8) {
         String var9 = var1.getLevelName();
         if (var9 == null || var9.isEmpty()) {
            var9 = RealmsSelectFileToUploadScreen.this.worldLang + " " + (var2 + 1);
         }

         String var10 = var1.getLevelId();
         var10 = var10 + " (" + RealmsSelectFileToUploadScreen.this.formatLastPlayed(var1);
         var10 = var10 + ")";
         String var11 = "";
         if (var1.isRequiresConversion()) {
            var11 = RealmsSelectFileToUploadScreen.this.conversionLang + " " + var11;
         } else {
            var11 = RealmsSelectFileToUploadScreen.this.gameModeName(var1);
            if (var1.isHardcore()) {
               var11 = ChatFormatting.DARK_RED + RealmsScreen.getLocalizedString("mco.upload.hardcore") + ChatFormatting.RESET;
            }

            if (var1.hasCheats()) {
               var11 = var11 + ", " + RealmsScreen.getLocalizedString("selectWorld.cheats");
            }
         }

         RealmsSelectFileToUploadScreen.this.drawString(var9, var3 + 2, var4 + 1, 16777215);
         RealmsSelectFileToUploadScreen.this.drawString(var10, var3 + 2, var4 + 12, 8421504);
         RealmsSelectFileToUploadScreen.this.drawString(var11, var3 + 2, var4 + 12 + 10, 8421504);
      }
   }

   class WorldSelectionList extends RealmsObjectSelectionList {
      public WorldSelectionList() {
         super(RealmsSelectFileToUploadScreen.this.width(), RealmsSelectFileToUploadScreen.this.height(), RealmsConstants.row(0), RealmsSelectFileToUploadScreen.this.height() - 40, 36);
      }

      public void addEntry(RealmsLevelSummary var1) {
         this.addEntry(RealmsSelectFileToUploadScreen.this.new WorldListEntry(var1));
      }

      public int getItemCount() {
         return RealmsSelectFileToUploadScreen.this.levelList.size();
      }

      public int getMaxPosition() {
         return RealmsSelectFileToUploadScreen.this.levelList.size() * 36;
      }

      public boolean isFocused() {
         return RealmsSelectFileToUploadScreen.this.isFocused(this);
      }

      public void renderBackground() {
         RealmsSelectFileToUploadScreen.this.renderBackground();
      }

      public void selectItem(int var1) {
         this.setSelected(var1);
         if (var1 != -1) {
            RealmsLevelSummary var2 = (RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(var1);
            String var3 = RealmsScreen.getLocalizedString("narrator.select.list.position", var1 + 1, RealmsSelectFileToUploadScreen.this.levelList.size());
            String var4 = Realms.joinNarrations(Arrays.asList(var2.getLevelName(), RealmsSelectFileToUploadScreen.this.formatLastPlayed(var2), RealmsSelectFileToUploadScreen.this.gameModeName(var2), var3));
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", var4));
         }

         RealmsSelectFileToUploadScreen.this.selectedWorld = var1;
         RealmsSelectFileToUploadScreen.this.uploadButton.active(RealmsSelectFileToUploadScreen.this.selectedWorld >= 0 && RealmsSelectFileToUploadScreen.this.selectedWorld < this.getItemCount() && !((RealmsLevelSummary)RealmsSelectFileToUploadScreen.this.levelList.get(RealmsSelectFileToUploadScreen.this.selectedWorld)).isHardcore());
      }
   }
}
