package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.Unit;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.level.storage.LevelSummary;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.slf4j.Logger;

public class RealmsUploadScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ReentrantLock UPLOAD_LOCK = new ReentrantLock();
   private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
   private static final Component VERIFYING_TEXT = Component.translatable("mco.upload.verifying");
   private final RealmsResetWorldScreen lastScreen;
   private final LevelSummary selectedLevel;
   private final long worldId;
   private final int slotId;
   private final UploadStatus uploadStatus;
   private final RateLimiter narrationRateLimiter;
   @Nullable
   private volatile Component[] errorMessage;
   private volatile Component status = Component.translatable("mco.upload.preparing");
   private volatile String progress;
   private volatile boolean cancelled;
   private volatile boolean uploadFinished;
   private volatile boolean showDots = true;
   private volatile boolean uploadStarted;
   private Button backButton;
   private Button cancelButton;
   private int tickCount;
   @Nullable
   private Long previousWrittenBytes;
   @Nullable
   private Long previousTimeSnapshot;
   private long bytesPersSecond;
   private final Runnable callback;

   public RealmsUploadScreen(long var1, int var3, RealmsResetWorldScreen var4, LevelSummary var5, Runnable var6) {
      super(NarratorChatListener.NO_TITLE);
      this.worldId = var1;
      this.slotId = var3;
      this.lastScreen = var4;
      this.selectedLevel = var5;
      this.uploadStatus = new UploadStatus();
      this.narrationRateLimiter = RateLimiter.create(0.10000000149011612);
      this.callback = var6;
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.backButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_BACK, (var1) -> {
         this.onBack();
      }));
      this.backButton.visible = false;
      this.cancelButton = (Button)this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.onCancel();
      }));
      if (!this.uploadStarted) {
         if (this.lastScreen.slot == -1) {
            this.upload();
         } else {
            this.lastScreen.switchSlot(() -> {
               if (!this.uploadStarted) {
                  this.uploadStarted = true;
                  this.minecraft.setScreen(this);
                  this.upload();
               }

            });
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onBack() {
      this.callback.run();
   }

   private void onCancel() {
      this.cancelled = true;
      this.minecraft.setScreen(this.lastScreen);
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

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten == this.uploadStatus.totalBytes) {
         this.status = VERIFYING_TEXT;
         this.cancelButton.active = false;
      }

      drawCenteredString(var1, this.font, this.status, this.width / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots(var1);
      }

      if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar(var1);
         this.drawUploadSpeed(var1);
      }

      if (this.errorMessage != null) {
         for(int var5 = 0; var5 < this.errorMessage.length; ++var5) {
            drawCenteredString(var1, this.font, this.errorMessage[var5], this.width / 2, 110 + 12 * var5, 16711680);
         }
      }

      super.render(var1, var2, var3, var4);
   }

   private void drawDots(PoseStack var1) {
      int var2 = this.font.width((FormattedText)this.status);
      this.font.draw(var1, DOTS[this.tickCount / 10 % DOTS.length], (float)(this.width / 2 + var2 / 2 + 5), 50.0F, 16777215);
   }

   private void drawProgressBar(PoseStack var1) {
      double var2 = Math.min((double)this.uploadStatus.bytesWritten / (double)this.uploadStatus.totalBytes, 1.0);
      this.progress = String.format(Locale.ROOT, "%.1f", var2 * 100.0);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      double var4 = (double)(this.width / 2 - 100);
      double var6 = 0.5;
      Tesselator var8 = Tesselator.getInstance();
      BufferBuilder var9 = var8.getBuilder();
      var9.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      var9.vertex(var4 - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
      var9.vertex(var4 + 200.0 * var2 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
      var9.vertex(var4 + 200.0 * var2 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
      var9.vertex(var4 - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
      var9.vertex(var4, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
      var9.vertex(var4 + 200.0 * var2, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
      var9.vertex(var4 + 200.0 * var2, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
      var9.vertex(var4, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
      var8.end();
      RenderSystem.enableTexture();
      drawCenteredString(var1, this.font, this.progress + " %", this.width / 2, 84, 16777215);
   }

   private void drawUploadSpeed(PoseStack var1) {
      if (this.tickCount % 20 == 0) {
         if (this.previousWrittenBytes != null) {
            long var2 = Util.getMillis() - this.previousTimeSnapshot;
            if (var2 == 0L) {
               var2 = 1L;
            }

            this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / var2;
            this.drawUploadSpeed0(var1, this.bytesPersSecond);
         }

         this.previousWrittenBytes = this.uploadStatus.bytesWritten;
         this.previousTimeSnapshot = Util.getMillis();
      } else {
         this.drawUploadSpeed0(var1, this.bytesPersSecond);
      }

   }

   private void drawUploadSpeed0(PoseStack var1, long var2) {
      if (var2 > 0L) {
         int var4 = this.font.width(this.progress);
         String var5 = "(" + Unit.humanReadable(var2) + "/s)";
         this.font.draw(var1, var5, (float)(this.width / 2 + var4 / 2 + 15), 84.0F, 16777215);
      }

   }

   public void tick() {
      super.tick();
      ++this.tickCount;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         Component var1 = this.createProgressNarrationMessage();
         NarratorChatListener.INSTANCE.sayNow(var1);
      }

   }

   private Component createProgressNarrationMessage() {
      ArrayList var1 = Lists.newArrayList();
      var1.add(this.status);
      if (this.progress != null) {
         var1.add(Component.literal(this.progress + "%"));
      }

      if (this.errorMessage != null) {
         var1.addAll(Arrays.asList(this.errorMessage));
      }

      return CommonComponents.joinLines((Collection)var1);
   }

   private void upload() {
      this.uploadStarted = true;
      (new Thread(() -> {
         File var1 = null;
         RealmsClient var2 = RealmsClient.create();
         long var3 = this.worldId;

         try {
            if (UPLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
               UploadInfo var5 = null;

               for(int var6 = 0; var6 < 20; ++var6) {
                  try {
                     if (this.cancelled) {
                        this.uploadCancelled();
                        return;
                     }

                     var5 = var2.requestUploadInfo(var3, UploadTokenCache.get(var3));
                     if (var5 != null) {
                        break;
                     }
                  } catch (RetryCallException var20) {
                     Thread.sleep((long)(var20.delaySeconds * 1000));
                  }
               }

               if (var5 == null) {
                  this.status = Component.translatable("mco.upload.close.failure");
                  return;
               }

               UploadTokenCache.put(var3, var5.getToken());
               if (!var5.isWorldClosed()) {
                  this.status = Component.translatable("mco.upload.close.failure");
                  return;
               }

               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               File var25 = new File(this.minecraft.gameDirectory.getAbsolutePath(), "saves");
               var1 = this.tarGzipArchive(new File(var25, this.selectedLevel.getLevelId()));
               if (this.cancelled) {
                  this.uploadCancelled();
                  return;
               }

               if (!this.verify(var1)) {
                  long var26 = var1.length();
                  Unit var9 = Unit.getLargest(var26);
                  Unit var10 = Unit.getLargest(5368709120L);
                  if (Unit.humanReadable(var26, var9).equals(Unit.humanReadable(5368709120L, var10)) && var9 != Unit.B) {
                     Unit var11 = Unit.values()[var9.ordinal() - 1];
                     this.setErrorMessage(Component.translatable("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), Component.translatable("mco.upload.size.failure.line2", Unit.humanReadable(var26, var11), Unit.humanReadable(5368709120L, var11)));
                     return;
                  }

                  this.setErrorMessage(Component.translatable("mco.upload.size.failure.line1", this.selectedLevel.getLevelName()), Component.translatable("mco.upload.size.failure.line2", Unit.humanReadable(var26, var9), Unit.humanReadable(5368709120L, var10)));
                  return;
               }

               this.status = Component.translatable("mco.upload.uploading", this.selectedLevel.getLevelName());
               FileUpload var7 = new FileUpload(var1, this.worldId, this.slotId, var5, this.minecraft.getUser(), SharedConstants.getCurrentVersion().getName(), this.uploadStatus);
               var7.upload((var3x) -> {
                  if (var3x.statusCode >= 200 && var3x.statusCode < 300) {
                     this.uploadFinished = true;
                     this.status = Component.translatable("mco.upload.done");
                     this.backButton.setMessage(CommonComponents.GUI_DONE);
                     UploadTokenCache.invalidate(var3);
                  } else if (var3x.statusCode == 400 && var3x.errorMessage != null) {
                     this.setErrorMessage(Component.translatable("mco.upload.failed", var3x.errorMessage));
                  } else {
                     this.setErrorMessage(Component.translatable("mco.upload.failed", var3x.statusCode));
                  }

               });

               while(!var7.isFinished()) {
                  if (this.cancelled) {
                     var7.cancel();
                     this.uploadCancelled();
                     return;
                  }

                  try {
                     Thread.sleep(500L);
                  } catch (InterruptedException var19) {
                     LOGGER.error("Failed to check Realms file upload status");
                  }
               }

               return;
            }

            this.status = Component.translatable("mco.upload.close.failure");
         } catch (IOException var21) {
            this.setErrorMessage(Component.translatable("mco.upload.failed", var21.getMessage()));
            return;
         } catch (RealmsServiceException var22) {
            this.setErrorMessage(Component.translatable("mco.upload.failed", var22.toString()));
            return;
         } catch (InterruptedException var23) {
            LOGGER.error("Could not acquire upload lock");
            return;
         } finally {
            this.uploadFinished = true;
            if (UPLOAD_LOCK.isHeldByCurrentThread()) {
               UPLOAD_LOCK.unlock();
               this.showDots = false;
               this.backButton.visible = true;
               this.cancelButton.visible = false;
               if (var1 != null) {
                  LOGGER.debug("Deleting file {}", var1.getAbsolutePath());
                  var1.delete();
               }

            }

            return;
         }

      })).start();
   }

   private void setErrorMessage(Component... var1) {
      this.errorMessage = var1;
   }

   private void uploadCancelled() {
      this.status = Component.translatable("mco.upload.cancelled");
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
}
