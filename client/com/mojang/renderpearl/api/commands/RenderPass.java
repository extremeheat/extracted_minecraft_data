package com.mojang.renderpearl.api.commands;

import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;

public interface RenderPass extends UncheckedAutoCloseable {
   int MAX_VERTEX_BUFFERS = 16;
   int INDIRECT_DRAW_SIZE = 16;
   int INDIRECT_INDEXED_DRAW_SIZE = 20;

   void pushDebugGroup(Supplier<String> label);

   void popDebugGroup();

   void writeTimestamp(GpuQueryPool pool, int index);

   void setPipeline(CompiledRenderPipeline pipeline);

   void setUniform(String name, @Nullable GpuTextureView textureView, @Nullable GpuSampler sampler);

   void setUniform(String name, GpuBuffer value);

   void setUniform(String name, GpuBufferSlice value);

   void pushConstants(ByteBuffer value);

   void enableScissor(int x, int y, int width, int height);

   void disableScissor();

   void setVertexBuffer(int slot, @Nullable GpuBufferSlice vertexBuffer);

   void setIndexBuffer(GpuBuffer indexBuffer, IndexType indexType);

   void drawIndexed(int indexCount, int instanceCount, int firstIndex, int vertexOffset, int firstInstance);

   void multiDrawIndexed(IntBuffer drawParameters, int instanceCount, int firstInstance, int drawCount);

   void multiDrawIndexed(PointerBuffer firstIndexOffsets, IntBuffer indexCounts, IntBuffer vertexOffsets, int drawCount);

   void drawIndexedIndirect(GpuBufferSlice commands, int drawCount);

   <T> void drawMultipleIndexed(Collection<Draw<T>> draws, @Nullable GpuBuffer defaultIndexBuffer, @Nullable IndexType defaultIndexType, Collection<String> dynamicUniforms, T uniformArgument);

   void draw(int vertexCount, int instanceCount, int firstVertex, int firstInstance);

   void multiDraw(IntBuffer drawParameters, int instanceCount, int firstInstance, int drawCount);

   void multiDraw(IntBuffer firstVertices, IntBuffer vertexCounts, int drawCount);

   void drawIndirect(GpuBufferSlice commands, final int drawCount);

   public static record Draw<T>(int slot, GpuBuffer vertexBuffer, @Nullable GpuBuffer indexBuffer, @Nullable IndexType indexType, int firstIndex, int indexCount, int baseVertex, @Nullable BiConsumer<T, UniformUploader> uniformUploaderConsumer) {
      public Draw(final int slot, final GpuBuffer vertexBuffer, final GpuBuffer indexBuffer, final IndexType indexType, final int firstIndex, final int indexCount, final int baseVertex) {
         this(slot, vertexBuffer, indexBuffer, indexType, firstIndex, indexCount, baseVertex, (BiConsumer)null);
      }

      public Draw {
         super();
      }
   }

   public static record RenderArea(int x, int y, int width, int height) {
      public RenderArea {
         super();
      }

      public boolean fillsTexture(final GpuTextureView texture) {
         return this.x == 0 && this.y == 0 && this.width == texture.getWidth(0) && this.height == texture.getHeight(0);
      }
   }

   public interface UniformUploader {
      void setUniform(String name, GpuBufferSlice buffer);

      void pushConstants(ByteBuffer buffer);
   }
}
