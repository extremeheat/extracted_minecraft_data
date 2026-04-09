package com.mojang.blaze3d.systems;

public record HintsAndWorkarounds(boolean alwaysCreateFreshImmediateBuffer, boolean writeToBufferIsSlow, boolean anisotropyHasKnownIssues) {
   public HintsAndWorkarounds {
      super();
   }
}
