package com.mojang.renderpearl.frontend;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.backend.api.BackendRenderPipeline;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record FrontendRenderPipeline(String name, BackendRenderPipeline backendRenderPipeline, IntList usedVertexBufferSlots, Object2IntMap<String> uniformIndices, List<CompiledRenderPipeline.CreateInfo.Uniform> uniforms, List<@Nullable ColorTargetState> colorTargetStates, boolean wantsDepthTexture, GpuFormat depthStencilFormat, int pushConstantSize) implements CompiledRenderPipeline {
   public FrontendRenderPipeline {
      super();
   }

   public boolean isClosed() {
      return this.backendRenderPipeline().isClosed();
   }

   public void close() {
      this.backendRenderPipeline.close();
   }
}
