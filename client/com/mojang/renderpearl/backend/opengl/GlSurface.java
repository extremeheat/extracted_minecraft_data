package com.mojang.renderpearl.backend.opengl;

import com.mojang.renderpearl.api.device.GpuSurface;
import com.mojang.renderpearl.api.device.SurfaceException;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import com.mojang.renderpearl.backend.api.CommandEncoderBackend;
import com.mojang.renderpearl.backend.api.GpuSurfaceBackend;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import org.lwjgl.sdl.SDLVideo;

public class GlSurface implements GpuSurfaceBackend {
   private static final Set<GpuSurface.PresentMode> SUPPORTED_PRESENT_MODES;
   private final long windowHandle;
   private int swapchainWidth;
   private int swapchainHeight;

   public GlSurface(final long windowHandle) {
      super();
      this.windowHandle = windowHandle;
   }

   public void configure(final GpuSurface.Configuration config) throws SurfaceException {
      SDLVideo.SDL_GL_SetSwapInterval(config.presentMode() == GpuSurface.PresentMode.FIFO ? 1 : 0);
      this.swapchainWidth = config.width();
      this.swapchainHeight = config.height();
   }

   public boolean isSuboptimal() {
      return false;
   }

   public void acquireNextTexture() {
   }

   public void blitFromTexture(final CommandEncoderBackend commandEncoder, final GpuTextureView textureView) {
      ((GlCommandEncoder)commandEncoder).presentTexture(this.windowHandle, textureView, this.swapchainWidth, this.swapchainHeight);
   }

   public void present() {
      SDLVideo.SDL_GL_SwapWindow(this.windowHandle);
   }

   public void close() {
   }

   public Collection<GpuSurface.PresentMode> supportedPresentModes() {
      return SUPPORTED_PRESENT_MODES;
   }

   static {
      SUPPORTED_PRESENT_MODES = EnumSet.of(GpuSurface.PresentMode.FIFO, GpuSurface.PresentMode.IMMEDIATE);
   }
}
