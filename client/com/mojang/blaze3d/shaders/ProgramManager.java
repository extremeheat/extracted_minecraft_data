package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.platform.GLX;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProgramManager {
   private static final Logger LOGGER = LogManager.getLogger();
   private static ProgramManager instance;

   public static void createInstance() {
      instance = new ProgramManager();
   }

   public static ProgramManager getInstance() {
      return instance;
   }

   private ProgramManager() {
      super();
   }

   public void releaseProgram(Effect var1) {
      var1.getFragmentProgram().close();
      var1.getVertexProgram().close();
      GLX.glDeleteProgram(var1.getId());
   }

   public int createProgram() throws IOException {
      int var1 = GLX.glCreateProgram();
      if (var1 <= 0) {
         throw new IOException("Could not create shader program (returned program ID " + var1 + ")");
      } else {
         return var1;
      }
   }

   public void linkProgram(Effect var1) throws IOException {
      var1.getFragmentProgram().attachToEffect(var1);
      var1.getVertexProgram().attachToEffect(var1);
      GLX.glLinkProgram(var1.getId());
      int var2 = GLX.glGetProgrami(var1.getId(), GLX.GL_LINK_STATUS);
      if (var2 == 0) {
         LOGGER.warn("Error encountered when linking program containing VS {} and FS {}. Log output:", var1.getVertexProgram().getName(), var1.getFragmentProgram().getName());
         LOGGER.warn(GLX.glGetProgramInfoLog(var1.getId(), 32768));
      }

   }
}
