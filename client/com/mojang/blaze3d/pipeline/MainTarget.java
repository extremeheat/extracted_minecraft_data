package com.mojang.blaze3d.pipeline;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.GpuOutOfMemoryException;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public class MainTarget extends RenderTarget {
   public static final int DEFAULT_WIDTH = 854;
   public static final int DEFAULT_HEIGHT = 480;
   private static final Dimension DEFAULT_DIMENSIONS = new Dimension(854, 480);

   public MainTarget(final int desiredWidth, final int desiredHeight) {
      super("Main", GpuFormat.RGBA8_UNORM, GpuFormat.D32_FLOAT);
      this.createFrameBuffer(desiredWidth, desiredHeight);
   }

   private void createFrameBuffer(final int desiredWidth, final int desiredHeight) {
      Dimension allocatedDimensions = this.allocateAttachments(desiredWidth, desiredHeight);
      if (this.colorTexture != null && this.depthTexture != null) {
         this.width = allocatedDimensions.width;
         this.height = allocatedDimensions.height;
      } else {
         throw new IllegalStateException("Missing color and/or depth textures");
      }
   }

   private Dimension allocateAttachments(final int width, final int height) {
      RenderSystem.assertOnRenderThread();

      for(Dimension dimension : MainTarget.Dimension.listWithFallback(width, height)) {
         if (this.colorTexture != null) {
            this.colorTexture.close();
            this.colorTexture = null;
         }

         if (this.colorTextureView != null) {
            this.colorTextureView.close();
            this.colorTextureView = null;
         }

         if (this.depthTexture != null) {
            this.depthTexture.close();
            this.depthTexture = null;
         }

         if (this.depthTextureView != null) {
            this.depthTextureView.close();
            this.depthTextureView = null;
         }

         this.colorTexture = this.allocateColorAttachment(dimension);
         this.depthTexture = this.allocateDepthAttachment(dimension);
         if (this.colorTexture != null && this.depthTexture != null) {
            this.colorTextureView = RenderSystem.getDevice().createTextureView(this.colorTexture);
            this.depthTextureView = RenderSystem.getDevice().createTextureView(this.depthTexture);
            return dimension;
         }
      }

      String var10002 = this.colorTexture == null ? "missing color" : "have color";
      throw new RuntimeException("Unrecoverable GL_OUT_OF_MEMORY (" + var10002 + ", " + (this.depthTexture == null ? "missing depth" : "have depth") + ")");
   }

   private @Nullable GpuTexture allocateColorAttachment(final Dimension dimension) {
      try {
         return RenderSystem.getDevice().createTexture((Supplier)(() -> this.label + " / Color"), 15, this.colorFormat, dimension.width, dimension.height, 1, 1);
      } catch (GpuOutOfMemoryException var3) {
         return null;
      }
   }

   private @Nullable GpuTexture allocateDepthAttachment(final Dimension dimension) {
      try {
         return RenderSystem.getDevice().createTexture((Supplier)(() -> this.label + " / Depth"), 15, this.depthFormat, dimension.width, dimension.height, 1, 1);
      } catch (GpuOutOfMemoryException var3) {
         return null;
      }
   }

   private static class Dimension {
      public final int width;
      public final int height;

      private Dimension(final int width, final int height) {
         super();
         this.width = width;
         this.height = height;
      }

      private static List<Dimension> listWithFallback(final int width, final int height) {
         RenderSystem.assertOnRenderThread();
         int maxTextureSize = RenderSystem.getDevice().getDeviceInfo().limits().maxTextureSize();
         return width > 0 && width <= maxTextureSize && height > 0 && height <= maxTextureSize ? ImmutableList.of(new Dimension(width, height), MainTarget.DEFAULT_DIMENSIONS) : ImmutableList.of(MainTarget.DEFAULT_DIMENSIONS);
      }

      public boolean equals(final Object other) {
         if (this == other) {
            return true;
         } else if (other != null && this.getClass() == other.getClass()) {
            Dimension that = (Dimension)other;
            return this.width == that.width && this.height == that.height;
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
