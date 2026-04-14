package net.minecraft.client.profiling;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.extract.LevelExtractor;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.profiling.ProfilerSamplerAdapter;
import net.minecraft.util.profiling.metrics.profiling.ServerMetricsSamplersProvider;

public class ClientMetricsSamplersProvider implements MetricsSamplerProvider {
   private final LevelRenderer levelRenderer;
   private final LevelExtractor levelExtractor;
   private final Set<MetricSampler> samplers = new ObjectOpenHashSet();
   private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

   public ClientMetricsSamplersProvider(final LongSupplier wallTimeSource, final LevelRenderer levelRenderer, final LevelExtractor levelExtractor) {
      super();
      this.levelRenderer = levelRenderer;
      this.levelExtractor = levelExtractor;
      this.samplers.add(ServerMetricsSamplersProvider.tickTimeSampler(wallTimeSource));
      this.registerStaticSamplers();
   }

   private void registerStaticSamplers() {
      this.samplers.addAll(ServerMetricsSamplersProvider.runtimeIndependentSamplers());
      Set var10000 = this.samplers;
      MetricCategory var10002 = MetricCategory.CHUNK_RENDERING;
      LevelExtractor var10003 = this.levelExtractor;
      Objects.requireNonNull(var10003);
      var10000.add(MetricSampler.createExtractSampler("totalChunks", var10002, var10003::totalSections));
      var10000 = this.samplers;
      var10002 = MetricCategory.CHUNK_RENDERING;
      var10003 = this.levelExtractor;
      Objects.requireNonNull(var10003);
      var10000.add(MetricSampler.createExtractSampler("renderedChunks", var10002, var10003::countRenderedSections));
      var10000 = this.samplers;
      var10002 = MetricCategory.CHUNK_RENDERING;
      var10003 = this.levelExtractor;
      Objects.requireNonNull(var10003);
      var10000.add(MetricSampler.createExtractSampler("lastViewDistance", var10002, var10003::lastViewDistance));
      SectionRenderDispatcher sectionRenderDispatcher = this.levelRenderer.sectionRenderDispatcher();
      if (sectionRenderDispatcher != null) {
         var10000 = this.samplers;
         var10002 = MetricCategory.CHUNK_RENDERING_DISPATCHING;
         Objects.requireNonNull(sectionRenderDispatcher);
         var10000.add(MetricSampler.createExtractSampler("freeBufferCount", var10002, sectionRenderDispatcher::getFreeBufferCount));
         var10000 = this.samplers;
         var10002 = MetricCategory.CHUNK_RENDERING_DISPATCHING;
         Objects.requireNonNull(sectionRenderDispatcher);
         var10000.add(MetricSampler.createExtractSampler("compileQueueSize", var10002, sectionRenderDispatcher::getCompileQueueSize));
      }

      var10000 = this.samplers;
      var10002 = MetricCategory.GPU;
      Minecraft var14 = Minecraft.getInstance();
      Objects.requireNonNull(var14);
      var10000.add(MetricSampler.createExtractSampler("gpuUtilization", var10002, var14::getGpuUtilization));
   }

   public Set<MetricSampler> samplers(final Supplier<ProfileCollector> singleTickProfiler) {
      this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(singleTickProfiler));
      return this.samplers;
   }
}
