package com.mojang.blaze3d;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.jtracy.TracyClient;
import java.nio.ByteBuffer;

public class TracyFrameCapture implements AutoCloseable {
   private static final int MAX_WIDTH = 320;
   private static final int MAX_HEIGHT = 180;
   private static final int BYTES_PER_PIXEL = 4;
   private int targetWidth;
   private int targetHeight;
   private int width;
   private int height;
   private final RenderTarget frameBuffer = new TextureTarget(320, 180, false);
   private final int pixelbuffer = GlStateManager._glGenBuffers();
   private long fence;
   private boolean inProgress;
   private int lastCaptureDelay;
   private boolean capturedThisFrame;

   public TracyFrameCapture() {
      super();
   }

   private void resize(int var1, int var2) {
      float var3 = (float)var1 / (float)var2;
      if (var1 > 320) {
         var1 = 320;
         var2 = (int)(320.0F / var3);
      }

      if (var2 > 180) {
         var1 = (int)(180.0F * var3);
         var2 = 180;
      }

      var1 = var1 / 4 * 4;
      var2 = var2 / 4 * 4;
      if (this.width != var1 || this.height != var2) {
         this.width = var1;
         this.height = var2;
         this.frameBuffer.resize(var1, var2);
         GlStateManager._glBindBuffer(35051, this.pixelbuffer);
         GlStateManager._glBufferData(35051, (long)var1 * (long)var2 * 4L, 35041);
         GlStateManager._glBindBuffer(35051, 0);
         this.inProgress = false;
      }
   }

   public void capture(RenderTarget var1) {
      if (!this.inProgress && !this.capturedThisFrame) {
         this.capturedThisFrame = true;
         if (var1.width != this.targetWidth || var1.height != this.targetHeight) {
            this.targetWidth = var1.width;
            this.targetHeight = var1.height;
            this.resize(this.targetWidth, this.targetHeight);
         }

         GlStateManager._glBindFramebuffer(36009, this.frameBuffer.frameBufferId);
         GlStateManager._glBindFramebuffer(36008, var1.frameBufferId);
         GlStateManager._glBlitFrameBuffer(0, 0, var1.width, var1.height, 0, 0, this.width, this.height, 16384, 9729);
         GlStateManager._glBindFramebuffer(36008, 0);
         GlStateManager._glBindFramebuffer(36009, 0);
         GlStateManager._glBindBuffer(35051, this.pixelbuffer);
         GlStateManager._glBindFramebuffer(36008, this.frameBuffer.frameBufferId);
         GlStateManager._readPixels(0, 0, this.width, this.height, 6408, 5121, 0L);
         GlStateManager._glBindFramebuffer(36008, 0);
         GlStateManager._glBindBuffer(35051, 0);
         this.fence = GlStateManager._glFenceSync(37143, 0);
         this.inProgress = true;
         this.lastCaptureDelay = 0;
      }
   }

   public void upload() {
      if (this.inProgress) {
         if (GlStateManager._glClientWaitSync(this.fence, 0, 0) != 37147) {
            GlStateManager._glDeleteSync(this.fence);
            GlStateManager._glBindBuffer(35051, this.pixelbuffer);
            ByteBuffer var1 = GlStateManager._glMapBuffer(35051, 35000);
            if (var1 != null) {
               TracyClient.frameImage(var1, this.width, this.height, this.lastCaptureDelay, true);
            }

            GlStateManager._glUnmapBuffer(35051);
            GlStateManager._glBindBuffer(35051, 0);
            this.inProgress = false;
         }
      }
   }

   public void endFrame() {
      this.lastCaptureDelay++;
      this.capturedThisFrame = false;
      TracyClient.markFrame();
   }

   @Override
   public void close() {
      if (this.inProgress) {
         GlStateManager._glDeleteSync(this.fence);
         this.inProgress = false;
      }

      GlStateManager._glDeleteBuffers(this.pixelbuffer);
      this.frameBuffer.destroyBuffers();
   }
}
