package com.mojang.blaze3d.platform;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.jspecify.annotations.Nullable;
import org.lwjgl.PointerBuffer;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.sdl.SDLStdinc;
import org.lwjgl.sdl.SDLVideo;
import org.lwjgl.sdl.SDL_DisplayMode;
import org.lwjgl.sdl.SDL_Rect;
import org.lwjgl.system.MemoryStack;
import org.slf4j.Logger;

public record Monitor(String name, int id, List<VideoMode> videoModes, VideoMode currentMode, int x, int y) {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final HexFormat HEX_FORMAT = HexFormat.of().withUpperCase();

   public Monitor {
      super();
   }

   public static @Nullable Monitor tryCreate(final int id) {
      String name = queryMonitorName(id);
      ImmutableList.Builder<VideoMode> videoModes = ImmutableList.builder();
      MemoryStack stack = MemoryStack.stackPush();

      Object var15;
      label176: {
         Object var17;
         label177: {
            Object var18;
            label178: {
               try {
                  PointerBuffer modes = SDLVideo.SDL_GetFullscreenDisplayModes(id);
                  if (modes == null) {
                     LOGGER.warn("Failed to query video modes of monitor {}: {}", name, SDLError.SDL_GetError());
                     var15 = null;
                     break label176;
                  }

                  try {
                     for(int i = 0; i < modes.limit(); ++i) {
                        VideoMode mode = new VideoMode(SDL_DisplayMode.create(modes.get(i)));
                        if (mode.getRedBits() >= 8 && mode.getGreenBits() >= 8 && mode.getBlueBits() >= 8) {
                           videoModes.add(mode);
                        }
                     }
                  } finally {
                     SDLStdinc.SDL_free(modes);
                  }

                  SDL_Rect var14 = SDL_Rect.malloc(stack);
                  if (!SDLVideo.SDL_GetDisplayBounds(id, var14)) {
                     LOGGER.warn("Failed to query monitor bounds of {}: {}", name, SDLError.SDL_GetError());
                     var17 = null;
                     break label177;
                  }

                  SDL_DisplayMode currentMode = SDLVideo.SDL_GetCurrentDisplayMode(id);
                  if (currentMode == null) {
                     LOGGER.warn("Failed to query current video mode of monitor {}: {}", name, SDLError.SDL_GetError());
                     var18 = null;
                     break label178;
                  }

                  var7 = new Monitor(name, id, videoModes.build(), new VideoMode(currentMode), var14.x(), var14.y());
               } catch (Throwable var13) {
                  if (stack != null) {
                     try {
                        stack.close();
                     } catch (Throwable var11) {
                        var13.addSuppressed(var11);
                     }
                  }

                  throw var13;
               }

               if (stack != null) {
                  stack.close();
               }

               return var7;
            }

            if (stack != null) {
               stack.close();
            }

            return (Monitor)var18;
         }

         if (stack != null) {
            stack.close();
         }

         return (Monitor)var17;
      }

      if (stack != null) {
         stack.close();
      }

      return (Monitor)var15;
   }

   private static String queryMonitorName(final int id) {
      String monitorName = (String)Objects.requireNonNullElse(SDLVideo.SDL_GetDisplayName(id), "unknown");
      return monitorName + "[0x" + HEX_FORMAT.toHexDigits(id) + "]";
   }

   public VideoMode getPreferredVideoMode() {
      return this.videoModes.isEmpty() ? this.currentMode : (VideoMode)this.videoModes.getFirst();
   }

   public VideoMode getPreferredVideoMode(final Optional<VideoMode> expectedMode) {
      List var10001 = this.videoModes;
      Objects.requireNonNull(var10001);
      return (VideoMode)expectedMode.filter(var10001::contains).orElse(this.currentMode);
   }

   public int indexOfMode(final VideoMode expectedMode) {
      return this.videoModes.indexOf(expectedMode);
   }

   public VideoMode mode(final int mode) {
      return (VideoMode)this.videoModes.get(mode);
   }

   public int modeCount() {
      return this.videoModes.size();
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s(%s at (%d,%d))", this.name, this.currentMode, this.x, this.y);
   }
}
