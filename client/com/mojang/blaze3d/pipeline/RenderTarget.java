package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.client.renderer.RenderPipelines;
import org.jspecify.annotations.Nullable;

public abstract class RenderTarget {
   private static int UNNAMED_RENDER_TARGETS = 0;
   public int width;
   public int height;
   protected final String label;
   public final boolean useDepth;
   protected @Nullable GpuTexture colorTexture;
   protected @Nullable GpuTextureView colorTextureView;
   protected @Nullable GpuTexture depthTexture;
   protected @Nullable GpuTextureView depthTextureView;

   public RenderTarget(final @Nullable String label, final boolean useDepth) {
      super();
      this.label = label == null ? "FBO " + UNNAMED_RENDER_TARGETS++ : label;
      this.useDepth = useDepth;
   }

   public void resize(final int width, final int height) {
      RenderSystem.assertOnRenderThread();
      this.destroyBuffers();
      this.createBuffers(width, height);
   }

   public void destroyBuffers() {
      RenderSystem.assertOnRenderThread();
      if (this.depthTexture != null) {
         this.depthTexture.close();
         this.depthTexture = null;
      }

      if (this.depthTextureView != null) {
         this.depthTextureView.close();
         this.depthTextureView = null;
      }

      if (this.colorTexture != null) {
         this.colorTexture.close();
         this.colorTexture = null;
      }

      if (this.colorTextureView != null) {
         this.colorTextureView.close();
         this.colorTextureView = null;
      }

   }

   public void copyDepthFrom(final RenderTarget source) {
      RenderSystem.assertOnRenderThread();
      if (this.depthTexture == null) {
         throw new IllegalStateException("Trying to copy depth texture to a RenderTarget without a depth texture");
      } else if (source.depthTexture == null) {
         throw new IllegalStateException("Trying to copy depth texture from a RenderTarget without a depth texture");
      } else {
         RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(source.depthTexture, this.depthTexture, 0, 0, 0, 0, 0, this.width, this.height);
      }
   }

   public void createBuffers(final int width, final int height) {
      RenderSystem.assertOnRenderThread();
      GpuDevice device = RenderSystem.getDevice();
      int maxTextureSize = device.getMaxTextureSize();
      if (width > 0 && width <= maxTextureSize && height > 0 && height <= maxTextureSize) {
         this.width = width;
         this.height = height;
         if (this.useDepth) {
            this.depthTexture = device.createTexture((Supplier)(() -> this.label + " / Depth"), 15, TextureFormat.DEPTH32, width, height, 1, 1);
            this.depthTextureView = device.createTextureView(this.depthTexture);
         }

         this.colorTexture = device.createTexture((Supplier)(() -> this.label + " / Color"), 15, TextureFormat.RGBA8, width, height, 1, 1);
         this.colorTextureView = device.createTextureView(this.colorTexture);
      } else {
         throw new IllegalArgumentException("Window " + width + "x" + height + " size out of bounds (max. size: " + maxTextureSize + ")");
      }
   }

   public void blitToScreen() {
      if (this.colorTexture == null) {
         throw new IllegalStateException("Can't blit to screen, color texture doesn't exist yet");
      } else {
         RenderSystem.getDevice().createCommandEncoder().presentTexture(this.colorTextureView);
      }
   }

   public void blitAndBlendToTexture(final GpuTextureView output) {
      RenderSystem.assertOnRenderThread();

      try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Blit render target", output, OptionalInt.empty())) {
         renderPass.setPipeline(RenderPipelines.ENTITY_OUTLINE_BLIT);
         RenderSystem.bindDefaultUniforms(renderPass);
         renderPass.bindTexture("InSampler", this.colorTextureView, RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
         renderPass.draw(0, 3);
      }

   }

   public @Nullable GpuTexture getColorTexture() {
      return this.colorTexture;
   }

   public @Nullable GpuTextureView getColorTextureView() {
      return this.colorTextureView;
   }

   public @Nullable GpuTexture getDepthTexture() {
      return this.depthTexture;
   }

   public @Nullable GpuTextureView getDepthTextureView() {
      return this.depthTextureView;
   }
}
