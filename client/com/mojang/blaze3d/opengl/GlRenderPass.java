package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.ScissorState;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import org.joml.Matrix4f;

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
   protected final ScissorState scissorState;
   protected final HashMap<String, Object> uniforms;
   protected final HashMap<String, GpuTexture> samplers;
   protected final Set<String> dirtyUniforms;
   protected final Set<String> dirtySamplers;

   public GlRenderPass(GlCommandEncoder var1, boolean var2) {
      super();
      this.indexType = VertexFormat.IndexType.INT;
      this.scissorState = new ScissorState();
      this.uniforms = new HashMap();
      this.samplers = new HashMap();
      this.dirtyUniforms = new HashSet();
      this.dirtySamplers = new HashSet();
      this.encoder = var1;
      this.hasDepthTexture = var2;
   }

   public boolean hasDepthTexture() {
      return this.hasDepthTexture;
   }

   public void setPipeline(RenderPipeline var1) {
      if (this.pipeline == null || this.pipeline.info() != var1) {
         this.dirtyUniforms.addAll(this.uniforms.keySet());
         this.dirtySamplers.addAll(this.samplers.keySet());
      }

      this.pipeline = this.encoder.getDevice().getOrCompilePipeline(var1);
   }

   public void bindSampler(String var1, GpuTexture var2) {
      this.samplers.put(var1, var2);
      this.dirtySamplers.add(var1);
   }

   public void setUniform(String var1, int... var2) {
      this.uniforms.put(var1, var2);
      this.dirtyUniforms.add(var1);
   }

   public void setUniform(String var1, float... var2) {
      this.uniforms.put(var1, var2);
      this.dirtyUniforms.add(var1);
   }

   public void setUniform(String var1, Matrix4f var2) {
      this.uniforms.put(var1, var2.get(new float[16]));
      this.dirtyUniforms.add(var1);
   }

   public void enableScissor(ScissorState var1) {
      this.scissorState.copyFrom(var1);
   }

   public void enableScissor(int var1, int var2, int var3, int var4) {
      this.scissorState.enable(var1, var2, var3, var4);
   }

   public void disableScissor() {
      this.scissorState.disable();
   }

   public void setVertexBuffer(int var1, GpuBuffer var2) {
      if (var1 >= 0 && var1 < 1) {
         this.vertexBuffers[var1] = var2;
      } else {
         throw new IllegalArgumentException("Vertex buffer slot is out of range: " + var1);
      }
   }

   public void setIndexBuffer(GpuBuffer var1, VertexFormat.IndexType var2) {
      this.indexBuffer = var1;
      this.indexType = var2;
   }

   public void drawIndexed(int var1, int var2) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.encoder.executeDraw(this, var1, var2, this.indexType);
      }
   }

   public void drawMultipleIndexed(Collection<RenderPass.Draw> var1) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.encoder.executeDrawMultiple(this, var1);
      }
   }

   public void draw(int var1, int var2) {
      if (this.closed) {
         throw new IllegalStateException("Can't use a closed render pass");
      } else {
         this.encoder.executeDraw(this, var1, var2, (VertexFormat.IndexType)null);
      }
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.encoder.finishRenderPass();
      }

   }

   static {
      VALIDATION = SharedConstants.IS_RUNNING_IN_IDE;
   }
}
