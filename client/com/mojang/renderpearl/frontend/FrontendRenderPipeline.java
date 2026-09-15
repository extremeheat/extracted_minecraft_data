package com.mojang.renderpearl.frontend;

import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.backend.api.BackendRenderPipeline;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.List;
import org.jspecify.annotations.Nullable;

public record FrontendRenderPipeline(String name, BackendRenderPipeline backendRenderPipeline, List<@Nullable VertexFormat> vertexFormats, Object2IntMap<String> uniformIndices, List<BindGroupLayout.UniformDescription> uniforms, List<@Nullable ColorTargetState> colorTargetStates, boolean wantsDepthTexture, int pushConstantSize) implements CompiledRenderPipeline {
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
