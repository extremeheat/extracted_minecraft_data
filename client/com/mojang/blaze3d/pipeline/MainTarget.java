package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;

public class MainTarget extends RenderTarget {
   public static final int DEFAULT_WIDTH = 854;
   public static final int DEFAULT_HEIGHT = 480;
   static final MainTarget.Dimension DEFAULT_DIMENSIONS = new MainTarget.Dimension(854, 480);

   public MainTarget(int var1, int var2) {
      super(true);
      this.createFrameBuffer(var1, var2);
   }

   private void createFrameBuffer(int var1, int var2) {
      MainTarget.Dimension var3 = this.allocateAttachments(var1, var2);
      this.frameBufferId = GlStateManager.glGenFramebuffers();
      GlStateManager._glBindFramebuffer(36160, this.frameBufferId);
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texParameter(3553, 10241, 9728);
      GlStateManager._texParameter(3553, 10240, 9728);
      GlStateManager._texParameter(3553, 10242, 33071);
      GlStateManager._texParameter(3553, 10243, 33071);
      GlStateManager._glFramebufferTexture2D(36160, 36064, 3553, this.colorTextureId, 0);
      GlStateManager._bindTexture(this.depthBufferId);
      GlStateManager._texParameter(3553, 34892, 0);
      GlStateManager._texParameter(3553, 10241, 9728);
      GlStateManager._texParameter(3553, 10240, 9728);
      GlStateManager._texParameter(3553, 10242, 33071);
      GlStateManager._texParameter(3553, 10243, 33071);
      GlStateManager._glFramebufferTexture2D(36160, 36096, 3553, this.depthBufferId, 0);
      GlStateManager._bindTexture(0);
      this.viewWidth = var3.width;
      this.viewHeight = var3.height;
      this.width = var3.width;
      this.height = var3.height;
      this.checkStatus();
      GlStateManager._glBindFramebuffer(36160, 0);
   }

   private MainTarget.Dimension allocateAttachments(int var1, int var2) {
      RenderSystem.assertOnRenderThreadOrInit();
      this.colorTextureId = TextureUtil.generateTextureId();
      this.depthBufferId = TextureUtil.generateTextureId();
      MainTarget.AttachmentState var3 = MainTarget.AttachmentState.NONE;

      for (MainTarget.Dimension var5 : MainTarget.Dimension.listWithFallback(var1, var2)) {
         var3 = MainTarget.AttachmentState.NONE;
         if (this.allocateColorAttachment(var5)) {
            var3 = var3.with(MainTarget.AttachmentState.COLOR);
         }

         if (this.allocateDepthAttachment(var5)) {
            var3 = var3.with(MainTarget.AttachmentState.DEPTH);
         }

         if (var3 == MainTarget.AttachmentState.COLOR_DEPTH) {
            return var5;
         }
      }

      throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (allocated attachments = " + var3.name() + ")");
   }

   private boolean allocateColorAttachment(MainTarget.Dimension var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._getError();
      GlStateManager._bindTexture(this.colorTextureId);
      GlStateManager._texImage2D(3553, 0, 32856, var1.width, var1.height, 0, 6408, 5121, null);
      return GlStateManager._getError() != 1285;
   }

   private boolean allocateDepthAttachment(MainTarget.Dimension var1) {
      RenderSystem.assertOnRenderThreadOrInit();
      GlStateManager._getError();
      GlStateManager._bindTexture(this.depthBufferId);
      GlStateManager._texImage2D(3553, 0, 6402, var1.width, var1.height, 0, 6402, 5126, null);
      return GlStateManager._getError() != 1285;
   }

   static enum AttachmentState {
      NONE,
      COLOR,
      DEPTH,
      COLOR_DEPTH;

      private static final MainTarget.AttachmentState[] VALUES = values();

      private AttachmentState() {
      }

      MainTarget.AttachmentState with(MainTarget.AttachmentState var1) {
         return VALUES[this.ordinal() | var1.ordinal()];
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

      static List<MainTarget.Dimension> listWithFallback(int var0, int var1) {
         RenderSystem.assertOnRenderThreadOrInit();
         int var2 = RenderSystem.maxSupportedTextureSize();
         return var0 > 0 && var0 <= var2 && var1 > 0 && var1 <= var2
            ? ImmutableList.of(new MainTarget.Dimension(var0, var1), MainTarget.DEFAULT_DIMENSIONS)
            : ImmutableList.of(MainTarget.DEFAULT_DIMENSIONS);
      }

      @Override
      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            MainTarget.Dimension var2 = (MainTarget.Dimension)var1;
            return this.width == var2.width && this.height == var2.height;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.width, this.height);
      }

      @Override
      public String toString() {
         return this.width + "x" + this.height;
      }
   }
}
