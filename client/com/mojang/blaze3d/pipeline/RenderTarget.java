package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.OptionalInt;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderPipelines;

public abstract class RenderTarget {
   private static int UNNAMED_RENDER_TARGETS = 0;
   public int width;
   public int height;
   public int viewWidth;
   public int viewHeight;
   protected final String label;
   public final boolean useDepth;
   @Nullable
   protected GpuTexture colorTexture;
   @Nullable
   protected GpuTexture depthTexture;
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

      if (this.colorTexture != null) {
         this.colorTexture.close();
         this.colorTexture = null;
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

   public void copyColorAndDepthFrom(RenderTarget var1) {
      RenderSystem.assertOnRenderThread();
      if (this.depthTexture != null && this.colorTexture != null) {
         if (var1.depthTexture != null && var1.colorTexture != null) {
            RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(var1.colorTexture, this.colorTexture, 0, 0, 0, 0, 0, this.width, this.height);
         } else {
            throw new IllegalStateException("Trying to copy depth texture from a RenderTarget without a depth texture");
         }
      } else {
         throw new IllegalStateException("Trying to copy depth texture to a RenderTarget without a depth texture");
      }
   }

   public void createBuffers(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      int var3 = RenderSystem.getDevice().getMaxTextureSize();
      if (var1 > 0 && var1 <= var3 && var2 > 0 && var2 <= var3) {
         this.viewWidth = var1;
         this.viewHeight = var2;
         this.width = var1;
         this.height = var2;
         if (this.useDepth) {
            this.depthTexture = RenderSystem.getDevice().createTexture((Supplier)(() -> this.label + " / Depth"), TextureFormat.DEPTH32, var1, var2, 1);
            this.depthTexture.setTextureFilter(FilterMode.NEAREST, false);
            this.depthTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         }

         this.colorTexture = RenderSystem.getDevice().createTexture((Supplier)(() -> this.label + " / Color"), TextureFormat.RGBA8, var1, var2, 1);
         this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         this.setFilterMode(FilterMode.NEAREST, true);
      } else {
         throw new IllegalArgumentException("Window " + var1 + "x" + var2 + " size out of bounds (max. size: " + var3 + ")");
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
         RenderSystem.getDevice().createCommandEncoder().presentTexture(this.colorTexture);
      }
   }

   public void blitAndBlendToTexture(GpuTexture var1) {
      RenderSystem.assertOnRenderThread();
      RenderSystem.AutoStorageIndexBuffer var2 = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
      GpuBuffer var3 = var2.getBuffer(6);
      GpuBuffer var4 = RenderSystem.getQuadVertexBuffer();

      try (RenderPass var5 = RenderSystem.getDevice().createCommandEncoder().createRenderPass(var1, OptionalInt.empty())) {
         var5.setPipeline(RenderPipelines.ENTITY_OUTLINE_BLIT);
         var5.setVertexBuffer(0, var4);
         var5.setIndexBuffer(var3, var2.type());
         var5.bindSampler("InSampler", this.colorTexture);
         var5.drawIndexed(0, 6);
      }

   }

   @Nullable
   public GpuTexture getColorTexture() {
      return this.colorTexture;
   }

   @Nullable
   public GpuTexture getDepthTexture() {
      return this.depthTexture;
   }
}
