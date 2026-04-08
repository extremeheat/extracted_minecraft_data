package com.mojang.realmsclient.gui.screens.configuration;

import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsSlot;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPopups;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RealmsBackupScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Component TITLE = Component.translatable("mco.configure.world.backup");
   private static final Component RESTORE_TOOLTIP = Component.translatable("mco.backup.button.restore");
   private static final Component HAS_CHANGES_TOOLTIP = Component.translatable("mco.backup.changes.tooltip");
   private static final Component NO_BACKUPS_LABEL = Component.translatable("mco.backup.nobackups");
   private static final Component DOWNLOAD_LATEST = Component.translatable("mco.backup.button.download");
   private static final String UPLOADED_KEY = "uploaded";
   private static final int PADDING = 8;
   public static final DateTimeFormatter SHORT_DATE_FORMAT;
   private final RealmsConfigureWorldScreen lastScreen;
   private List<Backup> backups = Collections.emptyList();
   private BackupObjectSelectionList backupList;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
   private final int slotId;
   private @Nullable Button downloadButton;
   private final RealmsServer serverData;
   private boolean noBackups = false;

   public RealmsBackupScreen(final RealmsConfigureWorldScreen lastScreen, final RealmsServer serverData, final int slotId) {
      super(TITLE);
      this.lastScreen = lastScreen;
      this.serverData = serverData;
      this.slotId = slotId;
   }

   public void init() {
      this.layout.addTitleHeader(TITLE, this.font);
      this.backupList = (BackupObjectSelectionList)this.layout.addToContents(new BackupObjectSelectionList());
      LinearLayout footer = (LinearLayout)this.layout.addToFooter(LinearLayout.horizontal().spacing(8));
      this.downloadButton = (Button)footer.addChild(Button.builder(DOWNLOAD_LATEST, (button) -> this.downloadClicked()).build());
      this.downloadButton.active = false;
      footer.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> this.onClose()).build());
      this.layout.visitWidgets((x$0) -> this.addRenderableWidget(x$0));
      this.repositionElements();
      this.fetchRealmsBackups();
   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      if (this.noBackups && this.backupList != null) {
         Font var10001 = this.font;
         Component var10002 = NO_BACKUPS_LABEL;
         int var10003 = this.width / 2 - this.font.width((FormattedText)NO_BACKUPS_LABEL) / 2;
         int var10004 = this.backupList.getY() + this.backupList.getHeight() / 2;
         Objects.requireNonNull(this.font);
         graphics.text(var10001, (Component)var10002, var10003, var10004 - 9 / 2, -1);
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
         {
            Objects.requireNonNull(RealmsBackupScreen.this);
         }

         public void run() {
            RealmsClient client = RealmsClient.getOrCreate();

            try {
               List<Backup> backups = client.backupsFor(RealmsBackupScreen.this.serverData.id).backups();
               RealmsBackupScreen.this.minecraft.execute(() -> {
                  RealmsBackupScreen.this.backups = backups;
                  RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.isEmpty();
                  if (!RealmsBackupScreen.this.noBackups && RealmsBackupScreen.this.downloadButton != null) {
                     RealmsBackupScreen.this.downloadButton.active = true;
                  }

                  if (RealmsBackupScreen.this.backupList != null) {
                     RealmsBackupScreen.this.backupList.replaceEntries(RealmsBackupScreen.this.backups.stream().map((x$0) -> RealmsBackupScreen.this.new Entry(x$0)).toList());
                  }

               });
            } catch (RealmsServiceException e) {
               RealmsBackupScreen.LOGGER.error("Couldn't request backups", e);
            }

         }
      }).start();
   }

   public void onClose() {
      this.minecraft.setScreen(this.lastScreen);
   }

   private void downloadClicked() {
      this.minecraft.setScreen(RealmsPopups.infoPopupScreen(this, Component.translatable("mco.configure.world.restore.download.question.line1"), (popup) -> {
         Minecraft var10000 = this.minecraft;
         RealmsConfigureWorldScreen var10003 = this.lastScreen.getNewScreen();
         LongRunningTask[] var10004 = new LongRunningTask[1];
         long var10009 = this.serverData.id;
         int var10010 = this.slotId;
         String var10011 = (String)Objects.requireNonNullElse(this.serverData.name, "");
         var10004[0] = new DownloadTask(var10009, var10010, var10011 + " (" + ((RealmsSlot)this.serverData.slots.get(this.serverData.activeSlot)).options.getSlotName(this.serverData.activeSlot) + ")", this);
         var10000.setScreen(new RealmsLongRunningMcoTaskScreen(var10003, var10004));
      }));
   }

   static {
      SHORT_DATE_FORMAT = Util.localizedDateFormatter(FormatStyle.SHORT);
   }

   private class BackupObjectSelectionList extends ContainerObjectSelectionList<Entry> {
      private static final int ITEM_HEIGHT = 36;

      public BackupObjectSelectionList() {
         Objects.requireNonNull(RealmsBackupScreen.this);
         super(Minecraft.getInstance(), RealmsBackupScreen.this.width, RealmsBackupScreen.this.layout.getContentHeight(), RealmsBackupScreen.this.layout.getHeaderHeight(), 36);
      }

      public int getRowWidth() {
         return 300;
      }
   }

   private class Entry extends ContainerObjectSelectionList.Entry<Entry> {
      private static final int Y_PADDING = 2;
      private final Backup backup;
      private @Nullable Button restoreButton;
      private @Nullable Button changesButton;
      private final List<AbstractWidget> children;

      public Entry(final Backup backup) {
         Objects.requireNonNull(RealmsBackupScreen.this);
         super();
         this.children = new ArrayList();
         this.backup = backup;
         this.populateChangeList(backup);
         if (!backup.changeList.isEmpty()) {
            this.changesButton = Button.builder(RealmsBackupScreen.HAS_CHANGES_TOOLTIP, (button) -> RealmsBackupScreen.this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, this.backup))).width(8 + RealmsBackupScreen.this.font.width((FormattedText)RealmsBackupScreen.HAS_CHANGES_TOOLTIP)).createNarration(this::narrationForBackupEntry).build();
            this.children.add(this.changesButton);
         }

         if (!RealmsBackupScreen.this.serverData.expired) {
            this.restoreButton = Button.builder(RealmsBackupScreen.RESTORE_TOOLTIP, (button) -> this.restoreClicked()).width(8 + RealmsBackupScreen.this.font.width((FormattedText)RealmsBackupScreen.HAS_CHANGES_TOOLTIP)).createNarration(this::narrationForBackupEntry).build();
            this.children.add(this.restoreButton);
         }

      }

      private MutableComponent narrationForBackupEntry(final Supplier<MutableComponent> defaultNarrationSupplier) {
         return CommonComponents.joinForNarration(Component.translatable("mco.backup.narration", RealmsBackupScreen.SHORT_DATE_FORMAT.format(this.backup.lastModifiedDate())), (Component)defaultNarrationSupplier.get());
      }

      private void populateChangeList(final Backup backup) {
         int index = RealmsBackupScreen.this.backups.indexOf(backup);
         if (index != RealmsBackupScreen.this.backups.size() - 1) {
            Backup olderBackup = (Backup)RealmsBackupScreen.this.backups.get(index + 1);

            for(String key : backup.metadata.keySet()) {
               if (!key.contains("uploaded") && olderBackup.metadata.containsKey(key)) {
                  if (!((String)backup.metadata.get(key)).equals(olderBackup.metadata.get(key))) {
                     this.addToChangeList(key);
                  }
               } else {
                  this.addToChangeList(key);
               }
            }

         }
      }

      private void addToChangeList(final String key) {
         if (key.contains("uploaded")) {
            String uploadedTime = RealmsBackupScreen.SHORT_DATE_FORMAT.format(this.backup.lastModifiedDate());
            this.backup.changeList.put(key, uploadedTime);
            this.backup.uploadedVersion = true;
         } else {
            this.backup.changeList.put(key, (String)this.backup.metadata.get(key));
         }

      }

      private void restoreClicked() {
         Component age = RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModified);
         String lastModifiedDate = RealmsBackupScreen.SHORT_DATE_FORMAT.format(this.backup.lastModifiedDate());
         Component popupMessage = Component.translatable("mco.configure.world.restore.question.line1", lastModifiedDate, age);
         RealmsBackupScreen.this.minecraft.setScreen(RealmsPopups.warningPopupScreen(RealmsBackupScreen.this, popupMessage, (popup) -> {
            RealmsConfigureWorldScreen newScreen = RealmsBackupScreen.this.lastScreen.getNewScreen();
            RealmsBackupScreen.this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(newScreen, new LongRunningTask[]{new RestoreTask(this.backup, RealmsBackupScreen.this.serverData.id, newScreen)}));
         }));
      }

      public List<? extends GuiEventListener> children() {
         return this.children;
      }

      public List<? extends NarratableEntry> narratables() {
         return this.children;
      }

      public void extractContent(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final boolean hovered, final float a) {
         int middle = this.getContentYMiddle();
         Objects.requireNonNull(RealmsBackupScreen.this.font);
         int firstLineYPos = middle - 9 - 2;
         int secondLineYPos = middle + 2;
         int color = this.backup.uploadedVersion ? -8388737 : -1;
         graphics.text(RealmsBackupScreen.this.font, (Component)Component.translatable("mco.backup.entry", RealmsUtil.convertToAgePresentationFromInstant(this.backup.lastModified)), this.getContentX(), firstLineYPos, color);
         graphics.text(RealmsBackupScreen.this.font, RealmsBackupScreen.SHORT_DATE_FORMAT.format(this.backup.lastModifiedDate()), this.getContentX(), secondLineYPos, -11776948);
         int iconXOffet = 0;
         int iconYPos = this.getContentYMiddle() - 10;
         if (this.restoreButton != null) {
            iconXOffet += this.restoreButton.getWidth() + 8;
            this.restoreButton.setX(this.getContentRight() - iconXOffet);
            this.restoreButton.setY(iconYPos);
            this.restoreButton.extractRenderState(graphics, mouseX, mouseY, a);
         }

         if (this.changesButton != null) {
            iconXOffet += this.changesButton.getWidth() + 8;
            this.changesButton.setX(this.getContentRight() - iconXOffet);
            this.changesButton.setY(iconYPos);
            this.changesButton.extractRenderState(graphics, mouseX, mouseY, a);
         }

      }
   }
}
