package com.mojang.blaze3d.platform;

import com.mojang.logging.LogUtils;
import java.nio.ByteBuffer;
import net.minecraft.util.StringDecomposer;
import org.jspecify.annotations.Nullable;
import org.lwjgl.sdl.SDLClipboard;
import org.lwjgl.sdl.SDLError;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
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

   public void setClipboard(final String contents) {
      Boolean result = setClipboardOnStack(contents);
      if (result == null) {
         result = setClipboardInMemory(contents);
      }

      if (!result) {
         LOGGER.error("Failed to set clipboard: {}", SDLError.SDL_GetError());
      }

   }

   private static @Nullable Boolean setClipboardOnStack(final String contents) {
      MemoryStack stack = MemoryStack.stackPush();

      Boolean e;
      label43: {
         Object var4;
         try {
            label37: {
               ByteBuffer buffer;
               try {
                  buffer = stack.UTF8(contents, true);
               } catch (OutOfMemoryError var6) {
                  var4 = null;
                  break label37;
               }

               e = SDLClipboard.SDL_SetClipboardText(buffer);
               break label43;
            }
         } catch (Throwable var7) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var5) {
                  var7.addSuppressed(var5);
               }
            }

            throw var7;
         }

         if (stack != null) {
            stack.close();
         }

         return (Boolean)var4;
      }

      if (stack != null) {
         stack.close();
      }

      return e;
   }

   private static boolean setClipboardInMemory(final String contents) {
      ByteBuffer buffer = MemoryUtil.memUTF8(contents, true);

      boolean var2;
      try {
         var2 = SDLClipboard.SDL_SetClipboardText(buffer);
      } finally {
         MemoryUtil.memFree(buffer);
      }

      return var2;
   }
}
