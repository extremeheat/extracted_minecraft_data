package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jspecify.annotations.Nullable;

public class TextureTarget extends RenderTarget {
   public TextureTarget(final @Nullable String label, final int width, final int height, final boolean useDepth) {
      super(label, useDepth);
      RenderSystem.assertOnRenderThread();
      this.resize(width, height);
   }
}
