package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.IndexType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;

class GlRenderPass implements RenderPassBackend {
   public static final boolean VALIDATION;
   private final GlCommandEncoder encoder;
   private final GlDevice device;
   private final boolean hasDepthTexture;
   private final ScissorState defaultScissorState;
   protected @Nullable GlRenderPipeline pipeline;
   protected final @Nullable GpuBufferSlice[] vertexBuffers = new GpuBufferSlice[16];
   protected boolean vertexBufferDirty = true;
   protected @Nullable GpuBuffer indexBuffer;
   protected IndexType indexType;
   private final ScissorState scissorState;
   protected final HashMap<String, GpuBufferSlice> uniforms;
   protected final HashMap<String, TextureViewAndSampler> samplers;
   protected final Set<String> dirtyUniforms;
   protected final int colorAttachmentCount;

   public GlRenderPass(final GlCommandEncoder encoder, final GlDevice device, final boolean hasDepthTexture, final int colorAttachmentCount, final ScissorState defaultScissorState) {
      super();
      this.indexType = IndexType.INT;
      this.scissorState = new ScissorState();
      this.uniforms = new HashMap();
      this.samplers = new HashMap();
      this.dirtyUniforms = new HashSet();
      this.encoder = encoder;
      this.device = device;
      this.hasDepthTexture = hasDepthTexture;
      this.colorAttachmentCount = colorAttachmentCount;
      this.defaultScissorState = defaultScissorState;
      this.scissorState.setFrom(defaultScissorState);
   }

   public boolean hasDepthTexture() {
      return this.hasDepthTexture;
   }

   public void pushDebugGroup(final Supplier<String> label) {
      this.device.debugLabels().pushDebugGroup(label);
   }

   public void popDebugGroup() {
      this.device.debugLabels().popDebugGroup();
   }

   public void setPipeline(final RenderPipeline pipeline) {
      if (this.pipeline == null || this.pipeline.info() != pipeline) {
         this.dirtyUniforms.addAll(this.uniforms.keySet());
         this.dirtyUniforms.addAll(this.samplers.keySet());
      }

      this.pipeline = this.device.getOrCompilePipeline(pipeline);
   }

   public void bindTexture(final String name, final @Nullable GpuTextureView textureView, final @Nullable GpuSampler sampler) {
      if (sampler == null) {
         this.samplers.remove(name);
      } else {
         this.samplers.put(name, new TextureViewAndSampler((GlTextureView)textureView, (GlSampler)sampler));
      }

      this.dirtyUniforms.add(name);
   }

   public void setUniform(final String name, final GpuBuffer value) {
      this.uniforms.put(name, value.slice());
      this.dirtyUniforms.add(name);
   }

   public void setUniform(final String name, final GpuBufferSlice value) {
      this.uniforms.put(name, value);
      this.dirtyUniforms.add(name);
   }

   public void enableScissor(final int x, final int y, final int width, final int height) {
      this.scissorState.enable(x, y, width, height);
   }

   public void disableScissor() {
      this.scissorState.setFrom(this.defaultScissorState);
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
   }

   public void drawIndexed(final int baseVertex, final int firstIndex, final int indexCount, final int instanceCount) {
      this.encoder.executeDraw(this, baseVertex, firstIndex, indexCount, this.indexType, instanceCount);
   }

   public <T> void drawMultipleIndexed(final Collection<RenderPass.Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, final @Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument) {
      this.encoder.executeDrawMultiple(this, draws, defaultIndexBuffer, defaultIndexType, dynamicUniforms, uniformArgument);
   }

   public void draw(final int firstVertex, final int vertexCount) {
      this.encoder.executeDraw(this, firstVertex, 0, vertexCount, (IndexType)null, 1);
   }

   public void writeTimestamp(final GpuQueryPool pool, final int index) {
      ((GlQueryPool)pool).writeTimestamp(index);
   }

   static {
      VALIDATION = SharedConstants.IS_RUNNING_IN_IDE;
   }

   protected static record TextureViewAndSampler(GlTextureView view, GlSampler sampler) {
      protected TextureViewAndSampler {
         super();
      }
   }
}
