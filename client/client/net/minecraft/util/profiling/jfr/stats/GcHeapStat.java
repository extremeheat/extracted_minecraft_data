package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant timestamp, long heapUsed, GcHeapStat.Timing timing) {
   public GcHeapStat(Instant timestamp, long heapUsed, GcHeapStat.Timing timing) {
      super();
      this.timestamp = timestamp;
      this.heapUsed = heapUsed;
      this.timing = timing;
   }

   public static GcHeapStat from(RecordedEvent var0) {
      return new GcHeapStat(
         var0.getStartTime(),
         var0.getLong("heapUsed"),
         var0.getString("when").equalsIgnoreCase("before gc") ? GcHeapStat.Timing.BEFORE_GC : GcHeapStat.Timing.AFTER_GC
      );
   }

   public static GcHeapStat.Summary summary(Duration var0, List<GcHeapStat> var1, Duration var2, int var3) {
      return new GcHeapStat.Summary(var0, var2, var3, calculateAllocationRatePerSecond(var1));
   }

   private static double calculateAllocationRatePerSecond(List<GcHeapStat> var0) {
      long var1 = 0L;
      Map var3 = var0.stream().collect(Collectors.groupingBy(var0x -> var0x.timing));
      List var4 = (List)var3.get(GcHeapStat.Timing.BEFORE_GC);
      List var5 = (List)var3.get(GcHeapStat.Timing.AFTER_GC);

      for (int var6 = 1; var6 < var4.size(); var6++) {
         GcHeapStat var7 = (GcHeapStat)var4.get(var6);
         GcHeapStat var8 = (GcHeapStat)var5.get(var6 - 1);
         var1 += var7.heapUsed - var8.heapUsed;
      }

      Duration var9 = Duration.between(((GcHeapStat)var0.get(1)).timestamp, ((GcHeapStat)var0.get(var0.size() - 1)).timestamp);
      return (double)var1 / (double)var9.getSeconds();
   }

   public static record Summary(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {
      public Summary(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {
         super();
         this.duration = duration;
         this.gcTotalDuration = gcTotalDuration;
         this.totalGCs = totalGCs;
         this.allocationRateBytesPerSecond = allocationRateBytesPerSecond;
      }

      public float gcOverHead() {
         return (float)this.gcTotalDuration.toMillis() / (float)this.duration.toMillis();
      }
   }

   static enum Timing {
      BEFORE_GC,
      AFTER_GC;

      private Timing() {
      }
   }
}