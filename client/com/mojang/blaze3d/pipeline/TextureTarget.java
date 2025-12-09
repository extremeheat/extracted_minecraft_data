package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;
import org.jspecify.annotations.Nullable;

public class TextureTarget extends RenderTarget {
   public TextureTarget(@Nullable String var1, int var2, int var3, boolean var4) {
      super(var1, var4);
      RenderSystem.assertOnRenderThread();
      this.resize(var2, var3);
   }
}
