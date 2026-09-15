package com.mojang.blaze3d.platform;

import com.mojang.logging.LogUtils;
import net.minecraft.util.StringDecomposer;
import org.lwjgl.sdl.SDLClipboard;
import org.lwjgl.sdl.SDLError;
import org.slf4j.Logger;

public class ClipboardManager {
   private static final Logger LOGGER = LogUtils.getLogger();

   public ClipboardManager() {
      super();
   }

   public String getClipboard() {
      String clipboard = SDLClipboard.SDL_GetClipboardText();
      if (clipboard == null) {
         LOGGER.error("Failed to read clipboard: {}", SDLError.SDL_GetError());
         return "";
      } else {
         return StringDecomposer.filterBrokenSurrogates(clipboard);
      }
   }

   public void setClipboard(final String clipboard) {
      if (!SDLClipboard.SDL_SetClipboardText(clipboard)) {
         LOGGER.error("Failed to set clipboard: {}", SDLError.SDL_GetError());
      }

   }
}
