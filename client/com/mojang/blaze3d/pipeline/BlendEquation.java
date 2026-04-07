package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.BlendOp;

public record BlendEquation(BlendFactor sourceFactor, BlendFactor destFactor, BlendOp op) {
   public BlendEquation {
      super();
   }
}
