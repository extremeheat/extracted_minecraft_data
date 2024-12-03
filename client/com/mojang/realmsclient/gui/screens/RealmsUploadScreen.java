package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.client.worldupload.RealmsUploadException;
import com.mojang.realmsclient.client.worldupload.RealmsWorldUpload;
import com.mojang.realmsclient.client.worldupload.RealmsWorldUploadStatusTracker;
import com.mojang.realmsclient.dto.RealmsWorldOptions;
import com.mojang.realmsclient.util.task.LongRunningTask;
import com.mojang.realmsclient.util.task.RealmCreationTask;
import com.mojang.realmsclient.util.task.SwitchSlotTask;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;

public class RealmsUploadScreen extends RealmsScreen implements RealmsWorldUploadStatusTracker {
   private static final int BAR_WIDTH = 200;
   private static final int BAR_TOP = 80;
   private static final int BAR_BOTTOM = 95;
   private static final int BAR_BORDER = 1;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private static final Component VERIFYING_TEXT = Component.translatable("mco.upload.verifying");
   private final RealmsResetWorldScreen lastScreen;
   private final LevelSummary selectedLevel;
   @Nullable
   private final RealmCreationTask realmCreationTask;
   private final long realmId;
   private final int slotId;
   final AtomicReference<RealmsWorldUpload> currentUpload = new AtomicReference();
   private final UploadStatus uploadStatus;
   private final RateLimiter narrationRateLimiter;
   @Nullable
   private volatile Component[] errorMessage;
   private volatile Component status = Component.translatable("mco.upload.preparing");
   @Nullable
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean uploadFinished;
   private volatile boolean showDots = true;
   private volatile boolean uploadStarted;
   @Nullable
   private Button backButton;
   @Nullable
   private Button cancelButton;
   private int tickCount;
   private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public RealmsUploadScreen(@Nullable RealmCreationTask var1, long var2, int var4, RealmsResetWorldScreen var5, LevelSummary var6) {
      super(GameNarrator.NO_TITLE);
      this.realmCreationTask = var1;
      this.realmId = var2;
      this.slotId = var4;
      this.lastScreen = var5;
      this.selectedLevel = var6;
      this.uploadStatus = new UploadStatus();
      this.narrationRateLimiter = RateLimiter.create(0.10000000149011612);
   }

   public void init() {
      this.backButton = (Button)this.layout.addToFooter(Button.builder(CommonComponents.GUI_BACK, (var1x) -> this.onBack()).build());
      this.backButton.visible = false;
      this.cancelButton = (Button)this.layout.addToFooter(Button.builder(CommonComponents.GUI_CANCEL, (var1x) -> this.onCancel()).build());
      if (!this.uploadStarted) {
         if (this.lastScreen.slot == -1) {
            this.uploadStarted = true;
            this.upload();
         } else {
            ArrayList var1 = new ArrayList();
            if (this.realmCreationTask != null) {
               var1.add(this.realmCreationTask);
            }

            var1.add(new SwitchSlotTask(this.realmId, this.lastScreen.slot, () -> {
               if (!this.uploadStarted) {
                  this.uploadStarted = true;
                  this.minecraft.execute(() -> {
                     this.minecraft.setScreen(this);
                     this.upload();
                  });
               }

            }));
            this.minecraft.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, (LongRunningTask[])var1.toArray(new LongRunningTask[0])));
         }
      }

      this.layout.visitWidgets((var1x) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1x);
      });
      this.repositionElements();
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   private void onBack() {
      this.minecraft.setScreen(new RealmsConfigureWorldScreen(new RealmsMainScreen(new TitleScreen()), this.realmId));
   }

   private void onCancel() {
      this.cancelled = true;
      RealmsWorldUpload var1 = (RealmsWorldUpload)this.currentUpload.get();
      if (var1 != null) {
         var1.cancel();
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         if (this.showDots) {
            this.onCancel();
         } else {
            this.onBack();
         }

         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      if (!this.uploadFinished && this.uploadStatus.uploadStarted() && this.uploadStatus.uploadCompleted() && this.cancelButton != null) {
         this.status = VERIFYING_TEXT;
         this.cancelButton.active = false;
      }

      var1.drawCenteredString(this.font, (Component)this.status, this.width / 2, 50, -1);
      if (this.showDots) {
         var1.drawString(this.font, (String)DOTS[this.tickCount / 10 % DOTS.length], this.width / 2 + this.font.width((FormattedText)this.status) / 2 + 5, 50, -1);
      }

      if (this.uploadStatus.uploadStarted() && !this.cancelled) {
         this.drawProgressBar(var1);
         this.drawUploadSpeed(var1);
      }

      Component[] var5 = this.errorMessage;
      if (var5 != null) {
         for(int var6 = 0; var6 < var5.length; ++var6) {
            var1.drawCenteredString(this.font, var5[var6], this.width / 2, 110 + 12 * var6, -65536);
         }
      }

   }

   private void drawProgressBar(GuiGraphics var1) {
      double var2 = this.uploadStatus.getPercentage();
      this.progress = String.format(Locale.ROOT, "%.1f", var2 * 100.0);
      int var4 = (this.width - 200) / 2;
      int var5 = var4 + (int)Math.round(200.0 * var2);
      var1.fill(var4 - 1, 79, var5 + 1, 96, -1);
      var1.fill(var4, 80, var5, 95, -8355712);
      var1.drawCenteredString(this.font, (Component)Component.translatable("mco.upload.percent", this.progress), this.width / 2, 84, -1);
   }

   private void drawUploadSpeed(GuiGraphics var1) {
      this.drawUploadSpeed0(var1, this.uploadStatus.getBytesPerSecond());
   }

   private void drawUploadSpeed0(GuiGraphics var1, long var2) {
      String var4 = this.progress;
      if (var2 > 0L && var4 != null) {
         int var5 = this.font.width(var4);
         String var6 = "(" + Unit.humanReadable(var2) + "/s)";
         var1.drawString(this.font, (String)var6, this.width / 2 + var5 / 2 + 15, 84, -1);
      }

   }

   public void tick() {
      super.tick();
      ++this.tickCount;
      this.uploadStatus.refreshBytesPerSecond();
      if (this.narrationRateLimiter.tryAcquire(1)) {
         Component var1 = this.createProgressNarrationMessage();
         this.minecraft.getNarrator().sayNow(var1);
      }

   }

   private Component createProgressNarrationMessage() {
      ArrayList var1 = Lists.newArrayList();
      var1.add(this.status);
      if (this.progress != null) {
         var1.add(Component.translatable("mco.upload.percent", this.progress));
      }

      Component[] var2 = this.errorMessage;
      if (var2 != null) {
         var1.addAll(Arrays.asList(var2));
      }

      return CommonComponents.joinLines((Collection)var1);
   }

   private void upload() {
      Path var1 = this.minecraft.gameDirectory.toPath().resolve("saves").resolve(this.selectedLevel.getLevelId());
      RealmsWorldOptions var2 = RealmsWorldOptions.createFromSettings(this.selectedLevel.getSettings(), this.selectedLevel.levelVersion().minecraftVersionName());
      RealmsWorldUpload var3 = new RealmsWorldUpload(var1, var2, this.minecraft.getUser(), this.realmId, this.slotId, this);
      if (!this.currentUpload.compareAndSet((Object)null, var3)) {
         throw new IllegalStateException("Tried to start uploading but was already uploading");
      } else {
         var3.packAndUpload().handleAsync((var1x, var2x) -> {
            if (var2x != null) {
               if (var2x instanceof CompletionException) {
                  CompletionException var3 = (CompletionException)var2x;
                  var2x = var3.getCause();
               }

               if (var2x instanceof RealmsUploadException) {
                  RealmsUploadException var4 = (RealmsUploadException)var2x;
                  if (var4.getStatusMessage() != null) {
                     this.status = var4.getStatusMessage();
                  }

                  this.setErrorMessage(var4.getErrorMessages());
               } else {
                  this.status = Component.translatable("mco.upload.failed", var2x.getMessage());
               }
            } else {
               this.status = Component.translatable("mco.upload.done");
               if (this.backButton != null) {
                  this.backButton.setMessage(CommonComponents.GUI_DONE);
               }
            }

            this.uploadFinished = true;
            this.showDots = false;
            if (this.backButton != null) {
               this.backButton.visible = true;
            }

            if (this.cancelButton != null) {
               this.cancelButton.visible = false;
            }

            this.currentUpload.set((Object)null);
            return null;
         }, this.minecraft);
      }
   }

   private void setErrorMessage(@Nullable Component... var1) {
      this.errorMessage = var1;
   }

   public UploadStatus getUploadStatus() {
      return this.uploadStatus;
   }

   public void setUploading() {
      this.status = Component.translatable("mco.upload.uploading", this.selectedLevel.getLevelName());
   }
}
