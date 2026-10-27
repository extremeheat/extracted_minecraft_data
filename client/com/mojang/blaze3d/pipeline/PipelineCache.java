package com.mojang.blaze3d.pipeline;

import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.RenderPassLayout;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public class PipelineCache implements AutoCloseable {
   private final PipelineBuilder pipelineBuilder;
   private final ShaderSource shaderSource;
   private final Map<Key, CompiledRenderPipeline> cache = new Object2ReferenceOpenHashMap();

   public PipelineCache(final PipelineBuilder pipelineBuilder, final ShaderSource shaderSource) {
      super();
      this.pipelineBuilder = pipelineBuilder;
      this.shaderSource = shaderSource;
   }

   public void insert(final Key pipeline, final CompiledRenderPipeline compiled) {
      this.cache.put(pipeline, compiled);
   }

   public @Nullable CompiledRenderPipeline get(final RenderPipeline pipeline, final RenderPassLayout.Subpass subpass) {
      return this.get(new Key(pipeline, subpass));
   }

   public @Nullable CompiledRenderPipeline get(final Key key) {
      CompiledRenderPipeline cachedPipeline = (CompiledRenderPipeline)this.cache.get(key);
      if (cachedPipeline != null) {
         return cachedPipeline;
      } else {
         CompiledRenderPipeline newPipeline = this.pipelineBuilder.compilePipeline(key.pipeline(), key.subpass(), this.shaderSource).finishCompile();
         if (newPipeline == null) {
            return null;
         } else {
            this.cache.put(key, newPipeline);
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
      this.shaderSource.close();
   }

   public static record Key(RenderPipeline pipeline, RenderPassLayout.Subpass subpass) {
      public Key {
         super();
      }
   }
}
