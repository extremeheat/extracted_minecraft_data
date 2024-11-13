package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import java.time.Instant;
import jdk.jfr.consumer.RecordedEvent;

public record TickTimeStat(Instant timestamp, Duration currentAverage) {
   public TickTimeStat(Instant var1, Duration var2) {
      super();
      this.timestamp = var1;
      this.currentAverage = var2;
   }

   public static TickTimeStat from(RecordedEvent var0) {
      return new TickTimeStat(var0.getStartTime(), var0.getDuration("averageTickDuration"));
   }
}
