package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.IntBuffer;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;

public abstract class RenderTarget {
   private static final int RED_CHANNEL = 0;
   private static final int GREEN_CHANNEL = 1;
   private static final int BLUE_CHANNEL = 2;
   private static final int ALPHA_CHANNEL = 3;
   public int width;
   public int height;
   public int viewWidth;
   public int viewHeight;
   public final boolean useDepth;
   public int frameBufferId;
   protected int colorTextureId;
   protected int depthBufferId;
   private final float[] clearChannels = (float[])Util.make(() -> {
      float[] var0 = new float[]{1.0F, 1.0F, 1.0F, 0.0F};
      return var0;
   });
   public int filterMode;

   public RenderTarget(boolean var1) {
      super();
      this.useDepth = var1;
      this.frameBufferId = -1;
      this.colorTextureId = -1;
      this.depthBufferId = -1;
   }

   public void resize(int var1, int var2, boolean var3) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._resize(var1, var2, var3);
         });
      } else {
         this._resize(var1, var2, var3);
      }

   }

   private void _resize(int var1, int var2, boolean var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._enableDepthTest();
      if (this.frameBufferId >= 0) {
         this.destroyBuffers();
      }

      this.createBuffers(var1, var2, var3);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void destroyBuffers() {
      RenderSystem.assertOnRenderThreadOrInit();
      this.unbindRead();
      this.unbindWrite();
      if (this.depthBufferId > -1) {
         TextureUtil.releaseTextureId(this.depthBufferId);
         this.depthBufferId = -1;
      }

      if (this.colorTextureId > -1) {
         TextureUtil.releaseTextureId(this.colorTextureId);
         this.colorTextureId = -1;
      }

      if (this.frameBufferId > -1) {
         GlStateManager._glBindFramebuffer(36160, 0);
         GlStateManager._glDeleteFramebuffers(this.frameBufferId);
         this.frameBufferId = -1;
      }

   }

   public void copyDepthFrom(RenderTarget var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._glBindFramebuffer(36008, var1.frameBufferId);
      GlStateManager._glBindFramebuffer(36009, this.frameBufferId);
      GlStateManager._glBlitFrameBuffer(0, 0, var1.width, var1.height, 0, 0, this.width, this.height, 256, 9728);
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   public void createBuffers(int var1, int var2, boolean var3) {
      RenderSystem.assertOnRenderThreadOrInit();
      int var4 = RenderSystem.maxSupportedTextureSize();
      if (var1 > 0 && var1 <= var4 && var2 > 0 && var2 <= var4) {
         this.viewWidth = var1;
         this.viewHeight = var2;
         this.width = var1;
         this.height = var2;
         this.frameBufferId = GlStateManager.glGenFramebuffers();
         this.colorTextureId = TextureUtil.generateTextureId();
         if (this.useDepth) {
            this.depthBufferId = TextureUtil.generateTextureId();
            GlStateManager._bindTexture(this.depthBufferId);
            GlStateManager._texParameter(3553, 10241, 9728);
            GlStateManager._texParameter(3553, 10240, 9728);
            GlStateManager._texParameter(3553, 34892, 0);
            GlStateManager._texParameter(3553, 10242, 33071);
            GlStateManager._texParameter(3553, 10243, 33071);
            GlStateManager._texImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, (IntBuffer)null);
         }

         this.setFilterMode(9728, true);
         GlStateManager._bindTexture(this.colorTextureId);
         GlStateManager._texParameter(3553, 10242, 33071);
         GlStateManager._texParameter(3553, 10243, 33071);
         GlStateManager._texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, (IntBuffer)null);
         GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
         GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorTextureId, 0);
         if (this.useDepth) {
            GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthBufferId, 0);
         }

         this.checkStatus();
         this.clear(var3);
         this.unbindRead();
      } else {
         throw new IllegalArgumentException("Window " + var1 + "x" + var2 + " size out of bounds (max. size: " + var4 + ")");
      }
   }

   public void setFilterMode(int var1) {
      this.setFilterMode(var1, false);
   }

   private void setFilterMode(int var1, boolean var2) {
      RenderSystem.assertOnRenderThreadOrInit();
      if (var2 || var1 != this.filterMode) {
         this.filterMode = var1;
         GlStateManager._bindTexture(this.colorTextureId);
         GlStateManager._texParameter(3553, 10241, var1);
         GlStateManager._texParameter(3553, 10240, var1);
         GlStateManager._bindTexture(0);
      }

   }

   public void checkStatus() {
      RenderSystem.assertOnRenderThreadOrInit();
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

   public void bindRead() {
      RenderSystem.assertOnRenderThread();
      GlStateManager._bindTexture(this.colorTextureId);
   }

   public void unbindRead() {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._bindTexture(0);
   }

   public void bindWrite(boolean var1) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._bindWrite(var1);
         });
      } else {
         this._bindWrite(var1);
      }

   }

   private void _bindWrite(boolean var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
      if (var1) {
         GlStateManager._viewport(0, 0, this.viewWidth, this.viewHeight);
      }

   }

   public void unbindWrite() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            GlStateManager._glBindFramebuffer(36160, 0);
         });
      } else {
         GlStateManager._glBindFramebuffer(36160, 0);
      }

   }

   public void setClearColor(float var1, float var2, float var3, float var4) {
      this.clearChannels[0] = var1;
      this.clearChannels[1] = var2;
      this.clearChannels[2] = var3;
      this.clearChannels[3] = var4;
   }

   public void blitToScreen(int var1, int var2) {
      this.blitToScreen(var1, var2, true);
   }

   public void blitToScreen(int var1, int var2, boolean var3) {
      this._blitToScreen(var1, var2, var3);
   }

   private void _blitToScreen(int var1, int var2, boolean var3) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._colorMask(true, true, true, false);
      GlStateManager._disableDepthTest();
      GlStateManager._depthMask(false);
      GlStateManager._viewport(0, 0, var1, var2);
      if (var3) {
         GlStateManager._disableBlend();
      }

      Minecraft var4 = Minecraft.getInstance();
      ShaderInstance var5 = (ShaderInstance)Objects.requireNonNull(var4.gameRenderer.blitShader, "Blit shader not loaded");
      var5.setSampler("DiffuseSampler", this.colorTextureId);
      var5.apply();
      BufferBuilder var6 = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
      var6.addVertex(0.0F, 0.0F, 0.0F);
      var6.addVertex(1.0F, 0.0F, 0.0F);
      var6.addVertex(1.0F, 1.0F, 0.0F);
      var6.addVertex(0.0F, 1.0F, 0.0F);
      BufferUploader.draw(var6.buildOrThrow());
      var5.clear();
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(true, true, true, true);
   }

   public void clear(boolean var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      this.bindWrite(true);
      GlStateManager._clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
      int var2 = 16384;
      if (this.useDepth) {
         GlStateManager._clearDepth(1.0);
         var2 |= 256;
      }

      GlStateManager._clear(var2, var1);
      this.unbindWrite();
   }

   public int getColorTextureId() {
      return this.colorTextureId;
   }

   public int getDepthTextureId() {
      return this.depthBufferId;
   }
}
