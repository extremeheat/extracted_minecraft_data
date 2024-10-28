package net.minecraft.util.profiling.jfr.stats;

import com.google.common.base.MoreObjects;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;

public record ThreadAllocationStat(Instant timestamp, String threadName, long totalBytes) {
   private static final String UNKNOWN_THREAD = "unknown";

   public ThreadAllocationStat(Instant var1, String var2, long var3) {
      super();
      this.timestamp = var1;
      this.threadName = var2;
      this.totalBytes = var3;
   }

   public static ThreadAllocationStat from(RecordedEvent var0) {
      RecordedThread var1 = var0.getThread("thread");
      String var2 = var1 == null ? "unknown" : (String)MoreObjects.firstNonNull(var1.getJavaName(), "unknown");
      return new ThreadAllocationStat(var0.getStartTime(), var2, var0.getLong("allocated"));
   }

   public static Summary summary(List<ThreadAllocationStat> var0) {
      TreeMap var1 = new TreeMap();
      Map var2 = (Map)var0.stream().collect(Collectors.groupingBy((var0x) -> {
         return var0x.threadName;
      }));
      var2.forEach((var1x, var2x) -> {
         if (var2x.size() >= 2) {
            ThreadAllocationStat var3 = (ThreadAllocationStat)var2x.get(0);
            ThreadAllocationStat var4 = (ThreadAllocationStat)var2x.get(var2x.size() - 1);
            long var5 = Duration.between(var3.timestamp, var4.timestamp).getSeconds();
            long var7 = var4.totalBytes - var3.totalBytes;
            var1.put(var1x, (double)var7 / (double)var5);
         }
      });
      return new Summary(var1);
   }

   public Instant timestamp() {
      return this.timestamp;
   }

   public String threadName() {
      return this.threadName;
   }

   public long totalBytes() {
      return this.totalBytes;
   }

   public static record Summary(Map<String, Double> allocationsPerSecondByThread) {
      public Summary(Map<String, Double> var1) {
         super();
         this.allocationsPerSecondByThread = var1;
      }

      public Map<String, Double> allocationsPerSecondByThread() {
         return this.allocationsPerSecondByThread;
      }
   }
}
