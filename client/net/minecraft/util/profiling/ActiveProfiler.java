package net.minecraft.util.profiling;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongMaps;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ActiveProfiler implements ProfileCollector {
   private static final long WARNING_TIME_NANOS = Duration.ofMillis(100L).toNanos();
   private static final Logger LOGGER = LogUtils.getLogger();
   private final List<String> paths = Lists.newArrayList();
   private final LongList startTimes = new LongArrayList();
   private final Map<String, PathEntry> entries = Maps.newHashMap();
   private final IntSupplier getTickTime;
   private final LongSupplier getRealTime;
   private final long startTimeNano;
   private final int startTimeTicks;
   private String path = "";
   private boolean started;
   private @Nullable PathEntry currentEntry;
   private final BooleanSupplier suppressWarnings;
   private final Set<Pair<String, MetricCategory>> chartedPaths = new ObjectArraySet();

   public ActiveProfiler(final LongSupplier getRealTime, final IntSupplier getTickTime, final BooleanSupplier suppressWarnings) {
      super();
      this.startTimeNano = getRealTime.getAsLong();
      this.getRealTime = getRealTime;
      this.startTimeTicks = getTickTime.getAsInt();
      this.getTickTime = getTickTime;
      this.suppressWarnings = suppressWarnings;
   }

   public void startTick() {
      if (this.started) {
         LOGGER.error("Profiler tick already started - missing endTick()?");
      } else {
         this.started = true;
         this.path = "";
         this.paths.clear();
         this.push("root");
      }
   }

   public void endTick() {
      if (!this.started) {
         LOGGER.error("Profiler tick already ended - missing startTick()?");
      } else {
         this.pop();
         this.started = false;
         if (!this.path.isEmpty()) {
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", LogUtils.defer(() -> ProfileResults.demanglePath(this.path)));
         }

      }
   }

   public void push(final String name) {
      if (!this.started) {
         LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", name);
      } else {
         if (!this.path.isEmpty()) {
            this.path = this.path + "\u001e";
         }

         this.path = this.path + name;
         this.paths.add(this.path);
         this.startTimes.add(Util.getNanos());
         this.currentEntry = null;
      }
   }

   public void push(final Supplier<String> name) {
      this.push((String)name.get());
   }

   public void markForCharting(final MetricCategory category) {
      this.chartedPaths.add(Pair.of(this.path, category));
   }

   public void pop() {
      if (!this.started) {
         LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
      } else if (this.startTimes.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         long endTime = Util.getNanos();
         long startTime = this.startTimes.removeLong(this.startTimes.size() - 1);
         this.paths.removeLast();
         long time = endTime - startTime;
         PathEntry currentEntry = this.getCurrentEntry();
         currentEntry.accumulatedDuration += time;
         ++currentEntry.count;
         currentEntry.maxDuration = Math.max(currentEntry.maxDuration, time);
         currentEntry.minDuration = Math.min(currentEntry.minDuration, time);
         if (time > WARNING_TIME_NANOS && !this.suppressWarnings.getAsBoolean()) {
            LOGGER.warn("Something's taking too long! '{}' took approximately {} ms", LogUtils.defer(() -> ProfileResults.demanglePath(this.path)), LogUtils.defer(() -> (double)time / 1000000.0));
         }

         this.path = this.paths.isEmpty() ? "" : (String)this.paths.getLast();
         this.currentEntry = null;
      }
   }

   public void popPush(final String name) {
      this.pop();
      this.push(name);
   }

   public void popPush(final Supplier<String> name) {
      this.pop();
      this.push(name);
   }

   private PathEntry getCurrentEntry() {
      if (this.currentEntry == null) {
         this.currentEntry = (PathEntry)this.entries.computeIfAbsent(this.path, (key) -> new PathEntry());
      }

      return this.currentEntry;
   }

   public void incrementCounter(final String name, final int amount) {
      this.getCurrentEntry().counters.addTo(name, (long)amount);
   }

   public void incrementCounter(final Supplier<String> name, final int amount) {
      this.getCurrentEntry().counters.addTo((String)name.get(), (long)amount);
   }

   public ProfileResults getResults() {
      return new FilledProfileResults(this.entries, this.startTimeNano, this.startTimeTicks, this.getRealTime.getAsLong(), this.getTickTime.getAsInt());
   }

   public @Nullable PathEntry getEntry(final String path) {
      return (PathEntry)this.entries.get(path);
   }

   public Set<Pair<String, MetricCategory>> getChartedPaths() {
      return this.chartedPaths;
   }

   public static class PathEntry implements ProfilerPathEntry {
      private long maxDuration = -9223372036854775808L;
      private long minDuration = 9223372036854775807L;
      private long accumulatedDuration;
      private long count;
      private final Object2LongOpenHashMap<String> counters = new Object2LongOpenHashMap();

      public PathEntry() {
         super();
      }

      public long getDuration() {
         return this.accumulatedDuration;
      }

      public long getMaxDuration() {
         return this.maxDuration;
      }

      public long getCount() {
         return this.count;
      }

      public Object2LongMap<String> getCounters() {
         return Object2LongMaps.unmodifiable(this.counters);
      }
   }
}
