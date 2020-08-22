package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBackupScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static int lastScrollPosition = -1;
   private final RealmsConfigureWorldScreen lastScreen;
   private List backups = Collections.emptyList();
   private String toolTip;
   private RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
   private int selectedBackup = -1;
   private final int slotId;
   private RealmsButton downloadButton;
   private RealmsButton restoreButton;
   private RealmsButton changesButton;
   private Boolean noBackups = false;
   private final RealmsServer serverData;
   private RealmsLabel titleLabel;

   public RealmsBackupScreen(RealmsConfigureWorldScreen var1, RealmsServer var2, int var3) {
      this.lastScreen = var1;
      this.serverData = var2;
      this.slotId = var3;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
      if (lastScrollPosition != -1) {
         this.backupObjectSelectionList.scroll(lastScrollPosition);
      }

      (new Thread("Realms-fetch-backups") {
         public void run() {
            RealmsClient var1 = RealmsClient.createRealmsClient();

            try {
               List var2 = var1.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
               Realms.execute(() -> {
                  RealmsBackupScreen.this.backups = var2;
                  RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                  RealmsBackupScreen.this.backupObjectSelectionList.clear();
                  Iterator var2x = RealmsBackupScreen.this.backups.iterator();

                  while(var2x.hasNext()) {
                     Backup var3 = (Backup)var2x.next();
                     RealmsBackupScreen.this.backupObjectSelectionList.addEntry(var3);
                  }

                  RealmsBackupScreen.this.generateChangeList();
               });
            } catch (RealmsServiceException var3) {
               RealmsBackupScreen.LOGGER.error("Couldn't request backups", var3);
            }

         }
      }).start();
      this.postInit();
   }

   private void generateChangeList() {
      if (this.backups.size() > 1) {
         label42:
         for(int var1 = 0; var1 < this.backups.size() - 1; ++var1) {
            Backup var2 = (Backup)this.backups.get(var1);
            Backup var3 = (Backup)this.backups.get(var1 + 1);
            if (!var2.metadata.isEmpty() && !var3.metadata.isEmpty()) {
               Iterator var4 = var2.metadata.keySet().iterator();

               while(true) {
                  while(true) {
                     if (!var4.hasNext()) {
                        continue label42;
                     }

                     String var5 = (String)var4.next();
                     if (!var5.contains("Uploaded") && var3.metadata.containsKey(var5)) {
                        if (!((String)var2.metadata.get(var5)).equals(var3.metadata.get(var5))) {
                           this.addToChangeList(var2, var5);
                        }
                     } else {
                        this.addToChangeList(var2, var5);
                     }
                  }
               }
            }
         }

      }
   }

   private void addToChangeList(Backup var1, String var2) {
      if (var2.contains("Uploaded")) {
         String var3 = DateFormat.getDateTimeInstance(3, 3).format(var1.lastModifiedDate);
         var1.changeList.put(var2, var3);
         var1.setUploadedVersion(true);
      } else {
         var1.changeList.put(var2, var1.metadata.get(var2));
      }

   }

   private void postInit() {
      this.buttonsAdd(this.downloadButton = new RealmsButton(2, this.width() - 135, RealmsConstants.row(1), 120, 20, getLocalizedString("mco.backup.button.download")) {
         public void onPress() {
            RealmsBackupScreen.this.downloadClicked();
         }
      });
      this.buttonsAdd(this.restoreButton = new RealmsButton(3, this.width() - 135, RealmsConstants.row(3), 120, 20, getLocalizedString("mco.backup.button.restore")) {
         public void onPress() {
            RealmsBackupScreen.this.restoreClicked(RealmsBackupScreen.this.selectedBackup);
         }
      });
      this.buttonsAdd(this.changesButton = new RealmsButton(4, this.width() - 135, RealmsConstants.row(5), 120, 20, getLocalizedString("mco.backup.changes.tooltip")) {
         public void onPress() {
            Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(RealmsBackupScreen.this.selectedBackup)));
            RealmsBackupScreen.this.selectedBackup = -1;
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() - 100, this.height() - 35, 85, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsBackupScreen.this.lastScreen);
         }
      });
      this.addWidget(this.backupObjectSelectionList);
      this.addWidget(this.titleLabel = new RealmsLabel(getLocalizedString("mco.configure.world.backup"), this.width() / 2, 12, 16777215));
      this.focusOn(this.backupObjectSelectionList);
      this.updateButtonStates();
      this.narrateLabels();
   }

   private void updateButtonStates() {
      this.restoreButton.setVisible(this.shouldRestoreButtonBeVisible());
      this.changesButton.setVisible(this.shouldChangesButtonBeVisible());
   }

   private boolean shouldChangesButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !((Backup)this.backups.get(this.selectedBackup)).changeList.isEmpty();
      }
   }

   private boolean shouldRestoreButtonBeVisible() {
      if (this.selectedBackup == -1) {
         return false;
      } else {
         return !this.serverData.expired;
      }
   }

   public void tick() {
      super.tick();
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void restoreClicked(int var1) {
      if (var1 >= 0 && var1 < this.backups.size() && !this.serverData.expired) {
         this.selectedBackup = var1;
         Date var2 = ((Backup)this.backups.get(var1)).lastModifiedDate;
         String var3 = DateFormat.getDateTimeInstance(3, 3).format(var2);
         String var4 = RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - var2.getTime());
         String var5 = getLocalizedString("mco.configure.world.restore.question.line1", new Object[]{var3, var4});
         String var6 = getLocalizedString("mco.configure.world.restore.question.line2");
         Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, var5, var6, true, 1));
      }

   }

   private void downloadClicked() {
      String var1 = getLocalizedString("mco.configure.world.restore.download.question.line1");
      String var2 = getLocalizedString("mco.configure.world.restore.download.question.line2");
      Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, var1, var2, true, 2));
   }

   private void downloadWorldData() {
      RealmsTasks.DownloadTask var1 = new RealmsTasks.DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot)).getSlotName(this.serverData.activeSlot) + ")", this);
      RealmsLongRunningMcoTaskScreen var2 = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), var1);
      var2.start();
      Realms.setScreen(var2);
   }

   public void confirmResult(boolean var1, int var2) {
      if (var1 && var2 == 1) {
         this.restore();
      } else if (var2 == 1) {
         this.selectedBackup = -1;
         Realms.setScreen(this);
      } else if (var1 && var2 == 2) {
         this.downloadWorldData();
      } else {
         Realms.setScreen(this);
      }

   }

   private void restore() {
      Backup var1 = (Backup)this.backups.get(this.selectedBackup);
      this.selectedBackup = -1;
      RealmsTasks.RestoreTask var2 = new RealmsTasks.RestoreTask(var1, this.serverData.id, this.lastScreen);
      RealmsLongRunningMcoTaskScreen var3 = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), var2);
      var3.start();
      Realms.setScreen(var3);
   }

   public void render(int var1, int var2, float var3) {
      this.toolTip = null;
      this.renderBackground();
      this.backupObjectSelectionList.render(var1, var2, var3);
      this.titleLabel.render(this);
      this.drawString(getLocalizedString("mco.configure.world.backup"), (this.width() - 150) / 2 - 90, 20, 10526880);
      if (this.noBackups) {
         this.drawString(getLocalizedString("mco.backup.nobackups"), 20, this.height() / 2 - 10, 16777215);
      }

      this.downloadButton.active(!this.noBackups);
      super.render(var1, var2, var3);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(this.toolTip, var1, var2);
      }

   }

   protected void renderMousehoverTooltip(String var1, int var2, int var3) {
      if (var1 != null) {
         int var4 = var2 + 12;
         int var5 = var3 - 12;
         int var6 = this.fontWidth(var1);
         this.fillGradient(var4 - 3, var5 - 3, var4 + var6 + 3, var5 + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(var1, var4, var5, 16777215);
      }
   }

   class BackupObjectSelectionListEntry extends RealmListEntry {
      final Backup mBackup;

      public BackupObjectSelectionListEntry(Backup var2) {
         this.mBackup = var2;
      }

      public void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9) {
         this.renderBackupItem(this.mBackup, var3 - 40, var2, var6, var7);
      }

      private void renderBackupItem(Backup var1, int var2, int var3, int var4, int var5) {
         int var6 = var1.isUploadedVersion() ? -8388737 : 16777215;
         RealmsBackupScreen.this.drawString("Backup (" + RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - var1.lastModifiedDate.getTime()) + ")", var2 + 40, var3 + 1, var6);
         RealmsBackupScreen.this.drawString(this.getMediumDatePresentation(var1.lastModifiedDate), var2 + 40, var3 + 12, 8421504);
         int var7 = RealmsBackupScreen.this.width() - 175;
         boolean var8 = true;
         int var9 = var7 - 10;
         boolean var10 = false;
         if (!RealmsBackupScreen.this.serverData.expired) {
            this.drawRestore(var7, var3 + -3, var4, var5);
         }

         if (!var1.changeList.isEmpty()) {
            this.drawInfo(var9, var3 + 0, var4, var5);
         }

      }

      private String getMediumDatePresentation(Date var1) {
         return DateFormat.getDateTimeInstance(3, 3).format(var1);
      }

      private void drawRestore(int var1, int var2, int var3, int var4) {
         boolean var5 = var3 >= var1 && var3 <= var1 + 12 && var4 >= var2 && var4 <= var2 + 14 && var4 < RealmsBackupScreen.this.height() - 15 && var4 > 32;
         RealmsScreen.bind("realms:textures/gui/realms/restore_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(var1 * 2, var2 * 2, 0.0F, var5 ? 28.0F : 0.0F, 23, 28, 23, 56);
         RenderSystem.popMatrix();
         if (var5) {
            RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.button.restore");
         }

      }

      private void drawInfo(int var1, int var2, int var3, int var4) {
         boolean var5 = var3 >= var1 && var3 <= var1 + 8 && var4 >= var2 && var4 <= var2 + 8 && var4 < RealmsBackupScreen.this.height() - 15 && var4 > 32;
         RealmsScreen.bind("realms:textures/gui/realms/plus_icon.png");
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(var1 * 2, var2 * 2, 0.0F, var5 ? 15.0F : 0.0F, 15, 15, 15, 30);
         RenderSystem.popMatrix();
         if (var5) {
            RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
         }

      }
   }

   class BackupObjectSelectionList extends RealmsObjectSelectionList {
      public BackupObjectSelectionList() {
         super(RealmsBackupScreen.this.width() - 150, RealmsBackupScreen.this.height(), 32, RealmsBackupScreen.this.height() - 15, 36);
      }

      public void addEntry(Backup var1) {
         this.addEntry(RealmsBackupScreen.this.new BackupObjectSelectionListEntry(var1));
      }

      public int getRowWidth() {
         return (int)((double)this.width() * 0.93D);
      }

      public boolean isFocused() {
         return RealmsBackupScreen.this.isFocused(this);
      }

      public int getItemCount() {
         return RealmsBackupScreen.this.backups.size();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground() {
         RealmsBackupScreen.this.renderBackground();
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 != 0) {
            return false;
         } else if (var1 < (double)this.getScrollbarPosition() && var3 >= (double)this.y0() && var3 <= (double)this.y1()) {
            int var6 = this.width() / 2 - 92;
            int var7 = this.width();
            int var8 = (int)Math.floor(var3 - (double)this.y0()) - this.headerHeight() + this.getScroll();
            int var9 = var8 / this.itemHeight();
            if (var1 >= (double)var6 && var1 <= (double)var7 && var9 >= 0 && var8 >= 0 && var9 < this.getItemCount()) {
               this.selectItem(var9);
               this.itemClicked(var8, var9, var1, var3, this.width());
            }

            return true;
         } else {
            return false;
         }
      }

      public int getScrollbarPosition() {
         return this.width() - 5;
      }

      public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
         int var8 = this.width() - 35;
         int var9 = var2 * this.itemHeight() + 36 - this.getScroll();
         int var10 = var8 + 10;
         int var11 = var9 - 3;
         if (var3 >= (double)var8 && var3 <= (double)(var8 + 9) && var5 >= (double)var9 && var5 <= (double)(var9 + 9)) {
            if (!((Backup)RealmsBackupScreen.this.backups.get(var2)).changeList.isEmpty()) {
               RealmsBackupScreen.this.selectedBackup = -1;
               RealmsBackupScreen.lastScrollPosition = this.getScroll();
               Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(var2)));
            }
         } else if (var3 >= (double)var10 && var3 < (double)(var10 + 13) && var5 >= (double)var11 && var5 < (double)(var11 + 15)) {
            RealmsBackupScreen.lastScrollPosition = this.getScroll();
            RealmsBackupScreen.this.restoreClicked(var2);
         }

      }

      public void selectItem(int var1) {
         this.setSelected(var1);
         if (var1 != -1) {
            Realms.narrateNow(RealmsScreen.getLocalizedString("narrator.select", ((Backup)RealmsBackupScreen.this.backups.get(var1)).lastModifiedDate.toString()));
         }

         this.selectInviteListItem(var1);
      }

      public void selectInviteListItem(int var1) {
         RealmsBackupScreen.this.selectedBackup = var1;
         RealmsBackupScreen.this.updateButtonStates();
      }
   }
}
