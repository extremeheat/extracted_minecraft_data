package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;

public record GcHeapStat(Instant a, long b, Timing c) {
   private final Instant timestamp;
   private final long heapUsed;
   private final Timing timing;

   public GcHeapStat(Instant var1, long var2, Timing var4) {
      super();
      this.timestamp = var1;
      this.heapUsed = var2;
      this.timing = var4;
   }

   public static GcHeapStat from(RecordedEvent var0) {
      return new GcHeapStat(var0.getStartTime(), var0.getLong("heapUsed"), var0.getString("when").equalsIgnoreCase("before gc") ? GcHeapStat.Timing.BEFORE_GC : GcHeapStat.Timing.AFTER_GC);
   }

   public static Summary summary(Duration var0, List<GcHeapStat> var1, Duration var2, int var3) {
      return new Summary(var0, var2, var3, calculateAllocationRatePerSecond(var1));
   }

   private static double calculateAllocationRatePerSecond(List<GcHeapStat> var0) {
      long var1 = 0L;
      Map var3 = (Map)var0.stream().collect(Collectors.groupingBy((var0x) -> {
         return var0x.timing;
      }));
      List var4 = (List)var3.get(GcHeapStat.Timing.BEFORE_GC);
      List var5 = (List)var3.get(GcHeapStat.Timing.AFTER_GC);

      for(int var6 = 1; var6 < var4.size(); ++var6) {
         GcHeapStat var7 = (GcHeapStat)var4.get(var6);
         GcHeapStat var8 = (GcHeapStat)var5.get(var6 - 1);
         var1 += var7.heapUsed - var8.heapUsed;
      }

      Duration var9 = Duration.between(((GcHeapStat)var0.get(1)).timestamp, ((GcHeapStat)var0.get(var0.size() - 1)).timestamp);
      return (double)var1 / (double)var9.getSeconds();
   }

   public Instant timestamp() {
      return this.timestamp;
   }

   public long heapUsed() {
      return this.heapUsed;
   }

   public Timing timing() {
      return this.timing;
   }

   static enum Timing {
      BEFORE_GC,
      AFTER_GC;

      private Timing() {
      }

      // $FF: synthetic method
      private static Timing[] $values() {
         return new Timing[]{BEFORE_GC, AFTER_GC};
      }
   }

   public static record Summary(Duration a, Duration b, int c, double d) {
      private final Duration duration;
      private final Duration gcTotalDuration;
      private final int totalGCs;
      private final double allocationRateBytesPerSecond;

      public Summary(Duration var1, Duration var2, int var3, double var4) {
         super();
         this.duration = var1;
         this.gcTotalDuration = var2;
         this.totalGCs = var3;
         this.allocationRateBytesPerSecond = var4;
      }

      public float gcOverHead() {
         return (float)this.gcTotalDuration.toMillis() / (float)this.duration.toMillis();
      }

      public Duration duration() {
         return this.duration;
      }

      public Duration gcTotalDuration() {
         return this.gcTotalDuration;
      }

      public int totalGCs() {
         return this.totalGCs;
      }

      public double allocationRateBytesPerSecond() {
         return this.allocationRateBytesPerSecond;
      }
   }
}
