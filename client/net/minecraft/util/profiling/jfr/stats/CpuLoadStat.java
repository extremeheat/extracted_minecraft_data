package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double jvm, double userJvm, double system) {
   public CpuLoadStat(double jvm, double userJvm, double system) {
      super();
      this.jvm = jvm;
      this.userJvm = userJvm;
      this.system = system;
   }

   public static CpuLoadStat from(RecordedEvent var0) {
      return new CpuLoadStat((double)var0.getFloat("jvmSystem"), (double)var0.getFloat("jvmUser"), (double)var0.getFloat("machineTotal"));
   }
}
