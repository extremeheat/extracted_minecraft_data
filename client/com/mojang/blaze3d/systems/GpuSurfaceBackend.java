package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.textures.GpuTextureView;

public interface GpuSurfaceBackend extends AutoCloseable {
   void configure(GpuSurface.Configuration config) throws SurfaceException;

   boolean isSuboptimal();

   void acquireNextTexture() throws SurfaceException;

   void blitFromTexture(CommandEncoderBackend commandEncoder, GpuTextureView textureView);

   void present();

   void close();
}
