package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

public class GlTextureView extends GpuTextureView {
   private boolean closed;

   protected GlTextureView(GlTexture var1, int var2, int var3) {
      super(var1, var2, var3);
      var1.addViews();
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.texture().removeViews();
      }

   }

   public GlTexture texture() {
      return (GlTexture)super.texture();
   }

   // $FF: synthetic method
   public GpuTexture texture() {
      return this.texture();
   }
}
