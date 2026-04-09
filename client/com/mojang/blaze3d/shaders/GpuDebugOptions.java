package com.mojang.blaze3d.shaders;

public record GpuDebugOptions(int logLevel, boolean synchronousLogs, boolean useLabels, boolean useValidationLayers) {
   public GpuDebugOptions {
      super();
   }
}
