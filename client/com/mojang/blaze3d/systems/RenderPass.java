package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class RenderPass implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final RenderPassBackend backend;
   private final GpuDeviceBackend device;
   private final Runnable onFinish;
   private boolean isClosed;
   private int pushedDebugGroups;

   public RenderPass(final RenderPassBackend backend, final GpuDeviceBackend device, final Runnable onFinish) {
      super();
      this.backend = backend;
      this.device = device;
      this.onFinish = onFinish;
   }

   public void pushDebugGroup(final Supplier<String> label) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         ++this.pushedDebugGroups;
         this.backend.pushDebugGroup(label);
      }
   }

   public void popDebugGroup() {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (this.pushedDebugGroups == 0) {
         throw new IllegalStateException("Can't pop more debug groups than was pushed!");
      } else {
         --this.pushedDebugGroups;
         this.backend.popDebugGroup();
      }
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      if (index >= 0 && index <= pool.size()) {
         this.backend.writeTimestamp(pool, index);
      } else {
         throw new IllegalStateException("Index " + index + " is out of range for query pool of size " + pool.size());
      }
   }

   public void setPipeline(final RenderPipeline pipeline) {
      this.backend.setPipeline(pipeline);
   }

   public void bindTexture(final String name, final @Nullable GpuTextureView textureView, final @Nullable GpuSampler sampler) {
      this.backend.bindTexture(name, textureView, sampler);
   }

   public void setUniform(final String name, final GpuBuffer value) {
      this.backend.setUniform(name, value);
   }

   public void setUniform(final String name, final GpuBufferSlice value) {
      int alignment = this.device.getDeviceInfo().limits().minUniformOffsetAlignment();
      if (value.offset() % (long)alignment > 0L) {
         throw new IllegalArgumentException("Uniform buffer offset must be aligned to " + alignment);
      } else {
         this.backend.setUniform(name, value);
      }
   }

   public void enableScissor(final int x, final int y, final int width, final int height) {
      this.backend.enableScissor(x, y, width, height);
   }

   public void disableScissor() {
      this.backend.disableScissor();
   }

   public void setVertexBuffer(final int slot, final GpuBuffer vertexBuffer) {
      this.backend.setVertexBuffer(slot, vertexBuffer);
   }

   public void setIndexBuffer(final GpuBuffer indexBuffer, final VertexFormat.IndexType indexType) {
      this.backend.setIndexBuffer(indexBuffer, indexType);
   }

   public void drawIndexed(final int baseVertex, final int firstIndex, final int indexCount, final int instanceCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.backend.drawIndexed(baseVertex, firstIndex, indexCount, instanceCount);
      }
   }

   public <T> void drawMultipleIndexed(final Collection<Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, final VertexFormat.@Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.backend.drawMultipleIndexed(draws, defaultIndexBuffer, defaultIndexType, dynamicUniforms, uniformArgument);
      }
   }

   public void draw(final int firstVertex, final int vertexCount) {
      if (this.isClosed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.backend.draw(firstVertex, vertexCount);
      }
   }

   public void close() {
      if (!this.isClosed) {
         this.isClosed = true;
         if (this.pushedDebugGroups > 0) {
            throw new IllegalStateException("Render pass had debug groups left open!");
         }

         this.onFinish.run();
      }

   }

   public static record Draw<T>(int slot, GpuBuffer vertexBuffer, @Nullable GpuBuffer indexBuffer, VertexFormat.@Nullable IndexType indexType, int firstIndex, int indexCount, int baseVertex, @Nullable BiConsumer<T, UniformUploader> uniformUploaderConsumer) {
      public Draw(final int slot, final GpuBuffer vertexBuffer, final GpuBuffer indexBuffer, final VertexFormat.IndexType indexType, final int firstIndex, final int indexCount, final int baseVertex) {
         this(slot, vertexBuffer, indexBuffer, indexType, firstIndex, indexCount, baseVertex, (BiConsumer)null);
      }

      public Draw {
         super();
      }
   }

   public interface UniformUploader {
      void upload(String name, GpuBufferSlice buffer);
   }
}
