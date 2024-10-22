package com.mojang.blaze3d;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuFence;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.jtracy.TracyClient;
import javax.annotation.Nullable;

public class TracyFrameCapture implements AutoCloseable {
   private static final int MAX_WIDTH = 320;
   private static final int MAX_HEIGHT = 180;
   private static final int BYTES_PER_PIXEL = 4;
   private int targetWidth;
   private int targetHeight;
   private int width;
   private int height;
   private final RenderTarget frameBuffer = new TextureTarget(320, 180, false);
   private final GpuBuffer pixelbuffer = new GpuBuffer(BufferType.PIXEL_PACK, BufferUsage.STREAM_READ, 0);
   @Nullable
   private GpuFence fence;
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
         this.pixelbuffer.resize(var1 * var2 * 4);
         if (this.fence != null) {
            this.fence.close();
            this.fence = null;
         }
      }
   }

   public void capture(RenderTarget var1) {
      if (this.fence == null && !this.capturedThisFrame) {
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
         this.pixelbuffer.bind();
         GlStateManager._glBindFramebuffer(36008, this.frameBuffer.frameBufferId);
         GlStateManager._readPixels(0, 0, this.width, this.height, 6408, 5121, 0L);
         GlStateManager._glBindFramebuffer(36008, 0);
         this.fence = new GpuFence();
         this.lastCaptureDelay = 0;
      }
   }

   public void upload() {
      if (this.fence != null) {
         if (this.fence.awaitCompletion(0L)) {
            this.fence = null;

            try (GpuBuffer.ReadView var1 = this.pixelbuffer.read()) {
               if (var1 != null) {
                  TracyClient.frameImage(var1.data(), this.width, this.height, this.lastCaptureDelay, true);
               }
            }
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
      if (this.fence != null) {
         this.fence.close();
         this.fence = null;
      }

      this.pixelbuffer.close();
      this.frameBuffer.destroyBuffers();
   }
}
