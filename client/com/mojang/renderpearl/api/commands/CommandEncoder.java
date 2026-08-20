package com.mojang.renderpearl.api.commands;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.buffers.TransientMemory;
import com.mojang.renderpearl.api.textures.GpuTexture;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public interface CommandEncoder {
   void submit();

   TransientMemory transientMemory();

   default RenderPass createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final Optional<Vector4fc> clearColor) {
      return this.createRenderPass(label, colorTexture, clearColor, (GpuTextureView)null, OptionalDouble.empty());
   }

   default RenderPass createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final Optional<Vector4fc> clearColor, final @Nullable GpuTextureView depthTexture, final OptionalDouble clearDepth) {
      return this.createRenderPass(label, colorTexture, clearColor, depthTexture, clearDepth, new RenderPass.RenderArea(0, 0, colorTexture.getWidth(0), colorTexture.getHeight(0)));
   }

   default RenderPass createRenderPass(final Supplier<String> label, final GpuTextureView colorTexture, final Optional<Vector4fc> clearColor, final @Nullable GpuTextureView depthTexture, final OptionalDouble clearDepth, final RenderPass.RenderArea renderArea) {
      RenderPassDescriptor.Builder descriptor = RenderPassDescriptor.builder(label).withColorAttachment(colorTexture, clearColor);
      if (depthTexture != null) {
         descriptor.withDepthAttachment(depthTexture, clearDepth);
      }

      descriptor.withRenderArea(renderArea);
      return this.createRenderPass(descriptor.build());
   }

   RenderPass createRenderPass(RenderPassDescriptor descriptor);

   void clearColorTexture(GpuTexture colorTexture, Vector4fc clearColor);

   void clearColorAndDepthTextures(GpuTexture colorTexture, Vector4fc clearColor, GpuTexture depthTexture, double clearDepth);

   void clearColorAndDepthTextures(GpuTexture colorTexture, Vector4fc clearColor, GpuTexture depthTexture, double clearDepth, int regionX, int regionY, int regionWidth, int regionHeight, int mipLevel);

   void clearDepthTexture(GpuTexture depthTexture, double clearDepth);

   void writeToBuffer(GpuBufferSlice destination, ByteBuffer data);

   void copyToBuffer(GpuBufferSlice source, GpuBufferSlice target);

   void writeToTexture(GpuTexture destination, NativeImage source);

   void writeToTexture(GpuTexture destination, NativeImage source, int mipLevel, int depthOrLayer, int destX, int destY);

   void writeToTexture(GpuTexture destination, ByteBuffer source, int mipLevel, int depthOrLayer, int destX, int destY, int width, int height);

   void copyBufferToTexture(GpuBufferSlice source, int sourceX, int sourceY, int sourceWidth, int sourceHeight, GpuTexture destination, int destinationX, int destinationY, int copyWidth, int copyHeight, int mipLevel, int arrayLayer);

   void copyTextureToBuffer(GpuTexture source, GpuBuffer destination, long offset, Runnable callback, int mipLevel);

   void copyTextureToBuffer(GpuTexture source, GpuBuffer destination, long offset, Runnable callback, int mipLevel, int x, int y, int width, int height);

   void copyTextureToTexture(GpuTexture source, GpuTexture destination, int mipLevel, int destX, int destY, int sourceX, int sourceY, int width, int height);

   GpuFence createFence();

   void writeTimestamp(GpuQueryPool pool, int index);
}
