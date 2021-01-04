package net.minecraft.util.profiling;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.time.Duration;
import java.util.List;
import java.util.function.IntSupplier;
import net.minecraft.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class ActiveProfiler implements ProfileCollector {
   private static final long WARNING_TIME_NANOS = Duration.ofMillis(100L).toNanos();
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<String> paths = Lists.newArrayList();
   private final LongList startTimes = new LongArrayList();
   private final Object2LongMap<String> times = new Object2LongOpenHashMap();
   private final Object2LongMap<String> counts = new Object2LongOpenHashMap();
   private final IntSupplier getTickTime;
   private final long startTimeNano;
   private final int startTimeTicks;
   private String path = "";
   private boolean started;

   public ActiveProfiler(long var1, IntSupplier var3) {
      super();
      this.startTimeNano = var1;
      this.startTimeTicks = var3.getAsInt();
      this.getTickTime = var3;
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
            this.path = this.path + '\u001e';
         }

         this.path = this.path + var1;
         this.paths.add(this.path);
         this.startTimes.add(Util.getNanos());
      }
   }

   public void push(java.util.function.Supplier<String> var1) {
      this.push((String)var1.get());
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
         this.times.put(this.path, this.times.getLong(this.path) + var5);
         this.counts.put(this.path, this.counts.getLong(this.path) + 1L);
         if (var5 > WARNING_TIME_NANOS) {
            LOGGER.warn("Something's taking too long! '{}' took aprox {} ms", new Supplier[]{() -> {
               return ProfileResults.demanglePath(this.path);
            }, () -> {
               return (double)var5 / 1000000.0D;
            }});
         }

         this.path = this.paths.isEmpty() ? "" : (String)this.paths.get(this.paths.size() - 1);
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

   public ProfileResults getResults() {
      return new FilledProfileResults(this.times, this.counts, this.startTimeNano, this.startTimeTicks, Util.getNanos(), this.getTickTime.getAsInt());
   }
}
