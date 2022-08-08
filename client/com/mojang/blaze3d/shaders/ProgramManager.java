package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import org.slf4j.Logger;

public class ProgramManager {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ProgramManager() {
      super();
   }

   public static void glUseProgram(int var0) {
      RenderSystem.assertOnRenderThread();
      GlStateManager._glUseProgram(var0);
   }

   public static void releaseProgram(Shader var0) {
      RenderSystem.assertOnRenderThread();
      var0.getFragmentProgram().close();
      var0.getVertexProgram().close();
      GlStateManager.glDeleteProgram(var0.getId());
   }

   public static int createProgram() throws IOException {
      RenderSystem.assertOnRenderThread();
      int var0 = GlStateManager.glCreateProgram();
      if (var0 <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + var0 + ")");
      } else {
         return var0;
      }
   }

   public static void linkShader(Shader var0) {
      RenderSystem.assertOnRenderThread();
      var0.attachToProgram();
      GlStateManager.glLinkProgram(var0.getId());
      int var1 = GlStateManager.glGetProgrami(var0.getId(), 35714);
      if (var1 == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", var0.getVertexProgram().getName(), var0.getFragmentProgram().getName());
         LOGGER.warn(GlStateManager.glGetProgramInfoLog(var0.getId(), 32768));
      }

   }
}
