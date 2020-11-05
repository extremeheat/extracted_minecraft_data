package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.util.RealmsUtil;
import com.mojang.realmsclient.util.task.DownloadTask;
import com.mojang.realmsclient.util.task.RestoreTask;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsLabel;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsBackupScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation PLUS_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/plus_icon.png");
   private static final ResourceLocation RESTORE_ICON_LOCATION = new ResourceLocation("realms", "textures/gui/realms/restore_icon.png");
   private static final Component RESTORE_TOOLTIP = new TranslatableComponent("mco.backup.button.restore");
   private static final Component HAS_CHANGES_TOOLTIP = new TranslatableComponent("mco.backup.changes.tooltip");
   private static final Component TITLE = new TranslatableComponent("mco.configure.world.backup");
   private static final Component NO_BACKUPS_LABEL = new TranslatableComponent("mco.backup.nobackups");
   private static int lastScrollPosition = -1;
   private final RealmsConfigureWorldScreen lastScreen;
   private List<Backup> backups = Collections.emptyList();
   @Nullable
   private Component toolTip;
   private RealmsBackupScreen.BackupObjectSelectionList backupObjectSelectionList;
   private int selectedBackup = -1;
   private final int slotId;
   private Button downloadButton;
   private Button restoreButton;
   private Button changesButton;
   private Boolean noBackups = false;
   private final RealmsServer serverData;
   private RealmsLabel titleLabel;

   public RealmsBackupScreen(RealmsConfigureWorldScreen var1, RealmsServer var2, int var3) {
      super();
      this.lastScreen = var1;
      this.serverData = var2;
      this.slotId = var3;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.backupObjectSelectionList = new RealmsBackupScreen.BackupObjectSelectionList();
      if (lastScrollPosition != -1) {
         this.backupObjectSelectionList.setScrollAmount((double)lastScrollPosition);
      }

      (new Thread("Realms-fetch-backups") {
         public void run() {
            RealmsClient var1 = RealmsClient.create();

            try {
               List var2 = var1.backupsFor(RealmsBackupScreen.this.serverData.id).backups;
               RealmsBackupScreen.this.minecraft.execute(() -> {
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
      this.downloadButton = (Button)this.addButton(new Button(this.width - 135, row(1), 120, 20, new TranslatableComponent("mco.backup.button.download"), (var1) -> {
         this.downloadClicked();
      }));
      this.restoreButton = (Button)this.addButton(new Button(this.width - 135, row(3), 120, 20, new TranslatableComponent("mco.backup.button.restore"), (var1) -> {
         this.restoreClicked(this.selectedBackup);
      }));
      this.changesButton = (Button)this.addButton(new Button(this.width - 135, row(5), 120, 20, new TranslatableComponent("mco.backup.changes.tooltip"), (var1) -> {
         this.minecraft.setScreen(new RealmsBackupInfoScreen(this, (Backup)this.backups.get(this.selectedBackup)));
         this.selectedBackup = -1;
      }));
      this.addButton(new Button(this.width - 100, this.height - 35, 85, 20, CommonComponents.GUI_BACK, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.addWidget(this.backupObjectSelectionList);
      this.titleLabel = (RealmsLabel)this.addWidget(new RealmsLabel(new TranslatableComponent("mco.configure.world.backup"), this.width / 2, 12, 16777215));
      this.magicalSpecialHackyFocus(this.backupObjectSelectionList);
      this.updateButtonStates();
      this.narrateLabels();
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

   private void updateButtonStates() {
      this.restoreButton.visible = this.shouldRestoreButtonBeVisible();
      this.changesButton.visible = this.shouldChangesButtonBeVisible();
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

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.minecraft.setScreen(this.lastScreen);
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
         String var4 = RealmsUtil.convertToAgePresentationFromInstant(var2);
         TranslatableComponent var5 = new TranslatableComponent("mco.configure.world.restore.question.line1", new Object[]{var3, var4});
         TranslatableComponent var6 = new TranslatableComponent("mco.configure.world.restore.question.line2");
         this.minecraft.setScreen(new RealmsLongConfirmationScreen((var1x) -> {
            if (var1x) {
               this.restore();
            } else {
               this.selectedBackup = -1;
               this.minecraft.setScreen(this);
            }

         }, RealmsLongConfirmationScreen.Type.Warning, var5, var6, true));
      }

   }

   private void downloadClicked() {
      TranslatableComponent var1 = new TranslatableComponent("mco.configure.world.restore.download.question.line1");
      TranslatableComponent var2 = new TranslatableComponent("mco.configure.world.restore.download.question.line2");
      this.minecraft.setScreen(new RealmsLongConfirmationScreen((var1x) -> {
         if (var1x) {
            this.downloadWorldData();
         } else {
            this.minecraft.setScreen(this);
         }

      }, RealmsLongConfirmationScreen.Type.Info, var1, var2, true));
   }

   private void downloadWorldData() {
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new DownloadTask(this.serverData.id, this.slotId, this.serverData.name + " (" + ((RealmsWorldOptions)this.serverData.slots.get(this.serverData.activeSlot)).getSlotName(this.serverData.activeSlot) + ")", this)));
   }

   private void restore() {
      Backup var1 = (Backup)this.backups.get(this.selectedBackup);
      this.selectedBackup = -1;
      this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), new RestoreTask(var1, this.serverData.id, this.lastScreen)));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.toolTip = null;
      this.renderBackground(var1);
      this.backupObjectSelectionList.render(var1, var2, var3, var4);
      this.titleLabel.render(this, var1);
      this.font.draw(var1, TITLE, (float)((this.width - 150) / 2 - 90), 20.0F, 10526880);
      if (this.noBackups) {
         this.font.draw(var1, NO_BACKUPS_LABEL, 20.0F, (float)(this.height / 2 - 10), 16777215);
      }

      this.downloadButton.active = !this.noBackups;
      super.render(var1, var2, var3, var4);
      if (this.toolTip != null) {
         this.renderMousehoverTooltip(var1, this.toolTip, var2, var3);
      }

   }

   protected void renderMousehoverTooltip(PoseStack var1, @Nullable Component var2, int var3, int var4) {
      if (var2 != null) {
         int var5 = var3 + 12;
         int var6 = var4 - 12;
         int var7 = this.font.width((FormattedText)var2);
         this.fillGradient(var1, var5 - 3, var6 - 3, var5 + var7 + 3, var6 + 8 + 3, -1073741824, -1073741824);
         this.font.drawShadow(var1, var2, (float)var5, (float)var6, 16777215);
      }
   }

   class Entry extends ObjectSelectionList.Entry<RealmsBackupScreen.Entry> {
      private final Backup backup;

      public Entry(Backup var2) {
         super();
         this.backup = var2;
      }

      public void render(PoseStack var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, float var10) {
         this.renderBackupItem(var1, this.backup, var4 - 40, var3, var7, var8);
      }

      private void renderBackupItem(PoseStack var1, Backup var2, int var3, int var4, int var5, int var6) {
         int var7 = var2.isUploadedVersion() ? -8388737 : 16777215;
         RealmsBackupScreen.this.font.draw(var1, "Backup (" + RealmsUtil.convertToAgePresentationFromInstant(var2.lastModifiedDate) + ")", (float)(var3 + 40), (float)(var4 + 1), var7);
         RealmsBackupScreen.this.font.draw(var1, this.getMediumDatePresentation(var2.lastModifiedDate), (float)(var3 + 40), (float)(var4 + 12), 5000268);
         int var8 = RealmsBackupScreen.this.width - 175;
         boolean var9 = true;
         int var10 = var8 - 10;
         boolean var11 = false;
         if (!RealmsBackupScreen.this.serverData.expired) {
            this.drawRestore(var1, var8, var4 + -3, var5, var6);
         }

         if (!var2.changeList.isEmpty()) {
            this.drawInfo(var1, var10, var4 + 0, var5, var6);
         }

      }

      private String getMediumDatePresentation(Date var1) {
         return DateFormat.getDateTimeInstance(3, 3).format(var1);
      }

      private void drawRestore(PoseStack var1, int var2, int var3, int var4, int var5) {
         boolean var6 = var4 >= var2 && var4 <= var2 + 12 && var5 >= var3 && var5 <= var3 + 14 && var5 < RealmsBackupScreen.this.height - 15 && var5 > 32;
         RealmsBackupScreen.this.minecraft.getTextureManager().bind(RealmsBackupScreen.RESTORE_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         float var7 = var6 ? 28.0F : 0.0F;
         GuiComponent.blit(var1, var2 * 2, var3 * 2, 0.0F, var7, 23, 28, 23, 56);
         RenderSystem.popMatrix();
         if (var6) {
            RealmsBackupScreen.this.toolTip = RealmsBackupScreen.RESTORE_TOOLTIP;
         }

      }

      private void drawInfo(PoseStack var1, int var2, int var3, int var4, int var5) {
         boolean var6 = var4 >= var2 && var4 <= var2 + 8 && var5 >= var3 && var5 <= var3 + 8 && var5 < RealmsBackupScreen.this.height - 15 && var5 > 32;
         RealmsBackupScreen.this.minecraft.getTextureManager().bind(RealmsBackupScreen.PLUS_ICON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.5F, 0.5F, 0.5F);
         float var7 = var6 ? 15.0F : 0.0F;
         GuiComponent.blit(var1, var2 * 2, var3 * 2, 0.0F, var7, 15, 15, 15, 30);
         RenderSystem.popMatrix();
         if (var6) {
            RealmsBackupScreen.this.toolTip = RealmsBackupScreen.HAS_CHANGES_TOOLTIP;
         }

      }
   }

   class BackupObjectSelectionList extends RealmsObjectSelectionList<RealmsBackupScreen.Entry> {
      public BackupObjectSelectionList() {
         super(RealmsBackupScreen.this.width - 150, RealmsBackupScreen.this.height, 32, RealmsBackupScreen.this.height - 15, 36);
      }

      public void addEntry(Backup var1) {
         this.addEntry(RealmsBackupScreen.this.new Entry(var1));
      }

      public int getRowWidth() {
         return (int)((double)this.width * 0.93D);
      }

      public boolean isFocused() {
         return RealmsBackupScreen.this.getFocused() == this;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground(PoseStack var1) {
         RealmsBackupScreen.this.renderBackground(var1);
      }

      public boolean mouseClicked(double var1, double var3, int var5) {
         if (var5 != 0) {
            return false;
         } else if (var1 < (double)this.getScrollbarPosition() && var3 >= (double)this.y0 && var3 <= (double)this.y1) {
            int var6 = this.width / 2 - 92;
            int var7 = this.width;
            int var8 = (int)Math.floor(var3 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount();
            int var9 = var8 / this.itemHeight;
            if (var1 >= (double)var6 && var1 <= (double)var7 && var9 >= 0 && var8 >= 0 && var9 < this.getItemCount()) {
               this.selectItem(var9);
               this.itemClicked(var8, var9, var1, var3, this.width);
            }

            return true;
         } else {
            return false;
         }
      }

      public int getScrollbarPosition() {
         return this.width - 5;
      }

      public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
         int var8 = this.width - 35;
         int var9 = var2 * this.itemHeight + 36 - (int)this.getScrollAmount();
         int var10 = var8 + 10;
         int var11 = var9 - 3;
         if (var3 >= (double)var8 && var3 <= (double)(var8 + 9) && var5 >= (double)var9 && var5 <= (double)(var9 + 9)) {
            if (!((Backup)RealmsBackupScreen.this.backups.get(var2)).changeList.isEmpty()) {
               RealmsBackupScreen.this.selectedBackup = -1;
               RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
               this.minecraft.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(var2)));
            }
         } else if (var3 >= (double)var10 && var3 < (double)(var10 + 13) && var5 >= (double)var11 && var5 < (double)(var11 + 15)) {
            RealmsBackupScreen.lastScrollPosition = (int)this.getScrollAmount();
            RealmsBackupScreen.this.restoreClicked(var2);
         }

      }

      public void selectItem(int var1) {
         this.setSelectedItem(var1);
         if (var1 != -1) {
            NarrationHelper.now(I18n.get("narrator.select", ((Backup)RealmsBackupScreen.this.backups.get(var1)).lastModifiedDate.toString()));
         }

         this.selectInviteListItem(var1);
      }

      public void selectInviteListItem(int var1) {
         RealmsBackupScreen.this.selectedBackup = var1;
         RealmsBackupScreen.this.updateButtonStates();
      }

      public void setSelected(@Nullable RealmsBackupScreen.Entry var1) {
         super.setSelected(var1);
         RealmsBackupScreen.this.selectedBackup = this.children().indexOf(var1);
         RealmsBackupScreen.this.updateButtonStates();
      }
   }
}
