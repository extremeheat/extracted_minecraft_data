package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double jvm, double userJvm, double system) {
   public CpuLoadStat {
      super();
   }

   public static CpuLoadStat from(final RecordedEvent event) {
      return new CpuLoadStat((double)event.getFloat("jvmSystem"), (double)event.getFloat("jvmUser"), (double)event.getFloat("machineTotal"));
   }
}
