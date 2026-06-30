package com.mojang.blaze3d.resource;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public record RenderTargetDescriptor(int width, int height, @Nullable TextureProperties color, @Nullable TextureProperties depth) implements ResourceDescriptor<RenderTarget> {
   public RenderTargetDescriptor {
      super();
   }

   public RenderTarget allocate() {
      return new TextureTarget((String)null, this.width, this.height, this.color != null ? this.color.format : null, this.depth != null ? this.depth.format : null);
   }

   public void prepare(final RenderTarget resource) {
      if (this.color != null && this.depth != null) {
         RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(resource.getColorTexture(), this.color.clearColor, resource.getDepthTexture(), (double)this.depth.clearColor.x());
      } else if (this.color != null) {
         RenderSystem.getDevice().createCommandEncoder().clearColorTexture(resource.getColorTexture(), this.color.clearColor);
      } else if (this.depth != null) {
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(resource.getDepthTexture(), (double)this.depth.clearColor.x());
      }

   }

   public void free(final RenderTarget resource) {
      resource.destroyBuffers();
   }

   public boolean canUsePhysicalResource(final ResourceDescriptor<?> other) {
      if (!(other instanceof RenderTargetDescriptor descriptor)) {
         return false;
      } else {
         return this.width == descriptor.width && this.height == descriptor.height && Objects.equals(this.color, descriptor.color) && Objects.equals(this.depth, descriptor.depth);
      }
   }

   public static record TextureProperties(Vector4fc clearColor, GpuFormat format) {
      public static final TextureProperties DEFAULT_DEPTH;

      public TextureProperties {
         super();
      }

      static {
         DEFAULT_DEPTH = new TextureProperties(new Vector4f(0.0F, 0.0F, 0.0F, 0.0F), GpuFormat.D32_FLOAT);
      }
   }
}
