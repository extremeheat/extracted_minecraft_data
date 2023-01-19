package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record CpuLoadStat(double a, double b, double c) {
   private final double jvm;
   private final double userJvm;
   private final double system;

   public CpuLoadStat(double var1, double var3, double var5) {
      super();
      this.jvm = var1;
      this.userJvm = var3;
      this.system = var5;
   }

   public static CpuLoadStat from(RecordedEvent var0) {
      return new CpuLoadStat((double)var0.getFloat("jvmSystem"), (double)var0.getFloat("jvmUser"), (double)var0.getFloat("machineTotal"));
   }
}
