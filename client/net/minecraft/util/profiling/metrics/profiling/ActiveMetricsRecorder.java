package net.minecraft.util.profiling.metrics.profiling;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiling.ActiveProfiler;
import net.minecraft.util.profiling.ContinuousProfiler;
import net.minecraft.util.profiling.EmptyProfileResults;
import net.minecraft.util.profiling.InactiveProfiler;
import net.minecraft.util.profiling.ProfileCollector;
import net.minecraft.util.profiling.ProfileResults;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricSampler;
import net.minecraft.util.profiling.metrics.MetricsSamplerProvider;
import net.minecraft.util.profiling.metrics.storage.MetricsPersister;
import net.minecraft.util.profiling.metrics.storage.RecordedDeviation;

public class ActiveMetricsRecorder implements MetricsRecorder {
   public static final int PROFILING_MAX_DURATION_SECONDS = 10;
   @Nullable
   private static Consumer<Path> globalOnReportFinished = null;
   private final Map<MetricSampler, List<RecordedDeviation>> deviationsBySampler = new Object2ObjectOpenHashMap();
   private final ContinuousProfiler taskProfiler;
   private final Executor ioExecutor;
   private final MetricsPersister metricsPersister;
   private final Consumer<ProfileResults> onProfilingEnd;
   private final Consumer<Path> onReportFinished;
   private final MetricsSamplerProvider metricsSamplerProvider;
   private final LongSupplier wallTimeSource;
   private final long deadlineNano;
   private int currentTick;
   private ProfileCollector singleTickProfiler;
   private volatile boolean killSwitch;
   private Set<MetricSampler> thisTickSamplers = ImmutableSet.of();

   private ActiveMetricsRecorder(MetricsSamplerProvider var1, LongSupplier var2, Executor var3, MetricsPersister var4, Consumer<ProfileResults> var5, Consumer<Path> var6) {
      super();
      this.metricsSamplerProvider = var1;
      this.wallTimeSource = var2;
      this.taskProfiler = new ContinuousProfiler(var2, () -> {
         return this.currentTick;
      });
      this.ioExecutor = var3;
      this.metricsPersister = var4;
      this.onProfilingEnd = var5;
      this.onReportFinished = globalOnReportFinished == null ? var6 : var6.andThen(globalOnReportFinished);
      this.deadlineNano = var2.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
      this.singleTickProfiler = new ActiveProfiler(this.wallTimeSource, () -> {
         return this.currentTick;
      }, false);
      this.taskProfiler.enable();
   }

   public static ActiveMetricsRecorder createStarted(MetricsSamplerProvider var0, LongSupplier var1, Executor var2, MetricsPersister var3, Consumer<ProfileResults> var4, Consumer<Path> var5) {
      return new ActiveMetricsRecorder(var0, var1, var2, var3, var4, var5);
   }

   public synchronized void end() {
      if (this.isRecording()) {
         this.killSwitch = true;
      }
   }

   public synchronized void cancel() {
      if (this.isRecording()) {
         this.singleTickProfiler = InactiveProfiler.INSTANCE;
         this.onProfilingEnd.accept(EmptyProfileResults.EMPTY);
         this.cleanup(this.thisTickSamplers);
      }
   }

   public void startTick() {
      this.verifyStarted();
      this.thisTickSamplers = this.metricsSamplerProvider.samplers(() -> {
         return this.singleTickProfiler;
      });
      Iterator var1 = this.thisTickSamplers.iterator();

      while(var1.hasNext()) {
         MetricSampler var2 = (MetricSampler)var1.next();
         var2.onStartTick();
      }

      ++this.currentTick;
   }

   public void endTick() {
      this.verifyStarted();
      if (this.currentTick != 0) {
         Iterator var1 = this.thisTickSamplers.iterator();

         while(var1.hasNext()) {
            MetricSampler var2 = (MetricSampler)var1.next();
            var2.onEndTick(this.currentTick);
            if (var2.triggersThreshold()) {
               RecordedDeviation var3 = new RecordedDeviation(Instant.now(), this.currentTick, this.singleTickProfiler.getResults());
               ((List)this.deviationsBySampler.computeIfAbsent(var2, (var0) -> {
                  return Lists.newArrayList();
               })).add(var3);
            }
         }

         if (!this.killSwitch && this.wallTimeSource.getAsLong() <= this.deadlineNano) {
            this.singleTickProfiler = new ActiveProfiler(this.wallTimeSource, () -> {
               return this.currentTick;
            }, false);
         } else {
            this.killSwitch = false;
            ProfileResults var4 = this.taskProfiler.getResults();
            this.singleTickProfiler = InactiveProfiler.INSTANCE;
            this.onProfilingEnd.accept(var4);
            this.scheduleSaveResults(var4);
         }
      }
   }

   public boolean isRecording() {
      return this.taskProfiler.isEnabled();
   }

   public ProfilerFiller getProfiler() {
      return ProfilerFiller.tee(this.taskProfiler.getFiller(), this.singleTickProfiler);
   }

   private void verifyStarted() {
      if (!this.isRecording()) {
         throw new IllegalStateException("Not started!");
      }
   }

   private void scheduleSaveResults(ProfileResults var1) {
      HashSet var2 = new HashSet(this.thisTickSamplers);
      this.ioExecutor.execute(() -> {
         Path var3 = this.metricsPersister.saveReports(var2, this.deviationsBySampler, var1);
         this.cleanup(var2);
         this.onReportFinished.accept(var3);
      });
   }

   private void cleanup(Collection<MetricSampler> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         MetricSampler var3 = (MetricSampler)var2.next();
         var3.onFinished();
      }

      this.deviationsBySampler.clear();
      this.taskProfiler.disable();
   }

   public static void registerGlobalCompletionCallback(Consumer<Path> var0) {
      globalOnReportFinished = var0;
   }
}
