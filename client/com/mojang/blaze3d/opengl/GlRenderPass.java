package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.GpuQueryPool;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderPassBackend;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import org.jspecify.annotations.Nullable;

class GlRenderPass implements RenderPassBackend {
   protected static final int MAX_VERTEX_BUFFERS = 1;
   public static final boolean VALIDATION;
   private final GlCommandEncoder encoder;
   private final GlDevice device;
   private final boolean hasDepthTexture;
   private final ScissorState defaultScissorState;
   protected @Nullable GlRenderPipeline pipeline;
   protected final @Nullable GpuBuffer[] vertexBuffers = new GpuBuffer[1];
   protected @Nullable GpuBuffer indexBuffer;
   protected VertexFormat.IndexType indexType;
   private final ScissorState scissorState;
   protected final HashMap<String, GpuBufferSlice> uniforms;
   protected final HashMap<String, TextureViewAndSampler> samplers;
   protected final Set<String> dirtyUniforms;

   public GlRenderPass(final GlCommandEncoder encoder, final GlDevice device, final boolean hasDepthTexture, final ScissorState defaultScissorState) {
      super();
      this.indexType = VertexFormat.IndexType.INT;
      this.scissorState = new ScissorState();
      this.uniforms = new HashMap();
      this.samplers = new HashMap();
      this.dirtyUniforms = new HashSet();
      this.encoder = encoder;
      this.device = device;
      this.hasDepthTexture = hasDepthTexture;
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

   public void setVertexBuffer(final int slot, final GpuBuffer vertexBuffer) {
      if (slot >= 0 && slot < 1) {
         this.vertexBuffers[slot] = vertexBuffer;
      } else {
         throw new IllegalArgumentException("Vertex buffer slot is out of range: " + slot);
      }
   }

   public void setIndexBuffer(final @Nullable GpuBuffer indexBuffer, final VertexFormat.IndexType indexType) {
      this.indexBuffer = indexBuffer;
      this.indexType = indexType;
   }

   public void drawIndexed(final int baseVertex, final int firstIndex, final int indexCount, final int instanceCount) {
      this.encoder.executeDraw(this, baseVertex, firstIndex, indexCount, this.indexType, instanceCount);
   }

   public <T> void drawMultipleIndexed(final Collection<RenderPass.Draw<T>> draws, final @Nullable GpuBuffer defaultIndexBuffer, final VertexFormat.@Nullable IndexType defaultIndexType, final Collection<String> dynamicUniforms, final T uniformArgument) {
      this.encoder.executeDrawMultiple(this, draws, defaultIndexBuffer, defaultIndexType, dynamicUniforms, uniformArgument);
   }

   public void draw(final int firstVertex, final int vertexCount) {
      this.encoder.executeDraw(this, firstVertex, 0, vertexCount, (VertexFormat.IndexType)null, 1);
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
