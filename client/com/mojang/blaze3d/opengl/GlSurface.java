package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuSurface;
import com.mojang.blaze3d.systems.GpuSurfaceBackend;
import com.mojang.blaze3d.systems.SurfaceException;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import org.lwjgl.glfw.GLFW;

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
      GLFW.glfwSwapInterval(config.presentMode() == GpuSurface.PresentMode.FIFO ? 1 : 0);
      this.swapchainWidth = config.width();
      this.swapchainHeight = config.height();
   }

   public boolean isSuboptimal() {
      return false;
   }

   public void acquireNextTexture() {
   }

   public void blitFromTexture(final CommandEncoderBackend commandEncoder, final GpuTextureView textureView) {
      ((GlCommandEncoder)commandEncoder).presentTexture(textureView, this.swapchainWidth, this.swapchainHeight);
   }

   public void present() {
      GLFW.glfwSwapBuffers(this.windowHandle);
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
