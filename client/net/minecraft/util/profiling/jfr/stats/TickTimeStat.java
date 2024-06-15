package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record TickTimeStat(Instant timestamp, Duration currentAverage) {
   public TickTimeStat(Instant timestamp, Duration currentAverage) {
      super();
      this.timestamp = timestamp;
      this.currentAverage = currentAverage;
   }

   public static TickTimeStat from(RecordedEvent var0) {
      return new TickTimeStat(var0.getStartTime(), var0.getDuration("averageTickDuration"));
   }
}
