package com.mojang.renderpearl.backend.opengl;

import org.lwjgl.sdl.SDLVideo;

class GlGlobalState {
   private static long currentWindow = 0L;
   private static long currentContext = 0L;

   GlGlobalState() {
      super();
   }

   static boolean isWindowCurrent(final long window) {
      return window == currentWindow;
   }

   static boolean isContextCurrent(final long context) {
      return context == currentContext;
   }

   static void makeCurrent(final long window, final long context) {
      if (currentWindow != window || currentContext != context) {
         currentWindow = window;
         currentContext = context;
         SDLVideo.SDL_GL_MakeCurrent(window, context);
      }
   }
}
