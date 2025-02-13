package com.mojang.blaze3d.resource;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import net.minecraft.util.ARGB;

public record RenderTargetDescriptor(int width, int height, boolean useDepth, int clearColor) implements ResourceDescriptor<RenderTarget> {
   public RenderTargetDescriptor(int var1, int var2, boolean var3, int var4) {
      super();
      this.width = var1;
      this.height = var2;
      this.useDepth = var3;
      this.clearColor = var4;
   }

   public RenderTarget allocate() {
      return new TextureTarget((String)null, this.width, this.height, this.useDepth);
   }

   public void prepare(RenderTarget var1) {
      var1.clear(ARGB.redFloat(this.clearColor), ARGB.greenFloat(this.clearColor), ARGB.blueFloat(this.clearColor), ARGB.alphaFloat(this.clearColor));
   }

   public void free(RenderTarget var1) {
      var1.destroyBuffers();
   }

   public boolean canUsePhysicalResource(ResourceDescriptor<?> var1) {
      if (!(var1 instanceof RenderTargetDescriptor var2)) {
         return false;
      } else {
         return this.width == var2.width && this.height == var2.height && this.useDepth == var2.useDepth;
      }
   }

   // $FF: synthetic method
   public void free(final Object var1) {
      this.free((RenderTarget)var1);
   }

   // $FF: synthetic method
   public void prepare(final Object var1) {
      this.prepare((RenderTarget)var1);
   }

   // $FF: synthetic method
   public Object allocate() {
      return this.allocate();
   }
}
