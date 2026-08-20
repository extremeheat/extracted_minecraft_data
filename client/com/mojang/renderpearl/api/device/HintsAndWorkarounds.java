package com.mojang.renderpearl.api.device;

public record HintsAndWorkarounds(boolean writeToBufferIsSlow, boolean anisotropyHasKnownIssues, boolean isExplicitDepthRequired) {
   public HintsAndWorkarounds {
      super();
   }
}
