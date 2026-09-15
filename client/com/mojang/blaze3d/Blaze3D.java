package com.mojang.blaze3d;

import com.mojang.logging.LogUtils;
import java.net.URI;
import java.nio.file.Path;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.Util;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLMisc;
import org.lwjgl.sdl.SDLTimer;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

public class Blaze3D {
   private static final Logger LOGGER = LogUtils.getLogger();

   public static void youJustLostTheGame() {
      MemoryUtil.memSet(0L, 0, 1L);
   }

   public static double getTime() {
      return (double)SDLTimer.SDL_GetTicksNS() / (double)TimeUtil.NANOSECONDS_PER_SECOND;
   }

   private Blaze3D() {
      super();
   }

   public static void openUri(final URI uri) {
      Util.nonCriticalIoPool().execute(() -> {
         if (!SDLMisc.SDL_OpenURL(uri.toString())) {
            LOGGER.warn("Failed to open uri {}: {}", uri, SDLError.SDL_GetError());
         }

      });
   }

   public static void openPath(final Path path) {
      openUri(path.normalize().toUri());
   }
}
