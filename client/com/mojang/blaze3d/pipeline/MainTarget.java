package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.GpuOutOfMemoryException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;

public class MainTarget extends RenderTarget {
   public static final int DEFAULT_WIDTH = 854;
   public static final int DEFAULT_HEIGHT = 480;
   static final Dimension DEFAULT_DIMENSIONS = new Dimension(854, 480);

   public MainTarget(int var1, int var2) {
      super("Main", true);
      this.createFrameBuffer(var1, var2);
   }

   private void createFrameBuffer(int var1, int var2) {
      Dimension var3 = this.allocateAttachments(var1, var2);
      if (this.colorTexture != null && this.depthTexture != null) {
         this.frameBufferId = GlStateManager.glGenFramebuffers();
         GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
         this.colorTexture.setTextureFilter(FilterMode.NEAREST, false);
         this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorTexture.glId(), 0);
         this.colorTexture.setTextureFilter(FilterMode.NEAREST, false);
         this.colorTexture.setAddressMode(AddressMode.CLAMP_TO_EDGE);
         GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthTexture.glId(), 0);
         GlStateManager._bindTexture(0);
         this.viewWidth = var3.width;
         this.viewHeight = var3.height;
         this.width = var3.width;
         this.height = var3.height;
         this.checkStatus();
         GlStateManager._glBindFramebuffer(36160, 0);
      } else {
         throw new IllegalStateException("Missing color and/or depth textures");
      }
   }

   private Dimension allocateAttachments(int var1, int var2) {
      RenderSystem.assertOnRenderThread();

      for(Dimension var4 : MainTarget.Dimension.listWithFallback(var1, var2)) {
         if (this.colorTexture != null) {
            this.colorTexture.close();
            this.colorTexture = null;
         }

         if (this.depthTexture != null) {
            this.depthTexture.close();
            this.depthTexture = null;
         }

         this.colorTexture = this.allocateColorAttachment(var4);
         this.depthTexture = this.allocateDepthAttachment(var4);
         if (this.colorTexture != null && this.depthTexture != null) {
            return var4;
         }
      }

      String var10002 = this.colorTexture == null ? "missing color" : "have color";
      throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (" + var10002 + ", " + (this.depthTexture == null ? "missing depth" : "have depth") + ")");
   }

   @Nullable
   private GpuTexture allocateColorAttachment(Dimension var1) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._getError();

      try {
         return new GpuTexture(() -> this.label + " / Color", TextureFormat.RGBA8, var1.width, var1.height, 1);
      } catch (GpuOutOfMemoryException var3) {
         return null;
      }
   }

   @Nullable
   private GpuTexture allocateDepthAttachment(Dimension var1) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._getError();

      try {
         return new GpuTexture(() -> this.label + " / Depth", TextureFormat.DEPTH32, var1.width, var1.height, 1);
      } catch (GpuOutOfMemoryException var3) {
         return null;
      }
   }

   static class Dimension {
      public final int width;
      public final int height;

      Dimension(int var1, int var2) {
         super();
         this.width = var1;
         this.height = var2;
      }

      static List<Dimension> listWithFallback(int var0, int var1) {
         RenderSystem.assertOnRenderThread();
         int var2 = RenderSystem.maxSupportedTextureSize();
         return var0 > 0 && var0 <= var2 && var1 > 0 && var1 <= var2 ? ImmutableList.of(new Dimension(var0, var1), MainTarget.DEFAULT_DIMENSIONS) : ImmutableList.of(MainTarget.DEFAULT_DIMENSIONS);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            Dimension var2 = (Dimension)var1;
            return this.width == var2.width && this.height == var2.height;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.width, this.height});
      }

      public String toString() {
         return this.width + "x" + this.height;
      }
   }
}
