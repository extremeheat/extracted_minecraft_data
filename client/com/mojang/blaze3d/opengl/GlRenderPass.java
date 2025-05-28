package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;

public class GlRenderPass implements RenderPass {
   protected static final int MAX_VERTEX_BUFFERS = 1;
   public static final boolean VALIDATION;
   private final GlCommandEncoder encoder;
   private final boolean hasDepthTexture;
   private boolean closed;
   @Nullable
   protected GlRenderPipeline pipeline;
   protected final GpuBuffer[] vertexBuffers = new GpuBuffer[1];
   @Nullable
   protected GpuBuffer indexBuffer;
   protected VertexFormat.IndexType indexType;
   private final ScissorState scissorState;
   protected final HashMap<String, GpuBufferSlice> uniforms;
   protected final HashMap<String, GpuTextureView> samplers;
   protected final Set<String> dirtyUniforms;
   protected int pushedDebugGroups;

   public GlRenderPass(GlCommandEncoder var1, boolean var2) {
      super();
      this.indexType = VertexFormat.IndexType.INT;
      this.scissorState = new ScissorState();
      this.uniforms = new HashMap();
      this.samplers = new HashMap();
      this.dirtyUniforms = new HashSet();
      this.encoder = var1;
      this.hasDepthTexture = var2;
   }

   public boolean hasDepthTexture() {
      return this.hasDepthTexture;
   }

   public void pushDebugGroup(Supplier<String> var1) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         ++this.pushedDebugGroups;
         this.encoder.getDevice().debugLabels().pushDebugGroup(var1);
      }
   }

   public void popDebugGroup() {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else if (this.pushedDebugGroups == 0) {
         throw new IllegalStateException("Can't pop more debug groups than was pushed!");
      } else {
         --this.pushedDebugGroups;
         this.encoder.getDevice().debugLabels().popDebugGroup();
      }
   }

   public void setPipeline(RenderPipeline var1) {
      if (this.pipeline == null || this.pipeline.info() != var1) {
         this.dirtyUniforms.addAll(this.uniforms.keySet());
         this.dirtyUniforms.addAll(this.samplers.keySet());
      }

      this.pipeline = this.encoder.getDevice().getOrCompilePipeline(var1);
   }

   public void bindSampler(String var1, @Nullable GpuTextureView var2) {
      if (var2 == null) {
         this.samplers.remove(var1);
      } else {
         this.samplers.put(var1, var2);
      }

      this.dirtyUniforms.add(var1);
   }

   public void setUniform(String var1, GpuBuffer var2) {
      this.uniforms.put(var1, var2.slice());
      this.dirtyUniforms.add(var1);
   }

   public void setUniform(String var1, GpuBufferSlice var2) {
      int var3 = this.encoder.getDevice().getUniformOffsetAlignment();
      if (var2.offset() % var3 > 0) {
         throw new IllegalArgumentException("Uniform buffer offset must be aligned to " + var3);
      } else {
         this.uniforms.put(var1, var2);
         this.dirtyUniforms.add(var1);
      }
   }

   public void enableScissor(int var1, int var2, int var3, int var4) {
      this.scissorState.enable(var1, var2, var3, var4);
   }

   public void disableScissor() {
      this.scissorState.disable();
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

   public void setVertexBuffer(int var1, GpuBuffer var2) {
      if (var1 >= 0 && var1 < 1) {
         this.vertexBuffers[var1] = var2;
      } else {
         throw new IllegalArgumentException("Vertex buffer slot is out of range: " + var1);
      }
   }

   public void setIndexBuffer(@Nullable GpuBuffer var1, VertexFormat.IndexType var2) {
      this.indexBuffer = var1;
      this.indexType = var2;
   }

   public void drawIndexed(int var1, int var2, int var3, int var4) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.encoder.executeDraw(this, var1, var2, var3, this.indexType, var4);
      }
   }

   public <T> void drawMultipleIndexed(Collection<RenderPass.Draw<T>> var1, @Nullable GpuBuffer var2, @Nullable VertexFormat.IndexType var3, Collection<String> var4, T var5) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.encoder.executeDrawMultiple(this, var1, var2, var3, var4, var5);
      }
   }

   public void draw(int var1, int var2) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.encoder.executeDraw(this, var1, 0, var2, (VertexFormat.IndexType)null, 1);
      }
   }

   public void close() {
      if (!this.closed) {
         if (this.pushedDebugGroups > 0) {
            throw new IllegalStateException("Render pass had debug groups left open!");
         }

         this.closed = true;
         this.encoder.finishRenderPass();
      }

   }

   static {
      VALIDATION = SharedConstants.IS_RUNNING_IN_IDE;
   }
}
