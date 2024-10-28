package com.mojang.blaze3d.resource;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;

public record RenderTargetDescriptor(int width, int height, boolean useDepth) implements ResourceDescriptor<RenderTarget> {
   public RenderTargetDescriptor(int var1, int var2, boolean var3) {
      super();
      this.width = var1;
      this.height = var2;
      this.useDepth = var3;
   }

   public RenderTarget allocate() {
      return new TextureTarget(this.width, this.height, this.useDepth);
   }

   public void free(RenderTarget var1) {
      var1.destroyBuffers();
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public boolean useDepth() {
      return this.useDepth;
   }

   // $FF: synthetic method
   public Object allocate() {
      return this.allocate();
   }
}
