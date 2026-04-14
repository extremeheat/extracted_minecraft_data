package com.mojang.blaze3d.systems;

public record HintsAndWorkarounds(boolean writeToBufferIsSlow, boolean anisotropyHasKnownIssues) {
   public HintsAndWorkarounds {
      super();
   }
}
