package com.mojang.blaze3d;

import net.minecraft.util.TimeUtil;
import org.lwjgl.sdl.SDLTimer;
import org.lwjgl.system.MemoryUtil;

public class Blaze3D {
   public static void youJustLostTheGame() {
      MemoryUtil.memSet(0L, 0, 1L);
   }

   public static double getTime() {
      return (double)SDLTimer.SDL_GetTicksNS() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
   }

   private Blaze3D() {
      super();
   }
}
