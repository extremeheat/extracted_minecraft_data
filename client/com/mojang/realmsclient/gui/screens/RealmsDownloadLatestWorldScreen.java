package com.mojang.realmsclient.gui.screens;

import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final WorldDownload worldDownload;
   private final String downloadTitle;
   private final RateLimiter narrationRateLimiter;
   private RealmsButton cancelButton;
   private final String worldName;
   private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
   private volatile String errorMessage;
   private volatile String status;
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean showDots = true;
   private volatile boolean finished;
   private volatile boolean extracting;
   private Long previousWrittenBytes;
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private int animTick;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private int dotIndex;
   private final int WARNING_ID = 100;
   private int confirmationId = -1;
   private boolean checked;
   private static final ReentrantLock downloadLock = new ReentrantLock();

   public RealmsDownloadLatestWorldScreen(RealmsScreen var1, WorldDownload var2, String var3) {
      super();
      this.lastScreen = var1;
      this.worldName = var3;
      this.worldDownload = var2;
      this.downloadStatus = new RealmsDownloadLatestWorldScreen.DownloadStatus();
      this.downloadTitle = getLocalizedString("mco.download.title");
      this.narrationRateLimiter = RateLimiter.create(0.10000000149011612D);
   }

   public void setConfirmationId(int var1) {
      this.confirmationId = var1;
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsDownloadLatestWorldScreen.this.cancelled = true;
            RealmsDownloadLatestWorldScreen.this.backButtonClicked();
         }
      });
      this.checkDownloadSize();
   }

   private void checkDownloadSize() {
      if (!this.finished) {
         if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
            String var1 = getLocalizedString("mco.download.confirmation.line1", new Object[]{humanReadableSize(5368709120L)});
            String var2 = getLocalizedString("mco.download.confirmation.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, var1, var2, false, 100));
         } else {
            this.downloadSave();
         }

      }
   }

   public void confirmResult(boolean var1, int var2) {
      this.checked = true;
      Realms.setScreen(this);
      this.downloadSave();
   }

   private long getContentLength(String var1) {
      FileDownload var2 = new FileDownload();
      return var2.contentLength(var1);
   }

   public void tick() {
      super.tick();
      ++this.animTick;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         ArrayList var1 = new ArrayList();
         var1.add(this.downloadTitle);
         var1.add(this.status);
         if (this.progress != null) {
            var1.add(this.progress + "%");
            var1.add(humanReadableSpeed(this.bytesPersSecond));
         }

         if (this.errorMessage != null) {
            var1.add(this.errorMessage);
         }

         String var2 = String.join(System.lineSeparator(), var1);
         Realms.narrateNow(var2);
      }

   }

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
      if (this.finished && this.confirmationId != -1 && this.errorMessage == null) {
         this.lastScreen.confirmResult(true, this.confirmationId);
      }

      Realms.setScreen(this.lastScreen);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      if (this.extracting && !this.finished) {
         this.status = getLocalizedString("mco.download.extracting");
      }

      this.drawCenteredString(this.downloadTitle, this.width() / 2, 20, 16777215);
      this.drawCenteredString(this.status, this.width() / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots();
      }

      if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar();
         this.drawDownloadSpeed();
      }

      if (this.errorMessage != null) {
         this.drawCenteredString(this.errorMessage, this.width() / 2, 110, 16711680);
      }

      super.render(var1, var2, var3);
   }

   private void drawDots() {
      int var1 = this.fontWidth(this.status);
      if (this.animTick % 10 == 0) {
         ++this.dotIndex;
      }

      this.drawString(DOTS[this.dotIndex % DOTS.length], this.width() / 2 + var1 / 2 + 5, 50, 16777215);
   }

   private void drawProgressBar() {
      double var1 = this.downloadStatus.bytesWritten.doubleValue() / this.downloadStatus.totalBytes.doubleValue() * 100.0D;
      this.progress = String.format(Locale.ROOT, "%.1f", var1);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableTexture();
      Tezzelator var3 = Tezzelator.instance;
      var3.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
      double var4 = (double)(this.width() / 2 - 100);
      double var6 = 0.5D;
      var3.vertex(var4 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var3.vertex(var4 + 200.0D * var1 / 100.0D + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var3.vertex(var4 + 200.0D * var1 / 100.0D + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var3.vertex(var4 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var3.vertex(var4, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var3.vertex(var4 + 200.0D * var1 / 100.0D, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var3.vertex(var4 + 200.0D * var1 / 100.0D, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var3.vertex(var4, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var3.end();
      GlStateManager.enableTexture();
      this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 16777215);
   }

   private void drawDownloadSpeed() {
      if (this.animTick % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long var1 = System.currentTimeMillis() - this.previousTimeSnapshot;
            if (var1 == 0L) {
               var1 = 1L;
            }

            this.bytesPersSecond = 1000L * (this.downloadStatus.bytesWritten - this.previousWrittenBytes) / var1;
            this.drawDownloadSpeed0(this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.downloadStatus.bytesWritten;
         this.previousTimeSnapshot = System.currentTimeMillis();
      } else {
         this.drawDownloadSpeed0(this.bytesPersSecond);
      }

   }

   private void drawDownloadSpeed0(long var1) {
      if (var1 > 0L) {
         int var3 = this.fontWidth(this.progress);
         String var4 = "(" + humanReadableSpeed(var1) + ")";
         this.drawString(var4, this.width() / 2 + var3 / 2 + 15, 84, 16777215);
      }

   }

   public static String humanReadableSpeed(long var0) {
      boolean var2 = true;
      if (var0 < 1024L) {
         return var0 + " B/s";
      } else {
         int var3 = (int)(Math.log((double)var0) / Math.log(1024.0D));
         String var4 = "KMGTPE".charAt(var3 - 1) + "";
         return String.format(Locale.ROOT, "%.1f %sB/s", (double)var0 / Math.pow(1024.0D, (double)var3), var4);
      }
   }

   public static String humanReadableSize(long var0) {
      boolean var2 = true;
      if (var0 < 1024L) {
         return var0 + " B";
      } else {
         int var3 = (int)(Math.log((double)var0) / Math.log(1024.0D));
         String var4 = "KMGTPE".charAt(var3 - 1) + "";
         return String.format(Locale.ROOT, "%.0f %sB", (double)var0 / Math.pow(1024.0D, (double)var3), var4);
      }
   }

   private void downloadSave() {
      (new Thread() {
         public void run() {
            try {
               if (!RealmsDownloadLatestWorldScreen.downloadLock.tryLock(1L, TimeUnit.SECONDS)) {
                  return;
               }

               RealmsDownloadLatestWorldScreen.this.status = RealmsScreen.getLocalizedString("mco.download.preparing");
               if (!RealmsDownloadLatestWorldScreen.this.cancelled) {
                  RealmsDownloadLatestWorldScreen.this.status = RealmsScreen.getLocalizedString("mco.download.downloading", RealmsDownloadLatestWorldScreen.this.worldName);
                  FileDownload var1 = new FileDownload();
                  var1.contentLength(RealmsDownloadLatestWorldScreen.this.worldDownload.downloadLink);
                  var1.download(RealmsDownloadLatestWorldScreen.this.worldDownload, RealmsDownloadLatestWorldScreen.this.worldName, RealmsDownloadLatestWorldScreen.this.downloadStatus, RealmsDownloadLatestWorldScreen.this.getLevelStorageSource());

                  while(!var1.isFinished()) {
                     if (var1.isError()) {
                        var1.cancel();
                        RealmsDownloadLatestWorldScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.download.failed");
                        RealmsDownloadLatestWorldScreen.this.cancelButton.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                        return;
                     }

                     if (var1.isExtracting()) {
                        RealmsDownloadLatestWorldScreen.this.extracting = true;
                     }

                     if (RealmsDownloadLatestWorldScreen.this.cancelled) {
                        var1.cancel();
                        RealmsDownloadLatestWorldScreen.this.downloadCancelled();
                        return;
                     }

                     try {
                        Thread.sleep(500L);
                     } catch (InterruptedException var8) {
                        RealmsDownloadLatestWorldScreen.LOGGER.error("Failed to check Realms backup download status");
                     }
                  }

                  RealmsDownloadLatestWorldScreen.this.finished = true;
                  RealmsDownloadLatestWorldScreen.this.status = RealmsScreen.getLocalizedString("mco.download.done");
                  RealmsDownloadLatestWorldScreen.this.cancelButton.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                  return;
               }

               RealmsDownloadLatestWorldScreen.this.downloadCancelled();
            } catch (InterruptedException var9) {
               RealmsDownloadLatestWorldScreen.LOGGER.error("Could not acquire upload lock");
               return;
            } catch (Exception var10) {
               RealmsDownloadLatestWorldScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.download.failed");
               var10.printStackTrace();
               return;
            } finally {
               if (!RealmsDownloadLatestWorldScreen.downloadLock.isHeldByCurrentThread()) {
                  return;
               }

               RealmsDownloadLatestWorldScreen.downloadLock.unlock();
               RealmsDownloadLatestWorldScreen.this.showDots = false;
               RealmsDownloadLatestWorldScreen.this.finished = true;
            }

         }
      }).start();
   }

   private void downloadCancelled() {
      this.status = getLocalizedString("mco.download.cancelled");
   }

   public class DownloadStatus {
      public volatile Long bytesWritten = 0L;
      public volatile Long totalBytes = 0L;

      public DownloadStatus() {
         super();
      }
   }
}
