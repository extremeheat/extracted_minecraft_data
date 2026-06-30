package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class PipelineCache implements AutoCloseable {
   private final GpuDevice device;
   private final ShaderSource shaderSource;
   private final Map<RenderPipeline, CompiledRenderPipeline> cache = new Reference2ReferenceOpenHashMap();

   public PipelineCache(final GpuDevice device, final ShaderSource shaderSource) {
      super();
      this.device = device;
      this.shaderSource = shaderSource;
   }

   public @Nullable CompiledRenderPipeline get(final RenderPipeline pipeline) {
      CompiledRenderPipeline cachedPipeline = (CompiledRenderPipeline)this.cache.get(pipeline);
      if (cachedPipeline != null) {
         return cachedPipeline;
      } else {
         CompiledRenderPipeline newPipeline = this.device.compilePipeline(pipeline, this.shaderSource);
         if (newPipeline == null) {
            return null;
         } else {
            this.cache.put(pipeline, newPipeline);
            return newPipeline;
         }
      }
   }

   public void clear() {
      this.cache.values().forEach(CompiledRenderPipeline::close);
      this.cache.clear();
   }

   public void close() {
      this.clear();
   }
}
