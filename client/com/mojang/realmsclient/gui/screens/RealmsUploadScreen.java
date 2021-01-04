package com.mojang.realmsclient.gui.screens;

import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.realmsclient.client.FileUpload;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.dto.UploadInfo;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsResetWorldScreen lastScreen;
   private final RealmsLevelSummary selectedLevel;
   private final long worldId;
   private final int slotId;
   private final UploadStatus uploadStatus;
   private final RateLimiter narrationRateLimiter;
   private volatile String errorMessage;
   private volatile String status;
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean uploadFinished;
   private volatile boolean showDots = true;
   private volatile boolean uploadStarted;
   private RealmsButton backButton;
   private RealmsButton cancelButton;
   private int animTick;
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private int dotIndex;
   private Long previousWrittenBytes;
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private static final ReentrantLock uploadLock = new ReentrantLock();

   public RealmsUploadScreen(long var1, int var3, RealmsResetWorldScreen var4, RealmsLevelSummary var5) {
      super();
      this.worldId = var1;
      this.slotId = var3;
      this.lastScreen = var4;
      this.selectedLevel = var5;
      this.uploadStatus = new UploadStatus();
      this.narrationRateLimiter = RateLimiter.create(0.10000000149011612D);
   }

   public void init() {
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.backButton = new RealmsButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.back")) {
         public void onPress() {
            RealmsUploadScreen.this.onBack();
         }
      };
      this.buttonsAdd(this.cancelButton = new RealmsButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, getLocalizedString("gui.cancel")) {
         public void onPress() {
            RealmsUploadScreen.this.onCancel();
         }
      });
      if (!this.uploadStarted) {
         if (this.lastScreen.slot == -1) {
            this.upload();
         } else {
            this.lastScreen.switchSlot(this);
         }
      }

   }

   public void confirmResult(boolean var1, int var2) {
      if (var1 && !this.uploadStarted) {
         this.uploadStarted = true;
         Realms.setScreen(this);
         this.upload();
      }

   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   private void onBack() {
      this.lastScreen.confirmResult(true, 0);
   }

   private void onCancel() {
      this.cancelled = true;
      Realms.setScreen(this.lastScreen);
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

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
         this.status = getLocalizedString("mco.upload.verifying");
         this.cancelButton.active(false);
      }

      this.drawCenteredString(this.status, this.width() / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots();
      }

      if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar();
         this.drawUploadSpeed();
      }

      if (this.errorMessage != null) {
         String[] var4 = this.errorMessage.split("\\\\n");

         for(int var5 = 0; var5 < var4.length; ++var5) {
            this.drawCenteredString(var4[var5], this.width() / 2, 110 + 12 * var5, 16711680);
         }
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
      double var1 = this.uploadStatus.bytesWritten.doubleValue() / this.uploadStatus.totalBytes.doubleValue() * 100.0D;
      if (var1 > 100.0D) {
         var1 = 100.0D;
      }

      this.progress = String.format(Locale.ROOT, "%.1f", var1);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableTexture();
      double var3 = (double)(this.width() / 2 - 100);
      double var5 = 0.5D;
      Tezzelator var7 = Tezzelator.instance;
      var7.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
      var7.vertex(var3 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var7.vertex(var3 + 200.0D * var1 / 100.0D + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var7.vertex(var3 + 200.0D * var1 / 100.0D + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var7.vertex(var3 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var7.vertex(var3, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var7.vertex(var3 + 200.0D * var1 / 100.0D, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var7.vertex(var3 + 200.0D * var1 / 100.0D, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var7.vertex(var3, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var7.end();
      GlStateManager.enableTexture();
      this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 16777215);
   }

   private void drawUploadSpeed() {
      if (this.animTick % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long var1 = System.currentTimeMillis() - this.previousTimeSnapshot;
            if (var1 == 0L) {
               var1 = 1L;
            }

            this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / var1;
            this.drawUploadSpeed0(this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.uploadStatus.bytesWritten;
         this.previousTimeSnapshot = System.currentTimeMillis();
      } else {
         this.drawUploadSpeed0(this.bytesPersSecond);
      }

   }

   private void drawUploadSpeed0(long var1) {
      if (var1 > 0L) {
         int var3 = this.fontWidth(this.progress);
         String var4 = "(" + humanReadableByteCount(var1) + ")";
         this.drawString(var4, this.width() / 2 + var3 / 2 + 15, 84, 16777215);
      }

   }

   public static String humanReadableByteCount(long var0) {
      boolean var2 = true;
      if (var0 < 1024L) {
         return var0 + " B";
      } else {
         int var3 = (int)(Math.log((double)var0) / Math.log(1024.0D));
         String var4 = "KMGTPE".charAt(var3 - 1) + "";
         return String.format(Locale.ROOT, "%.1f %sB/s", (double)var0 / Math.pow(1024.0D, (double)var3), var4);
      }
   }

   public void tick() {
      super.tick();
      ++this.animTick;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         ArrayList var1 = new ArrayList();
         var1.add(this.status);
         if (this.progress != null) {
            var1.add(this.progress + "%");
         }

         if (this.errorMessage != null) {
            var1.add(this.errorMessage);
         }

         Realms.narrateNow(String.join(System.lineSeparator(), var1));
      }

   }

   public static RealmsUploadScreen.Unit getLargestUnit(long var0) {
      if (var0 < 1024L) {
         return RealmsUploadScreen.Unit.B;
      } else {
         int var2 = (int)(Math.log((double)var0) / Math.log(1024.0D));
         String var3 = "KMGTPE".charAt(var2 - 1) + "";

         try {
            return RealmsUploadScreen.Unit.valueOf(var3 + "B");
         } catch (Exception var5) {
            return RealmsUploadScreen.Unit.GB;
         }
      }
   }

   public static double convertToUnit(long var0, RealmsUploadScreen.Unit var2) {
      return var2.equals(RealmsUploadScreen.Unit.B) ? (double)var0 : (double)var0 / Math.pow(1024.0D, (double)var2.ordinal());
   }

   public static String humanReadableSize(long var0, RealmsUploadScreen.Unit var2) {
      return String.format("%." + (var2.equals(RealmsUploadScreen.Unit.GB) ? "1" : "0") + "f %s", convertToUnit(var0, var2), var2.name());
   }

   private void upload() {
      this.uploadStarted = true;
      (new Thread() {
         public void run() {
            File var1 = null;
            RealmsClient var2 = RealmsClient.createRealmsClient();
            long var3 = RealmsUploadScreen.this.worldId;

            try {
               if (!RealmsUploadScreen.uploadLock.tryLock(1L, TimeUnit.SECONDS)) {
                  return;
               }

               RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.preparing");
               UploadInfo var5 = null;
               int var6 = 0;

               while(var6 < 20) {
                  try {
                     if (RealmsUploadScreen.this.cancelled) {
                        RealmsUploadScreen.this.uploadCancelled();
                        return;
                     }

                     var5 = var2.upload(var3, UploadTokenCache.get(var3));
                     break;
                  } catch (RetryCallException var20) {
                     Thread.sleep((long)(var20.delaySeconds * 1000));
                     ++var6;
                  }
               }

               if (var5 == null) {
                  RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
                  return;
               }

               UploadTokenCache.put(var3, var5.getToken());
               if (!var5.isWorldClosed()) {
                  RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.close.failure");
                  return;
               }

               if (RealmsUploadScreen.this.cancelled) {
                  RealmsUploadScreen.this.uploadCancelled();
                  return;
               }

               File var25 = new File(Realms.getGameDirectoryPath(), "saves");
               var1 = RealmsUploadScreen.this.tarGzipArchive(new File(var25, RealmsUploadScreen.this.selectedLevel.getLevelId()));
               if (!RealmsUploadScreen.this.cancelled) {
                  if (RealmsUploadScreen.this.verify(var1)) {
                     RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.uploading", RealmsUploadScreen.this.selectedLevel.getLevelName());
                     FileUpload var26 = new FileUpload(var1, RealmsUploadScreen.this.worldId, RealmsUploadScreen.this.slotId, var5, Realms.getSessionId(), Realms.getName(), Realms.getMinecraftVersionString(), RealmsUploadScreen.this.uploadStatus);
                     var26.upload((var3x) -> {
                        if (var3x.statusCode >= 200 && var3x.statusCode < 300) {
                           RealmsUploadScreen.this.uploadFinished = true;
                           RealmsUploadScreen.this.status = RealmsScreen.getLocalizedString("mco.upload.done");
                           RealmsUploadScreen.this.backButton.setMessage(RealmsScreen.getLocalizedString("gui.done"));
                           UploadTokenCache.invalidate(var3);
                        } else if (var3x.statusCode == 400 && var3x.errorMessage != null) {
                           RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", var3x.errorMessage);
                        } else {
                           RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", var3x.statusCode);
                        }

                     });

                     while(!var26.isFinished()) {
                        if (RealmsUploadScreen.this.cancelled) {
                           var26.cancel();
                           RealmsUploadScreen.this.uploadCancelled();
                           return;
                        }

                        try {
                           Thread.sleep(500L);
                        } catch (InterruptedException var19) {
                           RealmsUploadScreen.LOGGER.error("Failed to check Realms file upload status");
                        }
                     }

                     return;
                  }

                  long var7 = var1.length();
                  RealmsUploadScreen.Unit var9 = RealmsUploadScreen.getLargestUnit(var7);
                  RealmsUploadScreen.Unit var10 = RealmsUploadScreen.getLargestUnit(5368709120L);
                  if (RealmsUploadScreen.humanReadableSize(var7, var9).equals(RealmsUploadScreen.humanReadableSize(5368709120L, var10)) && var9 != RealmsUploadScreen.Unit.B) {
                     RealmsUploadScreen.Unit var11 = RealmsUploadScreen.Unit.values()[var9.ordinal() - 1];
                     RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.size.failure.line1", RealmsUploadScreen.this.selectedLevel.getLevelName()) + "\\n" + RealmsScreen.getLocalizedString("mco.upload.size.failure.line2", RealmsUploadScreen.humanReadableSize(var7, var11), RealmsUploadScreen.humanReadableSize(5368709120L, var11));
                     return;
                  }

                  RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.size.failure.line1", RealmsUploadScreen.this.selectedLevel.getLevelName()) + "\\n" + RealmsScreen.getLocalizedString("mco.upload.size.failure.line2", RealmsUploadScreen.humanReadableSize(var7, var9), RealmsUploadScreen.humanReadableSize(5368709120L, var10));
                  return;
               }

               RealmsUploadScreen.this.uploadCancelled();
            } catch (IOException var21) {
               RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", var21.getMessage());
               return;
            } catch (RealmsServiceException var22) {
               RealmsUploadScreen.this.errorMessage = RealmsScreen.getLocalizedString("mco.upload.failed", var22.toString());
               return;
            } catch (InterruptedException var23) {
               RealmsUploadScreen.LOGGER.error("Could not acquire upload lock");
               return;
            } finally {
               RealmsUploadScreen.this.uploadFinished = true;
               if (RealmsUploadScreen.uploadLock.isHeldByCurrentThread()) {
                  RealmsUploadScreen.uploadLock.unlock();
                  RealmsUploadScreen.this.showDots = false;
                  RealmsUploadScreen.this.childrenClear();
                  RealmsUploadScreen.this.buttonsAdd(RealmsUploadScreen.this.backButton);
                  if (var1 != null) {
                     RealmsUploadScreen.LOGGER.debug("Deleting file " + var1.getAbsolutePath());
                     var1.delete();
                  }

               }

               return;
            }

         }
      }).start();
   }

   private void uploadCancelled() {
      this.status = getLocalizedString("mco.upload.cancelled");
      LOGGER.debug("Upload was cancelled");
   }

   private boolean verify(File var1) {
      return var1.length() < 5368709120L;
   }

   private File tarGzipArchive(File var1) throws IOException {
      TarArchiveOutputStream var2 = null;

      File var4;
      try {
         File var3 = File.createTempFile("realms-upload-file", ".tar.gz");
         var2 = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(var3)));
         var2.setLongFileMode(3);
         this.addFileToTarGz(var2, var1.getAbsolutePath(), "world", true);
         var2.finish();
         var4 = var3;
      } finally {
         if (var2 != null) {
            var2.close();
         }

      }

      return var4;
   }

   private void addFileToTarGz(TarArchiveOutputStream var1, String var2, String var3, boolean var4) throws IOException {
      if (!this.cancelled) {
         File var5 = new File(var2);
         String var6 = var4 ? var3 : var3 + var5.getName();
         TarArchiveEntry var7 = new TarArchiveEntry(var5, var6);
         var1.putArchiveEntry(var7);
         if (var5.isFile()) {
            IOUtils.copy(new FileInputStream(var5), var1);
            var1.closeArchiveEntry();
         } else {
            var1.closeArchiveEntry();
            File[] var8 = var5.listFiles();
            if (var8 != null) {
               File[] var9 = var8;
               int var10 = var8.length;

               for(int var11 = 0; var11 < var10; ++var11) {
                  File var12 = var9[var11];
                  this.addFileToTarGz(var1, var12.getAbsolutePath(), var6 + "/", false);
               }
            }
         }

      }
   }

   static enum Unit {
      B,
      KB,
      MB,
      GB;

      private Unit() {
      }
   }
}
