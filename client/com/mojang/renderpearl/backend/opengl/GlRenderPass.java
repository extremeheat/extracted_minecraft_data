package com.mojang.renderpearl.backend.opengl;

import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.renderpearl.api.buffers.GpuBuffer;
import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import com.mojang.renderpearl.api.commands.GpuQueryPool;
import com.mojang.renderpearl.api.pipeline.IndexType;
import com.mojang.renderpearl.backend.api.BackendRenderPipeline;
import com.mojang.renderpearl.backend.api.RenderPassBackend;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;

class GlRenderPass implements RenderPassBackend {
   private final GlCommandEncoder encoder;
   private final GlDevice device;
   private final ScissorState defaultScissorState;
   protected @Nullable GlRenderPipeline pipeline;
   protected final @Nullable GpuBufferSlice[] vertexBuffers = new GpuBufferSlice[16];
   protected boolean vertexBufferDirty = true;
   protected @Nullable GpuBuffer indexBuffer;
   protected IndexType indexType;
   protected boolean indexBufferDirty;
   private final ScissorState scissorState;
   protected boolean scissorStateDirty;
   protected final ReferenceList<Object> uniforms;
   protected final BooleanList dirtyUniforms;
   protected boolean anyUniformDirty;
   protected @Nullable GpuBufferSlice pushConstants;
   protected boolean pushConstantsDirty;
   protected final int colorAttachmentCount;

   public GlRenderPass(final GlCommandEncoder encoder, final GlDevice device, final int colorAttachmentCount, final ScissorState defaultScissorState) {
      super();
      this.indexType = IndexType.INT;
      this.indexBufferDirty = false;
      this.scissorState = new ScissorState();
      this.scissorStateDirty = true;
      this.uniforms = new ReferenceArrayList();
      this.dirtyUniforms = new BooleanArrayList();
      this.anyUniformDirty = false;
      this.pushConstantsDirty = false;
      this.encoder = encoder;
      this.device = device;
      this.colorAttachmentCount = colorAttachmentCount;
      this.defaultScissorState = defaultScissorState;
      this.scissorState.setFrom(defaultScissorState);
   }

   public void pushDebugGroup(final Supplier<String> label) {
      this.device.debugLabels().pushDebugGroup(label);
   }

   public void popDebugGroup() {
      this.device.debugLabels().popDebugGroup();
   }

   public void setPipeline(final BackendRenderPipeline pipeline) {
      if (!(pipeline instanceof GlRenderPipeline glRenderPipeline)) {
         throw new IllegalArgumentException("Pipeline must be instance of GlRenderPipeline");
      } else {
         if (this.pipeline == null || this.pipeline != pipeline) {
            this.uniforms.clear();
            this.uniforms.size(glRenderPipeline.program().uniformCount());
            this.dirtyUniforms.clear();
            this.dirtyUniforms.size(glRenderPipeline.program().uniformCount());

            for(int i = 0; i < this.dirtyUniforms.size(); ++i) {
               this.dirtyUniforms.set(i, true);
            }

            this.anyUniformDirty = true;
         }

         this.pipeline = glRenderPipeline;
         this.vertexBufferDirty = true;
         this.indexBufferDirty = this.indexBuffer != null;
      }
   }

   public void setUniform(final int index, final @Nullable Object value) {
      this.uniforms.set(index, value);
      this.dirtyUniforms.set(index, true);
      this.anyUniformDirty = true;
   }

   public void pushConstants(final ByteBuffer value) {
      this.pushConstants = this.encoder.transientMemory().uploadGpu(value, (long)this.device.getDeviceInfo().limits().minUniformOffsetAlignment(), 128);
      this.pushConstantsDirty = true;
   }

   public void enableScissor(final int x, final int y, final int width, final int height) {
      this.scissorState.enable(x, y, width, height);
      this.scissorStateDirty = true;
   }

   public void disableScissor() {
      this.scissorState.setFrom(this.defaultScissorState);
      this.scissorStateDirty = true;
   }

   public boolean isScissorEnabled() {
      return this.scissorState.enabled();
   }

   public int getScissorX() {
      return this.scissorState.x();
   }

   public int getScissorY() {
      return this.scissorState.y();
   }

   public int getScissorWidth() {
      return this.scissorState.width();
   }

   public int getScissorHeight() {
      return this.scissorState.height();
   }

   public void setVertexBuffer(final int slot, final @Nullable GpuBufferSlice vertexBuffer) {
      GpuBuffer inputBuffer = vertexBuffer != null ? vertexBuffer.buffer() : null;
      GpuBuffer existingBuffer = this.vertexBuffers[slot] != null ? this.vertexBuffers[slot].buffer() : null;
      long inputOffset = vertexBuffer != null ? vertexBuffer.offset() : 0L;
      long exitingOffset = this.vertexBuffers[slot] != null ? this.vertexBuffers[slot].offset() : 0L;
      this.vertexBufferDirty |= inputBuffer != existingBuffer || inputOffset != exitingOffset;
      this.vertexBuffers[slot] = vertexBuffer;
   }

   public void setIndexBuffer(final @Nullable GpuBuffer indexBuffer, final IndexType indexType) {
      this.indexBuffer = indexBuffer;
      this.indexType = indexType;
      this.indexBufferDirty = true;
   }

   public void drawIndexed(final int indexCount, final int instanceCount, final int firstIndex, final int vertexOffset, final int firstInstance) {
      this.encoder.executeDraw(this, vertexOffset, firstIndex, indexCount, this.indexType, instanceCount, firstInstance);
   }

   public void multiDrawIndexed(final IntBuffer drawParameters, final int instanceCount, final int firstInstance, final int drawCount) {
      throw new UnsupportedOperationException("OpenGL does not support the multiDrawDirectInterleaved device feature");
   }

   public void multiDrawIndexed(final PointerBuffer firstIndexOffsets, final IntBuffer indexCounts, final IntBuffer vertexOffsets, final int drawCount) {
      this.encoder.executeDraws(this, this.indexType, firstIndexOffsets, indexCounts, vertexOffsets, drawCount);
   }

   public void drawIndexedIndirect(final GpuBufferSlice commands, final int drawCount) {
      this.encoder.executeDrawIndirect(this, this.indexType, (GlBuffer)commands.buffer(), commands.offset(), drawCount);
   }

   public void draw(final int vertexCount, final int instanceCount, final int firstVertex, final int firstInstance) {
      this.encoder.executeDraw(this, firstVertex, 0, vertexCount, (IndexType)null, instanceCount, firstInstance);
   }

   public void multiDraw(final IntBuffer drawParameters, final int instanceCount, final int firstInstance, final int drawCount) {
      throw new UnsupportedOperationException("OpenGL does not support the multiDrawDirectInterleaved device feature");
   }

   public void multiDraw(final IntBuffer firstVertices, final IntBuffer vertexCounts, final int drawCount) {
      this.encoder.executeDraws(this, (IndexType)null, (PointerBuffer)null, vertexCounts, firstVertices, drawCount);
   }

   public void drawIndirect(final GpuBufferSlice commands, final int drawCount) {
      this.encoder.executeDrawIndirect(this, (IndexType)null, (GlBuffer)commands.buffer(), commands.offset(), drawCount);
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      ((GlQueryPool)pool).writeTimestamp(index);
   }
}
