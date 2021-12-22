package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsRegistry;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class ServerMetricsSamplersProvider implements MetricsSamplerProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Set<MetricSampler> samplers = new ObjectOpenHashSet();
   private final ProfilerSamplerAdapter samplerFactory = new ProfilerSamplerAdapter();

   public ServerMetricsSamplersProvider(LongSupplier var1, boolean var2) {
      super();
      this.samplers.add(tickTimeSampler(var1));
      if (var2) {
         this.samplers.addAll(runtimeIndependentSamplers());
      }

   }

   public static Set<MetricSampler> runtimeIndependentSamplers() {
      Builder var0 = ImmutableSet.builder();

      try {
         ServerMetricsSamplersProvider.CpuStats var1 = new ServerMetricsSamplersProvider.CpuStats();
         Stream var10000 = IntStream.range(0, var1.nrOfCpus).mapToObj((var1x) -> {
            return MetricSampler.create("cpu#" + var1x, MetricCategory.CPU, () -> {
               return var1.loadForCpu(var1x);
            });
         });
         Objects.requireNonNull(var0);
         var10000.forEach(var0::add);
      } catch (Throwable var2) {
         LOGGER.warn("Failed to query cpu, no cpu stats will be recorded", var2);
      }

      var0.add(MetricSampler.create("heap MiB", MetricCategory.JVM, () -> {
         return (double)((float)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0F);
      }));
      var0.addAll(MetricsRegistry.INSTANCE.getRegisteredSamplers());
      return var0.build();
   }

   public Set<MetricSampler> samplers(Supplier<ProfileCollector> var1) {
      this.samplers.addAll(this.samplerFactory.newSamplersFoundInProfiler(var1));
      return this.samplers;
   }

   public static MetricSampler tickTimeSampler(final LongSupplier var0) {
      Stopwatch var1 = Stopwatch.createUnstarted(new Ticker() {
         public long read() {
            return var0.getAsLong();
         }
      });
      ToDoubleFunction var2 = (var0x) -> {
         if (var0x.isRunning()) {
            var0x.stop();
         }

         long var1 = var0x.elapsed(TimeUnit.NANOSECONDS);
         var0x.reset();
         return (double)var1;
      };
      MetricSampler.ValueIncreasedByPercentage var3 = new MetricSampler.ValueIncreasedByPercentage(2.0F);
      return MetricSampler.builder("ticktime", MetricCategory.TICK_LOOP, var2, var1).withBeforeTick(Stopwatch::start).withThresholdAlert(var3).build();
   }

   static class CpuStats {
      private final SystemInfo systemInfo = new SystemInfo();
      private final CentralProcessor processor;
      public final int nrOfCpus;
      private long[][] previousCpuLoadTick;
      private double[] currentLoad;
      private long lastPollMs;

      CpuStats() {
         super();
         this.processor = this.systemInfo.getHardware().getProcessor();
         this.nrOfCpus = this.processor.getLogicalProcessorCount();
         this.previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
         this.currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
      }

      public double loadForCpu(int var1) {
         long var2 = System.currentTimeMillis();
         if (this.lastPollMs == 0L || this.lastPollMs + 501L < var2) {
            this.currentLoad = this.processor.getProcessorCpuLoadBetweenTicks(this.previousCpuLoadTick);
            this.previousCpuLoadTick = this.processor.getProcessorCpuLoadTicks();
            this.lastPollMs = var2;
         }

         return this.currentLoad[var1] * 100.0D;
      }
   }
}
