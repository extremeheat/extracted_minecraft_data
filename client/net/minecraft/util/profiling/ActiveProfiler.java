package net.minecraft.util.profiling;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.util.profiling.metrics.MetricCategory;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class ActiveProfiler implements ProfileCollector {
   private static final long WARNING_TIME_NANOS = Duration.ofMillis(100L).toNanos();
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<String> paths = Lists.newArrayList();
   private final LongList startTimes = new LongArrayList();
   private final Map<String, ActiveProfiler.PathEntry> entries = Maps.newHashMap();
   private final IntSupplier getTickTime;
   private final LongSupplier getRealTime;
   private final long startTimeNano;
   private final int startTimeTicks;
   private String path = "";
   private boolean started;
   @Nullable
   private ActiveProfiler.PathEntry currentEntry;
   private final boolean warn;
   private final Set<Pair<String, MetricCategory>> chartedPaths = new ObjectArraySet();

   public ActiveProfiler(LongSupplier var1, IntSupplier var2, boolean var3) {
      super();
      this.startTimeNano = var1.getAsLong();
      this.getRealTime = var1;
      this.startTimeTicks = var2.getAsInt();
      this.getTickTime = var2;
      this.warn = var3;
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
            LOGGER.error("Profiler tick ended before path was fully popped (remainder: '{}'). Mismatched push/pop?", new Supplier[]{() -> {
               return ProfileResults.demanglePath(this.path);
            }});
         }

      }
   }

   public void push(String var1) {
      if (!this.started) {
         LOGGER.error("Cannot push '{}' to profiler if profiler tick hasn't started - missing startTick()?", var1);
      } else {
         if (!this.path.isEmpty()) {
            this.path = this.path + "\u001e";
         }

         this.path = this.path + var1;
         this.paths.add(this.path);
         this.startTimes.add(Util.getNanos());
         this.currentEntry = null;
      }
   }

   public void push(java.util.function.Supplier<String> var1) {
      this.push((String)var1.get());
   }

   public void markForCharting(MetricCategory var1) {
      this.chartedPaths.add(Pair.of(this.path, var1));
   }

   public void pop() {
      if (!this.started) {
         LOGGER.error("Cannot pop from profiler if profiler tick hasn't started - missing startTick()?");
      } else if (this.startTimes.isEmpty()) {
         LOGGER.error("Tried to pop one too many times! Mismatched push() and pop()?");
      } else {
         long var1 = Util.getNanos();
         long var3 = this.startTimes.removeLong(this.startTimes.size() - 1);
         this.paths.remove(this.paths.size() - 1);
         long var5 = var1 - var3;
         ActiveProfiler.PathEntry var7 = this.getCurrentEntry();
         var7.accumulatedDuration += var5;
         ++var7.count;
         var7.maxDuration = Math.max(var7.maxDuration, var5);
         var7.minDuration = Math.min(var7.minDuration, var5);
         if (this.warn && var5 > WARNING_TIME_NANOS) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", new Supplier[]{() -> {
               return ProfileResults.demanglePath(this.path);
            }, () -> {
               return (double)var5 / 1000000.0D;
            }});
         }

         this.path = this.paths.isEmpty() ? "" : (String)this.paths.get(this.paths.size() - 1);
         this.currentEntry = null;
      }
   }

   public void popPush(String var1) {
      this.pop();
      this.push(var1);
   }

   public void popPush(java.util.function.Supplier<String> var1) {
      this.pop();
      this.push(var1);
   }

   private ActiveProfiler.PathEntry getCurrentEntry() {
      if (this.currentEntry == null) {
         this.currentEntry = (ActiveProfiler.PathEntry)this.entries.computeIfAbsent(this.path, (var0) -> {
            return new ActiveProfiler.PathEntry();
         });
      }

      return this.currentEntry;
   }

   public void incrementCounter(String var1, int var2) {
      this.getCurrentEntry().counters.addTo(var1, (long)var2);
   }

   public void incrementCounter(java.util.function.Supplier<String> var1, int var2) {
      this.getCurrentEntry().counters.addTo((String)var1.get(), (long)var2);
   }

   public ProfileResults getResults() {
      return new FilledProfileResults(this.entries, this.startTimeNano, this.startTimeTicks, this.getRealTime.getAsLong(), this.getTickTime.getAsInt());
   }

   @Nullable
   public ActiveProfiler.PathEntry getEntry(String var1) {
      return (ActiveProfiler.PathEntry)this.entries.get(var1);
   }

   public Set<Pair<String, MetricCategory>> getChartedPaths() {
      return this.chartedPaths;
   }

   public static class PathEntry implements ProfilerPathEntry {
      long maxDuration = -9223372036854775808L;
      long minDuration = 9223372036854775807L;
      long accumulatedDuration;
      long count;
      final Object2LongOpenHashMap<String> counters = new Object2LongOpenHashMap();

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
