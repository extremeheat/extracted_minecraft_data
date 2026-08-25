package com.mojang.blaze3d.pipeline;

import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.Map;
import net.minecraft.util.Util;
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

   public void insert(final RenderPipeline pipeline, final CompiledRenderPipeline compiled) {
      this.cache.put(pipeline, compiled);
   }

   public @Nullable CompiledRenderPipeline get(final RenderPipeline pipeline) {
      CompiledRenderPipeline cachedPipeline = (CompiledRenderPipeline)this.cache.get(pipeline);
      if (cachedPipeline != null) {
         return cachedPipeline;
      } else {
         CompiledRenderPipeline newPipeline = ((CompiledRenderPipeline.Pending)this.device.compilePipeline(pipeline, this.shaderSource, Util.backgroundExecutor()).join()).finishCompile();
         if (newPipeline == null) {
            return null;
         } else {
            this.cache.put(pipeline, newPipeline);
            return newPipeline;
         }
      }
   }

   public void clear() {
      this.cache.values().forEach(UncheckedAutoCloseable::close);
      this.cache.clear();
   }

   public void close() {
      this.clear();
   }
}
