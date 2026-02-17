package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.GLFWErrorCapture;
import com.mojang.blaze3d.GLFWErrorScope;
import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.BackendCreationException;
import com.mojang.blaze3d.systems.GpuBackend;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.WindowAndDevice;
import com.mojang.logging.LogUtils;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public class GlBackend implements GpuBackend {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int VERSION_MAJOR = 3;
   private static final int VERSION_MINOR = 3;

   public GlBackend() {
      super();
   }

   public String getName() {
      return "OpenGL";
   }

   public WindowAndDevice createDeviceWithWindow(final int width, final int height, final String title, final long monitor, final ShaderSource defaultShaderSource, final GpuDebugOptions debugOptions) throws BackendCreationException {
      GLFWErrorCapture glfwErrors = new GLFWErrorCapture();

      WindowAndDevice result;
      try (GLFWErrorScope var10 = new GLFWErrorScope(glfwErrors)) {
         GLFW.glfwWindowHint(139265, 196609);
         GLFW.glfwWindowHint(139275, 221185);
         GLFW.glfwWindowHint(139266, 3);
         GLFW.glfwWindowHint(139267, 3);
         GLFW.glfwWindowHint(139272, 204801);
         GLFW.glfwWindowHint(139270, 1);
         long window = GLFW.glfwCreateWindow(width, height, title, monitor, 0L);
         if (window == 0L) {
            GLFWErrorCapture.Error error = glfwErrors.firstError();
            if (error != null) {
               if (error.error() == 65542) {
                  throw new BackendCreationException("Driver does not support OpenGL");
               }

               if (error.error() == 65543) {
                  throw new BackendCreationException("Driver does not support OpenGL 3.3");
               }

               throw new BackendCreationException(error.toString());
            }

            throw new BackendCreationException("Failed to create window with OpenGL context");
         }

         result = new WindowAndDevice(window, new GpuDevice(new GlDevice(window, defaultShaderSource, debugOptions)));
      }

      for(GLFWErrorCapture.Error error : glfwErrors) {
         LOGGER.error("GLFW error collected during GL backend initialization: {}", error);
      }

      return result;
   }
}
