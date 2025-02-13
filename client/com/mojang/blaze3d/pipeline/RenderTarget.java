package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;

public abstract class RenderTarget {
   private static int UNNAMED_RENDER_TARGETS = 0;
   private static final int RED_CHANNEL = 0;
   private static final int GREEN_CHANNEL = 1;
   private static final int BLUE_CHANNEL = 2;
   private static final int ALPHA_CHANNEL = 3;
   public int width;
   public int height;
   public int viewWidth;
   public int viewHeight;
   protected final String label;
   public final boolean useDepth;
   public int frameBufferId;
   @Nullable
   protected GpuTexture colorTexture;
   @Nullable
   protected GpuTexture depthTexture;
   private final float[] clearChannels = (float[])Util.make(() -> {
      float[] var0 = new float[]{1.0F, 1.0F, 1.0F, 0.0F};
      return var0;
   });
   public FilterMode filterMode;

   public RenderTarget(@Nullable String var1, boolean var2) {
      super();
      this.label = var1 == null ? "FBO " + UNNAMED_RENDER_TARGETS++ : var1;
      this.useDepth = var2;
      this.frameBufferId = -1;
   }

   public void resize(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._enableDepthTest();
      if (this.frameBufferId >= 0) {
         this.destroyBuffers();
      }

      this.createBuffers(var1, var2);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void destroyBuffers() {
      RenderSystem.assertOnRenderThread();
      this.unbindRead();
      this.unbindWrite();
      if (this.depthTexture != null) {
         this.depthTexture.close();
         this.depthTexture = null;
      }

      if (this.colorTexture != null) {
         this.colorTexture.close();
         this.colorTexture = null;
      }

      if (this.frameBufferId > -1) {
         GlStateManager._glBindFramebuffer(36160, 0);
         GlStateManager._glDeleteFramebuffers(this.frameBufferId);
         this.frameBufferId = -1;
      }

   }

   public void copyDepthFrom(RenderTarget var1) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._glBindFramebuffer(36008, var1.frameBufferId);
      GlStateManager._glBindFramebuffer(36009, this.frameBufferId);
      GlStateManager._glBlitFrameBuffer(0, 0, var1.width, var1.height, 0, 0, this.width, this.height, 256, 9728);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void createBuffers(int var1, int var2) {
      RenderSystem.assertOnRenderThread();
      int var3 = RenderSystem.maxSupportedTextureSize();
      if (var1 > 0 && var1 <= var3 && var2 > 0 && var2 <= var3) {
         this.viewWidth = var1;
         this.viewHeight = var2;
         this.width = var1;
         this.height = var2;
         this.frameBufferId = GlStateManager.glGenFramebuffers();
         if (this.useDepth) {
            this.depthTexture = new GpuTexture(() -> this.label + " / Depth", TextureFormat.DEPTH32, var1, var2, 1);
            this.depthTexture.setTextureFilter(FilterMode.NEAREST, false);
            this.depthTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         }

         this.colorTexture = new GpuTexture(() -> this.label + " / Color", TextureFormat.RGBA8, var1, var2, 1);
         this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         this.setFilterMode(FilterMode.NEAREST, true);
         GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
         GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorTexture.glId(), 0);
         if (this.useDepth) {
            GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthTexture.glId(), 0);
         }

         this.checkStatus();
         this.clear();
         this.unbindRead();
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

   public void checkStatus() {
      RenderSystem.assertOnRenderThread();
      int var1 = GlStateManager.glCheckFramebufferStatus(36160);
      if (var1 != 36053) {
         if (var1 == 36054) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (var1 == 36055) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (var1 == 36059) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (var1 == 36060) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else if (var1 == 36061) {
            throw new RuntimeException("GL_FRAMEBUFFER_UNSUPPORTED");
         } else if (var1 == 1285) {
            throw new RuntimeException("GL_OUT_OF_MEMORY");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + var1);
         }
      }
   }

   public void unbindRead() {
      RenderSystem.assertOnRenderThread();
      GlStateManager._bindTexture(0);
   }

   public void bindWrite(boolean var1) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
      if (var1) {
         GlStateManager._viewport(0, 0, this.viewWidth, this.viewHeight);
      }

   }

   public void unbindWrite() {
      RenderSystem.assertOnRenderThread();
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void setClearColor(float var1, float var2, float var3, float var4) {
      this.clearChannels[0] = var1;
      this.clearChannels[1] = var2;
      this.clearChannels[2] = var3;
      this.clearChannels[3] = var4;
   }

   public void blitToScreen(int var1, int var2) {
      GlStateManager._glBindFramebuffer(36008, this.frameBufferId);
      GlStateManager._glBlitFrameBuffer(0, 0, this.width, this.height, 0, 0, var1, var2, 16384, 9728);
      GlStateManager._glBindFramebuffer(36008, 0);
   }

   public void blitAndBlendToScreen() {
      RenderSystem.assertOnRenderThread();
      RenderSystem.getQuadVertices().drawWithRenderType(RenderType.entityOutlineBlit(), (var1) -> var1.bindSampler("InSampler", this.colorTexture));
   }

   public void clear() {
      this.clear(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
   }

   public void clear(float var1, float var2, float var3, float var4) {
      RenderSystem.assertOnRenderThread();
      this.bindWrite(true);
      GlStateManager._clearColor(var1, var2, var3, var4);
      int var5 = 16384;
      if (this.useDepth) {
         GlStateManager._clearDepth(1.0);
         var5 |= 256;
      }

      GlStateManager._clear(var5);
      this.unbindWrite();
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
