package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.OptionalInt;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderPipelines;

public abstract class RenderTarget {
   private static int UNNAMED_RENDER_TARGETS = 0;
   public int width;
   public int height;
   protected final String label;
   public final boolean useDepth;
   @Nullable
   protected GpuTexture colorTexture;
   @Nullable
   protected GpuTextureView colorTextureView;
   @Nullable
   protected GpuTexture depthTexture;
   @Nullable
   protected GpuTextureView depthTextureView;
   public FilterMode filterMode;

   public RenderTarget(@Nullable String var1, boolean var2) {
      super();
      this.label = var1 == null ? "FBO " + UNNAMED_RENDER_TARGETS++ : var1;
      this.useDepth = var2;
   }

   public void resize(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      this.destroyBuffers();
      this.createBuffers(var1, var2);
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

   public void copyDepthFrom(RenderTarget var1) {
      RenderSystem.assertOnRenderThread();
      if (this.depthTexture == null) {
         throw new IllegalStateException("Trying to copy depth texture to a RenderTarget without a depth texture");
      } else if (var1.depthTexture == null) {
         throw new IllegalStateException("Trying to copy depth texture from a RenderTarget without a depth texture");
      } else {
         RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(var1.depthTexture, this.depthTexture, 0, 0, 0, 0, 0, this.width, this.height);
      }
   }

   public void createBuffers(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      GpuDevice var3 = RenderSystem.getDevice();
      int var4 = var3.getMaxTextureSize();
      if (var1 > 0 && var1 <= var4 && var2 > 0 && var2 <= var4) {
         this.width = var1;
         this.height = var2;
         if (this.useDepth) {
            this.depthTexture = var3.createTexture((Supplier)(() -> this.label + " / Depth"), 15, TextureFormat.DEPTH32, var1, var2, 1, 1);
            this.depthTextureView = var3.createTextureView(this.depthTexture);
            this.depthTexture.setTextureFilter(FilterMode.NEAREST, false);
            this.depthTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         }

         this.colorTexture = var3.createTexture((Supplier)(() -> this.label + " / Color"), 15, TextureFormat.RGBA8, var1, var2, 1, 1);
         this.colorTextureView = var3.createTextureView(this.colorTexture);
         this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         this.setFilterMode(FilterMode.NEAREST, true);
      } else {
         throw new IllegalArgumentException("Window " + var1 + "x" + var2 + " size out of bounds (max. size: " + var4 + ")");
      }
   }

   public void setFilterMode(FilterMode var1) {
      this.setFilterMode(var1, false);
   }

   private void setFilterMode(FilterMode var1, boolean var2) {
      if (this.colorTexture == null) {
         throw new IllegalStateException("Can't change filter mode, color texture doesn't exist yet");
      } else {
         if (var2 || var1 != this.filterMode) {
            this.filterMode = var1;
            this.colorTexture.setTextureFilter(var1, false);
         }

      }
   }

   public void blitToScreen() {
      if (this.colorTexture == null) {
         throw new IllegalStateException("Can't blit to screen, color texture doesn't exist yet");
      } else {
         RenderSystem.getDevice().createCommandEncoder().presentTexture(this.colorTextureView);
      }
   }

   public void blitAndBlendToTexture(GpuTextureView var1) {
      RenderSystem.assertOnRenderThread();

      try (RenderPass var2 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(() -> "Blit render target", var1, OptionalInt.empty())) {
         var2.setPipeline(RenderPipelines.ENTITY_OUTLINE_BLIT);
         RenderSystem.bindDefaultUniforms(var2);
         var2.bindSampler("InSampler", this.colorTextureView);
         var2.draw(0, 3);
      }

   }

   @Nullable
   public GpuTexture getColorTexture() {
      return this.colorTexture;
   }

   @Nullable
   public GpuTextureView getColorTextureView() {
      return this.colorTextureView;
   }

   @Nullable
   public GpuTexture getDepthTexture() {
      return this.depthTexture;
   }

   @Nullable
   public GpuTextureView getDepthTextureView() {
      return this.depthTextureView;
   }
}
