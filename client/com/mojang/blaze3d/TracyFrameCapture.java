package com.mojang.blaze3d;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.jtracy.TracyClient;
import java.util.OptionalInt;
import net.minecraft.client.renderer.RenderPipelines;

public class TracyFrameCapture implements AutoCloseable {
   private static final int MAX_WIDTH = 320;
   private static final int MAX_HEIGHT = 180;
   private static final int BYTES_PER_PIXEL = 4;
   private int targetWidth;
   private int targetHeight;
   private int width;
   private int height;
   private GpuTexture frameBuffer;
   private GpuBuffer pixelbuffer;
   private int lastCaptureDelay;
   private boolean capturedThisFrame;
   private Status status;

   public TracyFrameCapture() {
      super();
      this.status = TracyFrameCapture.Status.WAITING_FOR_CAPTURE;
      this.width = 320;
      this.height = 180;
      this.frameBuffer = RenderSystem.getDevice().createTexture("Tracy Frame Capture", TextureFormat.RGBA8, this.width, this.height, 1);
      this.pixelbuffer = RenderSystem.getDevice().createBuffer(() -> "Tracy Frame Capture buffer", 9, this.width * this.height * 4);
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
         this.frameBuffer.close();
         this.frameBuffer = RenderSystem.getDevice().createTexture("Tracy Frame Capture", TextureFormat.RGBA8, var1, var2, 1);
         this.pixelbuffer.close();
         this.pixelbuffer = RenderSystem.getDevice().createBuffer(() -> "Tracy Frame Capture buffer", 9, var1 * var2 * 4);
      }

   }

   public void capture(RenderTarget var1) {
      if (this.status == TracyFrameCapture.Status.WAITING_FOR_CAPTURE && !this.capturedThisFrame && var1.getColorTexture() != null) {
         this.capturedThisFrame = true;
         if (var1.width != this.targetWidth || var1.height != this.targetHeight) {
            this.targetWidth = var1.width;
            this.targetHeight = var1.height;
            this.resize(this.targetWidth, this.targetHeight);
         }

         this.status = TracyFrameCapture.Status.WAITING_FOR_COPY;
         CommandEncoder var2 = RenderSystem.getDevice().createCommandEncoder();
         RenderSystem.AutoStorageIndexBuffer var3 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
         GpuBuffer var4 = var3.getBuffer(6);

         try (RenderPass var5 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(this.frameBuffer, OptionalInt.empty())) {
            var5.setPipeline(RenderPipelines.TRACY_BLIT);
            var5.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
            var5.setIndexBuffer(var4, var3.type());
            var5.bindSampler("InSampler", var1.getColorTexture());
            var5.drawIndexed(0, 6);
         }

         var2.copyTextureToBuffer(this.frameBuffer, this.pixelbuffer, 0, () -> this.status = TracyFrameCapture.Status.WAITING_FOR_UPLOAD, 0);
         this.lastCaptureDelay = 0;
      }
   }

   public void upload() {
      if (this.status == TracyFrameCapture.Status.WAITING_FOR_UPLOAD) {
         this.status = TracyFrameCapture.Status.WAITING_FOR_CAPTURE;

         try (GpuBuffer.MappedView var1 = RenderSystem.getDevice().createCommandEncoder().mapBuffer(this.pixelbuffer, true, false)) {
            TracyClient.frameImage(var1.data(), this.width, this.height, this.lastCaptureDelay, true);
         }

      }
   }

   public void endFrame() {
      ++this.lastCaptureDelay;
      this.capturedThisFrame = false;
      TracyClient.markFrame();
   }

   public void close() {
      this.frameBuffer.close();
      this.pixelbuffer.close();
   }

   static enum Status {
      WAITING_FOR_CAPTURE,
      WAITING_FOR_COPY,
      WAITING_FOR_UPLOAD;

      private Status() {
      }

      // $FF: synthetic method
      private static Status[] $values() {
         return new Status[]{WAITING_FOR_CAPTURE, WAITING_FOR_COPY, WAITING_FOR_UPLOAD};
      }
   }
}
