package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record FpsStat(int fps) {
   public FpsStat(int var1) {
      super();
      this.fps = var1;
   }

   public static FpsStat from(RecordedEvent var0, String var1) {
      return new FpsStat(var0.getInt(var1));
   }
}
