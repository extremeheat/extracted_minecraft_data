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
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import org.slf4j.Logger;

public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
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
      super(NarratorChatListener.NO_TITLE);
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
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.cancelButton = this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_CANCEL, var1 -> {
         this.cancelled = true;
         this.backButtonClicked();
      }));
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
            }, RealmsLongConfirmationScreen.Type.Warning, var1, var2, false));
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
         NarratorChatListener.INSTANCE.sayNow(var1);
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
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.downloadTitle, this.width / 2, 20, 16777215);
      drawCenteredString(var1, this.font, this.status, this.width / 2, 50, 16777215);
      if (this.showDots) {
         this.drawDots(var1);
      }

      if (this.downloadStatus.bytesWritten != 0L && !this.cancelled) {
         this.drawProgressBar(var1);
         this.drawDownloadSpeed(var1);
      }

      if (this.errorMessage != null) {
         drawCenteredString(var1, this.font, this.errorMessage, this.width / 2, 110, 16711680);
      }

      super.render(var1, var2, var3, var4);
   }

   private void drawDots(PoseStack var1) {
      int var2 = this.font.width(this.status);
      if (this.animTick % 10 == 0) {
         ++this.dotIndex;
      }

      this.font.draw(var1, DOTS[this.dotIndex % DOTS.length], (float)(this.width / 2 + var2 / 2 + 5), 50.0F, 16777215);
   }

   private void drawProgressBar(PoseStack var1) {
      double var2 = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0);
      this.progress = String.format(Locale.ROOT, "%.1f", var2 * 100.0);
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      Tesselator var4 = Tesselator.getInstance();
      BufferBuilder var5 = var4.getBuilder();
      var5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      double var6 = (double)(this.width / 2 - 100);
      double var8 = 0.5;
      var5.vertex(var6 - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6 + 200.0 * var2 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6 + 200.0 * var2 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6 - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
      var5.vertex(var6 + 200.0 * var2, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
      var5.vertex(var6 + 200.0 * var2, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
      var5.vertex(var6, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
      var4.end();
      RenderSystem.enableTexture();
      drawCenteredString(var1, this.font, this.progress + " %", this.width / 2, 84, 16777215);
   }

   private void drawDownloadSpeed(PoseStack var1) {
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

   private void drawDownloadSpeed0(PoseStack var1, long var2) {
      if (var2 > 0L) {
         int var4 = this.font.width(this.progress);
         String var5 = "(" + Unit.humanReadable(var2) + "/s)";
         this.font.draw(var1, var5, (float)(this.width / 2 + var4 / 2 + 15), 84.0F, 16777215);
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
