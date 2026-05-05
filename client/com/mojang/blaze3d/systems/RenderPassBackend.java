package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Collection;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;

public interface RenderPassBackend {
   void pushDebugGroup(final Supplier<String> label);

   void popDebugGroup();

   void setPipeline(final RenderPipeline pipeline);

   void bindTexture(final String name, final @Nullable GpuTextureView textureView, final @Nullable GpuSampler sampler);

   void setUniform(final String name, final GpuBuffer value);

   void setUniform(final String name, final GpuBufferSlice value);

   void enableScissor(final int x, final int y, final int width, final int height);

   void disableScissor();

   void setVertexBuffer(final int slot, final @Nullable GpuBufferSlice vertexBuffer);

   void setIndexBuffer(final GpuBuffer indexBuffer, final IndexType indexType);

   void drawIndexed(final int baseVertex, final int firstIndex, final int indexCount, final int instanceCount);

   <T> void drawMultipleIndexed(final Collection<RenderPass.Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, final @Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument);

   void draw(final int firstVertex, final int vertexCount);

   void writeTimestamp(GpuQueryPool pool, int index);
}
