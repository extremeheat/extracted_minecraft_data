package com.mojang.renderpearl.api.device;

public record GpuDebugOptions(int logLevel, boolean synchronousLogs, boolean useLabels, boolean useValidationLayers, boolean shaderDebug, boolean strictValidation) {
   public GpuDebugOptions {
      super();
   }
}
