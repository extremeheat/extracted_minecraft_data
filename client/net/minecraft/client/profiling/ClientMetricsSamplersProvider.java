package net.minecraft.client.profiling;

import com.mojang.blaze3d.systems.TimerQuery;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.profiling.ProfilerSamplerAdapter;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;

public class ClientMetricsSamplersProvider implements MetricsSamplerProvider {
   private final LevelRenderer levelRenderer;
   private final Set<MetricSampler> samplers = new ObjectOpenHashSet();
   private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

   public ClientMetricsSamplersProvider(LongSupplier var1, LevelRenderer var2) {
      super();
      this.levelRenderer = var2;
      this.samplers.add(ServerMetricsSamplersProvider.tickTimeSampler(var1));
      this.registerStaticSamplers();
   }

   private void registerStaticSamplers() {
      this.samplers.addAll(ServerMetricsSamplersProvider.runtimeIndependentSamplers());
      this.samplers.add(MetricSampler.create("totalChunks", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::getTotalChunks));
      this.samplers.add(MetricSampler.create("renderedChunks", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::countRenderedChunks));
      this.samplers.add(MetricSampler.create("lastViewDistance", MetricCategory.CHUNK_RENDERING, this.levelRenderer, LevelRenderer::getLastViewDistance));
      ChunkRenderDispatcher var1 = this.levelRenderer.getChunkRenderDispatcher();
      this.samplers.add(MetricSampler.create("toUpload", MetricCategory.CHUNK_RENDERING_DISPATCHING, var1, ChunkRenderDispatcher::getToUpload));
      this.samplers.add(MetricSampler.create("freeBufferCount", MetricCategory.CHUNK_RENDERING_DISPATCHING, var1, ChunkRenderDispatcher::getFreeBufferCount));
      this.samplers.add(MetricSampler.create("toBatchCount", MetricCategory.CHUNK_RENDERING_DISPATCHING, var1, ChunkRenderDispatcher::getToBatchCount));
      if (TimerQuery.getInstance().isPresent()) {
         this.samplers.add(MetricSampler.create("gpuUtilization", MetricCategory.GPU, Minecraft.getInstance(), Minecraft::getGpuUtilization));
      }
   }

   @Override
   public Set<MetricSampler> samplers(Supplier<ProfileCollector> var1) {
      this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(var1));
      return this.samplers;
   }
}
