package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgramManager {
   private static final Logger LOGGER = LogManager.getLogger();

   public static void glUseProgram(int var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      GlStateManager._glUseProgram(var0);
   }

   public static void releaseProgram(Effect var0) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      var0.getFragmentProgram().close();
      var0.getVertexProgram().close();
      GlStateManager.glDeleteProgram(var0.getId());
   }

   public static int createProgram() throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      int var0 = GlStateManager.glCreateProgram();
      if (var0 <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + var0 + ")");
      } else {
         return var0;
      }
   }

   public static void linkProgram(Effect var0) throws IOException {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      var0.getFragmentProgram().attachToEffect(var0);
      var0.getVertexProgram().attachToEffect(var0);
      GlStateManager.glLinkProgram(var0.getId());
      int var1 = GlStateManager.glGetProgrami(var0.getId(), 35714);
      if (var1 == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", var0.getVertexProgram().getName(), var0.getFragmentProgram().getName());
         LOGGER.warn(GlStateManager.glGetProgramInfoLog(var0.getId(), 32768));
      }

   }
}
