package com.mojang.realmsclient.gui.screens;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.realmsclient.Unit;
import com.mojang.realmsclient.client.FileDownload;
import com.mojang.realmsclient.dto.WorldDownload;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.NarrationHelper;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsDownloadLatestWorldScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ReentrantLock DOWNLOAD_LOCK = new ReentrantLock();
   private final Screen lastScreen;
   private final WorldDownload worldDownload;
   private final Component downloadTitle;
   private final RateLimiter narrationRateLimiter;
   private Button cancelButton;
   private final String worldName;
   private final RealmsDownloadLatestWorldScreen.DownloadStatus downloadStatus;
   private volatile Component errorMessage;
   private volatile Component status = new TranslatableComponent("mco.download.preparing");
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
   private boolean checked;
   private final BooleanConsumer callback;

   public RealmsDownloadLatestWorldScreen(Screen var1, WorldDownload var2, String var3, BooleanConsumer var4) {
      super();
      this.callback = var4;
      this.lastScreen = var1;
      this.worldName = var3;
      this.worldDownload = var2;
      this.downloadStatus = new RealmsDownloadLatestWorldScreen.DownloadStatus();
      this.downloadTitle = new TranslatableComponent("mco.download.title");
      this.narrationRateLimiter = RateLimiter.create(0.10000000149011612D);
   }

   public void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.cancelButton = (Button)this.addButton(new Button(this.width / 2 - 100, this.height - 42, 200, 20, CommonComponents.GUI_CANCEL, (var1) -> {
         this.cancelled = true;
         this.backButtonClicked();
      }));
      this.checkDownloadSize();
   }

   private void checkDownloadSize() {
      if (!this.finished) {
         if (!this.checked && this.getContentLength(this.worldDownload.downloadLink) >= 5368709120L) {
            TranslatableComponent var1 = new TranslatableComponent("mco.download.confirmation.line1", new Object[]{Unit.humanReadable(5368709120L)});
            TranslatableComponent var2 = new TranslatableComponent("mco.download.confirmation.line2");
            this.minecraft.setScreen(new RealmsLongConfirmationScreen((var1x) -> {
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

   public void tick() {
      super.tick();
      ++this.animTick;
      if (this.status != null && this.narrationRateLimiter.tryAcquire(1)) {
         ArrayList var1 = Lists.newArrayList();
         var1.add(this.downloadTitle);
         var1.add(this.status);
         if (this.progress != null) {
            var1.add(new TextComponent(this.progress + "%"));
            var1.add(new TextComponent(Unit.humanReadable(this.bytesPersSecond) + "/s"));
         }

         if (this.errorMessage != null) {
            var1.add(this.errorMessage);
         }

         String var2 = (String)var1.stream().map(Component::getString).collect(Collectors.joining("\n"));
         NarrationHelper.now(var2);
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
      if (this.finished && this.callback != null && this.errorMessage == null) {
         this.callback.accept(true);
      }

      this.minecraft.setScreen(this.lastScreen);
   }

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
      int var2 = this.font.width((FormattedText)this.status);
      if (this.animTick % 10 == 0) {
         ++this.dotIndex;
      }

      this.font.draw(var1, DOTS[this.dotIndex % DOTS.length], (float)(this.width / 2 + var2 / 2 + 5), 50.0F, 16777215);
   }

   private void drawProgressBar(PoseStack var1) {
      double var2 = Math.min((double)this.downloadStatus.bytesWritten / (double)this.downloadStatus.totalBytes, 1.0D);
      this.progress = String.format(Locale.ROOT, "%.1f", var2 * 100.0D);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.disableTexture();
      Tesselator var4 = Tesselator.getInstance();
      BufferBuilder var5 = var4.getBuilder();
      var5.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      double var6 = (double)(this.width / 2 - 100);
      double var8 = 0.5D;
      var5.vertex(var6 - 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6 + 200.0D * var2 + 0.5D, 95.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6 + 200.0D * var2 + 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6 - 0.5D, 79.5D, 0.0D).color(217, 210, 210, 255).endVertex();
      var5.vertex(var6, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var5.vertex(var6 + 200.0D * var2, 95.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var5.vertex(var6 + 200.0D * var2, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
      var5.vertex(var6, 80.0D, 0.0D).color(128, 128, 128, 255).endVertex();
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
      (new Thread(() -> {
         try {
            try {
               if (!DOWNLOAD_LOCK.tryLock(1L, TimeUnit.SECONDS)) {
                  this.status = new TranslatableComponent("mco.download.failed");
                  return;
               }

               if (this.cancelled) {
                  this.downloadCancelled();
                  return;
               }

               this.status = new TranslatableComponent("mco.download.downloading", new Object[]{this.worldName});
               FileDownload var1 = new FileDownload();
               var1.contentLength(this.worldDownload.downloadLink);
               var1.download(this.worldDownload, this.worldName, this.downloadStatus, this.minecraft.getLevelSource());

               while(!var1.isFinished()) {
                  if (var1.isError()) {
                     var1.cancel();
                     this.errorMessage = new TranslatableComponent("mco.download.failed");
                     this.cancelButton.setMessage(CommonComponents.GUI_DONE);
                     return;
                  }

                  if (var1.isExtracting()) {
                     if (!this.extracting) {
                        this.status = new TranslatableComponent("mco.download.extracting");
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
               this.status = new TranslatableComponent("mco.download.done");
               this.cancelButton.setMessage(CommonComponents.GUI_DONE);
               return;
            } catch (InterruptedException var9) {
               LOGGER.error("Could not acquire upload lock");
            } catch (Exception var10) {
               this.errorMessage = new TranslatableComponent("mco.download.failed");
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
      })).start();
   }

   private void downloadCancelled() {
      this.status = new TranslatableComponent("mco.download.cancelled");
   }

   public class DownloadStatus {
      public volatile long bytesWritten;
      public volatile long totalBytes;

      public DownloadStatus() {
         super();
      }
   }
}
