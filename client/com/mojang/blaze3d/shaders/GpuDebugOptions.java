package com.mojang.blaze3d.shaders;

public record GpuDebugOptions(int logLevel, boolean synchronousLogs, boolean useLabels) {
   public GpuDebugOptions {
      super();
   }
}
