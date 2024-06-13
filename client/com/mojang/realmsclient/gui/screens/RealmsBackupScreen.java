package com.mojang.realmsclient.gui.screens;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
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
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsBackupScreen extends RealmsScreen {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.configure.world.backup");
   static final Component RESTORE_TOOLTIP = Component.translatable("mco.backup.button.restore");
   static final Component HAS_CHANGES_TOOLTIP = Component.translatable("mco.backup.changes.tooltip");
   private static final Component NO_BACKUPS_LABEL = Component.translatable("mco.backup.nobackups");
   private static final String UPLOADED_KEY = "uploaded";
   private static final int PADDING = 8;
   final RealmsConfigureWorldScreen lastScreen;
   List<Backup> backups = Collections.emptyList();
   @Nullable
   RealmsBackupScreen.BackupObjectSelectionList backupList;
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

   @Override
   public void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.backupList = this.layout.addToContents(new RealmsBackupScreen.BackupObjectSelectionList());
      LinearLayout var1 = this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.downloadButton = var1.addChild(Button.builder(Component.translatable("mco.backup.button.download"), var1x -> this.downloadClicked()).build());
      this.downloadButton.active = false;
      var1.addChild(Button.builder(CommonComponents.GUI_BACK, var1x -> this.onClose()).build());
      this.layout.visitWidgets(var1x -> {
         AbstractWidget var10000 = this.addRenderableWidget(var1x);
      });
      this.repositionElements();
      this.fetchRealmsBackups();
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (this.noBackups && this.backupList != null) {
         var1.drawString(
            this.font,
            NO_BACKUPS_LABEL,
            this.width / 2 - this.font.width(NO_BACKUPS_LABEL) / 2,
            this.backupList.getY() + this.backupList.getHeight() / 2 - 9 / 2,
            -1,
            false
         );
      }
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.backupList != null) {
         this.backupList.updateSize(this.width, this.layout);
      }
   }

   private void fetchRealmsBackups() {
      (new Thread("Realms-fetch-backups") {
         @Override
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
                     RealmsBackupScreen.this.backupList.children().clear();

                     for (Backup var3x : RealmsBackupScreen.this.backups) {
                        RealmsBackupScreen.this.backupList.addEntry(var3x);
                     }
                  }
               });
            } catch (RealmsServiceException var3) {
               RealmsBackupScreen.LOGGER.error("Couldn't request backups", var3);
            }
         }
      }).start();
   }

   @Override
   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void downloadClicked() {
      MutableComponent var1 = Component.translatable("mco.configure.world.restore.download.question.line1");
      MutableComponent var2 = Component.translatable("mco.configure.world.restore.download.question.line2");
      this.minecraft
         .setScreen(
            new RealmsLongConfirmationScreen(
               var1x -> {
                  if (var1x) {
                     this.minecraft
                        .setScreen(
                           new RealmsLongRunningMcoTaskScreen(
                              this.lastScreen.getNewScreen(),
                              new DownloadTask(
                                 this.serverData.id,
                                 this.slotId,
                                 this.serverData.name
                                    + " ("
                                    + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot)
                                    + ")",
                                 this
                              )
                           )
                        );
                  } else {
                     this.minecraft.setScreen(this);
                  }
               },
               RealmsLongConfirmationScreen.Type.INFO,
               var1,
               var2,
               true
            )
         );
   }

   class BackupObjectSelectionList extends ContainerObjectSelectionList<RealmsBackupScreen.Entry> {
      private static final int ITEM_HEIGHT = 36;

      public BackupObjectSelectionList() {
         super(
            Minecraft.getInstance(),
            RealmsBackupScreen.this.width,
            RealmsBackupScreen.this.layout.getContentHeight(),
            RealmsBackupScreen.this.layout.getHeaderHeight(),
            36
         );
      }

      public void addEntry(Backup var1) {
         this.addEntry(RealmsBackupScreen.this.new Entry(var1));
      }

      @Override
      public int getMaxPosition() {
         return this.getItemCount() * 36 + this.headerHeight;
      }

      @Override
      public int getRowWidth() {
         return 300;
      }
   }

   class Entry extends ContainerObjectSelectionList.Entry<RealmsBackupScreen.Entry> {
      private static final int Y_PADDING = 2;
      private final Backup backup;
      @Nullable
      private Button changesButton;
      @Nullable
      private Button restoreButton;
      private final List<AbstractWidget> children = new ArrayList<>();

      public Entry(final Backup nullx) {
         super();
         this.backup = nullx;
         this.populateChangeList(nullx);
         if (!nullx.changeList.isEmpty()) {
            this.restoreButton = Button.builder(
                  RealmsBackupScreen.HAS_CHANGES_TOOLTIP,
                  var1 -> RealmsBackupScreen.this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.backup))
               )
               .width(8 + RealmsBackupScreen.this.font.width(RealmsBackupScreen.HAS_CHANGES_TOOLTIP))
               .createNarration(
                  var1 -> CommonComponents.joinForNarration(Component.translatable("mco.backup.narration", this.getShortBackupDate()), var1.get())
               )
               .build();
            this.children.add(this.restoreButton);
         }

         if (!RealmsBackupScreen.this.serverData.expired) {
            this.changesButton = Button.builder(RealmsBackupScreen.RESTORE_TOOLTIP, var1 -> this.restoreClicked())
               .width(8 + RealmsBackupScreen.this.font.width(RealmsBackupScreen.HAS_CHANGES_TOOLTIP))
               .createNarration(
                  var1 -> CommonComponents.joinForNarration(Component.translatable("mco.backup.narration", this.getShortBackupDate()), var1.get())
               )
               .build();
            this.children.add(this.changesButton);
         }
      }

      private void populateChangeList(Backup var1) {
         int var2 = RealmsBackupScreen.this.backups.indexOf(var1);
         if (var2 != RealmsBackupScreen.this.backups.size() - 1) {
            Backup var3 = RealmsBackupScreen.this.backups.get(var2 + 1);

            for (String var5 : var1.metadata.keySet()) {
               if (!var5.contains("uploaded") && var3.metadata.containsKey(var5)) {
                  if (!var1.metadata.get(var5).equals(var3.metadata.get(var5))) {
                     this.addToChangeList(var5);
                  }
               } else {
                  this.addToChangeList(var5);
               }
            }
         }
      }

      private void addToChangeList(String var1) {
         if (var1.contains("uploaded")) {
            String var2 = DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
            this.backup.changeList.put(var1, var2);
            this.backup.setUploadedVersion(true);
         } else {
            this.backup.changeList.put(var1, this.backup.metadata.get(var1));
         }
      }

      private String getShortBackupDate() {
         return DateFormat.getDateTimeInstance(3, 3).format(this.backup.lastModifiedDate);
      }

      private void restoreClicked() {
         Component var1 = RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate);
         MutableComponent var2 = Component.translatable("mco.configure.world.restore.question.line1", this.getShortBackupDate(), var1);
         MutableComponent var3 = Component.translatable("mco.configure.world.restore.question.line2");
         RealmsBackupScreen.this.minecraft
            .setScreen(
               new RealmsLongConfirmationScreen(
                  var1x -> {
                     if (var1x) {
                        RealmsBackupScreen.this.minecraft
                           .setScreen(
                              new RealmsLongRunningMcoTaskScreen(
                                 RealmsBackupScreen.this.lastScreen.getNewScreen(),
                                 new RestoreTask(this.backup, RealmsBackupScreen.this.serverData.id, RealmsBackupScreen.this.lastScreen)
                              )
                           );
                     } else {
                        RealmsBackupScreen.this.minecraft.setScreen(RealmsBackupScreen.this);
                     }
                  },
                  RealmsLongConfirmationScreen.Type.WARNING,
                  var2,
                  var3,
                  true
               )
            );
      }

      @Override
      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      @Override
      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      @Override
      public void render(GuiGraphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         int var11 = var3 + var6 / 2;
         int var12 = var11 - 9 - 2;
         int var13 = var11 + 2;
         int var14 = this.backup.isUploadedVersion() ? -8388737 : -1;
         var1.drawString(
            RealmsBackupScreen.this.font,
            Component.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModifiedDate)),
            var4,
            var12,
            var14,
            false
         );
         var1.drawString(RealmsBackupScreen.this.font, this.getMediumDatePresentation(this.backup.lastModifiedDate), var4, var13, 5000268, false);
         int var15 = 0;
         int var16 = var3 + var6 / 2 - 10;
         if (this.changesButton != null) {
            var15 += this.changesButton.getWidth() + 8;
            this.changesButton.setX(var4 + var5 - var15);
            this.changesButton.setY(var16);
            this.changesButton.render(var1, var7, var8, var10);
         }

         if (this.restoreButton != null) {
            var15 += this.restoreButton.getWidth() + 8;
            this.restoreButton.setX(var4 + var5 - var15);
            this.restoreButton.setY(var16);
            this.restoreButton.render(var1, var7, var8, var10);
         }
      }

      private String getMediumDatePresentation(Date var1) {
         return DateFormat.getDateTimeInstance(3, 3).format(var1);
      }
   }
}
