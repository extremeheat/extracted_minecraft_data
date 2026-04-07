package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuSurface;
import com.mojang.blaze3d.systems.GpuSurfaceBackend;
import com.mojang.blaze3d.systems.SurfaceException;
import com.mojang.blaze3d.textures.GpuTextureView;
import org.lwjgl.glfw.GLFW;

public class GlSurface implements GpuSurfaceBackend {
   private final long windowHandle;

   public GlSurface(final long windowHandle) {
      super();
      this.windowHandle = windowHandle;
   }

   public void configure(final GpuSurface.Configuration config) throws SurfaceException {
      GLFW.glfwSwapInterval(config.vsync() ? 1 : 0);
   }

   public boolean isSuboptimal() {
      return false;
   }

   public void acquireNextTexture() {
   }

   public void blitFromTexture(final CommandEncoderBackend commandEncoder, final GpuTextureView textureView) {
      ((GlCommandEncoder)commandEncoder).presentTexture(textureView);
   }

   public void present() {
      GLFW.glfwSwapBuffers(this.windowHandle);
   }

   public void close() {
   }
}
