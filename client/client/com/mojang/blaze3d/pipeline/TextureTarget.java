package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.systems.RenderSystem;

public class TextureTarget extends RenderTarget {
   public TextureTarget(int var1, int var2, boolean var3, boolean var4) {
      super(var3);
      RenderSystem.assertOnRenderThreadOrInit();
      this.resize(var1, var2, var4);
   }
}
