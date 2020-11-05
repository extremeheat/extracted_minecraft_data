package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.IntBuffer;

public class RenderTarget {
   public int width;
   public int height;
   public int viewWidth;
   public int viewHeight;
   public final boolean useDepth;
   public int frameBufferId;
   private int colorTextureId;
   private int depthBufferId;
   public final float[] clearChannels;
   public int filterMode;

   public RenderTarget(int var1, int var2, boolean var3, boolean var4) {
      super();
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.useDepth = var3;
      this.frameBufferId = -1;
      this.colorTextureId = -1;
      this.depthBufferId = -1;
      this.clearChannels = new float[4];
      this.clearChannels[0] = 1.0F;
      this.clearChannels[1] = 1.0F;
      this.clearChannels[2] = 1.0F;
      this.clearChannels[3] = 0.0F;
      this.resize(var1, var2, var4);
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
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager._enableDepthTest();
      if (this.frameBufferId >= 0) {
         this.destroyBuffers();
      }

      this.createBuffers(var1, var2, var3);
      GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
   }

   public void destroyBuffers() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
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
         GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
         GlStateManager._glDeleteFramebuffers(this.frameBufferId);
         this.frameBufferId = -1;
      }

   }

   public void copyDepthFrom(RenderTarget var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (GlStateManager.supportsFramebufferBlit()) {
         GlStateManager._glBindFramebuffer(36008, var1.frameBufferId);
         GlStateManager._glBindFramebuffer(36009, this.frameBufferId);
         GlStateManager._glBlitFrameBuffer(0, 0, var1.width, var1.height, 0, 0, this.width, this.height, 256, 9728);
      } else {
         GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.frameBufferId);
         int var2 = GlStateManager.getFramebufferDepthTexture();
         if (var2 != 0) {
            int var3 = GlStateManager.getActiveTextureName();
            GlStateManager._bindTexture(var2);
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, var1.frameBufferId);
            GlStateManager._glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, Math.min(this.width, var1.width), Math.min(this.height, var1.height));
            GlStateManager._bindTexture(var3);
         }
      }

      GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
   }

   public void createBuffers(int var1, int var2, boolean var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
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
         GlStateManager._texParameter(3553, 10242, 10496);
         GlStateManager._texParameter(3553, 10243, 10496);
         GlStateManager._texParameter(3553, 34892, 0);
         GlStateManager._texImage2D(3553, 0, 6402, this.width, this.height, 0, 6402, 5126, (IntBuffer)null);
      }

      this.setFilterMode(9728);
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, (IntBuffer)null);
      GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.frameBufferId);
      GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_COLOR_ATTACHMENT0, 3553, this.colorTextureId, 0);
      if (this.useDepth) {
         GlStateManager._glFramebufferTexture2D(GlConst.GL_FRAMEBUFFER, GlConst.GL_DEPTH_ATTACHMENT, 3553, this.depthBufferId, 0);
      }

      this.checkStatus();
      this.clear(var3);
      this.unbindRead();
   }

   public void setFilterMode(int var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.filterMode = var1;
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texParameter(3553, 10241, var1);
      GlStateManager._texParameter(3553, 10240, var1);
      GlStateManager._texParameter(3553, 10242, 10496);
      GlStateManager._texParameter(3553, 10243, 10496);
      GlStateManager._bindTexture(0);
   }

   public void checkStatus() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      int var1 = GlStateManager.glCheckFramebufferStatus(GlConst.GL_FRAMEBUFFER);
      if (var1 != GlConst.GL_FRAMEBUFFER_COMPLETE) {
         if (var1 == GlConst.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (var1 == GlConst.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (var1 == GlConst.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (var1 == GlConst.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + var1);
         }
      }
   }

   public void bindRead() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager._bindTexture(this.colorTextureId);
   }

   public void unbindRead() {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
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
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, this.frameBufferId);
      if (var1) {
         GlStateManager._viewport(0, 0, this.viewWidth, this.viewHeight);
      }

   }

   public void unbindWrite() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
         });
      } else {
         GlStateManager._glBindFramebuffer(GlConst.GL_FRAMEBUFFER, 0);
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
      RenderSystem.assertThread(RenderSystem::isOnGameThreadOrInit);
      if (!RenderSystem.isInInitPhase()) {
         RenderSystem.recordRenderCall(() -> {
            this._blitToScreen(var1, var2, var3);
         });
      } else {
         this._blitToScreen(var1, var2, var3);
      }

   }

   private void _blitToScreen(int var1, int var2, boolean var3) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager._colorMask(true, true, true, false);
      GlStateManager._disableDepthTest();
      GlStateManager._depthMask(false);
      GlStateManager._matrixMode(5889);
      GlStateManager._loadIdentity();
      GlStateManager._ortho(0.0D, (double)var1, (double)var2, 0.0D, 1000.0D, 3000.0D);
      GlStateManager._matrixMode(5888);
      GlStateManager._loadIdentity();
      GlStateManager._translatef(0.0F, 0.0F, -2000.0F);
      GlStateManager._viewport(0, 0, var1, var2);
      GlStateManager._enableTexture();
      GlStateManager._disableLighting();
      GlStateManager._disableAlphaTest();
      if (var3) {
         GlStateManager._disableBlend();
         GlStateManager._enableColorMaterial();
      }

      GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.bindRead();
      float var4 = (float)var1;
      float var5 = (float)var2;
      float var6 = (float)this.viewWidth / (float)this.width;
      float var7 = (float)this.viewHeight / (float)this.height;
      Tesselator var8 = RenderSystem.renderThreadTesselator();
      BufferBuilder var9 = var8.getBuilder();
      var9.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
      var9.vertex(0.0D, (double)var5, 0.0D).uv(0.0F, 0.0F).color(255, 255, 255, 255).endVertex();
      var9.vertex((double)var4, (double)var5, 0.0D).uv(var6, 0.0F).color(255, 255, 255, 255).endVertex();
      var9.vertex((double)var4, 0.0D, 0.0D).uv(var6, var7).color(255, 255, 255, 255).endVertex();
      var9.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, var7).color(255, 255, 255, 255).endVertex();
      var8.end();
      this.unbindRead();
      GlStateManager._depthMask(true);
      GlStateManager._colorMask(true, true, true, true);
   }

   public void clear(boolean var1) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.bindWrite(true);
      GlStateManager._clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
      int var2 = 16384;
      if (this.useDepth) {
         GlStateManager._clearDepth(1.0D);
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
