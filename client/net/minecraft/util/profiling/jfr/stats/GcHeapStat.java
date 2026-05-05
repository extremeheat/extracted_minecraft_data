package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant timestamp, long heapUsed, Timing timing) {
   public GcHeapStat {
      super();
   }

   public static GcHeapStat from(final RecordedEvent event) {
      return new GcHeapStat(event.getStartTime(), event.getLong("heapUsed"), event.getString("when").equalsIgnoreCase("before gc") ? GcHeapStat.Timing.BEFORE_GC : GcHeapStat.Timing.AFTER_GC);
   }

   public static Summary summary(final Duration recordingDuration, final List<GcHeapStat> heapStats, final Duration gcTotalDuration, final int totalGCs) {
      return new Summary(recordingDuration, gcTotalDuration, totalGCs, calculateAllocationRatePerSecond(heapStats));
   }

   private static double calculateAllocationRatePerSecond(final List<GcHeapStat> heapStats) {
      long totalAllocations = 0L;
      Map<Timing, List<GcHeapStat>> byTiming = (Map)heapStats.stream().collect(Collectors.groupingBy((it) -> it.timing));
      List<GcHeapStat> beforeGcs = (List)byTiming.get(GcHeapStat.Timing.BEFORE_GC);
      List<GcHeapStat> afterGcs = (List)byTiming.get(GcHeapStat.Timing.AFTER_GC);

      for(int i = 1; i < beforeGcs.size(); ++i) {
         GcHeapStat beforeGC = (GcHeapStat)beforeGcs.get(i);
         GcHeapStat previousGC = (GcHeapStat)afterGcs.get(i - 1);
         totalAllocations += beforeGC.heapUsed - previousGC.heapUsed;
      }

      Duration totalDuration = Duration.between(((GcHeapStat)heapStats.get(1)).timestamp, ((GcHeapStat)heapStats.get(heapStats.size() - 1)).timestamp);
      return (double)totalAllocations / (double)totalDuration.getSeconds();
   }

   public static record Summary(Duration duration, Duration gcTotalDuration, int totalGCs, double allocationRateBytesPerSecond) {
      public Summary {
         super();
      }

      public float gcOverHead() {
         return (float)this.gcTotalDuration.toMillis() / (float)this.duration.toMillis();
      }
   }

   public static enum Timing {
      BEFORE_GC,
      AFTER_GC;

      private Timing() {
      }

      // $FF: synthetic method
      private static Timing[] $values() {
         return new Timing[]{BEFORE_GC, AFTER_GC};
      }
   }
}
