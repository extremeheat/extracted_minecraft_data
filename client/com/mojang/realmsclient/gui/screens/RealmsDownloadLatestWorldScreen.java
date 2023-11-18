package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
   private static final int BAR_WIDTH = 200;
   private static final int BAR_TOP = 80;
   private static final int BAR_BOTTOM = 95;
   private static final int BAR_BORDER = 1;
   private final Screen lastScreen;
   private final WorldDownload worldDownload;
   private final Component downloadTitle;
   private final RateLimiter narrationRateLimiter;
   private Button cancelButton;
   private final String worldName;
   private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
   @Nullable
   private volatile Component errorMessage;
   private volatile Component status = Component.translatable("mco.download.preparing");
   @Nullable
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean showDots = true;
   private volatile boolean finished;
   private volatile boolean extracting;
   @Nullable
   private Long previousWrittenBytes;
   @Nullable
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private int animTick;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private int dotIndex;
   private boolean checked;
   private final BooleanConsumer callback;

   public RealmsDownloadLatestWorldScreen(Screen var1, WorldDownload var2, String var3, BooleanConsumer var4) {
      super(GameNarrator.NO_TITLE);
      this.callback = var4;
      this.lastScreen = var1;
      this.worldName = var3;
      this.worldDownload = var2;
      this.downloadStatus = new RealmsDownloadLatestWorldScreen.DownloadStatus();
      this.downloadTitle = Component.translatable("mco.download.title");
      this.narrationRateLimiter = RateLimiter.create(0.10000000149011612);
   }

   @Override
   public void init() {
      this.cancelButton = this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, var1 -> {
         this.cancelled = true;
         this.backButtonClicked();
      }).bounds((this.width - 200) / 2, this.height - 42, 200, 20).build());
      this.checkDownloadSize();
   }

   private void checkDownloadSize() {
      if (!this.finished) {
         if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
            MutableComponent var1 = Component.translatable("mco.download.confirmation.line1", Unit.humanReadable(5368709120L));
            MutableComponent var2 = Component.translatable("mco.download.confirmation.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen(var1x -> {
               this.checked = true;
               this.minecraft.setScreen(this);
               this.downloadSave();
            }, RealmsLongConfirmationScreen.Type.WARNING, var1, var2, false));
         } else {
            this.downloadSave();
         }
      }
   }

   private long getContentLength(String var1) {
      FileDownload var2 = new FileDownload();
      return var2.contentLength(var1);
   }

   @Override
   public void tick() {
      super.tick();
      ++this.animTick;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         Component var1 = this.createProgressNarrationMessage();
         this.minecraft.getNarrator().sayNow(var1);
      }
   }

   private Component createProgressNarrationMessage() {
      ArrayList var1 = Lists.newArrayList();
      var1.add(this.downloadTitle);
      var1.add(this.status);
      if (this.progress != null) {
         var1.add(Component.literal(this.progress + "%"));
         var1.add(Component.literal(Unit.humanReadable(this.bytesPersSecond) + "/s"));
      }

      if (this.errorMessage != null) {
         var1.add(this.errorMessage);
      }

      return CommonComponents.joinLines(var1);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.cancelled = true;
         this.backButtonClicked();
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   private void backButtonClicked() {
      if (this.finished && this.callback != null && this.errorMessage == null) {
         this.callback.accept(true);
      }

      this.minecraft.setScreen(this.lastScreen);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      var1.drawCenteredString(this.font, this.downloadTitle, this.width / 2, 20, 16777215);
      var1.drawCenteredString(this.font, this.status, this.width / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots(var1);
      }

      if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar(var1);
         this.drawDownloadSpeed(var1);
      }

      if (this.errorMessage != null) {
         var1.drawCenteredString(this.font, this.errorMessage, this.width / 2, 110, 16711680);
      }

      super.render(var1, var2, var3, var4);
   }

   private void drawDots(GuiGraphics var1) {
      int var2 = this.font.width(this.status);
      if (this.animTick % 10 == 0) {
         ++this.dotIndex;
      }

      var1.drawString(this.font, DOTS[this.dotIndex % DOTS.length], this.width / 2 + var2 / 2 + 5, 50, 16777215, false);
   }

   private void drawProgressBar(GuiGraphics var1) {
      double var2 = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0);
      this.progress = String.format(Locale.ROOT, "%.1f", var2 * 100.0);
      int var4 = (this.width - 200) / 2;
      int var5 = var4 + (int)Math.round(200.0 * var2);
      var1.fill(var4 - 1, 79, var5 + 1, 96, -2501934);
      var1.fill(var4, 80, var5, 95, -8355712);
      var1.drawCenteredString(this.font, Component.translatable("mco.download.percent", this.progress), this.width / 2, 84, 16777215);
   }

   private void drawDownloadSpeed(GuiGraphics var1) {
      if (this.animTick % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long var2 = Util.getMillis() - this.previousTimeSnapshot;
            if (var2 == 0L) {
               var2 = 1L;
            }

            this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / var2;
            this.drawDownloadSpeed0(var1, this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.downloadStatus.bytesWritten;
         this.previousTimeSnapshot = Util.getMillis();
      } else {
         this.drawDownloadSpeed0(var1, this.bytesPersSecond);
      }
   }

   private void drawDownloadSpeed0(GuiGraphics var1, long var2) {
      if (var2 > 0L) {
         int var4 = this.font.width(this.progress);
         var1.drawString(
            this.font, Component.translatable("mco.download.speed", Unit.humanReadable(var2)), this.width / 2 + var4 / 2 + 15, 84, 16777215, false
         );
      }
   }

   private void downloadSave() {
      new Thread(() -> {
         try {
            try {
               if (!DOWNLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                  this.status = Component.translatable("mco.download.failed");
                  return;
               }

               if (this.cancelled) {
                  this.downloadCancelled();
                  return;
               }

               this.status = Component.translatable("mco.download.downloading", this.worldName);
               FileDownload var1 = new FileDownload();
               var1.contentLength(this.worldDownload.downloadLink);
               var1.download(this.worldDownload, this.worldName, this.downloadStatus, this.minecraft.getLevelSource());

               while(!var1.isFinished()) {
                  if (var1.isError()) {
                     var1.cancel();
                     this.errorMessage = Component.translatable("mco.download.failed");
                     this.cancelButton.setMessage(CommonComponents.GUI_DONE);
                     return;
                  }

                  if (var1.isExtracting()) {
                     if (!this.extracting) {
                        this.status = Component.translatable("mco.download.extracting");
                     }

                     this.extracting = true;
                  }

                  if (this.cancelled) {
                     var1.cancel();
                     this.downloadCancelled();
                     return;
                  }

                  try {
                     Thread.sleep(500L);
                  } catch (InterruptedException var8) {
                     LOGGER.error("Failed to check Realms backup download status");
                  }
               }

               this.finished = true;
               this.status = Component.translatable("mco.download.done");
               this.cancelButton.setMessage(CommonComponents.GUI_DONE);
               return;
            } catch (InterruptedException var9) {
               LOGGER.error("Could not acquire upload lock");
            } catch (Exception var10) {
               this.errorMessage = Component.translatable("mco.download.failed");
               var10.printStackTrace();
            }
         } finally {
            if (!DOWNLOAD_LOCK.isHeldByCurrentThread()) {
               return;
            } else {
               DOWNLOAD_LOCK.unlock();
               this.showDots = false;
               this.finished = true;
            }
         }
      }).start();
   }

   private void downloadCancelled() {
      this.status = Component.translatable("mco.download.cancelled");
   }

   public static class DownloadStatus {
      public volatile long bytesWritten;
      public volatile long totalBytes;

      public DownloadStatus() {
         super();
      }
   }
}
