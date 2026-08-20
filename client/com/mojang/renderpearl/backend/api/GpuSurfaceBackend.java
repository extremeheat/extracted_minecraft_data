package com.mojang.renderpearl.backend.api;

import com.mojang.renderpearl.api.device.GpuSurface;
import com.mojang.renderpearl.api.device.SurfaceException;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import java.util.Collection;

public interface GpuSurfaceBackend extends UncheckedAutoCloseable {
   void configure(GpuSurface.Configuration config) throws SurfaceException;

   boolean isSuboptimal();

   void acquireNextTexture() throws SurfaceException;

   void blitFromTexture(CommandEncoderBackend commandEncoder, GpuTextureView textureView);

   void present();

   Collection<GpuSurface.PresentMode> supportedPresentModes();
}
