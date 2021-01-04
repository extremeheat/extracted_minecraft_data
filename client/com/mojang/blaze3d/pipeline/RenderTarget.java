package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.nio.IntBuffer;

public class RenderTarget {
   public int width;
   public int height;
   public int viewWidth;
   public int viewHeight;
   public final boolean useDepth;
   public int frameBufferId;
   public int colorTextureId;
   public int depthBufferId;
   public final float[] clearChannels;
   public int filterMode;

   public RenderTarget(int var1, int var2, boolean var3, boolean var4) {
      super();
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
      if (!GLX.isUsingFBOs()) {
         this.viewWidth = var1;
         this.viewHeight = var2;
      } else {
         GlStateManager.enableDepthTest();
         if (this.frameBufferId >= 0) {
            this.destroyBuffers();
         }

         this.createBuffers(var1, var2, var3);
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
      }
   }

   public void destroyBuffers() {
      if (GLX.isUsingFBOs()) {
         this.unbindRead();
         this.unbindWrite();
         if (this.depthBufferId > -1) {
            GLX.glDeleteRenderbuffers(this.depthBufferId);
            this.depthBufferId = -1;
         }

         if (this.colorTextureId > -1) {
            TextureUtil.releaseTextureId(this.colorTextureId);
            this.colorTextureId = -1;
         }

         if (this.frameBufferId > -1) {
            GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
            GLX.glDeleteFramebuffers(this.frameBufferId);
            this.frameBufferId = -1;
         }

      }
   }

   public void createBuffers(int var1, int var2, boolean var3) {
      this.viewWidth = var1;
      this.viewHeight = var2;
      this.width = var1;
      this.height = var2;
      if (!GLX.isUsingFBOs()) {
         this.clear(var3);
      } else {
         this.frameBufferId = GLX.glGenFramebuffers();
         this.colorTextureId = TextureUtil.generateTextureId();
         if (this.useDepth) {
            this.depthBufferId = GLX.glGenRenderbuffers();
         }

         this.setFilterMode(9728);
         GlStateManager.bindTexture(this.colorTextureId);
         GlStateManager.texImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5121, (IntBuffer)null);
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.frameBufferId);
         GLX.glFramebufferTexture2D(GLX.GL_FRAMEBUFFER, GLX.GL_COLOR_ATTACHMENT0, 3553, this.colorTextureId, 0);
         if (this.useDepth) {
            GLX.glBindRenderbuffer(GLX.GL_RENDERBUFFER, this.depthBufferId);
            GLX.glRenderbufferStorage(GLX.GL_RENDERBUFFER, 33190, this.width, this.height);
            GLX.glFramebufferRenderbuffer(GLX.GL_FRAMEBUFFER, GLX.GL_DEPTH_ATTACHMENT, GLX.GL_RENDERBUFFER, this.depthBufferId);
         }

         this.checkStatus();
         this.clear(var3);
         this.unbindRead();
      }
   }

   public void setFilterMode(int var1) {
      if (GLX.isUsingFBOs()) {
         this.filterMode = var1;
         GlStateManager.bindTexture(this.colorTextureId);
         GlStateManager.texParameter(3553, 10241, var1);
         GlStateManager.texParameter(3553, 10240, var1);
         GlStateManager.texParameter(3553, 10242, 10496);
         GlStateManager.texParameter(3553, 10243, 10496);
         GlStateManager.bindTexture(0);
      }

   }

   public void checkStatus() {
      int var1 = GLX.glCheckFramebufferStatus(GLX.GL_FRAMEBUFFER);
      if (var1 != GLX.GL_FRAMEBUFFER_COMPLETE) {
         if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
         } else if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
         } else if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
         } else if (var1 == GLX.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER) {
            throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
         } else {
            throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + var1);
         }
      }
   }

   public void bindRead() {
      if (GLX.isUsingFBOs()) {
         GlStateManager.bindTexture(this.colorTextureId);
      }

   }

   public void unbindRead() {
      if (GLX.isUsingFBOs()) {
         GlStateManager.bindTexture(0);
      }

   }

   public void bindWrite(boolean var1) {
      if (GLX.isUsingFBOs()) {
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, this.frameBufferId);
         if (var1) {
            GlStateManager.viewport(0, 0, this.viewWidth, this.viewHeight);
         }
      }

   }

   public void unbindWrite() {
      if (GLX.isUsingFBOs()) {
         GLX.glBindFramebuffer(GLX.GL_FRAMEBUFFER, 0);
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
      if (GLX.isUsingFBOs()) {
         GlStateManager.colorMask(true, true, true, false);
         GlStateManager.disableDepthTest();
         GlStateManager.depthMask(false);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.ortho(0.0D, (double)var1, (double)var2, 0.0D, 1000.0D, 3000.0D);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.translatef(0.0F, 0.0F, -2000.0F);
         GlStateManager.viewport(0, 0, var1, var2);
         GlStateManager.enableTexture();
         GlStateManager.disableLighting();
         GlStateManager.disableAlphaTest();
         if (var3) {
            GlStateManager.disableBlend();
            GlStateManager.enableColorMaterial();
         }

         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.bindRead();
         float var4 = (float)var1;
         float var5 = (float)var2;
         float var6 = (float)this.viewWidth / (float)this.width;
         float var7 = (float)this.viewHeight / (float)this.height;
         Tesselator var8 = Tesselator.getInstance();
         BufferBuilder var9 = var8.getBuilder();
         var9.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var9.vertex(0.0D, (double)var5, 0.0D).uv(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
         var9.vertex((double)var4, (double)var5, 0.0D).uv((double)var6, 0.0D).color(255, 255, 255, 255).endVertex();
         var9.vertex((double)var4, 0.0D, 0.0D).uv((double)var6, (double)var7).color(255, 255, 255, 255).endVertex();
         var9.vertex(0.0D, 0.0D, 0.0D).uv(0.0D, (double)var7).color(255, 255, 255, 255).endVertex();
         var8.end();
         this.unbindRead();
         GlStateManager.depthMask(true);
         GlStateManager.colorMask(true, true, true, true);
      }
   }

   public void clear(boolean var1) {
      this.bindWrite(true);
      GlStateManager.clearColor(this.clearChannels[0], this.clearChannels[1], this.clearChannels[2], this.clearChannels[3]);
      int var2 = 16384;
      if (this.useDepth) {
         GlStateManager.clearDepth(1.0D);
         var2 |= 256;
      }

      GlStateManager.clear(var2, var1);
      this.unbindWrite();
   }
}
