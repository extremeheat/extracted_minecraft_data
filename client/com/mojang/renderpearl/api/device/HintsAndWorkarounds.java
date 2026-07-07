package com.mojang.renderpearl.api.device;

public record HintsAndWorkarounds(boolean writeToBufferIsSlow, boolean anisotropyHasKnownIssues) {
   public HintsAndWorkarounds {
      super();
   }
}
