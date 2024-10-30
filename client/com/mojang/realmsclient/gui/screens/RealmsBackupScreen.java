package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsBackupScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.configure.world.backup");
   static final Component RESTORE_TOOLTIP = Component.translatable("mco.backup.button.restore");
   static final Component HAS_CHANGES_TOOLTIP = Component.translatable("mco.backup.changes.tooltip");
   private static final Component NO_BACKUPS_LABEL = Component.translatable("mco.backup.nobackups");
   private static final Component DOWNLOAD_LATEST = Component.translatable("mco.backup.button.download");
   private static final String UPLOADED_KEY = "uploaded";
   private static final int PADDING = 8;
   final RealmsConfigureWorldScreen lastScreen;
   List<Backup> backups = Collections.emptyList();
   @Nullable
   BackupObjectSelectionList backupList;
   final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final int slotId;
   @Nullable
   Button downloadButton;
   final RealmsServer serverData;
   boolean noBackups = false;

   public RealmsBackupScreen(RealmsConfigureWorldScreen var1, RealmsServer var2, int var3) {
      super(TITLE);
      this.lastScreen = var1;
      this.serverData = var2;
      this.slotId = var3;
   }

   public void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.backupList = (BackupObjectSelectionList)this.layout.addToContents(new BackupObjectSelectionList(this));
      LinearLayout var1 = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.downloadButton = (Button)var1.addChild(Button.builder(DOWNLOAD_LATEST, (var1x) -> {
         this.downloadClicked();
      }).build());
      this.downloadButton.active = false;
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, (var1x) -> {
         this.onClose();
      }).build());
      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
      this.fetchRealmsBackups();
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.noBackups && this.backupList != null) {
         Font var10001 = this.font;
         Component var10002 = NO_BACKUPS_LABEL;
         int var10003 = this.width / 2 - this.font.width((FormattedText)NO_BACKUPS_LABEL) / 2;
         int var10004 = this.backupList.getY() + this.backupList.getHeight() / 2;
         Objects.requireNonNull(this.font);
         var1.drawString(var10001, (Component)var10002, var10003, var10004 - 9 / 2, -1);
      }

   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.backupList != null) {
         this.backupList.updateSize(this.width, this.layout);
      }

   }

   private void fetchRealmsBackups() {
      (new Thread("Realms-fetch-backups") {
         public void run() {
            RealmsClient var1 = RealmsClient.create();

            try {
               List var2 = var1.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
               RealmsBackupScreen.this.minecraft.execute(() -> {
                  RealmsBackupScreen.this.backups = var2;
                  RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                  if (!RealmsBackupScreen.this.noBackups && RealmsBackupScreen.this.downloadButton != null) {
                     RealmsBackupScreen.this.downloadButton.active = true;
                  }

                  if (RealmsBackupScreen.this.backupList != null) {
                     RealmsBackupScreen.this.backupList.replaceEntries(RealmsBackupScreen.this.backups.stream().map((var1) -> {
                        return RealmsBackupScreen.this.new Entry(var1);
                     }).toList());
                  }

               });
            } catch (RealmsServiceException var3) {
               RealmsBackupScreen.LOGGER.error("Couldn't request backups", var3);
            }

         }
      }).start();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void downloadClicked() {
      this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this, Component.translatable("mco.configure.world.restore.download.question.line1"), (var1) -> {
         Minecraft var10000 = this.minecraft;
         RealmsConfigureWorldScreen var10003 = this.lastScreen.getNewScreen();
         LongRunningTask[] var10004 = new LongRunningTask[1];
         long var10009 = this.serverData.id;
         int var10010 = this.slotId;
         String var10011 = (String)Objects.requireNonNullElse(this.serverData.name, "");
         var10004[0] = new DownloadTask(var10009, var10010, var10011 + " (" + ((RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot)).getSlotName(this.serverData.activeSlot) + ")", this);
         var10000.setScreen(new RealmsLongRunningMcoTaskScreen(var10003, var10004));
      }));
   }

   private class BackupObjectSelectionList extends ContainerObjectSelectionList<Entry> {
      private static final int ITEM_HEIGHT = 36;

      public BackupObjectSelectionList(final RealmsBackupScreen var1) {
         super(Minecraft.getInstance(), var1.width, var1.layout.getContentHeight(), var1.layout.getHeaderHeight(), 36);
      }

      public int getRowWidth() {
         return 300;
      }
   }

   private class Entry extends ContainerObjectSelectionList.Entry<Entry> {
      private static final int Y_PADDING = 2;
      private final Backup backup;
      @Nullable
      private Button restoreButton;
      @Nullable
      private Button changesButton;
      private final List<AbstractWidget> children = new ArrayList();

      public Entry(final Backup var2) {
         super();
         this.backup = var2;
         this.populateChangeList(var2);
         if (!var2.changeList.isEmpty()) {
            this.changesButton = Button.builder(RealmsBackupScreen.HAS_CHANGES_TOOLTIP, (var1x) -> {
               RealmsBackupScreen.this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.backup));
            }).width(8 + RealmsBackupScreen.this.font.width((FormattedText)RealmsBackupScreen.HAS_CHANGES_TOOLTIP)).createNarration((var1x) -> {
               return CommonComponents.joinForNarration(Component.translatable("mco.backup.narration", this.getShortBackupDate()), (Component)var1x.get());
            }).build();
            this.children.add(this.changesButton);
         }

         if (!RealmsBackupScreen.this.serverData.expired) {
            this.restoreButton = Button.builder(RealmsBackupScreen.RESTORE_TOOLTIP, (var1x) -> {
               this.restoreClicked();
            }).width(8 + RealmsBackupScreen.this.font.width((FormattedText)RealmsBackupScreen.HAS_CHANGES_TOOLTIP)).createNarration((var1x) -> {
               return CommonComponents.joinForNarration(Component.translatable("mco.backup.narration", this.getShortBackupDate()), (Component)var1x.get());
            }).build();
            this.children.add(this.restoreButton);
         }

      }

      private void populateChangeList(Backup var1) {
         int var2 = RealmsBackupScreen.this.backups.indexOf(var1);
         if (var2 != RealmsBackupScreen.this.backups.size() - 1) {
            Backup var3 = (Backup)RealmsBackupScreen.this.backups.get(var2 + 1);
            Iterator var4 = var1.metadata.keySet().iterator();

            while(true) {
               while(var4.hasNext()) {
                  String var5 = (String)var4.next();
                  if (!var5.contains("uploaded") && var3.metadata.containsKey(var5)) {
                     if (!((String)var1.metadata.get(var5)).equals(var3.metadata.get(var5))) {
                        this.addToChangeList(var5);
                     }
                  } else {
                     this.addToChangeList(var5);
                  }
               }

               return;
            }
         }
      }

      private void addToChangeList(String var1) {
         if (var1.contains("uploaded")) {
            String var2 = DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
            this.backup.changeList.put(var1, var2);
            this.backup.setUploadedVersion(true);
         } else {
            this.backup.changeList.put(var1, (String)this.backup.metadata.get(var1));
         }

      }

      private String getShortBackupDate() {
         return DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
      }

      private void restoreClicked() {
         Component var1 = RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate);
         MutableComponent var2 = Component.translatable("mco.configure.world.restore.question.line1", this.getShortBackupDate(), var1);
         RealmsBackupScreen.this.minecraft.setScreen(RealmsPopups.warningPopupScreen(RealmsBackupScreen.this, var2, (var1x) -> {
            RealmsBackupScreen.this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(RealmsBackupScreen.this.lastScreen.getNewScreen(), new LongRunningTask[]{new RestoreTask(this.backup, RealmsBackupScreen.this.serverData.id, RealmsBackupScreen.this.lastScreen)}));
         }));
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = var3 + var6 / 2;
         Objects.requireNonNull(RealmsBackupScreen.this.font);
         int var12 = var11 - 9 - 2;
         int var13 = var11 + 2;
         int var14 = this.backup.isUploadedVersion() ? -8388737 : -1;
         var1.drawString(RealmsBackupScreen.this.font, (Component)Component.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate)), var4, var12, var14);
         var1.drawString(RealmsBackupScreen.this.font, this.getMediumDatePresentation(this.backup.lastModifiedDate), var4, var13, 5000268);
         int var15 = 0;
         int var16 = var3 + var6 / 2 - 10;
         if (this.restoreButton != null) {
            var15 += this.restoreButton.getWidth() + 8;
            this.restoreButton.setX(var4 + var5 - var15);
            this.restoreButton.setY(var16);
            this.restoreButton.render(var1, var7, var8, var10);
         }

         if (this.changesButton != null) {
            var15 += this.changesButton.getWidth() + 8;
            this.changesButton.setX(var4 + var5 - var15);
            this.changesButton.setY(var16);
            this.changesButton.render(var1, var7, var8, var10);
         }

      }

      private String getMediumDatePresentation(Date var1) {
         return DateFormat.getDateTimeInstance(3, 3).format(var1);
      }
   }
}
