package net.minecraft.client.renderer;

import org.lwjgl.opengl.GLContext;

public class OpenGlCapsChecker {
   public static boolean func_74371_a() {
      return GLContext.getCapabilities().GL_ARB_occlusion_query;
   }
}
