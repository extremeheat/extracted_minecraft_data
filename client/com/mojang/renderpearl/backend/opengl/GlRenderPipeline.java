package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.backend.api.BackendRenderPipeline;
import java.util.Objects;
import java.util.Optional;
import org.lwjgl.opengl.GL33C;

public final class GlRenderPipeline implements BackendRenderPipeline {
   private final GlDevice device;
   private final GlProgram program;
   private final VertexArray vertexArray;
   private final int primitiveTopology;
   private final boolean cull;
   private final int polygonMode;
   private final boolean depthEnabled;
   private final int depthFunc;
   private final boolean writeDepth;
   private final float depthBiasConstant;
   private final float depthBiasScaleFactor;
   private final @ColorTargetState.WriteMask int[] writeMasks;
   private final boolean[] blendEnabled;
   private final int srcRgb;
   private final int dstRgb;
   private final int modeRgb;
   private final int srcAlpha;
   private final int dstAlpha;
   private final int modeAlpha;
   private boolean closed = false;

   GlRenderPipeline(final GlDevice device, final CompiledRenderPipeline.CreateInfo createInfo, final GlProgram program, final VertexArray vertexArray) {
      super();
      this.device = device;
      this.program = program;
      this.vertexArray = vertexArray;
      this.primitiveTopology = GlConst.toGl(createInfo.primitiveTopology());
      this.cull = createInfo.cull();
      this.polygonMode = GlConst.toGl(createInfo.polygonMode());
      this.depthEnabled = createInfo.depthStencilState() != null;
      if (this.depthEnabled) {
         this.depthFunc = GlConst.toGl(createInfo.depthStencilState().depthTest());
         this.writeDepth = createInfo.depthStencilState().writeDepth();
         this.depthBiasConstant = createInfo.depthStencilState().depthBiasConstant();
         this.depthBiasScaleFactor = createInfo.depthStencilState().depthBiasScaleFactor();
      } else {
         this.depthFunc = 1280;
         this.writeDepth = false;
         this.depthBiasConstant = 0.0F;
         this.depthBiasScaleFactor = 0.0F;
      }

      this.writeMasks = new int[createInfo.colorTargetStates().size()];
      this.blendEnabled = new boolean[createInfo.colorTargetStates().size()];

      for(int i = 0; i < createInfo.colorTargetStates().size(); ++i) {
         ColorTargetState state = (ColorTargetState)createInfo.colorTargetStates().get(i);
         if (state != null) {
            this.writeMasks[i] = state.writeMask() & 15;
            this.blendEnabled[i] = state.blendFunction().isPresent();
         } else {
            this.writeMasks[i] = -1;
            this.blendEnabled[i] = false;
         }
      }

      Optional<BlendFunction> representativeBlendState = createInfo.colorTargetStates().stream().filter(Objects::nonNull).map(ColorTargetState::blendFunction).filter(Optional::isPresent).map(Optional::get).findFirst();
      if (representativeBlendState.isPresent()) {
         BlendFunction blendFunction = (BlendFunction)representativeBlendState.get();
         this.srcRgb = GlConst.toGl(blendFunction.color().sourceFactor());
         this.dstRgb = GlConst.toGl(blendFunction.color().destFactor());
         this.modeRgb = GlConst.toGl(blendFunction.color().op());
         this.srcAlpha = GlConst.toGl(blendFunction.alpha().sourceFactor());
         this.dstAlpha = GlConst.toGl(blendFunction.alpha().destFactor());
         this.modeAlpha = GlConst.toGl(blendFunction.alpha().op());
      } else {
         this.srcRgb = 1280;
         this.dstRgb = 1280;
         this.modeRgb = 1280;
         this.srcAlpha = 1280;
         this.dstAlpha = 1280;
         this.modeAlpha = 1280;
      }

   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.device.ensureCurrent();
         this.program.close();
         this.vertexArray.close();
         this.device.markAmdShaderCompilerAngry();
      }
   }

   public GlProgram program() {
      return this.program;
   }

   public VertexArray vertexArray() {
      return this.vertexArray;
   }

   public void bind() {
      GL33C.glUseProgram(this.program.getProgramId());
      GlStateManager stateManager = this.device.stateManager();
      if (this.depthEnabled) {
         stateManager._enableDepthTest();
         stateManager._depthFunc(this.depthFunc);
         stateManager._depthMask(this.writeDepth);
         if (this.depthBiasConstant == 0.0F && this.depthBiasScaleFactor == 0.0F) {
            stateManager._disablePolygonOffset();
         } else {
            stateManager._polygonOffset(this.depthBiasScaleFactor, this.depthBiasConstant);
            stateManager._enablePolygonOffset();
         }
      } else {
         stateManager._disableDepthTest();
         stateManager._depthMask(false);
         stateManager._disablePolygonOffset();
      }

      if (this.cull) {
         stateManager._enableCull();
      } else {
         stateManager._disableCull();
      }

      for(int i = 0; i < this.writeMasks.length; ++i) {
         if (this.writeMasks[i] != -1) {
            stateManager._colorMask(i, this.writeMasks[i]);
         } else {
            stateManager._colorMask(i, 0);
         }

         if (this.blendEnabled[i]) {
            stateManager._enableBlend(i);
         } else {
            stateManager._disableBlend(i);
         }
      }

      if (this.srcRgb != 1280) {
         stateManager._blendFuncSeparate(this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha);
         stateManager._blendEquationSeparate(this.modeRgb, this.modeAlpha);
      }

      GL33C.glPolygonMode(1032, this.polygonMode);
   }

   public int primitiveTopology() {
      return this.primitiveTopology;
   }
}
